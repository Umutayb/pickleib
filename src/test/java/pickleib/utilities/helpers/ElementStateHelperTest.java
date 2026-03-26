package pickleib.utilities.helpers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.FluentWait;
import pickleib.enums.ElementState;
import pickleib.exceptions.PickleibException;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ElementStateHelperTest {

    @Mock RemoteWebDriver driver;
    @Mock FluentWait<RemoteWebDriver> wait;
    @Mock WebElement element;
    @Mock org.openqa.selenium.WebDriver.Options options;
    @Mock org.openqa.selenium.WebDriver.Timeouts timeouts;

    ElementStateHelper helper;

    @BeforeEach
    void setUp() {
        helper = new ElementStateHelper(driver, 2000, 15);
    }

    @Test
    void elementIs_returns_true_when_displayed() {
        lenient().when(driver.manage()).thenReturn(options);
        lenient().when(options.timeouts()).thenReturn(timeouts);
        when(element.isDisplayed()).thenReturn(true);
        assertTrue(helper.elementIs(element, ElementState.displayed));
    }

    @Test
    void elementIs_returns_true_when_enabled() {
        lenient().when(driver.manage()).thenReturn(options);
        lenient().when(options.timeouts()).thenReturn(timeouts);
        when(element.isEnabled()).thenReturn(true);
        assertTrue(helper.elementIs(element, ElementState.enabled));
    }

    @Test
    void elementIs_returns_false_on_timeout() {
        lenient().when(driver.manage()).thenReturn(options);
        lenient().when(options.timeouts()).thenReturn(timeouts);
        when(element.isDisplayed()).thenReturn(false);
        assertFalse(helper.elementIs(element, ElementState.displayed));
    }

    @Test
    void elementIs_absent_returns_true_on_stale_element() {
        lenient().when(driver.manage()).thenReturn(options);
        lenient().when(options.timeouts()).thenReturn(timeouts);
        when(element.isDisplayed()).thenThrow(new StaleElementReferenceException("gone"));
        assertTrue(helper.elementIs(element, ElementState.absent));
    }

    @Test
    void verifyElementState_throws_when_state_incorrect() {
        lenient().when(driver.manage()).thenReturn(options);
        lenient().when(options.timeouts()).thenReturn(timeouts);
        when(element.isDisplayed()).thenReturn(false);
        assertThrows(PickleibException.class, () ->
            helper.verifyElementState(element, ElementState.displayed)
        );
    }

    @Test
    void verifyElementText_throws_on_mismatch() {
        when(element.getText()).thenReturn("actual");
        assertThrows(PickleibException.class, () ->
            helper.verifyElementText(element, "expected")
        );
    }

    @Test
    void elementContainsAttribute_returns_true_when_match() {
        when(element.getAttribute("class")).thenReturn("active");
        assertTrue(helper.elementContainsAttribute(element, "class", "active"));
    }

    @Test
    void elementContainsAttribute_returns_false_on_mismatch() {
        when(element.getAttribute("class")).thenReturn("inactive");
        assertFalse(helper.elementContainsAttribute(element, "class", "active"));
    }

    @Test
    void elementAttributeContainsValue_returns_true_when_contains() {
        lenient().when(driver.manage()).thenReturn(options);
        lenient().when(options.timeouts()).thenReturn(timeouts);
        when(element.getAttribute("class")).thenReturn("btn active primary");
        assertTrue(helper.elementAttributeContainsValue(element, "class", "active"));
    }
}
