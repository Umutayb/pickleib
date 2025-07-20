package pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import pickleib.web.PickleibPageObject;

import java.util.List;

public class AlertAndWindowsPage extends PickleibPageObject {

    @FindBy(css = ".button")
    public List<WebElement> buttons;

    @FindBy(name = "LameFrame")
    WebElement lameFrame;

}
