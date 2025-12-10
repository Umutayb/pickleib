package pickleib.enums;

/**
 * Defines general selector types
 */
public enum SelectorType {
    id("id"),
    name("name"),
    tagName("tagName"),
    className("class"),
    css("css"),
    xpath("xpath"),
    accessibilityId("accessibilityId"),
    androidDataMatcher("androidDataMatcher"),
    androidViewMatcher("androidViewMatcher"),
    androidViewTag("androidViewTag"),
    androidUIAutomator("androidUIAutomator"),
    iOSClassChain("iOSClassChain"),
    iOSNsPredicateString("iOSNsPredicateString"),
    text("text");

    final String key;

    SelectorType(String key){
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
