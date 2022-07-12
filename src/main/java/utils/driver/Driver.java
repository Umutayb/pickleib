package utils.driver;

import com.github.webdriverextensions.WebComponent;
import io.cucumber.core.api.Scenario;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v85.network.Network;
import org.openqa.selenium.devtools.v85.network.model.Headers;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testcontainers.shaded.com.trilead.ssh2.crypto.Base64;
import utils.Printer;
import utils.PropertiesReader;
import utils.ScreenCaptureUtility;
import utils.StringUtilities;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

	public void initialize(String id, String password){
		initialize();
		DevTools dev = ((ChromeDriver) driver).getDevTools();
		dev.createSession();
		dev.send(Network.enable(Optional.empty(), Optional.empty(), Optional.<Integer>empty()));
		Map<String, Object> map = new HashMap<>();
		map.put("Authorization", "Basic " + new String(Base64.encode((id + ":" + password).getBytes())));
		dev.send(Network.setExtraHTTPHeaders(new Headers(map)));
	}

	public void terminate(Scenario scenario){
		log.new Info("Finalizing driver...");
		if (scenario.isFailed())
			capture.captureScreen(scenario.getName()+"@"+scenario.getLine(),driver);
		driver.quit();
	}
}