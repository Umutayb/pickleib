package pickleib.platform.driver;

import context.ContextStore;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import utils.Printer;
import java.time.Duration;

import static utils.StringUtilities.Color.*;

/**
 * A factory class responsible for managing the lifecycle of the local Appium Server service.
 * <p>
 * This class allows the framework to programmatically start and stop the Appium server
 * (using {@link AppiumDriverLocalService}), removing the need for the user to manually
 * start Appium via the command line before running tests.
 * </p>
 */
public class ServiceFactory {

    static Printer log = new Printer(ServiceFactory.class);

    /**
     * The active instance of the local Appium service.
     * Can be used to check the status, stop the service, or retrieve the URL.
     */
    public static AppiumDriverLocalService service;

    /**
     * The IP address where the service is currently running.
     */
    static String address;

    /**
     * The port number where the service is currently running.
     */
    static Integer port;

    /**
     * Builds and starts a local Appium server service on the specified address and port.
     * <p>
     * This method configures the service using {@link AppiumServiceBuilder} with the following behaviors:
     * <ul>
     * <li><b>Timeout:</b> Reads {@code "appium-server-launch-timeout"} from {@link ContextStore}
     * (default: 45 seconds) to define how long to wait for the server to start.</li>
     * <li><b>Logging:</b> Reads {@code "detailed-logging"} from {@link ContextStore}.
     * If {@code false} (default), server output streams are cleared to reduce console clutter.</li>
     * </ul>
     * </p>
     *
     * @param address The IP address to bind the server to (e.g., "0.0.0.0" or "127.0.0.1").
     * @param port    The port number to listen on (e.g., 4723).
     */
    public static void startService(String address, Integer port){
        log.info("Starting service on " + PURPLE.getValue() + address + ":" + port + RESET.getValue());
        ServiceFactory.address = address;
        ServiceFactory.port = port;

        service = new AppiumServiceBuilder()
                .withIPAddress(address)
                .usingPort(port)
                .withTimeout(Duration.ofSeconds(ContextStore.getInt("appium-server-launch-timeout", 45)))
                .build();

        // Suppress Appium server logs in the console unless detailed logging is requested
        if(!Boolean.parseBoolean(ContextStore.get("detailed-logging", "false")))
            ServiceFactory.service.clearOutPutStreams();

        service.start();
    }
}
