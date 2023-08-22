package pickleib.utilities.screenshot;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.remote.RemoteWebDriver;
import utils.NumericUtilities;
import utils.Printer;
import utils.StringUtilities;

import java.io.File;

import static utils.StringUtilities.Color.*;

@SuppressWarnings("unused")
public class ScreenCaptureUtility {
    Printer log = new Printer(ScreenCaptureUtility.class);
    StringUtilities strUtils = new StringUtilities();
    NumericUtilities numeric = new NumericUtilities();

    /**
     * Captures screen
     * @param name screenshot name
     * @param driver session driver
     * @return returns the screenshot file
     */
    public File captureScreen(String name, String extension, RemoteWebDriver driver) {
        try {
            log.info("Capturing page...");
            if (!extension.contains(".")) extension = "." + extension;
            name += "#"+numeric.randomNumber(1,10000) + extension;
            File sourceFile = new File("screenshots");
            File fileDestination  = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(fileDestination, new File(sourceFile, name));

            log.info("Screenshot saved as; "+name+" at the \"screenshots\" file.");
            return fileDestination;
        }
        catch (Exception gamma){
            log.error(strUtils.highlighted(YELLOW, "Could not capture screen")+strUtils.highlighted(RED, "\n\t"+gamma), gamma);
            return null;
        }
    }
}
