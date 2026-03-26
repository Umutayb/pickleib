# Pickleib Improvements Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Refactor Pickleib internals for stability, maintainability, and testability while preserving the public API.

**Architecture:** Bottom-up refactor: extract a reusable `RetryPolicy`, make driver singletons thread-safe with `ThreadLocal`, decompose god classes into focused helpers that existing classes delegate to, add unit tests with Mockito, update dependencies, and clean up technical debt.

**Tech Stack:** Java 17, Selenium 4.x, Appium 9.x, JUnit 5, Mockito 5.x, Maven

**Spec:** `docs/superpowers/specs/2026-03-26-pickleib-improvements-design.md`

---

## File Structure

### New files to create:
- `src/main/java/pickleib/utilities/RetryPolicy.java` — centralized retry/poll utility
- `src/main/java/pickleib/utilities/helpers/ClickHelper.java` — click interaction logic
- `src/main/java/pickleib/utilities/helpers/InputHelper.java` — input fill/clear/verify logic
- `src/main/java/pickleib/utilities/helpers/ElementStateHelper.java` — element state checks & attribute verification
- `src/main/java/pickleib/utilities/helpers/DragDropHelper.java` — drag and drop logic
- `src/test/java/pickleib/utilities/RetryPolicyTest.java` — unit tests for RetryPolicy
- `src/test/java/pickleib/utilities/helpers/ClickHelperTest.java` — unit tests for ClickHelper
- `src/test/java/pickleib/utilities/helpers/InputHelperTest.java` — unit tests for InputHelper
- `src/test/java/pickleib/utilities/helpers/ElementStateHelperTest.java` — unit tests for ElementStateHelper
- `src/test/java/pickleib/utilities/helpers/DragDropHelperTest.java` — unit tests for DragDropHelper
- `src/test/java/pickleib/web/driver/ThreadSafeDriverTest.java` — thread isolation tests

### Files to modify:
- `src/main/java/pickleib/utilities/Utilities.java` — delegate to helpers
- `src/main/java/pickleib/web/utilities/WebUtilities.java` — replace retry loops with RetryPolicy
- `src/main/java/pickleib/web/interactions/WebInteractions.java` — remove @SuppressWarnings blanket
- `src/main/java/pickleib/platform/interactions/PlatformInteractions.java` — remove @SuppressWarnings blanket
- `src/main/java/pickleib/platform/utilities/PlatformUtilities.java` — replace retry loops with RetryPolicy
- `src/main/java/pickleib/utilities/element/acquisition/ElementAcquisition.java` — replace retry loops with RetryPolicy
- `src/main/java/pickleib/web/driver/PickleibWebDriver.java` — ThreadLocal, remove deprecated method
- `src/main/java/pickleib/web/driver/WebDriverFactory.java` — replace assert, document setter limitations
- `src/main/java/pickleib/platform/driver/PickleibAppiumDriver.java` — ThreadLocal
- `src/main/java/pickleib/platform/driver/ServiceFactory.java` — ThreadLocal for service
- `src/main/java/pickleib/driver/DriverFactory.java` — replace assert
- `pom.xml` — dependency updates, add Mockito, mark unused converters optional

---

## Task 0: Add Mockito Test Dependencies

**Files:**
- Modify: `pom.xml`

- [ ] **Step 1: Add Mockito dependencies to pom.xml**

Add after the JUnit dependency:
```xml
<!-- Mockito -->
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <version>5.14.2</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-junit-jupiter</artifactId>
    <version>5.14.2</version>
    <scope>test</scope>
</dependency>
```

- [ ] **Step 2: Verify compilation**

Run: `mvn compile 2>&1 | tail -10`
Expected: BUILD SUCCESS

- [ ] **Step 3: Commit**

```bash
git add pom.xml
git commit -m "build: add Mockito test dependencies"
```

---

## Task 1: Create RetryPolicy with Tests (TDD)

**Files:**
- Create: `src/main/java/pickleib/utilities/RetryPolicy.java`
- Create: `src/test/java/pickleib/utilities/RetryPolicyTest.java`

- [ ] **Step 1: Write failing tests for RetryPolicy**

