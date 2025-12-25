package pickleib.driver;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import pickleib.platform.driver.PickleibAppiumDriver;
import pickleib.web.driver.PickleibWebDriver;

public class DriverLoader {
    
    public static RemoteWebDriver loadWebDriver(){
        PickleibWebDriver.initialize();
        return PickleibWebDriver.get();
    }
    
    public static AppiumDriver loadMobileDriver(){
        PickleibAppiumDriver.initialize();
        return PickleibAppiumDriver.get();
    }
    
    public static void load(DriverFactory.DriverType... driverTypes){
        for (DriverFactory.DriverType type:driverTypes) {
            switch (type){
                case appium -> PickleibAppiumDriver.initialize();
                case selenium -> PickleibWebDriver.initialize();
                default -> throw new EnumConstantNotPresentException(DriverFactory.DriverType.class, type.name());
            }
        }
    }
}
