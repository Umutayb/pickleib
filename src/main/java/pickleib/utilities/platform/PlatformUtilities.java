package pickleib.utilities.platform;

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

public class PlatformUtilities {

    /**
     * Determines the type of driver associated with the provided WebElement.
     *
     * @param element The WebElement whose driver type needs to be determined.
     * @return The DriverType associated with the WebElement:
     * - If the WebElement is associated with an AppiumDriver, returns DriverType.appium.
     * - If the WebElement is associated with a standard WebDriver, returns DriverType.selenium.
     */
    public static Platform getElementDriverPlatform(WebElement element) {
        return ((RemoteWebDriver) getElementDriver(element)).getCapabilities().getPlatformName();
    }

    /**
     * Retrieves the WebDriver instance associated with a given WebElement.
     *
     * @param element The WebElement whose WebDriver instance is to be retrieved.
     * @return The WebDriver instance associated with the provided WebElement.
     * @throws IllegalArgumentException if the element cannot be processed to retrieve its WebDriver.
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
     * Gets the driver type associated with the provided WebElement.
     *
     * @param element The WebElement whose driver type needs to be determined.
     * @return The DriverType corresponding to the WebElement.
     */
    public static DriverFactory.DriverType getElementDriverType(WebElement element) {
        return DriverFactory.DriverType.getType(getElementDriverPlatform(element).name());
    }

    /**
     * Checks if the provided WebElement is associated with an AppiumDriver.
     *
     * @param element The WebElement to be checked.
     * @return true if the WebElement is associated with an AppiumDriver, false otherwise.
     * If a ClassCastException occurs during the check, it returns false.
     */
    public static boolean isPlatformElement(WebElement element) {
        return getElementDriverType(element).equals(appium);
    }

    /**
     * Checks if the provided WebElement is associated with an AppiumDriver.
     *
     * @param driver The RemoteWebDriver to be checked.
     * @return true if the WebElement is associated with an AppiumDriver, false otherwise.
     * If a ClassCastException occurs during the check, it returns false.
     */
    public static boolean isAppiumDriver(WebDriver driver) {
        try {return driver.getClass().isAssignableFrom(AppiumDriver.class);}
        catch (ClassCastException exception) {return false;}
    }

    /**
     * Retrieves the driver type based on the provided WebDriver instance.
     *
     * @param driver The WebDriver instance for which the driver type needs to be determined.
     * @return The DriverType corresponding to the provided WebDriver.
     */
    public static DriverFactory.DriverType getDriverType(WebDriver driver) {
        return DriverFactory.DriverType.getType(getDriverPlatform(driver).name());
    }

    /**
     * Retrieves the platform name of the WebDriver instance.
     *
     * @param driver The WebDriver instance for which the platform is to be retrieved.
     * @return The platform name as a {@link Platform} object.
     * @throws ClassCastException if the provided driver is not an instance of {@link RemoteWebDriver}.
     */
    public static Platform getDriverPlatform(WebDriver driver) {
        return ((RemoteWebDriver) driver).getCapabilities().getPlatformName();
    }

    /**
     * Retrieves the driver type based on the provided WebDriver instance.
     *
     * @param driver The WebDriver instance for which the driver type needs to be determined.
     * @return The DriverType corresponding to the provided WebDriver.
     */
    public static DriverFactory.DriverType getDriverPlatformParentType(WebDriver driver) {
        return getDriverType(driver);
    }

    /**
     * Gets the input attribute keyword for a specific DriverType.
     *
     * @param platform The DriverType for which the input attribute keyword is needed.
     * @return The input attribute keyword corresponding to the given DriverType.
     * @throws EnumConstantNotPresentException If the provided DriverType is not handled in the switch statement.
     */
    /*TODO: this feature needs a redesign for compatibility, mobile vs web vs platform approach is not reliable
        Instead try replacing platform with some other form on indicator. Ex: Android and iOS are different, iOS and
        WebDriver work the same... Perhaps a new Enum class 'TextAttribute' could solve this issue, but it needs to be
        assignable from WebElement type.
     */
    public static String getInputContentAttributeNameFor(Platform platform){
        return switch (platform){
            case ANDROID -> "text";
            default -> "value";
        };
    }

    /**
     * Gets the input attribute keyword for a specific DriverType.
     *
     * @param platform The DriverType for which the input attribute keyword is needed.
     * @return The input attribute keyword corresponding to the given DriverType.
     * @throws EnumConstantNotPresentException If the provided DriverType is not handled in the switch statement.
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
