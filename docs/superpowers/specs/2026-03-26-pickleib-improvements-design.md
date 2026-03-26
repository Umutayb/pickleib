# Pickleib Improvements Design Spec

**Date:** 2026-03-26
**Version:** 2.0.9 (current) — changes are internal, public API preserved
**Approach:** Bottom-Up Refactor

---

## Context

Pickleib is a polymorphic test automation library wrapping Selenium/Appium. It supports Web, Mobile, and Desktop testing with a dual Page Object Model (Java classes + JSON repository). The codebase has grown to ~3,700 lines of main source across deeply inherited utility classes with significant code duplication, no unit tests, thread-unsafe singletons, and outdated dependencies.

This spec covers a concentrated improvement effort targeting stability, maintainability, developer experience, and publish quality — without breaking the public API.

## Constraints

- **Public API must remain backward-compatible.** Consumers using `PickleibSteps`, `PickleibWebDriver`, `PickleibAppiumDriver`, `PickleibPageObject`, and the JSON repository format should not need to change their code.
- **Internals are fair game.** Class decomposition, new internal classes, dependency changes are all acceptable.
- **ContextStore returns defaults without initialization** — no need to change eager static field initialization in `WebDriverFactory`.

---

## 1. RetryPolicy — Centralized Retry/Poll Utility

### Problem

The same do-while retry loop is copy-pasted ~10 times across `Utilities.java`, `WebUtilities.java`, and `ElementAcquisition.java`. Each manually tracks `initialTime`, `counter`, `caughtException`, and logs "Iterating..." messages. The codebase has TODOs acknowledging this (Utilities.java lines 365, 903).

Locations of duplicated pattern:
- `Utilities.java`: `clickElement` (95-121), `elementIs` (366-421), `elementContainsAttribute` (850-885), `elementAttributeContainsValue` (894-932)
- `WebUtilities.java`: `hoverOver` (498-523) — **Note:** this method has an inverted loop condition bug where it continues *after* timeout instead of stopping. The RetryPolicy migration fixes this.
- `ElementAcquisition.java`: `acquireElementUsingAttributeAmongst` (61-77), `acquireNamedElementAmongst` (98-117)
- `PlatformUtilities.java`: `scrollUntilFound` (122-137)

### Solution

New class: `pickleib.utilities.RetryPolicy`

```java
public class RetryPolicy {
    // Retry an action that returns a value — throws on timeout
    public static <T> T execute(Supplier<T> action, long timeoutMs, Printer log);

    // Retry a void action — throws on timeout
    public static void execute(Runnable action, long timeoutMs, Printer log);

    // Poll until a condition is true — returns false on timeout (no throw)
    public static boolean pollUntil(BooleanSupplier condition, long timeoutMs, Printer log);

    // Poll with per-iteration setup/teardown hooks and special exit conditions
    // Needed for elementIs() which toggles implicitlyWait on each iteration
    // and has special logic (absent + StaleElementRef = true)
    public static boolean pollUntil(
        BooleanSupplier condition,
        long timeoutMs,
        Printer log,
        Runnable beforeEach,        // e.g., set implicitlyWait to 500ms
        Runnable afterEach,         // e.g., restore implicitlyWait to driverTimeout
        Predicate<WebDriverException> earlyExit  // e.g., absent + StaleElement → return true
    );
}
```

All retry logging moves into `RetryPolicy` for consistency. Each call site reduces to 1-3 lines.

The four-arg `pollUntil` handles the `elementIs()` case specifically:
- `beforeEach`/`afterEach` replace the manual `implicitlyWait` toggling (currently in a try/finally per iteration)
- `earlyExit` predicate handles the special case where `absent` + `StaleElementReferenceException` should return `true`
- Returns `false` on timeout instead of throwing, matching current `elementIs()` behavior

### Example transformation

Before (25 lines):
```java
public void clickElement(WebElement element, boolean scroll) {
    WebDriverException caughtException = null;
    int counter = 0;
    long initialTime = System.currentTimeMillis();
    do {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(element));
            if (scroll) this.scroller.scroll(element).click();
            else element.click();
            return;
        } catch (WebDriverException webDriverException) {
            // ... logging, counter tracking ...
            counter++;
        }
    } while (System.currentTimeMillis() - initialTime < elementTimeout);
    // ... final logging, throw ...
}
```

After (5 lines):
```java
public void clickElement(WebElement element, boolean scroll) {
    RetryPolicy.execute(() -> {
        wait.until(ExpectedConditions.elementToBeClickable(element));
        if (scroll) scroller.scroll(element).click();
        else element.click();
    }, elementTimeout, log);
}
```

---

## 2. Thread-Safe Driver Singletons

### Problem

`PickleibWebDriver` and `PickleibAppiumDriver` use plain `static` fields for the driver instance. No synchronization exists. Parallel test execution causes threads to overwrite each other's driver.

### Solution

Replace `static RemoteWebDriver driver` with `ThreadLocal<RemoteWebDriver>`:

