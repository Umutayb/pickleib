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
public class PickleibAppiumDriver {

	static {PropertyLoader.load();}

	public static AppiumDriver driver;
	public static WebDriverWait wait;

	private static final PropertiesReader reader = new PropertiesReader("properties-from-pom.properties");
	private static final StringUtilities strUtils = new StringUtilities();
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

		JSONObject json = AppiumDriverFactory.jsonUtils.parseJSONFile(directory+"/"+device+".json");
		driver = AppiumDriverFactory.getDriver(strUtils.firstLetterCapped(device), json);
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