package pickleib.enums;

/**
 * Defines the architectural strategies available for the Page Object Repository within the framework.
 * <p>
 * This enumeration dictates how the framework acquires and interacts with web elements:
 * <ul>
 * <li><b>POM</b>: Uses Java classes and reflection (Classic approach).</li>
 * <li><b>JSON</b>: Uses an external JSON file for element definitions (Low-Code approach).</li>
 * </ul>
 * </p>
 */
public enum PageRepositoryDesign {

    /**
     * Represents the <b>Classic Page Object Model (POM)</b> design.
     * <p>
     * In this mode, page objects are Java classes that typically extend {@code PickleibPageObject}.
     * Elements are identified within these classes using Selenium's {@code @FindBy} annotations
     * or public {@code WebElement} fields.
     * </p>
     */
    pom,

    /**
     * Represents the <b>Low-Code JSON</b> design.
     * <p>
     * In this mode, page definitions and element selectors are stored in an external JSON file
     * (e.g., {@code page-repository.json}). This decouples the element locators from the Java code,
     * allowing for updates without recompilation.
     * </p>
     */
    json;

    /**
     * Retrieves the {@link PageRepositoryDesign} constant corresponding to the provided text.
     * <p>
     * This method performs a case-insensitive lookup. If the text does not match any
     * defined design strategy, it returns {@code null}.
     * </p>
     *
     * @param text The string representation of the design (e.g., "pom" or "JSON").
     * Can be null.
     * @return The matching {@link PageRepositoryDesign} enum constant, or {@code null} if no match is found
     * or if the input text is null.
     */
    public static PageRepositoryDesign getDesign(String text) {
        if (text != null)
            for (PageRepositoryDesign design : values())
                if (design.name().equalsIgnoreCase(text))
                    return design;
        return null;
    }
}
