package pickleib.mobile.interactions;

import collections.Bundle;
import context.ContextStore;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.FluentWait;
import pickleib.enums.Direction;
import pickleib.enums.ElementState;
import pickleib.enums.InteractionType;
import pickleib.exceptions.PickleibException;
import pickleib.exceptions.PickleibVerificationException;
import pickleib.mobile.driver.PickleibAppiumDriver;
import pickleib.mobile.utilities.MobileUtilities;
import pickleib.utilities.element.acquisition.ElementAcquisition;
import pickleib.utilities.interfaces.PolymorphicUtilities;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static utils.StringUtilities.Color.BLUE;
import static utils.StringUtilities.Color.GRAY;
import static utils.StringUtilities.contextCheck;
import static utils.StringUtilities.*;

@SuppressWarnings("unused")
public class PlatformInteractions extends MobileUtilities implements PolymorphicUtilities {

    public PlatformInteractions(RemoteWebDriver driver, FluentWait<RemoteWebDriver> wait) {
        super(driver);
        this.wait = wait;
    }

    public PlatformInteractions() {
        super(PickleibAppiumDriver.get());
    }

    /**
     * Scrolls in a given direction
     *
     * @param direction target direction (UP or DOWN)
     */
    public void scroll(@NotNull Direction direction) {
        log.info("Swiping in direction " + highlighted(BLUE, direction.name()));
        super.scrollInDirection(direction);
    }

