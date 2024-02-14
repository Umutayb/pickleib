package pickleib.mobile.driver;

import context.ContextStore;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.AppiumFluentWait;
import org.json.simple.JSONObject;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import pickleib.utilities.PropertyLoader;
import pickleib.utilities.screenshot.ScreenCaptureUtility;
import properties.PropertiesReader;
import utils.*;
import java.io.IOException;
import java.net.ServerSocket;

@SuppressWarnings("unused")
public abstract class PickleibAppiumDriver {

	public static ScreenCaptureUtility capture = new ScreenCaptureUtility();

	static {PropertyLoader.load();}

	private static AppiumDriver driver;
	private static AppiumFluentWait<RemoteWebDriver> wait;

	public static AppiumDriver get(){
		return driver;
	}
	private static final PropertiesReader reader = new PropertiesReader("properties-from-pom.properties");
	private static final Printer log = new Printer(PickleibAppiumDriver.class);

	public static void startService(){
		new Printer(PickleibAppiumDriver.class).info("Initializing appium service");

		String address = ContextStore.get("address");
		int port = Integer.parseInt(ContextStore.get("port"));

		if (!new SystemUtilities().portIsAvailable(port)){
			try (ServerSocket socket = new ServerSocket(0)) {
				port = socket.getLocalPort();
			}
			catch (IOException e) {throw new RuntimeException(e);}
		}

		ServiceFactory.startService(address, port);	// Start Appium
	}

	public static void initialize() {
		log.info("Initializing appium driver");
		String device = reader.getProperty("device");
		if (device==null) device = ContextStore.get("device");

		String directory = ContextStore.get("config", "src/test/resources/configurations");

		JSONObject json = FileUtilities.Json.parseJSONFile(directory+"/"+device+".json");
		driver = AppiumDriverFactory.getDriver(StringUtilities.firstLetterCapped(device), json);
	}

	public static void terminate(){
		log.info("Finalizing driver...");
		try {driver.quit();}
		catch (Exception exception){exception.printStackTrace();}
		finally {
			if (ServiceFactory.service != null) ServiceFactory.service.stop(); //TODO: Verify socket & log success
		}
	}

	public static void captureAndTerminate(boolean success, String screenshotTag){
		log.info("Finalizing driver...");
		try {
			capture.captureScreen(screenshotTag, "png", driver);
			driver.quit();
		}
		catch (Exception exception){exception.printStackTrace();}
		finally {
			if (ServiceFactory.service != null) ServiceFactory.service.stop(); //TODO: Verify socket & log success
		}
	}
}