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
@SuppressWarnings("unused")
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
        RemoteWebDriver driver;

        try {
            if (browserType == null) browserType = BrowserType.fromString(browser);

            if (useSeleniumGrid){
                ImmutableCapabilities capabilities = new ImmutableCapabilities("browserName", browserType.getDriverKey());
                driver = new RemoteWebDriver(new URL(hubUrl), capabilities);
            }
            else {driver = driverSwitch(headless, useWDM, insecureLocalHost, noSandbox, disableNotifications, allowRemoteOrigin, loadStrategy, browserType, mobileMode, preferredDevice);}

            assert driver != null;
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
        CHROME("Chrome"),
        FIREFOX("Firefox"),
        SAFARI("Safari"),
        EDGE("Edge"),
        OPERA("Opera");

        final String driverName;

        BrowserType(String driverName){
            this.driverName = driverName;
        }

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

        public String getDriverName() {
            return driverName;
        }
    }

    // Setters for static configuration (useful for runtime adjustments)

    public static void setFrameWidth(int frameWidth) {
        WebDriverFactory.frameWidth = frameWidth;
    }

    public static void setFrameHeight(int frameHeight) {
        WebDriverFactory.frameHeight = frameHeight;
    }

    public static void setHeadless(boolean headless) {
        WebDriverFactory.headless = headless;
    }

    public static void setMaximise(boolean maximise) {
        WebDriverFactory.maximise = maximise;
    }

    public static void setDriverTimeout(long driverTimeout) {
        WebDriverFactory.driverTimeout = driverTimeout;
    }

    public static void setDeleteCookies(boolean deleteCookies) {
        WebDriverFactory.deleteCookies = deleteCookies;
    }

    public static void setUseSeleniumGrid(boolean useSeleniumGrid) {
        WebDriverFactory.useSeleniumGrid = useSeleniumGrid;
    }

    public static void setInsecureLocalHost(boolean insecureLocalHost) {
        WebDriverFactory.insecureLocalHost = insecureLocalHost;
    }

    public static void setDisableNotifications(boolean disableNotifications) {
        WebDriverFactory.disableNotifications = disableNotifications;
    }

    public static void setLoadStrategy(PageLoadStrategy loadStrategy) {
        WebDriverFactory.loadStrategy = loadStrategy;
    }

    public static void setUseWDM(Boolean useWDM) {
        WebDriverFactory.useWDM = useWDM;
    }

    public static void setAllowRemoteOrigin(Boolean allowRemoteOrigin) {
        WebDriverFactory.allowRemoteOrigin = allowRemoteOrigin;
    }

    public static void setLogLevel(String logLevel) {
        WebDriverFactory.logLevel = logLevel;
    }

    public static void setHubUrl(String hubUrl) {
        WebDriverFactory.hubUrl = hubUrl;
    }

    public static void setBrowser(String browser) {
        WebDriverFactory.browser = browser;
    }

    public static void setNoSandbox(boolean noSandbox) {WebDriverFactory.noSandbox = noSandbox;}

    // Getters for configuration values

    public static int getFrameWidth() {
        return frameWidth;
    }

    public static int getFrameHeight() {
        return frameHeight;
    }

    public static boolean isHeadless() {
        return headless;
    }

    public static boolean isMaximise() {
        return maximise;
    }

    public static long getDriverTimeout() {
        return driverTimeout;
    }

    public static boolean isDeleteCookies() {
        return deleteCookies;
    }

    public static boolean isUseSeleniumGrid() {
        return useSeleniumGrid;
    }

    public static boolean isInsecureLocalHost() {
        return insecureLocalHost;
    }

    public static boolean isDisableNotifications() {
        return disableNotifications;
    }

    public static PageLoadStrategy getLoadStrategy() {
        return loadStrategy;
    }

    public static Boolean getUseWDM() {
        return useWDM;
    }

    public static Boolean getAllowRemoteOrigin() {
        return allowRemoteOrigin;
    }

    public static String getLogLevel() {
        return logLevel;
    }

    public static String getHubUrl() {
        return hubUrl;
    }

    public static String getBrowser() {
        return browser;
    }

    public static boolean isNoSandbox() {return noSandbox;}
}
