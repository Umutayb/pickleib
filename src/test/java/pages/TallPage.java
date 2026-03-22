package pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import pickleib.web.PickleibPageObject;

public class TallPage extends PickleibPageObject {

    @FindBy(css = "h1")
    WebElement title;

    @FindBy(css = "[data-testid='tall-section-10']")
    WebElement logo;
}
