package pickleib.utilities.helpers;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;
import pickleib.utilities.Utilities;

public class DragDropHelper {

    private final RemoteWebDriver driver;

    public DragDropHelper(RemoteWebDriver driver) {
        this.driver = driver;
    }

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

    public void dragDropByAction(WebElement element, int xOffset, int yOffset) {
        Actions action = new Actions(driver);
        action.moveToElement(element)
                .clickAndHold(element)
                .dragAndDropBy(element, xOffset, yOffset)
                .build()
                .perform();
        Utilities.waitFor(0.5);
    }

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
