package utils.driver;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverLogLevel;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxDriverLogLevel;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;
import utils.Printer;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import static resources.Colors.*;
import static utils.FileUtilities.properties;

public class DriverFactory {

    private static final Printer log = new Printer(DriverFactory.class);

    static int frameWidth;
    static int frameHeight;
    static boolean headless;
    static boolean maximise;
    static long driverTimeout;
    static boolean deleteCookies;
    static boolean useSeleniumGrid;
    static boolean insecureLocalHost;
    static boolean disableNotifications;
    static PageLoadStrategy loadStrategy;

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
            properties.load(new FileReader("src/test/resources/test.properties"));

            if (driverType == null) driverType = DriverType.fromString(properties.getProperty("browser", "chrome"));

            if (useSeleniumGrid){
                ImmutableCapabilities capabilities = new ImmutableCapabilities("browserName", driverType.getDriverKey());
                driver = new RemoteWebDriver(new URL(properties.getProperty("hub-url","")), capabilities);
            }
            else {driver = driverSwitch(headless, false, insecureLocalHost, disableNotifications, loadStrategy, frameWidth, frameHeight, driverType);}

            assert driver != null;
            driver.manage().window().setSize(new Dimension(frameWidth, frameHeight));
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(driverTimeout));
            if (deleteCookies) driver.manage().deleteAllCookies();
            if (maximise) driver.manage().window().maximize();
            log.new Important(driverType + GRAY + " was selected");
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

    static RemoteWebDriver driverSwitch(
            Boolean headless,
            Boolean useWDM,
            Boolean insecureLocalHost, 
            Boolean disableNotifications,
            PageLoadStrategy loadStrategy,
            Integer frameWidth,
            Integer frameHeight,
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
                    chromeOptions.setLogLevel(ChromeDriverLogLevel.fromString(properties.getProperty("log-level", "severe")));
                    chromeOptions.setHeadless(headless);
                    chromeOptions.addArguments("window-size=" + frameWidth + "," + frameHeight);
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
                    firefoxOptions.setLogLevel(FirefoxDriverLogLevel.fromString(properties.getProperty("log-level", "severe")));
                    if (disableNotifications) firefoxOptions.addArguments("disable-notifications");
                    firefoxOptions.setHeadless(headless);
                    firefoxOptions.addArguments("window-size=" + frameWidth + "," + frameHeight);
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
            if (!useWDM) return driverSwitch(headless, true, insecureLocalHost, disableNotifications, loadStrategy, frameWidth, frameHeight, driverType);
            else return null;
        }
    }

    enum DriverType {
        CHROME("Chrome"),
        FIREFOX("Firefox"),
        SAFARI("Safari"),
        OPERA("Opera");

        String driverName;

        DriverType(String driverName){
            this.driverName = driverName;
        }

        public String getDriverName() {
            return driverName;
        }
        public String getDriverKey() {
            return driverName.toLowerCase();
        }

        public static DriverType fromString(String text) {
            if (text != null)
                for (DriverType driverType:values())
                    if (driverType.name().equalsIgnoreCase(text))
                        return driverType;
            return null;
        }
    }
}
