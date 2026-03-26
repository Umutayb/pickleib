package pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import pickleib.web.PickleibPageObject;

import java.util.List;

public class ButtonsPage extends PickleibPageObject {

    @FindBy(css = "main h1")
    WebElement title;

    @FindBy(css = "[data-testid='btn-primary']")
    WebElement primaryButton;

    @FindBy(css = "[data-testid='btn-secondary']")
    WebElement secondaryButton;

    @FindBy(css = "[data-testid='btn-danger']")
    WebElement dangerButton;

    @FindBy(css = "[data-testid='btn-ghost']")
    WebElement ghostButton;

    @FindBy(css = "[data-testid='btn-outline']")
    WebElement outlineButton;

    @FindBy(css = "[data-testid='btn-small']")
    WebElement smallButton;

    @FindBy(css = "[data-testid='btn-medium']")
    WebElement mediumButton;

    @FindBy(css = "[data-testid='btn-large']")
    WebElement largeButton;

    @FindBy(css = "[data-testid='btn-disabled']")
    WebElement disabledButton;

    @FindBy(css = "[data-testid='btn-loading']")
    WebElement loadingButton;

    @FindBy(css = "[data-testid='btn-result']")
    WebElement resultText;

    @FindBy(css = ".section:first-child .btn-row .btn")
    List<WebElement> variantButtons;
}
