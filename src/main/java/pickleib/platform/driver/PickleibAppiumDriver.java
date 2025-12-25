package pickleib.platform.driver;

import context.ContextStore;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.AppiumFluentWait;
import org.json.simple.JSONObject;
import org.openqa.selenium.remote.RemoteWebDriver;
import pickleib.utilities.screenshot.ScreenCaptureUtility;
import utils.*;
import java.io.IOException;
import java.net.ServerSocket;

/**
 * A static utility class that manages the lifecycle of the {@link AppiumDriver}.
 * <p>
 * This class handles:
 * <ul>
 * <li>Starting the local Appium server service.</li>
 * <li>Initializing the driver based on JSON configuration files.</li>
 * <li>Managing the singleton driver instance.</li>
 * <li>Terminating the session and stopping the service.</li>
 * <li>Capturing screenshots upon termination (useful for test failures).</li>
 * </ul>
 * </p>
 */
@SuppressWarnings("unused")
public abstract class PickleibAppiumDriver {

    private static AppiumDriver driver;
    private static AppiumFluentWait<RemoteWebDriver> wait;
    private static final Printer log = new Printer(PickleibAppiumDriver.class);

    /**
     * Retrieves the active instance of the {@link AppiumDriver}.
     *
     * @return The current {@link AppiumDriver} instance, or null if not initialized.
     */
    public static AppiumDriver get(){
        return driver;
    }

    /**
     * Starts the Appium server service.
     * <p>
     * This method retrieves the address and port from the {@link ContextStore}.
     * If the specified port is not available, it automatically finds a free available port
     * on the system to avoid conflicts.
     * </p>
     * * @throws RuntimeException If a free port cannot be found or an I/O error occurs.
     */
    public static void startService(){
        log.info("Initializing appium service");

        String address = ContextStore.get("address");
        int port = Integer.parseInt(ContextStore.get("port"));

        if (!new SystemUtilities().portIsAvailable(port)){
            try (ServerSocket socket = new ServerSocket(0)) {
                port = socket.getLocalPort();
            }
            catch (IOException e) {throw new RuntimeException(e);}
        }

        ServiceFactory.startService(address, port);    // Start Appium
    }

    /**
     * Initializes the Appium driver based on the configuration provided.
     * <p>
     * The initialization process involves:
     * <ol>
     * <li>Identifying the target device name from properties or {@link ContextStore}.</li>
     * <li>Locating the JSON configuration file for that device in the specified directory.</li>
     * <li>Parsing capabilities from the JSON file.</li>
     * <li>Creating the driver via {@link AppiumDriverFactory}, supporting both local and remote (cloud) executions.</li>
     * </ol>
     * </p>
     */
    public static void initialize() {
        log.info("Initializing appium driver");
        String device = ContextStore.get("device");
        if (device == null) device = ContextStore.get("device");

        String directory = ContextStore.get("config", "src/test/resources/configurations");

        JSONObject json = FileUtilities.Json.parseJSONFile(directory + "/" + device + ".json");
        driver = AppiumDriverFactory.getDriver(
                StringUtilities.firstLetterCapped(device),
                json,
                ContextStore.getBoolean("use-remote-mobile-driver", false)
        );
    }

    /**
     * Terminates the current driver session and stops the Appium service.
     * <p>
     * This method safely quits the driver and ensures the service is stopped,
     * even if an exception occurs during the driver quit process.
     * </p>
     */
    public static void terminate(){
        log.info("Finalizing driver...");
        try {
            if (driver != null) driver.quit();
        }
        catch (Exception exception){
            exception.printStackTrace();
        }
        finally {
            if (ServiceFactory.service != null) ServiceFactory.service.stop();
        }
    }

    /**
     * Captures a screenshot and then terminates the driver session.
     * <p>
     * This is typically used in test hooks to capture the state of the application
     * immediately after a test failure or completion before tearing down the environment.
     * </p>
     *
     * @param success       Currently unused in the method body but implies test status.
     * @param screenshotTag The name/tag to be used for the saved screenshot file.
     */
    public static void captureAndTerminate(boolean success, String screenshotTag){
        log.info("Finalizing driver...");
        try {
            ScreenCaptureUtility.captureScreen(screenshotTag, "png", driver);
            if (driver != null) driver.quit();
        }
        catch (Exception exception){
            exception.printStackTrace();
        }
        finally {
            if (ServiceFactory.service != null) ServiceFactory.service.stop();
        }
    }
}