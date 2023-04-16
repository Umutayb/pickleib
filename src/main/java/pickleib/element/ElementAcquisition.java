package pickleib.element;

import com.github.webdriverextensions.WebComponent;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ByAll;
import pickleib.enums.PrimarySelectorType;
import pickleib.enums.SelectorType;
import pickleib.utilities.WebUtilities;
import records.Bundle;
import records.Pair;

import java.util.*;

import static utils.StringUtilities.Color.*;

@SuppressWarnings("unused")
public class ElementAcquisition {

    public static class PageObjectModel extends WebUtilities {
        /**
         *
         * Acquire element {element name} from {page name}
         *
         * @param elementName target button name
         * @param pageName specified page instance name
         * @param objectRepository instance that includes specified page instance
         */
        public WebElement elementFromPage(String elementName, String pageName, Object objectRepository){
            log.new Info("Acquiring element " +
                    highlighted(BLUE, elementName) +
                    highlighted(GRAY," from the ") +
                    highlighted(BLUE, pageName)
            );
            pageName = strUtils.firstLetterDeCapped(pageName);
            elementName = strUtils.contextCheck(elementName);
            return getElementFromPage(elementName, pageName, objectRepository);
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
        public WebElement elementFromComponent(String elementName, String componentFieldName, String pageName, Object objectRepository) {
            log.new Info("Acquiring element " +
                    highlighted(BLUE, elementName) +
                    highlighted(GRAY," from the ") +
                    highlighted(BLUE, pageName)
            );
            pageName = strUtils.firstLetterDeCapped(pageName);
            componentFieldName = strUtils.firstLetterDeCapped(componentFieldName);
            elementName = strUtils.contextCheck(elementName);
            return getElementFromComponent(elementName, componentFieldName, pageName, objectRepository);
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
        public WebElement listedElementFromPage(
                String elementName,
                String listName,
                String pageName,
                Object objectRepository
        ) {
            log.new Info("Acquiring listed element named " +
                    highlighted(BLUE, elementName) +
                    highlighted(GRAY," selected from ") +
                    highlighted(BLUE, listName) +
                    highlighted(GRAY," on  ") +
                    highlighted(BLUE, pageName)
            );

            pageName = strUtils.firstLetterDeCapped(pageName);
            listName = strUtils.firstLetterDeCapped(listName);
            elementName = strUtils.contextCheck(elementName);
            List<WebElement> elements = getElementsFromPage(
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
        public WebElement listedElementFromComponent(
                String elementName,
                String componentFieldName,
                String listFieldName,
                String pageName,
                Object objectRepository
        ) {
            log.new Info("Acquiring listed element named " +
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
            List<WebElement> elements = getElementsFromComponent(
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
        public WebElement listedComponentElement(
                String elementName,
                String componentName,
                String componentListName,
                String pageName,
                Object objectRepository
        ) {
            log.new Info("Acquiring listed element named " +
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
            List<WebComponent> componentList = getComponentsFromPage(componentListName, pageName, objectRepository);
            WebComponent component = acquireNamedComponentAmongst(componentList, componentName);
            return getElementFromComponent(elementName, component);
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
        public WebElement exactNamedListedComponentElement(
                String elementFieldName,
                String elementText,
                String componentListName,
                String pageName,
                Object objectRepository
        ) {
            log.new Info("Acquiring exact listed element named " +
                    highlighted(BLUE, elementFieldName) +
                    highlighted(GRAY," selected from ") +
                    highlighted(BLUE, componentListName) +
                    highlighted(GRAY," component list on the ") +
                    highlighted(BLUE, pageName)
            );
            WebComponent component = exactNamedListedComponent(elementFieldName, elementText, componentListName, pageName, objectRepository);
            return getElementFromComponent(elementFieldName, component);
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
        public WebComponent exactNamedListedComponent(
                String elementFieldName,
                String elementText,
                String componentListName,
                String pageName,
                Object objectRepository
        ) {
            log.new Info("Acquiring exact listed component by element named " +
                    highlighted(BLUE, elementFieldName) +
                    highlighted(GRAY," selected from ") +
                    highlighted(BLUE, componentListName) +
                    highlighted(GRAY," component list on the ") +
                    highlighted(BLUE, pageName)
            );
            pageName = strUtils.firstLetterDeCapped(pageName);
            componentListName = strUtils.firstLetterDeCapped(componentListName);
            elementFieldName = strUtils.contextCheck(elementFieldName);
            List<WebComponent> components = getComponentsFromPage(componentListName, pageName, objectRepository);
            return acquireExactNamedComponentAmongst(components, elementText, elementFieldName);
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
        public WebElement listedElementAmongstListedComponents(
                String elementName,
                String elementListName,
                String componentName,
                String componentListName,
                String pageName,
                Object objectRepository
        ) {
            log.new Info("Acquiring listed element named " +
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
            List<WebComponent> components = getComponentsFromPage(componentListName, pageName, objectRepository);
            WebComponent component = acquireNamedComponentAmongst(components, componentName);
            List<WebElement> elements = getElementsFromComponent(elementListName, component);
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
        public WebElement listedElementByAttribute(
                String attributeName,
                String attributeValue,
                String listName,
                String pageName,
                Object objectRepository
        ) {
            log.new Info("Acquiring element by " +
                    highlighted(BLUE, attributeName) +
                    highlighted(GRAY," attribute selected from ") +
                    highlighted(BLUE, listName) +
                    highlighted(GRAY, " list on the ") +
                    highlighted(BLUE, pageName)
            );
            attributeName = strUtils.contextCheck(attributeName);
            pageName = strUtils.firstLetterDeCapped(pageName);
            List<WebElement> elements = getElementsFromPage(
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
        public WebElement listedComponentElementByAttribute(
                String componentName,
                String attributeValue,
                String attributeName,
                String listName,
                String pageName,
                Object objectRepository
        ) {
            log.new Info("Acquiring element by " +
                    highlighted(BLUE, attributeName) +
                    highlighted(GRAY," attribute selected from ") +
                    highlighted(BLUE, listName) +
                    highlighted(GRAY, " list on the ") +
                    highlighted(BLUE, pageName)
            );
            attributeName = strUtils.contextCheck(attributeName);
            pageName = strUtils.firstLetterDeCapped(pageName);
            componentName = strUtils.firstLetterDeCapped(componentName);
            List<WebElement> elements = getElementsFromComponent(
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
        public List<Bundle<WebElement, String, String>> elementList(List<Map<String, String>> signForms, String pageName, Object objectRepository) {
            log.new Info("Acquiring element list from " + highlighted(BLUE, pageName));
            pageName = strUtils.firstLetterDeCapped(pageName);
            List<Bundle<WebElement, String, String>> bundles = new ArrayList<>();
            for (Map<String, String> form : signForms) {
                String inputName = form.get("Input Element");
                String input = strUtils.contextCheck(form.get("Input"));
                Bundle<WebElement, String, String> bundle = new Bundle<>(
                        getElementFromPage(inputName, pageName, objectRepository),
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
        public List<Bundle<WebElement, String, String>> componentElementList(List<Map<String, String>> signForms, String componentName, String pageName, Object objectRepository) {
            log.new Info("Acquiring element list from " + highlighted(BLUE, pageName));
            pageName = strUtils.firstLetterDeCapped(pageName);
            List<Bundle<WebElement, String, String>> bundles = new ArrayList<>();
            for (Map<String, String> form : signForms) {
                String inputName = form.get("Input Element");
                String input = strUtils.contextCheck(form.get("Input"));
                componentName = strUtils.firstLetterDeCapped(componentName);
                Bundle<WebElement, String, String> bundle = new Bundle<>(
                        getElementFromComponent(inputName, componentName, pageName, objectRepository),
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
        public Bundle<String, WebElement, Map<String, String>> elementBundleFromPage(
                String elementFieldName,
                String pageName,
                Map<String, String> specifications,
                Object objectRepository
        ){
            log.new Info("Acquiring element bundle from " + highlighted(BLUE, pageName));
            return new Bundle<>(elementFieldName, elementFromPage(elementFieldName, pageName, objectRepository), specifications);
        }

        /**
         * Returns a list of element bundles from a page object, based on provided specifications.
         *
         * @param pageName The name of the page object.
         * @param specifications A list of maps containing the specifications for each element to be retrieved from the page object, including the element name.
         * @param objectRepository The object repository containing the page object.
         * @return A list of element bundles containing the element name, the matching element, and a map of the element's attributes.
         */
        public List<Bundle<String, WebElement, Map<String, String>>> elementBundlesFromPage(
                String pageName,
                List<Map<String, String>> specifications,
                Object objectRepository
        ){
            log.new Info("Acquiring element bundle from " + highlighted(BLUE, pageName));
            List<Bundle<String, WebElement, Map<String, String>>> bundles = new ArrayList<>();
            for (Map<String, String> specification:specifications) {
                bundles.add(elementBundleFromPage(specification.get("Element Name"), pageName, specification, objectRepository));
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
        public Bundle<String, WebElement, Map<String, String>> elementBundleFromComponent(
                String componentFieldName,
                String pageName,
                Map<String, String> specifications,
                Object objectRepository
        ){
            log.new Info("Acquiring element bundle from " + highlighted(BLUE, pageName));
            return new Bundle<>(specifications.get("Element Name"), elementFromComponent(
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
        public List<Bundle<String, WebElement, Map<String, String>>> elementBundlesFromComponent(
                String componentFieldName,
                String pageName,
                List<Map<String, String>> specifications,
                Object objectRepository
        ){
            log.new Info("Acquiring element bundle from " + highlighted(BLUE, pageName));
            List<Bundle<String, WebElement, Map<String, String>>> bundles = new ArrayList<>();
            for (Map<String, String> specification:specifications) {
                bundles.add(
                        new Bundle<>(specification.get("Element Name"),
                                elementFromComponent(
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
            log.new Info("Acquiring element bundle from " + highlighted(BLUE, pageName));
            String selectorElementText = strUtils.contextCheck(specifications.get("Selector Text"));
            String selectorElementName = strUtils.contextCheck(specifications.get("Selector Element"));
            String targetElementName = strUtils.contextCheck(specifications.get("Target Element"));
            pageName = strUtils.firstLetterDeCapped(pageName);
            List<WebComponent> components = getComponentsFromPage(componentListName, pageName, objectRepository);
            WebComponent component = acquireExactNamedComponentAmongst(components, selectorElementText, selectorElementName);
            return new Bundle<>(targetElementName, getElementFromComponent(targetElementName, component), specifications);
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
            log.new Info("Acquiring element bundles from " + highlighted(BLUE, pageName));
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
            log.new Info("Acquiring element " +
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
            log.new Info("Acquiring element " +
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
                        case text ->{
                            String text = elementJson.get("text").getAsJsonPrimitive().getAsString();
                            locator = By.xpath("//*[text()='" +text+ "']");
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
                        attributePair.alpha() + " = '" + attributePair.beta(),
                        "[",
                        "']"
                );
                selector.append(cssFormat);
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
            for (Pair<String, String> attributePair:attributePairs) {
                StringJoiner cssFormat = new StringJoiner(
                        attributePair.alpha() + " = '" + attributePair.beta(),
                        "//*[@",
                        "']"
                );
                selector.append(cssFormat);
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
}
