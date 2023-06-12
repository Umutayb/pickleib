package pickleib.element;

import com.github.webdriverextensions.WebComponent;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ByAll;
import pickleib.enums.PrimarySelectorType;
import pickleib.enums.SelectorType;
import pickleib.exceptions.PickleibException;
import pickleib.utilities.WebUtilities;
import records.Bundle;
import records.Pair;
import utils.ReflectionUtilities;

import java.util.*;

import static utils.StringUtilities.Color.*;

@SuppressWarnings("unused")
public class ElementAcquisition {

    public static class PageObjectModel extends WebUtilities {
        Reflections reflections = new Reflections();

        /**
         *
         * Acquire element {element name} from {page name}
         *
         * @param elementName target button name
         * @param pageName specified page instance name
         * @param objectRepository instance that includes specified page instance
         */
        public WebElement acquireElementFromPage(String elementName, String pageName, Object objectRepository){
            log.info("Acquiring element " +
                    highlighted(BLUE, elementName) +
                    highlighted(GRAY," from the ") +
                    highlighted(BLUE, pageName)
            );
            pageName = strUtils.firstLetterDeCapped(pageName);
            elementName = strUtils.contextCheck(elementName);
            return reflections.getElementFromPage(elementName, pageName, objectRepository);
        }

        /**
         *
         * Acquire component element {element name} of {component field name} component on the {page name}
         *
         * @param elementName target button name
         * @param componentFieldName specified component field name
         * @param pageName specified page instance name
         * @param objectRepository instance that includes specified page instance
         */
        public WebElement acquireElementFromComponent(String elementName, String componentFieldName, String pageName, Object objectRepository) {
            log.info("Acquiring element " +
                    highlighted(BLUE, elementName) +
                    highlighted(GRAY," from the ") +
                    highlighted(BLUE, pageName)
            );
            pageName = strUtils.firstLetterDeCapped(pageName);
            componentFieldName = strUtils.firstLetterDeCapped(componentFieldName);
            elementName = strUtils.contextCheck(elementName);
            return reflections.getElementFromComponent(elementName, componentFieldName, pageName, objectRepository);
        }

        /**
         *
         * Acquire listed element {element name} from {list name} list on the {page name}
         *
         * @param elementName target button name
         * @param listName specified component list name
         * @param pageName specified page instance name
         * @param objectRepository instance that includes specified page instance
         */
        public WebElement acquireListedElementFromPage(
                String elementName,
                String listName,
                String pageName,
                Object objectRepository
        ) {
            log.info("Acquiring listed element named " +
                    highlighted(BLUE, elementName) +
                    highlighted(GRAY," selected from ") +
                    highlighted(BLUE, listName) +
                    highlighted(GRAY," on  ") +
                    highlighted(BLUE, pageName)
            );

            pageName = strUtils.firstLetterDeCapped(pageName);
            listName = strUtils.firstLetterDeCapped(listName);
            elementName = strUtils.contextCheck(elementName);
            List<WebElement> elements = reflections.getElementsFromPage(
                    listName,
                    pageName,
                    objectRepository
            );
            return acquireNamedElementAmongst(elements, elementName);
        }

