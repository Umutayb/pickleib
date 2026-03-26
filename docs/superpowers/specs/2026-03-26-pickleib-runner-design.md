# PickleibRunner Design Spec — Sub-project 1: Core Runner + Annotation Framework

**Date:** 2026-03-26
**Scope:** Core annotation framework, runtime classpath scanning, `@PageObject` auto-discovery, `@ContextValue` injection, built-in generic Cucumber steps
**Approach:** Runtime classpath scanning + JUnit 5 Extension (no compile-time annotation processing)

---

## Context

Pickleib currently requires significant boilerplate from consumers:
- A manual `ObjectRepository` class listing every page object as a field
- Page objects must extend `PickleibPageObject` or `PickleibScreenObject`
- A ~994-line `CommonSteps` class that delegates nearly every method to Pickleib's framework
- Step classes must extend `PickleibSteps` to get access to the element repository and interactions

This spec introduces an annotation-driven runner that eliminates this boilerplate while preserving backward compatibility with the existing patterns.

## Constraints

- **Backward compatible** — existing `PickleibSteps`, `ObjectRepository`, and `PickleibPageObject` patterns must continue to work
- **Purely additive** — no changes to existing classes needed
- **Works with Cucumber** — built-in steps must integrate with Cucumber's glue mechanism
- **Works with JUnit 5** — extension model, not JUnit 4 runner

---

## 1. Annotations

**Package:** `pickleib.annotations`

### `@Pickleib`

Target: Class (test runner or test class)
Entry point annotation that triggers the runner.

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Pickleib {
    /** Packages to scan for @PageObject and @ScreenObject classes.
     *  Default: root package of the annotated class. */
    String[] scanPackages() default {};

    /** Whether to register built-in generic Cucumber steps.
     *  Default: true. */
    boolean builtInSteps() default true;
}
```

### `@PageObject`

Target: Class
Marks a POJO as a page object. Eliminates the need to extend `PickleibPageObject` or be listed in an `ObjectRepository`.

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PageObject {
    /** Platform: "web" (default), "android", "ios" */
    String platform() default "web";

    /** Optional explicit name. Default: class simple name. */
    String name() default "";
}
```

### `@ScreenObject`

Target: Class
Convenience alias for `@PageObject(platform = "mobile")`.

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ScreenObject {
    String name() default "";
}
```

### `@ContextValue`

Target: Field, Parameter
Injects a value from ContextStore at runtime.

```java
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface ContextValue {
    /** The ContextStore key */
    String value();

    /** Default value if key is not found */
    String defaultValue() default "";
}
```

### `@StepDefinitions`

Target: Class
Marks a class as containing Cucumber step definitions. The runner auto-injects `ElementRepository` and `PolymorphicUtilities` fields.

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface StepDefinitions {
}
```

---

## 2. PickleibRunner (JUnit 5 Extension)

**File:** `pickleib/runner/PickleibRunner.java` (replaces existing placeholder)

Implements:
- `BeforeAllCallback` — classpath scanning, page object registration
- `TestInstancePostProcessor` — `@ContextValue` field injection
- `BeforeEachCallback` — per-test setup (driver initialization)
- `AfterEachCallback` — per-test teardown (driver termination)

### Startup Flow

1. Find `@Pickleib` annotation on test class
2. Determine scan packages (annotation value or root package of test class)
3. Scan classpath for `@PageObject` and `@ScreenObject` classes
4. Register discovered classes in `PageObjectRegistry`
5. Create an `ElementRepository` backed by the registry
6. If `builtInSteps = true`, ensure `BuiltInSteps` class is in Cucumber's glue path

### Page Object Lifecycle

Page objects are instantiated **lazily** — when first accessed via `ElementRepository.acquireElementFromPage(elementName, pageName)`:

1. Registry looks up the class by page name
2. Instantiates via no-arg constructor
3. Calls `PageFactory.initElements()` with the appropriate decorator:
   - `platform = "web"` → standard `PageFactory.initElements(driver, instance)`
   - `platform = "android"` or `"ios"` → `AppiumFieldDecorator`
4. Caches the instance for the duration of the test
5. Extracts the requested `@FindBy` field via reflection

This means `@PageObject` classes are simple POJOs with `@FindBy` fields — no inheritance needed:

```java
@PageObject
public class LoginPage {
    @FindBy(id = "user-name")
    public WebElement usernameInput;

    @FindBy(css = "#login-button")
    public WebElement loginButton;
}
```

---

## 3. PageObjectRegistry

**File:** `pickleib/runner/PageObjectRegistry.java`

Runtime registry that replaces the manual `ObjectRepository` class.

