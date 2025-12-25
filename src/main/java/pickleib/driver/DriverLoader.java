package pickleib.driver;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import pickleib.platform.driver.PickleibAppiumDriver;
import pickleib.web.driver.PickleibWebDriver;

/**
 * Utility class for initializing and retrieving WebDriver instances for different testing platforms.
 * This class supports both Selenium-based web drivers and Appium-based mobile drivers, enabling seamless integration
 * into test automation frameworks. Drivers can be initialized individually or in bulk via the
 * {@link #load(DriverFactory.DriverType...)} method.
 *
 * @see DriverFactory.DriverType for supported driver types
 */
public class DriverLoader {
    

    /**
     * Initializes and returns a Selenium WebDriver instance for web browser automation.
     *
     * @return an instance of {@link RemoteWebDriver} configured for web testing
     */
    public static RemoteWebDriver loadWebDriver() {
        PickleibWebDriver.initialize();
        return PickleibWebDriver.get();
    }

    /**
     * Initializes and returns an Appium WebDriver instance for mobile application testing.
     *
     * @return an instance of {@link AppiumDriver} configured for mobile platforms
     */
    public static AppiumDriver loadMobileDriver() {
        PickleibAppiumDriver.initialize();
        return PickleibAppiumDriver.get();
    }

    /**
     * Initializes multiple WebDriver instances based on the specified driver types.
     * Each entry in the enum array triggers initialization of the corresponding driver.
     *
     * @param driverTypes an array of {@link DriverFactory.DriverType} enums specifying which drivers to initialize
     * @throws EnumConstantNotPresentException if any enum in the array is not a valid {@link DriverFactory.DriverType}
     */
    public static void load(DriverFactory.DriverType... driverTypes) {
        for (DriverFactory.DriverType type : driverTypes) {
            switch (type) {
                case appium -> PickleibAppiumDriver.initialize();
                case selenium -> PickleibWebDriver.initialize();
                default -> throw new EnumConstantNotPresentException(DriverFactory.DriverType.class, type.name());
            }
        }
    }
}