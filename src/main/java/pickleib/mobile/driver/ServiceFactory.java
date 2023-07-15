package pickleib.mobile.driver;

import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import utils.Printer;
import utils.PropertyUtility;

import static utils.StringUtilities.Color.*;

public class ServiceFactory {
    static Printer log = new Printer(ServiceFactory.class);

    public static AppiumDriverLocalService service;
    static String address;
    static Integer port;

    public static void startService(String address, Integer port){
        log.info("Starting service on " + PURPLE.getValue() + address + ":" + port + RESET.getValue());
        ServiceFactory.address = address;
        ServiceFactory.port = port;
        service = new AppiumServiceBuilder()
                .withIPAddress(address)
                .usingPort(port)
                .build();
        if(!Boolean.parseBoolean(PropertyUtility.getProperty("detailed-logging", "false")))
            ServiceFactory.service.clearOutPutStreams();
        service.start();
    }

}