```java
public class PickleibWebDriver {
    private static final ThreadLocal<RemoteWebDriver> driver = new ThreadLocal<>();

    public static RemoteWebDriver get() {
        return driver.get();
    }

    public static void initialize(WebDriverFactory.BrowserType browserType) {
        driver.set(WebDriverFactory.getDriver(browserType));
    }

    public static void terminate() {
        RemoteWebDriver current = driver.get();
        if (current != null) {
            current.quit();
            driver.remove(); // prevent memory leaks
        }
    }
}
```

Same pattern for `PickleibAppiumDriver`. Public API (`get()`, `initialize()`, `terminate()`) stays identical.

### WebDriverFactory static setters

`WebDriverFactory` has ~20 mutable static fields with public setters (e.g., `setHeadless()`, `setFrameWidth()`). If two threads call setters before `getDriver()`, they interfere. This is an accepted limitation: the setters are for pre-execution configuration, not per-thread overrides. Document this in Javadoc: "Configuration setters must be called before parallel driver initialization."

### ServiceFactory (Appium)

`PickleibAppiumDriver` manages `ServiceFactory.service` which is also a static singleton. `ServiceFactory` should also use `ThreadLocal` for its service instance, since each thread may need its own Appium service on a different port. The `captureAndTerminate()` method's screenshot logic should operate on the thread-local driver.

### Existing bug fix

The current `terminate()` calls `driver.quit()` but never nulls the field — a subsequent `get()` returns a dead driver. The `ThreadLocal` migration fixes this via `driver.remove()`.

---

## 3. God Class Decomposition

### Problem

The inheritance chain `Utilities (933 lines) -> WebUtilities (720) -> WebInteractions (1247)` totals ~2,900 lines with mixed responsibilities.

### Solution

Extract focused helper classes. Existing classes compose and delegate to them, preserving the public API.

| Helper Class | Responsibility | Key methods |
|-------------|---------------|-------------|
| `ClickHelper` | All click variants | `clickElement`, `clickButtonIfPresent`, `clickIfPresent`, `clickTowards`, `clickAtAnOffset`, `clickButtonWithText` |
| `InputHelper` | Fill, clear, verify inputs | `fillInput`, `fillAndVerify`, `clearFillInput`, `clearInputField`, `fillInputForm`, `fillInputElement` overloads |
| `ElementStateHelper` | State checks & attribute verification | `elementIs`, `verifyElementState`, `verifyElementText`, `verifyElementContainsText`, `elementContainsAttribute`, `elementAttributeContainsValue` |
| `DragDropHelper` | Drag and drop actions | `dragDropToAction`, `dragDropByAction`, `dragDropAction` |

Each helper takes `RemoteWebDriver`, `FluentWait`, `ScrollFunction`, timeout config, and `Printer` via constructor.

Package: `pickleib.utilities.helpers`

### Helper and RetryPolicy interaction

Helpers own the retry logic internally using `RetryPolicy`. `Utilities` delegates to helpers without wrapping in additional retry. For example, `ClickHelper.clickElement()` calls `RetryPolicy.execute()` directly.

### Shared utility methods

- `contextCheck()` (currently inherited from `StringUtilities`) is a static method — helpers call it directly, no ownership change needed.
- `getInputContentAttributeNameFor()` and `getElementDriverPlatform()` live in `DriverInspector` (already a static utility) — helpers call them directly.
- The `implicitlyWait` toggling in `elementIs()` is handled by `RetryPolicy.pollUntil()`'s `beforeEach`/`afterEach` hooks. `ElementStateHelper` passes these hooks when constructing the poll call.

### Delegation pattern

```java
public abstract class Utilities {
    protected final ClickHelper clicks;
    protected final InputHelper inputs;
    protected final ElementStateHelper states;
    protected final DragDropHelper dragDrop;

    public void clickElement(WebElement element, boolean scroll) {
        clicks.clickElement(element, scroll);
    }
    // ... all other methods delegate similarly
}
```

---

## 4. Unit Tests

### Problem

Zero unit tests exist. Only integration tests in `AppTest.java` (539 lines). Core logic in Utilities, ElementAcquisition, and WebDriverFactory is untested.

### Solution

Add Mockito as a test dependency. Target the extracted helpers and RetryPolicy first.

| Test Class | What it covers | Approach |
|-----------|---------------|----------|
| `RetryPolicyTest` | Timeout, retry count, exception propagation, success on Nth try | Pure unit tests |
| `ClickHelperTest` | Delegates to click, StaleElementRef handling, scroll-before-click | Mock WebElement, FluentWait |
| `InputHelperTest` | Clear-then-fill, verify after fill, context substitution | Mock WebElement |
| `ElementStateHelperTest` | Each ElementState enum, negative checks, absent+stale=true | Mock WebElement |
| `ThreadSafeDriverTest` | Thread isolation, terminate cleanup | Spawn threads, assert isolation |
| `WebDriverFactoryTest` | Correct options per browser, headless, mobile emulation | Verify options objects |

