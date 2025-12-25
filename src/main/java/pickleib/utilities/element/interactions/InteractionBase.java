package pickleib.utilities.element.interactions;

import org.openqa.selenium.WebElement;
import pickleib.driver.DriverFactory;
import pickleib.platform.interactions.PlatformInteractions;
import pickleib.utilities.interfaces.PolymorphicUtilities;
import pickleib.web.interactions.WebInteractions;
import utils.Printer;
import utils.StringUtilities;

import static pickleib.utilities.DriverInspector.getElementDriver;
import static pickleib.utilities.DriverInspector.isAppiumDriver;

/**
 * A foundational base class for Step Definitions and Page Objects that require access to interaction utilities.
 * <p>
 * This class abstracts the logic for selecting the correct interaction library ({@link WebInteractions} or
 * {@link PlatformInteractions}) based on the active driver or the specific element being interacted with.
 * It allows test code to be written polymorphically, handling both Web and Mobile contexts.
 * </p>
 *
 * @author  Umut Ay Bora
 */
public class InteractionBase {

    /**
     * The default platform to fall back to if no specific driver type is requested.
     * Defaults to {@link DriverFactory.DriverType#selenium} (Web).
     */
    public DriverFactory.DriverType defaultPlatform = DriverFactory.DriverType.selenium;

    public Printer log = new Printer(this.getClass());

    /**
     * Instance of utilities specifically for Web UI interactions (Selenium).
     * Null if web driver is not active.
     */
    public WebInteractions webInteractions;

    /**
     * Instance of utilities specifically for Mobile/Desktop interactions (Appium).
     * Null if platform driver is not active.
     */
    public PlatformInteractions platformInteractions;

    /**
     * Configurable constructor allowing granular control over which interaction sets are instantiated.
     * <p>
     * Use this if you want to optimize resource usage by only creating the interactions relevant
     * to the current test context (e.g., only Web).
     * </p>
     *
     * @param platformDriverActive If {@code true}, initializes {@link PlatformInteractions} (for Appium).
     * @param webDriverActive      If {@code true}, initializes {@link WebInteractions} (for Selenium).
     */
    public InteractionBase(
            boolean platformDriverActive,
            boolean webDriverActive) {
        if (webDriverActive) webInteractions = new WebInteractions();
        if (platformDriverActive) platformInteractions = new PlatformInteractions();
    }

    /**
     * Default constructor. Initializes both Web and Platform interactions.
     * <p>
     * This ensures the class is ready for any type of test execution but may instantiate unused objects.
     * </p>
     */
    public InteractionBase() {
        this(true, true);
    }

    /**
     * Retrieves the polymorphic utility instance corresponding to the specified driver type.
     * <p>
     * This method acts as a router, returning the correct set of tools (Web or Mobile)
     * so that the calling code doesn't need to know the implementation details.
     * </p>
     *
     * @param driverType The type of driver (Selenium or Appium) for which interactions are needed.
     * If {@code null} or blank, it falls back to {@link #defaultPlatform}.
     * @return An implementation of {@link PolymorphicUtilities} (either Web or Platform interactions).
     * @throws EnumConstantNotPresentException If an unsupported driver type is provided.
     */
    public PolymorphicUtilities getInteractions(DriverFactory.DriverType driverType) {
        if (webInteractions == null && platformInteractions == null)
            log.warning("Neither web nor platform interactions are instantiated!");

        if (!StringUtilities.isBlank(driverType))
            switch (driverType) {
                case selenium -> {
                    if (webInteractions == null)
                        log.warning("Web interactions requested without being instantiated!");
                    return webInteractions;
                }
                case appium -> {
                    if (platformInteractions == null)
                        log.warning("Platform interactions requested without being instantiated!");
                    return platformInteractions;
                }
                default -> throw new EnumConstantNotPresentException(DriverFactory.DriverType.class, driverType.name());
            }
        else return getInteractions(defaultPlatform);
    }

    /**
     * Intelligent utility retrieval based on the {@link WebElement} itself.
     * <p>
     * This method inspects the driver instance wrapped within the WebElement.
     * If the element belongs to an AppiumDriver, it returns {@link PlatformInteractions}.
     * Otherwise, it returns {@link WebInteractions}.
     * </p>
     *
     * @param element The WebElement to be interacted with.
     * @return The appropriate interaction utilities for that specific element.
     */
    public PolymorphicUtilities getInteractions(WebElement element) {
        if (isAppiumDriver(getElementDriver(element)))
            return platformInteractions;
        else
            return webInteractions;
    }
}