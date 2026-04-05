package pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import pickleib.web.PickleibPageObject;

import java.util.List;

public class DroppablePage extends PickleibPageObject {

    @FindBy(css = "main h1")
    public WebElement pageTitle;

    @FindBy(css = ".status-text")
    public WebElement statusText;

    @FindBy(css = ".drop-item")
    public List<WebElement> sourceItems;

    @FindBy(css = "[data-testid='droppable-zone-red']")
    public WebElement redZone;

    @FindBy(css = "[data-testid='droppable-zone-blue']")
    public WebElement blueZone;

    @FindBy(css = "[data-testid='droppable-zone-green']")
    public WebElement greenZone;

    @FindBy(css = ".drop-zone")
    public List<WebElement> dropZones;

    @FindBy(css = "[data-testid='droppable-reset']")
    public WebElement resetButton;
}
