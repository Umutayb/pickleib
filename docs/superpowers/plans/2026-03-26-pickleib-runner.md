# PickleibRunner Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build an annotation-driven runner (`@Pickleib`) that eliminates boilerplate — auto-discovers page objects, injects ContextStore values, and provides built-in generic Cucumber step definitions.

**Architecture:** Runtime classpath scanning via JUnit 5 Extension. `@PageObject`-annotated POJOs are auto-registered into a `PageObjectRegistry` (implements `ElementRepository`). A `BuiltInSteps` class provides ~56 generic Cucumber steps matching the patterns from test-automation-template's CommonSteps. `@ContextValue` injects from ContextStore into fields and parameters.

**Tech Stack:** Java 17, JUnit 5 Extension API, Selenium PageFactory, Appium AppiumFieldDecorator, Cucumber-Java

**Spec:** `docs/superpowers/specs/2026-03-26-pickleib-runner-design.md`

---

## File Structure

### New files to create:
- `src/main/java/pickleib/annotations/Pickleib.java` — main entry point annotation
- `src/main/java/pickleib/annotations/PageObject.java` — marks page object classes
- `src/main/java/pickleib/annotations/ScreenObject.java` — alias for mobile page objects
- `src/main/java/pickleib/annotations/ContextValue.java` — context store injection
- `src/main/java/pickleib/annotations/StepDefinitions.java` — marks custom step classes
- `src/main/java/pickleib/runner/PickleibRunner.java` — JUnit 5 Extension (replaces placeholder)
- `src/main/java/pickleib/runner/PageObjectRegistry.java` — runtime registry, implements ElementRepository
- `src/main/java/pickleib/runner/ClasspathScanner.java` — finds annotated classes on classpath
- `src/main/java/pickleib/runner/ContextValueInjector.java` — @ContextValue injection logic
- `src/main/java/pickleib/steps/BuiltInSteps.java` — generic Cucumber step definitions
- `src/test/java/pickleib/runner/ClasspathScannerTest.java` — unit tests
- `src/test/java/pickleib/runner/PageObjectRegistryTest.java` — unit tests
- `src/test/java/pickleib/runner/ContextValueInjectorTest.java` — unit tests

### Files to modify:
- `pom.xml` — add Cucumber dependency (cucumber-java) for BuiltInSteps Gherkin annotations

---

## Task 1: Create Annotation Definitions

**Files:**
- Create: `src/main/java/pickleib/annotations/Pickleib.java`
- Create: `src/main/java/pickleib/annotations/PageObject.java`
- Create: `src/main/java/pickleib/annotations/ScreenObject.java`
- Create: `src/main/java/pickleib/annotations/ContextValue.java`
- Create: `src/main/java/pickleib/annotations/StepDefinitions.java`

- [ ] **Step 1: Create all 5 annotations**

```java
// Pickleib.java
package pickleib.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Main entry point annotation for the Pickleib framework.
 * Place on your test runner or test class to enable auto-discovery of page objects,
 * built-in step definitions, and context value injection.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Pickleib {
    /** Packages to scan for @PageObject and @ScreenObject classes.
     *  Default: root package of the annotated class. */
    String[] scanPackages() default {};

    /** Whether to register built-in generic Cucumber steps. Default: true. */
    boolean builtInSteps() default true;
}
```

```java
// PageObject.java
package pickleib.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class as a page object. The runner will auto-discover it,
 * handle PageFactory initialization, and register it for element access.
 * No need to extend PickleibPageObject or list it in an ObjectRepository.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PageObject {
    /** Platform: "web" (default), "android", "ios" */
    String platform() default "web";

    /** Optional explicit name. Default: class simple name. */
    String name() default "";
}
```

```java
// ScreenObject.java
package pickleib.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Convenience alias for {@code @PageObject(platform = "mobile")}.
 * Use for Appium-based mobile/desktop screen objects.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ScreenObject {
    /** Optional explicit name. Default: class simple name. */
    String name() default "";
}
```

```java
// ContextValue.java
package pickleib.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Injects a value from ContextStore into a field or method parameter.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface ContextValue {
    /** The ContextStore key. */
    String value();

    /** Default value if key is not found. */
    String defaultValue() default "";
}
```

```java
// StepDefinitions.java
package pickleib.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class as containing Cucumber step definitions.
 * The runner auto-injects ElementRepository and PolymorphicUtilities fields.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface StepDefinitions {
}
```

