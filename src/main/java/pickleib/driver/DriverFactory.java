package pickleib.driver;

import org.jetbrains.annotations.Nullable;

public interface DriverFactory {
    enum DriverType {
        Web,
        Mobile;

        public static DriverType fromString(String text) {
            if (text != null)
                for (DriverType driverType:values())
                    if (driverType.name().equalsIgnoreCase(text))
                        return driverType;
            return null;
        }

        public static DriverType getType(@Nullable String text) {
            if (text != null) return fromString(text);
            else return Web;
        }
    }
}
