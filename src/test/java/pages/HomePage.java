package pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import pickleib.web.PickleibPageObject;
import java.util.List;

public class HomePage extends PickleibPageObject {

    @FindBy(css = ".category-grid .home-card")
    public List<WebElement> categories;

    @FindBy(css = ".home-page h1")
    public WebElement pageTitle;
}
