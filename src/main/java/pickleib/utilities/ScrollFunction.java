package pickleib.utilities;

import org.openqa.selenium.WebElement;

@FunctionalInterface
public interface ScrollFunction {
    WebElement scroll(WebElement element);
}
