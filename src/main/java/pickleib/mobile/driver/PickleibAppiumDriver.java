package pickleib.mobile.driver;

import context.ContextStore;
import io.appium.java_client.AppiumDriver;
import org.json.simple.JSONObject;
import org.openqa.selenium.support.ui.WebDriverWait;
import pickleib.utilities.PropertyLoader;
import properties.PropertiesReader;
import utils.*;
import java.io.IOException;
import java.net.ServerSocket;

@SuppressWarnings("unused")
public abstract class PickleibAppiumDriver {

	static {PropertyLoader.load();}

	private static AppiumDriver driver;
	private static WebDriverWait wait;

	public static AppiumDriver get(){
		return driver;
	}

	public static WebDriverWait getWait(){
		return wait;
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
		try {
			driver.quit();
			if (ServiceFactory.service != null) ServiceFactory.service.stop(); //TODO: Verify socket & log success
		}
		catch (Exception ignored){}
	}
}