- [ ] **Step 2: Verify compilation**

Run: `mvn compile 2>&1 | tail -10`
Expected: BUILD SUCCESS

- [ ] **Step 3: Commit**

```bash
git add src/main/java/pickleib/annotations/
git commit -m "feat: add Pickleib annotation definitions (@Pickleib, @PageObject, @ScreenObject, @ContextValue, @StepDefinitions)"
```

---

## Task 2: ClasspathScanner with Tests (TDD)

**Files:**
- Create: `src/main/java/pickleib/runner/ClasspathScanner.java`
- Create: `src/test/java/pickleib/runner/ClasspathScannerTest.java`

- [ ] **Step 1: Write failing tests**

```java
package pickleib.runner;

import org.junit.jupiter.api.Test;
import pickleib.annotations.PageObject;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ClasspathScannerTest {

    @Test
    void scanForAnnotatedClasses_finds_annotated_classes_in_package() {
        // The test page objects in src/test/java use PickleibPageObject, not @PageObject
        // So we test with the pickleib.annotations package itself (no @PageObject there)
        List<Class<?>> results = ClasspathScanner.scanForAnnotatedClasses(
            PageObject.class, "pickleib.annotations"
        );
        // No classes in pickleib.annotations are annotated with @PageObject
        assertTrue(results.isEmpty());
    }

    @Test
    void scanForAnnotatedClasses_returns_empty_for_nonexistent_package() {
        List<Class<?>> results = ClasspathScanner.scanForAnnotatedClasses(
            PageObject.class, "com.nonexistent.package"
        );
        assertTrue(results.isEmpty());
    }

    @Test
    void scanForAnnotatedClasses_handles_null_packages_gracefully() {
        List<Class<?>> results = ClasspathScanner.scanForAnnotatedClasses(
            PageObject.class
        );
        assertNotNull(results);
    }
}
```

- [ ] **Step 2: Run tests to verify they fail**

Run: `mvn test -Dtest=pickleib.runner.ClasspathScannerTest -Dsurefire.useFile=false 2>&1 | tail -20`
Expected: Compilation error.

- [ ] **Step 3: Implement ClasspathScanner**

```java
package pickleib.runner;

import utils.Printer;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Scans the classpath for classes annotated with specific annotations.
 * Used by PickleibRunner to auto-discover @PageObject and @ScreenObject classes.
 */
public class ClasspathScanner {

    private static final Printer log = new Printer(ClasspathScanner.class);

    /**
     * Finds all classes in the specified packages annotated with the given annotation.
     *
     * @param annotation the annotation to search for
     * @param packages   packages to scan (scans all if empty)
     * @return list of matching classes
     */
    public static List<Class<?>> scanForAnnotatedClasses(
            Class<? extends Annotation> annotation,
            String... packages) {
        List<Class<?>> results = new ArrayList<>();
        if (packages == null || packages.length == 0) return results;

        for (String pkg : packages) {
            try {
                results.addAll(scanPackage(pkg, annotation));
            } catch (Exception e) {
                log.warning("Failed to scan package: " + pkg + " — " + e.getMessage());
            }
        }
        return results;
    }

    private static List<Class<?>> scanPackage(String packageName, Class<? extends Annotation> annotation) throws Exception {
        List<Class<?>> classes = new ArrayList<>();
        String path = packageName.replace('.', '/');
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> resources = classLoader.getResources(path);

        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            if (resource.getProtocol().equals("file")) {
                File directory = new File(resource.toURI());
                if (directory.exists()) {
                    findClasses(directory, packageName, annotation, classes);
                }
            }
        }
        return classes;
    }

    private static void findClasses(
            File directory,
            String packageName,
            Class<? extends Annotation> annotation,
            List<Class<?>> results) {
        File[] files = directory.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                findClasses(file, packageName + "." + file.getName(), annotation, results);
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + "." + file.getName().replace(".class", "");
                try {
                    Class<?> clazz = Class.forName(className);
                    if (clazz.isAnnotationPresent(annotation)) {
                        results.add(clazz);
                    }
                } catch (ClassNotFoundException | NoClassDefFoundError e) {
                    // Skip classes that can't be loaded
                }
            }
        }
    }
}
```

- [ ] **Step 4: Run tests**

Run: `mvn test -Dtest=pickleib.runner.ClasspathScannerTest -Dsurefire.useFile=false 2>&1 | tail -20`
Expected: All PASS

