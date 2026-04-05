package pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import pickleib.web.PickleibPageObject;

import java.util.List;

public class DropdownPage extends PickleibPageObject {

    @FindBy(css = "main h1")
    WebElement title;

    @FindBy(css = "[data-testid='dropdown-single']")
    WebElement singleSelect;

    @FindBy(css = "[data-testid='dropdown-single'] option:not([value=''])")
    List<WebElement> singleSelectOptions;

    @FindBy(css = "[data-testid='dropdown-single-value']")
    WebElement singleSelectValue;

    @FindBy(css = "[data-testid='dropdown-multi']")
    WebElement multiSelect;

    @FindBy(css = "[data-testid='dropdown-multi'] option")
    List<WebElement> multiSelectOptions;

    @FindBy(css = "[data-testid='dropdown-multi-value']")
    WebElement multiSelectValue;

    @FindBy(css = "[data-testid='dropdown-custom']")
    WebElement customDropdownButton;

    @FindBy(css = "[data-testid='dropdown-custom-list']")
    WebElement customDropdownList;

    @FindBy(css = ".custom-dd-option")
    List<WebElement> customDropdownOptions;
}
