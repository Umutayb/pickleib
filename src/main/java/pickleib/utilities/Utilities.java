package pickleib.utilities;

import com.fasterxml.jackson.databind.ObjectMapper;
import context.ContextStore;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import pickleib.enums.ElementState;
import pickleib.exceptions.PickleibException;
import pickleib.utilities.element.ElementBundle;
import pickleib.utilities.interfaces.functions.ScrollFunction;
import utils.Printer;
import utils.StringUtilities;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

import static pickleib.enums.ElementState.absent;
import static pickleib.enums.ElementState.displayed;
import static pickleib.utilities.DriverInspector.*;
import static utils.StringUtilities.Color.*;
import static utils.StringUtilities.*;

/**
 * Abstract base class containing the core logic for WebDriver interactions.
 * <p>
 * This class provides robust wrappers around standard Selenium/Appium actions. It includes built-in
 * mechanisms for:
 * <ul>
 * <li><b>Resilience:</b> Automatic retries for {@link WebDriverException} (e.g., StaleElementReference).</li>
 * <li><b>Synchronization:</b> Intelligent waiting using {@link FluentWait} and implicit wait toggling.</li>
 * <li><b>Verification:</b> Assertions for element text, attributes, and states.</li>
 * </ul>
 *
 * @author  Umut Ay Bora
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public abstract class Utilities {

    public ObjectMapper objectMapper = new ObjectMapper();
    public Printer log = new Printer(this.getClass());
    public RemoteWebDriver driver;
    public FluentWait<RemoteWebDriver> wait;
    public ScrollFunction scroller;

    public long elementTimeout = ContextStore.getInt("element-timeout", 15000);
    public long driverTimeout = Long.parseLong(ContextStore.get("driver-timeout", "15000"))/1000;
    public Utilities(RemoteWebDriver driver, FluentWait<RemoteWebDriver> wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public Utilities(RemoteWebDriver driver, ScrollFunction scroller) {
        this.driver = driver;
        this.scroller = scroller;
        if (driver != null)
            wait = new FluentWait<>(driver)
                    .withTimeout(Duration.ofMillis(elementTimeout))
                    .pollingEvery(Duration.ofMillis(500))
                    .withMessage("Waiting for element visibility...")
                    .ignoring(WebDriverException.class);
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
        WebDriverException caughtException = null;
        int counter = 0;
        long initialTime = System.currentTimeMillis();
        do {
            try {
                wait.until(ExpectedConditions.elementToBeClickable(element));
                if (scroll) this.scroller.scroll(element).click();
                else element.click();
                return;
            }
            catch (WebDriverException webDriverException) {
                if (counter == 0) {
                    log.warning("Iterating... (" + webDriverException.getClass().getName() + ")");
                    caughtException = webDriverException;
                } else if (!webDriverException.getClass().getName().equals(caughtException.getClass().getName())) {
                    log.warning("Iterating... (" + webDriverException.getClass().getName() + ")");
                    caughtException = webDriverException;
                }
                counter++;
            }
        }
        while (System.currentTimeMillis() - initialTime < elementTimeout);
        if (counter > 0) log.warning("Iterated " + counter + " time(s)!");
        log.warning(caughtException.getMessage());
        throw new PickleibException(caughtException);
    }

    public boolean isElementInViewPort(WebElement element) {
        int windowHeight = driver.manage().window().getSize().getHeight();
        int windowWidth = driver.manage().window().getSize().getWidth();

        org.openqa.selenium.Point elementLocation = element.getRect().getPoint();
        int elementHeight = element.getRect().getHeight();
        int elementWidth = element.getRect().getWidth();

        int elementBottomY = elementLocation.getY() + elementHeight;
        int elementRightX = elementLocation.getX() + elementWidth;

        return (elementLocation.getY() >= 0 && elementLocation.getY() < windowHeight
                && elementLocation.getX() >= 0 && elementLocation.getX() < windowWidth
                && elementBottomY >= 0 && elementBottomY < windowHeight
                && elementRightX >= 0 && elementRightX < windowWidth);
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
        clearFillInput(inputElement, inputText, scroll);
    }

    /**
     * Clears and fills a given input
     *
     * @param inputElement target input element
     * @param inputText    input text
     */
    public void clearFillInput(WebElement inputElement, String inputText) {
        fillAndVerify(inputElement, inputText, false, true, false);
    }

    /**
     * Clears and fills a given input
     *
     * @param inputElement target input element
     * @param inputText    input text
     * @param scroll       If true, scrolls to the WebElement before clicking. If false, clicks directly without scrolling.
     */
    public void clearFillInput(WebElement inputElement, String inputText, boolean scroll) {
        fillAndVerify(inputElement, inputText, scroll, true, false);
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
        fillAndVerify(inputElement, inputText, scroll, true, verify);
    }

    /**
     * Clears and fills a given input
     *
     * @param inputElement target input element
     * @param inputText    input text
     * @param clear        If true, clears the input field before entering text. If false, does not clear.
     */
    public void fillInputElement(WebElement inputElement, String inputText, boolean clear) {
        fillAndVerify(inputElement, inputText, false, clear, false);
    }

    /**
     * Clears and fills a given input
     *
     * @param inputElement target input element
     * @param inputText    input text
     * @param clear        If true, clears the input field before entering text. If false, does not clear.
     * @param verify       verifies the input text value equals to an expected text if true
     */
    public void fillInputElement(WebElement inputElement, String inputText, boolean scroll, boolean clear, boolean verify) {
        fillAndVerify(inputElement, inputText, scroll, clear, verify);
    }

    /**
     * Clears and fills a given input
     *
     * @param inputElement target input element
     * @param inputText    input text
     * @param clear        If true, clears the input field before entering text. If false, does not clear.
     * @param verify       verifies the input text value equals to an expected text if true
     */
    public void fillInputElement(WebElement inputElement, String inputText, boolean clear, boolean verify) {
        fillAndVerify(inputElement, inputText, false, clear, verify);
    }

    /**
     * Fills the specified input WebElement with the given text.
     *
     * @param element The WebElement representing the input field.
     * @param inputText The text to be entered into the input field.
     * @param scroll If true, scrolls to the inputElement before filling. If false, does not scroll.
     * @param clear If true, clears the input field before entering text. If false, does not clear.
     * @param verify If true, verifies that the entered text matches the value attribute of the inputElement. If false, skips verification.
     *
     * @throws TimeoutException if the inputElement is not visible within the specified timeout.
     * @throws AssertionError if verification fails (inputText does not match the value attribute of inputElement).
     */
    public void fillAndVerify(WebElement element, String inputText, boolean scroll, boolean clear, boolean verify) {
        wait.until(ExpectedConditions.visibilityOf(element));
        inputText = contextCheck(inputText);
        if (scroll) scroller.scroll(element);
        if (clear) clearInputField(element);
        element.sendKeys(inputText);
        String inputValue =  element.getAttribute(getInputContentAttributeNameFor(getElementDriverPlatform(element)));
        assert !verify || inputText.equals(inputValue);
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
                driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(driverTimeout));
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
        clickElement(waitAndGetElementByText(buttonText), scroll);
    }

    /**
     * Clears an input element
     *
     * @param element target element
     */
    public WebElement clearInputField(@NotNull WebElement element) {
        StringJoiner deletion = new StringJoiner(Keys.BACK_SPACE);
        String inputValue =  element.getAttribute(getInputContentAttributeNameFor(getElementDriverPlatform(element)));
        if (inputValue != null)
            for (int i = 0; i <= inputValue.length(); i++)
                deletion.add("");
        element.sendKeys(deletion.toString());
        return element;
    }

    /**
     * Acquires an element by its text
     *
     * @param elementText target element text
     */
    public WebElement waitAndGetElementByText(String elementText) {
        try {
            String queryAttribute = getTextAttributeNameFor(getDriverPlatform(driver));
            String xpath = "//*[" + queryAttribute + "='" + elementText + "']";
            return wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
        }
        catch (NoSuchElementException exception) {
            throw new NoSuchElementException(GRAY + exception.getMessage() + RESET);
        }
    }

    /**
     * Acquires an element by its text
     *
     * @param elementText target element text
     */
    public WebElement getElementByText(String elementText) {
        String queryAttribute = getTextAttributeNameFor(getDriverPlatform(driver));
        String xpath = "//*[" + queryAttribute + "='" + elementText + "']";
        try {
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(1));

            return driver.findElement(By.xpath(xpath));
        }
        catch (WebDriverException exception) {
            log.warning("Failed to locate element containing text: '" + elementText + "'");
            return null;
        }
        finally {
            driver.manage().timeouts().implicitlyWait(Duration.ofMillis(elementTimeout));
        }
    }

    /**
     * Acquires an element that contains a certain text
     *
     * @param elementText target element text
     */
    public WebElement getElementContainingText(String elementText) {
        try {
            //*[contains(@text,'Schiphol')]"
            String queryKeyword = isAppiumDriver(driver) ? "@text" : "text()";
            String xpath = "//*[contains(" + queryKeyword + ",'" + elementText + "')]";
            return driver.findElement(By.xpath(xpath));
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
    }

    /**
     * Verify the text of {element name} on the {page name} to be: {expected text}
     *
     * @param element      target element
     * @param expectedText expected text
     */
    public void verifyElementContainsText(WebElement element, String elementName, String pageName, String expectedText) {
        expectedText = contextCheck(expectedText);
        log.info("Verifying that text of element " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY, " contains ") +
                highlighted(BLUE, expectedText) +
                highlighted(GRAY, " on ") +
                highlighted(BLUE, pageName)
        );
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
            List<ElementBundle<String>> bundles,
            String pageName) {
        for (ElementBundle<String> bundle : bundles) {
            String elementName = bundle.elementName();
            String expectedText = contextCheck(bundle.data());
            log.info("Performing text verification for " +
                    highlighted(BLUE, elementName) +
                    highlighted(GRAY, " on the ") +
                    highlighted(BLUE, pageName) +
                    highlighted(GRAY, " with the text: ") +
                    highlighted(BLUE, expectedText)
            );
            if (!expectedText.equals(bundle.element().getText()))
                throw new PickleibException("The " + bundle.elementName() + " does not contain text '");
            log.success("Text of the element" + bundle.elementName() + " was verified!");
        }
    }

    /**
     * Verify the text of an element from the list on the {page name}
     *
     * @param pageName specified page instance name
     */
    public void verifyListContainsElementByText(
            List<WebElement> elements,
            String expectedText,
            String listName,
            String pageName) {
        if (elements.stream().anyMatch(element -> element.getText().contains(expectedText)))
            log.success("The " + listName + " list contains an element with " + expectedText + " text!");
        else
            throw new PickleibException(
                    "The " + listName + " list does not contains an element with " + expectedText + " text!"
            );
    }

    /**
     * Fill form input on the {page name}
     *
     * @param bundles  list of bundles where input element, input name and input texts are stored
     * @param pageName specified page instance name
     */
    public void fillInputForm(List<ElementBundle<String>> bundles, String pageName) {
        String inputName;
        String input;
        String inputText;
        for (ElementBundle<String> bundle : bundles) {
            inputText = contextCheck(bundle.data());
            log.info("Filling " +
                    highlighted(BLUE, bundle.elementName()) +
                    highlighted(GRAY, " on the ") +
                    highlighted(BLUE, pageName) +
                    highlighted(GRAY, " with the text: ") +
                    highlighted(BLUE, inputText)
            );
            pageName = firstLetterDeCapped(pageName);
            clearFillInput(
                    bundle.element(),
                    inputText,
                    !isPlatformElement(bundle.element())
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
}
