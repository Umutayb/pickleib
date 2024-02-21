package pickleib.driver;

import collections.Pair;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import pickleib.mobile.driver.PickleibAppiumDriver;
import pickleib.web.driver.PickleibWebDriver;

public class DriverLoader {
    
    public RemoteWebDriver loadWebDriver(){
        PickleibWebDriver.initialize();
        return PickleibWebDriver.get();
    }
    
    public AppiumDriver loadMobileDriver(){
        PickleibAppiumDriver.initialize();
        return PickleibAppiumDriver.get();
    }
    
    public void load(DriverFactory.DriverType... driverTypes){
        for (DriverFactory.DriverType type:driverTypes) {
            switch (type){
                case Mobile -> PickleibAppiumDriver.initialize();
                case Web -> PickleibWebDriver.initialize();
                default -> throw new EnumConstantNotPresentException(DriverFactory.DriverType.class, type.name());
            }
        }
    }
}