- [ ] **Step 5: Commit**

```bash
git add src/main/java/pickleib/runner/ClasspathScanner.java src/test/java/pickleib/runner/ClasspathScannerTest.java
git commit -m "feat: add ClasspathScanner for runtime annotation discovery"
```

---

## Task 3: PageObjectRegistry with Tests (TDD)

**Files:**
- Create: `src/main/java/pickleib/runner/PageObjectRegistry.java`
- Create: `src/test/java/pickleib/runner/PageObjectRegistryTest.java`

- [ ] **Step 1: Write failing tests**

```java
package pickleib.runner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import pickleib.annotations.PageObject;

import static org.junit.jupiter.api.Assertions.*;

class PageObjectRegistryTest {

    PageObjectRegistry registry;

    // Test page object — annotated, no inheritance needed
    @PageObject
    static class TestLoginPage {
        @FindBy(id = "username")
        public WebElement usernameInput;

        @FindBy(id = "password")
        public WebElement passwordInput;
    }

    @BeforeEach
    void setUp() {
        registry = new PageObjectRegistry();
    }

    @Test
    void register_and_lookup_by_class_name() {
        registry.register(TestLoginPage.class, "", "web");
        // Verify the page is registered (lookup by simple name)
        assertTrue(registry.isRegistered("TestLoginPage"));
    }

    @Test
    void register_with_custom_name() {
        registry.register(TestLoginPage.class, "Login", "web");
        assertTrue(registry.isRegistered("Login"));
        assertFalse(registry.isRegistered("TestLoginPage"));
    }

    @Test
    void isRegistered_returns_false_for_unknown() {
        assertFalse(registry.isRegistered("NonExistent"));
    }

    @Test
    void getPageClass_returns_correct_class() {
        registry.register(TestLoginPage.class, "", "web");
        assertEquals(TestLoginPage.class, registry.getPageClass("TestLoginPage"));
    }

    @Test
    void getPageClass_is_case_insensitive() {
        registry.register(TestLoginPage.class, "", "web");
        assertEquals(TestLoginPage.class, registry.getPageClass("testloginpage"));
    }

    @Test
    void getPageClass_throws_for_unknown() {
        assertThrows(NoSuchElementException.class, () -> registry.getPageClass("Unknown"));
    }
}
```

- [ ] **Step 2: Run tests to verify they fail**

Run: `mvn test -Dtest=pickleib.runner.PageObjectRegistryTest -Dsurefire.useFile=false 2>&1 | tail -20`
Expected: Compilation error.

- [ ] **Step 3: Implement PageObjectRegistry**

The registry stores page class metadata and provides element access via reflection and PageFactory. It implements `ElementRepository` so it plugs directly into the existing framework.

Key design:
- `register(Class, name, platform)` — stores metadata
- `isRegistered(name)` / `getPageClass(name)` — lookup
- `acquireElementFromPage(elementName, pageName)` — instantiates page object lazily, runs PageFactory, returns field by name
- `acquireElementsFromPage(elementListName, pageName)` — same for List<WebElement> fields
- Page instances are cached per-thread (`ThreadLocal<Map<String, Object>>`)
- PageFactory is initialized using the correct driver: web → `PageFactory.initElements(PickleibWebDriver.get(), instance)`, mobile → `AppiumFieldDecorator`

The `acquireElementFromPage` flow:
1. Look up page class by `pageName` (case-insensitive)
2. Check thread-local cache for existing instance
3. If not cached: instantiate via no-arg constructor, call `PageFactory.initElements()` based on platform, cache it
4. Use reflection to get field `elementName` from the instance
5. Return as WebElement

Implement `ElementRepository` methods: `acquireElementFromPage`, `acquireElementsFromPage`, `acquireListedElementFromPage`, `acquireListedElementByAttribute`, `acquireElementList`, `acquireElementBundleFromPage`, `acquireElementBundlesFromPage`.

For listed element methods, delegate to the existing `ElementAcquisition.acquireNamedElementAmongst` and `ElementAcquisition.acquireElementUsingAttributeAmongst` static methods.

For `acquireElementList` (DataTable-based form filling), iterate the maps, look up each element field, create `ElementBundle<String>` instances.

- [ ] **Step 4: Run tests**

