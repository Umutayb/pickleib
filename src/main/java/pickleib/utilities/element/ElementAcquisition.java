package pickleib.utilities.element;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import context.ContextStore;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.pagefactory.ByAll;
import pickleib.enums.PrimarySelectorType;
import pickleib.enums.SelectorType;
import pickleib.exceptions.PickleibException;
import pickleib.utilities.interfaces.repository.PageRepository;
import collections.Bundle;
import collections.Pair;
import utils.Printer;
import utils.StringUtilities;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static utils.StringUtilities.*;
import static utils.reflection.ReflectionUtilities.getFieldValue;
import static utils.reflection.ReflectionUtilities.getFields;
import static utils.StringUtilities.Color.*;

@SuppressWarnings("unused")
public class ElementAcquisition {

    static long elementTimeout = Long.parseLong(ContextStore.get("element-timeout", "15000"));
    static Printer log = new Printer(ElementAcquisition.class);

    /**
     * Acquire listed component by the text of its given child element
     *
     * @param items list of components
     * @param attributeName component element attribute name
     * @param attributeValue attribute value
     * @param elementFieldName component elements field name
     * @return returns the matching component
     * @param <Component> component type
     */
    public static <Component> Component acquireComponentByElementAttributeAmongst(
            List<Component> items,
            String attributeName,
            String attributeValue,
            String elementFieldName
    ){
        log.info("Acquiring component by attribute " + highlighted(BLUE, attributeName + " -> " + attributeValue));
        boolean timeout = false;
        long initialTime = System.currentTimeMillis();
        while (!timeout){
            for (Component component : items) {
                Map<String, Object> componentFields = getFields(component);
                WebElement element = (WebElement) componentFields.get(elementFieldName);
                String attribute = element.getAttribute(attributeName);
                if (attribute.equals(attributeValue)) return component;
            }
            if (System.currentTimeMillis() - initialTime > elementTimeout) timeout = true;
        }
        throw new NoSuchElementException("No component with " + attributeName + " : " + attributeValue + " could be found!");
    }

    /**
     * Acquire a listed element by its attribute
     *
     * @param items list that includes target element
     * @param attributeName attribute name
     * @param attributeValue attribute value
     * @return returns the selected element
     */
    public static WebElement acquireElementUsingAttributeAmongst(List<WebElement> items, String attributeName, String attributeValue){
        log.info("Acquiring element called " + markup(BLUE, attributeValue) + " using its " + markup(BLUE, attributeName) + " attribute");
        boolean condition = true;
        boolean timeout = false;
        long initialTime = System.currentTimeMillis();
        WebDriverException caughtException = null;
        int counter = 0;
        while (!(System.currentTimeMillis() - initialTime > elementTimeout)){
            try {
                for (WebElement selection : items) {
                    String attribute = selection.getAttribute(attributeName);
                    if (attribute != null && (attribute.equalsIgnoreCase(attributeValue) || attribute.contains(attributeValue))) return selection;
                }
            }
            catch (WebDriverException webDriverException){
                if (counter != 0 && webDriverException.getClass().getName().equals(caughtException.getClass().getName()))
                    log.warning("Iterating... (" + webDriverException.getClass().getName() + ")");

                caughtException = webDriverException;
                counter++;
            }
        }
        throw new NoSuchElementException("No element with the attributes '" + attributeName + " : " + attributeValue + "' could be found!");
    }

    /**
     * Acquire listed element by its name
     *
     * @param items list that includes target element
     * @param selectionName element name
     * @return returns the selected element
     */
    public static WebElement acquireNamedElementAmongst(List<WebElement> items, String selectionName){
        boolean timeout = false;
        long initialTime = System.currentTimeMillis();
        WebDriverException caughtException = null;
        int counter = 0;
        do {
            try {
                for (WebElement selection : items) {
                    String text = selection.getText();
                    if (text.equalsIgnoreCase(selectionName) || text.contains(selectionName)) return selection;
                }
            }
            catch (WebDriverException webDriverException){
                if (counter == 0) {
                    log.warning("Iterating... (" + webDriverException.getClass().getName() + ")");
                    caughtException = webDriverException;
                }
                else if (!webDriverException.getClass().getName().equals(caughtException.getClass().getName())){
                    log.warning("Iterating... (" + webDriverException.getClass().getName() + ")");
                    caughtException = webDriverException;
                }
                counter++;
            }
        }
        while (!(System.currentTimeMillis() - initialTime > elementTimeout));
        throw new NoSuchElementException("No element with text/name '" + selectionName + "' could be found!");
    }

