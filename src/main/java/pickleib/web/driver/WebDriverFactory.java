package pickleib.web.driver;

import context.ContextStore;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;
import pickleib.driver.DriverFactory;
import pickleib.enums.EmulatedDevice;
import pickleib.exceptions.PickleibException;
import utils.LogUtilities;
import utils.Printer;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

import static utils.StringUtilities.Color.*;
import static utils.StringUtilities.highlighted;

/**
 * A factory class responsible for creating and configuring Selenium WebDriver instances.
 * <p>
 * This class serves as the central hub for browser instantiation. It supports:
 * <ul>
 * <li><b>Multiple Browsers:</b> Chrome, Firefox, Safari, Edge, Opera.</li>
 * <li><b>Execution Modes:</b> Headless, Mobile Emulation, Selenium Grid.</li>
 * <li><b>Configuration:</b> Extensive customization via {@link ContextStore} keys (e.g., timeouts, screen size, proxy).</li>
 * <li><b>Dependency Management:</b> Optional integration with {@code WebDriverManager} (WDM).</li>
 * </ul>
 *
 * @author Umut Ay Bora
 */
public class WebDriverFactory implements DriverFactory {

    /**
     * Determines the width of the browser window in pixels.
     * Default: 1920.
     */
    static int frameWidth = ContextStore.getInt("frame-width",1920);

    /**
     * The address of the proxy server to use (optional).
     */
    static String proxyAddress = ContextStore.get("proxy-address");

    /**
     * The port of the proxy server to use.
     * Default: 0 (disabled).
     */
    static int proxyPort = ContextStore.getInt("proxy-port", 0);

    /**
     * Determines the height of the browser window in pixels.
     * Default: 1080.
     */
    static int frameHeight = ContextStore.getInt("frame-height",1080);

    /**
     * If true, runs the browser in headless mode (no UI).
     * Default: false.
     */
    static boolean headless = ContextStore.getBoolean("headless", false);

    /**
     * If true, enables mobile emulation mode in Chrome.
     * Default: false.
     */
    static boolean mobileMode = ContextStore.getBoolean("mobile-mode", false);

    /**
     * The preferred device configuration for mobile emulation.
     * Default: iPhone12Pro.
     */
    static EmulatedDevice preferredDevice = EmulatedDevice.getType(ContextStore.get("emulated-device", "iPhone12Pro"));

    /**
     * If true, maximizes the browser window on startup.
     * Default: false.
     */
    static boolean maximise = Boolean.parseBoolean(ContextStore.get("driver-maximize", "false"));

    /**
     * Global timeout for the driver (implicit wait) in seconds.
     * Calculated by dividing "driver-timeout" (ms) by 1000.
     * Default: 15 seconds.
     */
    static long driverTimeout = ContextStore.getInt("driver-timeout", 15000)/1000;

    /**
     * If true, deletes all cookies upon driver initialization.
     * Default: false.
     */
    static boolean deleteCookies = Boolean.parseBoolean(ContextStore.get("delete-cookies", "false"));

    /**
     * If true, connects to a remote Selenium Grid hub instead of a local driver.
     * Default: false.
     */
    static boolean useSeleniumGrid = ContextStore.getBoolean("selenium-grid", false);

    /**
     * If true, accepts insecure SSL certificates (e.g., self-signed).
     * Default: false.
     */
    static boolean insecureLocalHost = ContextStore.getBoolean("insecure-localhost", false);

    /**
     * If true, adds the "--no-sandbox" argument (often required for CI/Docker environments).
     * Default: false.
     */
    static boolean noSandbox = ContextStore.getBoolean("driver-no-sandbox", false);

    /**
     * If true, disables browser notifications (e.g., "Show notifications").
     * Default: true.
     */
    static boolean disableNotifications = ContextStore.getBoolean("disable-notifications", true);

    /**
     * Determines the page load strategy (normal, eager, none).
     * Default: normal.
     */
    static PageLoadStrategy loadStrategy = PageLoadStrategy.fromString(ContextStore.get("load-strategy", "normal"));

