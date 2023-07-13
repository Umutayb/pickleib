package pickleib.utilities;

import org.openqa.selenium.remote.RemoteWebDriver;
import pickleib.mobile.driver.PickleibAppiumDriver;
import pickleib.web.driver.PickleibWebDriver;

import java.util.Arrays;

public class ClassNamer {
    static RemoteWebDriver getDriver(){
        switch (getCallingClassName()) {
            case "pickleib.mobile.utilities.MobileUtilities", "pickleib.mobile.interactions.MobileInteractions" -> {
                return PickleibAppiumDriver.driver;
            }
            case "pickleib.web.utilities.WebUtilities", "pickleib.web.interactions.WebInteractions" -> {
                return PickleibWebDriver.driver;
            }
            case "pickleib.utilities.Utilities" -> {
                return null;
            }
            case "pickleib.utilities.ElementAcquisition$PageObjectModel",
                    "pickleib.utilities.ElementAcquisition$PageObjectJson",
                    "pickleib.utilities.ElementAcquisition$Reflections"-> {
                return ElementAcquisition.driver;
            }
            default -> throw new RuntimeException("Unknown caller: " + getCallingClassName());
        }
    }

    /**
     * Gets the name of the method that called the API.
     *
     * @return the name of the method that called the API
     */
    private static String getCallingClassName(){
        Throwable dummyException = new Throwable();
        StackTraceElement[] stackTrace = dummyException.getStackTrace();
        System.out.println(Arrays.toString(dummyException.getStackTrace()));
        return stackTrace[2].getClassName();
    }
}
