package pickleib.utilities.interfaces.functions;

import org.openqa.selenium.WebElement;

@FunctionalInterface
public interface LocateElement {
    WebElement locate();
}
