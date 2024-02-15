package pickleib.utilities.interfaces;

import collections.Bundle;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import pickleib.enums.Direction;
import pickleib.enums.ElementState;
import pickleib.utilities.Utilities;

import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public interface PolymorphicUtilities {

    /**
     * Clicks a button by its {text} text
     *
     * @param text target text
     */
    void clickByText(String text);

    /**
     * Acquires a specified attribute of a given element
     *
     * @param element   target element
     * @param attribute target attribute
     * @return returns the element attribute
     */
    String getAttribute(WebElement element, String attribute);

    /**
     * Acquires a specified attribute of a given {element} with the {element name} on the {page name}.
     *
     * @param element     target element
     * @param attribute   target attribute
     * @param elementName target element name
     * @param pageName    specified page instance name
     * @return returns the element attribute
     */
    String getAttribute(WebElement element, String attribute, String elementName, String pageName);

    /**
     * Clicks an element after waiting for its state to be enabled
     *
     * @param element target element
     */
    void clickElement(WebElement element);

    /**
     * Clicks an {element} with the {element name} on the {page name} after waiting for its state to be enabled
     *
     * @param element     target element
     * @param elementName target element name
     * @param pageName    specified page instance name
     */
    void clickElement(WebElement element, String elementName, String pageName);

    /**
     * Clicks an element with optional scrolling after waiting for its state to be enabled
     *
     * @param element target element
     * @param scroll  scrolls if true
     */
    void clickElement(WebElement element, boolean scroll);

    /**
     * Clicks an {element} with the {element name} on the {page name} with optional scrolling
     * after waiting for its state to be enabled
     *
     * @param element     target element
     * @param scroll      scrolls if true
     * @param elementName target element name
     * @param pageName    specified page instance name
     */
    void clickElement(WebElement element, String elementName, String pageName, boolean scroll);

    /**
     * Click coordinates specified by the given offsets from the center of a given element
     *
     * @param element target element
     */
    void clickTowards(WebElement element);

    /**
     * Click coordinates specified by the given offsets from the center of a given {element}
     * with the {element name} on the {page name}
     *
     * @param element     target element
     * @param elementName target element name
     * @param pageName    specified page instance name
     */
    void clickTowards(WebElement element, String elementName, String pageName);

    /**
     * Clicks an element if its present (in enabled state)
     *
     * @param element target element
     * @param scroll  scrolls if true
     */
    void clickIfPresent(WebElement element, boolean scroll);

    /**
     * Clicks an {element} with the {element name} on the {page name} if its present (in enabled state).
     *
     * @param element target element
     */
    void clickIfPresent(WebElement element);

    /**
     * Fills the specified input WebElement with the given text.
     *
     * @param inputElement The WebElement representing the input field.
     * @param elementName  The target element name.
     * @param pageName     The specified page instance name.
     * @param inputText    The text to be entered into the input field.
     * @param clear        If true, clears the input field before entering text. If false, does not clear.
     * @param verify       If true, verifies that the entered text matches the value attribute of the inputElement. If false, skips verification.
     * @throws TimeoutException if the inputElement is not visible within the specified timeout.
     * @throws AssertionError   if verification fails (inputText does not match the value attribute of inputElement).
     */
    void fillInputElement(WebElement inputElement, String elementName, String pageName, String inputText, boolean clear, boolean verify);

    /**
     * Clears and fills a given input without scroll option.
     *
     * @param inputElement target input element
     * @param elementName  target element name
     * @param pageName     specified page instance name
     * @param inputText    input text
     * @param verify       verifies the input text value equals to an expected text if true
     */
    void clearFillInput(WebElement inputElement, String elementName, String pageName, String inputText, boolean verify);

    /**
     * Verifies a given element is in expected state
     *
     * @param element target element
     * @param state   expected state
     * @return returns the element if its in expected state
     */
    WebElement verifyElementState(WebElement element, ElementState state);

    /**
     * Verifies a given element is in expected state
     *
     * @param element     target element
     * @param elementName target element name
     * @param pageName    specified page instance name
     * @param state       expected state
     * @return returns the element if its in expected state
     */
    WebElement verifyElementState(WebElement element, String elementName, String pageName, ElementState state);

    /**
     * Waits until a given element is in expected state
     *
     * @param element target element
     * @param state   expected state
     * @return returns true if an element is in the expected state
     */
    Boolean elementIs(WebElement element, @NotNull ElementState state);

    /**
     * Waits until a given element is in expected state
     *
     * @param element     target element
     * @param elementName target element name
     * @param pageName    specified page instance name
     * @param state       expected state
     * @return returns true if an element is in the expected state
     */
    Boolean elementIs(WebElement element, String elementName, String pageName, @NotNull ElementState state);

    /**
     * Acquire listed element by its name
     *
     * @param items         list that includes target element
     * @param selectionName element name
     * @param pageName      specified page instance name
     * @return returns the selected element
     */
    WebElement acquireNamedElementAmongst(List<WebElement> items, String selectionName, String pageName);

    /**
     * Acquire a listed element by its attribute
     *
     * @param items          list that includes target element
     * @param attributeName  attribute name
     * @param attributeValue attribute value
     * @param pageName       specified page instance name
     * @return returns the selected element
     */
    WebElement acquireElementUsingAttributeAmongst(List<WebElement> items, String attributeName, String attributeValue, String pageName);

    /**
     * Clicks an element acquired by text with optional scrolling.
     *
     * @param buttonText target element text
     * @param scroll     scrolls if true
     */
    void clickByText(String buttonText, boolean scroll);

    /**
     * Clears an input element
     *
     * @param element target element
     */
    WebElement clearInputField(@NotNull WebElement element);

    /**
     * Clears an input {element} with the {element name} on the {page name}.
     *
     * @param element     target element
     * @param elementName target element name
     * @param pageName    specified page instance name
     */
    WebElement clearInputField(@NotNull WebElement element, String elementName, String pageName);

    /**
     * Acquires an element by its text
     *
     * @param elementText target element text
     */
    WebElement getElementByText(String elementText);

    /**
     * Acquires an {element} by its text on the {page name}.
     *
     * @param elementText target element text
     * @param pageName    specified page instance name
     */
    WebElement getElementByText(String elementText, String pageName);

    /**
     * Acquires an element that contains a certain text
     *
     * @param elementText target element text
     */
    WebElement getElementContainingText(String elementText);

    /**
     * Acquires an {element} that contains a certain text on the {page name}.
     *
     * @param elementText target element text
     * @param pageName    specified page instance name
     */
    WebElement getElementContainingText(String elementText, String pageName);

    /**
     * Drags and drops a given element on top of another element
     *
     * @param element            element that drags
     * @param destinationElement target element
     */
    void dragDropToAction(WebElement element, WebElement destinationElement);

    /**
     * Drags and drops a given element on top of another element
     *
     * @param element                element that drags
     * @param elementName            target element name
     * @param destinationElement     target element
     * @param destinationElementName target destination element name
     * @param pageName               specified page instance name
     */
    void dragDropToAction(WebElement element, String elementName, WebElement destinationElement, String destinationElementName, String pageName);

    /**
     * Drags and drops a given element to coordinates specified by offsets from the center of the element
     *
     * @param element target element
     * @param xOffset x offset from the center of the element
     * @param yOffset y offset from the center of the element
     */
    //This method performs click, hold, dragAndDropBy action on at a certain offset
    void dragDropByAction(WebElement element, int xOffset, int yOffset);

    /**
     * Drags and drops a given element to coordinates specified by offsets from the center of the element
     *
     * @param element     target element
     * @param elementName target element name
     * @param pageName    specified page instance name
     * @param xOffset     x offset from the center of the element
     * @param yOffset     y offset from the center of the element
     */
    //This method performs click, hold, dragAndDropBy action on at a certain offset
    void dragDropByAction(WebElement element, String elementName, String pageName, int xOffset, int yOffset);

    /**
     * Drags and drops a given element to coordinates specified by offsets from the center of the element
     * Uses moveToElement()
     *
     * @param element target element
     * @param xOffset x offset from the center of the element
     * @param yOffset y offset from the center of the element
     */
    void dragDropAction(WebElement element, int xOffset, int yOffset);

    /**
     * Drags and drops a given element to coordinates specified by offsets from the center of the element
     * Uses moveToElement()
     *
     * @param element     target element
     * @param elementName target element name
     * @param pageName    specified page instance name
     * @param xOffset     x offset from the center of the element
     * @param yOffset     y offset from the center of the element
     */
    void dragDropAction(WebElement element, String elementName, String pageName, int xOffset, int yOffset);

    /**
     * Clicks coordinates specified by the given offsets from the center of a given element
     *
     * @param element target element
     * @param xOffset x offset from the center of the element
     * @param yOffset y offset from the center of the element
     */
    @SuppressWarnings("SameParameterValue")
    void clickAtAnOffset(WebElement element, int xOffset, int yOffset);

    /**
     * Clicks coordinates specified by the given offsets from the center of a given element
     *
     * @param element     target element
     * @param elementName target element name
     * @param pageName    specified page instance name
     * @param xOffset     x offset from the center of the element
     * @param yOffset     y offset from the center of the element
     */
    @SuppressWarnings("SameParameterValue")
    void clickAtAnOffset(WebElement element, String elementName, String pageName, int xOffset, int yOffset);

    /**
     * Uploads a given file
     *
     * @param fileUploadInput upload element
     * @param directory       absolute file directory (excluding the file name)
     * @param fileName        file name (including a file extension)
     */
    void uploadFile(@NotNull WebElement fileUploadInput, String directory, String fileName);

    /**
     * Uploads a given file
     *
     * @param fileUploadInput upload element
     * @param elementName     target element name
     * @param pageName        specified page instance name
     * @param directory       absolute file directory (excluding the file name)
     * @param fileName        file name (including a file extension)
     */
    void uploadFile(@NotNull WebElement fileUploadInput, String elementName, String pageName, String directory, String fileName);

    /**
     * Combines the given keys
     *
     * @param keys key inputs
     */
    String combineKeys(Keys... keys);

    /**
     * Waits for a certain while
     *
     * @param seconds duration as a double
     */
    //This method makes the thread wait for a certain while
    static void waitFor(double seconds) {
        Utilities.waitFor(seconds);
    };

    /**
     * Scrolls an element to the center of the view
     *
     * @param element target element
     * @return returns the targeted element
     */
    //This method scrolls an element to the center of the view
    WebElement centerElement(WebElement element);

    /**
     * Scrolls an element to the center of the view
     *
     * @param element     target element
     * @param elementName target element name
     * @param pageName    specified page instance name
     * @return returns the targeted element
     */
    //This method scrolls an element to the center of the view
    WebElement centerElement(WebElement element, String elementName, String pageName);

    /**
     * Scrolls in a given direction
     *
     * @param direction target direction (UP or DOWN)
     */
    void scrollOrSwipeInDirection(@NotNull Direction direction);

    /**
     * Gets the parent class from a child element using a selector class
     *
     * @param childElement        element that generates the parent class
     * @param current             empty string (at the beginning)
     * @param parentSelectorClass selector class for selecting the parent elements
     * @return returns the targeted parent element
     */
    WebElement getParentByClass(WebElement childElement, String current, String parentSelectorClass);

    /**
     * Gets the parent class from a child element using a selector class
     *
     * @param childElement        element that generates the parent class
     * @param elementName         target element name
     * @param pageName            specified page instance name
     * @param current             empty string (at the beginning)
     * @param parentSelectorClass selector class for selecting the parent elements
     * @return returns the targeted parent element
     */
    WebElement getParentByClass(WebElement childElement, String elementName, String pageName, String current, String parentSelectorClass);

    /**
     * Generates a xPath for a given element
     *
     * @param childElement web element gets generated a xPath from
     * @param current      empty string (at the beginning)
     * @return returns generated xPath
     */
    String generateXPath(@NotNull WebElement childElement, String current);

    /**
     * Generates a xPath for a given element
     *
     * @param childElement web element gets generated a xPath from
     * @param elementName  target element name
     * @param pageName     specified page instance name
     * @param current      empty string (at the beginning)
     * @return returns generated xPath
     */
    String generateXPath(@NotNull WebElement childElement, String elementName, String pageName, String current);

    /**
     * Acquires attribute {attribute name} from element {element name} on the {page name}
     * (Use 'innerHTML' attributeName to acquire text on an element)
     *
     * @param element       target element
     * @param attributeName acquired attribute name
     * @param elementName   target element name
     * @param pageName      specified page instance name
     */
    void saveAttributeValue(WebElement element, String attributeName, String elementName, String pageName);

    /**
     * Fills input form
     *
     * @param bundles  list of bundles where input element, input text and boolean value
     *                 (true - if the input text value equals to an expected text) are stored
     * @param pageName specified page instance name
     */
    void fillForm(List<Bundle<WebElement, String, String>> bundles, String pageName);

    /**
     * Verifies the text of {element name} on the {page name} to be: {expected text}
     *
     * @param element      target element
     * @param elementName  target element name
     * @param pageName     specified page instance name
     * @param expectedText expected text
     */
    void verifyText(WebElement element, String elementName, String pageName, String expectedText);


    /**
     * Verifies the text of {element name} on the {page name} to contain: {expected text}
     *
     * @param element      target element
     * @param elementName  target element name
     * @param pageName     specified page instance name
     * @param expectedText expected text
     */
    void verifyContainsText(WebElement element, String elementName, String pageName, String expectedText);

    /**
     * Verifies the text of an element from the list on the {page name}.
     *
     * @param bundles  list of bundles where element text, element name and expected text are stored
     * @param pageName specified page instance name
     */
    void verifyListedText(List<Bundle<WebElement, String, String>> bundles, String pageName);

    /**
     * Verifies the presence of an element {element name} on the {page name}
     *
     * @param element     target element
     * @param elementName target element name
     * @param pageName    specified page instance name
     */
    void verifyPresence(WebElement element, String elementName, String pageName);

    /**
     * Waits for absence of an element {element name} on the {page name}
     *
     * @param element     target element
     * @param elementName target element name
     * @param pageName    specified page instance name
     */
    void waitUntilAbsence(WebElement element, String elementName, String pageName);

    /**
     * Waits for element {element name} on the {page name} to be visible
     *
     * @param element     target element
     * @param elementName target element name
     * @param pageName    specified page instance name
     */
    void waitUntilVisible(WebElement element, String elementName, String pageName);

    /**
     * Waits until an element {element name} on the {page name} has {attribute value} value for its {attribute name} attribute
     *
     * @param element        target element
     * @param elementName    target element name
     * @param pageName       specified page instance name
     * @param attributeValue expected attribute value
     * @param attributeName  target attribute name
     */
    void waitUntilElementContainsAttribute(WebElement element, String elementName, String pageName, String attributeName, String attributeValue);

    /**
     * Verifies that element {element name} on the {page name} has {attribute value} value for its {attribute name} attribute
     *
     * @param element        target element
     * @param elementName    target element name
     * @param pageName       specified page instance name
     * @param attributeValue expected attribute value
     * @param attributeName  target attribute name
     */
    void verifyElementContainsAttribute(WebElement element, String elementName, String pageName, String attributeName, String attributeValue);

    /**
     * Verifies that an attribute {attribute name} of element {element name} on the {page name} contains a specific {value}.
     *
     * @param attributeName the name of the attribute to be verified
     * @param elementName   the name of the element to be verified
     * @param pageName      the name of the page containing the element
     * @param value         the expected part of value of the attribute
     */
    void verifyElementAttributeContainsValue(WebElement element, String attributeName, String elementName, String pageName, String value);

    /**
     * Verifies {attribute name} css attribute of an element {element name} on the {page name} is {attribute value}
     *
     * @param element        target element
     * @param attributeName  target attribute name
     * @param elementName    target attribute name
     * @param pageName       specified page instance name
     * @param attributeValue expected attribute value
     */
    void verifyElementColor(WebElement element, String attributeName, String elementName, String pageName, String attributeValue);

    /**
     * Verifies the presence of listed element from a list on the {page name}
     *
     * @param bundles   list that contains element, elementName, elementText
     * @param pageName  specified page instance name
     * @param signForms table that has key as "Input" and value as "Input Element" (dataTable.asMaps())
     */
    void verifyPresenceOfListedElements(List<Bundle<WebElement, String, String>> bundles, WebElement element, List<WebElement> elements, String pageName, List<Map<String, String>> signForms);

    /**
     * Updates context {key} -> {value}
     *
     * @param key   Context key
     * @param value Context value
     */
    void updateContext(String key, String value);

    /**
     * Presses {target key} key on {element name} element of the {}
     *
     * @param keys        target key
     * @param elementName target element name
     * @param pageName    specified page instance name
     */
    void pressKey(WebElement element, String elementName, String pageName, Keys... keys);

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
    void fillInputWithFile(WebElement inputElement, String inputName, String pageName, String absoluteFilePath);

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
    void bundleInteraction(List<Bundle<String, WebElement, Map<String, String>>> bundles, String pageName);

    /**
     * Scrolls through a list of elements until an element with the specified text is found and displayed.
     * Uses a provided list of elements to perform the scroll action.
     *
     * @param elementText The text of the element to be found.
     * @param elements    The list of elements to scroll through.
     * @return WebElement representing the found element, or null if not found within the specified time.
     */
    WebElement scrollInList(String elementText, List<WebElement> elements);
}
