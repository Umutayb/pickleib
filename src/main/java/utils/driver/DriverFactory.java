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
import utils.StringUtilities;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.Objects;
import static resources.Colors.*;
import static utils.FileUtilities.properties;

public class DriverFactory {

    private static final Printer log = new Printer(DriverFactory.class);

    static int frameWidth;
    static int frameHeight;
    static long driverTimeout;
    static boolean headless;
    static boolean deleteCookies;
    static boolean maximise;
    static boolean insecureLocalHost;
    static boolean disableNotifications;
    static PageLoadStrategy loadStrategy;

    public static RemoteWebDriver getDriver(String driverName){
        StringUtilities strUtils = new StringUtilities();
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

            if (driverName == null) driverName = strUtils.firstLetterCapped(properties.getProperty("browser", "chrome"));

            if (Boolean.parseBoolean(properties.getProperty("selenium-grid", "false"))){
                ImmutableCapabilities capabilities;
                switch (driverName.toLowerCase()) {
                    case "chrome" -> capabilities = new ImmutableCapabilities("browserName", "chrome");
                    case "firefox" -> capabilities = new ImmutableCapabilities("browserName", "firefox");
                    case "opera" -> capabilities = new ImmutableCapabilities("browserName", "opera");
                    default -> {
                        capabilities = null;
                        Assert.fail(YELLOW + "The driver type \"" + driverName + "\" was undefined." + RESET);
                    }
                }
                driver = new RemoteWebDriver(new URL(properties.getProperty("hub-url","")), capabilities);
            }
            else {driver = driverSwitch(headless, false, insecureLocalHost, disableNotifications, loadStrategy, frameWidth, frameHeight, driverName);}
            assert driver != null;
            driver.manage().window().setSize(new Dimension(frameWidth, frameHeight));
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(driverTimeout));
            if (deleteCookies) driver.manage().deleteAllCookies();
            if (maximise) driver.manage().window().maximize();
            log.new Important(driverName + GRAY + " was selected");
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
            String driverName){
        if (useWDM) log.new Warning("Using WebDriverManager...");
        try {
            switch (Objects.requireNonNull(driverName).toLowerCase()) {
                case "chrome" -> {
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
                case "firefox" -> {
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
                case "safari" -> {
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
            if (!useWDM) return driverSwitch(headless, true, insecureLocalHost, disableNotifications, loadStrategy, frameWidth, frameHeight, driverName);
            else return null;
        }
    }
}
