package pickleib.utilities.steps;

import com.google.gson.JsonObject;
import pickleib.driver.DriverFactory;
import pickleib.mobile.driver.PickleibAppiumDriver;
import pickleib.utilities.element.ElementAcquisition;
import pickleib.web.driver.PickleibWebDriver;
import utils.Printer;

public class PageJsonStepUtilities {
    public Printer log = new Printer(this.getClass());
    ElementAcquisition.PageObjectJson pageJson;
    ElementAcquisition.PageObjectJson screenJson;

    public PageJsonStepUtilities(JsonObject pageJson){
        this.pageJson = new ElementAcquisition.PageObjectJson(PickleibWebDriver.get(), pageJson);
        this.screenJson = new ElementAcquisition.PageObjectJson(PickleibAppiumDriver.get(), pageJson);
    }

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
