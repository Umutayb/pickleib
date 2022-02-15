package utils.classes;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import utils.Printer;
import utils.Utilities;
public class TextBox extends Utilities {

     Printer PrinterInstance = new Printer(TextBox.class);

     @FindBy(css = "[id='permanentAddress']")
     public WebElement addressButton;

     public void clickAddressButton(){
          PrinterInstance.new Info("Clicking addressButton button");
          clickElement(addressButton);
     }
}