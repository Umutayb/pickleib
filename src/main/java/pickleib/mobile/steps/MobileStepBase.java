package pickleib.mobile.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.remote.RemoteWebDriver;
import pickleib.mobile.driver.PickleibAppiumDriver;
import pickleib.utilities.ElementAcquisition;
import pickleib.utilities.ScreenCaptureUtility;
import pickleib.web.interactions.WebInteractions;
import utils.Printer;
import utils.StringUtilities;

public class MobileStepBase {

    public RemoteWebDriver driver = PickleibAppiumDriver.driver;

    public ElementAcquisition.PageObjectModel acquire = new ElementAcquisition.PageObjectModel();
    public Printer log = new Printer(this.getClass());
    public StringUtilities strUtils = new StringUtilities();
    public WebInteractions interact = new WebInteractions();
    public ObjectMapper objectMapper = new ObjectMapper();
    public ScreenCaptureUtility capture = new ScreenCaptureUtility();
}
