package utils.driver;

import com.github.webdriverextensions.WebComponent;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.Printer;

public class Driver extends WebComponent {

	public static RemoteWebDriver driver;
	public static WebDriverWait wait;

	Printer log = new Printer(Driver.class);

	public void setup(String browserName){
		log.new info("Initializing driver");
		driver = DriverFactory.getDriver(browserName, driver);
		assert driver != null;
		wait = new WebDriverWait(driver, 15);
	}

	public void teardown(){
		log.new info("Finalizing driver...");
		driver.quit();
	}
}