package pickleib.utilities.helpers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.FluentWait;
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
    void clickButtonIfPresent_does_not_throw_when_not_found() {
        when(wait.until(any())).thenThrow(new NoSuchElementException("not found"));
        assertDoesNotThrow(() -> clickHelper.clickButtonIfPresent(element, false));
    }

    @Test
    void clickButtonIfPresent_does_not_throw_on_stale() {
        when(wait.until(any())).thenThrow(new StaleElementReferenceException("stale"));
        assertDoesNotThrow(() -> clickHelper.clickButtonIfPresent(element, false));
    }

    @Test
    void clickIfPresent_does_not_throw_when_not_found() {
        when(wait.until(any())).thenThrow(new NoSuchElementException("not found"));
        assertDoesNotThrow(() -> clickHelper.clickIfPresent(element, false));
    }
}
