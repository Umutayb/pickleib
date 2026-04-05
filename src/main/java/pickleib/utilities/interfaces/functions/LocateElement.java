package pickleib.utilities.interfaces.functions;

import org.openqa.selenium.WebElement;

/** Functional interface for locating a {@link WebElement}. */
@FunctionalInterface
public interface LocateElement {
    /**
     * Locates and returns a web element.
     *
     * @return the located {@link WebElement}
     */
    WebElement locate();
}
