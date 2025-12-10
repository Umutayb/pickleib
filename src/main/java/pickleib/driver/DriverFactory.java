package pickleib.driver;

import org.jetbrains.annotations.Nullable;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriverException;


public interface DriverFactory {
    enum DriverType {
        selenium,
        appium;

        public static DriverType fromString(String text) {
            if (text != null)
                for (DriverType driverType:values())
                    if (driverType.name().equalsIgnoreCase(text))
                        return driverType;
            return null;
        }

        public static DriverType getType(String text) {
            try {
                Platform.fromString(text);
                return appium;
            }
            catch (WebDriverException ignored){
                if (text != null && text.equalsIgnoreCase("web")) return selenium;
                else if (text != null && text.equalsIgnoreCase("mobile")) return appium;
            }
            return fromString(text);
        }
    }
}
