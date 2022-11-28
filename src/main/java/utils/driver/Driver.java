package utils.driver;

import com.github.webdriverextensions.WebComponent;
import org.apache.xerces.impl.dv.util.Base64;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v85.network.Network;
import org.openqa.selenium.devtools.v85.network.model.Headers;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.Printer;
import utils.PropertiesReader;
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
	StringUtilities strUtils = new StringUtilities();
	Printer log = new Printer(Driver.class);

	public void initialize(){
		log.new Info("Initializing driver");
		driver = DriverFactory.getDriver(strUtils.firstLetterCapped(reader.getProperty("browser")));
		wait = new WebDriverWait(driver, Duration.of(15, ChronoUnit.SECONDS));
	}

	public void initialize(String id, String password){ //Only works with chrome!
		initialize();
		DevTools dev = ((ChromeDriver) driver).getDevTools();
		dev.createSession();
		dev.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
		Map<String, Object> map = new HashMap<>();
		map.put("Authorization", "Basic " + (Base64.encode((id + ":" + password).getBytes())));
		dev.send(Network.setExtraHTTPHeaders(new Headers(map)));
	}

	public void terminate(){
		log.new Info("Terminating driver...");
		driver.quit();
	}
}