package pickleib.utilities.steps;

import com.google.gson.JsonObject;
import pickleib.mobile.interactions.PlatformInteractions;
import pickleib.utilities.element.acquisition.design.PageObjectJson;
import pickleib.utilities.element.interactions.InteractionBase;
import pickleib.utilities.interfaces.StepUtilities;

public class PageJsonStepUtilities extends InteractionBase implements StepUtilities {

    protected PageObjectJson objectRepository;

    public PageJsonStepUtilities(JsonObject pageJson){
        super();
        this.platformInteractions = new PlatformInteractions();
    }

    public PageJsonStepUtilities(
            JsonObject pageJson,
            boolean platformDriverActive,
            boolean webDriverActive) {
        super(platformDriverActive, webDriverActive);
        this.objectRepository = new PageObjectJson(pageJson);
    }

    @Override
    public PageObjectJson getObjectRepository() {
        return objectRepository;
    }
}
