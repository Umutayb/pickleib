package pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import pickleib.web.PickleibPageObject;

import java.util.List;

public class TallPage extends PickleibPageObject {

    @FindBy(css = ".tall-page h1")
    WebElement title;

    @FindBy(css = ".tall-section")
    List<WebElement> sections;

    @FindBy(css = ".tall-section h2")
    List<WebElement> sectionHeadings;

    @FindBy(css = "[data-testid='tall-scroll-position']")
    WebElement scrollPosition;

    @FindBy(css = "[data-testid='tall-section-10']")
    WebElement lastSection;
}