```java
public class PageObjectRegistry implements ElementRepository {
    // Maps page name (case-insensitive) → page class metadata
    private final Map<String, PageObjectMetadata> registry = new HashMap<>();

    // Lazy-initialized page object instances (per-thread for parallel execution)
    private final ThreadLocal<Map<String, Object>> instances = ThreadLocal.withInitial(HashMap::new);

    void register(Class<?> pageClass, String name, String platform);

    // ElementRepository implementation
    WebElement acquireElementFromPage(String elementName, String pageName);
    List<WebElement> acquireElementsFromPage(String elementListName, String pageName);
    // ... other ElementRepository methods
}
```

`PageObjectMetadata` is a record:
```java
record PageObjectMetadata(Class<?> pageClass, String name, String platform) {}
```

The registry implements `ElementRepository` directly, so it plugs into the existing framework seamlessly — `BuiltInSteps` and user step classes both consume `ElementRepository`.

---

## 4. ClasspathScanner

**File:** `pickleib/runner/ClasspathScanner.java`

Scans packages for annotated classes using Java's `ClassLoader` and reflection.

```java
public class ClasspathScanner {
    /**
     * Finds all classes annotated with @PageObject or @ScreenObject in the given packages.
     */
    public static List<Class<?>> scanForPageObjects(String... packages);

    /**
     * Finds all classes annotated with @StepDefinitions in the given packages.
     */
    public static List<Class<?>> scanForStepDefinitions(String... packages);
}
```

Implementation uses `ClassLoader.getResources()` to find package directories, then scans `.class` files recursively. This is the same approach used by libraries like Reflections and Spring's ClassPathScanningCandidateComponentProvider, but simpler — no external dependency needed.

---

## 5. ContextValueInjector

**File:** `pickleib/runner/ContextValueInjector.java`

Handles `@ContextValue` injection for both fields and method parameters.

```java
public class ContextValueInjector {
    /**
     * Injects @ContextValue-annotated fields on the given instance.
     * Called by PickleibRunner's TestInstancePostProcessor.
     */
    public static void injectFields(Object instance);

    /**
     * Resolves a @ContextValue parameter value.
     * Called by Cucumber parameter type or custom ObjectFactory.
     */
    public static String resolveParameter(ContextValue annotation);
}
```

Field injection:
1. Scan instance class for fields annotated with `@ContextValue`
2. For each: `ContextStore.get(annotation.value(), annotation.defaultValue())`
3. Set field value via reflection (handles String, int, boolean, long conversions)

Parameter injection:
- Registers a Cucumber `@ParameterType` or uses a custom `ObjectFactory` to resolve `@ContextValue` on step method parameters

---

## 6. BuiltInSteps — Generic Cucumber Step Definitions

**File:** `pickleib/steps/BuiltInSteps.java`

A single class providing ~56 generic Cucumber steps that cover the patterns from the test-automation-template's CommonSteps. This class receives `ElementRepository` and interaction objects from the runner.

### Step Categories

**Navigation (~6 steps):**
- `Navigate to {url}` / `Navigate to the test page`
- `Navigate browser {forwards/backwards}`
- `Refresh the page`
- `Switch to the next active window` / `Switch to parent tab`
- `Set frame size to {width} x {height}`

**Click (~8 steps):**
- `Click the {elementName} on the {pageName}`
- `Click listed element {text} from {listName} list on the {pageName}`
- `Click listed element by attribute {attributeName}:{attributeValue} from {listName} list on the {pageName}`
- `Click the {elementName} on the {pageName} if present`
- `Click the {elementName} on the {pageName} if enabled`
- `Click towards the {elementName} on the {pageName}`
- `Click button by text {text}`

**Input (~4 steps):**
- `Fill input {elementName} on the {pageName} with text: {text}`
- `Fill form input on the {pageName}` (DataTable)
- `Fill listed input {elementName} on the {pageName} with text: {text}`
- `Fill iframe element {elementName} of {iframeName} on the {pageName} with text: {text}`

**Verification (~10 steps):**
- `Verify element {elementName} on the {pageName} has expected state: {state}`
- `Verify the text of {elementName} on the {pageName} is: {text}`
- `Verify the text of {elementName} on the {pageName} contains: {text}`
- `Verify element {elementName} on {pageName} has {attributeValue} for {attributeName}`
- `Verify element attribute {attributeName} of {elementName} on {pageName} contains {value}`
- `Verify listed element text on the {pageName}` (DataTable)
- `Verify the {url} is present in the current url`
- `Verify the current url is: {url}`
- `Verify page title contains: {title}`
- `Select listed element containing partial text {text} from {listName} on {pageName} and verify its text contains {expected}`

**Wait (~4 steps):**
- `Wait for {seconds} seconds`
- `Wait until {elementName} on the {pageName} is {state}`
- `Wait until {elementName} on the {pageName} has {attributeValue} for {attributeName}`

