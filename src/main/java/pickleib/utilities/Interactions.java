package pickleib.utilities;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import pickleib.enums.ElementState;
import records.Bundle;
import java.util.List;
import java.util.Map;

public interface Interactions {

    boolean isScrolling();

    void setScroll(boolean scroll);

    /**
     *
     * Adds given values to the local storage
     *
     * @param form Map(String, String)
     */
    void addLocalStorageValues(Map<String, String> form);

    /**
     *
     * Adds given cookies
     *
     * @param cookies Map(String, String)
     */
    void addCookies(Map<String, String> cookies);

    /**
     * Deletes all cookies
     */
    void deleteCookies();

    /**
     *
     * Clicks a button by its {text} text
     *
     * @param text target text
     */
    void clickByText(String text);

    /**
     *
     * Wait for {duration} seconds
     *
     * @param duration desired duration
     */
    void waitForSeconds(Integer duration);

    /**
     *
     * Click the {button name} on the {page name}
     *
     * @param buttonName target button name
     * @param pageName specified page instance name
     */
    void clickInteraction(WebElement button, String buttonName, String pageName);

    /**
     *
     * Click the {button name} on the {page name}
     *
     */
    void clickInteraction(WebElement button);

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
    void saveAttributeValue(
            WebElement element,
            String attributeName,
            String elementName,
            String pageName
    );

    /**
     *
     * Center the {element name} on the {page name}
     *
     * @param element target element
     * @param elementName target element name
     * @param pageName specified page instance name
     */
    void center(WebElement element, String elementName, String pageName);

    /**
     * Center a given element
     *
     * @param element target element
     * @return the given element
     */
    WebElement center(WebElement element);

    /**
     *
     * Click towards to {element name} on the {page name}
     *
     * @param element target element
     * @param elementName target element name
     * @param pageName specified page instance name
     */
    public void clickTowards(WebElement element, String elementName, String pageName);

    /**
     *
     * If present, click element {element name} on the {page name}
     *
     * @param element target element
     * @param elementName target element name
     * @param pageName specified page instance name
     */
    void clickIfPresent(WebElement element, String elementName, String pageName);

    /**
     *
     * Fill input element {input name} from {pageName} with text: {input text}
     *
     * @param inputElement target input element
     * @param inputName target input element name
     * @param pageName specified page instance name
     * @param input input text
     */
    void basicFill(WebElement inputElement, String inputName, String pageName, String input, boolean verify);

    /**
     *
     * Fill form input on the {page name}
     *
     * @param bundles list of bundles where input element, input name and input texts are stored
     * @param pageName specified page instance name
     */
    void fillForm(List<Bundle<WebElement, String, String>> bundles, String pageName);

    /**
     *
     * Verify the text of {element name} on the {page name} to be: {expected text}
     *
     * @param element target element
     * @param elementName target element name
     * @param pageName specified page instance name
     * @param expectedText expected text
     */
    void verifyText(WebElement element, String elementName, String pageName, String expectedText);

    /**
     *
     * Verify the text of {element name} on the {page name} to be: {expected text}
     *
     * @param element target element
     * @param elementName target element name
     * @param pageName specified page instance name
     * @param expectedText expected text
     */
    void verifyContainsText(WebElement element, String elementName, String pageName, String expectedText);

    /**
     *
     * Verify the text of an element from the list on the {page name}
     *
     * @param pageName specified page instance name
     */
    void verifyListedText(List<Bundle<WebElement, String, String>> bundles, String pageName);

    /**
     *
     * Verify the presence of an element {element name} on the {page name}
     *
     * @param element target element
     * @param elementName target element name
     * @param pageName specified page instance name
     */
    void verifyPresence(WebElement element, String elementName, String pageName);

    /**
     * Closes the browser
     */
    void quitDriver();

    /**
     *
     * Verify that the element {element name} on the {page name} is in {expected state} state
     *
     * @param element target element
     * @param elementName target element name
     * @param pageName specified page instance name
     * @param expectedState expected state
     */
    void verifyState(
            WebElement element,
            String elementName,
            String pageName,
            ElementState expectedState
    );

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
    boolean elementIs(WebElement element, String elementName, String pageName, ElementState expectedState);

    /**
     *
     * Wait for absence of an element {element name} on the {page name}
     *
     * @param element target element
     * @param elementName target element name
     * @param pageName specified page instance name
     */
    void waitUntilAbsence(WebElement element, String elementName, String pageName);

    /**
     *
     * Wait for element {element name} on the {page name} to be visible
     *
     * @param element target element
     * @param elementName target element name
     * @param pageName specified page instance name
     */
    void waitUntilVisible(WebElement element, String elementName, String pageName);

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
    void waitUntilElementContainsAttribute(
            WebElement element,
            String elementName,
            String pageName,
            String attributeName,
            String attributeValue
    );

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
    void verifyElementContainsAttribute(
            WebElement element,
            String elementName,
            String pageName,
            String attributeName,
            String attributeValue
    );

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
    void verifyElementColor(
            WebElement element,
            String attributeName,
            String elementName,
            String pageName,
            String attributeValue
    );

    /**
     *
     * Verify the presence of listed element from a list on the {page name}
     *
     * @param bundles list that contains element, elementName, elementText
     * @param pageName specified page instance name
     * @param signForms table that has key as "Input" and value as "Input Element" (dataTable.asMaps())
     */
    void verifyPresenceOfListedElements(
            List<Bundle<WebElement, String, String>> bundles,
            WebElement element,
            List<WebElement> elements,
            String pageName,
            List<Map<String, String>> signForms
    );

    /**
     *
     * Click on a button that contains {button text} text
     *
     * @param buttonText target button text
     * @param scroll scrolls if true
     */
    void clickButtonByText(String buttonText, Boolean scroll);

    /**
     *
     * Update context {key} -> {value}
     *
     * @param key Context key
     * @param value Context value
     */
    void updateContext(String key, String value);

    /**
     *
     * Press {target key} key on {element name} element of the {}
     *
     * @param keys target key
     * @param elementName target element name
     * @param pageName specified page instance name
     */
    void pressKey(WebElement element, String elementName, String pageName, Keys... keys);

    /**
     *
     * Upload file on input {input element field name} on the {page name} with file: {target file path}
     *
     * @param inputElement target input element
     * @param inputName input element field name
     * @param pageName specified page instance name
     * @param absoluteFilePath target file path
     */
    void fillInputWithFile(WebElement inputElement, String inputName, String pageName, String absoluteFilePath);

    /**
     * Executes interactions on a list of element bundles, based on the specified interaction type.
     * <p>
     * The interaction type is specified in the "Interaction Type" key of the map contained in each element bundle.
     * <p>
     * @param bundles A list of element bundles containing the element name, the matching element, and a map of the element's attributes.
     * @param pageName The name of the page object.
     * @throws EnumConstantNotPresentException if an invalid interaction type is specified in the element bundle.
     */
    void bundleInteraction(List<Bundle<String, WebElement, Map<String, String>>> bundles, String pageName);
}
