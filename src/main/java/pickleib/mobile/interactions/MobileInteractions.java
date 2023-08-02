package pickleib.mobile.interactions;

import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import pickleib.driver.DriverFactory;
import pickleib.enums.SwipeDirection;
import pickleib.mobile.driver.PickleibAppiumDriver;
import pickleib.mobile.utilities.MobileUtilities;
import pickleib.utilities.element.ElementInteractions;

import static utils.StringUtilities.Color.*;

@SuppressWarnings("unused")
public class MobileInteractions extends MobileUtilities {

    public ElementInteractions interact;
    protected RemoteWebDriver driver;
    protected WebDriverWait wait;

    public MobileInteractions(RemoteWebDriver driver, WebDriverWait wait){
        super(driver);
        this.driver = driver;
        this.wait = wait;
        interact = new ElementInteractions(
                driver,
                wait,
                DriverFactory.DriverType.Mobile
        );
    }

    public MobileInteractions(){
        super(PickleibAppiumDriver.driver);
        this.driver = PickleibAppiumDriver.driver;
        this.wait = PickleibAppiumDriver.wait;
        interact = new ElementInteractions(
                driver,
                wait,
                DriverFactory.DriverType.Mobile
        );
    }

    /**
     *
     * Scroll {direction}
     *
     * @param direction target direction (up or down)
     */
    public void swipeInDirection(SwipeDirection direction){swiper(direction);}

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
     * Closes the browser
     */
    public void quitDriver(){
        driver.quit();
    }
}