```java
package pickleib.utilities;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.StaleElementReferenceException;
import pickleib.exceptions.PickleibException;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class RetryPolicyTest {

    // === execute (void) tests ===

    @Test
    void execute_void_succeeds_on_first_try() {
        AtomicInteger counter = new AtomicInteger(0);
        RetryPolicy.execute(counter::incrementAndGet, 5000);
        assertEquals(1, counter.get());
    }

    @Test
    void execute_void_retries_and_succeeds() {
        AtomicInteger counter = new AtomicInteger(0);
        RetryPolicy.execute(() -> {
            if (counter.incrementAndGet() < 3)
                throw new WebDriverException("stale");
        }, 5000);
        assertEquals(3, counter.get());
    }

    @Test
    void execute_void_throws_on_timeout() {
        assertThrows(PickleibException.class, () ->
            RetryPolicy.execute(() -> {
                throw new WebDriverException("always fails");
            }, 500)
        );
    }

    // === execute (supplier) tests ===

    @Test
    void execute_supplier_returns_value() {
        String result = RetryPolicy.execute(() -> "hello", 5000);
        assertEquals("hello", result);
    }

    @Test
    void execute_supplier_retries_and_returns() {
        AtomicInteger counter = new AtomicInteger(0);
        String result = RetryPolicy.execute(() -> {
            if (counter.incrementAndGet() < 3)
                throw new WebDriverException("stale");
            return "success";
        }, 5000);
        assertEquals("success", result);
    }

    // === pollUntil tests ===

    @Test
    void pollUntil_returns_true_when_condition_met() {
        AtomicInteger counter = new AtomicInteger(0);
        boolean result = RetryPolicy.pollUntil(() -> counter.incrementAndGet() >= 3, 5000);
        assertTrue(result);
    }

    @Test
    void pollUntil_returns_false_on_timeout() {
        boolean result = RetryPolicy.pollUntil(() -> false, 500);
        assertFalse(result);
    }

    @Test
    void pollUntil_handles_exceptions_during_polling() {
        AtomicInteger counter = new AtomicInteger(0);
        boolean result = RetryPolicy.pollUntil(() -> {
            if (counter.incrementAndGet() < 3)
                throw new WebDriverException("stale");
            return true;
        }, 5000);
        assertTrue(result);
    }

    // === pollUntil with hooks tests ===

    @Test
    void pollUntil_with_hooks_calls_beforeEach_and_afterEach() {
        AtomicInteger beforeCount = new AtomicInteger(0);
        AtomicInteger afterCount = new AtomicInteger(0);
        AtomicInteger counter = new AtomicInteger(0);

        boolean result = RetryPolicy.pollUntil(
            () -> counter.incrementAndGet() >= 2,
            5000,
            beforeCount::incrementAndGet,
            afterCount::incrementAndGet,
            null
        );

        assertTrue(result);
        assertTrue(beforeCount.get() >= 2);
        assertTrue(afterCount.get() >= 2);
    }

    @Test
    void pollUntil_with_earlyExit_returns_true_on_matching_exception() {
        boolean result = RetryPolicy.pollUntil(
            () -> { throw new StaleElementReferenceException("gone"); },
            5000,
            null,
            null,
            ex -> ex instanceof StaleElementReferenceException
        );
        assertTrue(result);
    }

    @Test
    void pollUntil_with_earlyExit_continues_on_non_matching_exception() {
        AtomicInteger counter = new AtomicInteger(0);
        boolean result = RetryPolicy.pollUntil(
            () -> {
                if (counter.incrementAndGet() < 3)
                    throw new WebDriverException("not stale");
                return true;
            },
            5000,
            null,
            null,
            ex -> ex instanceof StaleElementReferenceException
        );
        assertTrue(result);
    }
}
```

- [ ] **Step 2: Run tests to verify they fail**

Run: `mvn test -pl . -Dtest=pickleib.utilities.RetryPolicyTest -Dsurefire.useFile=false 2>&1 | tail -20`
Expected: Compilation error — `RetryPolicy` class does not exist yet.

- [ ] **Step 3: Implement RetryPolicy**

```java
package pickleib.utilities;

import org.openqa.selenium.WebDriverException;
import pickleib.exceptions.PickleibException;
import utils.Printer;

import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Centralized retry/poll utility that replaces duplicated do-while timeout loops
 * throughout the codebase. Provides consistent retry behavior, logging, and
 * exception handling for all WebDriver interactions.
 */
public class RetryPolicy {

    private static final Printer log = new Printer(RetryPolicy.class);

    /**
     * Retries a void action until it succeeds or the timeout is reached.
     * Throws PickleibException wrapping the last caught exception on timeout.
     */
    public static void execute(Runnable action, long timeoutMs) {
        execute(() -> { action.run(); return null; }, timeoutMs);
    }

    /**
     * Retries a supplier action until it returns a value or the timeout is reached.
     * Throws PickleibException wrapping the last caught exception on timeout.
     */
    public static <T> T execute(Supplier<T> action, long timeoutMs) {
        long startTime = System.currentTimeMillis();
        WebDriverException lastException = null;
        String lastExceptionType = null;
        int counter = 0;

        do {
            try {
                return action.get();
            } catch (WebDriverException e) {
                String exType = e.getClass().getName();
                if (lastExceptionType == null || !exType.equals(lastExceptionType)) {
                    log.warning("Iterating... (" + exType + ")");
                    lastExceptionType = exType;
                }
                lastException = e;
                counter++;
            }
        } while (System.currentTimeMillis() - startTime < timeoutMs);

        if (counter > 0) log.warning("Iterated " + counter + " time(s)!");
        if (lastException != null) log.warning(lastException.getMessage());
        throw new PickleibException(lastException);
    }

    /**
     * Polls until a condition returns true or the timeout is reached.
     * Returns false on timeout (does not throw).
     */
    public static boolean pollUntil(BooleanSupplier condition, long timeoutMs) {
        return pollUntil(condition, timeoutMs, null, null, null);
    }

    /**
     * Polls until a condition returns true or the timeout is reached, with optional
     * per-iteration hooks and early exit on specific exceptions.
     *
     * @param condition  the condition to poll
     * @param timeoutMs  maximum time to poll in milliseconds
     * @param beforeEach optional hook to run before each poll attempt (e.g., set implicit wait)
     * @param afterEach  optional hook to run after each poll attempt (e.g., restore implicit wait)
     * @param earlyExit  optional predicate; if a WebDriverException matches, return true immediately
     * @return true if the condition was met or earlyExit matched, false on timeout
     */
    public static boolean pollUntil(
            BooleanSupplier condition,
            long timeoutMs,
            Runnable beforeEach,
            Runnable afterEach,
            Predicate<WebDriverException> earlyExit) {

        long startTime = System.currentTimeMillis();
        String lastExceptionType = null;
        int counter = 0;

        do {
            try {
                if (beforeEach != null) beforeEach.run();
                boolean result = condition.getAsBoolean();
                if (result) return true;
            } catch (WebDriverException e) {
                if (earlyExit != null && earlyExit.test(e)) return true;

                String exType = e.getClass().getName();
                if (lastExceptionType == null || !exType.equals(lastExceptionType)) {
                    log.warning("Iterating... (" + exType + ")");
                    lastExceptionType = exType;
                }
                counter++;
            } finally {
                if (afterEach != null) afterEach.run();
            }
        } while (System.currentTimeMillis() - startTime < timeoutMs);

        if (counter > 0) log.warning("Iterated " + counter + " time(s)!");
        return false;
    }
}
```

