package pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import pickleib.web.PickleibPageObject;

public class LameFramePage extends PickleibPageObject {

    @FindBy(css = "iframe#lameframe-test-id")
    WebElement iframe;

    @FindBy(id = "toggle-inner-dark")
    WebElement innerDarkModeButton;

    @FindBy(id = "data-inner-dark")
    WebElement innerDarkModeData;

    @FindBy(id = "userInput")
    WebElement userInput;

    @FindBy(css = "[onclick='saveText()']")
    WebElement submitButton;

    @FindBy(id = "savedDisplay")
    WebElement submittedText;

}
