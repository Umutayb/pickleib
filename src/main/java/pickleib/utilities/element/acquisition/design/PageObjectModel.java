package pickleib.utilities.element.acquisition.design;

import collections.Bundle;
import org.openqa.selenium.WebElement;
import pickleib.utilities.element.ElementBundle;
import pickleib.utilities.element.acquisition.ElementAcquisition;
import pickleib.utilities.interfaces.repository.PageObjectRepository;
import pickleib.utilities.interfaces.repository.PageRepository;
import utils.Printer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static pickleib.utilities.element.acquisition.ElementAcquisition.*;
import static pickleib.utilities.platform.PlatformUtilities.getElementDriverType;
import static utils.StringUtilities.Color.BLUE;
import static utils.StringUtilities.Color.GRAY;
import static utils.StringUtilities.contextCheck;
import static utils.StringUtilities.firstLetterDeCapped;
import static utils.StringUtilities.highlighted;

public class PageObjectModel <ObjectRepository extends PageObjectRepository> implements PageRepository {

    Reflections<ObjectRepository> reflections;
    Printer log = new Printer(ElementAcquisition.class);

    public PageObjectModel(Class<ObjectRepository> pageRepository) {
        reflections = new ElementAcquisition.Reflections<>(pageRepository);
    }

    /**
     *
     * Acquire element {element name} from {page name}
     *
     * @param elementName target button name
     * @param pageName specified page instance name
     */
    public WebElement acquireElementFromPage(String elementName, String pageName){
        log.info("Acquiring element " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY," from the ") +
                highlighted(BLUE, pageName)
        );
        pageName = firstLetterDeCapped(pageName);
        elementName = contextCheck(elementName);
        return reflections.getElementFromPage(elementName, pageName);
    }

    /**
     *
     * Acquire element {element name} from {page name}
     *
     * @param elementName target button name
     * @param pageName specified page instance name
     */
    public List<WebElement> acquireElementsFromPage(String elementName, String pageName){
        log.info("Acquiring element " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY," from the ") +
                highlighted(BLUE, pageName)
        );
        pageName = firstLetterDeCapped(pageName);
        elementName = contextCheck(elementName);
        return reflections.getElementsFromPage(elementName, pageName);
    }

    /**
     *
     * Acquire component element {element name} of {component field name} component on the {page name}
     *
     * @param elementName target button name
     * @param componentFieldName specified component field name
     * @param pageName specified page instance name
     */
    @Deprecated(since = "2.0.3")
    public WebElement acquireElementFromComponent(String elementName, String componentFieldName, String pageName) {
        log.info("Acquiring element " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY," from the ") +
                highlighted(BLUE, pageName)
        );
        pageName = firstLetterDeCapped(pageName);
        componentFieldName = firstLetterDeCapped(componentFieldName);
        elementName = contextCheck(elementName);
        return reflections.getElementFromComponent(elementName, componentFieldName, pageName);
    }

    /**
     *
     * Acquire a listed element {element name} from {list name} list on the {page name}
     *
     * @param elementName target button name
     * @param listName specified component list name
     * @param pageName specified page instance name
     */
    public WebElement acquireListedElementFromPage(
            String elementName,
            String listName,
            String pageName
    ) {
        log.info("Acquiring listed element named " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY," selected from ") +
                highlighted(BLUE, listName) +
                highlighted(GRAY," on  ") +
                highlighted(BLUE, pageName)
        );

        pageName = firstLetterDeCapped(pageName);
        listName = firstLetterDeCapped(listName);
        elementName = contextCheck(elementName);
        List<WebElement> elements = reflections.getElementsFromPage(
                listName,
                pageName
        );
        return acquireNamedElementAmongst(elements, elementName);
    }

    /**
     *
     * Acquire a listed component element {element name} of {component field name} from {component list name} list on the {page name}
     *
     * @param elementName target button name
     * @param componentFieldName specified component field name
     * @param listFieldName specified component list name
     * @param pageName specified page instance name
     */
    @Deprecated(since = "2.0.3")
    public WebElement acquireListedElementFromComponent(
            String elementName,
            String componentFieldName,
            String listFieldName,
            String pageName
    ) {
        log.info("Acquiring listed element named " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY," selected from ") +
                highlighted(BLUE, listFieldName) +
                highlighted(GRAY," of ") +
                highlighted(BLUE, componentFieldName) +
                highlighted(GRAY," component on the ") +
                highlighted(BLUE, pageName)
        );

        componentFieldName = firstLetterDeCapped(componentFieldName);
        pageName = firstLetterDeCapped(pageName);
        listFieldName = firstLetterDeCapped(listFieldName);
        elementName = contextCheck(elementName);
        List<WebElement> elements = reflections.getElementsFromComponent(
                listFieldName,
                componentFieldName,
                pageName
        );
        return acquireNamedElementAmongst(elements, elementName);
    }

    /**
     *
     * Select component named {component name} from {component list name} component list on the {page name} and acquire the {element name} element
     *
     * @param componentName specified component name
     * @param componentListName specified component list name
     * @param pageName specified page instance name
     * @param elementName target button name
     */
    @Deprecated(since = "2.0.3")
    public <Component extends WebElement> WebElement acquireListedComponentElement(
            String elementName,
            String componentName,
            String componentListName,
            String pageName
    ) {
        log.info("Acquiring listed element named " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY," selected from ") +
                highlighted(BLUE, componentListName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, componentName) +
                highlighted(GRAY," component on the ") +
                highlighted(BLUE, pageName)
        );
        pageName = firstLetterDeCapped(pageName);
        componentListName = firstLetterDeCapped(componentListName);
        elementName = contextCheck(elementName);
        List<Component> componentList = reflections.getComponentsFromPage(componentListName, pageName);
        Component component = acquireNamedComponentAmongst(componentList, componentName);
        return reflections.getElementFromComponent(elementName, component);
    }

    /**
     *
     * Select exact component named {component name} from {component list name} component list on the {page name} and acquire the {element name} element
     *
     * @param elementFieldName specified element field name
     * @param elementText specified element text
     * @param componentListName specified component list name
     * @param pageName specified page instance name
     */
    @Deprecated(since = "2.0.3")
    public WebElement acquireExactNamedListedComponentElement(
            String elementFieldName,
            String elementText,
            String componentListName,
            String pageName
    ) {
        log.info("Acquiring exact listed element named " +
                highlighted(BLUE, elementFieldName) +
                highlighted(GRAY," selected from ") +
                highlighted(BLUE, componentListName) +
                highlighted(GRAY," component list on the ") +
                highlighted(BLUE, pageName)
        );
        Object component = acquireExactNamedListedComponent(elementFieldName, elementText, componentListName, pageName);
        return reflections.getElementFromComponent(elementFieldName, component);
    }

    /**
     *
     * Acquire component {component name} from {component list name} component list on the {page name} and by selecting it using child element name
     *
     * @param elementText specified element text
     * @param componentListName specified component list name
     * @param pageName specified page instance name
     * @param elementFieldName target element name
     */
    @Deprecated(since = "2.0.3")
    public <Component extends WebElement> Component acquireExactNamedListedComponent(
            String elementFieldName,
            String elementText,
            String componentListName,
            String pageName
    ) {
        log.info("Acquiring exact listed component by element named " +
                highlighted(BLUE, elementFieldName) +
                highlighted(GRAY," selected from ") +
                highlighted(BLUE, componentListName) +
                highlighted(GRAY," component list on the ") +
                highlighted(BLUE, pageName)
        );
        pageName = firstLetterDeCapped(pageName);
        componentListName = firstLetterDeCapped(componentListName);
        elementFieldName = contextCheck(elementFieldName);
        List<Component> components = reflections.getComponentsFromPage(componentListName, pageName);
        return reflections.acquireExactNamedComponentAmongst(components, elementText, elementFieldName);
    }

    /**
     *
     * Select component named {component name} from {component list name} component list on the {page name} and acquire listed element {element name} of {element list name}
     *
     * @param componentName specified component name
     * @param componentListName specified component list name
     * @param pageName specified page instance name
     * @param elementName target button name
     * @param elementListName target element list name
     */
    @Deprecated(since = "2.0.3")
    public <Component extends WebElement> WebElement acquireListedElementAmongstListedComponents(
            String elementName,
            String elementListName,
            String componentName,
            String componentListName,
            String pageName
    ) {
        log.info("Acquiring listed element named " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY," selected from ") +
                highlighted(BLUE, componentListName) +
                highlighted(GRAY," component list on the ") +
                highlighted(BLUE, pageName)
        );
        elementName = contextCheck(elementName);
        componentName = contextCheck(componentName);
        pageName = firstLetterDeCapped(pageName);
        componentListName = firstLetterDeCapped(componentListName);
        List<Component> components = reflections.getComponentsFromPage(componentListName, pageName);
        Component component = acquireNamedComponentAmongst(components, componentName);
        List<WebElement> elements = reflections.getElementsFromComponent(elementListName, component);
        return acquireNamedElementAmongst(elements, elementName);
    }

    /**
     *
     * Acquire a listed attribute element that has {attribute value} value for its {attribute name} attribute from {list name} list on the {page name}
     *
     * @param attributeName target attribute name
     * @param attributeValue expected attribute value
     * @param listName target list name
     * @param pageName specified page instance name
     */
    public WebElement acquireListedElementByAttribute(
            String attributeName,
            String attributeValue,
            String listName,
            String pageName
    ) {
        log.info("Acquiring element by " +
                highlighted(BLUE, attributeName) +
                highlighted(GRAY," attribute selected from ") +
                highlighted(BLUE, listName) +
                highlighted(GRAY, " list on the ") +
                highlighted(BLUE, pageName)
        );
        attributeName = contextCheck(attributeName);
        pageName = firstLetterDeCapped(pageName);
        List<WebElement> elements = reflections.getElementsFromPage(
                listName,
                firstLetterDeCapped(pageName)
        );
        return acquireElementUsingAttributeAmongst(elements, attributeName, attributeValue);
    }

    /**
     *
     * Acquire listed attribute element of {component name} component that has {attribute value} value for its {attribute name} attribute from {list name} list on the {page name}
     *
     * @param componentName specified component name
     * @param attributeValue expected attribute value
     * @param attributeName target attribute name
     * @param listName target list name
     * @param pageName specified page instance name
     */
    @Deprecated(since = "2.0.3")
    public WebElement acquireListedComponentElementByAttribute(
            String componentName,
            String attributeValue,
            String attributeName,
            String listName,
            String pageName
    ) {
        log.info("Acquiring element by " +
                highlighted(BLUE, attributeName) +
                highlighted(GRAY," attribute selected from ") +
                highlighted(BLUE, listName) +
                highlighted(GRAY, " list on the ") +
                highlighted(BLUE, pageName)
        );
        attributeName = contextCheck(attributeName);
        pageName = firstLetterDeCapped(pageName);
        componentName = firstLetterDeCapped(componentName);
        List<WebElement> elements = reflections.getElementsFromComponent(
                listName,
                componentName,
                pageName
        );
        return acquireElementUsingAttributeAmongst(elements, attributeName, attributeValue);
    }

    /**
     * Acquire form input on the {page name}
     *
     * @param pageName         specified page instance name
     * @param signForms        table that has key as "Input" and value as "Input Element" (dataTable.asMaps())
     */
    public List<ElementBundle<String>> acquireElementList(List<Map<String, String>> signForms, String pageName) {
        log.info("Acquiring element list from " + highlighted(BLUE, pageName));
        pageName = firstLetterDeCapped(pageName);
        List<ElementBundle<String>> bundles = new ArrayList<>();
        for (Map<String, String> form : signForms) {
            String inputName = form.get("Input Element");
            String input = contextCheck(form.get("Input"));
            WebElement element = reflections.getElementFromPage(inputName, pageName);
            ElementBundle<String> bundle = new ElementBundle<>(
                    element,
                    inputName,
                    getElementDriverType(element).name(),
                    input
            );
            bundles.add(bundle);
        }
        return bundles;
    }

    /**
     * Acquire component form input on the {page name}
     *
     * @param pageName         specified page instance name
     * @param signForms        table that has key as "Input" and value as "Input Element" (dataTable.asMaps())
     */
    @Deprecated(since = "2.0.3")
    public List<Bundle<WebElement, String, String>> acquireComponentElementList(List<Map<String, String>> signForms, String componentName, String pageName) {
        log.info("Acquiring element list from " + highlighted(BLUE, pageName));
        pageName = firstLetterDeCapped(pageName);
        List<Bundle<WebElement, String, String>> bundles = new ArrayList<>();
        for (Map<String, String> form : signForms) {
            String inputName = form.get("Input Element");
            String input = contextCheck(form.get("Input"));
            componentName = firstLetterDeCapped(componentName);
            Bundle<WebElement, String, String> bundle = new Bundle<>(
                    reflections.getElementFromComponent(inputName, componentName, pageName),
                    input,
                    inputName
            );
            bundles.add(bundle);
        }
        return bundles;
    }

    /**
     * Returns an element bundle from a page object, based on provided specifications.
     *
     * @param elementFieldName The name of the element fields in the page object.
     * @param pageName The name of the page object.
     * @param specifications A map containing the specifications for the element to be retrieved from the page object, including the element name.
     * @return An element bundle containing the element name, the matching element, and a map of the element's attributes.
     */
    public ElementBundle<Map<String, String>> acquireElementBundleFromPage(
            String elementFieldName,
            String pageName,
            Map<String, String> specifications
    ){
        log.info("Acquiring element bundle from " + highlighted(BLUE, pageName));
        WebElement element = acquireElementFromPage(elementFieldName, pageName);
        return new ElementBundle<>(element, elementFieldName, getElementDriverType(element).name(), specifications);
    }

    /**
     * Returns a list of element bundles from a page object, based on provided specifications.
     *
     * @param pageName The name of the page object.
     * @param specifications A list of maps containing the specifications for each element to be retrieved from the page object, including the element name.
     * @return A list of element bundles containing the element name, the matching element, and a map of the element's attributes.
     */
    public List<ElementBundle<Map<String, String>>> acquireElementBundlesFromPage(
            String pageName,
            List<Map<String, String>> specifications
    ){
        log.info("Acquiring element bundle from " + highlighted(BLUE, pageName));
        List<ElementBundle<Map<String, String>>> bundles = new ArrayList<>();
        for (Map<String, String> specification:specifications) {
            bundles.add(acquireElementBundleFromPage(specification.get("Element Name"), pageName, specification));
        }
        return bundles;
    }

    /**
     * Returns an element bundle from a specified component in a page object, based on provided specifications.
     *
     * @param componentFieldName The name of the component field in the page object.
     * @param pageName The name of the page object.
     * @param specifications A map containing the specifications for the element to be retrieved from the component, including the element name.
     * @return An element bundle containing the element name, the matching element, and a map of the element's attributes.
     */
    @Deprecated(since = "2.0.3")
    public Bundle<String, WebElement, Map<String, String>> acquireElementBundleFromComponent(
            String componentFieldName,
            String pageName,
            Map<String, String> specifications
    ){
        log.info("Acquiring element bundle from " + highlighted(BLUE, pageName));
        return new Bundle<>(specifications.get("Element Name"), acquireElementFromComponent(
                specifications.get("Element Name"),
                componentFieldName,
                pageName
        ), specifications);
    }

    /**
     * Returns a list of element bundles from a specified component in a page object, based on provided specifications.
     *
     * @param componentFieldName The name of the component field in the page object.
     * @param pageName The name of the page object.
     * @param specifications A list of maps containing the specifications for each element to be retrieved from the component, including the element name.
     * @return A list of element bundles containing the element name, the matching element, and a map of the element's attributes.
     */
    @Deprecated(since = "2.0.3")
    public List<Bundle<String, WebElement, Map<String, String>>> acquireElementBundlesFromComponent(
            String componentFieldName,
            String pageName,
            List<Map<String, String>> specifications
    ){
        log.info("Acquiring element bundle from " + highlighted(BLUE, pageName));
        List<Bundle<String, WebElement, Map<String, String>>> bundles = new ArrayList<>();
        for (Map<String, String> specification:specifications) {
            bundles.add(
                    new Bundle<>(specification.get("Element Name"),
                            acquireElementFromComponent(
                                    specification.get("Element Name"),
                                    componentFieldName,
                                    pageName
                            ),
                            specification
                    )
            );
        }
        return bundles;
    }

    /**
     * Returns an element bundle from a page object's component list, where the second child element's text matches the provided specifications.
     *
     * @param specifications A map containing the specifications for the second child element's text, including the selector text, selector element name, and target element name.
     * @param componentListName The name of the component list in the page object.
     * @param pageName The name of the page object.
     * @return An element bundle containing the target element name, the matching element, and a map of the element's attributes.
     */
    @Deprecated(since = "2.0.3")
    public <Component extends WebElement> Bundle<String, WebElement, Map<String, String>> selectChildElementFromComponentsBySecondChildText(
            Map<String, String> specifications,
            String componentListName,
            String pageName
    ){
        log.info("Acquiring element bundle from " + highlighted(BLUE, pageName));
        String selectorElementText = contextCheck(specifications.get("Selector Text"));
        String selectorElementName = contextCheck(specifications.get("Selector Element"));
        String targetElementName = contextCheck(specifications.get("Target Element"));
        pageName = firstLetterDeCapped(pageName);
        List<Component> components = reflections.getComponentsFromPage(componentListName, pageName);
        Component component = reflections.acquireExactNamedComponentAmongst(components, selectorElementText, selectorElementName);
        return new Bundle<>(targetElementName, reflections.getElementFromComponent(targetElementName, component), specifications);
    }

    /**
     * Returns a list of element bundles from a page object's component list, where the second child element's text matches the provided specifications.
     *
     * @param specifications A list of maps containing the specifications for the second child element's text.
     * @param componentListName The name of the component list in the page object.
     * @param pageName The name of the page object.
     * @return A list of element bundles containing the component name, the matching element, and a map of the element's attributes.
     */
    @Deprecated(since = "2.0.3")
    public List<Bundle<String, WebElement, Map<String, String>>> selectChildElementsFromComponentsBySecondChildText(
            List<Map<String, String>> specifications,
            String componentListName,
            String pageName
    ){
        log.info("Acquiring element bundles from " + highlighted(BLUE, pageName));
        List<Bundle<String, WebElement, Map<String, String>>> pairs = new ArrayList<>();
        for (Map<String, String> map:specifications) {
            pairs.add(selectChildElementFromComponentsBySecondChildText(map, componentListName, pageName));
        }
        return pairs;
    }

    /**
     * Returns boolean value about containing element with text in the component list.
     *
     * @param elementName The name of element.
     * @param elementText The text of element.
     * @param componentListName The name of the component list in the page object.
     * @param pageName The name of the page object.
     * @return Boolean value.
     */
    @Deprecated(since = "2.0.3")
    public <Component extends WebElement> boolean listedComponentContainsElementText(String elementName, String elementText, String componentListName, String pageName) {
        pageName = firstLetterDeCapped(pageName);
        componentListName = firstLetterDeCapped(componentListName);
        elementText = contextCheck(elementText);
        List<Component> componentList = reflections.getComponentsFromPage(componentListName, pageName);
        boolean textMatch = false;
        for (Component component : componentList) {
            WebElement element = reflections.getElementFromComponent(elementName, component);
            textMatch = element.getText().equals(elementText);
            if(textMatch) break;
        }
        return textMatch;
    }
}
