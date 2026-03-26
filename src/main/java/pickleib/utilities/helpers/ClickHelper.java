package pickleib.utilities.helpers;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import pickleib.exceptions.PickleibException;
import pickleib.utilities.RetryPolicy;
import pickleib.utilities.interfaces.functions.ScrollFunction;
import utils.Printer;

/**
 * Encapsulates all click-related interactions with WebElements.
 * Uses {@link RetryPolicy} for automatic retries on transient WebDriver failures.
 */
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

    /**
     * Clicks element if present. Only catches NoSuchElementException and StaleElementReferenceException
     * (directly or wrapped in PickleibException by RetryPolicy).
     * Other WebDriverExceptions (session-not-created, etc.) propagate.
     */
    public void clickButtonIfPresent(WebElement element, boolean scroll) {
        try {
            clickElement(element, scroll);
        } catch (NoSuchElementException | StaleElementReferenceException ignored) {
            log.warning("The element was not present!");
        } catch (PickleibException e) {
            if (isAbsenceException(e)) log.warning("The element was not present!");
            else throw e;
        }
    }

    public void clickIfPresent(WebElement element, boolean scroll) {
        try {
            clickElement(element, scroll);
        } catch (NoSuchElementException | StaleElementReferenceException exception) {
            log.warning(exception.getMessage());
        } catch (PickleibException e) {
            if (isAbsenceException(e)) log.warning(e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
            else throw e;
        }
    }

    private static boolean isAbsenceException(PickleibException e) {
        Throwable cause = e.getCause();
        return cause instanceof NoSuchElementException || cause instanceof StaleElementReferenceException;
    }

    public void clickIfPresent(WebElement element) {
        clickIfPresent(element, false);
    }

    /**
     * Waits for the element to be visible, then clicks at its center coordinates.
     * Preserves the visibility check from the original Utilities.clickTowards.
     */
    public void clickTowards(WebElement element) {
        wait.until(ExpectedConditions.visibilityOf(element));
        Actions builder = new Actions(driver);
        builder.moveToElement(element, 0, 0).click().build().perform();
    }

    public void clickAtAnOffset(WebElement element, int xOffset, int yOffset) {
        Actions builder = new Actions(driver);
        builder.moveToElement(element, xOffset, yOffset).click().build().perform();
    }
}
