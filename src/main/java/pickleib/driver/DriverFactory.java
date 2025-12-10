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
            return fromString(text);
        }
    }
}
