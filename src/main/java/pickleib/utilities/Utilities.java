package pickleib.utilities;

import collections.Bundle;
import com.fasterxml.jackson.databind.ObjectMapper;
import context.ContextStore;
import io.appium.java_client.AppiumDriver;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import pickleib.driver.DriverFactory;
import pickleib.enums.ElementState;
import pickleib.exceptions.PickleibException;
import pickleib.utilities.interfaces.functions.ScrollFunction;
import pickleib.utilities.screenshot.ScreenCaptureUtility;
import utils.Printer;
import utils.StringUtilities;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

import static pickleib.driver.DriverFactory.DriverType.Mobile;
import static pickleib.driver.DriverFactory.DriverType.Web;
import static pickleib.enums.ElementState.absent;
import static pickleib.enums.ElementState.displayed;
import static pickleib.web.driver.WebDriverFactory.getDriverTimeout;
import static utils.StringUtilities.Color.*;
import static utils.StringUtilities.*;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public abstract class Utilities {

    static {
        PropertyLoader.load();
    }

    public ScreenCaptureUtility capture = new ScreenCaptureUtility();
    public ObjectMapper objectMapper = new ObjectMapper();
    public Printer log = new Printer(this.getClass());
    public RemoteWebDriver driver;
    public FluentWait<RemoteWebDriver> wait;
    public ScrollFunction scroller;

    public long elementTimeout = Long.parseLong(ContextStore.get("element-timeout", "15000"));

    public Utilities(RemoteWebDriver driver, FluentWait<RemoteWebDriver> wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public Utilities(RemoteWebDriver driver, ScrollFunction scroller) {
        this.driver = driver;
        this.scroller = scroller;
        if (driver != null)
            wait = new FluentWait<>(driver)
                    .withTimeout(Duration.ofSeconds(elementTimeout))
                    .pollingEvery(Duration.ofMillis(500))
                    .withMessage("Waiting for element visibility...")
                    .ignoring(WebDriverException.class);
    }

    /**
     * Highlights a given text with a specified color (resets to plain)
     *
     * @param color target color
     * @param text  target text
     */
    public String highlighted(StringUtilities.Color color, CharSequence text) {
        StringJoiner colorFormat = new StringJoiner("", color.getValue(), RESET.getValue());
        return String.valueOf(colorFormat.add(text));
    }

    /**
     * Acquires a specified attribute of a given element
     *
     * @param element   target element
     * @param attribute target attribute
     * @return returns the element attribute
     */
    public String getAttribute(WebElement element, String attribute) {
        return element.getAttribute(attribute);
    }

    /**
     * Clicks an element after waiting for its state to be enabled
     *
     * @param element target element
     */
    public void clickElement(WebElement element) {
        clickElement(element, false);
    }

    /**
     * Clicks on the specified WebElement.
     *
     * @param element The WebElement to click on.
     * @param scroll  If true, scrolls to the WebElement before clicking. If false, clicks directly without scrolling.
     * @throws TimeoutException if the element is not clickable within the specified timeout.
     */
    public void clickElement(WebElement element, boolean scroll) {
        wait.until(ExpectedConditions.elementToBeClickable(element));
        if (scroll) this.scroller.scroll(element).click();
        else element.click();
    }

    /**
     * If present, click element {element name} on the {page name}
     * It does not scroll by default.
     *
     * @param element target element
     */
    public void clickButtonIfPresent(WebElement element) {
        clickButtonIfPresent(element, false);
    }

    /**
     * If present, clicks the specified {@code element} on the {page name}.
     * It scrolls according to the provided {@code ScrollFunction} by default.
     *
     * <p>
     * This method checks if the given {@code element} is present and displayed.
     * If the element is present and displayed, it is clicked using the provided {@code scroller} for scrolling.
     * If the element is not present, a {@code WebDriverException} is caught, and a warning message is logged.
     * </p>
     *
     * @param element The target {@code WebElement} to be clicked if present.
     * @param scroll  scrolls if true
     */
    public void clickButtonIfPresent(WebElement element, boolean scroll) {
        try {
            clickElement(element, scroll);
        } catch (WebDriverException ignored) {
            log.warning("The element was not present!");
        }
    }

    /**
     * Press {target key} key on {element name} element of the {}
     *
     * @param keys        target keys
     * @param elementName target element name
     * @param pageName    specified page instance name
     */
    public void pressKeysOnElement(WebElement element, String elementName, String pageName, Keys... keys) {
        String combination = Keys.chord(keys);
        log.info("Pressing " + markup(BLUE, combination) + " keys on " + markup(BLUE, elementName) + " element.");
        element.sendKeys(combination);
    }

    /**
     * Click coordinates specified by the given offsets from the center of a given element
     *
     * @param element target element
     */
    public void clickTowards(WebElement element) {
        elementIs(element, ElementState.displayed);
        Actions builder = new org.openqa.selenium.interactions.Actions(driver);
        builder
                .moveToElement(element, 0, 0)
                .click()
                .build()
                .perform();
    }

    /**
     * Clicks an element if its present (in enabled state)
     *
     * @param element target element
     * @param scroll  If true, scrolls to the WebElement before clicking. If false, clicks directly without scrolling.
     */
    public void clickIfPresent(WebElement element, boolean scroll) {
        try {
            clickElement(element, scroll);
        } catch (WebDriverException exception) {
            log.warning(exception.getMessage());
        }
    }

    /**
     * Clicks an element if its present (in enabled state)
     * Does not scroll by default.
     *
     * @param element target element
     */
    public void clickIfPresent(WebElement element) {
        clickIfPresent(element, false);
    }

    /**
     * Clears and fills a given input
     *
     * @param inputElement target input element
     * @param inputText    input text
     */
    public void fillInput(WebElement inputElement, String inputText) {
        // This method clears the input field before filling it
        clearFillInput(inputElement, inputText, false);
    }

    /**
     * Clears and fills a given input
     *
     * @param inputElement target input element
     * @param inputText    input text
     */
    public void fillAndVerifyInput(WebElement inputElement, String inputText) {
        // This method clears the input field before filling it
        clearFillInput(inputElement, inputText, true);
    }

    /**
     * Clears and fills a given input
     *
     * @param inputElement target input element
     * @param inputText    input text
     */
    public void fillAndVerifyInput(WebElement inputElement, String inputText, boolean scroll) {
        // This method clears the input field before filling it
        clearFillInput(inputElement, inputText, scroll, true);
    }

    /**
     * Clears and fills a given input
     *
     * @param inputElement target input element
     * @param inputText    input text
     * @param verify       verifies the input text value equals to an expected text if true
     */
    public void clearFillInput(WebElement inputElement, String inputText, boolean verify) {
        fillInputElement(inputElement, inputText, false, true, verify);
    }

    /**
     * Clears and fills a given input
     *
     * @param inputElement target input element
     * @param inputText    input text
     * @param scroll       If true, scrolls to the WebElement before clicking. If false, clicks directly without scrolling.
     * @param verify       verifies the input text value equals to an expected text if true
     */
    public void clearFillInput(WebElement inputElement, String inputText, boolean scroll, boolean verify) {
        fillInputElement(inputElement, inputText, scroll, true, verify);
    }

    /**
     * Fills the specified input WebElement with the given text.
     *
     * @param inputElement The WebElement representing the input field.
     * @param inputText The text to be entered into the input field.
     * @param scroll If true, scrolls to the inputElement before filling. If false, does not scroll.
     * @param clear If true, clears the input field before entering text. If false, does not clear.
     * @param verify If true, verifies that the entered text matches the value attribute of the inputElement. If false, skips verification.
     *
     * @throws TimeoutException if the inputElement is not visible within the specified timeout.
     * @throws AssertionError if verification fails (inputText does not match the value attribute of inputElement).
     */
    public void fillInputElement(WebElement inputElement, String inputText, boolean scroll, boolean clear, boolean verify) {
        wait.until(ExpectedConditions.visibilityOf(inputElement));
        inputText = contextCheck(inputText);
        if (scroll) scroller.scroll(inputElement);
        if (clear) clearInputField(inputElement);
        inputElement.sendKeys(inputText);
        assert !verify || inputText.equals(inputElement.getAttribute("value"));
    }

    /**
     * Clears and fills a given input
     *
     * @param inputElement target input element
     * @param inputText    input text
     * @param verify       verifies the input text value equals to an expected text if true
     * @param clear If true, clears the input field before entering text. If false, does not clear.
     */
    public void fillInputElement(WebElement inputElement, String inputText, boolean clear, boolean verify) {
       fillInputElement(inputElement, inputText, false, clear, verify);
    }

    /**
     * Verifies a given element is in expected state
     *
     * @param element target element
     * @param state   expected state
     * @return returns the element if its in expected state
     */
    public WebElement verifyElementState(WebElement element, ElementState state) {
        if (!elementIs(element, state)) throw new PickleibException("Element is not in " + state.name() + " state!");
        log.success("Element state is verified to be: " + state.name());
        return element;
    }

    /**
     * Waits until a given element is in expected state
     *
     * @param element target element
     * @param state   expected state
     * @return returns true if an element is in the expected state
     */ //TODO: elementIs should use iterativeConditionalInvocation() instead of iterating in itself. (same for other similar methods).
    public Boolean elementIs(WebElement element, @NotNull ElementState state) {
        long initialTime = System.currentTimeMillis();
        String caughtException = null;
        boolean timeout;
        boolean condition = false;
        boolean negativeCheck = false;
        int counter = 0;
        do { //TODO: Replace this with iterativeConditionalInvocation
            if (condition || (counter > 1 && negativeCheck)) return true;
            try {
                driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));
                switch (state) {
                    case enabled -> {
                        negativeCheck = false;
                        condition = element.isEnabled();
                    }
                    case displayed -> {
                        negativeCheck = false;
                        condition = element.isDisplayed();
                    }
                    case selected -> {
                        negativeCheck = false;
                        condition = element.isSelected();
                    }
                    case disabled -> {
                        negativeCheck = true;
                        condition = !element.isEnabled();
                    }
                    case unselected -> {
                        negativeCheck = true;
                        condition = !element.isSelected();
                    }
                    case absent -> {
                        negativeCheck = true;
                        condition = !element.isDisplayed();
                    }
                    default -> throw new EnumConstantNotPresentException(ElementState.class, state.name());
                }
            } catch (WebDriverException webDriverException) {
                if (counter == 0) {
                    log.warning("Iterating... (" + webDriverException.getClass().getName() + ")");
                    caughtException = webDriverException.getClass().getName();
                } else if (!webDriverException.getClass().getName().equals(caughtException)) {
                    log.warning("Iterating... (" + webDriverException.getClass().getName() + ")");
                    caughtException = webDriverException.getClass().getName();
                } else if (state.equals(absent) && webDriverException.getClass().getName().equals("StaleElementReferenceException"))
                    return true;
                counter++;
            } finally {
                driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(getDriverTimeout()));
            }
        }
        while (!(System.currentTimeMillis() - initialTime > elementTimeout));
        if (counter > 0) log.warning("Iterated " + counter + " time(s)!");
        return false;
    }

    /**
     * Clicks an element acquired by text without scrolling.
     *
     * <p>
     * This method locates an element by its text using {@code getElementByText}.
     * If the element is found, it is clicked without any scrolling.
     * </p>
     *
     * @param buttonText The text of the target element to be clicked.
     */
    public void clickButtonWithText(String buttonText) {
        clickButtonWithText(buttonText, false);
    }

    /**
     * Clicks an element acquired by text with optional scrolling.
     *
     * <p>
     * This method locates an element by its text using {@code getElementByText}.
     * If the element is found, it is clicked with optional scrolling using the provided {@code scroller}.
     * </p>
     *
     * @param buttonText The text of the target element to be clicked.
     * @param scroll     If true, scrolls to the WebElement before clicking. If false, clicks directly without scrolling.
     */
    public void clickButtonWithText(String buttonText, boolean scroll) {
        clickElement(getElementByText(buttonText), scroll);
    }

    /**
     * Clears an input element
     *
     * @param element target element
     */
    public WebElement clearInputField(@NotNull WebElement element) {
        int textLength = element.getAttribute("value").length();
        for (int i = 0; i < textLength; i++) {
            element.sendKeys(Keys.BACK_SPACE);
        }
        return element;
    }

    /**
     * Acquires an element by its text
     *
     * @param elementText target element text
     */
    public WebElement getElementByText(String elementText) {
        try {
            return driver.findElement(By.xpath("//*[text()='" + elementText + "']"));
        } catch (NoSuchElementException exception) {
            throw new NoSuchElementException(GRAY + exception.getMessage() + RESET);
        }
    }

    /**
     * Acquires an element that contains a certain text
     *
     * @param elementText target element text
     */
    public WebElement getElementContainingText(String elementText) {
        try {
            return driver.findElement(By.xpath("//*[contains(text(), '" + elementText + "')]"));
        } catch (NoSuchElementException exception) {
            throw new NoSuchElementException(GRAY + exception.getMessage() + RESET);
        }
    }

    /**
     * Drags and drops a given element on top of another element
     *
     * @param element            element that drags
     * @param destinationElement target element
     */
    public void dragDropToAction(WebElement element, WebElement destinationElement) {
        Actions action = new Actions(driver);
        action.moveToElement(element)
                .clickAndHold(element)
                .moveToElement(destinationElement)
                .release()
                .build()
                .perform();
        waitFor(0.5);
    }

    /**
     * Drags a given element to coordinates specified by offsets from the center of the element
     *
     * @param element target element
     * @param xOffset x offset from the center of the element
     * @param yOffset y offset from the center of the element
     */
    //This method performs click, hold, dragAndDropBy action on at a certain offset
    public void dragDropByAction(WebElement element, int xOffset, int yOffset) {
        Actions action = new Actions(driver);
        action.moveToElement(element)
                .clickAndHold(element)
                .dragAndDropBy(element, xOffset, yOffset)
                .build()
                .perform();
        waitFor(0.5);
    }

    /**
     * Drags a given element to coordinates specified by offsets from the center of the element
     * Uses moveToElement()
     *
     * @param element target element
     * @param xOffset x offset from the center of the element
     * @param yOffset y offset from the center of the element
     */
    public void dragDropAction(WebElement element, int xOffset, int yOffset) {
        Actions action = new Actions(driver);
        action.moveToElement(element)
                .clickAndHold(element)
                .moveToElement(element, xOffset, yOffset)
                .release()
                .build()
                .perform();
        waitFor(0.5);
    }

    /**
     * Refreshes the current page
     */
    public void refreshThePage() {
        driver.navigate().refresh();
    }

    /**
     * Click coordinates specified by the given offsets from the center of a given element
     *
     * @param element target element
     * @param xOffset x offset from the center of the element
     * @param yOffset y offset from the center of the element
     */
    @SuppressWarnings("SameParameterValue")
    public void clickAtAnOffset(WebElement element, int xOffset, int yOffset) {
        Actions builder = new org.openqa.selenium.interactions.Actions(driver);
        builder
                .moveToElement(element, xOffset, yOffset)
                .click()
                .build()
                .perform();
    }

    /**
     * Uploads a given file
     *
     * @param fileUploadInput upload element
     * @param directory       absolute file directory (excluding the file name)
     * @param fileName        file name (including a file extension)
     */
    public void uploadFile(@NotNull WebElement fileUploadInput, String directory, String fileName) {
        fileUploadInput.sendKeys(directory + "/" + fileName);
    }

    /**
     * Combines the given keys
     *
     * @param keys key inputs
     */
    public String combineKeys(Keys... keys) {
        return Keys.chord(keys);
    }

    /**
     * Waits for a certain while
     *
     * @param seconds duration as a double
     */
    //This method makes the thread wait for a certain while
    public static void waitFor(double seconds) {
        Printer log = new Printer(Utilities.class);
        if (seconds > 1) log.info("Waiting for " + markup(BLUE, String.valueOf(seconds)) + " seconds");
        try {
            Thread.sleep((long) (seconds * 1000L));
        } catch (InterruptedException exception) {
            throw new PickleibException(StringUtilities.highlighted(GRAY, exception.getLocalizedMessage()));
        }
    }

    /**
     * Gets the parent class from a child element using a selector class
     *
     * @param childElement        element that generates the parent class
     * @param current             empty string (at the beginning)
     * @param parentSelectorClass selector class for selecting the parent elements
     * @return returns the targeted parent element
     */
    public WebElement getParentByClass(WebElement childElement, String current, String parentSelectorClass) {

        if (current == null) {
            current = "";
        }

        String childTag = childElement.getTagName();

        if (childElement.getAttribute("class").contains(parentSelectorClass)) return childElement;

        WebElement parentElement = childElement.findElement(By.xpath(".."));
        List<WebElement> childrenElements = parentElement.findElements(By.xpath("*"));

        int count = 0;
        for (WebElement childrenElement : childrenElements) {
            String childrenElementTag = childrenElement.getTagName();
            if (childTag.equals(childrenElementTag)) count++;
            if (childElement.equals(childrenElement)) {
                return getParentByClass(parentElement, "/" + childTag + "[" + count + "]" + current, parentSelectorClass);
            }
        }
        return null;
    }

    /**
     * Generate a xPath for a given element
     *
     * @param childElement web element gets generated a xPath from
     * @param current      empty string (at the beginning)
     * @return returns generated xPath
     */
    public String generateXPath(@NotNull WebElement childElement, String current) {
        String childTag = childElement.getTagName();
        if (childTag.equals("html")) {
            return "/html[1]" + current;
        }
        WebElement parentElement = childElement.findElement(By.xpath(".."));
        List<WebElement> childrenElements = parentElement.findElements(By.xpath("*"));
        int count = 0;
        for (WebElement childrenElement : childrenElements) {
            String childrenElementTag = childrenElement.getTagName();
            if (childTag.equals(childrenElementTag)) count++;
            if (childElement.equals(childrenElement)) {
                return generateXPath(parentElement, "/" + childTag + "[" + count + "]" + current);
            }
        }
        return null;
    }

    /**
     * Gets the name of the method that called the API.
     *
     * @return the name of the method that called the API
     */
    private static String getCallingClassName() {
        StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
        for (int i = 1; i < stElements.length; i++) {
            StackTraceElement ste = stElements[i];
            if (!ste.getClassName().equals(Utilities.class.getName()) && ste.getClassName().indexOf("java.lang.Thread") != 0) {
                return ste.getClassName();
            }
        }
        return null;
    }

    /**
     * Acquire attribute {attribute name} from element {element name} on the {page name}
     * (Use 'innerHTML' attributeName to acquire text on an element)
     *
     * @param element       target element
     * @param attributeName acquired attribute name
     * @param elementName   target element name
     * @param pageName      specified page instance name
     */
    public void updateContextByElementAttribute(
            WebElement element,
            String attributeName,
            String elementName,
            String pageName) {
        log.info("Acquiring " +
                highlighted(BLUE, attributeName) +
                highlighted(GRAY, " attribute of ") +
                highlighted(BLUE, elementName) +
                highlighted(GRAY, " on the ") +
                highlighted(BLUE, pageName)
        );
        String attribute = element.getAttribute(attributeName);
        log.info("Attribute -> " + highlighted(BLUE, attributeName) + highlighted(GRAY, " : ") + highlighted(BLUE, attribute));
        ContextStore.put(elementName + "-" + attributeName, attribute);
        log.info("Element attribute saved to the ContextStore as -> '" +
                highlighted(BLUE, elementName + "-" + attributeName) +
                highlighted(GRAY, "' : '") +
                highlighted(BLUE, attribute) +
                highlighted(GRAY, "'")
        );
    }

    /**
     * Verify the text of {element name} on the {page name} to be: {expected text}
     *
     * @param element      target element
     * @param expectedText expected text
     */
    public void verifyElementText(WebElement element, String expectedText) {
        expectedText = contextCheck(expectedText);
        if (!expectedText.equals(element.getText()))
            throw new PickleibException("Element text is not \"" + highlighted(BLUE, expectedText) + "\"!");
        log.success("Text of the element \"" + expectedText + "\" was verified!");
    }

    /**
     * Verify the text of {element name} on the {page name} to be: {expected text}
     *
     * @param element      target element
     * @param expectedText expected text
     */
    public void verifyElementContainsText(WebElement element, String expectedText) {
        expectedText = contextCheck(expectedText);
        elementIs(element, displayed);
        if (!element.getText().contains(expectedText))
            throw new PickleibException("Element text does not contain \"" + highlighted(BLUE, expectedText) + "\"!");
        log.success("The element text does contain \"" + expectedText + "\" text!");
    }

    /**
     * Verify the text of an element from the list on the {page name}
     *
     * @param pageName specified page instance name
     */
    public void verifyListedElementText(
            List<Bundle<WebElement, String, String>> bundles,
            String pageName) {
        for (Bundle<WebElement, String, String> bundle : bundles) {
            String elementName = bundle.beta();
            String expectedText = bundle.theta();
            log.info("Performing text verification for " +
                    highlighted(BLUE, elementName) +
                    highlighted(GRAY, " on the ") +
                    highlighted(BLUE, pageName) +
                    highlighted(GRAY, " with the text: ") +
                    highlighted(BLUE, expectedText)
            );
            if (!expectedText.equals(bundle.alpha().getText()))
                throw new PickleibException("The " + bundle.alpha().getText() + " does not contain text '");
            log.success("Text of the element" + bundle.alpha().getText() + " was verified!");
        }
    }

    /**
     * Fill form input on the {page name}
     *
     * @param bundles  list of bundles where input element, input name and input texts are stored
     * @param pageName specified page instance name
     */
    public void fillInputForm(List<Bundle<WebElement, String, String>> bundles, String pageName) {
        String inputName;
        String input;
        for (Bundle<WebElement, String, String> bundle : bundles) {
            log.info("Filling " +
                    highlighted(BLUE, bundle.theta()) +
                    highlighted(GRAY, " on the ") +
                    highlighted(BLUE, pageName) +
                    highlighted(GRAY, " with the text: ") +
                    highlighted(BLUE, bundle.beta())
            );
            pageName = firstLetterDeCapped(pageName);
            clearFillInput(bundle.alpha(), //Input Element
                    bundle.beta(), //Input Text
                    true
            );
        }
    }

    /**
     * Verify that element {element name} on the {page name} has {attribute value} value for its {attribute name} attribute
     *
     * @param element        target element
     * @param attributeValue expected attribute value
     * @param attributeName  target attribute name
     */
    public boolean elementContainsAttribute(
            WebElement element,
            String attributeName,
            String attributeValue) {

        long initialTime = System.currentTimeMillis();
        String caughtException = null;
        int counter = 0;
        attributeValue = contextCheck(attributeValue);
        do {
            try {
                if (Objects.equals(element.getAttribute(attributeName), attributeValue))
                    return element.getAttribute(attributeName).contains(attributeValue);
            } catch (WebDriverException webDriverException) {
                if (counter == 0) {
                    log.warning("Iterating... (" + webDriverException.getClass().getName() + ")");
                    caughtException = webDriverException.getClass().getName();
                } else if (!webDriverException.getClass().getName().equals(caughtException)) {
                    log.warning("Iterating... (" + webDriverException.getClass().getName() + ")");
                    caughtException = webDriverException.getClass().getName();
                }
                waitFor(0.5);
                counter++;
            }
        }
        while (!(System.currentTimeMillis() - initialTime > elementTimeout));
        if (counter > 0) log.warning("Iterated " + counter + " time(s)!");
        log.warning("Element does not contain " +
                highlighted(BLUE, attributeName) +
                highlighted(GRAY, " -> ") +
                highlighted(BLUE, attributeValue) +
                highlighted(GRAY, " attribute pair.")
        );
        log.warning(caughtException);
        return false;
    }

    /**
     * Verify that an attribute {attribute name} of element {element name} contains a specific {value}.
     *
     * @param elementName   the name of the element to be verified
     * @param attributeName the name of the attribute to be verified
     * @param value         the expected part of value of the attribute
     */
    public boolean elementAttributeContainsValue(
            WebElement elementName,
            String attributeName,
            String value) {

        long initialTime = System.currentTimeMillis();
        String caughtException = null;
        int counter = 0;
        value = contextCheck(value);
        //TODO replace do-while with iterativeConditionalInvocation() method
        do {
            try {
                driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));
                return elementName.getAttribute(attributeName).contains(value);
            } catch (WebDriverException webDriverException) {
                if (counter == 0) {
                    log.warning("Iterating... (" + webDriverException.getClass().getName() + ")");
                    caughtException = webDriverException.getClass().getName();
                } else if (!webDriverException.getClass().getName().equals(caughtException)) {
                    log.warning("Iterating... (" + webDriverException.getClass().getName() + ")");
                    caughtException = webDriverException.getClass().getName();
                }
                waitFor(0.5);
                counter++;
            } finally {
                driver.manage().timeouts().implicitlyWait(Duration.ofMillis(elementTimeout));
            }
        }
        while (!(System.currentTimeMillis() - initialTime > elementTimeout));
        if (counter > 0) log.warning("Iterated " + counter + " time(s)!");
        log.warning("Element attribute does not contain " +
                highlighted(BLUE, attributeName) +
                highlighted(GRAY, " -> ") +
                highlighted(BLUE, value) +
                highlighted(GRAY, " value.")
        );
        log.warning(caughtException);
        return false;
    }

    /**
     * Determines the type of driver associated with the provided WebElement.
     *
     * @param element The WebElement whose driver type needs to be determined.
     * @return The DriverType associated with the WebElement:
     * - If the WebElement is associated with an AppiumDriver, returns DriverType.Mobile.
     * - If the WebElement is associated with a standard WebDriver, returns DriverType.Web.
     */
    public static DriverFactory.DriverType getElementDriverType(WebElement element) {
        if (isAppiumElement(element))
            return Mobile;
        else
            return Web;
    }

    /**
     * Checks if the provided WebElement is associated with an AppiumDriver.
     *
     * @param element The WebElement to be checked.
     * @return true if the WebElement is associated with an AppiumDriver, false otherwise.
     * If a ClassCastException occurs during the check, it returns false.
     */
    public static boolean isAppiumElement(WebElement element) {
        try {
            return ((RemoteWebElement) element).getWrappedDriver().getClass().isAssignableFrom(AppiumDriver.class);
        } catch (ClassCastException exception) {
            return false;
        }
    }
}
