package pickleib.driver;

import org.jetbrains.annotations.Nullable;
import org.openqa.selenium.Platform;

public interface DriverFactory {
    enum DriverType {
        Web,
        Mobile,
        iOS,
        Android;

        public static DriverType fromString(String text) {
            if (text != null)
                for (DriverType driverType:values())
                    if (driverType.name().equalsIgnoreCase(text))
                        return driverType;
            return null;
        }

        public static DriverType getType(@Nullable String text) {
            return fromString(text);
        }

        public static DriverType getParentType(Platform platform) {
            return switch (platform) {
                case WINDOWS, SONOMA, LINUX, UNIX, VENTURA, MONTEREY, BIG_SUR, CATALINA, MOJAVE, HIGH_SIERRA, SIERRA, EL_CAPITAN, YOSEMITE, MAVERICKS, MOUNTAIN_LION, SNOW_LEOPARD, MAC, WIN11, WIN10, WIN8_1, WIN8, WIN7, VISTA, XP -> Web;
                case ANDROID -> Android;
                case IOS -> iOS;
                case ANY -> null;
            };
        }

        public static DriverType getGeneralType(DriverType driverType){
            return switch (driverType){
                case Web -> Web;
                case Mobile, iOS, Android -> Mobile;
            };
        }
    }
}
