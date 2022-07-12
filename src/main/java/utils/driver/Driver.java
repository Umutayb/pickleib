package utils.driver;

import com.github.webdriverextensions.WebComponent;
import io.cucumber.core.api.Scenario;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.Printer;
import utils.PropertiesReader;
import utils.ScreenCaptureUtility;
import utils.StringUtilities;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class Driver extends WebComponent {

	public static RemoteWebDriver driver;
	public static WebDriverWait wait;

	PropertiesReader reader = new PropertiesReader("properties-from-pom.properties");
	ScreenCaptureUtility capture = new ScreenCaptureUtility();
	StringUtilities strUtils = new StringUtilities();
	Printer log = new Printer(Driver.class);

	public void initialize(){
		log.new Info("Initializing driver");
		driver = DriverFactory.getDriver(strUtils.firstLetterCapped(reader.getProperty("browser")), driver);
		assert driver != null;
		wait = new WebDriverWait(driver, Duration.of(15, ChronoUnit.SECONDS));
	}

	public void terminate(Scenario scenario){
		log.new Info("Finalizing driver...");
		if (scenario.isFailed())
			capture.captureScreen(scenario.getName()+"@"+scenario.getLine(),driver);
		driver.quit();
	}
}