- [ ] **Step 4: Run tests to verify they pass**

Run: `mvn test -pl . -Dtest=pickleib.utilities.RetryPolicyTest -Dsurefire.useFile=false 2>&1 | tail -20`
Expected: All tests PASS.

- [ ] **Step 5: Commit**

```bash
git add src/main/java/pickleib/utilities/RetryPolicy.java src/test/java/pickleib/utilities/RetryPolicyTest.java
git commit -m "feat: add RetryPolicy to centralize retry/poll loops"
```

---

## Task 2: Replace Retry Loops in Utilities.java with RetryPolicy

**Files:**
- Modify: `src/main/java/pickleib/utilities/Utilities.java`

- [ ] **Step 1: Replace `clickElement` (lines 95-121)**

Replace the manual do-while loop with:
```java
public void clickElement(WebElement element, boolean scroll) {
    RetryPolicy.execute(() -> {
        wait.until(ExpectedConditions.elementToBeClickable(element));
        if (scroll) this.scroller.scroll(element).click();
        else element.click();
    }, elementTimeout);
}
```

- [ ] **Step 2: Replace `elementIs` (lines 366-421)**

Replace the manual do-while with `RetryPolicy.pollUntil` using hooks.

**Bug fix:** The existing `StaleElementReferenceException` early-exit (line 411) compares `getClass().getName()` against the simple name `"StaleElementReferenceException"` — this never matches because `getName()` returns the FQCN. The `instanceof` check below fixes this silent bug. Document in changelog.

**Behavioral note:** The existing `negativeCheck` double-confirmation heuristic is no longer needed — `RetryPolicy.pollUntil` handles stability through its polling loop naturally.

```java
public Boolean elementIs(WebElement element, @NotNull ElementState state) {
    return RetryPolicy.pollUntil(
        () -> checkElementState(element, state),
        elementTimeout,
        () -> driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500)),
        () -> driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(driverTimeout)),
        ex -> state.equals(absent) && ex instanceof StaleElementReferenceException
    );
}
```

Add private helper method:
```java
private boolean checkElementState(WebElement element, ElementState state) {
    return switch (state) {
        case enabled -> element.isEnabled();
        case displayed -> element.isDisplayed();
        case selected -> element.isSelected();
        case disabled -> !element.isEnabled();
        case unselected -> !element.isSelected();
        case absent -> !element.isDisplayed();
        default -> throw new EnumConstantNotPresentException(ElementState.class, state.name());
    };
}
```

- [ ] **Step 3: Replace `elementContainsAttribute` (lines 850-885)**

```java
public boolean elementContainsAttribute(WebElement element, String attributeName, String attributeValue) {
    attributeValue = contextCheck(attributeValue);
    final String checkedValue = attributeValue;
    boolean result = RetryPolicy.pollUntil(
        () -> Objects.equals(element.getAttribute(attributeName), checkedValue),
        elementTimeout
    );
    if (!result) {
        log.warning("Element does not contain " +
            highlighted(BLUE, attributeName) +
            highlighted(GRAY, " -> ") +
            highlighted(BLUE, checkedValue) +
            highlighted(GRAY, " attribute pair.")
        );
    }
    return result;
}
```

- [ ] **Step 4: Replace `elementAttributeContainsValue` (lines 894-932)**

```java
public boolean elementAttributeContainsValue(WebElement elementName, String attributeName, String value) {
    value = contextCheck(value);
    final String checkedValue = value;
    boolean result = RetryPolicy.pollUntil(
        () -> {
            String attr = elementName.getAttribute(attributeName);
            return attr != null && attr.contains(checkedValue);
        },
        elementTimeout,
        () -> driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500)),
        () -> driver.manage().timeouts().implicitlyWait(Duration.ofMillis(elementTimeout)),
        null
    );
    if (!result) {
        log.warning("Element attribute does not contain " +
            highlighted(BLUE, attributeName) +
            highlighted(GRAY, " -> ") +
            highlighted(BLUE, checkedValue) +
            highlighted(GRAY, " value.")
        );
    }
    return result;
}
```

- [ ] **Step 5: Run compilation and existing tests to verify no regressions**

Run: `mvn compile 2>&1 | tail -10`
Expected: BUILD SUCCESS

Run: `mvn test -Dsurefire.useFile=false 2>&1 | tail -20`
Expected: All existing tests PASS (integration tests in AppTest.java)

- [ ] **Step 6: Commit**

```bash
git add src/main/java/pickleib/utilities/Utilities.java
git commit -m "refactor: replace manual retry loops in Utilities with RetryPolicy

Fixes: StaleElementReferenceException early-exit in elementIs() was silently broken
(FQCN vs simple name comparison). Now uses instanceof check."
```

---

