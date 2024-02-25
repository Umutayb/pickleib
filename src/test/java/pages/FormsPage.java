package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import pickleib.web.PickleibPageObject;
import utils.arrays.lambda.Collectors;

import java.util.List;

public class FormsPage extends PickleibPageObject {

    @FindBy(id = "title")
    WebElement title;

    @FindBy(id = "name")
    WebElement nameInput;

    @FindBy(id = "email")
    WebElement emailInput;

    @FindBy(id = "gender")
    WebElement genderDropdown;

    @FindBy(css = "#gender option:not([disabled])")
    List<WebElement> genderOptions;

    @FindBy(id = "mobile")
    WebElement mobileInput;

    @FindBy(id = "dob")
    WebElement dateOfBirthInput;

    @FindBy(id = "hobbies")
    WebElement hobbiesInput;

    @FindBy(id = "currentAddress")
    WebElement addressInput;

    @FindBy(id = "city")
    WebElement cityInput;

    @FindBy(id = "submit")
    WebElement submitButton;

    @FindBy(css = ".submitted-info-table tr")
    List<WebElement> submissionEntries;

    @FindBy(className = "submitted-info-table")
    WebElement table;

    @FindBy(css = "[aria-label='Open years overlay']")
    WebElement datePickerYearsButton;

    @FindBy(css = "[aria-label='Open months overlay']")
    WebElement datePickerMonthsButton;

    @FindBy(css = ".dp__action_row .dp__action_select")
    WebElement datePickerSubmitButton;

    @FindBy(css = "[role='gridcell']")
    List<WebElement> datePickerCells;

    public static WebElement getEntryValue(String entryKey, List<WebElement> submissionEntries) {
        return submissionEntries.stream().filter(
                entry -> entry.findElement(By.className("table-key")).getText().equalsIgnoreCase(entryKey)
        ).collect(Collectors.toSingleton()).findElement(By.className("table-value"));
    }
}
