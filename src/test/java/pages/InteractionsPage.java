package pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import pickleib.web.PickleibPageObject;

import java.util.List;

public class InteractionsPage extends PickleibPageObject {

    @FindBy(id = "title")
    WebElement title;

    @FindBy(id = "tool")
    List<WebElement> tools;
}
