package utils.driver;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver; // Currently replaced by bonigarcia drivers
import org.junit.Assert;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;
import utils.Printer;
import utils.StringUtilities;
import java.io.FileReader;
import java.net.URL;
import java.util.Properties;
import static resources.Colors.*;
import java.util.concurrent.TimeUnit;

public class DriverFactory {

    private static final Printer log = new Printer(DriverFactory.class);

    public static RemoteWebDriver getDriver(String driverName, RemoteWebDriver driver){
        Properties properties = new Properties();
        StringUtilities strUtils = new StringUtilities();
        try {
            properties.load(new FileReader("src/test/resources/test.properties"));

            if (driverName == null)
                driverName = strUtils.firstLetterCapped(properties.getProperty("browser"));

            if (Boolean.parseBoolean(properties.getProperty("selenium.grid"))){
                DesiredCapabilities capabilities;

                switch (driverName.toLowerCase()){
                    case "chrome":
                        capabilities = DesiredCapabilities.chrome();
                        break;

                    case "firefox":
                        capabilities = DesiredCapabilities.firefox();
                        break;

                    case "opera":
                        capabilities = DesiredCapabilities.operaBlink();
                        break;

                    default:
                        capabilities = null;
                        Assert.fail(YELLOW+"The driver type \""+driverName+"\" was undefined."+RESET);
                }
                driver = new RemoteWebDriver(new URL(properties.getProperty("hub.url")), capabilities);
            }
            else {
                switch (driverName.toLowerCase()){
                    case "chrome":
                        ChromeOptions chromeOptions = new ChromeOptions();
                        WebDriverManager.chromedriver().setup();
                        chromeOptions.addArguments("disable-notifications");
//                        System.setProperty("webdriver.chrome.driver", "src/test/resources/drivers/chromedriver");
                        chromeOptions.setHeadless(Boolean.parseBoolean(properties.getProperty("headless")));
                        driver = new ChromeDriver(chromeOptions);
                        break;

                    case "firefox":
                        FirefoxOptions firefoxOptions = new FirefoxOptions();
                        WebDriverManager.firefoxdriver().setup();
                        firefoxOptions.addArguments("disable-notifications");
//                        System.setProperty("webdriver.gecko.driver", "src/test/resources/drivers/geckodriver");
                        firefoxOptions.setHeadless(Boolean.parseBoolean(properties.getProperty("headless")));
                        driver = new FirefoxDriver(firefoxOptions);
                        break;

                    case "safari":
                        SafariOptions safariOptions = new SafariOptions();
//                        WebDriverManager.safaridriver().setup();
                        System.setProperty("webdriver.safari.driver","/usr/bin/safaridriver.");
                        driver = new SafariDriver(safariOptions);
                        break;

                    default:
                        Assert.fail("No such driver was defined.");
                        return null ;
                }
            }
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
            driver.manage().window().maximize();
            log.new important(driverName+GRAY+" was selected");
            return driver;

        }
        catch (Exception gamma) {
            if(gamma.toString().contains("Could not start a new session. Possible causes are invalid address of the remote server or browser start-up failure")){
                log.new info("Please make sure the "+PURPLE+"Selenium Grid "+GRAY+"is on & verify the port that its running on at 'resources/test.properties'."+RESET);
                Assert.fail(YELLOW+gamma+RESET);
            }
            else {Assert.fail(YELLOW+"Something went wrong while selecting a driver "+"\n\t"+RED+gamma+RESET);}
            driver.quit();
            return null;
        }
    }
}