    /**
     * If true, uses WebDriverManager to automatically download/setup driver binaries.
     * Default: false.
     */
    static Boolean useWDM = ContextStore.getBoolean("web-driver-manager", false);

    /**
     * If true, allows remote origins (fixes "Connection Refused" issues in newer Chrome versions).
     * Default: true.
     */
    static Boolean allowRemoteOrigin = ContextStore.getBoolean("allow-remote-origin", true);

    /**
     * The logging level used by the underlying Selenium driver.
     * Default: "off".
     */
    static String logLevel = ContextStore.get("selenium-log-level", "off");

    /**
     * The URL of the Selenium Grid hub. Required if {@code useSeleniumGrid} is true.
     */
    static String hubUrl = ContextStore.get("hub-url","");

    /**
     * The default browser to use if no specific type is requested.
     * Default: "chrome".
     */
    static String browser = ContextStore.get("browser", "chrome");

    /**
     * DriverFactory Logger.
     */
    private static final Printer log = new Printer(WebDriverFactory.class);

    /**
     * Initializes and returns a fully configured {@link RemoteWebDriver} instance.
     * This method handles the logic for:
     * <ul>
     *   <li>Connecting to a Grid vs. Local execution.</li>
     *   <li>Setting implicit wait timeouts.</li>
     *   <li>Configuring window size/maximization.</li>
     *   <li>Managing cookies and logging levels.</li>
     * </ul>
     *
     * @param browserType The specific {@link BrowserType} to initialize. If null, falls back to the "browser" config.
     * @return A configured RemoteWebDriver instance.
     * @throws RuntimeException If the driver cannot be initialized (e.g., Malformed URL for Grid, Connection refused).
     */
    public static RemoteWebDriver getDriver(BrowserType browserType){
        // Suppress Selenium CDP version mismatch warnings
        Logger.getLogger("org.openqa.selenium").setLevel(Level.SEVERE);

        RemoteWebDriver driver;

        try {
            if (browserType == null) browserType = BrowserType.fromString(browser);

            if (useSeleniumGrid){
                ImmutableCapabilities capabilities = new ImmutableCapabilities("browserName", browserType.getDriverKey());
                driver = new RemoteWebDriver(new URL(hubUrl), capabilities);
            }
            else {driver = driverSwitch(headless, useWDM, insecureLocalHost, noSandbox, disableNotifications, allowRemoteOrigin, loadStrategy, browserType, mobileMode, preferredDevice);}

            if (driver == null) throw new PickleibException("Driver initialization failed — driverSwitch returned null");
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(driverTimeout));
            if (deleteCookies) driver.manage().deleteAllCookies();
            if (maximise && !headless) driver.manage().window().maximize();
            else driver.manage().window().setSize(new Dimension(frameWidth, frameHeight));
            driver.setLogLevel(LogUtilities.getLevel(logLevel));
            log.important(browserType.getDriverName() + GRAY.getValue() + " was selected");
            return driver;
        }
        catch (IOException malformedURLException) {throw new RuntimeException(malformedURLException);}
        catch (Exception gamma) {
            if(gamma.toString().contains("Could not start a new session. Possible causes are invalid address of the remote server or browser start-up failure")){
                log.info("Please make sure the "+PURPLE+"Selenium Grid "+GRAY+"is on & verify the port that its running on at 'resources/test.properties'."+RESET);
                throw new RuntimeException(gamma);
            }
            else
                throw new RuntimeException(highlighted(YELLOW, "Something went wrong while selecting a driver ") + highlighted(RED, "\n\t"+gamma), gamma);
        }
    }

    /**
     * Internal helper method to instantiate the correct driver class with specific options.
     * <p>
     * This method applies browser-specific settings (like ChromeOptions, FirefoxOptions)
     * based on the provided configuration flags. It also handles the optional setup of WebDriverManager.
     * </p>
     *
     * @param headless             Run in headless mode.
     * @param useWDM               Use WebDriverManager.
     * @param insecureLocalHost    Accept insecure certs.
     * @param noSandbox            Disable sandbox.
     * @param disableNotifications Disable browser notifications.
     * @param allowRemoteOrigin    Allow remote origins (Chrome).
     * @param loadStrategy         Page loading strategy.
     * @param browserType          Target browser.
     * @param mobileMode           Enable mobile emulation.
     * @param preferredDevice      Target mobile device for emulation.
     * @return A new instance of the requested driver.
     */
    static RemoteWebDriver driverSwitch(
            Boolean headless,
            Boolean useWDM,
            Boolean insecureLocalHost,
            Boolean noSandbox,
            Boolean disableNotifications,
            Boolean allowRemoteOrigin,
            PageLoadStrategy loadStrategy,
            BrowserType browserType,
            Boolean mobileMode,
            EmulatedDevice preferredDevice){
        if (useWDM) log.warning("Using WebDriverManager...");
        try {
            switch (browserType) {
                case CHROME -> {
                    ChromeOptions options = new ChromeOptions();
                    if (disableNotifications) options.addArguments("disable-notifications");
                    if (insecureLocalHost){
                        options.addArguments("--allow-insecure-localhost");
                        options.addArguments("--ignore-certificate-errors");
                    }
                    if (noSandbox) options.addArguments("--no-sandbox");
                    options.setPageLoadStrategy(loadStrategy);

                    if (allowRemoteOrigin) options.addArguments("--remote-allow-origins=*");
                    if (headless) options.addArguments("--headless=new");
                    if (useWDM) WebDriverManager.chromedriver().setup();
                    if (mobileMode) options.setExperimentalOption("mobileEmulation", preferredDevice.emulate());
                    return new ChromeDriver(options);
                }
                case FIREFOX -> {
                    FirefoxOptions options = new FirefoxOptions();
                    if (insecureLocalHost){
                        options.addPreference("webdriver.accept.insecure.certs", true);
                        options.addPreference("webdriver.accept.untrusted.certs", true);
                    }
                    options.setPageLoadStrategy(loadStrategy);
                    options.setAcceptInsecureCerts(insecureLocalHost);
                    if (disableNotifications) options.addPreference("dom.webnotifications.enabled", false);
                    if (headless) options.addArguments("-headless");
                    if (useWDM) WebDriverManager.firefoxdriver().setup();
                    return new FirefoxDriver(options);
                }
                case SAFARI -> {
                    SafariOptions options = new SafariOptions();
                    if (useWDM) WebDriverManager.safaridriver().setup();
                    return new SafariDriver(options);
                }
                case EDGE -> {
                    EdgeOptions options = new EdgeOptions();
                    if (useWDM) WebDriverManager.edgedriver().setup();
                    return new EdgeDriver(options);
                }
                default -> throw new PickleibException("No such driver was defined.");
            }
        }
        catch (SessionNotCreatedException sessionException){
            log.warning(sessionException.getLocalizedMessage());
            if (!useWDM) return driverSwitch(headless, true, insecureLocalHost, noSandbox, disableNotifications, allowRemoteOrigin, loadStrategy, browserType, mobileMode, preferredDevice);
            else return null;
        }
    }

    /**
     * Enumeration of supported browser types.
     */
    public enum BrowserType {
        /** Google Chrome browser. */
        CHROME("Chrome"),
        /** Mozilla Firefox browser. */
        FIREFOX("Firefox"),
        /** Apple Safari browser. */
        SAFARI("Safari"),
        /** Microsoft Edge browser. */
        EDGE("Edge"),
        /** Opera browser. */
        OPERA("Opera");

        final String driverName;

        BrowserType(String driverName){
            this.driverName = driverName;
        }

        /**
         * Returns the driver key in lowercase (used for capability matching).
         *
         * @return the lowercase driver key
         */
        public String getDriverKey() {
            return driverName.toLowerCase();
        }

        /**
         * Returns a driver type matching a given text (Non-case-sensitive).
         *
         * @param text The desired driver name (e.g., "chrome", "FIREFOX").
         * @return The matching BrowserType, or null if not found.
         */
        public static BrowserType fromString(String text) {
            if (text != null)
                for (BrowserType browserType :values())
                    if (browserType.name().equalsIgnoreCase(text))
                        return browserType;
            return null;
        }

        /**
         * Returns the display name of the browser.
         *
         * @return the browser display name
         */
        public String getDriverName() {
            return driverName;
        }
    }

    // Setters for static configuration (useful for runtime adjustments)

    /**
     * Sets the browser window width in pixels.
     * <p><b>Thread Safety:</b> This setter modifies shared static state.
     * It must be called before parallel driver initialization, not during.</p>
     *
     * @param frameWidth the width in pixels
     */
    public static void setFrameWidth(int frameWidth) {
        WebDriverFactory.frameWidth = frameWidth;
    }

    /**
     * Sets the browser window height in pixels.
     * <p><b>Thread Safety:</b> This setter modifies shared static state.
     * It must be called before parallel driver initialization, not during.</p>
     *
     * @param frameHeight the height in pixels
     */
    public static void setFrameHeight(int frameHeight) {
        WebDriverFactory.frameHeight = frameHeight;
    }

    /**
     * Enables or disables headless mode.
     * <p><b>Thread Safety:</b> This setter modifies shared static state.
     * It must be called before parallel driver initialization, not during.</p>
     *
     * @param headless true to enable headless mode
     */
    public static void setHeadless(boolean headless) {
        WebDriverFactory.headless = headless;
    }

    /**
     * Enables or disables window maximization on driver startup.
     * <p><b>Thread Safety:</b> This setter modifies shared static state.
     * It must be called before parallel driver initialization, not during.</p>
     *
     * @param maximise true to maximize the window on startup
     */
    public static void setMaximise(boolean maximise) {
        WebDriverFactory.maximise = maximise;
    }

    /**
     * Sets the implicit wait timeout (in seconds) for the driver.
     * <p><b>Thread Safety:</b> This setter modifies shared static state.
     * It must be called before parallel driver initialization, not during.</p>
     *
     * @param driverTimeout the timeout in seconds
     */
    public static void setDriverTimeout(long driverTimeout) {
        WebDriverFactory.driverTimeout = driverTimeout;
    }

    /**
     * Enables or disables cookie deletion on driver initialization.
     * <p><b>Thread Safety:</b> This setter modifies shared static state.
     * It must be called before parallel driver initialization, not during.</p>
     *
     * @param deleteCookies true to delete cookies on initialization
     */
    public static void setDeleteCookies(boolean deleteCookies) {
        WebDriverFactory.deleteCookies = deleteCookies;
    }

    /**
     * Enables or disables Selenium Grid usage.
     * <p><b>Thread Safety:</b> This setter modifies shared static state.
     * It must be called before parallel driver initialization, not during.</p>
     *
     * @param useSeleniumGrid true to use Selenium Grid
     */
    public static void setUseSeleniumGrid(boolean useSeleniumGrid) {
        WebDriverFactory.useSeleniumGrid = useSeleniumGrid;
    }

    /**
     * Enables or disables acceptance of insecure SSL certificates.
     * <p><b>Thread Safety:</b> This setter modifies shared static state.
     * It must be called before parallel driver initialization, not during.</p>
     *
     * @param insecureLocalHost true to accept insecure certificates
     */
    public static void setInsecureLocalHost(boolean insecureLocalHost) {
        WebDriverFactory.insecureLocalHost = insecureLocalHost;
    }

    /**
     * Enables or disables browser notifications.
     * <p><b>Thread Safety:</b> This setter modifies shared static state.
     * It must be called before parallel driver initialization, not during.</p>
     *
     * @param disableNotifications true to disable browser notifications
     */
    public static void setDisableNotifications(boolean disableNotifications) {
        WebDriverFactory.disableNotifications = disableNotifications;
    }

    /**
     * Sets the page load strategy for the driver.
     * <p><b>Thread Safety:</b> This setter modifies shared static state.
     * It must be called before parallel driver initialization, not during.</p>
     *
     * @param loadStrategy the page load strategy
     */
    public static void setLoadStrategy(PageLoadStrategy loadStrategy) {
        WebDriverFactory.loadStrategy = loadStrategy;
    }

    /**
     * Enables or disables WebDriverManager for automatic driver binary management.
     * <p><b>Thread Safety:</b> This setter modifies shared static state.
     * It must be called before parallel driver initialization, not during.</p>
     *
     * @param useWDM true to enable WebDriverManager
     */
    public static void setUseWDM(Boolean useWDM) {
        WebDriverFactory.useWDM = useWDM;
    }

    /**
     * Enables or disables allowing remote origins (fixes connection issues in newer Chrome).
     * <p><b>Thread Safety:</b> This setter modifies shared static state.
     * It must be called before parallel driver initialization, not during.</p>
     *
     * @param allowRemoteOrigin true to allow remote origins
     */
    public static void setAllowRemoteOrigin(Boolean allowRemoteOrigin) {
        WebDriverFactory.allowRemoteOrigin = allowRemoteOrigin;
    }

    /**
     * Sets the Selenium log level (e.g., "off", "info", "debug").
     * <p><b>Thread Safety:</b> This setter modifies shared static state.
     * It must be called before parallel driver initialization, not during.</p>
     *
     * @param logLevel the log level string
     */
    public static void setLogLevel(String logLevel) {
        WebDriverFactory.logLevel = logLevel;
    }

    /**
     * Sets the Selenium Grid hub URL.
     * <p><b>Thread Safety:</b> This setter modifies shared static state.
     * It must be called before parallel driver initialization, not during.</p>
     *
     * @param hubUrl the Selenium Grid hub URL
     */
    public static void setHubUrl(String hubUrl) {
        WebDriverFactory.hubUrl = hubUrl;
    }

    /**
     * Sets the default browser type (e.g., "chrome", "firefox").
     * <p><b>Thread Safety:</b> This setter modifies shared static state.
     * It must be called before parallel driver initialization, not during.</p>
     *
     * @param browser the browser name
     */
    public static void setBrowser(String browser) {
        WebDriverFactory.browser = browser;
    }

    /**
     * Enables or disables the --no-sandbox browser argument.
     * <p><b>Thread Safety:</b> This setter modifies shared static state.
     * It must be called before parallel driver initialization, not during.</p>
     *
     * @param noSandbox true to add --no-sandbox argument
     */
    public static void setNoSandbox(boolean noSandbox) {WebDriverFactory.noSandbox = noSandbox;}

    // Getters for configuration values

    /** @return the browser window width in pixels */
    public static int getFrameWidth() {
        return frameWidth;
    }

    /** @return the browser window height in pixels */
    public static int getFrameHeight() {
        return frameHeight;
    }

    /** @return true if headless mode is enabled */
    public static boolean isHeadless() {
        return headless;
    }

    /** @return true if window maximization is enabled */
    public static boolean isMaximise() {
        return maximise;
    }

    /** @return the driver timeout in seconds */
    public static long getDriverTimeout() {
        return driverTimeout;
    }

    /** @return true if cookies are deleted on initialization */
    public static boolean isDeleteCookies() {
        return deleteCookies;
    }

    /** @return true if Selenium Grid is enabled */
    public static boolean isUseSeleniumGrid() {
        return useSeleniumGrid;
    }

    /** @return true if insecure certificates are accepted */
    public static boolean isInsecureLocalHost() {
        return insecureLocalHost;
    }

    /** @return true if browser notifications are disabled */
    public static boolean isDisableNotifications() {
        return disableNotifications;
    }

    /** @return the page load strategy */
    public static PageLoadStrategy getLoadStrategy() {
        return loadStrategy;
    }

    /** @return true if WebDriverManager is enabled */
    public static Boolean getUseWDM() {
        return useWDM;
    }

    /** @return true if remote origins are allowed */
    public static Boolean getAllowRemoteOrigin() {
        return allowRemoteOrigin;
    }

    /** @return the Selenium log level */
    public static String getLogLevel() {
        return logLevel;
    }

    /** @return the Selenium Grid hub URL */
    public static String getHubUrl() {
        return hubUrl;
    }

    /** @return the default browser name */
    public static String getBrowser() {
        return browser;
    }

    /** @return true if --no-sandbox is enabled */
    public static boolean isNoSandbox() {return noSandbox;}
}