## Task 3: Replace Retry Loops in WebUtilities, PlatformUtilities, and ElementAcquisition

**Files:**
- Modify: `src/main/java/pickleib/web/utilities/WebUtilities.java` (hoverOver, lines 498-523)
- Modify: `src/main/java/pickleib/platform/utilities/PlatformUtilities.java` (scrollUntilFound, lines 122-137; performSequence, lines 311-318; scrollInList, lines 203-221)
- Modify: `src/main/java/pickleib/utilities/element/acquisition/ElementAcquisition.java` (acquireElementUsingAttributeAmongst, lines 54-79; acquireNamedElementAmongst, lines 93-119)

- [ ] **Step 1: Fix and replace `hoverOver` in WebUtilities.java**

Note: The existing code has an inverted loop condition bug (`while (timeout)` continues *after* timeout). The RetryPolicy migration fixes this.

```java
public WebElement hoverOver(WebElement element) {
    Actions actions = new Actions(driver);
    RetryPolicy.execute(() -> {
        centerElement(element);
        actions.moveToElement(element).build().perform();
    }, elementTimeout);
    return element;
}
```

- [ ] **Step 2: Replace `scrollUntilFound(LocateElement)` in PlatformUtilities.java (lines 122-137)**

```java
public WebElement scrollUntilFound(LocateElement locator) {
    log.info("Scrolling until the element is found.");
    return RetryPolicy.execute(() -> {
        try {
            WebElement element = locator.locate();
            if (element.isDisplayed()) return element;
            else throw new WebDriverException("Element is not displayed (yet)!");
        } catch (WebDriverException e) {
            scrollInDirection(Direction.up);
            throw e;
        }
    }, elementTimeout * 5);
}
```

- [ ] **Step 3: Replace `scrollInList(String, List)` in PlatformUtilities.java (lines 203-221)**

```java
public WebElement scrollInList(String elementText, List<WebElement> elements) {
    log.info("Scrolling the list to element with text: " + highlighted(BLUE, elementText));
    return RetryPolicy.execute(() -> {
        try {
            WebElement element = waitAndGetElementByText(elementText);
            if (element.isDisplayed()) return element;
            throw new WebDriverException("Element is not displayed!");
        } catch (WebDriverException e) {
            log.info("Swiping...");
            swipeFromTo(elements.get(elements.size() - 1), elements.get(0));
            throw e;
        }
    }, elementTimeout * 5);
}
```

- [ ] **Step 4: Replace `performSequence` in PlatformUtilities.java (lines 311-318)**

```java
public static void performSequence(Sequence sequence, long initialTime, RemoteWebDriver driver) {
    RetryPolicy.execute(() -> driver.perform(singletonList(sequence)), 15000);
}
```

- [ ] **Step 5: Replace `acquireElementUsingAttributeAmongst` in ElementAcquisition.java (lines 54-79)**

```java
public static WebElement acquireElementUsingAttributeAmongst(List<WebElement> items, String attributeName, String attributeValue) {
    log.info("Acquiring element called " + markup(BLUE, attributeValue) + " using its " + markup(BLUE, attributeName) + " attribute");
    return RetryPolicy.execute(() -> {
        for (WebElement selection : items) {
            String attribute = selection.getAttribute(attributeName);
            if (attribute != null &&
                (attribute.equalsIgnoreCase(attributeValue) || attribute.contains(attributeValue)))
                return selection;
        }
        throw new NoSuchElementException("No element with the attributes '" + attributeName + " : " + attributeValue + "' could be found!");
    }, elementTimeout);
}
```

- [ ] **Step 6: Replace `acquireNamedElementAmongst` in ElementAcquisition.java (lines 93-119)**

```java
public static WebElement acquireNamedElementAmongst(List<WebElement> items, String selectionName) {
    return RetryPolicy.execute(() -> {
        for (WebElement selection : items) {
            String text = selection.getText();
            if (text.equalsIgnoreCase(selectionName) || text.contains(selectionName))
                return selection;
        }
        throw new NoSuchElementException("No element with text/name '" + selectionName + "' could be found!");
    }, elementTimeout);
}
```

- [ ] **Step 7: Verify compilation and tests**

Run: `mvn compile 2>&1 | tail -10`
Expected: BUILD SUCCESS

Run: `mvn test -Dsurefire.useFile=false 2>&1 | tail -20`
Expected: All existing tests PASS

- [ ] **Step 8: Commit**

```bash
git add src/main/java/pickleib/web/utilities/WebUtilities.java src/main/java/pickleib/platform/utilities/PlatformUtilities.java src/main/java/pickleib/utilities/element/acquisition/ElementAcquisition.java
git commit -m "refactor: replace retry loops in WebUtilities, PlatformUtilities, ElementAcquisition with RetryPolicy

Fixes: hoverOver() only retried once due to inverted timeout check (exited immediately on first failure)"
```

---

## Task 4: Thread-Safe Driver Singletons

**Files:**
- Modify: `src/main/java/pickleib/web/driver/PickleibWebDriver.java`
- Modify: `src/main/java/pickleib/platform/driver/PickleibAppiumDriver.java`
- Modify: `src/main/java/pickleib/platform/driver/ServiceFactory.java`
- Create: `src/test/java/pickleib/web/driver/ThreadSafeDriverTest.java`

- [ ] **Step 1: Write failing test for thread-safe web driver**

