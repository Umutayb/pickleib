package pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import pickleib.web.PickleibPageObject;
import java.util.List;

public class HomePage extends PickleibPageObject {
    @FindBy(css = "categories a")
    List<WebElement> categories;
}