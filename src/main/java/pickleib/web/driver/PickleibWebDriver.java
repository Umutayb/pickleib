package pickleib.web.driver;

import context.ContextStore;
import org.bouncycastle.util.encoders.Base64;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v85.network.Network;
import org.openqa.selenium.devtools.v85.network.model.Headers;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.FluentWait;
import properties.PropertiesReader;
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
     * The active RemoteWebDriver instance.
     */
    private static RemoteWebDriver driver;

    /**
     * The FluentWait instance associated with the active driver.
     */
    private static FluentWait<RemoteWebDriver> wait;

    static PropertiesReader reader = new PropertiesReader("properties-from-pom.properties");
    public static Printer log = new Printer(PickleibWebDriver.class);

    /**
     * Retrieves the active {@link RemoteWebDriver} instance.
     *
     * @return The current driver instance, or null if not initialized.
     */
    public static RemoteWebDriver get(){
        return driver;
    }

    /**
     * Initializes a specific type of driver (e.g., Chrome, Firefox, Safari).
     *
     * @param browserType The {@link WebDriverFactory.BrowserType} enum representing the desired browser.
     */
    public static void initialize(WebDriverFactory.BrowserType browserType){
        log.info("Initializing " + markup(StringUtilities.Color.PURPLE, browserType.getDriverName()) + " driver...");
        driver = WebDriverFactory.getDriver(browserType);
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
        String driverName = firstLetterCapped(reader.getProperty("browser"));
        String driverProperty = firstLetterCapped(ContextStore.get("browser"));
        if (driverName != null) initialize(WebDriverFactory.BrowserType.fromString(driverName));
        else if (driverProperty != null) initialize(WebDriverFactory.BrowserType.fromString(driverProperty));
        else initialize(WebDriverFactory.BrowserType.CHROME);
    }

    /**
     * Initializes a Chrome driver with Basic Authentication headers injected via DevTools.
     *
     * @param id          The username for Basic Auth.
     * @param password    The password for Basic Auth.
     * @param browserType The browser type (Must be Chrome-based).
     * @deprecated As of 1.5.6. This method relies on specific DevTools versions and is not maintained.
     * Prefer using standard URL encoding (https://user:pass@site.com) or proxy utilities.
     */
    @Deprecated(since = "1.5.6")
    public static void initialize(String id, String password, WebDriverFactory.BrowserType browserType){ //Only works with chrome!
        initialize(browserType);
        DevTools dev = ((ChromeDriver) driver).getDevTools();
        dev.createSession();
        dev.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
        Map<String, Object> map = new HashMap<>();
        map.put("Authorization", "Basic " + (Arrays.toString(Base64.encode((id + ":" + password).getBytes()))));
        dev.send(Network.setExtraHTTPHeaders(new Headers(map)));
    }

    /**
     * Terminates the current driver session and closes the browser window.
     * <p>
     * This method is typically called in the `@After` hook of the test framework.
     * </p>
     */
    public static void terminate(){
        log.info("Terminating driver...");
        if (driver != null) {
            driver.quit();
        }
    }
}