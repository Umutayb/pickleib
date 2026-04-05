package pickleib.utilities.helpers;

import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import pickleib.enums.ElementState;
import pickleib.exceptions.PickleibException;
import pickleib.utilities.RetryPolicy;
import pickleib.utilities.element.ElementBundle;
import utils.Printer;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

import static pickleib.enums.ElementState.absent;
import static pickleib.enums.ElementState.displayed;
import static utils.StringUtilities.*;
import static utils.StringUtilities.Color.*;

/**
 * Encapsulates element state checking and verification methods.
 * Extracted from {@link pickleib.utilities.Utilities} to keep the base class focused.
 */
public class ElementStateHelper {

    private final RemoteWebDriver driver;
    private final long elementTimeout;
    private final long driverTimeout;
    private final Printer log = new Printer(ElementStateHelper.class);

    /**
     * Constructs an ElementStateHelper with the required WebDriver dependencies.
     *
     * @param driver         the RemoteWebDriver instance
     * @param elementTimeout maximum time in milliseconds to poll for element state changes
     * @param driverTimeout  the default implicit wait timeout in seconds used to restore after polling
     */
    public ElementStateHelper(RemoteWebDriver driver, long elementTimeout, long driverTimeout) {
        this.driver = driver;
        this.elementTimeout = elementTimeout;
        this.driverTimeout = driverTimeout;
    }

    /**
     * Waits until a given element is in expected state.
     *
     * @param element target element
     * @param state   expected state
     * @return returns true if an element is in the expected state
     */
    public Boolean elementIs(WebElement element, @NotNull ElementState state) {
        return RetryPolicy.pollUntil(
            () -> checkElementState(element, state),
            elementTimeout,
            () -> driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500)),
            () -> driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(driverTimeout)),
            ex -> state.equals(absent) && ex instanceof StaleElementReferenceException
        );
    }

    /**
     * Evaluates whether the given element satisfies the specified state without any retry logic.
     *
     * @param element the element to inspect
     * @param state   the state to check against
     * @return true if the element matches the given state, false otherwise
     * @throws EnumConstantNotPresentException if the state value is not handled
     */
    private boolean checkElementState(WebElement element, ElementState state) {
        return switch (state) {
            case enabled -> element.isEnabled();
            case displayed -> element.isDisplayed();
            case selected -> element.isSelected();
            case disabled -> !element.isEnabled();
            case unselected -> !element.isSelected();
            case absent -> !element.isDisplayed();
            default -> throw new EnumConstantNotPresentException(ElementState.class, state.name());
        };
    }

    /**
     * Verifies a given element is in expected state.
     *
     * @param element target element
     * @param state   expected state
     * @return returns the element if it is in expected state
     */
    public WebElement verifyElementState(WebElement element, ElementState state) {
        if (!elementIs(element, state)) throw new PickleibException("Element is not in " + state.name() + " state!");
        log.success("Element state is verified to be: " + state.name());
        return element;
    }

    /**
     * Verifies the text of an element equals the expected text.
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
     * Verifies the text of an element contains the expected text.
     *
     * @param element      target element
     * @param elementName  target element name
     * @param pageName     specified page instance name
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
     * Verifies the text of each element in a list of bundles.
     *
     * @param bundles  list of element bundles with expected text
     * @param pageName specified page instance name
     */
    public void verifyListedElementText(List<ElementBundle<String>> bundles, String pageName) {
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
     * Verifies that a list of elements contains an element with the given text.
     *
     * @param elements     list of elements to search
     * @param expectedText expected text to find
     * @param listName     name of the list (for logging)
     * @param pageName     specified page instance name
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
     * Verifies an element has a specific attribute value (exact match).
     *
     * @param element        target element
     * @param attributeName  target attribute name
     * @param attributeValue expected attribute value
     * @return true if the element has the attribute with the given value, false otherwise
     */
    public boolean elementContainsAttribute(
            WebElement element,
            String attributeName,
            String attributeValue) {

        attributeValue = contextCheck(attributeValue);
        final String checkedValue = attributeValue;
        boolean result = RetryPolicy.pollUntil(
            () -> Objects.equals(element.getAttribute(attributeName), checkedValue),
            elementTimeout
        );
        if (!result) {
            log.warning("Element does not contain " +
                highlighted(BLUE, attributeName) +
                highlighted(GRAY, " -> ") +
                highlighted(BLUE, checkedValue) +
                highlighted(GRAY, " attribute pair.")
            );
        }
        return result;
    }

    /**
     * Verifies that an attribute of an element contains a specific value (substring match).
     *
     * @param element       the element to check
     * @param attributeName the name of the attribute to check
     * @param value         the expected substring of the attribute value
     * @return true if the attribute contains the value, false otherwise
     */
    public boolean elementAttributeContainsValue(
            WebElement element,
            String attributeName,
            String value) {

        value = contextCheck(value);
        final String checkedValue = value;
        boolean result = RetryPolicy.pollUntil(
            () -> {
                String attr = element.getAttribute(attributeName);
                return attr != null && attr.contains(checkedValue);
            },
            elementTimeout,
            () -> driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500)),
            () -> driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(driverTimeout)),
            null
        );
        if (!result) {
            log.warning("Element attribute does not contain " +
                highlighted(BLUE, attributeName) +
                highlighted(GRAY, " -> ") +
                highlighted(BLUE, checkedValue) +
                highlighted(GRAY, " value.")
            );
        }
        return result;
    }
}
