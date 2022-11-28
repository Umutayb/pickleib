package utils.driver;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.Assert;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.ImmutableCapabilities;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;
import utils.Printer;
import utils.StringUtilities;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Objects;
import java.util.Properties;
import static resources.Colors.*;

public class DriverFactory {

    private static final Printer log = new Printer(DriverFactory.class);

    public static RemoteWebDriver getDriver(String driverName, RemoteWebDriver driver){
        Properties properties = new Properties();
        StringUtilities strUtils = new StringUtilities();
        int frameWidth = Integer.parseInt(properties.getProperty("frame-width","1920"));
        int frameHeight = Integer.parseInt(properties.getProperty("frame-height","1080"));
        long timeout = Long.parseLong(properties.getProperty("driver-timeout", "15000"))/1000;
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
            else {
                switch (driverName.toLowerCase()) {
                    case "chrome" -> {
                        ChromeOptions chromeOptions = new ChromeOptions();
                        chromeOptions.addArguments("disable-notifications");
                        chromeOptions.setHeadless(Boolean.parseBoolean(properties.getProperty("headless", "false")));
                        chromeOptions.addArguments("window-size=" + frameWidth + "," + frameHeight);
                        driver = new ChromeDriver(chromeOptions);
                    }
                    case "firefox" -> {
                        FirefoxOptions firefoxOptions = new FirefoxOptions();
                        firefoxOptions.addArguments("disable-notifications");
                        firefoxOptions.setHeadless(Boolean.parseBoolean(properties.getProperty("headless", "false")));
                        firefoxOptions.addArguments("window-size=" + frameWidth + "," + frameHeight);
                        driver = new FirefoxDriver(firefoxOptions);
                    }
                    case "safari" -> {
                        SafariOptions safariOptions = new SafariOptions();
                        driver = new SafariDriver(safariOptions);
                    }
                    default -> {
                        Assert.fail("No such driver was defined.");
                        return null;
                    }
                }
            }
            driver.manage().window().setSize(new Dimension(frameWidth, frameHeight));
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(timeout));
            if (Boolean.parseBoolean(properties.getProperty("delete-cookies", "false")))
                driver.manage().deleteAllCookies();
            if (Boolean.parseBoolean(properties.getProperty("driver-maximize", "false")))
                driver.manage().window().maximize();
            log.new Important(driverName + GRAY + " was selected");
            return driver;
        }
        catch (SessionNotCreatedException sessionException){
            if (sessionException.getLocalizedMessage().contains("Could not start a new session. Response code 500. Message: session not created: This version of")){
                switch (Objects.requireNonNull(driverName).toLowerCase()) {
                    case "chrome" -> {
                        ChromeOptions chromeOptions = new ChromeOptions();
                        chromeOptions.addArguments("disable-notifications");
                        chromeOptions.setHeadless(Boolean.parseBoolean(properties.getProperty("headless", "false")));
                        chromeOptions.addArguments("window-size=" + frameWidth + "," + frameHeight);
                        WebDriverManager.chromedriver().setup();
                        driver = new ChromeDriver(chromeOptions);
                    }
                    case "firefox" -> {
                        FirefoxOptions firefoxOptions = new FirefoxOptions();
                        firefoxOptions.addArguments("disable-notifications");
                        firefoxOptions.setHeadless(Boolean.parseBoolean(properties.getProperty("headless", "false")));
                        firefoxOptions.addArguments("window-size=" + frameWidth + "," + frameHeight);
                        WebDriverManager.firefoxdriver().setup();
                        driver = new FirefoxDriver(firefoxOptions);
                    }
                    case "safari" -> {
                        SafariOptions safariOptions = new SafariOptions();
                        WebDriverManager.safaridriver().setup();
                        driver = new SafariDriver(safariOptions);
                    }
                    default -> {
                        Assert.fail("No such driver was defined.");
                        return null;
                    }
                }
                driver.manage().window().setSize(new Dimension(frameWidth, frameHeight));
                driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(timeout));
                if (Boolean.parseBoolean(properties.getProperty("delete-cookies", "false")))
                    driver.manage().deleteAllCookies();
                if (Boolean.parseBoolean(properties.getProperty("driver-maximize", "false")))
                    driver.manage().window().maximize();
                log.new Important(driverName + GRAY + " was selected");
                return driver;
            }
            else {throw new RuntimeException(sessionException);}
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
}
