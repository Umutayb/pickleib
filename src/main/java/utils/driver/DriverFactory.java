package utils.driver;

import org.junit.Assert;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import java.io.FileReader;
import java.net.URL;
import java.util.Properties;
import static resources.Colors.*;
import java.util.concurrent.TimeUnit;

public class DriverFactory {

    public static Properties properties = new Properties();

    public static RemoteWebDriver driverSetup(String driverName, RemoteWebDriver driver){
        try {

            properties.load(new FileReader("src/test/resources/test.properties"));

            DesiredCapabilities capabilities = null;

            if (Boolean.parseBoolean(properties.getProperty("selenium.grid"))){

                switch (driverName.toLowerCase()){
                    case "chrome":
                        capabilities = DesiredCapabilities.chrome();
                        break;

                    case "firefox":
                        capabilities = DesiredCapabilities.firefox();
                        break;

                    case "safari":
                        capabilities = DesiredCapabilities.safari();
                        break;

                    case "edge":
                        capabilities = DesiredCapabilities.edge();
                        break;

                    case "opera":
                        capabilities = DesiredCapabilities.operaBlink();
                        break;

                    default:
                        Assert.fail(YELLOW+"The driver type \""+driverName+"\" was undefined."+RESET);
                }
                driver = new RemoteWebDriver(new URL(properties.getProperty("hub.url")), capabilities);
            }
            else {
                switch (properties.getProperty("browser").toLowerCase()){
                    case "chrome":
                        ChromeOptions chromeOptions = new ChromeOptions();
                        chromeOptions.addArguments("disable-notifications");
                        System.setProperty("webdriver.chrome.driver", "src/test/resources/drivers/chromedriver");
                        driver = new ChromeDriver(chromeOptions);
                        break;

                    case "firefox":
                        FirefoxOptions firefoxOptions = new FirefoxOptions();
                        firefoxOptions.addArguments("disable-notifications");
                        System.setProperty("webdriver.gecko.driver", "src/test/resources/drivers/geckodriver");
                        driver = new FirefoxDriver(firefoxOptions);
                        break;

                    default:
                        Assert.fail("No such driver was defined.");
                        return null ;
                }
            }
            driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
            driver.manage().window().maximize();
            System.out.println(PURPLE+driverName+GRAY+" was selected"+RESET);
            return driver;

        }catch (Exception gamma) {
            if(gamma.toString().contains("Could not start a new session. Possible causes are invalid address of the remote server or browser start-up failure")){
                System.out.println(GRAY+"Please make sure the "+PURPLE+"Selenium Grid "+GRAY+"is on & verify the port that its running on at 'resources/test.properties'."+RESET);
                Assert.fail(YELLOW+gamma+RESET);
            }
            else {
                Assert.fail(YELLOW+"Something went wrong while selecting a driver "+"\n\t"+RED+gamma+RESET);
            }
            driver.quit();
            return null;
        }
    }
}
