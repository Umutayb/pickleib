package pickleib.utilities.element;

import context.ContextStore;
import dev.failsafe.internal.util.Assert;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import pickleib.driver.DriverFactory;
import pickleib.enums.ElementState;
import pickleib.enums.InteractionType;
import pickleib.exceptions.PickleibException;
import pickleib.exceptions.PickleibVerificationException;
import pickleib.mobile.interactions.MobileInteractions;
import pickleib.utilities.Interactions;
import pickleib.utilities.Utilities;
import pickleib.web.interactions.WebInteractions;
import records.Bundle;
import java.util.List;
import java.util.Map;
import static utils.StringUtilities.Color.*;

/**
 * A utility class that provides common interaction methods for web and mobile steps in the context of Pickleib.
 *
 * <p>
 * This class houses the utilities and common functionality for both web and mobile platforms.
 * </p>
 *
 *
 * @author Umut Ay Bora
 * @author Egecan Şen
 * @author Inna Drapii
 *
 * @since 1.8.0
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ElementInteractions extends Utilities {

    public DriverFactory.DriverType driverType;

    protected WebDriverWait wait;

    boolean scroll = false;

    public ElementInteractions(RemoteWebDriver driver, WebDriverWait wait, DriverFactory.DriverType driverType){
        super(driver);
        this.driverType = driverType;
        this.wait = wait;
    }

    public ElementInteractions(RemoteWebDriver driver, DriverFactory.DriverType driverType){
        super(driver);
        this.driverType = driverType;
    }

    public boolean isScrolling() {
        return scroll;
    }

    public void setScroll(boolean scroll) {
        this.scroll = scroll;
    }

    /**
     *
     * Adds given values to the local storage
     *
     * @param form Map(String, String)
     */
    public void addLocalStorageValues(Map<String, String> form){
        addValuesToLocalStorage(form);
    }

    /**
     *
     * Adds given cookies
     *
     * @param cookies Map(String, String)
     */
    public void addCookies(Map<String, String> cookies){
        putCookies(cookies);
    }

    /**
     * Deletes all cookies
     */
    public void deleteCookies() {deleteAllCookies();}

    /**
     *
     * Clicks a button by its {text} text
     *
     * @param text target text
     */
    public void clickByText(String text) {
        clickButtonWithText(text, scroll);
    }

    /**
     *
     * Wait for {duration} seconds
     *
     * @param duration desired duration
     */
    public void waitForSeconds(Integer duration) {
        waitFor(duration);
    }

    /**
     *
     * Click the {button name} on the {page name}
     *
     * @param buttonName target button name
     * @param pageName specified page instance name
     */
    public void clickInteraction(WebElement button, String buttonName, String pageName){
        log.info("Clicking " +
                highlighted(BLUE, buttonName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName)
        );
        clickElement(button, scroll);
    }

    /**
     *
     * Click the {button name} on the {page name}
     *
     */
    public void clickInteraction(WebElement button){
        clickElement(button, scroll);
    }

    /**
     *
     * Acquire attribute {attribute name} from element {element name} on the {page name}
     * (Use 'innerHTML' attributeName to acquire text on an element)
     *
     * @param element target element
     * @param attributeName acquired attribute name
     * @param elementName target element name
     * @param pageName specified page instance name
     */
    public void saveAttributeValue(
            WebElement element,
            String attributeName,
            String elementName,
            String pageName){
        updateContextByElementAttribute(element, attributeName, elementName, pageName);
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
    public WebElement center(WebElement element){
        centerElement(element);
        return element;
    }

    /**
     *
     * Click towards to {element name} on the {page name}
     *
     * @param element target element
     * @param elementName target element name
     * @param pageName specified page instance name
     */
    public void clickTowards(WebElement element, String elementName, String pageName){
        log.info("Clicking " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName)
        );
        clickAtAnOffset(element, 0, 0);
    }

    /**
     *
     * If present, click element {element name} on the {page name}
     *
     * @param element target element
     * @param elementName target element name
     * @param pageName specified page instance name
     */
    public void clickIfPresent(WebElement element, String elementName, String pageName){
        log.info("Clicking " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName) +
                highlighted(GRAY, ", if it is present...")
        );
        clickButtonIfPresent(element);
    }

    /**
     *
     * Fill input element {input name} from {pageName} with text: {input text}
     *
     * @param inputElement target input element
     * @param inputName target input element name
     * @param pageName specified page instance name
     * @param input input text
     */
    public void basicFill(WebElement inputElement, String inputName, String pageName, String input, boolean verify){
        log.info("Filling " +
                highlighted(BLUE, inputName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName) +
                highlighted(GRAY, " with the text: ") +
                highlighted(BLUE, input)
        );
        clearFillInput(
                inputElement, //Element
                input, //Input Text
                scroll,
                verify
        );
    }

    /**
     *
     * Fill input element {input name} from {pageName} with text: {input text}
     *
     * @param inputElement target input element
     * @param inputName target input element name
     * @param pageName specified page instance name
     * @param input input text
     */
    public void basicFill(WebElement inputElement, String inputName, String pageName, String input){
        basicFill(inputElement, inputName, pageName, input, true);
    }

    /**
     *
     * Fill form input on the {page name}
     *
     * @param bundles list of bundles where input element, input name and input texts are stored
     * @param pageName specified page instance name
     */
    public void fillForm(List<Bundle<WebElement, String, String>> bundles, String pageName){
        fillInputForm(bundles, pageName);
    }

    /**
     *
     * Verify the text of {element name} on the {page name} to be: {expected text}
     *
     * @param element target element
     * @param elementName target element name
     * @param pageName specified page instance name
     * @param expectedText expected text
     */
    public void verifyText(WebElement element, String elementName, String pageName, String expectedText){
        log.info("Performing text verification for " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName)
        );
        verifyElementText(element, expectedText);
    }

    /**
     *
     * Verify the text of {element name} on the {page name} to be: {expected text}
     *
     * @param element target element
     * @param elementName target element name
     * @param pageName specified page instance name
     * @param expectedText expected text
     */
    public void verifyContainsText(WebElement element, String elementName, String pageName, String expectedText){
        log.info("Performing text verification for " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName)
        );
        verifyElementContainsText(element, expectedText);
    }

    /**
     *
     * Verify the text of an element from the list on the {page name}
     *
     * @param pageName specified page instance name
     */
    public void verifyListedText(
            List<Bundle<WebElement, String, String>> bundles,
            String pageName){
        verifyListedElementText(bundles, pageName);
    }

    /**
     *
     * Verify the presence of an element {element name} on the {page name}
     *
     * @param element target element
     * @param elementName target element name
     * @param pageName specified page instance name
     */
    public void verifyPresence(WebElement element, String elementName, String pageName){
        log.info("Verifying presence of " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName)
        );
        verifyElementState(element, ElementState.displayed);
        log.success("Presence of the element " + elementName + " was verified!");
    }

    /**
     * Closes the browser
     */
    public void quitDriver(){
        driver.quit();
    }

    /**
     *
     * Verify that the element {element name} on the {page name} is in {expected state} state
     *
     * @param element target element
     * @param elementName target element name
     * @param pageName specified page instance name
     * @param expectedState expected state
     */
    public void verifyState(
            WebElement element,
            String elementName,
            String pageName,
            ElementState expectedState){
        log.info("Verifying " +
                highlighted(BLUE, expectedState.name()) +
                highlighted(GRAY," state of ")+
                highlighted(BLUE, elementName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName)
        );
        verifyElementState(element, expectedState);
    }

    /**
     * Logs and verifies the state of a given web element on a specific page.
     * <p>
     * This method logs the expected state of the element and then checks if the element's current state
     * matches the expected state. If it does, a success message is logged. If not, a warning message is logged.
     * </p>
     *
     * @param element        The WebElement to be checked.
     * @param elementName    A human-readable name or description for the web element.
     * @param pageName       A human-readable name or description for the page on which the element resides.
     * @param expectedState  The expected {@link ElementState} of the web element.
     *
     * <p><strong>Usage Example:</strong></p>
     * <pre>{@code
     * WebElement myButton = driver.findElement(By.id("myButton"));
     * elementIs(myButton, "myButton", "MainPage", ElementState.displayed);
     * }</pre>
     *
     */
    public boolean elementIs(WebElement element, String elementName, String pageName, ElementState expectedState){
        log.info("Waiting for " +
                highlighted(BLUE, expectedState.name()) +
                highlighted(GRAY," state of ")+
                highlighted(BLUE, elementName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName)
        );
        boolean inExpectedState  = elementIs(element, expectedState);
        if (inExpectedState)
            log.success(elementName + " element is in " + expectedState + " state!");
        else
            log.warning(elementName + " element is not in " + expectedState + " state!");
        return inExpectedState;
    }

    public boolean elementStateIs(WebElement element, ElementState expectedState){
        return elementIs(element, expectedState);
    }

    /**
     *
     * Wait for absence of an element {element name} on the {page name}
     *
     * @param element target element
     * @param elementName target element name
     * @param pageName specified page instance name
     */
    public void waitUntilAbsence(WebElement element, String elementName, String pageName){
        log.info("Waiting for the absence of " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName)
        );
        elementIs(element, ElementState.absent);
    }

    /**
     *
     * Wait for element {element name} on the {page name} to be visible
     *
     * @param element target element
     * @param elementName target element name
     * @param pageName specified page instance name
     */
    public void waitUntilVisible(WebElement element, String elementName, String pageName) {
        log.info("Waiting visibility of " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName)
        );
        boolean visible = elementIs(element, ElementState.displayed);
        log.info("Element is visible ? " + highlighted(BLUE, String.valueOf(visible)));
    }

    /**
     *
     * Wait until an element {element name} on the {page name} has {attribute value} value for its {attribute name} attribute
     *
     * @param element target element
     * @param elementName target element name
     * @param pageName specified page instance name
     * @param attributeValue expected attribute value
     * @param attributeName target attribute name
     */
    public void waitUntilElementContainsAttribute(
            WebElement element,
            String elementName,
            String pageName,
            String attributeName,
            String attributeValue) {
        log.info("Waiting until element " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY," contains ") +
                highlighted(BLUE, attributeValue) +
                highlighted(GRAY," in its ") +
                highlighted(BLUE, attributeName) +
                highlighted(GRAY," attribute.")
        );
        boolean attributeFound = elementContainsAttribute(element, attributeName, attributeValue);
        log.info("Attribute match ? " + highlighted(BLUE, String.valueOf(attributeFound)));
    }

    /**
     *
     * Verify that element {element name} on the {page name} has {attribute value} value for its {attribute name} attribute
     *
     * @param element target element
     * @param elementName target element name
     * @param pageName specified page instance name
     * @param attributeValue expected attribute value
     * @param attributeName target attribute name
     */
    public void verifyElementContainsAttribute(
            WebElement element,
            String elementName,
            String pageName,
            String attributeName,
            String attributeValue) {
        log.info("Verifying that " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY," contains ") +
                highlighted(BLUE, attributeValue) +
                highlighted(GRAY," in its ") +
                highlighted(BLUE, attributeName) +
                highlighted(GRAY," attribute.")
        );
        if (!elementContainsAttribute(element, attributeName, attributeValue))
            throw new PickleibException(
                "The " + attributeName + " attribute of element " + elementName + " could not be verified." +
                        "\nExpected value: " + attributeValue + "\nActual value: " + element.getAttribute(attributeName)
            );
        log.success("The " + attributeName + " attribute of element " + elementName + " is verified!" );
    }

    /**
     * Verify that an attribute {attribute name} of element {element name} on the {page name} contains a specific {value}.
     *
     * @param attributeName  the name of the attribute to be verified
     * @param elementName    the name of the element to be verified
     * @param pageName       the name of the page containing the element
     * @param value the expected part of value of the attribute
     *
     */
    public void verifyElementAttributeContainsValue(
            WebElement element,
            String elementName,
            String pageName,
            String attributeName,
            String value) {
        log.info("Verifying that " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY," contains ") +
                highlighted(BLUE, value) +
                highlighted(GRAY," in its ") +
                highlighted(BLUE, attributeName) +
                highlighted(GRAY," attribute.")
        );

        if (!elementAttributeContainsValue(element, attributeName, value))
            throw new PickleibVerificationException(
                    "The " + attributeName + " attribute of element " + elementName + " could not be verified." +
                            "\nExpected value: " + value + "\nActual value: " + element.getAttribute(attributeName)
            );
        log.success("The " + attributeName + " attribute of element " + elementName + " is verified!" );
    }

    /**
     *
     * Verify {attribute name} css attribute of an element {element name} on the {page name} is {attribute value}
     *
     * @param element target element
     * @param attributeName target attribute name
     * @param elementName target attribute name
     * @param pageName specified page instance name
     * @param attributeValue expected attribute value
     */
    public void verifyElementColor(
            WebElement element,
            String attributeName,
            String elementName,
            String pageName,
            String attributeValue) {
        log.info("Verifying " +
                highlighted(BLUE, attributeName) +
                highlighted(GRAY, " attribute of ") +
                highlighted(BLUE, elementName) +
                highlighted(GRAY," on the ") +
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
     *
     * Verify the presence of listed element from a list on the {page name}
     *
     * @param bundles list that contains element, elementName, elementText
     * @param pageName specified page instance name
     * @param signForms table that has key as "Input" and value as "Input Element" (dataTable.asMaps())
     */
    public void verifyPresenceOfListedElements(
            List<Bundle<WebElement, String, String>> bundles,
            WebElement element,
            List<WebElement> elements,
            String pageName,
            List<Map<String, String>> signForms){

        for (Bundle<WebElement, String, String> bundle : bundles) {
            String elementName = bundle.beta();
            String expectedText = strUtils.contextCheck(bundle.theta());

            log.info("Performing text verification for " +
                    highlighted(BLUE, elementName) +
                    highlighted(GRAY," on the ") +
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
     *
     * Verify the page is redirecting to the page {target url}
     *
     * @param url target url
     */
    public void verifyCurrentUrl(String url) {
        url = strUtils.contextCheck(url);
        log.info("The url contains " + url);
        if (!driver.getCurrentUrl().contains(url))
            throw new PickleibException("Current url does not contain the expected url!");
    }

    /**
     *
     * Click on a button that contains {button text} text
     *
     * @param buttonText target button text
     * @param scroll scrolls if true
     */
    public void clickButtonByText(String buttonText, Boolean scroll) {
        clickButtonByItsText(buttonText, scroll);
    }

    /**
     *
     * Update context {key} -> {value}
     *
     * @param key Context key
     * @param value Context value
     */
    public void updateContext(String key, String value){
        value = strUtils.contextCheck(value);
        log.info(
                "Updating context: " +
                        highlighted(BLUE, key) +
                        highlighted(GRAY, " -> ") +
                        highlighted(BLUE, value)
        );
        ContextStore.put(key, value);
    }

    /**
     *
     * Press {target key} key on {element name} element of the {}
     *
     * @param keys target key
     * @param elementName target element name
     * @param pageName specified page instance name
     */
    public void pressKey(WebElement element, String elementName, String pageName, Keys... keys){
        pressKeysOnElement(element, elementName, pageName, keys);
    }

    /**
     *
     * Upload file on input {input element field name} on the {page name} with file: {target file path}
     *
     * @param inputElement target input element
     * @param inputName input element field name
     * @param pageName specified page instance name
     * @param absoluteFilePath target file path
     */
    public void fillInputWithFile(
            WebElement inputElement,
            String inputName,
            String pageName,
            String absoluteFilePath){
        absoluteFilePath = strUtils.contextCheck(absoluteFilePath);
        log.info("Filling " +
                highlighted(BLUE, inputName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName) +
                highlighted(GRAY, " with the text: ") +
                highlighted(BLUE, absoluteFilePath)
        );
        clearFillInput(
                inputElement,
                absoluteFilePath,
                scroll,
                false
        );
    }

    /**
     * Executes interactions on a list of element bundles, based on the specified interaction type.
     * <p>
     * The interaction type is specified in the "Interaction Type" key of the map contained in each element bundle.
     * <p>
     * @param bundles A list of element bundles containing the element name, the matching element, and a map of the element's attributes.
     * @param pageName The name of the page object.
     * @throws EnumConstantNotPresentException if an invalid interaction type is specified in the element bundle.
     */
    public void bundleInteraction(List<Bundle<String, WebElement, Map<String, String>>> bundles, String pageName){
        for (Bundle<String, WebElement, Map<String, String>> bundle:bundles) {
            InteractionType interactionType = InteractionType.valueOf(bundle.theta().get("Interaction Type"));
            switch (interactionType) {
                case click -> clickInteraction(bundle.beta(), bundle.alpha(), pageName);
                case fill -> basicFill(bundle.beta(), bundle.alpha(), pageName, bundle.theta().get("Input"));
                case center -> center(bundle.beta(), bundle.alpha(), pageName);
                case verify -> verifyElementContainsAttribute(
                            bundle.beta(),
                            bundle.alpha(),
                            pageName,
                            bundle.theta().get("Attribute Name"),
                            strUtils.contextCheck(bundle.theta().get("Attribute Value"))
                    );
                default -> throw new EnumConstantNotPresentException(InteractionType.class, interactionType.name());
            }
        }
    }

    @Override
    protected WebElement centerElement(WebElement element) {
        WebInteractions webInteractions = new WebInteractions();
        MobileInteractions mobileInteractions = new MobileInteractions();
        return switch (driverType){
            case Web -> webInteractions.center(element);
            case Mobile -> mobileInteractions.center(element);
        };
    }
}
