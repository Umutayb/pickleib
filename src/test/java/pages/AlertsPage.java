package pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import pickleib.web.PickleibPageObject;

import java.util.List;

public class AlertsPage extends PickleibPageObject {

    @FindBy(css = "main h2")
    WebElement title;

    @FindBy(id = "click-me")
    WebElement clickMeButton;

    @FindBy(id = "right-click-me")
    WebElement rightClickMeButton;

    @FindBy(id = "double-click-me")
    WebElement doubleClickMeButton;

    @FindBy(css = "main .button")
    public List<WebElement> buttons;

    @FindBy(css = "#tabs .button")
    public List<WebElement> windowButtons;
}