    /**
     * Centers the {element name} on the {page name}
     *
     * @param element     target element
     * @param elementName target element name
     * @param pageName    specified page instance name
     * @return returns centered element
     */
    public WebElement centerElement(WebElement element, String elementName, String pageName) {
        log.info("Centering " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY, " on ") +
                highlighted(BLUE, pageName)
        );
        return super.centerElement(element);
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
    public void clickElement(WebElement element, String elementName, String pageName, boolean scroll) {
        log.info("Clicking " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY, " on ") +
                highlighted(BLUE, pageName)
        );
        super.clickElement(element, scroll);
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
        super.clickElement(element);
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
        super.clickIfPresent(element, scroll);
    }

    /**
     * Clears and fills a given input.
     *
     * @param inputElement target input element
     * @param inputName  target element name
     * @param pageName     specified page instance name
     * @param inputText    input text
     * @param verify       verifies the input text value equals to an expected text if true
     */
    public void clearFillInput(
            WebElement inputElement,
            String inputName,
            String pageName,
            String inputText,
            boolean verify) {
        inputText = contextCheck(inputText);
        log.info("Filling " +
                highlighted(BLUE, inputName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName) +
                highlighted(GRAY, " with the text: ") +
                highlighted(BLUE, inputText)
        );
        super.clearFillInput(inputElement, inputText, verify);
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
        return super.verifyElementState(element, state);
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
    public Boolean elementIs(@NotNull WebElement element, String elementName, String pageName, @NotNull ElementState state) {
        log.info("Verifying that the state of " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY, " is ") +
                highlighted(BLUE, state.name()) +
                highlighted(GRAY, " on the ") +
                highlighted(BLUE, pageName)
        );
        return super.elementIs(element, state);
    }

    /**
     * Acquires listed element by its name
     *
     * @param items         list that includes target element
     * @param selectionName element name
     * @param pageName      specified page instance name
     * @return returns the selected element
     */
    public WebElement acquireNamedElementAmongst(List<WebElement> items, String selectionName, String pageName) {
        log.info("Acquiring element " +
                highlighted(BLUE, selectionName) +
                highlighted(GRAY, " on ") +
                highlighted(BLUE, pageName)
        );
        return ElementAcquisition.acquireNamedElementAmongst(items, selectionName);
    }

    /**
     * Acquires a listed element by its attribute
     *
     * @param items          list that includes target element
     * @param attributeName  attribute name
     * @param attributeValue attribute value
     * @param pageName       specified page instance name
     * @return returns the selected element
     */
    public WebElement acquireElementUsingAttributeAmongst(List<WebElement> items, String attributeName, String attributeValue, String pageName) {
        log.info("Acquiring element using attribute " +
                highlighted(BLUE, attributeName) +
                highlighted(GRAY, " with value ") +
                highlighted(BLUE, attributeValue) +
                highlighted(GRAY, " on ") +
                highlighted(BLUE, pageName)
        );
        return ElementAcquisition.acquireElementUsingAttributeAmongst(items, attributeName, attributeValue);
    }

    /**
     * Clicks an element acquired by text with optional scrolling.
     *
     * @param buttonText target element text
     * @param scroll     scrolls if true
     */
    public void clickByText(String buttonText, boolean scroll) {
        log.info("Clicking button with text " + highlighted(BLUE, buttonText));
        super.clickButtonWithText(buttonText, scroll);
    }

    /**
     * Clicks a button by its {text} text
     *
     * @param text target text
     */
    public void clickByText(String text) {
        log.info("Clicking button by text " + highlighted(BLUE, text));
        super.clickButtonWithText(text, false);
    }

    /**
     * Acquires a specified attribute of a given {element} with the {element name} on the {page name}.
     *
     * @param element     target element
     * @param attribute   target attribute
     * @param elementName target element name
     * @param pageName    specified page instance name
     * @return returns the element attribute
     */
    public String getAttribute(WebElement element, String attribute, String elementName, String pageName) {
        log.info("Acquiring " +
                highlighted(BLUE, attribute) +
                highlighted(GRAY, " of ") +
                highlighted(BLUE, elementName) +
                highlighted(GRAY, " on ") +
                highlighted(BLUE, pageName)
        );
        return super.getAttribute(element, attribute);
    }

    /**
     * Clears an input {element} with the {element name} on the {page name}.
     *
     * @param element     target element
     * @param elementName target element name
     * @param pageName    specified page instance name
     */
    public WebElement clearInputField(@NotNull WebElement element, String elementName, String pageName) {
        log.info("Clearing " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY, " on ") +
                highlighted(BLUE, pageName)
        );
        return super.clearInputField(element);
    }

    /**
     * Acquires an  {element} by its text on the {page name}.
     *
     * @param elementText target element text
     * @param pageName    specified page instance name
     */
    public WebElement getElementByText(String elementText, String pageName) {
        log.info("Acquiring element by text " +
                highlighted(BLUE, elementText) +
                highlighted(GRAY, " on ") +
                highlighted(BLUE, pageName)
        );
        return super.getElementByText(elementText);
    }

    /**
     * Acquires an {element} that contains a certain text on the {page name}.
     *
     * @param elementText target element text
     * @param pageName    specified page instance name
     */
    public WebElement getElementContainingText(String elementText, String pageName) {
        log.info("Acquiring element by containing text " +
                highlighted(BLUE, elementText) +
                highlighted(GRAY, " on ") +
                highlighted(BLUE, pageName)
        );
        return super.getElementContainingText(elementText);
    }

    /**
     * Drags and drops a given element on top of another element
     *
     * @param element                element that drags
     * @param elementName            target element name
     * @param destinationElement     target element
     * @param destinationElementName target destination element name
     * @param pageName               specified page instance name
     */
    public void dragDropToAction(WebElement element, String elementName, WebElement destinationElement, String destinationElementName, String pageName) {
        log.info("Drag drop from " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY, " to ") +
                highlighted(BLUE, destinationElementName) +
                highlighted(GRAY, " on ") +
                highlighted(BLUE, pageName)
        );
        super.dragDropToAction(element, destinationElement);
    }

    /**
     * Drags and drops a given element to coordinates specified by offsets from the center of the element
     *
     * @param element     target element
     * @param elementName target element name
     * @param pageName    specified page instance name
     * @param xOffset     x offset from the center of the element
     * @param yOffset     y offset from the center of the element
     */
    public void dragDropByAction(WebElement element, String elementName, String pageName, int xOffset, int yOffset) {
        log.info("Drag drop " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY, " to point (") +
                highlighted(BLUE, String.valueOf(xOffset)) +
                highlighted(GRAY, "; ") +
                highlighted(BLUE, String.valueOf(yOffset)) +
                highlighted(GRAY, ") ") +
                highlighted(GRAY, " on ") +
                highlighted(BLUE, pageName)
        );
        super.dragDropByAction(element, xOffset, yOffset);
    }

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
    public void dragDropAction(WebElement element, String elementName, String pageName, int xOffset, int yOffset) {
        log.info("Drag drop " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY, " to point (") +
                highlighted(BLUE, String.valueOf(xOffset)) +
                highlighted(GRAY, "; ") +
                highlighted(BLUE, String.valueOf(yOffset)) +
                highlighted(GRAY, ") ") +
                highlighted(GRAY, " on ") +
                highlighted(BLUE, pageName)
        );
        super.dragDropAction(element, xOffset, yOffset);
    }

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
    public void clickAtAnOffset(WebElement element, String elementName, String pageName, int xOffset, int yOffset) {
        log.info("Clicking " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY, " at point (") +
                highlighted(BLUE, String.valueOf(xOffset)) +
                highlighted(GRAY, "; ") +
                highlighted(BLUE, String.valueOf(yOffset)) +
                highlighted(GRAY, ") ") +
                highlighted(GRAY, " on ") +
                highlighted(BLUE, pageName)
        );
        super.clickAtAnOffset(element, xOffset, yOffset);
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

    /**
     * Combines the given keys
     *
     * @param keys key inputs
     */
    public String combineKeys(Keys... keys) {
        log.info("Combining keys: " + Arrays.toString(keys));
        return super.combineKeys(keys);
    }

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
    public WebElement getParentByClass(WebElement childElement, String elementName, String pageName, String current, String parentSelectorClass) {
        log.info("Acquiring parent class from " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY, " using ") +
                highlighted(BLUE, parentSelectorClass) +
                highlighted(GRAY, " on ") +
                highlighted(BLUE, pageName)
        );
        return super.getParentByClass(childElement, current, parentSelectorClass);
    }

    /**
     * Generates a xPath for a given element
     *
     * @param childElement web element gets generated a xPath from
     * @param elementName  target element name
     * @param pageName     specified page instance name
     * @param current      empty string (at the beginning)
     * @return returns generated xPath
     */
    public String generateXPath(@NotNull WebElement childElement, String elementName, String pageName, String current) {
        log.info("Generating XPath for " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY, " on ") +
                highlighted(BLUE, pageName)
        );
        String xpath = super.generateXPath(childElement, current);
        log.success("XPath was generated for " + elementName + " of " + pageName + ": " + xpath);
        return xpath;
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
        log.success("Attribute " + attributeName + " of " + elementName + " from " + pageName + " was saved to context!");
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
        super.fillInputForm(bundles, pageName);
        log.success("Form was filled on " + pageName);
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
        log.success("Text of element " + elementName + " is " + expectedText + " on " + pageName);
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
        super.verifyElementContainsText(element, expectedText);
        log.success("Text of element " + elementName + " contains " + expectedText + " on " + pageName);
    }

    /**
     * Verifies the text of an element from the list on the {page name}.
     *
     * @param bundles  list of bundles where element text, element name and expected text are stored
     * @param pageName specified page instance name
     */
    public void verifyListedText(List<Bundle<WebElement, String, String>> bundles, String pageName) {
        log.info("Verifying the text of elements on " + highlighted(BLUE, pageName));
        super.verifyListedElementText(bundles, pageName);
        log.success("Text of the elements was verified on " + pageName);
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
        log.success("Presence of the element " + elementName + " was verified on " + pageName);
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
        boolean absent = super.elementIs(element, ElementState.absent);
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
        boolean visible = super.elementIs(element, ElementState.displayed);
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
        boolean attributeFound = super.elementContainsAttribute(element, attributeName, attributeValue);
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
                false,
                false
        );
    }

    /**
     * Fills the specified input WebElement with the given text.
     *
     * @param inputElement The WebElement representing the input field.
     * @param elementName  The target element name.
     * @param pageName     The specified page instance name.
     * @param inputText    The text to be entered into the input field.
     * @param scroll       If true, scrolls to the inputElement before filling. If false, does not scroll.
     * @param clear        If true, clears the input field before entering text. If false, does not clear.
     * @param verify       If true, verifies that the entered text matches the value attribute of the inputElement. If false, skips verification.
     * @throws TimeoutException if the inputElement is not visible within the specified timeout.
     * @throws AssertionError   if verification fails (inputText does not match the value attribute of inputElement).
     */
    public void fillInputElement(WebElement inputElement, String elementName, String pageName, String inputText, boolean scroll, boolean clear, boolean verify) {
        inputText = contextCheck(inputText);
        log.info("Filling " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName) +
                highlighted(GRAY, " with the text: ") +
                highlighted(BLUE, inputText)
        );
        super.fillInputElement(inputElement, inputText, scroll, clear, verify);
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
        log.info("Executing bundle interactions on " + pageName);
        bundleInteraction(bundles, pageName, true);
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
                case click -> clickElement(bundle.beta(), bundle.alpha(), pageName, scroll);
                case fill ->
                        clearFillInput(bundle.beta(), bundle.alpha(), pageName, bundle.theta().get("Input"),false);
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
}