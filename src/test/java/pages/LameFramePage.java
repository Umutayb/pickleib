package pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import pickleib.web.PickleibPageObject;

public class LameFramePage extends PickleibPageObject {

    @FindBy(css = "iframe#lameframe-test-id")
    WebElement mainIframe;

    @FindBy(css = "iframe#lame-cc-iframe")
    WebElement lameCCIframe;

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

    @FindBy(id = "cardNumber")
    WebElement cardNumber;

    @FindBy(id = "expiry")
    WebElement cardExpiryDate;

    @FindBy(id = "cvc")
    WebElement cardCVC;

    @FindBy(id = "submit")
    WebElement cardSubmitButton;

    @FindBy(id = "submittedMsg")
    WebElement cardSubmitMessage;

}
