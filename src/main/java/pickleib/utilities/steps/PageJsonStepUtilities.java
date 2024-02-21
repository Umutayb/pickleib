package pickleib.utilities.steps;

import com.google.gson.JsonObject;
import pickleib.utilities.element.acquisition.ElementAcquisition;
import pickleib.utilities.element.interactions.InteractionBase;
import pickleib.web.driver.PickleibWebDriver;

public class PageJsonStepUtilities extends InteractionBase {

    public ElementAcquisition.PageObjectJson pageJson;

    public PageJsonStepUtilities(JsonObject pageJson){
        super();
        this.pageJson = new ElementAcquisition.PageObjectJson(PickleibWebDriver.get(), pageJson);
    }

    public PageJsonStepUtilities(
            JsonObject pageJson,
            boolean mobileDriverActive,
            boolean webDriverActive) {
        super(mobileDriverActive, webDriverActive);
        this.pageJson = new ElementAcquisition.PageObjectJson(PickleibWebDriver.get(), pageJson);
    }
}
