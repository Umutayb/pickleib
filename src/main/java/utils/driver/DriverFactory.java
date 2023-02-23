package utils.driver;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;
import utils.Printer;
import utils.PropertyUtility;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.Arrays;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Level;

import static resources.Colors.*;

public class DriverFactory {

    private static final Printer log = new Printer(DriverFactory.class);
    static Properties properties = PropertyUtility.properties;

    /**
     * determines frameWidth value
     */
    static int frameWidth;

    /**
     * determines frameHeight value
     */
    static int frameHeight;

    /**
     * session runs headless if true
     */
    static boolean headless;

    /**
     * maximises session window if true
     */
    static boolean maximise;

    /**
     * determines driverTimeout duration
     */
    static long driverTimeout;

    /**
     * cookies are deleted if true
     */
    static boolean deleteCookies;

    /**
     * Selenium Grid is used if true
     */
    static boolean useSeleniumGrid;

    /**
     * enables insecure local host if true
     */
    static boolean insecureLocalHost;

    /**
     * disables browser notifications if true
     */
    static boolean disableNotifications;

    /**
     * determines page load strategy
     */
    static PageLoadStrategy loadStrategy;

    /**
     * Initializes and returns a driver of specified type
     * @param driverType driver type
     * @return returns driver
     */
    public static RemoteWebDriver getDriver(DriverType driverType){
        useSeleniumGrid = Boolean.parseBoolean(properties.getProperty("selenium-grid", "false"));
        frameWidth = Integer.parseInt(properties.getProperty("frame-width","1920"));
        frameHeight = Integer.parseInt(properties.getProperty("frame-height","1080"));
        driverTimeout = Long.parseLong(properties.getProperty("driver-timeout", "15000"))/1000;
        headless = Boolean.parseBoolean(properties.getProperty("headless", "false"));
        deleteCookies = Boolean.parseBoolean(properties.getProperty("delete-cookies", "false"));
        maximise = Boolean.parseBoolean(properties.getProperty("driver-maximize", "false"));
        insecureLocalHost = Boolean.parseBoolean(properties.getProperty("insecure-localhost", "false"));
        loadStrategy = PageLoadStrategy.fromString(properties.getProperty("load-strategy", "normal"));
        disableNotifications = Boolean.parseBoolean(properties.getProperty("disable-notifications", "true"));

        RemoteWebDriver driver;

        try {
            if (driverType == null) driverType = DriverType.fromString(properties.getProperty("browser", "chrome"));

            if (useSeleniumGrid){
                ImmutableCapabilities capabilities = new ImmutableCapabilities("browserName", driverType.getDriverKey());
                driver = new RemoteWebDriver(new URL(properties.getProperty("hub-url","")), capabilities);
            }
            else {driver = driverSwitch(headless, false, insecureLocalHost, disableNotifications, loadStrategy, driverType);}

            assert driver != null;
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(driverTimeout));
            if (deleteCookies) driver.manage().deleteAllCookies();
            if (maximise) driver.manage().window().maximize();
            else driver.manage().window().setSize(new Dimension(frameWidth, frameHeight));
            driver.setLogLevel(getLevel(properties.getProperty("selenium-log-level", "off")));
            log.new Important(driverType.getDriverName() + GRAY + " was selected");
            return driver;
        }
        catch (IOException malformedURLException) {throw new RuntimeException(malformedURLException);}
        catch (Exception gamma) {
            if(gamma.toString().contains("Could not start a new session. Possible causes are invalid address of the remote server or browser start-up failure")){
                log.new Info("Please make sure the "+PURPLE+"Selenium Grid "+GRAY+"is on & verify the port that its running on at 'resources/test.properties'."+RESET);
                throw new RuntimeException(gamma);
            }
            else
            {throw new RuntimeException(YELLOW+"Something went wrong while selecting a driver "+"\n\t"+RED+gamma+RESET);}
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
     * @param driverType driver type
     * @return returns the configured driver
     */
    static RemoteWebDriver driverSwitch(
            Boolean headless,
            Boolean useWDM,
            Boolean insecureLocalHost, 
            Boolean disableNotifications,
            PageLoadStrategy loadStrategy,
            DriverType driverType){
        if (useWDM) log.new Warning("Using WebDriverManager...");
        try {
            switch (driverType) {
                case CHROME -> {
                    ChromeOptions chromeOptions = new ChromeOptions();
                    if (disableNotifications) chromeOptions.addArguments("disable-notifications");
                    if (insecureLocalHost){
                        chromeOptions.addArguments("--allow-insecure-localhost");
                        chromeOptions.addArguments("--ignore-certificate-errors");
                    }
                    chromeOptions.setPageLoadStrategy(loadStrategy);
                    chromeOptions.setAcceptInsecureCerts(insecureLocalHost);
                    if (headless) chromeOptions.addArguments("--headless=new");
                    if (useWDM) WebDriverManager.chromedriver().setup();
                    return new ChromeDriver(chromeOptions);
                }
                case FIREFOX -> {
                    FirefoxOptions firefoxOptions = new FirefoxOptions();
                    if (insecureLocalHost){
                        firefoxOptions.addArguments("--allow-insecure-localhost");
                        firefoxOptions.addArguments("--ignore-certificate-errors");
                    }
                    firefoxOptions.setPageLoadStrategy(loadStrategy);
                    firefoxOptions.setAcceptInsecureCerts(insecureLocalHost);
                    if (disableNotifications) firefoxOptions.addArguments("disable-notifications");
                    if (headless) firefoxOptions.addArguments("--headless=new");
                    if (useWDM) WebDriverManager.firefoxdriver().setup();
                    return new FirefoxDriver(firefoxOptions);
                }
                case SAFARI -> {
                    SafariOptions safariOptions = new SafariOptions();
                    if (useWDM) WebDriverManager.safaridriver().setup();
                    return new SafariDriver(safariOptions);
                }
                default -> {
                    Assert.fail("No such driver was defined.");
                    return null;
                }
            }
        }
        catch (SessionNotCreatedException sessionException){
            log.new Warning(sessionException);
            if (!useWDM) return driverSwitch(headless, true, insecureLocalHost, disableNotifications, loadStrategy, driverType);
            else return null;
        }
    }

    /**
     * returns log level from a string
     *
     * @param logLevel desired log level
     * @return returns log level
     */
    public static Level getLevel(String logLevel){
        return Level.parse(Objects.requireNonNull(Arrays.stream(Level.class.getFields()).filter(field -> {
            field.setAccessible(true);
            String fieldName = field.getName();
            return fieldName.equalsIgnoreCase(logLevel);
        }).findAny().orElse(null)).getName());
    }

    /**
     * available driver types
     */
    public enum DriverType {
        CHROME("Chrome"),
        FIREFOX("Firefox"),
        SAFARI("Safari"),
        OPERA("Opera");

        final String driverName;

        DriverType(String driverName){
            this.driverName = driverName;
        }

        public String getDriverName() {
            return driverName;
        }
        public String getDriverKey() {
            return driverName.toLowerCase();
        }

        /**
         * Returns driver type matching a given text (Non-case-sensitive)
         * @param text desired driver
         * @return returns matching driver type
         */
        public static DriverType fromString(String text) {
            if (text != null)
                for (DriverType driverType:values())
                    if (driverType.name().equalsIgnoreCase(text))
                        return driverType;
            return null;
        }
    }
}
