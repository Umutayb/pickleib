package pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import pickleib.web.PickleibPageObject;

import java.util.List;

public class LongListPage extends PickleibPageObject {

    @FindBy(css = ".list-container")
    WebElement listContainer;

    @FindBy(css = "[data-testid^='long-list-item-']")
    List<WebElement> listItems;
}
