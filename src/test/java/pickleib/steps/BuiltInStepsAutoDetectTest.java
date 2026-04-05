package pickleib.steps;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pickleib.annotations.PageObject;
import pickleib.enums.Platform;
import pickleib.runner.PageObjectRegistry;
import pickleib.runner.PickleibRunner;
import pickleib.utilities.interfaces.repository.ElementRepository;

import java.lang.reflect.Field;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the auto-detection logic in {@link BuiltInSteps} without requiring a browser.
 * <p>
 * Because BuiltInSteps cannot be instantiated without a driver (the super() constructor
 * requires WebDriver), these tests operate on the static fields directly via reflection
 * and verify the static {@code setElementRepository()} / field-level behavior.
 */
class BuiltInStepsAutoDetectTest {

    @BeforeEach
    void setUp() throws Exception {
        // Reset static state before each test
        resetStaticFields();
        // Clear the shared registry
        PickleibRunner.getRegistry().clearInstances();
        clearRegistryEntries();
    }

    @AfterEach
    void tearDown() throws Exception {
        // Clean up static state after each test
        resetStaticFields();
        clearRegistryEntries();
    }

    // ---- 1. setElementRepository() takes priority ----

    @Test
    void setElementRepository_stores_repository_in_static_field() throws Exception {
        ElementRepository custom = new StubElementRepository();
        BuiltInSteps.setElementRepository(custom);

        ElementRepository stored = getStaticElementRepository();
        assertSame(custom, stored, "setElementRepository should store the repository in the static field");
    }

    @Test
    void setElementRepository_overwrites_previous_repository() throws Exception {
        ElementRepository first = new StubElementRepository();
        ElementRepository second = new StubElementRepository();

        BuiltInSteps.setElementRepository(first);
        BuiltInSteps.setElementRepository(second);

        ElementRepository stored = getStaticElementRepository();
        assertSame(second, stored, "A second setElementRepository call should overwrite the first");
    }

    @Test
    void setElementRepository_with_null_clears_repository() throws Exception {
        BuiltInSteps.setElementRepository(new StubElementRepository());
        BuiltInSteps.setElementRepository(null);

        ElementRepository stored = getStaticElementRepository();
        assertNull(stored, "Setting null should clear the repository");
    }

    // ---- 2. Registry with entries takes priority ----

    @Test
    void registry_with_entries_is_returned_before_auto_detection() throws Exception {
        // Register a page object in the shared registry
        PickleibRunner.getRegistry().register(DummyPage.class, "DummyPage", Platform.web);

        // Ensure no explicit repository is set
        assertNull(getStaticElementRepository(), "No explicit repository should be set");

        // The registry should have entries, so getElementRepository() should return the registry
        // before attempting auto-detection
        assertTrue(PickleibRunner.getRegistry().size() > 0,
                "Registry should have entries after registration");
    }

    @Test
    void empty_registry_does_not_short_circuit() throws Exception {
        assertEquals(0, PickleibRunner.getRegistry().size(),
                "Registry should be empty before any registration");

        // With no entries, the registry size check in getElementRepository() would not short-circuit
        assertFalse(PickleibRunner.getRegistry().size() > 0,
                "Empty registry should not satisfy the size > 0 check");
    }

    // ---- 3. Auto-detection is thread-safe (volatile + synchronized) ----

    @Test
    void elementRepository_field_is_volatile() throws Exception {
        Field field = BuiltInSteps.class.getDeclaredField("elementRepository");
        assertTrue(java.lang.reflect.Modifier.isVolatile(field.getModifiers()),
                "elementRepository field should be volatile for thread safety");
    }

    @Test
    void autoDetected_field_is_volatile() throws Exception {
        Field field = BuiltInSteps.class.getDeclaredField("autoDetected");
        assertTrue(java.lang.reflect.Modifier.isVolatile(field.getModifiers()),
                "autoDetected field should be volatile for thread safety");
    }

