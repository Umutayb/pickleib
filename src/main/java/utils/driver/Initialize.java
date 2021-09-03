package utils.driver;

import io.cucumber.core.api.Scenario;
import utils.PropertiesReader;
import utils.StringUtilities;
import utils.Utilities;

public class Initialize extends Utilities {

    PropertiesReader reader = new PropertiesReader("properties-from-pom.properties");
    StringUtilities strUtils = new StringUtilities();

    public void init(){
        String browser = reader.getProperty("browser");
        setup(strUtils.firstLetterCapped(browser));
    }

    public void kill(Scenario scenario){
        if (scenario.isFailed())
            captureScreen(scenario.getName()+"@"+scenario.getLine());
        teardown();
    }
}
