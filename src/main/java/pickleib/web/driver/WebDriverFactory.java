package pickleib.web.driver;

import context.ContextStore;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.chromium.ChromiumDriver;
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
import utils.StringUtilities;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;

import static utils.StringUtilities.Color.*;

@SuppressWarnings("unused")
public class WebDriverFactory implements DriverFactory {

    static StringUtilities strUtils = new StringUtilities();

    /**
     * determines frameWidth value
     */
    static int frameWidth = Integer.parseInt(ContextStore.get("frame-width","1920"));

    /**
     * determines frameHeight value
     */
    static int frameHeight = Integer.parseInt(ContextStore.get("frame-height","1080"));

    /**
     * session runs headless if true
     */
    static boolean headless = Boolean.parseBoolean(ContextStore.get("headless", "false"));

    /**
     * session runs in mobile mode if true
     */
    static boolean mobileMode = Boolean.parseBoolean(ContextStore.get("mobile-mode", "false"));

    /**
     * Preferred EmulatedDevice
     */
    static EmulatedDevice preferredDevice = EmulatedDevice.getType(ContextStore.get("emulated-device", "iPhone12Pro"));

    /**
     * session runs in tablet mode if true
     */
    static boolean tabletMode = Boolean.parseBoolean(ContextStore.get("tablet-mode", "false"));

    /**
     * maximizes a session window if true
     */
    static boolean maximise = Boolean.parseBoolean(ContextStore.get("driver-maximize", "false"));

    /**
     * determines driverTimeout duration
     */
    static long driverTimeout = Long.parseLong(ContextStore.get("driver-timeout", "15000"))/1000;

    /**
     * cookies are deleted if true
     */
    static boolean deleteCookies = Boolean.parseBoolean(ContextStore.get("delete-cookies", "false"));

    /**
     * Selenium Grid is used if true
     */
    static boolean useSeleniumGrid = Boolean.parseBoolean(ContextStore.get("selenium-grid", "false"));

    /**
     * enables insecure local host if true
     */
    static boolean insecureLocalHost = Boolean.parseBoolean(ContextStore.get("insecure-localhost", "false"));

    /**
     * enables insecure local host if true
     */
    static boolean noSandbox = Boolean.parseBoolean(ContextStore.get("driver-no-sandbox", "false"));

    /**
     * disables browser notifications if true
     */
    static boolean disableNotifications = Boolean.parseBoolean(ContextStore.get("disable-notifications", "true"));

    /**
     * determines page load strategy
     */
    static PageLoadStrategy loadStrategy = PageLoadStrategy.fromString(ContextStore.get("load-strategy", "normal"));

    /**
     * determines usage of web driver manager
     */
    static Boolean useWDM = Boolean.parseBoolean(ContextStore.get("web-driver-manager", "false"));

    /**
     * determines usage of web driver manager
     */
    static Boolean allowRemoteOrigin = Boolean.parseBoolean(ContextStore.get("allow-remote-origin", "true"));

    /**
     * The logging level used by Pickleib.
     * This value can be set in the properties file with the key "selenium-log-level".
     * If not specified in the properties file, the default value is "off".
     */
    static String logLevel = ContextStore.get("selenium-log-level", "off");

    /**
     * The URL of the Selenium Grid hub.
     * This value can be set in the properties file with the key "hub-url".
     * If not specified in the properties file, the default value is an empty string.
     */
    static String hubUrl = ContextStore.get("hub-url","");

    /**
     * The browser used for tests.
     * This value can be set in the properties file with the key "browser".
     * If not specified in the properties file, the default value is "chrome".
     */
    static String browser = ContextStore.get("browser", "chrome");

    /**
     * DriverFactory Logger.
     */
    private static final Printer log = new Printer(WebDriverFactory.class);

    /**
     * Logging utilities.
     */
    private static final LogUtilities logUtils = new LogUtilities();

    /**
     * Initializes and returns a driver of specified type
     * @param browserType driver type
     * @return returns driver
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
            if (maximise) driver.manage().window().maximize();
            else driver.manage().window().setSize(new Dimension(frameWidth, frameHeight));
            driver.setLogLevel(logUtils.getLevel(logLevel));
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
                throw new RuntimeException(strUtils.highlighted(YELLOW, "Something went wrong while selecting a driver ")+strUtils.highlighted(RED, "\n\t"+gamma), gamma);
        }
    }

    /**
     * Selects the driver type and assigns desired capabilities
     *
     * @param headless session runs headless if true
     * @param useWDM WebDriverManager is used if true
     * @param insecureLocalHost enables insecure local host if true
     * @param disableNotifications disables browser notifications if true
     * @param loadStrategy determines page load strategy
     * @param browserType driver type
     * @return returns the configured driver
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
                        options.addArguments("--allow-insecure-localhost");
                        options.addArguments("--ignore-certificate-errors");
                    }
                    if (noSandbox) options.addArguments("--no-sandbox");
                    options.setPageLoadStrategy(loadStrategy);
                    options.setAcceptInsecureCerts(insecureLocalHost);
                    if (allowRemoteOrigin) options.addArguments("--remote-allow-origins=*");
                    if (disableNotifications) options.addArguments("disable-notifications");
                    if (headless) options.addArguments("--headless=new");
                    if (useWDM) WebDriverManager.firefoxdriver().setup();
                    return new FirefoxDriver(options);
                }
                case SAFARI -> {
                    SafariOptions options = new SafariOptions();
                    if (useWDM) WebDriverManager.safaridriver().setup();
                    return new SafariDriver(options);
                }
                default -> {
                    throw new PickleibException("No such driver was defined.");
                }
            }
        }
        catch (SessionNotCreatedException sessionException){
            log.warning(sessionException.getLocalizedMessage());
            if (!useWDM) return driverSwitch(headless, true, insecureLocalHost, noSandbox, disableNotifications, allowRemoteOrigin, loadStrategy, browserType, mobileMode, preferredDevice);
            else return null;
        }
    }

    /**
     * Available driver types
     */
    public enum BrowserType {
        CHROME("Chrome"),
        FIREFOX("Firefox"),
        SAFARI("Safari"),
        OPERA("Opera");

        final String driverName;

        BrowserType(String driverName){
            this.driverName = driverName;
        }

        public String getDriverName() {
            return driverName;
        }
        public String getDriverKey() {
            return driverName.toLowerCase();
        }

        /**
         * Returns a driver type matching a given text (Non-case-sensitive)
         * @param text desired driver
         * @return returns matching a driver type
         */
        public static BrowserType fromString(String text) {
            if (text != null)
                for (BrowserType browserType :values())
                    if (browserType.name().equalsIgnoreCase(text))
                        return browserType;
            return null;
        }
    }

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

    public static StringUtilities getStrUtils() {
        return strUtils;
    }

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
