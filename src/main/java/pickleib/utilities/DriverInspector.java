package pickleib.utilities;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.pagefactory.ElementLocator;
import pickleib.driver.DriverFactory;
import java.lang.reflect.InvocationHandler;

import static pickleib.driver.DriverFactory.DriverType.*;
import static utils.reflection.ReflectionUtilities.getField;

/**
 * A static utility class designed to enable polymorphic behavior by determining the underlying
 * platform (Web, Android, iOS, etc.) of a given {@link WebElement} or {@link WebDriver} at runtime.
 * <p>
 * This class allows the framework to inspect a generic {@code WebElement} and decide whether to
 * apply Web-specific logic (Selenium) or Mobile-specific logic (Appium).
 * </p>
 * @author  Umut Ay Bora
 * @since   2.0.6
 */
public class DriverInspector {

    /**
     * Determines the specific {@link Platform} (e.g., ANDROID, MAC, WIN10) associated with the provided WebElement.
     *
     * @param element The WebElement whose driver platform needs to be identified.
     * @return The {@link Platform} enum value derived from the element's underlying driver capabilities.
     */
    public static Platform getElementDriverPlatform(WebElement element) {
        return ((RemoteWebDriver) getElementDriver(element)).getCapabilities().getPlatformName();
    }

    /**
     * Retrieves the actual {@link WebDriver} instance associated with a given {@link WebElement}.
     * <p>
     * <b>Technical Detail:</b><br>
     * Elements created via Selenium's {@code PageFactory} are actually Java Proxies. This method
     * uses reflection to:
     * <ol>
     * <li>Access the proxy's {@link InvocationHandler}.</li>
     * <li>Extract the {@link ElementLocator}.</li>
     * <li>Force a {@code findElement()} call to get the concrete {@link RemoteWebElement}.</li>
     * <li>Retrieve the wrapped driver from the concrete element.</li>
     * </ol>
     * </p>
     *
     *
     * @param element The WebElement (Proxy or Concrete) to inspect.
     * @return The underlying {@link WebDriver} instance.
     * @throws IllegalArgumentException if the element structure is not compatible with standard PageFactory proxies.
     */
    public static WebDriver getElementDriver(WebElement element) {
        if (element instanceof java.lang.reflect.Proxy) {
            InvocationHandler proxyInvocationHandler = java.lang.reflect.Proxy.getInvocationHandler(element);
            ElementLocator locator = (ElementLocator) getField("locator", proxyInvocationHandler);
            RemoteWebElement remoteWebElement = (RemoteWebElement) (locator).findElement();
            return remoteWebElement.getWrappedDriver();
        } else {
            RemoteWebElement remoteWebElement = ((RemoteWebElement) element);
            return remoteWebElement.getWrappedDriver();
        }
    }

    /**
     * Determines if the element belongs to a Selenium (Web) or Appium (Mobile) driver.
     *
     * @param element The WebElement to check.
     * @return {@link DriverFactory.DriverType#appium} if the driver is an instance of {@link AppiumDriver},
     * otherwise {@link DriverFactory.DriverType#selenium}.
     */
    public static DriverFactory.DriverType getElementDriverType(WebElement element) {
        return isAppiumDriver(getElementDriver(element)) ? appium : selenium;
    }

    /**
     * Checks if the provided WebElement is managed by an {@link AppiumDriver}.
     *
     * @param element The WebElement to check.
     * @return {@code true} if it is a mobile/platform element, {@code false} if it is a standard web element.
     */
    public static boolean isPlatformElement(WebElement element) {
        return getElementDriverType(element).equals(appium);
    }

    /**
     * Checks if the provided WebDriver is an instance of {@link AppiumDriver}.
     *
     * @param driver The WebDriver to check.
     * @return {@code true} if the driver class is assignable from {@code AppiumDriver.class}.
     */
    public static boolean isAppiumDriver(WebDriver driver) {
        try {
            return AppiumDriver.class.isAssignableFrom(driver.getClass());
        }
        catch (ClassCastException exception) {
            return false;
        }
    }

    /**
     * Retrieves the high-level {@link DriverFactory.DriverType} from a driver instance.
     *
     * @param driver The WebDriver instance.
     * @return {@code appium} or {@code selenium}.
     */
    public static DriverFactory.DriverType getDriverType(WebDriver driver) {
        return isAppiumDriver(driver) ? appium : selenium;
    }

    /**
     * Retrieves the platform name directly from the WebDriver's capabilities.
     *
     * @param driver The WebDriver instance.
     * @return The {@link Platform} object (e.g., Platform.ANDROID).
     * @throws ClassCastException if the driver is not a {@link RemoteWebDriver}.
     */
    public static Platform getDriverPlatform(WebDriver driver) {
        return ((RemoteWebDriver) driver).getCapabilities().getPlatformName();
    }

    /**
     * Alias for {@link #getDriverType(WebDriver)}.
     *
     * @param driver The WebDriver instance.
     * @return The driver type.
     */
    public static DriverFactory.DriverType getDriverPlatformParentType(WebDriver driver) {
        return getDriverType(driver);
    }

    /**
     * Resolves the correct HTML/XML attribute name for retrieving input text based on the platform.
     * <p>
     * Different platforms store user input in different attributes:
     * <ul>
     * <li><b>Android:</b> Uses the "text" attribute.</li>
     * <li><b>Web/iOS:</b> Typically uses the "value" attribute.</li>
     * </ul>
     * </p>
     *
     * @param platform The target platform.
     * @return The attribute name string.
     */
    public static String getInputContentAttributeNameFor(Platform platform){
        return switch (platform){
            case ANDROID -> "text";
            default -> "value";
        };
    }

    /**
     * Resolves the correct XPath attribute syntax for text validation based on the platform.
     *
     * @param platform The target platform.
     * @return The XPath segment (e.g., "@text", "text()", "@value").
     * @throws EnumConstantNotPresentException If an unsupported platform is passed.
     */
    public static String getTextAttributeNameFor(Platform platform){
        return switch (platform){
            case ANDROID -> "@text";
            case ANY -> "text()";
            case IOS -> "@value";
            default -> throw new EnumConstantNotPresentException(platform.getClass(), platform.name());
        };
    }
}
