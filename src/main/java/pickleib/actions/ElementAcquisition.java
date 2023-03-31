package pickleib.actions;

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


    /**
     * Acquires specified selectors for target element from a given Json file
     * Json file includes specified page names with element selectors
     *
     * @param elementName specified target element name
     * @param pageName specified page name that includes target element selectors
     * @param objectRepository target json file directory
     * @return target element selectors as JsonObject
     */
    public static JsonObject getElementJson(String elementName, String pageName, JsonObject objectRepository){
        JsonArray pages = objectRepository.getAsJsonArray("pages");
        JsonObject pageJson = Objects.requireNonNull(pages.asList().stream().filter(
                page -> page.getAsJsonObject().get("name").getAsString().equals(pageName)
        ).findAny().orElse(null)).getAsJsonObject();
        JsonArray elements = pageJson.getAsJsonArray("elements");
        for (JsonElement elementJson:elements)
            if (elementJson.getAsJsonObject().get("name").getAsString().equals(elementName))
                return elementJson.getAsJsonObject();
        return null;
    }

    /**
     * Acquisition methods for POM
     */

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
            return getElementFromComponent(elementName, componentFieldName, pageName, objectRepository);
        }

        /**
         *
         * Acquire listed component element {element name} of {component field name} from {component list name} list on the {page name}
         *
         * @param elementName target button name
         * @param componentFieldName specified component field name
         * @param componentListName specified component list name
         * @param pageName specified page instance name
         * @param objectRepository instance that includes specified page instance
         */
        public WebElement listedElementFromComponent(
                String elementName,
                String componentFieldName,
                String componentListName,
                String pageName,
                Object objectRepository
        ) {
            log.new Info("Acquiring listed element named " +
                    highlighted(BLUE, elementName) +
                    highlighted(GRAY," selected from ") +
                    highlighted(BLUE, componentListName) +
                    highlighted(GRAY," on the ") +
                    highlighted(BLUE, componentFieldName) +
                    highlighted(GRAY," component on the ") +
                    highlighted(BLUE, pageName)
            );

            componentFieldName = strUtils.firstLetterDeCapped(componentFieldName);
            pageName = strUtils.firstLetterDeCapped(pageName);
            componentListName = strUtils.firstLetterDeCapped(componentListName);
            elementName = strUtils.contextCheck(elementName);
            List<WebElement> elements = getElementsFromComponent(
                    componentListName,
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
                String componentName,
                String componentListName,
                String pageName,
                String elementName,
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
            List<WebComponent> componentList = getComponentsFromPage(componentListName, pageName, objectRepository);
            WebComponent component = acquireNamedComponentAmongst(componentList, componentName);
            return getElementFromComponent(elementName, component);
        }


        /**
         *
         * Select exact component named {component name} from {component list name} component list on the {page name} and acquire the {element name} element
         *
         * @param componentName specified component name
         * @param componentListName specified component list name
         * @param pageName specified page instance name
         * @param elementName target element name
         * @param objectRepository instance that includes specified page instance
         */
        public WebElement exactNamedListedComponentElement(
                String componentName,
                String componentListName,
                String pageName,
                String elementName,
                Object objectRepository
        ) {
            log.new Info("Acquiring exact listed element named " +
                    highlighted(BLUE, elementName) +
                    highlighted(GRAY," selected from ") +
                    highlighted(BLUE, componentListName) +
                    highlighted(GRAY," component list on the ") +
                    highlighted(BLUE, pageName)
            );
            pageName = strUtils.firstLetterDeCapped(pageName);
            componentListName = strUtils.firstLetterDeCapped(componentListName);
            List<WebComponent> components = getComponentsFromPage(componentListName, pageName, objectRepository);
            WebComponent component = acquireExactNamedComponentAmongst(components, componentName, elementName);
            return getElementFromComponent(elementName, component);
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
                String componentName,
                String componentListName,
                String pageName,
                String elementName,
                String elementListName,
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
        public WebElement listedElementByAttribute(
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
            List<WebElement> elements = getElementsFromComponent(
                    listName,
                    strUtils.firstLetterDeCapped(componentName),
                    strUtils.firstLetterDeCapped(pageName),
                    objectRepository
            );
            return acquireElementUsingAttributeAmongst(elements, attributeName, attributeValue);
        }

        /**
         * Acquire component form input on the {page name}
         *
         * @param pageName         specified page instance name
         * @param signForms        table that has key as "Input" and value as "Input Element" (dataTable.asMaps())
         * @param objectRepository instance that includes specified page instance
         */
        public List<Bundle<WebElement, String, String>> elementList(List<Map<String, String>> signForms, String pageName, Object objectRepository) {
            List<Bundle<WebElement, String, String>> bundles = new ArrayList<>();
            for (Map<String, String> form : signForms) {
                String inputName = form.get("Input Element");
                String input = strUtils.contextCheck(form.get("Input"));
                log.new Info("Filling " +
                        highlighted(BLUE, inputName) +
                        highlighted(GRAY, " on the ") +
                        highlighted(BLUE, pageName) +
                        highlighted(GRAY, " with the text: ") +
                        highlighted(BLUE, input)
                );
                Bundle<WebElement, String, String> bundle = new Bundle<>(
                        getElementFromPage(inputName, pageName, objectRepository),
                        input,
                        inputName
                );
                bundles.add(bundle);
            }
            return bundles;
        }

    }

    /**
     * Acquisition methods for POJson
     */
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

            List<By> locators = new ArrayList<>();
            for (SelectorType selectorType:selectorTypes) {
                By locator;
                switch (selectorType){
                    case id ->          locator = By.id(elementJson.get("id").getAsString());
                    case name ->        locator = By.name(elementJson.get("name").getAsString());
                    case tagName ->     locator = By.tagName(elementJson.get("tagName").getAsString());
                    case className ->   locator = By.className(elementJson.get("className").getAsString());
                    case css ->         locator = By.cssSelector(elementJson.get("cssSelector").getAsString());
                    case xpath ->       locator = By.xpath(elementJson.get("xpath").getAsString());
                    case text ->{
                        String text = elementJson.get("text").getAsString();
                        locator = By.xpath("//*[text()='" +text+ "']");
                    }
                    default -> throw new EnumConstantNotPresentException(SelectorType.class, selectorType.name());
                }
                locators.add(locator);
            }

            ByAll byAll = new ByAll(new By[locators.size()]);
            return driver.findElement(byAll);
        }

        /**
         *
<<<<<<< Updated upstream:src/main/java/pickleib/utilities/ElementAcquisition.java
         * Acquire element {element name} from {page name}
         *
         * @param elementName target button name
         * @param pageName specified page instance name
         * @param objectRepository instance that includes specified page instance
         */
        public List<WebElement> elementsFromPage(String elementName, String pageName, JsonObject objectRepository, SelectorType... selectorTypes){
            log.new Info("Acquiring element " +
                    highlighted(BLUE, elementName) +
                    highlighted(GRAY," from the ") +
                    highlighted(BLUE, pageName)
            );
            JsonObject elementJson = getElementJson(elementName, pageName, objectRepository);
            assert elementJson != null;

            List<By> locators = new ArrayList<>();
            for (SelectorType selectorType:selectorTypes) {
                By locator;
                switch (selectorType){
                    case id ->          locator = By.id(elementJson.get("id").getAsString());
                    case name ->        locator = By.name(elementJson.get("name").getAsString());
                    case tagName ->     locator = By.tagName(elementJson.get("tagName").getAsString());
                    case className ->   locator = By.className(elementJson.get("className").getAsString());
                    case css ->         locator = By.cssSelector(elementJson.get("cssSelector").getAsString());
                    case xpath ->       locator = By.xpath(elementJson.get("xpath").getAsString());
                    case text ->{
                        String text = elementJson.get("text").getAsString();
                        locator = By.xpath("//*[text()='" +text+ "']");
                    }
                    default -> throw new EnumConstantNotPresentException(SelectorType.class, selectorType.name());
                }
                locators.add(locator);
            }

            ByAll byAll = new ByAll(new By[locators.size()]);
            return driver.findElements(byAll);
        }


        /**
         * Generates a primary selector by element attributes (css or xpath)
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
         * Generates cssSelector by element attributes
         *
         * @param attributePairs target element attributes as 'label = value'
         * @return target element selector
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

        public static JsonObject getElementJson(String elementName, String pageName, JsonObject objectRepository){
            JsonArray pages = objectRepository.getAsJsonArray("pages");

            JsonObject pageJson = Objects.requireNonNull(
                    pages.asList().stream().filter(
                            page -> page.getAsJsonObject().get("name").getAsString().equals(pageName)
                    ).findAny().orElse(null)
            ).getAsJsonObject();

            JsonArray elements = pageJson.getAsJsonArray("elements");
            for (JsonElement elementJson:elements)
                if (elementJson.getAsJsonObject().get("name").getAsString().equals(elementName))
                    return elementJson.getAsJsonObject();
            return null;
        }
    }
}
