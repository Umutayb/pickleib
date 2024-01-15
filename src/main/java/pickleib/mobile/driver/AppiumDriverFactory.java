package pickleib.mobile.driver;

import context.ContextStore;
import io.appium.java_client.AppiumDriver;
import org.json.simple.JSONObject;
import org.openqa.selenium.remote.DesiredCapabilities;
import pickleib.driver.DriverFactory;
import utils.FileUtilities;
import utils.Printer;
import java.net.URL;
import java.time.Duration;

import static pickleib.mobile.driver.ServiceFactory.service;
import static pickleib.utilities.element.ElementAcquisition.strUtils;
import static utils.StringUtilities.Color.*;

public class AppiumDriverFactory implements DriverFactory {

    static Printer log = new Printer(AppiumDriverFactory.class);
    static FileUtilities.Json jsonUtils = new FileUtilities.Json();

    public static AppiumDriver getDriver(String deviceName, JSONObject capabilities){
        DesiredCapabilities desiredCapabilities = getConfig(capabilities);
        desiredCapabilities.setCapability("app", strUtils.contextCheck("UPLOAD-" + capabilities.get("app")));
        try {
            URL url;
            if (service == null) {
                String address = ContextStore.get("address", "0.0.0.0");
                String port = ContextStore.get("port", "4723");
                url = new URL("http://" + address + ":" + port + "/wd/hub");
            }
            else url = service.getUrl();

            AppiumDriver driver = new AppiumDriver(url, desiredCapabilities);
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
            log.important(deviceName + strUtils.markup(GRAY, " was selected"));
            return driver;
        }
        catch (Exception gamma) {
            if(gamma.toString().contains("Could not start a new session. Possible causes are invalid address of the remote server or browser start-up failure")){
                log.info("Please make sure " + strUtils.markup(PURPLE, "Appium ") + "is on & verify the port that its running on at 'resources/test.properties'.");
                throw new RuntimeException(strUtils.markup(YELLOW, gamma.getMessage()));
            }
            else throw new RuntimeException(strUtils.markup(YELLOW, "Something went wrong while selecting a driver") + "\n" + strUtils.markup(RED, gamma.getMessage()));
        }
    }

    public static DesiredCapabilities getConfig(JSONObject capabilities) {
        log.info("Setting capabilities...");
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        for (Object key : capabilities.keySet()) desiredCapabilities.setCapability((String) key, capabilities.get(key));
        return desiredCapabilities;
    }
}
