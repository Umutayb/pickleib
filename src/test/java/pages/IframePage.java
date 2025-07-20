package pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import pickleib.web.PickleibPageObject;

import java.util.List;

public class IframePage  extends PickleibPageObject {

    @FindBy(css = "iframe#lameframe-test-id")
    WebElement iframe;

    @FindBy(id = "toggle-inner-dark")
    WebElement innerDarkModeButton;

    @FindBy(id = "userInput")
    WebElement userInput;

    @FindBy(css = "[onclick='saveText()']")
    WebElement submitButton;

    @FindBy(id = "savedDisplay")
    WebElement submittedText;

}