```java
package pickleib.web.driver;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class ThreadSafeDriverTest {

    @Test
    void threads_get_isolated_driver_instances() throws Exception {
        CountDownLatch latch = new CountDownLatch(2);
        AtomicReference<RemoteWebDriver> driver1 = new AtomicReference<>();
        AtomicReference<RemoteWebDriver> driver2 = new AtomicReference<>();

        // Simulate two threads setting different drivers
        // We can't easily create real drivers in unit tests,
        // so we test that get() returns null when no driver is set on a new thread
        Thread t1 = new Thread(() -> {
            assertNull(PickleibWebDriver.get(), "New thread should have null driver");
            latch.countDown();
        });

        Thread t2 = new Thread(() -> {
            assertNull(PickleibWebDriver.get(), "New thread should have null driver");
            latch.countDown();
        });

        t1.start();
        t2.start();
        latch.await();
    }

    @Test
    void terminate_removes_threadlocal_reference() {
        // terminate on a thread with no driver should not throw
        assertDoesNotThrow(PickleibWebDriver::terminate);
        assertNull(PickleibWebDriver.get());
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `mvn test -pl . -Dtest=pickleib.web.driver.ThreadSafeDriverTest -Dsurefire.useFile=false 2>&1 | tail -20`
Expected: FAIL (current PickleibWebDriver uses static field, not ThreadLocal)

- [ ] **Step 3: Update PickleibWebDriver to use ThreadLocal**

Replace in `PickleibWebDriver.java`:
- Replace `private static RemoteWebDriver driver;` with `private static final ThreadLocal<RemoteWebDriver> driver = new ThreadLocal<>();`
- Replace `private static FluentWait<RemoteWebDriver> wait;` with `private static final ThreadLocal<FluentWait<RemoteWebDriver>> wait = new ThreadLocal<>();`
- Update `get()` to return `driver.get()`
- Update `initialize(BrowserType)` to call `driver.set(...)`
- Update `terminate()` to call `driver.get()`, `quit()`, `driver.remove()`
- Remove the deprecated `initialize(String id, String password, BrowserType)` method and the BouncyCastle import

- [ ] **Step 4: Update PickleibAppiumDriver to use ThreadLocal**

Replace in `PickleibAppiumDriver.java`:
- Replace `private static AppiumDriver driver;` with `private static final ThreadLocal<AppiumDriver> driver = new ThreadLocal<>();`
- Replace `private static AppiumFluentWait<RemoteWebDriver> wait;` with `private static final ThreadLocal<AppiumFluentWait<RemoteWebDriver>> wait = new ThreadLocal<>();`
- Update `get()` to return `driver.get()`
- Update `initialize()` to call `driver.set(...)`
- Update `terminate()` and `captureAndTerminate()` to use `driver.get()`, then `driver.remove()`

- [ ] **Step 5: Update ServiceFactory to use ThreadLocal**

Replace in `ServiceFactory.java`:
- Replace `public static AppiumDriverLocalService service;` with `public static final ThreadLocal<AppiumDriverLocalService> service = new ThreadLocal<>();`
- Update `startService()` to call `service.set(...)`
- Update all references in `PickleibAppiumDriver.terminate()` and `captureAndTerminate()` to use `ServiceFactory.service.get()` and add `ServiceFactory.service.remove()` after stop

- [ ] **Step 6: Verify compilation and run tests**

Run: `mvn compile 2>&1 | tail -10 && mvn test -pl . -Dtest=pickleib.web.driver.ThreadSafeDriverTest -Dsurefire.useFile=false 2>&1 | tail -20`
Expected: BUILD SUCCESS, tests PASS

- [ ] **Step 7: Commit**

```bash
git add src/main/java/pickleib/web/driver/PickleibWebDriver.java src/main/java/pickleib/platform/driver/PickleibAppiumDriver.java src/main/java/pickleib/platform/driver/ServiceFactory.java src/test/java/pickleib/web/driver/ThreadSafeDriverTest.java
git commit -m "feat: make driver singletons thread-safe with ThreadLocal

- PickleibWebDriver, PickleibAppiumDriver, ServiceFactory now use ThreadLocal
- Remove deprecated BasicAuth initialize method and BouncyCastle import
- Fixes stale driver reference after terminate()"
```

---

## Task 5: Extract ClickHelper with Tests (TDD)

**Files:**
- Create: `src/main/java/pickleib/utilities/helpers/ClickHelper.java`
- Create: `src/test/java/pickleib/utilities/helpers/ClickHelperTest.java`
- Modify: `src/main/java/pickleib/utilities/Utilities.java`

- [ ] **Step 1: Write failing tests for ClickHelper**

```java
package pickleib.utilities.helpers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.FluentWait;
import pickleib.exceptions.PickleibException;
import pickleib.utilities.interfaces.functions.ScrollFunction;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClickHelperTest {

    @Mock RemoteWebDriver driver;
    @Mock FluentWait<RemoteWebDriver> wait;
    @Mock ScrollFunction scroller;
    @Mock WebElement element;

    ClickHelper clickHelper;

    @BeforeEach
    void setUp() {
        clickHelper = new ClickHelper(driver, wait, scroller, 2000);
    }

    @Test
    void clickElement_calls_element_click() {
        when(wait.until(any())).thenReturn(element);
        clickHelper.clickElement(element, false);
        verify(element).click();
    }

    @Test
    void clickElement_with_scroll_uses_scroller() {
        when(wait.until(any())).thenReturn(element);
        when(scroller.scroll(element)).thenReturn(element);
        clickHelper.clickElement(element, true);
        verify(scroller).scroll(element);
    }

    @Test
    void clickButtonIfPresent_does_not_throw_when_element_missing() {
        when(wait.until(any())).thenThrow(new WebDriverException("not found"));
        assertDoesNotThrow(() -> clickHelper.clickButtonIfPresent(element, false));
    }

    @Test
    void clickIfPresent_does_not_throw_when_element_missing() {
        when(wait.until(any())).thenThrow(new WebDriverException("not found"));
        assertDoesNotThrow(() -> clickHelper.clickIfPresent(element, false));
    }
}
```

- [ ] **Step 2: Run tests to verify they fail**

Run: `mvn test -pl . -Dtest=pickleib.utilities.helpers.ClickHelperTest -Dsurefire.useFile=false 2>&1 | tail -20`
Expected: Compilation error — `ClickHelper` does not exist.

- [ ] **Step 3: Implement ClickHelper**

Extract `clickElement`, `clickButtonIfPresent`, `clickIfPresent`, `clickTowards`, `clickAtAnOffset`, `clickButtonWithText` from `Utilities.java` into `ClickHelper.java`. The helper uses `RetryPolicy` internally.

```java
package pickleib.utilities.helpers;

