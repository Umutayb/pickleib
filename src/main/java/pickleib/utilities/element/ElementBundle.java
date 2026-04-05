package pickleib.utilities.element;

import org.openqa.selenium.WebElement;

/**
 * Groups a {@link WebElement} with its metadata and associated data payload.
 *
 * @param element     the web element
 * @param elementName the element's name in the repository
 * @param platform    the target platform
 * @param data        additional data associated with the element
 * @param <Data>      the type of the data payload
 */
public record ElementBundle<Data>(WebElement element, String elementName, String platform, Data data) {
}