package pickleib.element;

import com.github.webdriverextensions.WebComponent;
import org.openqa.selenium.WebElement;
import pickleib.utilities.WebUtilities;
import records.Bundle;
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
//
     //   /**
     //    * Finds WebElements based on the provided element name, page name, object repository, and selector types.
     //    * @param elementName a String representing the name of the element to find
     //    * @param pageName a String representing the name of the page containing the element
     //    * @param objectRepository a JsonObject representing the object repository where the element is defined
     //    * @param selectorTypes an array of SelectorType enum values representing the types of selectors to use
     //    * @return a List of WebElements that match the provided element name and selector types
     //    * @throws NoSuchElementException if no matching WebElement is found
     //    */
     //   public WebElement elementFromPage(
     //           String elementName,
     //           String pageName,
     //           JsonObject objectRepository,
     //           SelectorType... selectorTypes
     //   ){
     //       log.new Info("Acquiring element " +
     //               highlighted(BLUE, elementName) +
     //               highlighted(GRAY," from the ") +
     //               highlighted(BLUE, pageName)
     //       );
     //       JsonObject elementJson = getElementJson(elementName, pageName, objectRepository);
     //       assert elementJson != null;
//
     //       List<By> locators = new ArrayList<>();
     //       for (SelectorType selectorType:selectorTypes) {
     //           By locator;
     //           switch (selectorType){
     //               case id ->          locator = By.id(elementJson.get("id").getAsString());
     //               case name ->        locator = By.name(elementJson.get("name").getAsString());
     //               case tagName ->     locator = By.tagName(elementJson.get("tagName").getAsString());
     //               case className ->   locator = By.className(elementJson.get("className").getAsString());
     //               case css ->         locator = By.cssSelector(elementJson.get("cssSelector").getAsString());
     //               case xpath ->       locator = By.xpath(elementJson.get("xpath").getAsString());
     //               case text ->{
     //                   String text = elementJson.get("text").getAsString();
     //                   locator = By.xpath("//*[text()='" +text+ "']");
     //               }
     //               default -> throw new EnumConstantNotPresentException(SelectorType.class, selectorType.name());
     //           }
     //           locators.add(locator);
     //       }
//
     //       ByAll byAll = new ByAll(new By[locators.size()]);
     //       return driver.findElement(byAll);
     //   }
//
     //   /**
     //    *
     //    * Acquire element {element name} from {page name}
     //    *
     //    * @param elementName target button name
     //    * @param pageName specified page instance name
     //    * @param objectRepository instance that includes specified page instance
     //    */
     //   public List<WebElement> elementsFromPage(String elementName, String pageName, JsonObject objectRepository, SelectorType... selectorTypes){
     //       log.new Info("Acquiring element " +
     //               highlighted(BLUE, elementName) +
     //               highlighted(GRAY," from the ") +
     //               highlighted(BLUE, pageName)
     //       );
     //       JsonObject elementJson = getElementJson(elementName, pageName, objectRepository);
     //       assert elementJson != null;
//
     //       List<By> locators = new ArrayList<>();
     //       for (SelectorType selectorType:selectorTypes) {
     //           By locator;
     //           switch (selectorType){
     //               case id ->          locator = By.id(elementJson.get("id").getAsString());
     //               case name ->        locator = By.name(elementJson.get("name").getAsString());
     //               case tagName ->     locator = By.tagName(elementJson.get("tagName").getAsString());
     //               case className ->   locator = By.className(elementJson.get("className").getAsString());
     //               case css ->         locator = By.cssSelector(elementJson.get("cssSelector").getAsString());
     //               case xpath ->       locator = By.xpath(elementJson.get("xpath").getAsString());
     //               case text ->{
     //                   String text = elementJson.get("text").getAsString();
     //                   locator = By.xpath("//*[text()='" +text+ "']");
     //               }
     //               default -> throw new EnumConstantNotPresentException(SelectorType.class, selectorType.name());
     //           }
     //           locators.add(locator);
     //       }
//
     //       ByAll byAll = new ByAll(new By[locators.size()]);
     //       return driver.findElements(byAll);
     //   }
