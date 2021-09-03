package utils.driver;

import org.junit.Assert;
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

            properties.load(new FileReader("src/test/java/resources/test.properties"));

            DesiredCapabilities capabilities = null;

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
            driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
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
