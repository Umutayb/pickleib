package pickleib.utilities.element.acquisition;

import context.ContextStore;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import pickleib.exceptions.PickleibException;
import pickleib.utilities.interfaces.repository.PageObjectRepository;
import utils.Printer;
import utils.StringUtilities;
import utils.arrays.lambda.Collectors;
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
    @Deprecated(since = "2.0.3")
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
    @Deprecated(since = "2.0.3")
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

    public static class Reflections<ObjectRepository extends PageObjectRepository> {
        private final Class<ObjectRepository> pageRepositoryClass;

        public Reflections(Class<ObjectRepository> pageRepository) {
            this.pageRepositoryClass = pageRepository;
        }

        protected Object getPageObject(String pageName){
            try {
                return Arrays.stream(pageRepositoryClass.getDeclaredFields())
                        .filter(field -> field.getName().equalsIgnoreCase(pageName))
                        .collect(Collectors.toSingleton())
                        .getType()
                        .getDeclaredConstructor()
                        .newInstance();
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
            pageName = StringUtilities.firstLetterDeCapped(pageName);
            Map<String, Object> pageFields;
            Object pageObject = getPageObject(pageName);
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
            Object pageObject = getPageObject(pageName);
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
            Object pageObject = getPageObject(pageName);
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
            Object pageObject = getPageObject(pageName);
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
