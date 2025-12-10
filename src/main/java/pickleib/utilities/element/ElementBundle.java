package pickleib.utilities.element;

import org.openqa.selenium.WebElement;

public record ElementBundle<Data>(WebElement element, String elementName, String platform, Data data) {
}