Run: `mvn test -Dtest=pickleib.runner.PageObjectRegistryTest -Dsurefire.useFile=false 2>&1 | tail -20`
Expected: All PASS

- [ ] **Step 5: Commit**

```bash
git add src/main/java/pickleib/runner/PageObjectRegistry.java src/test/java/pickleib/runner/PageObjectRegistryTest.java
git commit -m "feat: add PageObjectRegistry for auto-discovered page objects"
```

---

## Task 4: ContextValueInjector with Tests (TDD)

**Files:**
- Create: `src/main/java/pickleib/runner/ContextValueInjector.java`
- Create: `src/test/java/pickleib/runner/ContextValueInjectorTest.java`

- [ ] **Step 1: Write failing tests**

```java
package pickleib.runner;

import context.ContextStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pickleib.annotations.ContextValue;

import static org.junit.jupiter.api.Assertions.*;

class ContextValueInjectorTest {

    static class TestTarget {
        @ContextValue("test-key")
        String injectedValue;

        @ContextValue(value = "missing-key", defaultValue = "fallback")
        String withDefault;

        String notAnnotated = "original";
    }

    @BeforeEach
    void setUp() {
        ContextStore.put("test-key", "hello");
    }

    @Test
    void injectFields_sets_annotated_field() {
        TestTarget target = new TestTarget();
        ContextValueInjector.injectFields(target);
        assertEquals("hello", target.injectedValue);
    }

    @Test
    void injectFields_uses_default_when_key_missing() {
        TestTarget target = new TestTarget();
        ContextValueInjector.injectFields(target);
        assertEquals("fallback", target.withDefault);
    }

    @Test
    void injectFields_ignores_non_annotated_fields() {
        TestTarget target = new TestTarget();
        ContextValueInjector.injectFields(target);
        assertEquals("original", target.notAnnotated);
    }
}
```

- [ ] **Step 2: Run tests to verify they fail**

- [ ] **Step 3: Implement ContextValueInjector**

```java
package pickleib.runner;

import context.ContextStore;
import pickleib.annotations.ContextValue;
import utils.Printer;

import java.lang.reflect.Field;

/**
 * Handles @ContextValue injection for fields and method parameters.
 */
public class ContextValueInjector {

    private static final Printer log = new Printer(ContextValueInjector.class);

    /**
     * Injects @ContextValue-annotated fields on the given instance.
     */
    public static void injectFields(Object instance) {
        for (Field field : instance.getClass().getDeclaredFields()) {
            ContextValue annotation = field.getAnnotation(ContextValue.class);
            if (annotation == null) continue;

            String value = ContextStore.get(annotation.value(), annotation.defaultValue());
            try {
                field.setAccessible(true);
                field.set(instance, convertValue(value, field.getType()));
            } catch (IllegalAccessException e) {
                log.warning("Failed to inject @ContextValue for field: " + field.getName());
            }
        }
    }

    private static Object convertValue(String value, Class<?> targetType) {
        if (targetType == String.class) return value;
        if (targetType == int.class || targetType == Integer.class) return Integer.parseInt(value);
        if (targetType == long.class || targetType == Long.class) return Long.parseLong(value);
        if (targetType == boolean.class || targetType == Boolean.class) return Boolean.parseBoolean(value);
        if (targetType == double.class || targetType == Double.class) return Double.parseDouble(value);
        return value;
    }
}
```

- [ ] **Step 4: Run tests**

Expected: All PASS

- [ ] **Step 5: Commit**

```bash
git add src/main/java/pickleib/runner/ContextValueInjector.java src/test/java/pickleib/runner/ContextValueInjectorTest.java
git commit -m "feat: add ContextValueInjector for @ContextValue field injection"
```

---

## Task 5: PickleibRunner JUnit 5 Extension

**Files:**
- Create: `src/main/java/pickleib/runner/PickleibRunner.java` (replaces existing placeholder)

- [ ] **Step 1: Read existing placeholder**

Read `src/main/java/pickleib/runner/PickleibRunner.java` to see the current TODO.

- [ ] **Step 2: Implement PickleibRunner**

The extension implements `BeforeAllCallback`, `TestInstancePostProcessor`:

```java
package pickleib.runner;

import org.junit.jupiter.api.extension.*;
import pickleib.annotations.PageObject;
import pickleib.annotations.Pickleib;
import pickleib.annotations.ScreenObject;
import utils.Printer;

import java.util.List;

/**
 * JUnit 5 Extension that powers the @Pickleib annotation.
 * Handles classpath scanning, page object registration, and @ContextValue injection.
 */
public class PickleibRunner implements BeforeAllCallback, TestInstancePostProcessor {

    private static final Printer log = new Printer(PickleibRunner.class);
    private static final PageObjectRegistry registry = new PageObjectRegistry();

    /**
     * Returns the global page object registry.
     * Used by BuiltInSteps and other consumers to access discovered page objects.
     */
    public static PageObjectRegistry getRegistry() {
        return registry;
    }

    @Override
    public void beforeAll(ExtensionContext context) {
        Class<?> testClass = context.getRequiredTestClass();
        Pickleib annotation = testClass.getAnnotation(Pickleib.class);
        if (annotation == null) return;

        String[] packages = annotation.scanPackages();
        if (packages.length == 0) {
            // Default: scan root package of test class
            String pkg = testClass.getPackageName();
            if (pkg.contains(".")) pkg = pkg.substring(0, pkg.indexOf('.'));
            packages = new String[]{pkg};
        }

        // Scan for @PageObject classes
        List<Class<?>> pageObjects = ClasspathScanner.scanForAnnotatedClasses(PageObject.class, packages);
        for (Class<?> clazz : pageObjects) {
            PageObject po = clazz.getAnnotation(PageObject.class);
            registry.register(clazz, po.name(), po.platform());
            log.info("Registered @PageObject: " + clazz.getSimpleName());
        }

        // Scan for @ScreenObject classes
        List<Class<?>> screenObjects = ClasspathScanner.scanForAnnotatedClasses(ScreenObject.class, packages);
        for (Class<?> clazz : screenObjects) {
            ScreenObject so = clazz.getAnnotation(ScreenObject.class);
            registry.register(clazz, so.name(), "mobile");
            log.info("Registered @ScreenObject: " + clazz.getSimpleName());
        }

        log.info("Pickleib initialized with " + registry.size() + " page objects");
    }

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) {
        ContextValueInjector.injectFields(testInstance);
    }
}
```

- [ ] **Step 3: Verify compilation**

Run: `mvn compile 2>&1 | tail -10`
Expected: BUILD SUCCESS

- [ ] **Step 4: Commit**

```bash
git add src/main/java/pickleib/runner/PickleibRunner.java
git commit -m "feat: implement PickleibRunner JUnit 5 Extension"
```

---

## Task 6: Add Cucumber Dependency + BuiltInSteps

**Files:**
- Modify: `pom.xml` — add cucumber-java dependency
- Create: `src/main/java/pickleib/steps/BuiltInSteps.java`

- [ ] **Step 1: Add Cucumber dependency to pom.xml**

Add to pom.xml dependencies:
```xml
<!-- Cucumber (for built-in step definitions) -->
<dependency>
    <groupId>io.cucumber</groupId>
    <artifactId>cucumber-java</artifactId>
    <version>7.14.0</version>
    <optional>true</optional>
</dependency>
```

Mark as `<optional>true</optional>` — consumers who don't use Cucumber don't need it.

- [ ] **Step 2: Create BuiltInSteps**

Create `src/main/java/pickleib/steps/BuiltInSteps.java` — this is the largest file. It contains all ~56 generic Cucumber step definitions that replace the 994-line CommonSteps from test-automation-template.

The class extends `InteractionBase` and implements `PageRepository`. It gets its `ElementRepository` from `PickleibRunner.getRegistry()`.

Key implementation notes:
- Import all Cucumber annotations: `io.cucumber.java.en.*`
- Import `io.cucumber.datatable.DataTable` for form-filling steps
- Step patterns must match the exact Gherkin patterns used in test-automation-template feature files
- Use `contextCheck()` from `StringUtilities` for CONTEXT- prefix resolution
- Use `getInteractions(element)` for polymorphic Web/Mobile dispatch
- Use `isPlatformElement()` for scroll-before-click decisions

The step methods should be organized in this order:
1. Platform/context management
2. Navigation
3. Window/tab management
4. Storage (localStorage, cookies)
5. Click interactions
6. Input/form filling
7. IFrame interactions
8. Scroll/swipe
9. Centering
10. Verification (text, state, attribute, URL)
11. Wait operations
12. Context updates
13. Attribute acquisition
14. File upload
15. JavaScript execution
16. Mobile commands
17. Event listening
18. Assertions
19. Element bundle interactions

