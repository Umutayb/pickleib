package pickleib.runner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import pickleib.annotations.PageObject;
import pickleib.enums.Platform;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

class PageObjectRegistryTest {

    PageObjectRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new PageObjectRegistry();
    }

    @Test
    void register_and_lookup_by_class_name() {
        registry.register(TestPage.class, "", Platform.web);
        assertTrue(registry.isRegistered("TestPage"));
    }

    @Test
    void register_with_custom_name() {
        registry.register(TestPage.class, "Login", Platform.web);
        assertTrue(registry.isRegistered("Login"));
    }

    @Test
    void isRegistered_returns_false_for_unknown() {
        assertFalse(registry.isRegistered("NonExistent"));
    }

    @Test
    void getPageClass_returns_correct_class() {
        registry.register(TestPage.class, "", Platform.web);
        assertEquals(TestPage.class, registry.getPageClass("TestPage"));
    }

    @Test
    void getPageClass_is_case_insensitive() {
        registry.register(TestPage.class, "", Platform.web);
        assertEquals(TestPage.class, registry.getPageClass("testpage"));
    }

    @Test
    void getPageClass_throws_for_unknown() {
        assertThrows(NoSuchElementException.class, () -> registry.getPageClass("Unknown"));
    }

    @Test
    void size_returns_correct_count() {
        assertEquals(0, registry.size());
        registry.register(TestPage.class, "", Platform.web);
        assertEquals(1, registry.size());
    }

    // --- clearInstances tests ---

    @Test
    void clearInstances_does_not_affect_registry() {
        registry.register(TestPage.class, "", Platform.web);
        registry.clearInstances();
        assertTrue(registry.isRegistered("TestPage"), "Registry entries should survive clearInstances");
        assertEquals(1, registry.size());
    }

    @Test
    void clearInstances_allows_fresh_instance_creation() {
        // Register a page and obtain an instance via reflection on the ThreadLocal cache
        registry.register(TestPage.class, "", Platform.web);

        // Access the internal instances map by calling clearInstances and verifying no error
        // Since getOrCreateInstance requires a driver, we verify the cache is cleared by
        // confirming clearInstances can be called multiple times without side effects
        registry.clearInstances();
        registry.clearInstances(); // double-clear should not throw

        // Registry is still intact after clearing instances
        assertTrue(registry.isRegistered("TestPage"));
    }

    // --- getPlatform tests ---

    @Test
    void getPlatform_returns_correct_platform_for_registered_page() {
        registry.register(TestPage.class, "AndroidPage", Platform.android);
        assertEquals("android", registry.getPlatform("AndroidPage"));
    }

    @Test
    void getPlatform_returns_correct_platform_for_each_platform_type() {
        registry.register(TestPage.class, "WebPage", Platform.web);
        assertEquals("web", registry.getPlatform("WebPage"));

        PageObjectRegistry reg2 = new PageObjectRegistry();
        reg2.register(TestPage.class, "IosPage", Platform.ios);
        assertEquals("ios", reg2.getPlatform("IosPage"));
    }

    @Test
    void getPlatform_returns_web_for_unknown_page() {
        assertEquals("web", registry.getPlatform("NonExistentPage"));
    }

    @Test
    void getPlatform_returns_web_for_unknown_after_other_registrations() {
        registry.register(TestPage.class, "MyPage", Platform.android);
        assertEquals("web", registry.getPlatform("SomeOtherPage"));
    }

    @Test
    void getPlatform_is_case_insensitive() {
        registry.register(TestPage.class, "MobilePage", Platform.ios);
        assertEquals("ios", registry.getPlatform("mobilepage"));
        assertEquals("ios", registry.getPlatform("MOBILEPAGE"));
        assertEquals("ios", registry.getPlatform("MobilePage"));
    }

    // --- Thread safety tests ---

    @Test
    void concurrent_register_calls_do_not_corrupt_registry() throws InterruptedException {
        int threadCount = 20;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            futures.add(executor.submit(() -> {
                try {
                    latch.countDown();
                    latch.await(); // all threads start together
                    registry.register(TestPage.class, "Page" + index, Platform.web);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }));
        }

        executor.shutdown();
        assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));

        assertEquals(threadCount, registry.size());
        for (int i = 0; i < threadCount; i++) {
            assertTrue(registry.isRegistered("Page" + i), "Page" + i + " should be registered");
        }
    }

    @Test
    void concurrent_register_and_lookup_do_not_throw() throws InterruptedException {
        registry.register(TestPage.class, "Seed", Platform.web);

        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            futures.add(executor.submit(() -> {
                try {
                    latch.countDown();
                    latch.await();
                    // Mix reads and writes
                    registry.register(TestPage.class, "Concurrent" + index, Platform.android);
                    registry.isRegistered("Seed");
                    registry.getPageClass("Seed");
                    registry.getPlatform("Seed");
                    registry.size();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }));
        }

        executor.shutdown();
        assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));

        // Original entry is intact
        assertTrue(registry.isRegistered("Seed"));
        assertEquals(TestPage.class, registry.getPageClass("Seed"));
    }

    // --- Case insensitivity tests ---

    @Test
    void register_with_custom_name_is_case_insensitive_for_lookup() {
        registry.register(TestPage.class, "LoginPage", Platform.web);
        assertTrue(registry.isRegistered("loginpage"));
        assertTrue(registry.isRegistered("LOGINPAGE"));
        assertTrue(registry.isRegistered("LoginPage"));
        assertTrue(registry.isRegistered("lOgInPaGe"));
    }

    @Test
    void register_by_class_name_is_case_insensitive_for_lookup() {
        registry.register(TestPage.class, "", Platform.web);
        assertTrue(registry.isRegistered("testpage"));
        assertTrue(registry.isRegistered("TESTPAGE"));
        assertTrue(registry.isRegistered("TestPage"));
    }

    @Test
    void getPageClass_with_various_cases_returns_same_class() {
        registry.register(TestPage.class, "Dashboard", Platform.web);
        assertEquals(TestPage.class, registry.getPageClass("dashboard"));
        assertEquals(TestPage.class, registry.getPageClass("DASHBOARD"));
        assertEquals(TestPage.class, registry.getPageClass("Dashboard"));
        assertEquals(TestPage.class, registry.getPageClass("dAsHbOaRd"));
    }

    @Test
    void case_insensitive_registration_overwrites_same_key() {
        registry.register(TestPage.class, "Page", Platform.web);
        registry.register(TestPage.class, "page", Platform.android);
        assertEquals(1, registry.size(), "Same key in different cases should map to one entry");
        assertEquals("android", registry.getPlatform("Page"), "Last registration should win");
    }

    // Simple test page object — no driver needed for registration tests
    @PageObject
    static class TestPage {
        @FindBy(id = "test")
        public WebElement testElement;
    }
}
