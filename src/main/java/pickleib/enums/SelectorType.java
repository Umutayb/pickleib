package pickleib.enums;

/**
 * Defines the supported selector strategies for identifying UI elements within the
 * JSON-based Page Repository.
 * <p>
 * These enum constants correspond to the keys used in the {@code "selectors"} object
 * inside {@code page-repository.json}. They determine how the framework attempts
 * to locate the element on the UI (Web, Mobile, or Desktop).
 * </p>
 */
public enum SelectorType {

    /**
     * Locates elements by their "id" attribute.
     * <br>Corresponds to {@link org.openqa.selenium.By#id(String)}.
     */
    id("id"),

    /**
     * Locates elements by their "name" attribute.
     * <br>Corresponds to {@link org.openqa.selenium.By#name(String)}.
     */
    name("name"),

    /**
     * Locates elements by their HTML tag name.
     * <br>Corresponds to {@link org.openqa.selenium.By#tagName(String)}.
     */
    tagName("tagName"),

    /**
     * Locates elements by their "class" attribute.
     * <br>Corresponds to {@link org.openqa.selenium.By#className(String)}.
     */
    className("className"),

    /**
     * Locates elements using a CSS selector.
     * <br>Corresponds to {@link org.openqa.selenium.By#cssSelector(String)}.
     */
    css("css"),

    /**
     * Locates elements using an XPath expression.
     * <br>Corresponds to {@link org.openqa.selenium.By#xpath(String)}.
     */
    xpath("xpath"),

    /**
     * Locates elements by their accessibility ID (common in Mobile/Desktop automation).
     * <br>Corresponds to {@link io.appium.java_client.AppiumBy#accessibilityId(String)}.
     */
    accessibilityId("accessibilityId"),

    /**
     * Locates elements using Android Data Matcher (Espresso).
     * <br>Corresponds to {@link io.appium.java_client.AppiumBy#androidDataMatcher(String)}.
     */
    androidDataMatcher("androidDataMatcher"),

    /**
     * Locates elements using Android View Matcher (Espresso).
     * <br>Corresponds to {@link io.appium.java_client.AppiumBy#androidViewMatcher(String)}.
     */
    androidViewMatcher("androidViewMatcher"),

    /**
     * Locates elements using Android View Tag (Espresso).
     * <br>Corresponds to {@link io.appium.java_client.AppiumBy#androidViewTag(String)}.
     */
    androidViewTag("androidViewTag"),

    /**
     * Locates elements using Android UIAutomator selector strings.
     * <br>Corresponds to {@link io.appium.java_client.AppiumBy#androidUIAutomator(String)}.
     */
    androidUIAutomator("androidUIAutomator"),

    /**
     * Locates elements using iOS Class Chain strategies.
     * <br>Corresponds to {@link io.appium.java_client.AppiumBy#iOSClassChain(String)}.
     */
    iOSClassChain("iOSClassChain"),

    /**
     * Locates elements using iOS NsPredicateString strategies.
     * <br>Corresponds to {@link io.appium.java_client.AppiumBy#iOSNsPredicateString(String)}.
     */
    iOSNsPredicateString("iOSNsPredicateString"),

    /**
     * Locates elements by their visible text content.
     * <br><b>Note:</b> This is a custom strategy often implemented using XPath text matching.
     */
    text("text");

    final String key;

    /**
     * Constructor for SelectorType.
     *
     * @param key The string representation of the selector type as used in the JSON repository.
     */
    SelectorType(String key){
        this.key = key;
    }

    /**
     * Retrieves the string key associated with this selector type.
     *
     * @return The JSON key string (e.g., "css", "xpath", "accessibilityId").
     */
    public String getKey() {
        return key;
    }
}
