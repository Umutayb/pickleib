package pickleib.web.interactions;

import context.ContextStore;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import pickleib.enums.Direction;
import pickleib.enums.ElementState;
import pickleib.enums.InteractionType;
import pickleib.enums.Navigation;
import pickleib.exceptions.PickleibException;
import pickleib.exceptions.PickleibVerificationException;
import pickleib.utilities.element.ElementInteractions;
import pickleib.utilities.screenshot.ScreenCaptureUtility;
import pickleib.web.driver.PickleibWebDriver;
import pickleib.web.utilities.WebUtilities;
import collections.Bundle;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static utils.StringUtilities.Color.*;
import static utils.StringUtilities.contextCheck;

@SuppressWarnings("unused")
public class WebInteractions extends WebUtilities {

    public ElementInteractions interact;
    protected WebDriverWait wait;
    boolean scroll = false;

    public WebInteractions(RemoteWebDriver driver, WebDriverWait wait){
        super(driver);
        this.driver = driver;
        this.wait = wait;
    }

    public WebInteractions(){
        super(PickleibWebDriver.get());
        this.wait = PickleibWebDriver.driverWait();
    }

    public boolean isScrolling() {
        return scroll;
    }

    public void setScroll(boolean scroll) {
        this.scroll = scroll;
    }

    /**
     * Adds given values to the local storage
     *
     * @param form Map(String, String)
     */
    public void addLocalStorageValues(Map<String, String> form) {
        super.addValuesToLocalStorage(form);
    }

    /**
     * Adds given cookies
     *
     * @param cookies Map(String, String)
     */
    public void addCookies(Map<String, String> cookies) {
        super.addCookies(cookies);
    }

    /**
     * Deletes all cookies
     */
    public void deleteCookies() {
        super.deleteAllCookies();
    }

    /**
     * Clicks a button by its {text} text
     *
     * @param text target text
     */
    public void clickByText(String text) {
        log.info("Clicking button by text " + highlighted(BLUE, text));
        super.clickButtonByItsText(text);
    }

    /**
     * Clicks an element acquired by text with optional scrolling.
     *
     * @param buttonText target element text
     * @param scroll     scrolls if true
     */
    public void clickButtonWithText(String buttonText, boolean scroll) {
        log.info("Clicking button with text " + highlighted(BLUE, buttonText));
        if (scroll) super.clickButtonByItsText(buttonText, scroll);
        else super.clickByText(buttonText);
    }

