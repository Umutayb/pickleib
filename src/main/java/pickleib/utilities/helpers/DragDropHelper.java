package pickleib.utilities.helpers;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;
import pickleib.utilities.Utilities;

/**
 * Encapsulates drag-and-drop interactions with WebElements.
 * Provides methods for dragging to a target element or to a coordinate offset.
 */
public class DragDropHelper {

    private final RemoteWebDriver driver;

    /**
     * Constructs a DragDropHelper with the given WebDriver instance.
     *
     * @param driver the RemoteWebDriver instance
     */
    public DragDropHelper(RemoteWebDriver driver) {
        this.driver = driver;
    }

    /**
     * Drags the source element and drops it onto the destination element.
     *
     * @param element             the element to drag
     * @param destinationElement  the target element to drop onto
     */
    public void dragDropToAction(WebElement element, WebElement destinationElement) {
        Actions action = new Actions(driver);
        action.moveToElement(element)
                .clickAndHold(element)
                .moveToElement(destinationElement)
                .release()
                .build()
                .perform();
        Utilities.waitFor(0.5);
    }

    /**
     * Drags the element by the given pixel offset using {@code dragAndDropBy}.
     *
     * @param element  the element to drag
     * @param xOffset  horizontal offset in pixels
     * @param yOffset  vertical offset in pixels
     */
    public void dragDropByAction(WebElement element, int xOffset, int yOffset) {
        Actions action = new Actions(driver);
        action.moveToElement(element)
                .clickAndHold(element)
                .dragAndDropBy(element, xOffset, yOffset)
                .build()
                .perform();
        Utilities.waitFor(0.5);
    }

    /**
     * Drags the element to a position offset from its own center and releases it.
     *
     * @param element  the element to drag
     * @param xOffset  horizontal offset in pixels from the element's center
     * @param yOffset  vertical offset in pixels from the element's center
     */
    public void dragDropAction(WebElement element, int xOffset, int yOffset) {
        Actions action = new Actions(driver);
        action.moveToElement(element)
                .clickAndHold(element)
                .moveToElement(element, xOffset, yOffset)
                .release()
                .build()
                .perform();
        Utilities.waitFor(0.5);
    }
}