### Test structure

New unit tests follow standard Maven conventions and mirror the main source package structure:
- `src/test/java/pickleib/utilities/RetryPolicyTest.java`
- `src/test/java/pickleib/utilities/helpers/ClickHelperTest.java`
- `src/test/java/pickleib/utilities/helpers/InputHelperTest.java`
- etc.

Existing `AppTest.java` integration tests remain untouched.

### New test dependency

```xml
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <version>5.x</version>
    <scope>test</scope>
</dependency>
```

---

## 5. Dependency Updates

### Dependency upgrades

| Dependency | Current | Target |
|-----------|---------|--------|
| Selenium | 4.19.0 | Latest 4.x |
| Appium | 9.2.2 | Latest 9.x |
| WebDriverManager | 5.5.3 | Latest 5.x |
| Gson | 2.10.1 | Latest 2.x |
| Lombok | 1.18.26 | Latest 1.18.x |
| OKHttp | 4.10.0 | Latest 4.x |

### Plugin upgrades

| Plugin | Current | Target |
|--------|---------|--------|
| maven-compiler | 3.8.0 | Latest 3.x |
| maven-source | 2.2.1 | Latest 3.x |
| maven-javadoc | 2.9.1 | Latest 3.x |

### Retrofit converter cleanup

Audit which of the 7 included converters (gson, jackson, scalars, simplexml, moshi, protobuf, wire) are actually used in Pickleib's own source code. Since Pickleib is a published library, downstream consumers may depend on these transitively. For unused converters: mark them `<optional>true</optional>` rather than removing outright, so existing consumers aren't broken but new consumers don't pull them in by default.

### Guava managed dependency

Verify if the managed Guava 31.0.1-jre declaration is needed by transitive dependencies. Remove if not.

---

## 6. Cleanup

### Replace `assert` with proper exceptions

- `Utilities.java:343`: `assert !verify || inputText.equals(inputValue)` — replace with `PickleibVerificationException`
- `WebDriverFactory.java:193`: `assert driver != null` — replace with `PickleibException`
- `DriverFactory.java:55`: `assert text != null` — replace with `PickleibException`

### Remove deprecated Basic Auth method

`PickleibWebDriver.java:88-96` — deprecated since 1.5.6, only reason BouncyCastle is imported. Remove the method and the import.

### Bug fix: `hoverOver` inverted timeout

`WebUtilities.java` `hoverOver` (498-523) has a loop condition `while (timeout)` where `timeout = System.currentTimeMillis() - initialTime > elementTimeout` — this continues *after* timeout instead of stopping. Fixed by migrating to `RetryPolicy`. Document in changelog as a bug fix.

### Bug fix: `clickButtonIfPresent` overly broad catch

`Utilities.java:163-168` catches all `WebDriverException` subtypes (including session-not-created, unexpected alert) and treats them as "not present." After refactoring, narrow the catch to `NoSuchElementException` and `StaleElementReferenceException`.

### Consistent exception context

After RetryPolicy extraction, review remaining catch blocks. Ensure none silently swallow exceptions (e.g., `catch (WebDriverException ignored)` at Utilities.java:166 should at minimum log).

### `@SuppressWarnings` cleanup

Remove blanket `@SuppressWarnings({"unused", "UnusedReturnValue"})` at class level on `Utilities` and `WebDriverFactory`. Apply targeted suppressions only where genuinely needed post-refactor.

---

## Implementation Order

1. **RetryPolicy** — extract and replace all duplicated loops
2. **ThreadLocal drivers** — fix PickleibWebDriver and PickleibAppiumDriver
3. **Helper extraction** — ClickHelper, InputHelper, ElementStateHelper, DragDropHelper
4. **Unit tests** — test each helper, RetryPolicy, driver isolation
5. **Dependency updates** — upgrade versions, trim unused converters, add Mockito
6. **Cleanup** — asserts, deprecated code, exception context, suppress-warnings

Each step is independently verifiable before proceeding to the next.

## Acceptance Criteria

1. **RetryPolicy** — All ~10 manual retry loops replaced. Zero do-while timeout loops remain in Utilities/WebUtilities/ElementAcquisition/PlatformUtilities. `RetryPolicyTest` passes.
2. **ThreadLocal drivers** — `PickleibWebDriver`, `PickleibAppiumDriver`, and `ServiceFactory` use `ThreadLocal`. `ThreadSafeDriverTest` demonstrates thread isolation.
3. **Helper extraction** — `Utilities.java` shrinks to delegation-only methods. Each helper is <200 lines. No logic remains in the delegation layer.
4. **Unit tests** — All helper classes and `RetryPolicy` have test coverage. All tests pass with `mvn test`.
5. **Dependency updates** — All dependencies updated to latest compatible versions. `mvn compile` succeeds. Unused Retrofit converters marked `<optional>`.
6. **Cleanup** — Zero `assert` statements in production code. Deprecated Basic Auth method removed. No blanket `@SuppressWarnings` at class level.
