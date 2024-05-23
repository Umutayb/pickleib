package pickleib.utilities.screenshot;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.remote.RemoteWebDriver;
import utils.NumericUtilities;
import utils.Printer;
import java.io.File;

@SuppressWarnings("unused")
public class ScreenCaptureUtility {
    static Printer log = new Printer(ScreenCaptureUtility.class);

    /**
     * Captures screen
     * @param name screenshot name
     * @param driver session driver
     * @return returns the screenshot file
     */
    public static File captureScreen(String name, String extension, RemoteWebDriver driver) {
        try {
            log.info("Capturing page...");
            if (!extension.contains(".")) extension = "." + extension;
            name += "#"+ NumericUtilities.randomNumber(1,10000) + extension;
            File sourceFile = new File("screenshots");
            File fileDestination  = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(fileDestination, new File(sourceFile, name));

            log.info("Screenshot saved as; "+name+" at the \"screenshots\" file.");
            return fileDestination;
        }
        catch (Exception exception){
            log.error("Could not capture screen", exception);
            exception.printStackTrace();
            return null;
        }
    }

    /**
     * Captures screen
     * @param name screenshot name
     * @param driver session driver
     * @return returns the screenshot file
     */
    public static File silentCaptureScreen(String name, String extension, RemoteWebDriver driver) {
        try {
            if (!extension.contains(".")) extension = "." + extension;
            name += "#"+ NumericUtilities.randomNumber(1,10000) + extension;
            File sourceFile = new File("screenshots");
            File fileDestination  = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(fileDestination, new File(sourceFile, name));

            return fileDestination;
        }
        catch (Exception exception){
            log.error("Could not capture screen", exception);
            exception.printStackTrace();
            return null;
        }
    }
}
