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
import static pickleib.driver.DriverFactory.DriverType.getParentType;
import static utils.reflection.ReflectionUtilities.getField;

public class PlatformUtilities {

    /**
     * Determines the type of driver associated with the provided WebElement.
     *
     * @param element The WebElement whose driver type needs to be determined.
     * @return The DriverType associated with the WebElement:
     * - If the WebElement is associated with an AppiumDriver, returns DriverType.Mobile.
     * - If the WebElement is associated with a standard WebDriver, returns DriverType.Web.
     */
    public static Platform getElementDriverPlatform(WebElement element) {
        if (element instanceof java.lang.reflect.Proxy){
            InvocationHandler proxyInvocationHandler = java.lang.reflect.Proxy.getInvocationHandler(element);
            ElementLocator locator = (ElementLocator) getField("locator", proxyInvocationHandler);
            RemoteWebElement remoteWebElement = (RemoteWebElement) (locator).findElement();
            return ((RemoteWebDriver) remoteWebElement.getWrappedDriver()).getCapabilities().getPlatformName();
        }
        else {
            RemoteWebElement remoteWebElement = ((RemoteWebElement) element);
            RemoteWebDriver remoteWebDriver = ((RemoteWebDriver) remoteWebElement.getWrappedDriver());
            return remoteWebDriver.getCapabilities().getPlatformName();
        }
    }

    /**
     * Gets the driver type associated with the provided WebElement.
     *
     * @param element The WebElement whose driver type needs to be determined.
     * @return The DriverType corresponding to the WebElement.
     */
    public static DriverFactory.DriverType getElementDriverType(WebElement element) {
        return getParentType(getElementDriverPlatform(element));
    }

    /**
     * Checks if the provided WebElement is associated with an AppiumDriver.
     *
     * @param element The WebElement to be checked.
     * @return true if the WebElement is associated with an AppiumDriver, false otherwise.
     * If a ClassCastException occurs during the check, it returns false.
     */
    public static boolean isMobileElement(WebElement element) {
        return getGeneralType(getElementDriverType(element)).equals(Mobile);
    }

    /**
     * Checks if the provided WebElement is associated with an AppiumDriver.
     *
     * @param driver The RemoteWebDriver to be checked.
     * @return true if the WebElement is associated with an AppiumDriver, false otherwise.
     * If a ClassCastException occurs during the check, it returns false.
     */
    public static boolean isAppiumDriver(WebDriver driver) {
        try {
            return driver.getClass().isAssignableFrom(AppiumDriver.class);
        }
        catch (ClassCastException exception) {
            return false;
        }
    }

    /**
     * Retrieves the driver type based on the provided WebDriver instance.
     *
     * @param driver The WebDriver instance for which the driver type needs to be determined.
     * @return The DriverType corresponding to the provided WebDriver.
     */
    public static DriverFactory.DriverType getDriverType(WebDriver driver) {
        return getGeneralType(getParentType(((RemoteWebDriver) driver).getCapabilities().getPlatformName()));
    }

    /**
     * Retrieves the driver type based on the provided WebDriver instance.
     *
     * @param driver The WebDriver instance for which the driver type needs to be determined.
     * @return The DriverType corresponding to the provided WebDriver.
     */
    public static DriverFactory.DriverType getDriverPlatformParentType(WebDriver driver) {
        return getParentType(((RemoteWebDriver) driver).getCapabilities().getPlatformName());
    }

    /**
     * Gets the input attribute keyword for a specific DriverType.
     *
     * @param driverType The DriverType for which the input attribute keyword is needed.
     * @return The input attribute keyword corresponding to the given DriverType.
     * @throws EnumConstantNotPresentException If the provided DriverType is not handled in the switch statement.
     */
    public static String getInputContentAttributeNameFor(DriverFactory.DriverType driverType){
        return switch (driverType){
            case Web, iOS -> "value";
            case Android -> "text";
            default -> throw new EnumConstantNotPresentException(driverType.getClass(), driverType.name());
        };
    }

    /**
     * Gets the input attribute keyword for a specific DriverType.
     *
     * @param driverType The DriverType for which the input attribute keyword is needed.
     * @return The input attribute keyword corresponding to the given DriverType.
     * @throws EnumConstantNotPresentException If the provided DriverType is not handled in the switch statement.
     */
    public static String getTextAttributeNameFor(DriverFactory.DriverType driverType){
        return switch (driverType){
            case Android -> "@text";
            case Web -> "text()";
            case iOS -> "@value";
            default -> throw new EnumConstantNotPresentException(driverType.getClass(), driverType.name());
        };
    }
}
