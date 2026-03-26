package pickleib.utilities.helpers;

import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import pickleib.exceptions.PickleibException;
import pickleib.utilities.element.ElementBundle;
import pickleib.utilities.interfaces.functions.ScrollFunction;
import utils.Printer;
import utils.StringUtilities;

import java.util.List;
import java.util.StringJoiner;

import static pickleib.utilities.DriverInspector.*;
import static utils.StringUtilities.contextCheck;
import static utils.StringUtilities.firstLetterDeCapped;

/**
 * Encapsulates all input-filling interactions with WebElements.
 * Extracted from {@link pickleib.utilities.Utilities} to keep the base class focused.
 */
public class InputHelper {

    private final RemoteWebDriver driver;
    private final FluentWait<RemoteWebDriver> wait;
    private final ScrollFunction scroller;
    private final long elementTimeout;
    private final Printer log = new Printer(InputHelper.class);

    public InputHelper(RemoteWebDriver driver, FluentWait<RemoteWebDriver> wait, ScrollFunction scroller, long elementTimeout) {
        this.driver = driver;
        this.wait = wait;
        this.scroller = scroller;
        this.elementTimeout = elementTimeout;
    }

    /**
     * Fills the specified input WebElement with the given text.
     *
     * @param element   The WebElement representing the input field.
     * @param inputText The text to be entered into the input field.
     * @param scroll    If true, scrolls to the element before filling. If false, does not scroll.
     * @param clear     If true, clears the input field before entering text. If false, does not clear.
     * @param verify    If true, verifies that the entered text matches the value attribute of the element.
     *
     * @throws PickleibException if verification fails (inputText does not match the value attribute of element).
     */
    public void fillAndVerify(WebElement element, String inputText, boolean scroll, boolean clear, boolean verify) {
        wait.until(ExpectedConditions.visibilityOf(element));
        inputText = contextCheck(inputText);
        if (scroll) scroller.scroll(element);
        if (clear) clearInputField(element);
        element.sendKeys(inputText);
        String inputValue = element.getAttribute(getInputContentAttributeNameFor(getElementDriverPlatform(element)));
        if (verify && !inputText.equals(inputValue))
            throw new PickleibException("Input verification failed: expected '" + inputText + "' but got '" + inputValue + "'");
    }

    /**
     * Clears an input element by sending backspace characters for each existing character.
     *
     * @param element target element
     * @return the cleared element
     */
    public WebElement clearInputField(@NotNull WebElement element) {
        StringJoiner deletion = new StringJoiner(Keys.BACK_SPACE);
        String inputValue = element.getAttribute(getInputContentAttributeNameFor(getElementDriverPlatform(element)));
        if (inputValue != null)
            for (int i = 0; i <= inputValue.length(); i++)
                deletion.add("");
        element.sendKeys(deletion.toString());
        return element;
    }

    /**
     * Clears and fills a given input (no scroll, no verify).
     *
     * @param inputElement target input element
     * @param inputText    input text
     */
    public void fillInput(WebElement inputElement, String inputText) {
        clearFillInput(inputElement, inputText, false);
    }

    /**
     * Clears and fills a given input, then verifies (no scroll).
     *
     * @param inputElement target input element
     * @param inputText    input text
     */
    public void fillAndVerifyInput(WebElement inputElement, String inputText) {
        clearFillInput(inputElement, inputText, true);
    }

    /**
     * Clears and fills a given input, then verifies (with optional scroll).
     *
     * @param inputElement target input element
     * @param inputText    input text
     * @param scroll       If true, scrolls to the element before filling.
     */
    public void fillAndVerifyInput(WebElement inputElement, String inputText, boolean scroll) {
        clearFillInput(inputElement, inputText, scroll);
    }

    /**
     * Clears and fills a given input (no scroll, no verify).
     *
     * @param inputElement target input element
     * @param inputText    input text
     */
    public void clearFillInput(WebElement inputElement, String inputText) {
        fillAndVerify(inputElement, inputText, false, true, false);
    }

    /**
     * Clears and fills a given input with optional scroll (no verify).
     *
     * @param inputElement target input element
     * @param inputText    input text
     * @param scroll       If true, scrolls to the element before filling.
     */
    public void clearFillInput(WebElement inputElement, String inputText, boolean scroll) {
        fillAndVerify(inputElement, inputText, scroll, true, false);
    }

    /**
     * Clears and fills a given input with optional scroll and verify.
     *
     * @param inputElement target input element
     * @param inputText    input text
     * @param scroll       If true, scrolls to the element before filling.
     * @param verify       verifies the input text value equals expected text if true
     */
    public void clearFillInput(WebElement inputElement, String inputText, boolean scroll, boolean verify) {
        fillAndVerify(inputElement, inputText, scroll, true, verify);
    }

    /**
     * Fills an input element with optional clear (no scroll, no verify).
     *
     * @param inputElement target input element
     * @param inputText    input text
     * @param clear        If true, clears the input field before entering text.
     */
    public void fillInputElement(WebElement inputElement, String inputText, boolean clear) {
        fillAndVerify(inputElement, inputText, false, clear, false);
    }

    /**
     * Fills an input element with optional scroll, clear, and verify.
     *
     * @param inputElement target input element
     * @param inputText    input text
     * @param scroll       If true, scrolls to the element before filling.
     * @param clear        If true, clears the input field before entering text.
     * @param verify       verifies the input text value equals expected text if true
     */
    public void fillInputElement(WebElement inputElement, String inputText, boolean scroll, boolean clear, boolean verify) {
        fillAndVerify(inputElement, inputText, scroll, clear, verify);
    }

    /**
     * Fills an input element with optional clear and verify (no scroll).
     *
     * @param inputElement target input element
     * @param inputText    input text
     * @param clear        If true, clears the input field before entering text.
     * @param verify       verifies the input text value equals expected text if true
     */
    public void fillInputElement(WebElement inputElement, String inputText, boolean clear, boolean verify) {
        fillAndVerify(inputElement, inputText, false, clear, verify);
    }

    /**
     * Fill form input on the given page.
     *
     * @param bundles  list of bundles where input element, input name and input texts are stored
     * @param pageName specified page instance name
     */
    public void fillInputForm(List<ElementBundle<String>> bundles, String pageName) {
        for (ElementBundle<String> bundle : bundles) {
            String inputText = contextCheck(bundle.data());
            log.info("Filling " +
                    StringUtilities.highlighted(utils.StringUtilities.Color.BLUE, bundle.elementName()) +
                    StringUtilities.highlighted(utils.StringUtilities.Color.GRAY, " on the ") +
                    StringUtilities.highlighted(utils.StringUtilities.Color.BLUE, pageName) +
                    StringUtilities.highlighted(utils.StringUtilities.Color.GRAY, " with the text: ") +
                    StringUtilities.highlighted(utils.StringUtilities.Color.BLUE, inputText)
            );
            firstLetterDeCapped(pageName);
            clearFillInput(
                    bundle.element(),
                    inputText,
                    !isPlatformElement(bundle.element())
            );
        }
    }
}
