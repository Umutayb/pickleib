package pickleib.utilities;

import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.*;
import pickleib.enums.Direction;
import pickleib.enums.ElementState;
import java.util.List;

@SuppressWarnings("unused")
public interface PolymorphicUtilities {

    /**
     *
     * Clicks a button by its {text} text
     *
     * @param text target text
     */
    void clickByText(String text);

    /**
     * Acquires a specified attribute of a given element
     *
     * @param element target element
     * @param attribute target attribute
     * @return returns the element attribute
     */
    String getAttribute(WebElement element, String attribute);

    /**
     * Clicks an element after waiting for its state to be enabled
     *
     * @param element target element
     */
    void clickElement(WebElement element);

    /**
     * Clicks an element after waiting for its state to be enabled
     *
     * @param element target element
     * @param scroll scrolls if true
     */
    void clickElement(WebElement element, boolean scroll);

    /**
     * Clicks an element after waiting for its state to be enabled
     *
     * @param element target element
     */
    void clickElement(WebElement element, String buttonName, String pageName);

    /**
     * Click coordinates specified by the given offsets from the center of a given element
     *
     * @param element target element
     */
    void clickTowards(WebElement element);

    /**
     * Clicks an element if its present (in enabled state)
     *
     * @param element target element
     * @param scroll scrolls if true
     */
    void clickIfPresent(WebElement element, Boolean scroll);

    /**
     * Clears and fills a given input
     *
     * @param inputElement target input element
     * @param inputText input text
     * @param scroll scrolls if true
     * @param verify verifies the input text value equals to an expected text if true
     */
    void clearFillInput(WebElement inputElement, String inputText, boolean scroll, boolean verify);

    /**
     * Verifies a given element is in expected state
     *
     * @param element target element
     * @param state expected state
     * @return returns the element if its in expected state
     */
    WebElement verifyElementState(WebElement element, ElementState state);

    /**
     * Waits until a given element is in expected state
     *
     * @param element target element
     * @param state expected state
     * @return returns true if an element is in the expected state
     */
    Boolean elementIs(WebElement element, @NotNull ElementState state);

    /**
     * Hovers cursor over of a given element
     *
     * @param element target element
     * @return returns the selected element
     */
    WebElement hoverOver(WebElement element);

    /**
     * Acquire a component amongst a list of components by its name
     *
     * @param items list of components
     * @param selectionName component name
     * @return returns the selected component
     */
    <T> T acquireNamedComponentAmongst(List<T> items, String selectionName);

    /**
     * Acquire listed element by its name
     *
     * @param items list that includes target element
     * @param selectionName element name
     * @return returns the selected element
     */
    WebElement acquireNamedElementAmongst(List<WebElement> items, String selectionName);

    /**
     * Acquire a listed element by its attribute
     *
     * @param items list that includes target element
     * @param attributeName attribute name
     * @param attributeValue attribute value
     * @return returns the selected element
     */
    WebElement acquireElementUsingAttributeAmongst(List<WebElement> items, String attributeName, String attributeValue);

    /**
     * Clicks an element acquired by text
     *
     * @param buttonText target element text
     * @param scroll scrolls if true
     */
    void clickButtonWithText(String buttonText, Boolean scroll);

    /**
     * Clears an input element
     *
     * @param element target element
     */
    WebElement clearInputField(@NotNull WebElement element);

    /**
     * Acquires an element by its text
     *
     * @param elementText target element text
     */
    WebElement getElementByText(String elementText);

    /**
     * Acquires an element that contains a certain text
     *
     * @param elementText target element text
     */
    WebElement getElementContainingText(String elementText);

    /**
     * Drags and drops a given element on top of another element
     *
     * @param element element that drags
     * @param destinationElement target element
     */
    void dragDropToAction(WebElement element, WebElement destinationElement);

    /**
     * Drags a given element to coordinates specified by offsets from the center of the element
     *
     * @param element target element
     * @param xOffset x offset from the center of the element
     * @param yOffset y offset from the center of the element
     */
    //This method performs click, hold, dragAndDropBy action on at a certain offset
    void dragDropByAction(WebElement element, int xOffset, int yOffset);

    /**
     * Drags a given element to coordinates specified by offsets from the center of the element
     * Uses moveToElement()
     *
     * @param element target element
     * @param xOffset x offset from the center of the element
     * @param yOffset y offset from the center of the element
     */
    void dragDropAction(WebElement element, int xOffset, int yOffset);

    /**
     * Click coordinates specified by the given offsets from the center of a given element
     *
     * @param element target element
     * @param xOffset x offset from the center of the element
     * @param yOffset y offset from the center of the element
     */
    @SuppressWarnings("SameParameterValue")
    void clickAtAnOffset(WebElement element, int xOffset, int yOffset);

    /**
     * Uploads a given file
     *
     * @param fileUploadInput upload element
     * @param directory absolute file directory (excluding the file name)
     * @param fileName file name (including a file extension)
     */
    void uploadFile(@NotNull WebElement fileUploadInput, String directory, String fileName);

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
    void waitFor(double seconds);

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
     * @param element target element
     * @return returns the targeted element
     */
    //This method scrolls an element to the center of the view
    WebElement centerElement(WebElement element, String elementName, String pageName);

    /**
     * Scroll in a given direction
     *
     * @param direction target direction (UP or DOWN)
     */
    void scroll(@NotNull Direction direction);

    /**
     * Gets the parent class from a child element using a selector class
     *
     * @param childElement element that generates the parent class
     * @param current empty string (at the beginning)
     * @param parentSelectorClass selector class for selecting the parent elements
     * @return returns the targeted parent element
     */
    WebElement getParentByClass(WebElement childElement, String current, String parentSelectorClass);

    /**
     * Generate a xPath for a given element
     *
     * @param childElement web element gets generated a xPath from
     * @param current empty string (at the beginning)
     * @return returns generated xPath
     */
    String generateXPath(@NotNull WebElement childElement, String current);
}
