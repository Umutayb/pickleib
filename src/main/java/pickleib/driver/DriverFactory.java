package pickleib.driver;

/**
 * Defines the factory interface and enum for specifying supported WebDriver types.
 * The {@link DriverType} enum enumerates the available automation frameworks,
 * while this interface serves as a container for type definitions and conversion utilities.
 *
 * @author  Umut Ay Bora
 */
public interface DriverFactory {

    /**
     * Enumerates the types of WebDriver frameworks supported by this library.
     * Each constant represents a different automation framework:
     * <ul>
     *   <li>{@link #selenium}: For controlling web browsers via Selenium WebDriver</li>
     *   <li>{@link #appium}: For automating mobile applications using the Appium protocol</li>
     * </ul>
     */
    enum DriverType {
        selenium,
        appium;

        /**
         * Converts a case-insensitive string representation to the corresponding enum.
         *
         * @param text the string to parse (case-insensitive)
         * @return the matching {@link DriverType} if found, otherwise {@code null}
         */
        public static DriverType fromString(String text) {
            if (text != null) {
                for (DriverType driverType : values()) {
                    if (driverType.name().equalsIgnoreCase(text)) {
                        return driverType;
                    }
                }
            }
            return null;
        }

        /**
         * Determines the appropriate {@link DriverType} based on the provided string.
         * Recognizes common aliases:
         * <ul>
         *   <li>"web" is treated as {@link #selenium}</li>
         *   <li>"mobile" is treated as {@link #appium}</li>
         * </ul>
         * For other strings, performs a case-insensitive match against enum names.
         *
         * @param text the input string to evaluate
         * @return the corresponding {@link DriverType} enum
         * @throws AssertionError if {@code text} is {@code null} (implementation assertion)
         */
        public static DriverType getType(String text) {
            assert text != null;
            if (text.equalsIgnoreCase("web")) return selenium;
            if (text.equalsIgnoreCase("mobile")) return appium;
            return fromString(text);
        }
    }
}
