package pickleib.web.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.remote.RemoteWebDriver;
import pickleib.utilities.ElementAcquisition;
import pickleib.utilities.screenshot.ScreenCaptureUtility;
import pickleib.web.driver.PickleibWebDriver;
import pickleib.web.interactions.WebInteractions;
import utils.Printer;
import utils.StringUtilities;

public class WebStepBase {

    public RemoteWebDriver driver = PickleibWebDriver.driver;

    public ElementAcquisition.PageObjectModel acquire = new ElementAcquisition.PageObjectModel();
    public Printer log = new Printer(this.getClass());
    public StringUtilities strUtils = new StringUtilities();
    public WebInteractions interact = new WebInteractions();
    public ObjectMapper objectMapper = new ObjectMapper();
    public ScreenCaptureUtility capture = new ScreenCaptureUtility();
}