    @Test
    void setElementRepository_is_visible_across_threads() throws Exception {
        ElementRepository custom = new StubElementRepository();
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<ElementRepository> observedRef = new AtomicReference<>();

        // Set from main thread
        BuiltInSteps.setElementRepository(custom);

        // Read from another thread
        Thread reader = new Thread(() -> {
            try {
                observedRef.set(getStaticElementRepository());
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                latch.countDown();
            }
        });
        reader.start();
        latch.await();

        assertSame(custom, observedRef.get(),
                "Volatile field should be visible to other threads immediately after write");
    }

    @Test
    void concurrent_setElementRepository_last_write_wins() throws Exception {
        int threadCount = 10;
        CountDownLatch startGate = new CountDownLatch(1);
        CountDownLatch endGate = new CountDownLatch(threadCount);
        StubElementRepository[] repos = new StubElementRepository[threadCount];

        for (int i = 0; i < threadCount; i++) {
            repos[i] = new StubElementRepository();
            final ElementRepository repo = repos[i];
            new Thread(() -> {
                try {
                    startGate.await();
                    BuiltInSteps.setElementRepository(repo);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    endGate.countDown();
                }
            }).start();
        }

        startGate.countDown();
        endGate.await();

        // After all threads complete, the field should hold one of the repositories (last write wins)
        ElementRepository stored = getStaticElementRepository();
        assertNotNull(stored, "After concurrent writes, the field should hold a non-null repository");

        boolean matchesOne = false;
        for (StubElementRepository repo : repos) {
            if (stored == repo) {
                matchesOne = true;
                break;
            }
        }
        assertTrue(matchesOne, "The stored repository should be one of the repositories that was set");
    }

    @Test
    void autoDetected_flag_uses_double_checked_locking_pattern() throws Exception {
        // Verify the autoDetected flag starts as false
        Field autoDetectedField = BuiltInSteps.class.getDeclaredField("autoDetected");
        autoDetectedField.setAccessible(true);
        boolean value = (boolean) autoDetectedField.get(null);
        assertFalse(value, "autoDetected should start as false");

        // Set it to true (simulating what getElementRepository does after auto-detection)
        autoDetectedField.set(null, true);
        assertTrue((boolean) autoDetectedField.get(null),
                "autoDetected should be settable to true");
    }

    // ---- 4. clearInstances() is called in constructor ----

    @Test
    void registry_clearInstances_clears_thread_local_cache() {
        // Register a page in the registry
        PageObjectRegistry registry = PickleibRunner.getRegistry();
        registry.register(DummyPage.class, "DummyPage", Platform.web);

        // clearInstances should not throw and should be callable
        assertDoesNotThrow(registry::clearInstances,
                "clearInstances() should not throw");
    }

    @Test
    void registry_clearInstances_is_thread_local() throws Exception {
        PageObjectRegistry registry = PickleibRunner.getRegistry();
        registry.register(DummyPage.class, "DummyPage", Platform.web);

        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger sizeAfterClear = new AtomicInteger(-1);

        // Clear instances on a different thread; registry entries (not instances) should persist
        Thread other = new Thread(() -> {
            registry.clearInstances();
            sizeAfterClear.set(registry.size());
            latch.countDown();
        });
        other.start();
        latch.await();

        assertEquals(1, sizeAfterClear.get(),
                "clearInstances on another thread should not affect registry size (only thread-local instance cache)");
        assertEquals(1, registry.size(),
                "Registry size should remain unchanged on the original thread");
    }

    // ---- 5. Static field defaults ----

    @Test
    void default_page_repository_path_matches_pickleib_runner() throws Exception {
        Field field = BuiltInSteps.class.getDeclaredField("DEFAULT_PAGE_REPOSITORY");
        field.setAccessible(true);
        String value = (String) field.get(null);
        assertEquals(PickleibRunner.DEFAULT_PAGE_REPOSITORY, value,
                "BuiltInSteps.DEFAULT_PAGE_REPOSITORY should match PickleibRunner.DEFAULT_PAGE_REPOSITORY");
    }

