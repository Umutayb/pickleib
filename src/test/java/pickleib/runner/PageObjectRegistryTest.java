package pickleib.runner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import pickleib.annotations.PageObject;
import pickleib.enums.Platform;

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

    // Simple test page object — no driver needed for registration tests
    @PageObject
    static class TestPage {
        @FindBy(id = "test")
        public WebElement testElement;
    }
}
