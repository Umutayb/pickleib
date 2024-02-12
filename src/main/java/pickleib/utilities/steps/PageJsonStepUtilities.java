package pickleib.utilities.steps;

import com.google.gson.JsonObject;
import pickleib.driver.DriverFactory;
import pickleib.mobile.driver.PickleibAppiumDriver;
import pickleib.utilities.element.ElementAcquisition;
import pickleib.web.driver.PickleibWebDriver;
import utils.Printer;

public class PageJsonStepUtilities {
//    public DriverFactory.DriverType defaultPlatform = DriverFactory.DriverType.Web;
//    public WebInteractions webInteractions = new WebInteractions();
//    public MobileInteractions mobileInteractions = new MobileInteractions();
    public Printer log = new Printer(this.getClass());
    ElementAcquisition.PageObjectJson pageJson;
    ElementAcquisition.PageObjectJson screenJson;

    public PageJsonStepUtilities(JsonObject pageJson){
        this.pageJson = new ElementAcquisition.PageObjectJson(PickleibWebDriver.get(), pageJson);
        this.screenJson = new ElementAcquisition.PageObjectJson(PickleibAppiumDriver.get(), pageJson);
    }

    /**
     * Retrieves the appropriate element interactions based on the given driver type.
     *
     * @param driverType The type of the driver (Web or Mobile).
     * @return The element interactions for the specified driver type.
     */
//    public Interactions getInteractions(DriverFactory.DriverType driverType) {
//        switch (driverType) {
//            case Web -> {
//                return webInteractions;
//            }
//            case Mobile -> {
//                return mobileInteractions;
//            }
//            default -> {
//                return getInteractions(defaultPlatform);
//            }
//        }
//    }
//
//    public Interactions getInteractions(WebElement element) {
//        switch (Utilities.getElementDriverType(element)) {
//            case Web -> {
//                return webInteractions;
//            }
//            case Mobile -> {
//                return mobileInteractions;
//            }
//            default -> {
//                return getInteractions(defaultPlatform);
//            }
//        }
//    }

    public ElementAcquisition.PageObjectJson getAcquisition(DriverFactory.DriverType driverType) {
        switch (driverType) {
            case Web -> {
                return pageJson;
            }
            case Mobile -> {
                return screenJson;
            }
            default -> throw new EnumConstantNotPresentException(DriverFactory.DriverType.class, driverType.name());
        }
    }
}
