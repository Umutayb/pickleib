package pickleib.utilities;

import org.openqa.selenium.WebElement;

@FunctionalInterface
public interface ClickFunction {
    void click(WebElement element);
}