    /**
     * Clicks an {element} with the {element name} on the {page name} with optional scrolling
     * after waiting for its state to be enabled
     *
     * @param element     target element
     * @param scroll      scrolls if true
     * @param elementName target element name
     * @param pageName    specified page instance name
     */
    public void clickElement(WebElement element, boolean scroll, String elementName, String pageName) {
        log.info("Clicking " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY, " on ") +
                highlighted(BLUE, pageName)
        );
        clickElement(element, scroll);
    }

    /**
     * Clicks an {element} with the {element name} on the {page name} after waiting for its state to be enabled
     *
     * @param element     target element
     * @param elementName target element name
     * @param pageName    specified page instance name
     */
    public void clickElement(WebElement element, String elementName, String pageName) {
        log.info("Clicking " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY, " on the ") +
                highlighted(BLUE, pageName)
        );
        clickElement(element);
    }

    /**
     * Waits for a certain while
     *
     * @param seconds duration as a double
     */
    //This method makes the thread wait for a certain while
    public void waitFor(double seconds) {
        log.info("Waiting for " + highlighted(BLUE, String.valueOf(seconds)));
        super.waitFor(seconds);
    }

    /**
     * Saves element attribute to context
     *
     * @param element       target element
     * @param attributeName attribute name
     * @param elementName   target element name
     * @param pageName      specified page instance name
     */
    public void saveAttributeValue(WebElement element, String attributeName, String elementName, String pageName) {
        log.info("Saving attribute " +
                highlighted(BLUE, attributeName) +
                highlighted(GRAY, " of ") +
                highlighted(BLUE, elementName) +
                highlighted(GRAY, " on ") +
                highlighted(BLUE, pageName) +
                highlighted(GRAY, " to context")
        );
        super.updateContextByElementAttribute(element, attributeName, elementName, pageName);
        log.success("Attribute " +
                highlighted(BLUE, attributeName) +
                highlighted(GRAY, " of ") +
                highlighted(BLUE, elementName) +
                highlighted(GRAY, " from ") +
                highlighted(BLUE, pageName) +
                highlighted(GRAY, " was saved to context!"));
    }

    /**
     * Clicks coordinates specified by the given offsets from the center of a given {element}
     * with the {element name} on the {page name}.
     *
     * @param element     target element
     * @param elementName target element name
     * @param pageName    specified page instance name
     */
    public void clickTowards(WebElement element, String elementName, String pageName) {
        log.info("Clicking towards " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY, " on the ") +
                highlighted(BLUE, pageName)
        );
        super.clickTowards(element);
    }

    /**
     * Clicks an {element} with the {element name} on the {page name} after waiting for its state to be enabled.
     *
     * @param element     target element
     * @param scroll      scrolls if true
     * @param elementName target element name
     * @param pageName    specified page instance name
     */
    public void clickIfPresent(WebElement element, Boolean scroll, String elementName, String pageName) {
        log.info("Checking if " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY, " presents on the ") +
                highlighted(BLUE, pageName)
        );
        clickIfPresent(element, scroll);
    }

    /**
     * Clears and fills a given input.
     *
     * @param inputElement target input element
     * @param elementName  target element name
     * @param pageName     specified page instance name
     * @param inputText    input text
     * @param scroll       scrolls if true
     * @param verify       verifies the input text value equals to an expected text if true
     */
    public void clearFillInput(WebElement inputElement, String elementName, String pageName, String inputText, @NotNull boolean scroll, boolean verify) {
        log.info("Clearing input " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY, " on the ") +
                highlighted(BLUE, pageName)
        );
        clearFillInput(inputElement, inputText, scroll, verify);
    }

    /**
     * Fills input form
     *
     * @param bundles  list of bundles where input element, input text and boolean value
     *                 (true - if the input text value equals to an expected text) are stored
     * @param pageName specified page instance name
     */
    public void fillForm(List<Bundle<WebElement, String, String>> bundles, String pageName) {
        log.info("Filling form on " + highlighted(BLUE, pageName));
        fillInputForm(bundles, pageName);
        log.success("Form was filled on " + highlighted(BLUE, pageName));
    }

    /**
     * Verifies the text of {element name} on the {page name} to be: {expected text}
     *
     * @param element      target element
     * @param expectedText expected text
     */
    public void verifyText(WebElement element, String elementName, String pageName, String expectedText) {
        log.info("Verifying text " +
                highlighted(BLUE, expectedText) +
                highlighted(GRAY, " of element ") +
                highlighted(BLUE, elementName) +
                highlighted(GRAY, " on ") +
                highlighted(BLUE, pageName)
        );
        super.verifyElementText(element, expectedText);
        log.success("Text of element " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY, " is ") +
                highlighted(BLUE, expectedText) +
                highlighted(GRAY, " on ") +
                highlighted(BLUE, pageName));
    }

    /**
     * Verifies the text of {element name} on the {page name} to contain: {expected text}
     *
     * @param element      target element
     * @param elementName  target element name
     * @param pageName     specified page instance name
     * @param expectedText expected text
     */
    public void verifyContainsText(WebElement element, String elementName, String pageName, String expectedText) {
        log.info("Verifying that text of element " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY, " contains ") +
                highlighted(BLUE, expectedText) +
                highlighted(GRAY, " on ") +
                highlighted(BLUE, pageName)
        );
        verifyElementContainsText(element, expectedText);
        log.success("Text of element " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY, " contains ") +
                highlighted(BLUE, expectedText) +
                highlighted(GRAY, " on ") +
                highlighted(BLUE, pageName));
    }

    /**
     * Verifies the text of an element from the list on the {page name}.
     *
     * @param bundles  list of bundles where element text, element name and expected text are stored
     * @param pageName specified page instance name
     */
    public void verifyListedText(List<Bundle<WebElement, String, String>> bundles, String pageName) {
        log.info("Verifying the text of elements on " + highlighted(BLUE, pageName));
        verifyListedElementText(bundles, pageName);
        log.success("Text of the elements was verified on " + highlighted(BLUE, pageName));
    }

    /**
     * Verifies the presence of an element {element name} on the {page name}
     *
     * @param element     target element
     * @param elementName target element name
     * @param pageName    specified page instance name
     */
    public void verifyPresence(WebElement element, String elementName, String pageName) {
        log.info("Verifying the presence of " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY, " on ") +
                highlighted(BLUE, pageName)
        );
        super.verifyElementState(element, ElementState.displayed);
        log.success("Presence of the element " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY, " was verified on ") +
                highlighted(BLUE, pageName));
    }

    /**
     * Closes the browser
     */
    public void quitDriver() {
        super.quitDriver();
    }

    /**
     * Verifies a given element is in expected state
     *
     * @param element     target element
     * @param elementName target element name
     * @param pageName    specified page instance name
     * @param state       expected state
     * @return returns the element if its in expected state
     */
    public WebElement verifyElementState(WebElement element, String elementName, String pageName, ElementState state) {
        log.info("Verifying that the state of " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY, " is ") +
                highlighted(BLUE, state.name()) +
                highlighted(GRAY, " on the ") +
                highlighted(BLUE, pageName)
        );
        return verifyElementState(element, state);
    }

    /**
     * Waits until a given element is in expected state
     *
     * @param element     target element
     * @param elementName target element name
     * @param pageName    specified page instance name
     * @param state       expected state
     * @return returns true if an element is in the expected state
     */
    public Boolean elementIs(WebElement element, String elementName, String pageName, @NotNull ElementState state) {
        log.info("Verifying that the state of " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY, " is ") +
                highlighted(BLUE, state.name()) +
                highlighted(GRAY, " on the ") +
                highlighted(BLUE, pageName)
        );
        return elementIs(element, state);
    }

    /**
     * Waits for absence of an element {element name} on the {page name}
     *
     * @param element     target element
     * @param elementName target element name
     * @param pageName    specified page instance name
     */
    public void waitUntilAbsence(WebElement element, String elementName, String pageName) {
        log.info("Waiting for absence of " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY, " on the ") +
                highlighted(BLUE, pageName)
        );
        boolean absent = elementIs(element, ElementState.absent);
        log.info("Element is absent ? " + highlighted(BLUE, String.valueOf(absent)));
    }

    /**
     * Waits for element {element name} on the {page name} to be visible
     *
     * @param element     target element
     * @param elementName target element name
     * @param pageName    specified page instance name
     */
    public void waitUntilVisible(WebElement element, String elementName, String pageName) {
        log.info("Waiting visibility of " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY, " on the ") +
                highlighted(BLUE, pageName)
        );
        boolean visible = elementIs(element, ElementState.displayed);
        log.info("Element is visible ? " + highlighted(BLUE, String.valueOf(visible)));
    }

    /**
     * Waits until an element {element name} on the {page name} has {attribute value} value for its {attribute name} attribute
     *
     * @param element        target element
     * @param elementName    target element name
     * @param pageName       specified page instance name
     * @param attributeValue expected attribute value
     * @param attributeName  target attribute name
     */
    public void waitUntilElementContainsAttribute(WebElement element, String elementName, String pageName, String attributeName, String attributeValue) {
        log.info("Waiting until element " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY, " contains ") +
                highlighted(BLUE, attributeValue) +
                highlighted(GRAY, " in its ") +
                highlighted(BLUE, attributeName) +
                highlighted(GRAY, " attribute.")
        );
        boolean attributeFound = elementContainsAttribute(element, attributeName, attributeValue);
        log.info("Attribute match ? " + highlighted(BLUE, String.valueOf(attributeFound)));
    }

    /**
     * Verifies that element {element name} on the {page name} has {attribute value} value for its {attribute name} attribute
     *
     * @param element        target element
     * @param elementName    target element name
     * @param pageName       specified page instance name
     * @param attributeValue expected attribute value
     * @param attributeName  target attribute name
     */
    public void verifyElementContainsAttribute(WebElement element, String elementName, String pageName, String attributeName, String attributeValue) {
        log.info("Verifying that " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY, " contains ") +
                highlighted(BLUE, attributeValue) +
                highlighted(GRAY, " in its ") +
                highlighted(BLUE, attributeName) +
                highlighted(GRAY, " attribute.")
        );
        if (!super.elementContainsAttribute(element, attributeName, attributeValue))
            throw new PickleibException(
                    "The " + attributeName + " attribute of element " + elementName + " could not be verified." +
                            "\nExpected value: " + attributeValue + "\nActual value: " + element.getAttribute(attributeName)
            );
        log.success("The " + attributeName + " attribute of element " + elementName + " is verified!");
    }

    /**
     * Verifies that an attribute {attribute name} of element {element name} on the {page name} contains a specific {value}.
     *
     * @param attributeName the name of the attribute to be verified
     * @param elementName   the name of the element to be verified
     * @param pageName      the name of the page containing the element
     * @param value         the expected part of value of the attribute
     */
    public void verifyElementAttributeContainsValue(WebElement element, String attributeName, String elementName, String pageName, String value) {
        log.info("Verifying that " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY, " contains ") +
                highlighted(BLUE, value) +
                highlighted(GRAY, " in its ") +
                highlighted(BLUE, attributeName) +
                highlighted(GRAY, " attribute.")
        );

        if (!super.elementAttributeContainsValue(element, attributeName, value))
            throw new PickleibVerificationException(
                    "The " + attributeName + " attribute of element " + elementName + " could not be verified." +
                            "\nExpected value: " + value + "\nActual value: " + element.getAttribute(attributeName)
            );
        log.success("The " + attributeName + " attribute of element " + elementName + " is verified!");
    }

    /**
     * Verifies {attribute name} css attribute of an element {element name} on the {page name} is {attribute value}
     *
     * @param element        target element
     * @param attributeName  target attribute name
     * @param elementName    target attribute name
     * @param pageName       specified page instance name
     * @param attributeValue expected attribute value
     */
    public void verifyElementColor(WebElement element, String attributeName, String elementName, String pageName, String attributeValue) {
        log.info("Verifying " +
                highlighted(BLUE, attributeName) +
                highlighted(GRAY, " attribute of ") +
                highlighted(BLUE, elementName) +
                highlighted(GRAY, " on the ") +
                highlighted(BLUE, pageName)
        );
        if (!attributeValue.equals(element.getCssValue(attributeName)))
            throw new PickleibException(
                    "The " + attributeName + " attribute of element " + elementName + " could not be verified." +
                            "\nExpected value: " + attributeValue + "\nActual value: " + element.getCssValue(attributeName)
            );
        log.success("Value of '" + attributeName + "' attribute is verified to be '" + attributeValue + "'!");
    }

    /**
     * Verifies the presence of listed element from a list on the {page name}
     *
     * @param bundles   list that contains element, elementName, elementText
     * @param pageName  specified page instance name
     * @param signForms table that has key as "Input" and value as "Input Element" (dataTable.asMaps())
     */
    public void verifyPresenceOfListedElements(List<Bundle<WebElement, String, String>> bundles, WebElement element, List<WebElement> elements, String pageName, List<Map<String, String>> signForms) {
        for (Bundle<WebElement, String, String> bundle : bundles) {
            String elementName = bundle.beta();
            String expectedText = contextCheck(bundle.theta());

            log.info("Performing text verification for " +
                    highlighted(BLUE, elementName) +
                    highlighted(GRAY, " on the ") +
                    highlighted(BLUE, pageName) +
                    highlighted(GRAY, " with the text: ") +
                    highlighted(BLUE, expectedText)
            );
            if (!element.getText().contains(expectedText))
                throw new PickleibException("The " + elementName + " does not contain text '" + expectedText + "' ");
            log.success("Text of '" + elementName + "' verified as '" + expectedText + "'!");
        }
    }

    /**
     * Updates context {key} -> {value}
     *
     * @param key   Context key
     * @param value Context value
     */
    public void updateContext(String key, String value) {
        value = contextCheck(value);
        log.info(
                "Updating context: " +
                        highlighted(BLUE, key) +
                        highlighted(GRAY, " -> ") +
                        highlighted(BLUE, value)
        );
        ContextStore.put(key, value);
    }

    /**
     * Presses {target key} key on {element name} element of the {}
     *
     * @param keys        target key
     * @param elementName target element name
     * @param pageName    specified page instance name
     */
    public void pressKey(WebElement element, String elementName, String pageName, Keys... keys) {
        super.pressKeysOnElement(element, elementName, pageName, keys);
    }

    /**
     * Fills the specified input element with the content of a file.
     *
     * <p>
     * This method fills the provided {@code inputElement} with the content of a file specified by {@code absoluteFilePath}.
     * The method logs information about the input name, page name, and the absolute file path being used.
     * Before filling the input, it clears the existing content if specified.
     * </p>
     *
     * @param inputElement     The {@code WebElement} representing the input field to be filled.
     * @param inputName        The name of the input field for logging purposes.
     * @param pageName         The name of the page where the interaction is performed for logging purposes.
     * @param absoluteFilePath The absolute file path to the file whose content will be used to fill the input.
     */
    public void fillInputWithFile(WebElement inputElement, String inputName, String pageName, String absoluteFilePath) {
        absoluteFilePath = contextCheck(absoluteFilePath);
        log.info("Filling " +
                highlighted(BLUE, inputName) +
                highlighted(GRAY, " on the ") +
                highlighted(BLUE, pageName) +
                highlighted(GRAY, " with the text: ") +
                highlighted(BLUE, absoluteFilePath)
        );
        super.clearFillInput(
                inputElement,
                absoluteFilePath,
                null,
                false
        );
    }

    /**
     * Executes interactions on a list of element bundles, based on the specified interaction type.
     * <p>
     * The interaction type is specified in the "Interaction Type" key of the map contained in each element bundle.
     * <p>
     *
     * @param bundles  A list of element bundles containing the element name, the matching element, and a map of the element's attributes.
     * @param pageName The name of the page object.
     * @throws EnumConstantNotPresentException if an invalid interaction type is specified in the element bundle.
     */
    public void bundleInteraction(List<Bundle<String, WebElement, Map<String, String>>> bundles, String pageName) {
        bundleInteraction(bundles, pageName);
    }

    /**
     * Performs a series of interactions based on the provided list of bundles.
     *
     * <p>
     * This method iterates through the given list of bundles, where each bundle contains information for a specific interaction.
     * The interaction type is determined by the "Interaction Type" value in the bundle.
     * The supported interaction types include:
     * </p>
     *
     * <ul>
     *     <li><b>Click:</b> Executes a click interaction on the specified element.</li>
     *     <li><b>Fill:</b> Fills the specified input value into the specified input field.</li>
     *     <li><b>Center:</b> Centers the specified element on the page using the provided {@code scroll} for scrolling.</li>
     *     <li><b>Verify:</b> Verifies that the specified element contains the expected attribute value.</li>
     * </ul>
     *
     * <p>
     * For 'fill' interactions, an additional boolean parameter is added to specify whether to clear the input field before filling.
     * </p>
     *
     * @param bundles  The list of bundles, where each bundle contains information for a specific interaction.
     * @param pageName The name of the page where the interactions are performed.
     * @param scroll   Scrolls if true
     * @throws EnumConstantNotPresentException If an unsupported interaction type is encountered in the bundle.
     */
    public void bundleInteraction(List<Bundle<String, WebElement, Map<String, String>>> bundles, String pageName, boolean scroll) {
        for (Bundle<String, WebElement, Map<String, String>> bundle : bundles) {
            InteractionType interactionType = InteractionType.valueOf(bundle.theta().get("Interaction Type"));
            switch (interactionType) {
                case click -> clickElement(bundle.beta(), scroll, bundle.alpha(), pageName);
                case fill ->
                        clearFillInput(bundle.beta(), bundle.alpha(), pageName, bundle.theta().get("Input"), false, scroll);
                case center -> centerElement(bundle.beta(), bundle.alpha(), pageName);
                case verify -> verifyElementContainsAttribute(
                        bundle.beta(),
                        bundle.alpha(),
                        pageName,
                        bundle.theta().get("Attribute Name"),
                        contextCheck(bundle.theta().get("Attribute Value"))
                );
                default -> throw new EnumConstantNotPresentException(InteractionType.class, interactionType.name());
            }
        }
    }

    /**
     *
     * Center the {element name} on the {page name}
     *
     * @param element target element
     * @param elementName target element name
     * @param pageName specified page instance name
     */
    public void center(WebElement element, String elementName, String pageName){
        log.info("Centering " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY," on ") +
                highlighted(BLUE, pageName)
        );
        center(element);
    }

    /**
     * Center a given element
     *
     * @param element target element
     * @return the given element
     */
    public WebElement centerElement(WebElement element){
        return super.centerElement(element);
    }

    private final ScreenCaptureUtility capture = new ScreenCaptureUtility();

    /**
     *
     * Navigate to url: {url}
     *
     * @param url target url
     */
    public void getUrl(String url) {
        url = contextCheck(url);
        driver.get(url);
    }

    /**
     *
     * Go to the {page} page
     *
     * @param page target page
     */
    public void toPage(String page){
        String url = driver.getCurrentUrl();
        String pageUrl = url + page;
        navigate(pageUrl);
    }

    /**
     *
     * Switch to the next tab
     *
     */
    public void switchToNextTab() {
        String parentHandle = switchWindowByHandle(null);
        ContextStore.put("parentHandle", parentHandle);
    }

    /**
     *
     * Switch to a specified parent tab
     *
     */
    public void switchToParentTab() {
        switchWindowByHandle(ContextStore.get("parentHandle").toString());
    }

    /**
     *
     * Switch to the tab with handle: {handle}
     * Switches a specified tab by tab handle
     *
     * @param handle target a tab handle
     */
    public void switchToTabByHandle(String handle) {
        handle = contextCheck(handle);
        String parentHandle = switchWindowByHandle(handle);
        ContextStore.put("parentHandle", parentHandle);
    }

    /**
     *
     * Switch to the tab number {tab index}
     * Switches tab by index
     *
     * @param handle target tab index
     */
    public void switchToTabByIndex(Integer handle) {
        String parentHandle = switchWindowByIndex(handle);
        ContextStore.put("parentHandle", parentHandle);
    }

    /**
     * Get HTML at {htmlPath}
     * Acquires the HTML from a given directory
     *
     * @param htmlPath target directory
     */
    public void getHTML(String htmlPath) {
        htmlPath = contextCheck(htmlPath);
        log.info("Navigating to the email @" + htmlPath);
        driver.get(htmlPath);
    }

    /**
     *
     * Set window width and height as {width} and {height}
     *
     * @param width target width
     * @param height target height
     */
    public void setFrameSize(Integer width, Integer height) {setWindowSize(width,height);}

    /**
     * Refreshes the page
     */
    public void refresh() {refreshThePage();}

    /**
     *
     * Navigate browser in {direction} direction
     *
     * @param direction target direction (backwards or forwards)
     */
    public void browserNavigate(Navigation direction) {navigateBrowser(direction);}

    /**
     *
     * Click button includes {button text} text with css locator
     *
     * @param cssSelector target text
     */
    public void clickByCssSelector(String cssSelector) {
        clickButtonByCssSelector(cssSelector);
    }

    /**
     * Scroll in a given direction
     *
     * @param direction target direction (UP or DOWN)
     */
    public void scrollOrSwipeInDirection(@NotNull Direction direction){
        log.info("Scrolling " + highlighted(BLUE, direction.name().toLowerCase()));
        String script = switch (direction) {
            case up -> "window.scrollBy(0,-document.body.scrollHeight)";
            case down -> "window.scrollBy(0,document.body.scrollHeight)";
            case left, right -> null;
        };
        ((JavascriptExecutor) driver).executeScript(script);
    }

    /**
     * Waits actively for the page to load up to 10 seconds
     */
    public void waitUntilPageLoads(int waitingTime) {
        waitUntilLoads(waitingTime);
    }

    /**
     *
     * Perform a JS click on an element {element name} on the {page name}
     *
     * @param element target element
     * @param elementName target element name
     * @param pageName specified page instance name
     */
    public void performJSClick(WebElement element, String elementName, String pageName){
        log.info("Clicking " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName)
        );
        clickWithJS(centerElement(element));
    }

    /**
     *
     * Click iFrame element {element name} in {iframe name} on the {page name}
     *
     * @param iframe target iframe
     * @param element target element
     * @param elementName target element name
     * @param iframeName target iframe name
     * @param pageName specified page instance name
     */
    public void clickIframeElement(WebElement iframe, WebElement element, String elementName, String iframeName, String pageName){
        log.info("Clicking i-frame element " +
                highlighted(BLUE, elementName) +
                highlighted(BLUE, " in i-frame ") +
                highlighted(BLUE, iframeName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName)
        );
        clickIframeButton(iframe, element);
    }

    /**
     *
     * Fill iFrame element {element name} of {iframe name} on the {page name} with text: {input text}
     *
     * @param iframe target iframe
     * @param element target element
     * @param inputName target element name
     * @param pageName specified page instance name
     * @param inputText input text
     */
    public void fillIframeInput(
            WebElement iframe,
            WebElement element,
            String inputName,
            String pageName,
            String inputText){
        log.info("Filling " +
                highlighted(BLUE, inputName) +
                highlighted(GRAY," i-frame element input on the ") +
                highlighted(BLUE, pageName) +
                highlighted(GRAY, " with the text: ") +
                highlighted(BLUE, inputText)
        );
        fillIframeInputElement(iframe, element, inputText);
    }

    /**
     *
     * Fill {iframe name} iframe form input on the {page name}
     *
     * @param bundles list of bundles where input element, input name and input texts are stored
     * @param iFrame target element
     * @param iframeName target iframe name
     * @param pageName specified page instance name
     */
    public void fillFormIframe(
            List<Bundle<WebElement, String, String>> bundles,
            WebElement iFrame,
            String iframeName,
            String pageName){
        fillIframeForm(
                bundles,
                iFrame,
                pageName
        );
    }

    /**
     *
     * Execute JS command: {script}
     *
     * @param script JS script
     */
    public void executeJSCommand(String script) {
        executeScript(script);
    }

    /**
     *
     * Listen to {event name} event and print {specified script} object
     * example -> listenerScript = "_ddm.listen(" + eventName + ");";
     *
     * @param eventName target event name
     * @param objectScript object script
     */
    //@Given("Listen to {} event & print {} object")
    public void listenGetAndPrintObject(String listenerScript, String eventName, String objectScript)  {
        objectScript = "return " + objectScript;
        if (isEventFired(eventName, listenerScript)) {
            Object object = executeScript(objectScript);
            log.info(object.toString());
        }
    }

    /**
     *
     * Listen to {event name} event and verify value of {node source} node is {expected value}
     * example -> listenerScript = "_ddm.listen(" + eventName + ");";
     *
     * @param eventName evet name
     * @param nodeSource node source
     * @param expectedValue expected value
     */
    public void listenGetAndVerifyObject(String listenerScript, String eventName, String nodeSource, String expectedValue)  {
        log.info("Verifying value of '" + nodeSource + "' node");
        String nodeScript = "return " + nodeSource;
        if (isEventFired(eventName, listenerScript)) {
            log.info("Verifying value of '" + highlighted(BLUE, nodeSource) + highlighted(GRAY, "' node"));
            Object object = executeScript(nodeScript);

            Pattern sourcePattern = Pattern.compile(expectedValue);
            Matcher nodeValueMatcher = sourcePattern.matcher(object.toString());

            if (!nodeValueMatcher.find())
                throw new PickleibException("Node values do not match! Expected: " + expectedValue + ", Found: " + object);
            log.success("Value of '" + nodeSource + "' is verified to be '" + object + "'");
        }
        else log.warning("'" + eventName + "' event is not fired!");
    }

    /**
     *
     * Listen to {event name} event and verify the values of the following nodes
     * example -> listenerScript = "_ddm.listen(" + eventName + ");";
     *
     * @param eventName event name
     * @param nodeList target node list
     */
    public void listenGetAndVerifyObject(String listenerScript, String eventName, List<Map<String, String>> nodeList)  {
        if (isEventFired(eventName, listenerScript)) {
            for (Map<String, String> nodeMap:nodeList) {
                String nodeSource = nodeMap.get("Node Source");
                String nodeValue = nodeMap.get("Node Value");

                log.info("Verifying value of '" + highlighted(BLUE, nodeSource) + highlighted(GRAY, "' node"));
                String nodeScript = "return " + nodeSource;
                Object object = executeScript(nodeScript);

                Pattern sourcePattern = Pattern.compile(nodeValue);
                Matcher nodeValueMatcher = sourcePattern.matcher(object.toString());

                if (!nodeValueMatcher.find())
                    throw new PickleibException("Node values do not match! Expected: " + nodeValue + ", Found: " + object);
                log.success("Value of '" + nodeSource + "' is verified to be '" + object + "'");
            }
        }
        else throw new RuntimeException("'" + eventName + "' event is not fired!");
    }

    /**
     * Uploads a given file
     *
     * @param fileUploadInput upload element
     * @param elementName     target element name
     * @param pageName        specified page instance name
     * @param directory       absolute file directory (excluding the file name)
     * @param fileName        file name (including a file extension)
     */
    public void uploadFile(@NotNull WebElement fileUploadInput, String elementName, String pageName, String directory, String fileName) {
        log.info("Uploading file " +
                highlighted(BLUE, fileName) +
                highlighted(GRAY, " to ") +
                highlighted(BLUE, directory) +
                highlighted(GRAY, " using ") +
                highlighted(BLUE, elementName) +
                highlighted(GRAY, " on ") +
                highlighted(BLUE, pageName)
        );
        super.uploadFile(fileUploadInput, directory, fileName);
    }
}