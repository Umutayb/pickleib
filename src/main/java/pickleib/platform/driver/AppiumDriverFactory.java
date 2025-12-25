package pickleib.platform.driver;

import context.ContextStore;
import io.appium.java_client.AppiumDriver;
import org.json.simple.JSONObject;
import org.openqa.selenium.remote.DesiredCapabilities;
import pickleib.driver.DriverFactory;
import utils.FileUtilities;
import utils.Printer;
import java.net.URL;

import static pickleib.platform.driver.ServiceFactory.service;
import static utils.FileUtilities.isValidFilePath;
import static utils.StringUtilities.Color.*;
import static utils.StringUtilities.*;

/**
 * A factory implementation for creating and configuring {@link AppiumDriver} instances.
 * <p>
 * This class handles the initialization of mobile drivers (Android/iOS), managing
 * local service addresses, remote grid connections (e.g., BrowserStack, SauceLabs),
 * and the conversion of JSON capabilities into Selenium {@link DesiredCapabilities}.
 * </p>
 */
public class AppiumDriverFactory implements DriverFactory {

    static Printer log = new Printer(AppiumDriverFactory.class);
    static String deviceName;

    /**
     * Initializes an {@link AppiumDriver} based on the provided device name, capabilities, and environment configuration.
     * <p>
     * This method constructs the Appium Server URL based on the {@code remote} flag:
     * <ul>
     * <li><b>Remote:</b> Fetches credentials and server URL from {@link ContextStore}.</li>
     * <li><b>Local:</b> Uses local address/port settings (defaulting to 0.0.0.0:4723) or a running Appium Service.</li>
     * </ul>
     * <p>
     * It also automatically resolves the absolute path for the "app" capability if it detects a valid local file path.
     *
     * @param deviceName       The nickname or identifier for the device being initialized (for logging purposes).
     * @param capabilitiesJSON A {@link JSONObject} containing key-value pairs of Appium capabilities.
     * @param remote           {@code true} to connect to a remote grid (e.g., BrowserStack); {@code false} for a local Appium server.
     * @return A fully initialized {@link AppiumDriver}.
     * @see ContextStore
     */
    public static AppiumDriver getDriver(String deviceName, JSONObject capabilitiesJSON, boolean remote){
        AppiumDriverFactory.deviceName = deviceName;
        DesiredCapabilities capabilities = getConfig(capabilitiesJSON);
        String urlString;
        String extension = ContextStore.get("appium-service-uri", "");
        if (remote) {
            String userName = ContextStore.get("remote-mobile-username");
            String accessKey = ContextStore.get("remote-mobile-access-key");
            String server = ContextStore.get("remote-mobile-server");
            urlString = String.format("https://%s:%s@%s%s", userName , accessKey, server, extension);
        }
        else {
            String address = ContextStore.get("address", "0.0.0.0");
            String port = ContextStore.get("port", "4723");
            urlString = String.format("http://%s:%s%s", address, port, extension);

            if(capabilitiesJSON.get("app") != null) {
                String appCapability = isValidFilePath(String.valueOf(capabilitiesJSON.get("app"))) ?
                        FileUtilities.getAbsolutePath(String.valueOf(capabilitiesJSON.get("app"))) :
                        String.valueOf(capabilitiesJSON.get("app"));
                capabilities.setCapability("app", appCapability);
            }
            // Note: 'service' needs to be defined in the class scope for this to work.
            // Assuming 'service' is a static AppiumDriverLocalService field in the full implementation.
            if (service != null) urlString = service.getUrl().toString();
        }
        return getDriver(capabilities, urlString);
    }

    /**
     * Core method to instantiate the {@link AppiumDriver} using specific capabilities and a server URL.
     * <p>
     * This method attempts to connect to the Appium server. If the connection fails, it provides
     * troubleshooting advice regarding the Appium server status and port configuration.
     * </p>
     *
     * @param capabilities The {@link DesiredCapabilities} required for the driver session.
     * @param urlString    The full URL of the Appium server (e.g., "http://127.0.0.1:4723/wd/hub").
     * @return A new instance of {@link AppiumDriver}.
     * @throws RuntimeException If the driver cannot be initialized due to connection errors or invalid configurations.
     */
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

    /**
     * Converts a JSON object of capabilities into Selenium {@link DesiredCapabilities}.
     * <p>
     * This iterates through the keys of the provided JSON object and sets them
     * as capabilities on the resulting DesiredCapabilities object.
     * </p>
     *
     * @param capabilities A {@link JSONObject} containing capability key-value pairs.
     * @return A {@link DesiredCapabilities} object populated with the provided data.
     */
    public static DesiredCapabilities getConfig(JSONObject capabilities) {
        log.info("Setting capabilities...");
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        for (Object key : capabilities.keySet()) desiredCapabilities.setCapability((String) key, capabilities.get(key));
        return desiredCapabilities;
    }
}