//
     //   /**
     //    * Finds a single WebElement based on the provided attribute pairs and selector type.
     //    * @param selectorType a PrimarySelectorType enum value representing the type of selector to use
     //    * @param attributePairs an array of Pair objects representing attribute name-value pairs
     //    * @return a WebElement that matches the provided selector and attributes
     //    * @throws EnumConstantNotPresentException if an invalid PrimarySelectorType value is provided
     //    * @throws NoSuchElementException if no matching WebElement is found
     //    */
     //   @SafeVarargs
     //   public final WebElement getElementByAttributes(PrimarySelectorType selectorType, Pair<String, String>... attributePairs){
     //       By locator;
     //       switch (selectorType){
     //           case css ->     locator = By.cssSelector(generateCssByAttributes(attributePairs));
     //           case xpath ->   locator = By.xpath(generateXPathByAttributes(attributePairs));
     //           default -> throw new EnumConstantNotPresentException(PrimarySelectorType.class, selectorType.name());
     //       }
     //       return driver.findElement(locator);
     //   }
//
     //   /**
     //    * Finds WebElements based on the provided attribute pairs and selector type.
     //    * @param selectorType a PrimarySelectorType enum value representing the type of selector to use
     //    * @param attributePairs an array of Pair objects representing attribute name-value pairs
     //    * @return a List of WebElements that match the provided selector and attributes
     //    * @throws EnumConstantNotPresentException if an invalid PrimarySelectorType value is provided
     //    */
     //   @SafeVarargs
     //   public final List<WebElement> getElementsByAttributes(PrimarySelectorType selectorType, Pair<String, String>... attributePairs){
     //       By locator;
     //       switch (selectorType){
     //           case css ->     locator = By.cssSelector(generateCssByAttributes(attributePairs));
     //           case xpath ->   locator = By.xpath(generateXPathByAttributes(attributePairs));
     //           default -> throw new EnumConstantNotPresentException(PrimarySelectorType.class, selectorType.name());
     //       }
     //       return driver.findElements(locator);
     //   }
//
     //   /**
     //    * Generates a CSS selector based on the provided attribute pairs.
     //    * @param attributePairs an array of Pair objects representing attribute name-value pairs
     //    * @return a String representing the generated CSS selector
     //    */
     //   @SafeVarargs
     //   public final String generateCssByAttributes(Pair<String, String>... attributePairs){
     //       StringBuilder selector = new StringBuilder();
     //       for (Pair<String, String> attributePair:attributePairs) {
     //           StringJoiner cssFormat = new StringJoiner(
     //                   attributePair.alpha() + " = '" + attributePair.beta(),
     //                   "[",
     //                   "']"
     //           );
     //           selector.append(cssFormat);
     //       }
     //       return selector.toString();
     //   }
//
     //   /**
     //    * Generates an XPath selector based on the provided attribute pairs.
     //    * @param attributePairs an array of Pair objects representing attribute name-value pairs
     //    * @return a String representing the generated XPath selector
     //    */
     //   @SafeVarargs
     //   public final String generateXPathByAttributes(Pair<String, String>... attributePairs){
     //       StringBuilder selector = new StringBuilder();
     //       for (Pair<String, String> attributePair:attributePairs) {
     //           StringJoiner cssFormat = new StringJoiner(
     //                   attributePair.alpha() + " = '" + attributePair.beta(),
     //                   "//*[@",
     //                   "']"
     //           );
     //           selector.append(cssFormat);
     //       }
     //       return selector.toString();
     //   }
//
     //   /**
     //    * Returns the JSON object for the specified element in the given page from the object repository.
     //    *
     //    * @param elementName the name of the element to retrieve
     //    * @param pageName the name of the page containing the element
     //    * @param objectRepository the JSON object representing the object repository
     //    * @return the JSON object for the specified element in the given page, or null if not found
     //    * @throws NullPointerException if the specified page name is not found in the object repository
     //    */
     //   public static JsonObject getElementJson(String elementName, String pageName, JsonObject objectRepository){
     //       JsonArray pages = objectRepository.getAsJsonArray("pages");
     //       JsonObject pageJson = Objects.requireNonNull(
     //               pages.asList().stream().filter(
     //                       page -> page.getAsJsonObject().get("name").getAsString().equals(pageName)
     //               ).findAny().orElse(null)
     //       ).getAsJsonObject();
     //       JsonArray elements = pageJson.getAsJsonArray("elements");
     //       for (JsonElement elementJson:elements)
     //           if (elementJson.getAsJsonObject().get("name").getAsString().equals(elementName))
     //               return elementJson.getAsJsonObject();
     //       return null;
     //   }
    }
}
