package pickleib.utilities.interfaces.functions;

import org.openqa.selenium.WebElement;

/** Functional interface for click operations on web elements. */
@FunctionalInterface
public interface ClickFunction {
    /** @param element the element to click */
    void click(WebElement element);
}
