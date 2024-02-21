package pickleib.utilities.element.interactions;

import org.openqa.selenium.WebElement;
import pickleib.driver.DriverFactory;
import pickleib.mobile.interactions.MobileInteractions;
import pickleib.utilities.interfaces.PolymorphicUtilities;
import pickleib.web.interactions.WebInteractions;
import utils.Printer;
import utils.StringUtilities;

import static pickleib.driver.DriverFactory.DriverType.getGeneralType;
import static pickleib.utilities.platform.PlatformUtilities.getElementDriverType;

public class InteractionBase {
    public DriverFactory.DriverType defaultPlatform = DriverFactory.DriverType.Web;
    public Printer log = new Printer(this.getClass());
    public WebInteractions webInteractions;
    public MobileInteractions mobileInteractions;

    public InteractionBase(
            boolean mobileDriverActive,
            boolean webDriverActive) {
        if (webDriverActive) webInteractions = new WebInteractions();
        if (mobileDriverActive) mobileInteractions = new MobileInteractions();
    }

    public InteractionBase() {
        webInteractions = new WebInteractions();
        mobileInteractions = new MobileInteractions();
    }

    public PolymorphicUtilities getInteractions(DriverFactory.DriverType driverType) {
        if (!StringUtilities.isBlank(driverType))
            switch (driverType) {
                case Web -> {
                    return webInteractions;
                }
                case Mobile -> {
                    return mobileInteractions;
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
        switch (getGeneralType(getElementDriverType(element))) {
            case Web -> {
                return webInteractions;
            }
            case Mobile -> {
                return mobileInteractions;
            }
        }
        return null;
    }
}
