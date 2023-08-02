import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import pickleib.web.utilities.WebUtilities;

import java.util.List;

public class PageClass extends WebUtilities {

    String trainingUrl = "https://www.toolsqa.com/selenium-training/";
    String baseUrl = "https://demoqa.com/";

    @FindBy(css = ".card")
    List<WebElement> toolCards;
    @FindBy(css = ".col-md-6 div:nth-child(3) button")
    WebElement clickMeButton;
    @FindBy(css = "#dynamicClickMessage")
    WebElement dynamicClickMessage;
    @FindBy(css = ".element-group")
    List<WebElement> accordionLeftPanel;
    @FindBy(css = ".accordion div:nth-child(5)")
    WebElement interactionsAccordionBar;
}