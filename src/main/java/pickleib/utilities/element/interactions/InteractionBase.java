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
        if (webInteractions == null && platformInteractions == null)
            log.warning("Neither web nor platform interactions are instantiated!");
        if (!StringUtilities.isBlank(driverType))
            switch (driverType) {
                case selenium -> {
                    if (webInteractions == null)
                        log.warning("Web interactions requested without beings instantiated!");
                    return webInteractions;
                }
                case appium -> {
                    if (platformInteractions == null)
                        log.warning("Platform interactions requested without beings instantiated!");
                    return platformInteractions;
                }
                default -> throw new EnumConstantNotPresentException(DriverFactory.DriverType.class, driverType.name());
            }
        else return getInteractions(defaultPlatform);
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