        /**
         *
         * Acquire listed component element {element name} of {component field name} from {component list name} list on the {page name}
         *
         * @param elementName target button name
         * @param componentFieldName specified component field name
         * @param listFieldName specified component list name
         * @param pageName specified page instance name
         * @param objectRepository instance that includes specified page instance
         */
        public WebElement acquireListedElementFromComponent(
                String elementName,
                String componentFieldName,
                String listFieldName,
                String pageName,
                Object objectRepository
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

            componentFieldName = strUtils.firstLetterDeCapped(componentFieldName);
            pageName = strUtils.firstLetterDeCapped(pageName);
            listFieldName = strUtils.firstLetterDeCapped(listFieldName);
            elementName = strUtils.contextCheck(elementName);
            List<WebElement> elements = reflections.getElementsFromComponent(
                    listFieldName,
                    componentFieldName,
                    pageName,
                    objectRepository
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
         * @param objectRepository instance that includes specified page instance
         */
        public WebElement acquireListedComponentElement(
                String elementName,
                String componentName,
                String componentListName,
                String pageName,
                Object objectRepository
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
            pageName = strUtils.firstLetterDeCapped(pageName);
            componentListName = strUtils.firstLetterDeCapped(componentListName);
            elementName = strUtils.contextCheck(elementName);
            List<WebComponent> componentList = reflections.getComponentsFromPage(componentListName, pageName, objectRepository);
            WebComponent component = acquireNamedComponentAmongst(componentList, componentName);
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
         * @param objectRepository instance that includes specified page instance
         */
        public WebElement acquireExactNamedListedComponentElement(
                String elementFieldName,
                String elementText,
                String componentListName,
                String pageName,
                Object objectRepository
        ) {
            log.info("Acquiring exact listed element named " +
                    highlighted(BLUE, elementFieldName) +
                    highlighted(GRAY," selected from ") +
                    highlighted(BLUE, componentListName) +
                    highlighted(GRAY," component list on the ") +
                    highlighted(BLUE, pageName)
            );
            WebComponent component = acquireExactNamedListedComponent(elementFieldName, elementText, componentListName, pageName, objectRepository);
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
         * @param objectRepository instance that includes specified page instance
         */
        public WebComponent acquireExactNamedListedComponent(
                String elementFieldName,
                String elementText,
                String componentListName,
                String pageName,
                Object objectRepository
        ) {
            log.info("Acquiring exact listed component by element named " +
                    highlighted(BLUE, elementFieldName) +
                    highlighted(GRAY," selected from ") +
                    highlighted(BLUE, componentListName) +
                    highlighted(GRAY," component list on the ") +
                    highlighted(BLUE, pageName)
            );
            pageName = strUtils.firstLetterDeCapped(pageName);
            componentListName = strUtils.firstLetterDeCapped(componentListName);
            elementFieldName = strUtils.contextCheck(elementFieldName);
            List<WebComponent> components = reflections.getComponentsFromPage(componentListName, pageName, objectRepository);
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
         * @param objectRepository instance that includes specified page instance
         */
        public WebElement acquireListedElementAmongstListedComponents(
                String elementName,
                String elementListName,
                String componentName,
                String componentListName,
                String pageName,
                Object objectRepository
        ) {
            log.info("Acquiring listed element named " +
                    highlighted(BLUE, elementName) +
                    highlighted(GRAY," selected from ") +
                    highlighted(BLUE, componentListName) +
                    highlighted(GRAY," component list on the ") +
                    highlighted(BLUE, pageName)
            );
            elementName = strUtils.contextCheck(elementName);
            componentName = strUtils.contextCheck(componentName);
            pageName = strUtils.firstLetterDeCapped(pageName);
            componentListName = strUtils.firstLetterDeCapped(componentListName);
            List<WebComponent> components = reflections.getComponentsFromPage(componentListName, pageName, objectRepository);
            WebComponent component = acquireNamedComponentAmongst(components, componentName);
            List<WebElement> elements = reflections.getElementsFromComponent(elementListName, component);
            return acquireNamedElementAmongst(elements, elementName);
        }

        /**
         *
         * Acquire listed attribute element that has {attribute value} value for its {attribute name} attribute from {list name} list on the {page name}
         *
         * @param attributeName target attribute name
         * @param attributeValue expected attribute value
         * @param listName target list name
         * @param pageName specified page instance name
         * @param objectRepository instance that includes specified page instance
         */
        public WebElement acquireListedElementByAttribute(
                String attributeName,
                String attributeValue,
                String listName,
                String pageName,
                Object objectRepository
        ) {
            log.info("Acquiring element by " +
                    highlighted(BLUE, attributeName) +
                    highlighted(GRAY," attribute selected from ") +
                    highlighted(BLUE, listName) +
                    highlighted(GRAY, " list on the ") +
                    highlighted(BLUE, pageName)
            );
            attributeName = strUtils.contextCheck(attributeName);
            pageName = strUtils.firstLetterDeCapped(pageName);
            List<WebElement> elements = reflections.getElementsFromPage(
                    listName,
                    strUtils.firstLetterDeCapped(pageName),
                    objectRepository
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
         * @param objectRepository instance that includes specified page instance
         */
        public WebElement acquireListedComponentElementByAttribute(
                String componentName,
                String attributeValue,
                String attributeName,
                String listName,
                String pageName,
                Object objectRepository
        ) {
            log.info("Acquiring element by " +
                    highlighted(BLUE, attributeName) +
                    highlighted(GRAY," attribute selected from ") +
                    highlighted(BLUE, listName) +
                    highlighted(GRAY, " list on the ") +
                    highlighted(BLUE, pageName)
            );
            attributeName = strUtils.contextCheck(attributeName);
            pageName = strUtils.firstLetterDeCapped(pageName);
            componentName = strUtils.firstLetterDeCapped(componentName);
            List<WebElement> elements = reflections.getElementsFromComponent(
                    listName,
                    componentName,
                    pageName,
                    objectRepository
            );
            return acquireElementUsingAttributeAmongst(elements, attributeName, attributeValue);
        }

        /**
         * Acquire form input on the {page name}
         *
         * @param pageName         specified page instance name
         * @param signForms        table that has key as "Input" and value as "Input Element" (dataTable.asMaps())
         * @param objectRepository instance that includes specified page instance
         */
        public List<Bundle<WebElement, String, String>> acquireElementList(List<Map<String, String>> signForms, String pageName, Object objectRepository) {
            log.info("Acquiring element list from " + highlighted(BLUE, pageName));
            pageName = strUtils.firstLetterDeCapped(pageName);
            List<Bundle<WebElement, String, String>> bundles = new ArrayList<>();
            for (Map<String, String> form : signForms) {
                String inputName = form.get("Input Element");
                String input = strUtils.contextCheck(form.get("Input"));
                Bundle<WebElement, String, String> bundle = new Bundle<>(
                        reflections.getElementFromPage(inputName, pageName, objectRepository),
                        input,
                        inputName
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
         * @param objectRepository instance that includes specified page instance
         */
        public List<Bundle<WebElement, String, String>> acquireComponentElementList(List<Map<String, String>> signForms, String componentName, String pageName, Object objectRepository) {
            log.info("Acquiring element list from " + highlighted(BLUE, pageName));
            pageName = strUtils.firstLetterDeCapped(pageName);
            List<Bundle<WebElement, String, String>> bundles = new ArrayList<>();
            for (Map<String, String> form : signForms) {
                String inputName = form.get("Input Element");
                String input = strUtils.contextCheck(form.get("Input"));
                componentName = strUtils.firstLetterDeCapped(componentName);
                Bundle<WebElement, String, String> bundle = new Bundle<>(
                        reflections.getElementFromComponent(inputName, componentName, pageName, objectRepository),
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
         * @param elementFieldName The name of the element field in the page object.
         * @param pageName The name of the page object.
         * @param specifications A map containing the specifications for the element to be retrieved from the page object, including the element name.
         * @param objectRepository The object repository containing the page object.
         * @return An element bundle containing the element name, the matching element, and a map of the element's attributes.
         */
        public Bundle<String, WebElement, Map<String, String>> acquireElementBundleFromPage(
                String elementFieldName,
                String pageName,
                Map<String, String> specifications,
                Object objectRepository
        ){
            log.info("Acquiring element bundle from " + highlighted(BLUE, pageName));
            return new Bundle<>(elementFieldName, acquireElementFromPage(elementFieldName, pageName, objectRepository), specifications);
        }

        /**
         * Returns a list of element bundles from a page object, based on provided specifications.
         *
         * @param pageName The name of the page object.
         * @param specifications A list of maps containing the specifications for each element to be retrieved from the page object, including the element name.
         * @param objectRepository The object repository containing the page object.
         * @return A list of element bundles containing the element name, the matching element, and a map of the element's attributes.
         */
        public List<Bundle<String, WebElement, Map<String, String>>> acquireElementBundlesFromPage(
                String pageName,
                List<Map<String, String>> specifications,
                Object objectRepository
        ){
            log.info("Acquiring element bundle from " + highlighted(BLUE, pageName));
            List<Bundle<String, WebElement, Map<String, String>>> bundles = new ArrayList<>();
            for (Map<String, String> specification:specifications) {
                bundles.add(acquireElementBundleFromPage(specification.get("Element Name"), pageName, specification, objectRepository));
            }
            return bundles;
        }

        /**
         * Returns an element bundle from a specified component in a page object, based on provided specifications.
         *
         * @param componentFieldName The name of the component field in the page object.
         * @param pageName The name of the page object.
         * @param specifications A map containing the specifications for the element to be retrieved from the component, including the element name.
         * @param objectRepository The object repository containing the page object.
         * @return An element bundle containing the element name, the matching element, and a map of the element's attributes.
         */
        public Bundle<String, WebElement, Map<String, String>> acquireElementBundleFromComponent(
                String componentFieldName,
                String pageName,
                Map<String, String> specifications,
                Object objectRepository
        ){
            log.info("Acquiring element bundle from " + highlighted(BLUE, pageName));
            return new Bundle<>(specifications.get("Element Name"), acquireElementFromComponent(
                    specifications.get("Element Name"),
                    componentFieldName,
                    pageName,
                    objectRepository
            ), specifications);
        }

        /**
         * Returns a list of element bundles from a specified component in a page object, based on provided specifications.
         *
         * @param componentFieldName The name of the component field in the page object.
         * @param pageName The name of the page object.
         * @param specifications A list of maps containing the specifications for each element to be retrieved from the component, including the element name.
         * @param objectRepository The object repository containing the page object.
         * @return A list of element bundles containing the element name, the matching element, and a map of the element's attributes.
         */
        public List<Bundle<String, WebElement, Map<String, String>>> acquireElementBundlesFromComponent(
                String componentFieldName,
                String pageName,
                List<Map<String, String>> specifications,
                Object objectRepository
        ){
            log.info("Acquiring element bundle from " + highlighted(BLUE, pageName));
            List<Bundle<String, WebElement, Map<String, String>>> bundles = new ArrayList<>();
            for (Map<String, String> specification:specifications) {
                bundles.add(
                        new Bundle<>(specification.get("Element Name"),
                                acquireElementFromComponent(
                                        specification.get("Element Name"),
                                        componentFieldName,
                                        pageName,
                                        objectRepository
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
         * @param objectRepository The object repository containing the page object.
         * @return An element bundle containing the target element name, the matching element, and a map of the element's attributes.
         */
        public Bundle<String, WebElement, Map<String, String>> selectChildElementFromComponentsBySecondChildText(
                Map<String, String> specifications,
                String componentListName,
                String pageName,
                Object objectRepository
        ){
            log.info("Acquiring element bundle from " + highlighted(BLUE, pageName));
            String selectorElementText = strUtils.contextCheck(specifications.get("Selector Text"));
            String selectorElementName = strUtils.contextCheck(specifications.get("Selector Element"));
            String targetElementName = strUtils.contextCheck(specifications.get("Target Element"));
            pageName = strUtils.firstLetterDeCapped(pageName);
            List<WebComponent> components = reflections.getComponentsFromPage(componentListName, pageName, objectRepository);
            WebComponent component = reflections.acquireExactNamedComponentAmongst(components, selectorElementText, selectorElementName);
            return new Bundle<>(targetElementName, reflections.getElementFromComponent(targetElementName, component), specifications);
        }

        /**
         * Returns a list of element bundles from a page object's component list, where the second child element's text matches the provided specifications.
         *
         * @param specifications A list of maps containing the specifications for the second child element's text.
         * @param componentListName The name of the component list in the page object.
         * @param pageName The name of the page object.
         * @param objectRepository The object repository containing the page object.
         * @return A list of element bundles containing the component name, the matching element, and a map of the element's attributes.
         */
        public List<Bundle<String, WebElement, Map<String, String>>> selectChildElementsFromComponentsBySecondChildText(
                List<Map<String, String>> specifications,
                String componentListName,
                String pageName,
                Object objectRepository
        ){
            log.info("Acquiring element bundles from " + highlighted(BLUE, pageName));
            List<Bundle<String, WebElement, Map<String, String>>> pairs = new ArrayList<>();
            for (Map<String, String> map:specifications) {
                pairs.add(selectChildElementFromComponentsBySecondChildText(map, componentListName, pageName, objectRepository));
            }
            return pairs;
        }
    }

    public static class PageObjectJson extends WebUtilities {

        /**
         *
         * Acquires an element selector by desired selector types from a given Json file
         *
         * @param elementName target element name
         * @param pageName page name that includes target element selectors
         * @param objectRepository target json file directory
         * @param selectorTypes desired selector types
         * @return target element
         */
        public WebElement elementFromPage(String elementName, String pageName, JsonObject objectRepository, SelectorType... selectorTypes){
            log.info("Acquiring element " +
                    highlighted(BLUE, elementName) +
                    highlighted(GRAY," from the ") +
                    highlighted(BLUE, pageName)
            );
            JsonObject elementJson = getElementJson(elementName, pageName, objectRepository);
            assert elementJson != null;
            ByAll byAll = getByAll(elementJson, selectorTypes);
            return driver.findElement(byAll);
        }

        /**
         *
         * Acquires an element list selector by desired selector types from a given Json file
         *
         * @param elementName target element name
         * @param pageName page name that includes target element selectors
         * @param objectRepository target json file directory
         * @param selectorTypes desired selector types
         * @return target element list
         */
        public List<WebElement> elementsFromPage(String elementName, String pageName, JsonObject objectRepository, SelectorType... selectorTypes){
            log.info("Acquiring element " +
                    highlighted(BLUE, elementName) +
                    highlighted(GRAY," from the ") +
                    highlighted(BLUE, pageName)
            );
            JsonObject elementJson = getElementJson(elementName, pageName, objectRepository);
            assert elementJson != null;
            ByAll byAll = getByAll(elementJson, selectorTypes);
            return driver.findElements(byAll);
        }

        public ByAll getByAll(JsonObject elementJson, SelectorType... selectorTypes){
            List<By> locators = new ArrayList<>();
            for (SelectorType selectorType:selectorTypes) {
                try {
                    By locator;
                    switch (selectorType){
                        case id ->          locator = By.id(elementJson.get("id").getAsJsonPrimitive().getAsString());
                        case name ->        locator = By.name(elementJson.get("name").getAsJsonPrimitive().getAsString());
                        case tagName ->     locator = By.tagName(elementJson.get("tagName").getAsJsonPrimitive().getAsString());
                        case className ->   locator = By.className(elementJson.get("className").getAsJsonPrimitive().getAsString());
                        case css ->         locator = By.cssSelector(elementJson.get("cssSelector").getAsJsonPrimitive().getAsString());
                        case xpath ->       locator = By.xpath(elementJson.get("xpath").getAsJsonPrimitive().getAsString());
                        case text -> {
                            String text = elementJson.get("text").getAsJsonPrimitive().getAsString();
                            locator = By.xpath("//*[text()='" + text + "']");
                        }
                        default -> throw new EnumConstantNotPresentException(SelectorType.class, selectorType.name());
                    }
                    locators.add(locator);
                }
                catch (NullPointerException | IllegalStateException ignored){}

            }
            return new ByAll(locators.toArray(new By[0]));
        }

        /**
         * Generates an element using a primary selector by given element attributes (css or xpath)
         *
         * @param selectorType desired primary selector type
         * @param attributePairs target element attributes as 'label = value'
         * @return target element
         */
        @SafeVarargs
        public final WebElement getElementByAttributes(PrimarySelectorType selectorType, Pair<String, String>... attributePairs){
            By locator;
            switch (selectorType){
                case css ->     locator = By.cssSelector(generateCssByAttributes(attributePairs));
                case xpath ->   locator = By.xpath(generateXPathByAttributes(attributePairs));
                default -> throw new EnumConstantNotPresentException(PrimarySelectorType.class, selectorType.name());
            }
            return driver.findElement(locator);
        }

        /**
         *
         * Generates an element list using a primary selector by given element attributes (css or xpath)
         *
         * @param attributePairs target element attributes as 'label = value'
         * @return target element list
         */
        @SafeVarargs
        public final List<WebElement> getElementsByAttributes(PrimarySelectorType selectorType, Pair<String, String>... attributePairs){
            By locator;
            switch (selectorType){
                case css ->     locator = By.cssSelector(generateCssByAttributes(attributePairs));
                case xpath ->   locator = By.xpath(generateXPathByAttributes(attributePairs));
                default -> throw new EnumConstantNotPresentException(PrimarySelectorType.class, selectorType.name());
            }
            return driver.findElements(locator);
        }

        /**
         *
         * Generates cssSelector by element attributes
         *
         * @param attributePairs target element attributes as 'label = value'
         * @return target element selector
         */
        @SafeVarargs
        public final String generateCssByAttributes(Pair<String, String>... attributePairs){
            StringBuilder selector = new StringBuilder();
            for (Pair<String, String> attributePair:attributePairs) {
                StringJoiner cssFormat = new StringJoiner(
                        "",
                        "[",
                        "']"
                );
                selector.append(cssFormat.add(attributePair.alpha() + " = '" + attributePair.beta()));
            }
            return selector.toString();
        }

        /**
         *
         * Generates xPath by element attributes
         *
         * @param attributePairs target element attributes as 'label = value'
         * @return target element selector
         */
        @SafeVarargs
        public final String generateXPathByAttributes(Pair<String, String>... attributePairs){
            StringBuilder selector = new StringBuilder();
            selector.append("//*");
            for (Pair<String, String> attributePair:attributePairs) {
                StringJoiner cssFormat = new StringJoiner(
                        "",
                        "[@",
                        "']"
                );
                selector.append(cssFormat.add(attributePair.alpha() + " = '" + attributePair.beta()));
            }
            return selector.toString();
        }

        /**
         * Acquires specified selectors for target element from a given Json file.
         * Json file includes specified page names with element selectors.
         *
         * @param elementName specified target element name
         * @param pageName specified page name that includes target element selectors
         * @param objectRepository target json file directory
         * @return target element selectors as JsonObject
         */
        public static JsonObject getElementJson(String elementName, String pageName, JsonObject objectRepository){
            JsonArray pages = objectRepository.getAsJsonArray("pages");

            JsonObject pageJson = Objects.requireNonNull(
                    pages.asList().stream().filter(
                            page -> page.getAsJsonObject().get("name").getAsJsonPrimitive().getAsString().equals(pageName)
                    ).findAny().orElse(null)
            ).getAsJsonObject();

            JsonArray elements = pageJson.getAsJsonArray("elements");
            for (JsonElement elementJson:elements)
                if (elementJson.getAsJsonObject().get("elementName").getAsJsonPrimitive().getAsString().equals(elementName))
                    return elementJson.getAsJsonObject();

            return null;
        }
    }

    public static class Reflections extends WebUtilities {
        protected ReflectionUtilities reflectionUtils = new ReflectionUtilities();

        /**
         * Acquires an element from a given page
         *
         * @param elementFieldName element field name
         * @param pageName name of the page instance
         * @param objectRepository instance of an object that contains instances of every page
         * @return returns the element
         */
        protected WebElement getElementFromPage(String elementFieldName, String pageName, Object objectRepository){
            Map<String, Object> pageFields;
            Object pageObject = reflectionUtils.getFields(objectRepository).get(pageName);
            if (pageObject != null) pageFields = reflectionUtils.getFields(pageObject);
            else throw new PickleibException("ObjectRepository does not contain an instance of " + pageName + " object!");
            return (WebElement) pageFields.get(elementFieldName);
        }

        /**
         * Acquires a list of elements from a given page
         *
         * @param elementFieldName element field name
         * @param pageName name of the page instance
         * @param objectRepository instance of an object that contains instances of every page
         * @return returns the list of elements
         */
        @SuppressWarnings("unchecked")
        protected List<WebElement> getElementsFromPage(String elementFieldName, String pageName, Object objectRepository){
            Map<String, Object> pageFields;
            Object pageObject = reflectionUtils.getFields(objectRepository).get(pageName);
            if (pageObject != null) pageFields = reflectionUtils.getFields(pageObject);
            else throw new PickleibException("ObjectRepository does not contain an instance of " + pageName + " object!");
            return (List<WebElement>) pageFields.get(elementFieldName);
        }

        /**
         * Acquires an element from a given component
         *
         * @param elementFieldName element field name
         * @param selectionName element text
         * @param pageName name of the page instance
         * @param objectRepository instance of an object that contains instances of every page
         * @return returns the element
         */
        protected WebElement getElementAmongstComponentsFromPage(
                String elementFieldName,
                String selectionName,
                String componentListName,
                String pageName,
                Object objectRepository){
            List<WebComponent> componentList = getComponentsFromPage(componentListName, pageName, objectRepository);
            WebComponent component = acquireNamedComponentAmongst(componentList, selectionName);
            Map<String, Object> componentFields = reflectionUtils.getFields(component);
            return (WebElement) componentFields.get(elementFieldName);
        }

        /**
         * Acquires a list of elements from a given component
         *
         * @param elementFieldName element field name
         * @param selectionName element text
         * @param pageName name of the page instance
         * @param objectRepository instance of an object that contains instances of every page
         * @return returns the list of elements
         */
        @SuppressWarnings("unchecked")
        protected List<WebElement> getElementsAmongstComponentsFromPage(
                String elementFieldName,
                String selectionName,
                String componentListName,
                String pageName,
                Object objectRepository){
            List<WebComponent> componentList = getComponentsFromPage(componentListName, pageName, objectRepository);
            WebComponent component = acquireNamedComponentAmongst(componentList, selectionName);
            Map<String, Object> componentFields = reflectionUtils.getFields(component);
            return (List<WebElement>) componentFields.get(elementFieldName);
        }

        /**
         * Acquires an element from a component amongst a list of components
         *
         * @param elementFieldName element field name
         * @param selectionName element text
         * @param pageName name of the page instance
         * @param objectRepository instance of an object that contains instances of every page
         * @return returns the element
         */
        protected WebElement getElementAmongstNamedComponentsFromPage(
                String elementFieldName,
                String selectionName,
                String componentListName,
                String pageName,
                Object objectRepository){
            List<WebComponent> componentList = getComponentsFromPage(componentListName, pageName, objectRepository);
            WebComponent component = acquireNamedComponentAmongst(componentList, selectionName);
            Map<String, Object> componentFields = reflectionUtils.getFields(component);
            return (WebElement) componentFields.get(elementFieldName);
        }

        /**
         * Acquires a list of elements from a component amongst a list of components
         *
         * @param listFieldName list field name
         * @param selectionName element text
         * @param pageName name of the page instance
         * @param objectRepository instance of an object that contains instances of every page
         * @return returns the list of elements
         */
        @SuppressWarnings("unchecked")
        protected List<WebElement> getElementsAmongstNamedComponentsFromPage(
                String listFieldName,
                String selectionName,
                String componentListName,
                String pageName,
                Object objectRepository){
            List<WebComponent> componentList = getComponentsFromPage(componentListName, pageName, objectRepository);
            WebComponent component = acquireNamedComponentAmongst(componentList, selectionName);
            Map<String, Object> componentFields = reflectionUtils.getFields(component);
            return (List<WebElement>) componentFields.get(listFieldName);
        }

        /**
         * Acquires an element from a component amongst a list of components
         *
         * @param elementFieldName element field name
         * @param elementIdentifier element text
         * @param pageName name of the page instance
         * @param objectRepository instance of an object that contains instances of every page
         * @return returns the element if exact match found
         */
        @Deprecated(since = "1.6.2")
        protected WebElement getElementAmongstExactComponentsFromPage(
                String elementFieldName,
                String elementIdentifier,
                String componentListName,
                String pageName,
                Object objectRepository){
            WebComponent component = acquireExactNamedComponentAmongst(elementIdentifier, elementFieldName, componentListName, pageName, objectRepository);
            Map<String, Object> componentFields = reflectionUtils.getFields(component);
            return (WebElement) componentFields.get(elementFieldName);
        }

        /**
         * Acquires a map of fields from a given component
         *
         * @param componentName component name
         * @param pageName name of the page instance
         * @param objectRepository instance of an object that contains instances of every page
         * @return returns map of fields
         */
        protected Map<String, Object> getComponentFieldsFromPage(String componentName, String pageName, Object objectRepository){
            Map<String, Object> componentFields;
            Object pageObject = reflectionUtils.getFields(objectRepository).get(pageName);
            if (pageObject != null) componentFields = reflectionUtils.getFields(pageObject);
            else throw new PickleibException("ObjectRepository does not contain an instance of " + pageName + " object!");
            return reflectionUtils.getFields(componentFields.get(componentName));
        }

        /**
         * Acquires a list of element from a given page
         *
         * @param componentListName component list name
         * @param pageName name of the page instance
         * @param objectRepository instance of an object that contains instances of every page
         * @return returns the list of components
         */
        @SuppressWarnings("unchecked")
        protected List<WebComponent> getComponentsFromPage(String componentListName, String pageName, Object objectRepository){
            Map<String, Object> pageFields;
            Map<String, Object> componentFields;
            Object pageObject = reflectionUtils.getFields(objectRepository).get(pageName);
            if (pageObject != null) pageFields = reflectionUtils.getFields(pageObject);
            else throw new PickleibException("ObjectRepository does not contain an instance of " + pageName + " object!");
            return (List<WebComponent>) pageFields.get(componentListName);
        }

        /**
         * Acquire a map of fields from a given component
         *
         * @param componentName component name
         * @return returns the map of fields
         */
        protected Map<String, Object> getComponentFields(Object componentName){
            return  reflectionUtils.getFields(componentName);
        }

        /**
         * Acquire listed component by the text of its given child element
         *
         * @param items list of components
         * @param attributeName component element attribute name
         * @param attributeValue attribute value
         * @param elementFieldName component elements field name
         * @return returns the matching component
         * @param <T> component type
         */
        protected  <T> T acquireComponentByElementAttributeAmongst(
                List<T> items,
                String attributeName,
                String attributeValue,
                String elementFieldName
        ){
            log.info("Acquiring component by attribute " + strUtils.highlighted(BLUE, attributeName + " -> " + attributeValue));
            boolean timeout = false;
            long initialTime = System.currentTimeMillis();
            while (!timeout){
                for (T component : items) {
                    Map<String, Object> componentFields = reflectionUtils.getFields(component);
                    WebElement element = (WebElement) componentFields.get(elementFieldName);
                    String attribute = element.getAttribute(attributeName);
                    if (attribute.equals(attributeValue)) return component;
                }
                if (System.currentTimeMillis() - initialTime > elementTimeout) timeout = true;
            }
            throw new NoSuchElementException("No component with " + attributeName + " : " + attributeValue + " could be found!");
        }

        /**
         * Acquire listed component by the text of its given child element
         *
         * @param items list of components
         * @param elementText text of the component element
         * @param targetElementFieldName component elements field name
         * @return returns the matching component
         * @param <Component> component type
         */
        protected  <Component extends WebComponent> Component acquireExactNamedComponentAmongst(
                List<Component> items,
                String elementText,
                String targetElementFieldName
        ){
            log.info("Acquiring component called " + strUtils.highlighted(BLUE, elementText));
            boolean timeout = false;
            long initialTime = System.currentTimeMillis();
            while (!timeout){
                for (Component component : items) {
                    Map<String, Object> componentFields = reflectionUtils.getFields(component);
                    WebElement element = (WebElement) componentFields.get(targetElementFieldName);
                    String text = element.getText();
                    String name = element.getAccessibleName();
                    if (text.equalsIgnoreCase(elementText) || name.equalsIgnoreCase(elementText)) return component;
                }
                if (System.currentTimeMillis() - initialTime > elementTimeout) timeout = true;
            }
            throw new NoSuchElementException("No component with text/name '" + elementText + "' could be found!");
        }

        /**
         *
         * @deprecated replaced by acquireExactNamedComponentAmongst(components, elementText, elementFieldName)
         */
        @Deprecated(since = "1.6.2")
        protected WebComponent acquireExactNamedComponentAmongst(
                String elementText,
                String elementFieldName,
                String componentListName,
                String pageName,
                Object objectRepository){
            log.info("Acquiring component called " + strUtils.highlighted(BLUE, elementText));
            boolean timeout = false;
            long initialTime = System.currentTimeMillis();
            while (!timeout){
                for (WebComponent component : getComponentsFromPage(componentListName, pageName, objectRepository)) {
                    Map<String, Object> componentFields = reflectionUtils.getFields(component);
                    WebElement element = (WebElement) componentFields.get(elementFieldName);
                    String text = element.getText();
                    String name = element.getAccessibleName();
                    if (text.equalsIgnoreCase(elementText) || name.equalsIgnoreCase(elementText)) return component;
                }
                if (System.currentTimeMillis() - initialTime > elementTimeout) timeout = true;
            }
            throw new NoSuchElementException("No component with text/name '" + elementText + "' could be found!");
        }

        /**
         * Acquires web element from a page object by using Java reflections
         *
         * @param fieldName field name of the element, in the page object
         * @param inputClass instance of the page object that the WebElement resides in
         * @return corresponding WebElement from the given page object
         */
        protected  <T> WebElement getElement(String fieldName, Class<T> inputClass){
            return (WebElement) reflectionUtils.getFieldValue(fieldName, inputClass);
        }

        /**
         * Acquires an element from a given component name
         *
         * @param elementFieldName element field name
         * @param componentName target component
         * @param pageName name of the page instance
         * @param objectRepository instance of an object that contains instances of every page
         * @return returns the element
         */
        protected WebElement getElementFromComponent(String elementFieldName, String componentName, String pageName, Object objectRepository){
            return (WebElement) getComponentFieldsFromPage(componentName, pageName, objectRepository).get(elementFieldName);
        }

        /**
         * Acquires an element from a given component
         *
         * @param elementFieldName element field name
         * @param component target component
         * @return returns the element
         */
        protected WebElement getElementFromComponent(String elementFieldName, WebComponent component){
            return (WebElement) getComponentFields(component).get(elementFieldName);
        }

        /**
         * Acquires a list elements from a given component name
         *
         * @param listFieldName element field
         * @param componentName target component name
         * @param pageName name of the page instance
         * @param objectRepository instance of an object that contains instances of every page
         * @return returns the list of elements
         */
        @SuppressWarnings("unchecked")
        protected List<WebElement> getElementsFromComponent(String listFieldName, String componentName, String pageName, Object objectRepository){
            return (List<WebElement>) getComponentFieldsFromPage(componentName, pageName, objectRepository).get(listFieldName);
        }

        /**
         * Acquires a list elements from a given component
         *
         * @param elementListFieldName elements list field
         * @param component target component
         * @return returns the list of elements
         */
        @SuppressWarnings("unchecked")
        protected List<WebElement> getElementsFromComponent(String elementListFieldName, Object component){
            return (List<WebElement>) getComponentFields(component).get(elementListFieldName);
        }

    }
}