**Scroll (~4 steps):**
- `Scroll to the {elementName} on the {pageName}`
- `Center the {elementName} on the {pageName}`
- `Center listed element {text} from {listName} on the {pageName}`
- `Scroll in direction {up/down/left/right}`
- `Swipe until element with text {text} is found`

**Context (~4 steps):**
- `Update context {key} -> {value}`
- `Acquire {attributeName} attribute of {elementName} on {pageName} and save to context`
- `Save context value from {sourceKey} to {targetKey}`

**Window/Storage (~6 steps):**
- `Switch tab` / `Switch to parent tab`
- `Save the current url to context`
- `Add local storage values` (DataTable)
- `Add cookies` (DataTable)
- `Update cookie {cookieName} with value {cookieValue}`
- `Delete cookies`

**Misc (~6 steps):**
- `Upload file on input {elementName} on the {pageName} with file: {filePath}`
- `Execute JS command: {script}`
- `Take a screenshot`
- `Set default platform to {platform}`
- `Execute mobile command {command} on element {elementName} on {pageName}`
- `Listen to {eventName} event`

### How BuiltInSteps gets its dependencies

```java
public class BuiltInSteps extends InteractionBase implements PageRepository {
    private ElementRepository elementRepository;

    // Set by the runner during initialization
    public void setElementRepository(ElementRepository elementRepository) {
        this.elementRepository = elementRepository;
    }

    @Override
    public ElementRepository getElementRepository() {
        return elementRepository;
    }
}
```

The runner injects the `PageObjectRegistry` (which implements `ElementRepository`) into `BuiltInSteps` during Cucumber's initialization phase.

---

## 7. Consumer Usage — Before and After

### Before (current pattern — ~1050 lines of boilerplate)

```java
// ObjectRepository.java (~20 lines)
public class ObjectRepository implements PageObjectRepository {
    LoginPage loginPage;
    HomePage homePage;
    // ... every page listed manually
}

// LoginPage.java
public class LoginPage extends PickleibPageObject {
    @FindBy(id = "user-name") public WebElement usernameInput;
    @FindBy(css = "#login-button") public WebElement loginButton;
}

// CommonSteps.java (~994 lines of delegation boilerplate)
public class CommonSteps extends PickleibSteps {
    public CommonSteps() { super(ObjectRepository.class); }

    @When("Click the {string} on the {string}")
    public void click(String element, String page) { ... }
    // ... 55 more methods
}

// Hooks.java (~130 lines)
public class Hooks extends PickleibPageObject {
    @Before public void before(Scenario scenario) { ... }
    @After public void kill(Scenario scenario) { ... }
}
```

### After (with PickleibRunner — ~0 lines of boilerplate)

```java
// LoginPage.java — no inheritance needed
@PageObject
public class LoginPage {
    @FindBy(id = "user-name") public WebElement usernameInput;
    @FindBy(css = "#login-button") public WebElement loginButton;
}

// TestRunner.java
@Pickleib(scanPackages = "pages")
@CucumberOptions(features = "src/test/resources/features", glue = {"steps", "pickleib.steps"})
@RunWith(Cucumber.class)
public class TestRunner { }

// Only domain-specific steps remain:
// BookStoreApiSteps.java (47 lines) — kept as-is
// EmailSteps.java (131 lines) — kept as-is
```

No `ObjectRepository`. No `CommonSteps`. No `Hooks` boilerplate. No inheritance on page objects. Feature files stay exactly the same.

---

## 8. New File Structure

```
src/main/java/pickleib/
├── annotations/
│   ├── Pickleib.java
│   ├── PageObject.java
│   ├── ScreenObject.java
│   ├── ContextValue.java
│   └── StepDefinitions.java
├── runner/
│   ├── PickleibRunner.java          (JUnit 5 Extension — replaces placeholder)
│   ├── PageObjectRegistry.java      (runtime registry, implements ElementRepository)
│   ├── ClasspathScanner.java        (finds annotated classes)
│   └── ContextValueInjector.java    (@ContextValue field/parameter injection)
├── steps/
│   └── BuiltInSteps.java            (generic Cucumber steps)
```

---

## 9. Acceptance Criteria

1. A test class annotated with `@Pickleib` auto-discovers `@PageObject`-annotated classes without a manual ObjectRepository
2. `@PageObject` classes work without extending `PickleibPageObject` — `@FindBy` fields are initialized lazily by the runner
3. `@ScreenObject` classes work without extending `PickleibScreenObject`
4. `@ContextValue` injects values from ContextStore into fields and method parameters
5. Built-in generic steps (in `pickleib.steps`) handle all patterns from test-automation-template's CommonSteps
6. Feature files from test-automation-template run unchanged with the new runner
7. Existing `PickleibSteps` + manual `ObjectRepository` pattern still works (backward compatible)
8. Thread-safe — parallel test execution supported via ThreadLocal page object instances