    /**
     * Acquire a component amongst a list of components by its name
     *
     * @param items list of components
     * @param selectionName component name
     * @return returns the selected component
     */
    public static <Component extends WebElement> Component acquireNamedComponentAmongst(
            List<Component> items,
            String selectionName
    ){
        log.info("Acquiring component called " + highlighted(BLUE, selectionName));
        boolean timeout = false;
        long initialTime = System.currentTimeMillis();
        WebDriverException caughtException = null;
        int counter = 0;
        while (!(System.currentTimeMillis() - initialTime > elementTimeout)){
            try {
                for (Component selection : items) {
                    String text = selection.getText();
                    if (text.equalsIgnoreCase(selectionName) || text.contains(selectionName)) return selection;
                }
            }
            catch (WebDriverException webDriverException){
                if (counter != 0 && webDriverException.getClass().getName().equals(caughtException.getClass().getName()))
                    log.warning("Iterating... (" + webDriverException.getClass().getName() + ")");

                caughtException = webDriverException;
                counter++;
            }
        }
        throw new NoSuchElementException("No component with text/name '" + selectionName + "' could be found!");
    }


    public static class PageObjectModel <ObjectRepository extends PageRepository> {
        Reflections<ObjectRepository> reflections;

        public PageObjectModel(Class<ObjectRepository> pageRepository) {
            reflections = new Reflections<>(pageRepository);
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
         * Acquire component element {element name} of {component field name} component on the {page name}
         *
         * @param elementName target button name
         * @param componentFieldName specified component field name
         * @param pageName specified page instance name
         */
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
        public List<Bundle<WebElement, String, String>> acquireElementList(List<Map<String, String>> signForms, String pageName) {
            log.info("Acquiring element list from " + highlighted(BLUE, pageName));
            pageName = firstLetterDeCapped(pageName);
            List<Bundle<WebElement, String, String>> bundles = new ArrayList<>();
            for (Map<String, String> form : signForms) {
                String inputName = form.get("Input Element");
                String input = contextCheck(form.get("Input"));
                Bundle<WebElement, String, String> bundle = new Bundle<>(
                        reflections.getElementFromPage(inputName, pageName),
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
         */
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
        public Bundle<String, WebElement, Map<String, String>> acquireElementBundleFromPage(
                String elementFieldName,
                String pageName,
                Map<String, String> specifications
        ){
            log.info("Acquiring element bundle from " + highlighted(BLUE, pageName));
            return new Bundle<>(elementFieldName, acquireElementFromPage(elementFieldName, pageName), specifications);
        }

        /**
         * Returns a list of element bundles from a page object, based on provided specifications.
         *
         * @param pageName The name of the page object.
         * @param specifications A list of maps containing the specifications for each element to be retrieved from the page object, including the element name.
         * @return A list of element bundles containing the element name, the matching element, and a map of the element's attributes.
         */
        public List<Bundle<String, WebElement, Map<String, String>>> acquireElementBundlesFromPage(
                String pageName,
                List<Map<String, String>> specifications
        ){
            log.info("Acquiring element bundle from " + highlighted(BLUE, pageName));
            List<Bundle<String, WebElement, Map<String, String>>> bundles = new ArrayList<>();
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

    public static class PageObjectJson {

        static RemoteWebDriver driver;
        static JsonObject objectRepository;

        public PageObjectJson(RemoteWebDriver driver, JsonObject objectRepository) {
            PageObjectJson.driver = driver;
            PageObjectJson.objectRepository = objectRepository;
        }

        /**
         *
         * Acquires an element selector by desired selector types from a given Json file
         *
         * @param elementName target element name
         * @param pageName page name that includes target element selectors
         * @param selectorTypes desired selector types
         * @return target element
         */
        public WebElement elementFromPage(String elementName, String pageName, SelectorType... selectorTypes){
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
         * @param selectorTypes desired selector types
         * @return target element list
         */
        public List<WebElement> elementsFromPage(String elementName, String pageName, SelectorType... selectorTypes){
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

        /**
         * Constructs a compound {@link org.openqa.selenium.By} object based on the provided JSON representation
         * of a web element and one or more {@link SelectorType}s. Each provided SelectorType is used to generate
         * a corresponding Selenium {@link org.openqa.selenium.By} locator for the specified type within the element's JSON.
         *
         * @param elementJson The JSON representation of the web element containing various locator information.
         * @param selectorTypes One or more SelectorType enums specifying the types of locators to be generated.
         * @return A compound By object, formed by combining individual locators based on the specified SelectorTypes.
         * @throws EnumConstantNotPresentException If an unsupported SelectorType is provided.
         * @see org.openqa.selenium.By
         * @see SelectorType
         */
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

    public static class Reflections<ObjectRepository extends PageRepository> {
        private final Class<ObjectRepository> pageRepositoryClass;

        public Reflections(Class<ObjectRepository> pageRepository) {
            this.pageRepositoryClass = pageRepository;
        }

        protected ObjectRepository getObjectRepository(){
            try {
                return pageRepositoryClass.getConstructor().newInstance();
            }
            catch (
                    InstantiationException |
                    IllegalAccessException |
                    NoSuchMethodException  |
                    InvocationTargetException e
            ) {
                throw new RuntimeException(e);
            }
        }

        /**
         * Acquires an element from a given page
         *
         * @param elementFieldName element field name
         * @param pageName name of the page instance
         * @return returns the element
         */
        public WebElement getElementFromPage(String elementFieldName, String pageName){
            Map<String, Object> pageFields;
            Object pageObject = getFields(getObjectRepository()).get(pageName);
            if (pageObject != null) pageFields = getFields(pageObject);
            else throw new PickleibException("ObjectRepository does not contain an instance of " + pageName + " object!");
            if (pageFields.get(elementFieldName) == null)
                throw new PickleibException("The " + highlighted(YELLOW, pageName) + " page object does not contain " + highlighted(YELLOW, elementFieldName) + " element!");
            return (WebElement) pageFields.get(elementFieldName);
        }

        /**
         * Acquires a list of elements from a given page
         *
         * @param elementListFieldName element list field name
         * @param pageName name of the page instance
         * @return returns the list of elements
         */
        @SuppressWarnings("unchecked")
        public List<WebElement> getElementsFromPage(String elementListFieldName, String pageName){
            Map<String, Object> pageFields;
            pageName = firstLetterDeCapped(pageName);
            Object pageObject = getFields(getObjectRepository()).get(pageName);
            if (pageObject != null) pageFields = getFields(pageObject);
            else throw new PickleibException("ObjectRepository does not contain an instance of " + pageName + " object!");
            if (pageFields.get(elementListFieldName) == null)
                throw new PickleibException("The " + highlighted(YELLOW, pageName) + " page object does not contain " + highlighted(YELLOW, elementListFieldName) + " element list!");
            return (List<WebElement>) pageFields.get(elementListFieldName);
        }

        /**
         * Acquires an element from a given component
         *
         * @param elementFieldName element field name
         * @param selectionName element text
         * @param pageName name of the page instance
         * @return returns the element
         */
        public <Component extends WebElement> WebElement getElementAmongstComponentsFromPage(
                String elementFieldName,
                String selectionName,
                String componentListName,
                String pageName){
            List<Component> componentList = getComponentsFromPage(componentListName, pageName);
            Component component = acquireNamedComponentAmongst(componentList, selectionName);
            Map<String, Object> componentFields = getFields(component);
            return (WebElement) componentFields.get(elementFieldName);
        }

        /**
         * Acquires a list of elements from a given component
         *
         * @param elementFieldName element field name
         * @param selectionName element text
         * @param pageName name of the page instance
         * @return returns the list of elements
         */
        @SuppressWarnings("unchecked")
        public <Component extends WebElement> List<WebElement> getElementsAmongstComponentsFromPage(
                String elementFieldName,
                String selectionName,
                String componentListName,
                String pageName){
            List<Component> componentList = getComponentsFromPage(componentListName, pageName);
            Component component = acquireNamedComponentAmongst(componentList, selectionName);
            Map<String, Object> componentFields = getFields(component);
            return (List<WebElement>) componentFields.get(elementFieldName);
        }

        /**
         * Acquires an element from a component amongst a list of components
         *
         * @param elementFieldName element field name
         * @param selectionName element text
         * @param pageName name of the page instance
         * @return returns the element
         */
        public <Component extends WebElement> WebElement getElementAmongstNamedComponentsFromPage(
                String elementFieldName,
                String selectionName,
                String componentListName,
                String pageName){
            List<Component> componentList = getComponentsFromPage(componentListName, pageName);
            Component component = acquireNamedComponentAmongst(componentList, selectionName);
            Map<String, Object> componentFields = getFields(component);
            return (WebElement) componentFields.get(elementFieldName);
        }

        /**
         * Acquires a list of elements from a component amongst a list of components
         *
         * @param listFieldName list field name
         * @param selectionName element text
         * @param pageName name of the page instance
         * @return returns the list of elements
         */
        @SuppressWarnings("unchecked")
        public <Component extends WebElement> List<WebElement> getElementsAmongstNamedComponentsFromPage(
                String listFieldName,
                String selectionName,
                String componentListName,
                String pageName){
            List<Component> componentList = getComponentsFromPage(componentListName, pageName);
            Component component = acquireNamedComponentAmongst(componentList, selectionName);
            Map<String, Object> componentFields = getFields(component);
            return (List<WebElement>) componentFields.get(listFieldName);
        }

        /**
         * Acquires a map of fields from a given component
         *
         * @param componentName component name
         * @param pageName name of the page instance
         * @return returns map of fields
         */
        public Map<String, Object> getComponentFieldsFromPage(String componentName, String pageName){
            Map<String, Object> pageFields;
            pageName = firstLetterDeCapped(pageName);
            Object pageObject = getFields(getObjectRepository()).get(pageName);
            if (pageObject != null) pageFields = getFields(pageObject);
            else throw new PickleibException("ObjectRepository does not contain an instance of " + pageName + " object!");
            if (pageFields.containsKey(componentName)) return getFields(pageFields.get(componentName));
            else throw new PickleibException(pageName + " does not contain " + componentName + " component!");
        }

        /**
         * Acquires a list of element from a given page
         *
         * @param componentListName component list name
         * @param pageName name of the page instance
         * @return returns the list of components
         */
        @SuppressWarnings("unchecked")
        public <Component extends WebElement> List<Component> getComponentsFromPage(String componentListName, String pageName){
            Map<String, Object> pageFields;
            Map<String, Object> componentFields;
            pageName = firstLetterDeCapped(pageName);
            Object pageObject = getFields(getObjectRepository()).get(pageName);
            if (pageObject != null) pageFields = getFields(pageObject);
            else throw new PickleibException("ObjectRepository does not contain an instance of " + pageName + " object!");
            return (List<Component>) pageFields.get(componentListName);
        }

        /**
         * Acquire a map of fields from a given component
         *
         * @param componentName component name
         * @return returns the map of fields
         */
        public Map<String, Object> getComponentFields(Object componentName){
            return  getFields(componentName);
        }

        /**
         * Acquires a web element from a page object by using Java reflections
         *
         * @param fieldName field name of the element, in the page object
         * @param inputClass instance of the page object that the WebElement resides in
         * @return corresponding WebElement from the given page object
         */
        public  <PageObject> WebElement getElement(String fieldName, Class<PageObject> inputClass){
            return (WebElement) getFieldValue(fieldName, inputClass);
        }

        /**
         * Acquires an element from a given component name
         *
         * @param elementFieldName element field name
         * @param componentName target component
         * @param pageName name of the page instance
         * @return returns the element
         */
        public WebElement getElementFromComponent(String elementFieldName, String componentName, String pageName){
            Map<String, Object> fields = getComponentFieldsFromPage(componentName, pageName);
            if (fields.containsKey(elementFieldName)) return (WebElement) fields.get(elementFieldName);
            else throw new PickleibException(componentName + " component of " + pageName + " does not contain a field called " + elementFieldName);
        }

        /**
         * Acquires an element from a given component
         *
         * @param elementFieldName element field name
         * @param component target component
         * @return returns the element
         */
        public WebElement getElementFromComponent(String elementFieldName, Object component){
            Map<String, Object> fields = getComponentFields(component);
            if (fields.containsKey(elementFieldName)) return (WebElement) fields.get(elementFieldName);
            else throw new PickleibException("The component does not contain a field called " + elementFieldName);
        }

        /**
         * Acquires a list elements from a given component name
         *
         * @param listFieldName element field
         * @param componentName target component name
         * @param pageName name of the page instance
         * @return returns the list of elements
         */
        @SuppressWarnings("unchecked")
        public List<WebElement> getElementsFromComponent(String listFieldName, String componentName, String pageName){
            Map<String, Object> fields = getComponentFieldsFromPage(componentName, pageName);
            if (fields.containsKey(listFieldName)) return (List<WebElement>) fields.get(listFieldName);
            else throw new PickleibException(componentName + " component of " + pageName + " does not contain a field called " + listFieldName);
        }

        /**
         * Acquires a list elements from a given component
         *
         * @param elementListFieldName element list field
         * @param component target component
         * @return returns the list of elements
         */
        @SuppressWarnings("unchecked")
        public List<WebElement> getElementsFromComponent(String elementListFieldName, Object component){
            Map<String, Object> fields = getComponentFields(component);
            if (fields.containsKey(elementListFieldName)) return (List<WebElement>) fields.get(elementListFieldName);
            else throw new PickleibException("The component does not contain a field called " + elementListFieldName);
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
        public  <Component extends WebElement> Component acquireExactNamedComponentAmongst(
                List<Component> items,
                String elementText,
                String targetElementFieldName
        ){
            log.info("Acquiring component called " + highlighted(BLUE, elementText));
            boolean timeout = false;
            long initialTime = System.currentTimeMillis();
            while (!timeout){
                for (Component component : items) {
                    Map<String, Object> componentFields = getFields(component);
                    WebElement element = (WebElement) componentFields.get(targetElementFieldName);
                    String text = element.getText();
                    String name = element.getAccessibleName();
                    if (text.equalsIgnoreCase(elementText) || name.equalsIgnoreCase(elementText)) return component;
                }
                if (System.currentTimeMillis() - initialTime > elementTimeout) timeout = true;
            }
            throw new NoSuchElementException("No component with text/name '" + elementText + "' could be found!");
        }
    }
}
