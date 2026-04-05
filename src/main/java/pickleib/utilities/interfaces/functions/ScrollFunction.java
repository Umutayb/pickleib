package pickleib.utilities.interfaces.functions;

import org.openqa.selenium.WebElement;

/** Functional interface for scroll operations that bring elements into view. */
@FunctionalInterface
public interface ScrollFunction {
    /** @param element the element to scroll into view
     *  @return the element after scrolling */
    WebElement scroll(WebElement element);
}
