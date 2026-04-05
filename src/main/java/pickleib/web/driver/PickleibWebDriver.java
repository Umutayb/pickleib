package pickleib.web.driver;

import context.ContextStore;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.FluentWait;
import utils.Printer;
import utils.StringUtilities;
import java.util.*;

import static utils.StringUtilities.*;

/**
 * A static utility class that manages the lifecycle of the Selenium {@link RemoteWebDriver}.
 * <p>
 * This class acts as a <b>Singleton</b> wrapper for the active browser session. It handles:
 * <ul>
 * <li>Initialization of the driver based on configuration (Properties or Context).</li>
 * <li>Providing global access to the active driver instance via {@link #get()}.</li>
 * <li>Safe termination of the driver session.</li>
 * </ul>
 *
 * @author  Umut Ay Bora
 */
@SuppressWarnings("unused")
public class PickleibWebDriver {

    /**
     * The active RemoteWebDriver instance, scoped per thread for parallel execution.
     */
    private static final ThreadLocal<RemoteWebDriver> driver = new ThreadLocal<>();

    /**
     * The FluentWait instance associated with the active driver, scoped per thread.
     */
    private static final ThreadLocal<FluentWait<RemoteWebDriver>> wait = new ThreadLocal<>();

    /** Logger instance for web driver operations. */
    public static Printer log = new Printer(PickleibWebDriver.class);

    /**
     * Retrieves the active {@link RemoteWebDriver} instance for the current thread.
     *
     * @return The current driver instance, or null if not initialized.
     */
    public static RemoteWebDriver get(){
        return driver.get();
    }

    /**
     * Initializes a specific type of driver (e.g., Chrome, Firefox, Safari).
     *
     * @param browserType The {@link WebDriverFactory.BrowserType} enum representing the desired browser.
     */
    public static void initialize(WebDriverFactory.BrowserType browserType){
        log.info("Initializing " + markup(StringUtilities.Color.PURPLE, browserType.getDriverName()) + " driver...");
        driver.set(WebDriverFactory.getDriver(browserType));
    }

    /**
     * Smart initialization of the driver based on the environment configuration.
     * The method determines which browser to launch based on the following priority order:
     * <ol>
     * <li><b>Maven Property:</b> Checks `properties-from-pom.properties` for a "browser" key.</li>
     * <li><b>Context Store:</b> Checks {@link ContextStore} for a "browser" key (useful for runtime overrides).</li>
     * <li><b>Default:</b> Falls back to {@link WebDriverFactory.BrowserType#CHROME} if no configuration is found.</li>
     * </ol>
     */
    public static void initialize(){
        String driverName = firstLetterCapped(ContextStore.get("browser-name", "chrome"));
        if (driverName != null) initialize(WebDriverFactory.BrowserType.fromString(driverName));
        else initialize(WebDriverFactory.BrowserType.CHROME);
    }

    /**
     * Terminates the current driver session and closes the browser window.
     * <p>
     * This method is typically called in the `@After` hook of the test framework.
     * </p>
     */
    public static void terminate(){
        log.info("Terminating driver...");
        RemoteWebDriver current = driver.get();
        if (current != null) {
            current.quit();
            driver.remove();
        }
    }
}
