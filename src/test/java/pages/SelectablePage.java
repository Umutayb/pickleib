package pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import pickleib.web.PickleibPageObject;

import java.util.List;

public class SelectablePage extends PickleibPageObject {

    @FindBy(id = "title")
    WebElement title;

    @FindBy(css = "#countriesDropDown")
    WebElement countriesDropDown;

    @FindBy(css = "[role='listbox']")
    WebElement countriesContainer;

    @FindBy(css = "#vs1__listbox .vs__dropdown-option")
    List<WebElement> countriesList;

    @FindBy(css = "#countriesDropDown .vs__selected")
    WebElement selection;
}
