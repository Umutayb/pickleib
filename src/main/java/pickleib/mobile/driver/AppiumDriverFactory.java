package pickleib.mobile.driver;

import com.google.gson.JsonObject;
import context.ContextStore;
import io.appium.java_client.AppiumDriver;
import org.json.simple.JSONObject;
import org.openqa.selenium.remote.DesiredCapabilities;
import pickleib.driver.DriverFactory;
import utils.FileUtilities;
import utils.Printer;

import java.io.IOException;
import java.net.URL;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static pickleib.mobile.driver.ServiceFactory.service;
import static utils.StringUtilities.Color.*;
import static utils.StringUtilities.*;

public class AppiumDriverFactory implements DriverFactory {

    static Printer log = new Printer(AppiumDriverFactory.class);
    static String deviceName;

    /**
     * Checks if a given string is a valid file path.
     *
     * @param path The string representing the file path to validate.
     * @return true if the string is a valid file path, false otherwise.
     */
    public static boolean isValidFilePath(String path) {
        if (path == null || path.isEmpty()) {
            return false; // Null or empty strings are not valid file paths
        }

        try {
            // Try to create a Path object from the string
            Path filePath = Paths.get(path);

            // Check if the path exists or is accessible (optional, depends on your needs)
            filePath.toRealPath();

            return true; // Successfully resolved the path
        } catch (InvalidPathException | SecurityException | IOException e) {
            return false; // The string is not a valid file path
        }
    }

    public static AppiumDriver getDriver(String deviceName, JSONObject capabilitiesJSON, boolean remote){
        AppiumDriverFactory.deviceName = deviceName;
        DesiredCapabilities capabilities = getConfig(capabilitiesJSON);
        String urlString;
        if (remote) {
            String userName = ContextStore.get("remote-mobile-username");
            String accessKey = ContextStore.get("remote-mobile-access-key");
            String server = ContextStore.get("remote-mobile-server");
            urlString = String.format("https://%s:%s@%s/wd/hub", userName , accessKey, server);
        }
        else {
            String address = ContextStore.get("address", "0.0.0.0");
            String port = ContextStore.get("port", "4723");
            urlString = "http://" + address + ":" + port + "/wd/hub";
            String appCapability = isValidFilePath(String.valueOf(capabilitiesJSON.get("app"))) ?
                    FileUtilities.getAbsolutePath(String.valueOf(capabilitiesJSON.get("app"))) :
                    String.valueOf(capabilitiesJSON.get("app"));

                    capabilities.setCapability("app", appCapability);
            if (service != null) urlString = service.getUrl().toString();
        }
        return getDriver(capabilities, urlString);
    }

    public static AppiumDriver getDriver(DesiredCapabilities capabilities, String urlString){
        try {
            log.important(deviceName + markup(GRAY, " was selected"));
            return new AppiumDriver(new URL(urlString), capabilities);
        }
        catch (Exception gamma) {
            gamma.printStackTrace();
            if(gamma.toString().contains("Could not start a new session. Possible causes are invalid address of the remote server or browser start-up failure")){
                log.info("Please make sure " + markup(PURPLE, "Appium ") + "is on & verify the port that its running on at 'resources/test.properties'.");
                throw new RuntimeException(markup(YELLOW, gamma.getMessage()));
            }
            else throw new RuntimeException(markup(YELLOW, "Something went wrong while selecting a driver") + "\n" + markup(RED, gamma.getMessage()));
        }
    }

    public static DesiredCapabilities getConfig(JSONObject capabilities) {
        log.info("Setting capabilities...");
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        for (Object key : capabilities.keySet()) desiredCapabilities.setCapability((String) key, capabilities.get(key));
        return desiredCapabilities;
    }
}
