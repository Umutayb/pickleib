package pickleib.driver;

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

	/**
	 * RemoteWebDriver instance
	 */
	public static RemoteWebDriver driver;

	/**
	 * WebDriverWait instance
	 */
	public static WebDriverWait wait;

	static PropertiesReader reader = new PropertiesReader("properties-from-pom.properties");
	static StringUtilities strUtils = new StringUtilities();
	static Printer log = new Printer(Driver.class);

	/**
	 * Initializes a specified type of driver
	 *
	 * @param driverType driver type
	 */
	public static void initialize(DriverFactory.DriverType driverType){
		log.new Info("Initializing " + driverType.getDriverName() + " driver...");
		driver = DriverFactory.getDriver(driverType);
		wait = new WebDriverWait(driver, Duration.of(DriverFactory.driverTimeout, ChronoUnit.SECONDS));
	}

	/**
	 * Initializes driver according to the browser property
	 */
	public static void initialize(){
		String driverName = strUtils.firstLetterCapped(reader.getProperty("browser"));
		initialize(DriverFactory.DriverType.fromString(driverName));
	}

	/**
	 * @deprecated This method is no longer maintained
	 */
	@Deprecated(since = "1.5.6")
	public static void initialize(String id, String password, DriverFactory.DriverType driverType){ //Only works with chrome!
		initialize(driverType);
		DevTools dev = ((ChromeDriver) driver).getDevTools();
		dev.createSession();
		dev.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
		Map<String, Object> map = new HashMap<>();
		map.put("Authorization", "Basic " + (Base64.encode((id + ":" + password).getBytes())));
		dev.send(Network.setExtraHTTPHeaders(new Headers(map)));
	}

	/**
	 * Quits the driver session
	 */
	public static void terminate(){
		log.new Info("Terminating driver...");
		driver.quit();
	}
}