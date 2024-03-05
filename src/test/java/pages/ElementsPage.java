package pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import pickleib.web.PickleibPageObject;

import java.util.List;

public class ElementsPage extends PickleibPageObject {

    @FindBy(css = ".question-box h2")
    public WebElement questionBoxTitle;

    @FindBy(name = ".question-box form p")
    public WebElement checkMarkMessage;

    @FindBy(css = ".radio-group label")
    public List<WebElement> checkMarks;

}