Each method body should be a direct translation from `test-automation-template/src/test/java/steps/CommonSteps.java` — same logic, same Gherkin patterns, but now provided by the framework instead of by the consumer.

Constructor:
```java
public BuiltInSteps() {
    super(true, true); // Initialize both web and platform interactions
}

@Override
public ElementRepository getElementRepository() {
    return PickleibRunner.getRegistry();
}
```

- [ ] **Step 3: Verify compilation**

Run: `mvn compile 2>&1 | tail -10`
Expected: BUILD SUCCESS

- [ ] **Step 4: Commit**

```bash
git add pom.xml src/main/java/pickleib/steps/BuiltInSteps.java
git commit -m "feat: add BuiltInSteps with ~56 generic Cucumber step definitions

Replaces the need for a manual CommonSteps class. Steps are auto-available
when 'pickleib.steps' is included in Cucumber's glue path."
```

---

## Task 7: Integration Test — Verify Full Flow

**Files:**
- Create: `src/test/java/pickleib/runner/PickleibRunnerIntegrationTest.java`

- [ ] **Step 1: Write integration test**

This test verifies the full annotation flow works end-to-end:
- `@Pickleib` triggers classpath scanning
- `@PageObject` classes are discovered and registered
- `@ContextValue` fields are injected
- `PageObjectRegistry` returns correct element repository

```java
package pickleib.runner;

import context.ContextStore;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import pickleib.annotations.ContextValue;
import pickleib.annotations.PageObject;
import pickleib.annotations.Pickleib;

import static org.junit.jupiter.api.Assertions.*;

@Pickleib(scanPackages = "pickleib.runner")
@ExtendWith(PickleibRunner.class)
class PickleibRunnerIntegrationTest {

    @PageObject
    static class IntegrationTestPage {
        // No @FindBy — just testing registration
    }

    @ContextValue(value = "integration-test-key", defaultValue = "test-value")
    String injectedValue;

    @Test
    void runner_discovers_page_objects() {
        assertTrue(PickleibRunner.getRegistry().isRegistered("IntegrationTestPage"));
    }

    @Test
    void runner_injects_context_values() {
        assertEquals("test-value", injectedValue);
    }

    @Test
    void runner_injects_context_values_from_store() {
        ContextStore.put("integration-test-key", "from-store");
        // Re-inject (simulating what happens on next test instance)
        ContextValueInjector.injectFields(this);
        assertEquals("from-store", injectedValue);
    }

    @Test
    void registry_provides_element_repository() {
        assertNotNull(PickleibRunner.getRegistry());
    }
}
```

- [ ] **Step 2: Run integration test**

Run: `mvn test -Dtest=pickleib.runner.PickleibRunnerIntegrationTest -Dsurefire.useFile=false 2>&1 | tail -20`
Expected: All PASS

- [ ] **Step 3: Commit**

```bash
git add src/test/java/pickleib/runner/PickleibRunnerIntegrationTest.java
git commit -m "test: add PickleibRunner integration test for full annotation flow"
```

---

## Task 8: Final Verification

- [ ] **Step 1: Run full build**

Run: `mvn clean compile 2>&1 | tail -15`
Expected: BUILD SUCCESS

- [ ] **Step 2: Run all tests**

Run: `mvn test -Dsurefire.useFile=false 2>&1 | tail -30`
Expected: All unit and integration tests PASS

- [ ] **Step 3: Verify file structure**

```bash
find src/main/java/pickleib/annotations -name "*.java" | sort
find src/main/java/pickleib/runner -name "*.java" | sort
find src/main/java/pickleib/steps -name "*.java" | sort
```

Expected:
```
src/main/java/pickleib/annotations/ContextValue.java
src/main/java/pickleib/annotations/PageObject.java
src/main/java/pickleib/annotations/Pickleib.java
src/main/java/pickleib/annotations/ScreenObject.java
src/main/java/pickleib/annotations/StepDefinitions.java
src/main/java/pickleib/runner/ClasspathScanner.java
src/main/java/pickleib/runner/ContextValueInjector.java
src/main/java/pickleib/runner/PageObjectRegistry.java
src/main/java/pickleib/runner/PickleibRunner.java
src/main/java/pickleib/steps/BuiltInSteps.java
```

- [ ] **Step 4: Git log**

Run: `git log --oneline`
Expected: Clean commit history with one commit per task.
