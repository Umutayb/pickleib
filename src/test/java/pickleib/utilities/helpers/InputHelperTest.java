package pickleib.utilities.helpers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.FluentWait;
import pickleib.utilities.interfaces.functions.ScrollFunction;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InputHelperTest {

    @Mock RemoteWebDriver driver;
    @Mock FluentWait<RemoteWebDriver> wait;
    @Mock ScrollFunction scroller;
    @Mock RemoteWebElement element;
    @Mock Capabilities capabilities;

    InputHelper inputHelper;

    @BeforeEach
    void setUp() {
        inputHelper = new InputHelper(driver, wait, scroller, 2000);
        // Make getElementDriverPlatform work: element.getWrappedDriver() -> driver -> capabilities -> platform
        lenient().when(element.getWrappedDriver()).thenReturn(driver);
        lenient().when(driver.getCapabilities()).thenReturn(capabilities);
        lenient().when(capabilities.getPlatformName()).thenReturn(Platform.ANY);
    }

    @Test
    void fillAndVerify_sends_text_to_element() {
        when(wait.until(any())).thenReturn(element);
        when(element.getAttribute(anyString())).thenReturn("hello");
        inputHelper.fillAndVerify(element, "hello", false, false, false);
        verify(element).sendKeys("hello");
    }

    @Test
    void fillAndVerify_clears_before_fill_when_clear_true() {
        when(wait.until(any())).thenReturn(element);
        when(element.getAttribute(anyString())).thenReturn(null).thenReturn("hello");
        inputHelper.fillAndVerify(element, "hello", false, true, false);
        verify(element, atLeastOnce()).sendKeys(anyString()); // clear sends backspaces, then fills
        verify(element).sendKeys("hello");
    }

    @Test
    void fillAndVerify_throws_on_verification_failure() {
        when(wait.until(any())).thenReturn(element);
        when(element.getAttribute(anyString())).thenReturn("wrong");
        assertThrows(RuntimeException.class, () ->
            inputHelper.fillAndVerify(element, "expected", false, false, true)
        );
    }

    @Test
    void fillAndVerify_does_not_throw_when_verify_false() {
        when(wait.until(any())).thenReturn(element);
        when(element.getAttribute(anyString())).thenReturn("wrong");
        assertDoesNotThrow(() ->
            inputHelper.fillAndVerify(element, "expected", false, false, false)
        );
    }

    @Test
    void clearInputField_sends_backspaces() {
        when(element.getAttribute(anyString())).thenReturn("abc");
        inputHelper.clearInputField(element);
        verify(element).sendKeys(anyString()); // sends backspace sequence
    }
}