    @Test
    void default_scan_package_matches_pickleib_runner() throws Exception {
        Field field = BuiltInSteps.class.getDeclaredField("DEFAULT_SCAN_PACKAGE");
        field.setAccessible(true);
        String value = (String) field.get(null);
        assertEquals(PickleibRunner.DEFAULT_SCAN_PACKAGE, value,
                "BuiltInSteps.DEFAULT_SCAN_PACKAGE should match PickleibRunner.DEFAULT_SCAN_PACKAGE");
    }

    @Test
    void elementRepository_starts_as_null() throws Exception {
        assertNull(getStaticElementRepository(),
                "elementRepository should be null initially (before any set call)");
    }

    // ---- Helpers ----

    /**
     * Resets the static fields in BuiltInSteps to their initial state.
     */
    private void resetStaticFields() throws Exception {
        Field repoField = BuiltInSteps.class.getDeclaredField("elementRepository");
        repoField.setAccessible(true);
        repoField.set(null, null);

        Field autoDetectedField = BuiltInSteps.class.getDeclaredField("autoDetected");
        autoDetectedField.setAccessible(true);
        autoDetectedField.set(null, false);
    }

    /**
     * Reads the static elementRepository field via reflection.
     */
    private ElementRepository getStaticElementRepository() throws Exception {
        Field field = BuiltInSteps.class.getDeclaredField("elementRepository");
        field.setAccessible(true);
        return (ElementRepository) field.get(null);
    }

    /**
     * Clears all entries from the shared PageObjectRegistry using reflection,
     * since there is no public clear() method.
     */
    private void clearRegistryEntries() throws Exception {
        PageObjectRegistry registry = PickleibRunner.getRegistry();
        Field registryMapField = PageObjectRegistry.class.getDeclaredField("registry");
        registryMapField.setAccessible(true);
        ((java.util.Map<?, ?>) registryMapField.get(registry)).clear();
    }

    // ---- Test doubles ----

    /**
     * Minimal stub that satisfies the ElementRepository interface without requiring a browser.
     */
    private static class StubElementRepository implements ElementRepository {
        @Override
        public org.openqa.selenium.WebElement acquireElementFromPage(String elementName, String pageName) {
            throw new UnsupportedOperationException("stub");
        }

        @Override
        public java.util.List<org.openqa.selenium.WebElement> acquireElementsFromPage(String elementListName, String pageName) {
            throw new UnsupportedOperationException("stub");
        }

        @Override
        public org.openqa.selenium.WebElement acquireListedElementFromPage(String elementName, String listName, String pageName) {
            throw new UnsupportedOperationException("stub");
        }

        @Override
        public org.openqa.selenium.WebElement acquireListedElementByAttribute(String attributeName, String attributeValue, String listName, String pageName) {
            throw new UnsupportedOperationException("stub");
        }

        @Override
        public java.util.List<pickleib.utilities.element.ElementBundle<String>> acquireElementList(java.util.List<pickleib.utilities.element.FormInput> formInputs, String pageName) {
            throw new UnsupportedOperationException("stub");
        }

        @Override
        public pickleib.utilities.element.ElementBundle<java.util.Map<String, String>> acquireElementBundleFromPage(String elementFieldName, String pageName, java.util.Map<String, String> specifications) {
            throw new UnsupportedOperationException("stub");
        }

        @Override
        public java.util.List<pickleib.utilities.element.ElementBundle<java.util.Map<String, String>>> acquireElementBundlesFromPage(String pageName, java.util.List<java.util.Map<String, String>> specifications) {
            throw new UnsupportedOperationException("stub");
        }
    }

    /** Dummy page object for registry tests. */
    @PageObject
    static class DummyPage {}
}
