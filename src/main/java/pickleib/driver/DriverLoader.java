package pickleib.driver;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import pickleib.platform.driver.PickleibAppiumDriver;
import pickleib.web.driver.PickleibWebDriver;

/**
 * Utility class responsible for initializing and loading driver instances
 * used by the framework.
 *
 * <p>
 * This class provides convenience methods for:
 * </p>
 * <ul>
 *   <li>Initializing and retrieving a Selenium {@link RemoteWebDriver}</li>
 *   <li>Initializing and retrieving an Appium {@link AppiumDriver}</li>
 *   <li>Initializing one or more drivers based on {@link DriverFactory.DriverType}</li>
 * </ul>
 *
 * <p>
 * Driver initialization is delegated to the corresponding driver managers
 * ({@code PickleibWebDriver} and {@code PickleibAppiumDriver}).
 * </p>
 *
 * @author  Umut Ay Bora
 */
public class DriverLoader {

    /**
     * Initializes and returns a Selenium {@link RemoteWebDriver} instance.
     *
     * <p>
     * This method ensures the WebDriver is initialized before returning the
     * current instance managed by {@code PickleibWebDriver}.
     * </p>
     *
     * @return the initialized {@link RemoteWebDriver} instance
     * @throws RuntimeException if the WebDriver cannot be initialized
     */
    public static RemoteWebDriver loadWebDriver() {
        PickleibWebDriver.initialize();
        return PickleibWebDriver.get();
    }

    /**
     * Initializes and returns an Appium {@link AppiumDriver} instance.
     *
     * <p>
     * This method ensures the Appium driver is initialized before returning
     * the current instance managed by {@code PickleibAppiumDriver}.
     * </p>
     *
     * @return the initialized {@link AppiumDriver} instance
     * @throws RuntimeException if the Appium driver cannot be initialized
     */
    public static AppiumDriver loadMobileDriver() {
        PickleibAppiumDriver.initialize();
        return PickleibAppiumDriver.get();
    }

    /**
     * Initializes one or more drivers based on the provided driver types.
     *
     * <p>
     * Each {@link DriverFactory.DriverType} value triggers initialization
     * of its corresponding driver implementation.
     * </p>
     *
     * @param driverTypes one or more driver types to initialize
     * @throws EnumConstantNotPresentException if an unsupported driver type is provided
     */
    public static void load(DriverFactory.DriverType... driverTypes) {
        for (DriverFactory.DriverType type : driverTypes) {
            switch (type) {
                case appium -> PickleibAppiumDriver.initialize();
                case selenium -> PickleibWebDriver.initialize();
                default -> throw new EnumConstantNotPresentException(
                        DriverFactory.DriverType.class, type.name());
            }
        }
    }
}

