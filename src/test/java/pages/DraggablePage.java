package pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import pickleib.web.PickleibPageObject;

import java.util.List;

public class DraggablePage extends PickleibPageObject {

    @FindBy(css = "main h1")
    public WebElement pageTitle;

    @FindBy(css = ".status-text")
    public WebElement statusText;

    @FindBy(css = ".drag-item")
    public List<WebElement> blocks;

    @FindBy(css = "[data-testid='draggable-item-1']")
    public WebElement blockA;

    @FindBy(css = "[data-testid='draggable-item-2']")
    public WebElement blockB;
}
