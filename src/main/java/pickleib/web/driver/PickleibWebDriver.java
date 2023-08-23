package pickleib.web.driver;

import org.bouncycastle.util.encoders.Base64;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v85.network.Network;
import org.openqa.selenium.devtools.v85.network.model.Headers;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import pickleib.utilities.PropertyLoader;
import utils.Printer;
import utils.PropertiesReader;
import utils.PropertyUtility;
import utils.StringUtilities;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;

@SuppressWarnings("unused")
public class PickleibWebDriver {

	static {PropertyLoader.load();}

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
	public static Printer log = new Printer(PickleibWebDriver.class);

	/**
	 * Initializes a specified type of driver
	 *
	 * @param browserType driver type
	 */
	public static void initialize(WebDriverFactory.BrowserType browserType){
		log.info("Initializing " + strUtils.markup(StringUtilities.Color.PURPLE, browserType.getDriverName()) + " driver...");
		driver = WebDriverFactory.getDriver(browserType);
		wait = new WebDriverWait(driver, Duration.of(WebDriverFactory.driverTimeout, ChronoUnit.SECONDS));
	}

	/**
	 * Initializes a driver according to the browser property
	 */
	public static void initialize(){
		String driverName = strUtils.firstLetterCapped(reader.getProperty("browser"));
		String driverProperty = strUtils.firstLetterCapped(PropertyUtility.getProperty("browser"));
		if (driverName!=null) initialize(WebDriverFactory.BrowserType.fromString(driverName));
		else if (driverProperty != null) initialize(WebDriverFactory.BrowserType.fromString(driverProperty));
		else initialize(WebDriverFactory.BrowserType.CHROME);
	}

	/**
	 * @deprecated This method is no longer maintained
	 */
	@Deprecated(since = "1.5.6")
	public static void initialize(String id, String password, WebDriverFactory.BrowserType browserType){ //Only works with chrome!
		initialize(browserType);
		DevTools dev = ((ChromeDriver) driver).getDevTools();
		dev.createSession();
		dev.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
		Map<String, Object> map = new HashMap<>();
		map.put("Authorization", "Basic " + (Arrays.toString(Base64.encode((id + ":" + password).getBytes()))));
		dev.send(Network.setExtraHTTPHeaders(new Headers(map)));
	}

	/**
	 * Quits the driver session
	 */
	public static void terminate(){
		log.info("Terminating driver...");
		driver.quit();
	}
}