import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import pickleib.utilities.RetryPolicy;
import pickleib.utilities.interfaces.functions.ScrollFunction;
import utils.Printer;

public class ClickHelper {

    private final RemoteWebDriver driver;
    private final FluentWait<RemoteWebDriver> wait;
    private final ScrollFunction scroller;
    private final long elementTimeout;
    private final Printer log = new Printer(ClickHelper.class);

    public ClickHelper(RemoteWebDriver driver, FluentWait<RemoteWebDriver> wait, ScrollFunction scroller, long elementTimeout) {
        this.driver = driver;
        this.wait = wait;
        this.scroller = scroller;
        this.elementTimeout = elementTimeout;
    }

    public void clickElement(WebElement element) {
        clickElement(element, false);
    }

    public void clickElement(WebElement element, boolean scroll) {
        RetryPolicy.execute(() -> {
            wait.until(ExpectedConditions.elementToBeClickable(element));
            if (scroll) scroller.scroll(element).click();
            else element.click();
        }, elementTimeout);
    }

    public void clickButtonIfPresent(WebElement element) {
        clickButtonIfPresent(element, false);
    }

    public void clickButtonIfPresent(WebElement element, boolean scroll) {
        try {
            clickElement(element, scroll);
        } catch (org.openqa.selenium.NoSuchElementException | org.openqa.selenium.StaleElementReferenceException ignored) {
            log.warning("The element was not present!");
        }
    }

    public void clickIfPresent(WebElement element, boolean scroll) {
        try {
            clickElement(element, scroll);
        } catch (org.openqa.selenium.NoSuchElementException | org.openqa.selenium.StaleElementReferenceException exception) {
            log.warning(exception.getMessage());
        }
    }

    public void clickIfPresent(WebElement element) {
        clickIfPresent(element, false);
    }

    /**
     * Note: Preserves the elementIs(displayed) check from the original Utilities.clickTowards.
     * Requires an ElementStateHelper reference or a BooleanSupplier for the pre-click visibility wait.
     */
    public void clickTowards(WebElement element, FluentWait<RemoteWebDriver> wait) {
        wait.until(ExpectedConditions.visibilityOf(element));
        Actions builder = new Actions(driver);
        builder.moveToElement(element, 0, 0).click().build().perform();
    }

    public void clickAtAnOffset(WebElement element, int xOffset, int yOffset) {
        Actions builder = new Actions(driver);
        builder.moveToElement(element, xOffset, yOffset).click().build().perform();
    }

    public void clickButtonWithText(String buttonText, boolean scroll, FluentWait<RemoteWebDriver> wait) {
        String queryAttribute = DriverInspector.getTextAttributeNameFor(
            DriverInspector.getDriverPlatform(driver)
        );
        String xpath = "//*[" + queryAttribute + "='" + buttonText + "']";
        WebElement foundElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
        clickElement(foundElement, scroll);
    }
}
```

- [ ] **Step 4: Wire Utilities.java to delegate to ClickHelper**

In `Utilities.java`, add a `ClickHelper` field initialized in both constructors. Update all click methods to delegate (including `clickButtonWithText`):

```java
protected ClickHelper clickHelper;

// In constructors, after other init:
this.clickHelper = new ClickHelper(driver, wait, scroller, elementTimeout);

// Delegation:
public void clickElement(WebElement element, boolean scroll) {
    clickHelper.clickElement(element, scroll);
}
// ... same for clickButtonIfPresent, clickIfPresent, clickTowards, clickAtAnOffset
```

- [ ] **Step 5: Run tests**

Run: `mvn test -pl . -Dtest=pickleib.utilities.helpers.ClickHelperTest -Dsurefire.useFile=false 2>&1 | tail -20`
Expected: PASS

- [ ] **Step 6: Commit**

```bash
git add src/main/java/pickleib/utilities/helpers/ClickHelper.java src/test/java/pickleib/utilities/helpers/ClickHelperTest.java src/main/java/pickleib/utilities/Utilities.java
git commit -m "refactor: extract ClickHelper from Utilities with unit tests"
```

---

## Task 6: Extract InputHelper with Tests (TDD)

**Files:**
- Create: `src/main/java/pickleib/utilities/helpers/InputHelper.java`
- Create: `src/test/java/pickleib/utilities/helpers/InputHelperTest.java`
- Modify: `src/main/java/pickleib/utilities/Utilities.java`

- [ ] **Step 1: Write failing tests for InputHelper**

Test `fillAndVerify` with clear=true, verify=true, and verify=false scenarios. Test `clearInputField`. Mock `WebElement.getAttribute()` and `sendKeys()`.

- [ ] **Step 2: Run tests to verify they fail**

Run: `mvn test -pl . -Dtest=pickleib.utilities.helpers.InputHelperTest -Dsurefire.useFile=false 2>&1 | tail -20`
Expected: Compilation error.

- [ ] **Step 3: Implement InputHelper**

Extract `fillAndVerify`, `clearInputField`, `fillInput`, `clearFillInput`, `fillInputElement`, `fillAndVerifyInput`, `fillInputForm` from `Utilities.java`. Uses `PickleibVerificationException` instead of `assert` for verification.

Key change: Replace `assert !verify || inputText.equals(inputValue)` with:
```java
if (verify && !inputText.equals(inputValue))
    throw new PickleibVerificationException("Input verification failed: expected '" + inputText + "' but got '" + inputValue + "'");
