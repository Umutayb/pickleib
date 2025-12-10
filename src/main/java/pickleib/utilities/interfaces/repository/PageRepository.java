package pickleib.utilities.interfaces.repository;

import collections.Bundle;
import org.openqa.selenium.WebElement;
import pickleib.utilities.element.ElementBundle;

import java.util.List;
import java.util.Map;

/**
 * Interface defining operations for acquiring page-level elements
 * from Page Object Models, excluding all component-related APIs.
 */
public interface PageRepository {

    /**
     * Acquire element {element name} from {page name}.
     *
     * @param elementName target element name
     * @param pageName specified page instance name
     * @return matching WebElement
     */
    WebElement acquireElementFromPage(String elementName, String pageName);

    /**
     * Acquire element {element name} from {page name}.
     *
     * @param elementListName target element name
     * @param pageName specified page instance name
     * @return matching WebElement
     */
    List<WebElement> acquireElementsFromPage(String elementListName, String pageName);

    /**
     * Acquire a listed element {element name} from {list name} list on the {page name}.
     *
     * @param elementName target element name
     * @param listName specified list name
     * @param pageName specified page instance name
     * @return matching WebElement
     */
    WebElement acquireListedElementFromPage(
            String elementName,
            String listName,
            String pageName
    );

    /**
     * Acquire a listed attribute element that has {attribute value}
     * value for its {attribute name} attribute from {list name} list
     * on the {page name}.
     *
     * @param attributeName target attribute name
     * @param attributeValue expected attribute value
     * @param listName target list name
     * @param pageName specified page instance name
     * @return matching WebElement
     */
    WebElement acquireListedElementByAttribute(
            String attributeName,
            String attributeValue,
            String listName,
            String pageName
    );

    /**
     * Acquire form input on the {page name}.
     *
     * @param signForms table containing "Input" → value and "Input Element" → element field
     * @param pageName specified page instance name
     * @return list of element bundles
     */
    List<ElementBundle<String>> acquireElementList(
            List<Map<String, String>> signForms,
            String pageName
    );

    /**
     * Returns an element bundle from a page object, based on provided specifications.
     *
     * @param elementFieldName the name of the element field in the page object
     * @param pageName the name of the page object
     * @param specifications map containing specifications for the element to be retrieved
     * @return bundle containing the element's name, element, and attribute map
     */
    ElementBundle<Map<String, String>> acquireElementBundleFromPage(
            String elementFieldName,
            String pageName,
            Map<String, String> specifications
    );

    /**
     * Returns a list of element bundles from a page object, based on provided specifications.
     *
     * @param pageName the name of the page object
     * @param specifications list of maps containing element specifications
     * @return list of element bundles
     */
    List<ElementBundle<Map<String, String>>> acquireElementBundlesFromPage(
            String pageName,
            List<Map<String, String>> specifications
    );
}
