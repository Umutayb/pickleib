package pickleib.utilities.element.interactions;

import org.openqa.selenium.WebElement;
import pickleib.driver.DriverFactory;
import pickleib.mobile.interactions.PlatformInteractions;
import pickleib.utilities.interfaces.PolymorphicUtilities;
import pickleib.web.interactions.WebInteractions;
import utils.Printer;
import utils.StringUtilities;

import static pickleib.utilities.platform.PlatformUtilities.*;

public class InteractionBase {
    public DriverFactory.DriverType defaultPlatform = DriverFactory.DriverType.selenium;
    public Printer log = new Printer(this.getClass());
    public WebInteractions webInteractions;
    public PlatformInteractions platformInteractions;

    public InteractionBase(
            boolean platformDriverActive,
            boolean webDriverActive) {
        if (webDriverActive) webInteractions = new WebInteractions();
        if (platformDriverActive) platformInteractions = new PlatformInteractions();
    }

    public InteractionBase() {
        webInteractions = new WebInteractions();
        platformInteractions = new PlatformInteractions();
    }

    public PolymorphicUtilities getInteractions(DriverFactory.DriverType driverType) {
        if (!StringUtilities.isBlank(driverType))
            switch (driverType) {
                case selenium -> {
                    return webInteractions;
                }
                case appium -> {
                    return platformInteractions;
                }
            }
        else return getInteractions(defaultPlatform);
        return null;
    }

    /**
     * Retrieves the appropriate element interactions based on the given driver type.
     *
     * @return The element interactions for the specified driver type.
     */
    public PolymorphicUtilities getInteractions(WebElement element) {
        if (isAppiumDriver(getElementDriver(element)))
            return platformInteractions;
        else
            return webInteractions;
    }
}