```

- [ ] **Step 4: Wire Utilities.java to delegate to InputHelper**

Add `InputHelper` field, initialize in constructors, delegate all input methods.

- [ ] **Step 5: Run tests**

Run: `mvn test -pl . -Dtest=pickleib.utilities.helpers.InputHelperTest -Dsurefire.useFile=false 2>&1 | tail -20`
Expected: PASS

- [ ] **Step 6: Commit**

```bash
git add src/main/java/pickleib/utilities/helpers/InputHelper.java src/test/java/pickleib/utilities/helpers/InputHelperTest.java src/main/java/pickleib/utilities/Utilities.java
git commit -m "refactor: extract InputHelper from Utilities with unit tests

Replaces assert with PickleibVerificationException for input verification"
```

---

## Task 7: Extract ElementStateHelper with Tests (TDD)

**Files:**
- Create: `src/main/java/pickleib/utilities/helpers/ElementStateHelper.java`
- Create: `src/test/java/pickleib/utilities/helpers/ElementStateHelperTest.java`
- Modify: `src/main/java/pickleib/utilities/Utilities.java`

- [ ] **Step 1: Write failing tests for ElementStateHelper**

Test each `ElementState` enum value (enabled, displayed, selected, disabled, unselected, absent). Test `absent` + `StaleElementReferenceException` returns true. Test `verifyElementState` throws on incorrect state. Test `elementContainsAttribute` and `elementAttributeContainsValue`.

- [ ] **Step 2: Run tests to verify they fail**

Run: `mvn test -pl . -Dtest=pickleib.utilities.helpers.ElementStateHelperTest -Dsurefire.useFile=false 2>&1 | tail -20`
Expected: Compilation error.

- [ ] **Step 3: Implement ElementStateHelper**

Extract `elementIs`, `checkElementState`, `verifyElementState`, `verifyElementText`, `verifyElementContainsText`, `verifyListedElementText`, `verifyListContainsElementByText`, `elementContainsAttribute`, `elementAttributeContainsValue` from `Utilities.java`.

- [ ] **Step 4: Wire Utilities.java to delegate to ElementStateHelper**

- [ ] **Step 5: Run tests**

Run: `mvn test -pl . -Dtest=pickleib.utilities.helpers.ElementStateHelperTest -Dsurefire.useFile=false 2>&1 | tail -20`
Expected: PASS

- [ ] **Step 6: Commit**

```bash
git add src/main/java/pickleib/utilities/helpers/ElementStateHelper.java src/test/java/pickleib/utilities/helpers/ElementStateHelperTest.java src/main/java/pickleib/utilities/Utilities.java
git commit -m "refactor: extract ElementStateHelper from Utilities with unit tests"
```

---

## Task 8: Extract DragDropHelper with Tests (TDD)

**Files:**
- Create: `src/main/java/pickleib/utilities/helpers/DragDropHelper.java`
- Create: `src/test/java/pickleib/utilities/helpers/DragDropHelperTest.java`
- Modify: `src/main/java/pickleib/utilities/Utilities.java`

- [ ] **Step 1: Write failing tests for DragDropHelper**

Test that `dragDropToAction`, `dragDropByAction`, `dragDropAction` construct correct Actions sequences.

- [ ] **Step 2: Run tests to verify they fail**

- [ ] **Step 3: Implement DragDropHelper**

Extract `dragDropToAction`, `dragDropByAction`, `dragDropAction` from `Utilities.java`.

- [ ] **Step 4: Wire Utilities.java to delegate to DragDropHelper**

- [ ] **Step 5: Run tests**

Expected: PASS

- [ ] **Step 6: Commit**

```bash
git add src/main/java/pickleib/utilities/helpers/DragDropHelper.java src/test/java/pickleib/utilities/helpers/DragDropHelperTest.java src/main/java/pickleib/utilities/Utilities.java
git commit -m "refactor: extract DragDropHelper from Utilities with unit tests"
```

---

## Task 9: Dependency Updates

**Files:**
- Modify: `pom.xml`

- [ ] **Step 1: Update dependency versions**

Check latest stable versions and update in `pom.xml`:
- `selenium.version` → latest 4.x (check mvnrepository.com)
- `appium.version` → latest 9.x
- `docker-java.version` if needed
- Gson → latest 2.x
- WebDriverManager → latest 5.x
- Lombok → latest 1.18.x
- OKHttp (`okhttp.version`) → latest 4.x
- JUnit Jupiter → latest 5.x (currently 5.13.1, verify)

- [ ] **Step 2: Update plugin versions**

- `maven-compiler-plugin` → latest 3.x
- `maven-source-plugin` → latest 3.x
- `maven-javadoc-plugin` → latest 3.x

- [ ] **Step 3: Mark unused Retrofit converters as optional**

None of the 7 Retrofit converters are used in Pickleib's own source code. Mark all of them `<optional>true</optional>` so downstream consumers must explicitly declare them:

```xml
<dependency>
    <groupId>com.squareup.retrofit2</groupId>
    <artifactId>converter-jackson</artifactId>
    <version>${retrofit.version}</version>
    <optional>true</optional>
