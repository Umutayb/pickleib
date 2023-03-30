package pickleib.utilities;

import org.openqa.selenium.WebElement;
import utils.StringUtilities;

import static utils.StringUtilities.Color.BLUE;
import static utils.StringUtilities.Color.GRAY;

public class ElementAcquisition extends WebUtilities{

    public StringUtilities strUtils = new StringUtilities();

    public WebElement acquireElementFromPage(String elementName, String pageName, Object objectRepository){
        log.new Info("Acquiring element " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY," from the ") +
                highlighted(BLUE, pageName)
        );
        pageName = strUtils.firstLetterDeCapped(pageName);
        return getElementFromPage(elementName, pageName, objectRepository);
    }

}