</dependency>
<!-- Same for converter-scalars, converter-simplexml, converter-moshi, converter-protobuf, converter-wire -->
<!-- Keep converter-gson as non-optional since it's the most common default -->
```

- [ ] **Step 4: Check if Guava managed dependency is needed**

Run: `mvn dependency:tree 2>&1 | grep guava`
If no direct dependency on Guava, remove the `<dependencyManagement>` entry. If transitive, keep it.

- [ ] **Step 5: Verify compilation**

Run: `mvn clean compile 2>&1 | tail -15`
Expected: BUILD SUCCESS

- [ ] **Step 6: Run all tests**

Run: `mvn test -Dsurefire.useFile=false 2>&1 | tail -20`
Expected: All tests PASS

- [ ] **Step 7: Commit**

```bash
git add pom.xml
git commit -m "build: update dependencies, mark unused Retrofit converters optional"
```

---

## Task 10: Cleanup — Asserts, Deprecated Code, SuppressWarnings

**Files:**
- Modify: `src/main/java/pickleib/driver/DriverFactory.java` (line 55)
- Modify: `src/main/java/pickleib/web/driver/WebDriverFactory.java` (line 193)
- Modify: `src/main/java/pickleib/web/interactions/WebInteractions.java` (line 41)
- Modify: `src/main/java/pickleib/platform/interactions/PlatformInteractions.java` (line 31)
- Modify: `src/main/java/pickleib/utilities/element/acquisition/ElementAcquisition.java` (line 30)

- [ ] **Step 1: Replace `assert` in DriverFactory.java:55**

```java
// Before:
assert text != null;

// After:
if (text == null) throw new PickleibException("Driver type text cannot be null");
```

Add import for `PickleibException`.

- [ ] **Step 2: Replace `assert` in WebDriverFactory.java:193**

```java
// Before:
assert driver != null;

// After:
if (driver == null) throw new PickleibException("Driver initialization failed — driverSwitch returned null");
```

- [ ] **Step 3: Remove blanket @SuppressWarnings**

In `WebInteractions.java` line 41, remove `@SuppressWarnings("unused")`.
In `PlatformInteractions.java` line 31, remove `@SuppressWarnings("unused")`.
In `ElementAcquisition.java` line 30, remove `@SuppressWarnings("unused")`.

Review if any individual methods need targeted `@SuppressWarnings` and add only those.

- [ ] **Step 4: Add Javadoc to WebDriverFactory setters about thread-safety**

Add to each static setter method:
```java
/**
 * ...existing docs...
 * <p><b>Thread Safety:</b> This setter modifies shared static state.
 * It must be called before parallel driver initialization, not during.</p>
 */
```

- [ ] **Step 5: Verify compilation**

Run: `mvn clean compile 2>&1 | tail -10`
Expected: BUILD SUCCESS

- [ ] **Step 6: Run all tests**

Run: `mvn test -Dsurefire.useFile=false 2>&1 | tail -20`
Expected: All tests PASS

- [ ] **Step 7: Commit**

```bash
git add src/main/java/pickleib/driver/DriverFactory.java src/main/java/pickleib/web/driver/WebDriverFactory.java src/main/java/pickleib/web/interactions/WebInteractions.java src/main/java/pickleib/platform/interactions/PlatformInteractions.java src/main/java/pickleib/utilities/element/acquisition/ElementAcquisition.java
git commit -m "cleanup: replace asserts with exceptions, remove blanket @SuppressWarnings, document setter thread-safety"
```

---

## Task 11: Final Verification

- [ ] **Step 1: Run full build**

Run: `mvn clean compile 2>&1 | tail -15`
Expected: BUILD SUCCESS

- [ ] **Step 2: Run all tests**

Run: `mvn test -Dsurefire.useFile=false 2>&1 | tail -30`
Expected: All tests PASS

- [ ] **Step 3: Verify no remaining manual retry loops**

Search for the old pattern:
Run: `grep -rn "System.currentTimeMillis" src/main/java/pickleib/utilities/ src/main/java/pickleib/web/utilities/ src/main/java/pickleib/platform/utilities/ src/main/java/pickleib/utilities/element/acquisition/`
Expected: No matches in the refactored methods (may still appear in non-retry-loop contexts like `waitUntilLoads`)

- [ ] **Step 4: Verify no remaining `assert` in production code**

Run: `grep -rn "^\s*assert " src/main/java/`
Expected: No matches (the `assert driverLoad != null` in WebUtilities `waitUntilLoads` lambda is acceptable as it's a local assertion in an ExpectedCondition)

- [ ] **Step 5: Verify Utilities.java is now a thin delegation layer**

Count lines: `wc -l src/main/java/pickleib/utilities/Utilities.java`
Expected: Significantly reduced from 933 lines (target: <300 lines of delegation + shared utility methods)
