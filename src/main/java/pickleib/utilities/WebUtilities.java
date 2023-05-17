package pickleib.utilities;

import com.github.webdriverextensions.WebComponent;
import com.github.webdriverextensions.WebDriverExtensionFieldDecorator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import com.gargoylesoftware.htmlunit.*;
import org.openqa.selenium.*;
import java.util.*;
import context.ContextStore;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import pickleib.driver.Driver;
import pickleib.enums.Direction;
import pickleib.enums.ElementState;
import pickleib.enums.Navigation;
import pickleib.enums.PrimarySelectorType;
import pickleib.exceptions.PickleibException;
import utils.*;
import static utils.StringUtilities.Color.*;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public abstract class WebUtilities extends Driver {
    //TODO: Write a method which creates a unique css selector for elements
    //TODO: Method has to loop through the parents of the element and add tag names back to back, then add unique
    //TODO: attributes of the element at the lowest level (target element)
    //TODO: Write a method that acquires all attributes of an element (check elementObject method)

    /**
     * Used to extract snipped from texts
     */
    protected TextParser parser = new TextParser();

    /**
     * Picklieb Logger
     */
    protected Printer log = new Printer(this.getClass());
    protected StringUtilities strUtils = new StringUtilities();
    protected ReflectionUtilities reflection = new ReflectionUtilities();

    /**
     * Default Pickleib properties
     */
    protected static Properties properties = PropertyUtility.properties;

    /**
     * Duration value for methods
     */
    protected static long elementTimeout;

    /**
     * WebUtilities for frameworks that use the Pickleib driver
     *
     */
    protected WebUtilities(){
        PageFactory.initElements(new WebDriverExtensionFieldDecorator(driver), this);
        elementTimeout = Long.parseLong(properties.getProperty("element-timeout", "15000"));
    }

    /**
     * WebUtilities for frameworks that do not use the Pickleib driver
     *
     */
    protected WebUtilities(WebDriver driver){
        Driver.driver = (RemoteWebDriver) driver;
        PageFactory.initElements(new WebDriverExtensionFieldDecorator(driver), this);
        elementTimeout = Long.parseLong(properties.getProperty("element-timeout", "15000"));
    }

    /**
     * Highlights a given text with a specified color (resets to plain)
     *
     * @param color target color
     * @param text target text
     */
    protected String highlighted(StringUtilities.Color color, CharSequence text){
        StringJoiner colorFormat = new StringJoiner("", color.getValue(), RESET.getValue());
        return String.valueOf(colorFormat.add(text));
    }

    /**
     * Acquires a specified attribute of a given element
     *
     * @param element target element
     * @param attribute target attribute
     * @return returns the elements attribute
     */
    protected String getAttribute(WebElement element, String attribute){return element.getAttribute(attribute);}

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
        Object pageObject = reflection.getFields(objectRepository).get(pageName);
        if (pageObject != null) pageFields = reflection.getFields(pageObject);
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
        Object pageObject = reflection.getFields(objectRepository).get(pageName);
        if (pageObject != null) pageFields = reflection.getFields(pageObject);
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
        Map<String, Object> componentFields = reflection.getFields(component);
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
        Map<String, Object> componentFields = reflection.getFields(component);
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
        Map<String, Object> componentFields = reflection.getFields(component);
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
        Map<String, Object> componentFields = reflection.getFields(component);
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
        Map<String, Object> componentFields = reflection.getFields(component);
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
        Object pageObject = reflection.getFields(objectRepository).get(pageName);
        if (pageObject != null) componentFields = reflection.getFields(pageObject);
        else throw new PickleibException("ObjectRepository does not contain an instance of " + pageName + " object!");
        return reflection.getFields(componentFields.get(componentName));
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
        Object pageObject = reflection.getFields(objectRepository).get(pageName);
        if (pageObject != null) pageFields = reflection.getFields(pageObject);
        else throw new PickleibException("ObjectRepository does not contain an instance of " + pageName + " object!");
        return (List<WebComponent>) pageFields.get(componentListName);
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

    /**
     * Acquire a map of fields from a given component
     *
     * @param componentName component name
     * @return returns the map of fields
     */
    protected Map<String, Object> getComponentFields(Object componentName){
        return  reflection.getFields(componentName);
    }

    /**
     * Navigates to a given url
     *
     * @param url target url
     */
    protected String navigate(String url){
        try {
            log.info("Navigating to "+RESET+BLUE+url+RESET);

            if (!url.contains("http")) url = "https://"+url;

            driver.get(url);
        }
        catch (Exception gamma){
            Assert.fail("Unable to navigate to the \""+strUtils.highlighted(YELLOW, url)+"\"");
            driver.quit();
        }
        return url;
    }

    /**
     * Sets the window size
     *
     * @param width windows width
     * @param height windows height
     */
    protected void setWindowSize(Integer width, Integer height) {
        driver.manage().window().setSize(new Dimension(width,height));
    }

    /**
     * Navigates browsers in a given direction
     *
     * @param direction backwards or forwards
     */
    protected void navigateBrowser(Navigation direction){
        try {
            log.info("Navigating " + strUtils.highlighted(BLUE, direction.name()));

            switch (direction) {
                case forwards -> driver.navigate().forward();
                case backwards -> driver.navigate().back();
                default -> throw new EnumConstantNotPresentException(Navigation.class, direction.name());
            }
        }
        catch (Exception e){
            Assert.fail("Unable to navigate browser \"" + strUtils.highlighted(YELLOW, direction.name())+"\" due to: " + e);
        }
    }

    @Deprecated(since = "1.2.7", forRemoval = true)
    protected WebElement waitUntilElementIsVisible(WebElement element, long initialTime){
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));
        try {if (!element.isDisplayed()){throw new InvalidElementStateException("Element is not displayed!");}}
        catch (WebDriverException exception){
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(elementTimeout/1000));
            if (!(System.currentTimeMillis()-initialTime > elementTimeout)){
                log.warning("Recursion! (" + exception.getClass().getName() + ")");
                waitUntilElementIsVisible(element, initialTime);
            }
            else throw new NoSuchElementException("The element could not be located!");
        }
        return element;
    }
    /**
     * Scrolls through a list of elements until an element containing a given text is found
     *
     * @param list target element list
     * @param elementText target element text
     */
    protected void scrollInContainer(List<WebElement> list, String elementText){
        for (WebElement element : list) {
            scrollWithJS(element);
            if (element.getText().contains(elementText)) {
                break;
            }
        }
    }

    /**
     * Clicks an element after waiting for its state to be enabled
     *
     * @param element target element
     * @param scroll scrolls if true
     */
    protected void clickElement(WebElement element, Boolean scroll){
        long initialTime = System.currentTimeMillis();
        WebDriverException caughtException = null;
        int counter = 0;
        elementIs(element, ElementState.enabled);
        do {
            try {
                if (scroll) clickTowards(centerElement(element));
                else element.click();
                return;
            }
            catch (WebDriverException webDriverException){
                if (counter != 0 && webDriverException.getClass().getName().equals(caughtException.getClass().getName()))
                    log.warning("Iterating... (" + webDriverException.getClass().getName() + ")");

                caughtException = webDriverException;
                counter++;

                if (System.currentTimeMillis()-initialTime > elementTimeout) break;
            }
        }
        while (true);
        if (counter > 0) log.warning("Iterated " + counter + " time(s)!");
        log.warning(caughtException.getMessage());
        throw new PickleibException(caughtException);
    }

    /**
     * Click coordinates specified by the given offsets from the center of a given element
     *
     * @param element target element
     */
    protected void clickTowards(WebElement element){
        elementIs(element, ElementState.displayed);
        Actions builder = new org.openqa.selenium.interactions.Actions(driver);
        builder
                .moveToElement(element, 0, 0)
                .click()
                .build()
                .perform();
    }

    /**
     * Clicks an element after waiting for its state to be enabled
     *
     * @param element target element
     */
    protected void clickElement(WebElement element){
        clickElement(element, true);
    }

    /**
     * Clicks an element if its present (in enabled state)
     *
     * @param element target element
     * @param scroll scrolls if true
     */
    protected void clickIfPresent(WebElement element, Boolean scroll){
        try {clickElement(element, scroll);}
        catch (WebDriverException exception){log.warning(exception.getMessage());}
    }

    /**
     * Clears and fills a given input
     *
     * @param inputElement target input element
     * @param inputText input text
     * @param scroll scrolls if true
     * @param verify verifies the input text value equals to expected text if true
     */
    protected void clearFillInput(WebElement inputElement, String inputText, @NotNull Boolean scroll, Boolean verify){
        // This method clears the input field before filling it
        elementIs(inputElement, ElementState.displayed);
        if (scroll) clearInputField(centerElement(inputElement)).sendKeys(inputText);
        else clearInputField(inputElement).sendKeys(inputText);
        if (verify) Assert.assertEquals(inputText, inputElement.getAttribute("value"));
    }

    /**
     * Verifies a given element is in expected state
     *
     * @deprecated replaced by verifyElementState(element, state)
     */
    @Deprecated(since = "1.6.2")
    protected WebElement waitUntilElementIs(WebElement element, ElementState state, @NotNull Boolean strict){
        if (strict) Assert.assertTrue("Element is not in " + state.name() + " state!", elementIs(element, state));
        else elementIs(element, state);
        return element;
    }

    /**
     * Verifies a given element is in expected state
     *
     * @param element target element
     * @param state expected state
     * @return returns the element if its in expected state
     */
    protected WebElement verifyElementState(WebElement element, ElementState state){
        Assert.assertTrue("Element is not in " + state.name() + " state!", elementIs(element, state));
        log.success("Element state is verified to be: " + state.name());
        return element;
    }

    /**
     * Waits until a given element is in expected state
     *
     * @param element target element
     * @param state expected state
     * @return returns true if element is in the expected state
     */
    protected Boolean elementIs(WebElement element, @NotNull ElementState state){
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));
        long initialTime = System.currentTimeMillis();
        String caughtException = null;
        boolean timeout;
        boolean condition = false;
        boolean negativeCheck = false;
        int counter = 0;
        do {
            timeout = System.currentTimeMillis()-initialTime > elementTimeout;
            if (condition) return true;
            else if (counter > 1 && negativeCheck) return true;
            try {
                switch (state) {
                    case enabled -> {
                        negativeCheck = false;
                        condition = element.isEnabled();
                    }
                    case displayed -> {
                        negativeCheck = false;
                        condition = element.isDisplayed();
                    }
                    case selected -> {
                        negativeCheck = false;
                        condition = element.isSelected();
                    }
                    case disabled -> {
                        negativeCheck = true;
                        condition = !element.isEnabled();
                    }
                    case unselected -> {
                        negativeCheck = true;
                        condition = !element.isSelected();
                    }
                    case absent -> {
                        negativeCheck = true;
                        condition = !element.isDisplayed();
                    }
                    default -> throw new EnumConstantNotPresentException(ElementState.class, state.name());
                }
            }
            catch (WebDriverException webDriverException){
                if (counter == 0) {
                    log.warning("Iterating... (" + webDriverException.getClass().getName() + ")");
                    caughtException = webDriverException.getClass().getName();
                }
                else if (!webDriverException.getClass().getName().equals(caughtException)){
                    log.warning("Iterating... (" + webDriverException.getClass().getName() + ")");
                    caughtException = webDriverException.getClass().getName();
                }
                counter++;
            }
        }
        while (!timeout);
        if (counter > 0) log.warning("Iterated " + counter + " time(s)!");
        return false;
    }

    /**
     * Hovers cursor over of a given element
     *
     * @deprecated replaced by hoverOver(element)
     */
    @Deprecated(since = "1.2.7")
    protected WebElement hoverOver(WebElement element, Long initialTime){
        if (System.currentTimeMillis() - initialTime > elementTimeout) return null;
        centerElement(element);
        Actions actions = new Actions(driver);
        try {actions.moveToElement(element).build().perform();}
        catch (WebDriverException ignored) {hoverOver(element, initialTime);}
        return element;
    }

    /**
     * Hovers cursor over of a given element
     *
     * @param element target element
     * @return returns the selected element
     */
    protected WebElement hoverOver(WebElement element){
        long initialTime = System.currentTimeMillis();
        Actions actions = new Actions(driver);
        String caughtException = null;
        boolean timeout;
        int counter = 0;
        do {
            try {
                centerElement(element);
                actions.moveToElement(element).build().perform();
                break;
            }
            catch (WebDriverException webDriverException){
                if (counter == 0) {
                    log.warning("Iterating... (" + webDriverException.getClass().getName() + ")");
                    caughtException = webDriverException.getClass().getName();
                    counter++;
                }
                else if (!webDriverException.getClass().getName().equals(caughtException)){
                    log.warning("Iterating... (" + webDriverException.getClass().getName() + ")");
                    caughtException = webDriverException.getClass().getName();
                    counter++;
                }
                timeout = System.currentTimeMillis()-initialTime > elementTimeout;
            }
        }
        while (timeout);
        return element;
    }

    /**
     * @deprecated redundant method
     */
    @Deprecated(since = "1.6.2", forRemoval = true)
    protected void loopAndClick(List<WebElement> list, String buttonName, Boolean scroll){
        clickElement(acquireNamedElementAmongst(list,buttonName), scroll);
    }

    /**
     * Acquire a component amongst a list of components by its name
     *
     * @param items list of components
     * @param selectionName component name
     * @return returns the selected component
     */
    protected  <T> T acquireNamedComponentAmongst(List<T> items, String selectionName){
        log.info("Acquiring component called " + strUtils.highlighted(BLUE, selectionName));
        boolean timeout = false;
        long initialTime = System.currentTimeMillis();
        while (!timeout){
            for (T selection : items) {
                String text = ((WebElement) selection).getText();
                if (text.equalsIgnoreCase(selectionName) || text.contains(selectionName)) return selection;
            }
            if (System.currentTimeMillis() - initialTime > elementTimeout) timeout = true;
        }
        throw new NoSuchElementException("No component with text/name '" + selectionName + "' could be found!");
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
                Map<String, Object> componentFields = reflection.getFields(component);
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
                Map<String, Object> componentFields = reflection.getFields(component);
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
                Map<String, Object> componentFields = reflection.getFields(component);
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
     * Acquire listed element by its name
     *
     * @param items list that includes target element
     * @param selectionName element name
     * @return returns the selected element
     */
    protected WebElement acquireNamedElementAmongst(List<WebElement> items, String selectionName){
        log.info("Acquiring element called " + strUtils.highlighted(BLUE, selectionName));
        boolean timeout = false;
        long initialTime = System.currentTimeMillis();
        while (!timeout){
            for (WebElement selection : items) {
                String name = selection.getAccessibleName();
                String text = selection.getText();
                if (    name.equalsIgnoreCase(selectionName) ||
                        name.contains(selectionName)         ||
                        text.equalsIgnoreCase(selectionName) ||
                        text.contains(selectionName)
                ) return selection;
            }
            if (System.currentTimeMillis() - initialTime > elementTimeout) timeout = true;
        }
        throw new NoSuchElementException("No element with text/name '" + selectionName + "' could be found!");
    }

    /**
     * @deprecated replace by acquireNamedElementAmongst(items, String selectionName)
     */
    @Deprecated(since = "1.2.7", forRemoval = true)
    protected WebElement acquireNamedElementAmongst(@NotNull List<WebElement> items, String selectionName, long initialTime){
        log.info("Acquiring element called " + strUtils.highlighted(BLUE, selectionName));
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));
        try {
            for (WebElement selection : items) {
                String name = selection.getAccessibleName();
                String text = selection.getText();
                if (
                        name.equalsIgnoreCase(selectionName) ||
                                name.contains(selectionName)         ||
                                text.equalsIgnoreCase(selectionName) ||
                                text.contains(selectionName)
                ) return selection;
            }
            throw new NoSuchElementException("No element with text/name '" + selectionName + "' could be found!");
        }
        catch (WebDriverException exception){
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(elementTimeout/1000));
            if (!(System.currentTimeMillis()-initialTime > elementTimeout)) {
                log.warning("Recursion! (" + exception.getClass().getName() + ")");
                return acquireNamedElementAmongst(items, selectionName, initialTime);
            }
            throw exception;
        }
    }

    /**
     * @deprecated replace by acquireNamedComponentAmongst(items, String selectionName)
     */
    @Deprecated(since = "1.2.7", forRemoval = true)
    protected  <T> T acquireNamedComponentAmongst(@NotNull List<T> items, String selectionName, long initialTime){
        log.info("Acquiring element called " + strUtils.highlighted(BLUE, selectionName));
        try {
            for (T selection : items) {
                String text = ((WebElement) selection).getText();
                if (text.equalsIgnoreCase(selectionName) || text.contains(selectionName)) return selection;
            }
            throw new NoSuchElementException("No component with text/name '" + selectionName + "' could be found!");
        }
        catch (WebDriverException exception){
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(elementTimeout/1000));
            if (!(System.currentTimeMillis()-initialTime > elementTimeout)) {
                log.warning("Recursion! (" + exception.getClass().getName() + ")");
                return acquireNamedComponentAmongst(items, selectionName, initialTime);
            }
            throw exception;
        }
    }

    /**
     * Acquire listed element by its attribute
     *
     * @param items list that includes target element
     * @param attributeName attribute name
     * @param attributeValue attribute value
     * @return returns the selected element
     */
    protected WebElement acquireElementUsingAttributeAmongst(List<WebElement> items, String attributeName, String attributeValue){
        log.info("Acquiring element called " + strUtils.markup(BLUE, attributeValue) + " using its " + strUtils.markup(BLUE, attributeName) + " attribute");
        boolean condition = true;
        long initialTime = System.currentTimeMillis();
        while (condition){
            for (WebElement selection : items) {
                String attribute = selection.getAttribute(attributeName);
                if (attribute != null && (attribute.equalsIgnoreCase(attributeValue) || attribute.contains(attributeValue))) return selection;
            }
            if (System.currentTimeMillis() - initialTime > elementTimeout) condition = false;
        }
        throw new NoSuchElementException("No element with the attributes '" + attributeName + " : " + attributeValue + "' could be found!");
    }

    /**
     * @deprecated replace by acquireElementUsingAttributeAmongst(items, String attributeName, String attributeValue)
     */
    @Deprecated(since = "1.2.7", forRemoval = true)
    protected WebElement acquireElementUsingAttributeAmongst(@NotNull List<WebElement> elements, String attributeName, String attributeValue, long initialTime){
        log.info("Acquiring element called " + strUtils.markup(BLUE, attributeValue) + " using its " + strUtils.markup(BLUE, attributeName) + " attribute");
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));
        try {
            for (WebElement selection : elements) {
                String attribute = selection.getAttribute(attributeName);
                if (attribute.equalsIgnoreCase(attributeValue) || attribute.contains(attributeValue)) return selection;
            }
            throw new NoSuchElementException("No element with the attributes '" + attributeName + " : " + attributeValue + "' could be found!");
        }
        catch (WebDriverException exception){
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(elementTimeout/1000));
            if (!(System.currentTimeMillis()-initialTime > elementTimeout)) {
                log.warning("Recursion! (" + exception.getClass().getName() + ")");
                return acquireElementUsingAttributeAmongst(elements, attributeName, attributeValue, initialTime);
            }
            throw exception;
        }
    }

    /**
     * Switches driver focus by using tab handle
     *
     * @param handle target tab/window
     */
    protected String switchWindowByHandle(@Nullable String handle){
        log.info("Switching to the next tab");
        String parentWindowHandle = driver.getWindowHandle();
        if (handle == null)
            for (String windowHandle: driver.getWindowHandles()) {
                if (!windowHandle.equalsIgnoreCase(parentWindowHandle))
                    driver = (RemoteWebDriver) driver.switchTo().window((windowHandle));
            }
        else driver = (RemoteWebDriver) driver.switchTo().window(handle);
        return parentWindowHandle;
    }

    /**
     * Switches driver focus by using tab index
     *
     * @param tabIndex target tab/window
     */
    protected String switchWindowByIndex(Integer tabIndex){
        log.info("Switching the tab with the window index: " + tabIndex);
        String parentWindowHandle = driver.getWindowHandle();
        List<String> handles = new ArrayList<>(driver.getWindowHandles());
        String handle = handles.get(tabIndex);
        driver = (RemoteWebDriver) driver.switchTo().window(handle);
        return parentWindowHandle;
    }

    /**
     * Clicks an element acquired by text
     *
     * @param buttonText target element text
     * @param scroll scrolls if true
     */
    protected void clickButtonWithText(String buttonText, Boolean scroll){clickElement(getElementByText(buttonText), scroll);}

    /**
     * Clears an input element
     *
     * @param element target element
     */
    protected WebElement clearInputField(@NotNull WebElement element){
        int textLength = element.getAttribute("value").length();
        for(int i = 0; i < textLength; i++){element.sendKeys(Keys.BACK_SPACE);}
        return element;
    }

    /**
     * Acquires an element by its text
     *
     * @param elementText target element text
     */
    protected WebElement getElementByText(String elementText){
        try {
            return driver.findElement(By.xpath("//*[text()='" +elementText+ "']"));
        }
        catch (ElementNotFoundException | NoSuchElementException exception){
            throw new NoSuchElementException(GRAY+exception.getMessage()+RESET);
        }
    }

    /**
     * Acquires an element that contains a certain text
     *
     * @param elementText target element text
     */
    protected WebElement getElementContainingText(String elementText){
        try {
            return driver.findElement(By.xpath("//*[contains(text(), '" +elementText+ "')]"));
        }
        catch (ElementNotFoundException | NoSuchElementException exception){
            throw new NoSuchElementException(GRAY+exception.getMessage()+RESET);
        }
    }

    /**
     * Drags and drops a given element on top of another element
     *
     * @param element element that drags
     * @param destinationElement target element
     */
    protected void dragDropToAction(WebElement element, WebElement destinationElement){

        centerElement(element);

        Actions action = new Actions(driver);
        action.moveToElement(element)
                .clickAndHold(element)
                .moveToElement(destinationElement)
                .release()
                .build()
                .perform();
        waitFor(0.5);
    }

    /**
     * Drags a given element to coordinates specified by offsets from the center of the element
     *
     * @param element target element
     * @param xOffset x offset from the center of the element
     * @param yOffset y offset from the center of the element
     */
    //This method performs click, hold, dragAndDropBy action on at a certain offset
    protected void dragDropByAction(WebElement element, int xOffset, int yOffset){

        centerElement(element);

        Actions action = new Actions(driver);
        action.moveToElement(element)
                .clickAndHold(element)
                .dragAndDropBy(element, xOffset, yOffset)
                .build()
                .perform();
        waitFor(0.5);
    }

    /**
     * Drags a given element to coordinates specified by offsets from the center of the element
     * Uses moveToElement()
     *
     * @param element target element
     * @param xOffset x offset from the center of the element
     * @param yOffset y offset from the center of the element
     */
    protected void dragDropAction(WebElement element, int xOffset, int yOffset){

        centerElement(element);

        Actions action = new Actions(driver);
        action.moveToElement(element)
                .clickAndHold(element)
                .moveToElement(element,xOffset,yOffset)
                .release()
                .build()
                .perform();
        waitFor(0.5);
    }

    /**
     * Refreshes the current page
     *
     */
    protected void refreshThePage(){
        driver.navigate().refresh();}

    /**
     * Click coordinates specified by the given offsets from the center of a given element
     *
     * @param element target element
     * @param xOffset x offset from the center of the element
     * @param yOffset y offset from the center of the element
     */
    @SuppressWarnings("SameParameterValue")
    protected void clickAtAnOffset(WebElement element, int xOffset, int yOffset){
        Actions builder = new org.openqa.selenium.interactions.Actions(driver);
        builder
                .moveToElement(element, xOffset, yOffset)
                .click()
                .build()
                .perform();
    }

    /**
     * Switches to present alert
     *
     * @return returns the alert
     */
    protected Alert getAlert(){return driver.switchTo().alert();}

    /**
     * Uploads a given file
     *
     * @param fileUploadInput upload element
     * @param directory absolute file directory (excluding the file name)
     * @param fileName file name (including file extension)
     */
    protected void uploadFile(@NotNull WebElement fileUploadInput, String directory, String fileName){fileUploadInput.sendKeys(directory+"/"+fileName);}

    /**
     * Combines the given keys
     *
     * @param keys key inputs
     */
    protected String combineKeys(Keys... keys) {
        return Keys.chord(keys);
    }

    /**
     * Waits for a certain while
     *
     * @param seconds duration as a double
     */
    //This method makes the thread wait for a certain while
    protected void waitFor(double seconds){
        if (seconds > 1) log.info("Waiting for " + strUtils.markup(BLUE,"" + seconds) + " seconds");
        try {TimeUnit.MICROSECONDS.wait(Double.valueOf( seconds * 1000).longValue());}
        catch (InterruptedException exception){Assert.fail(GRAY+exception.getLocalizedMessage()+RESET);}
    }

    /**
     * Scrolls an element to the center of the view
     *
     * @param element target element
     * @return returns the targeted element
     */
    //This method scrolls an element to the center of the view
    protected WebElement centerElement(WebElement element){
        String scrollScript = "var viewPortHeight = Math.max(document.documentElement.clientHeight, window.innerHeight || 0);"
                + "var elementTop = arguments[0].getBoundingClientRect().top;"
                + "window.scrollBy(0, elementTop-(viewPortHeight/2));";

        ((JavascriptExecutor) driver).executeScript(scrollScript, element);

        waitFor(0.3);
        return element;
    }

    /**
     * Scroll in a given direction
     *
     * @param direction target direction (UP or DOWN)
     */
    protected void scroll(@NotNull Direction direction){
        log.info("Scrolling " + strUtils.highlighted(BLUE, direction.name().toLowerCase()));
        String script = switch (direction) {
            case up -> "window.scrollBy(0,-document.body.scrollHeight)";
            case down -> "window.scrollBy(0,document.body.scrollHeight)";
        };
        ((JavascriptExecutor) driver).executeScript(script);
    }

    /**
     * Verifies the current url
     *
     * @param url target url
     * @deprecated replaced by verifyCurrentUrl(url)
     */
    @Deprecated(since = "1.6.2")
    protected void verifyUrl(String url){
        Assert.assertTrue(driver.getCurrentUrl().contains(url));
    }

    /**
     * Verifies the current url contains the given url
     *
     * @param url target url
     */
    protected void verifyUrlContains(String url){
        Assert.assertTrue(driver.getCurrentUrl().contains(url));
    }

    /**
     * Verifies the current url equals to given url
     *
     * @param url target url
     */
    protected void verifyCurrentUrl(String url){
        Assert.assertTrue(driver.getCurrentUrl().equalsIgnoreCase(url));
    }

    /**
     * Verifies the given page title
     *
     * @param pageTitle target page
     */
    //This method verifies the page title
    protected void verifyPageTitle(String pageTitle){
        Assert.assertTrue(driver.getTitle().contains(pageTitle));
    }

    /**
     * Transform a given element to an object using javascript
     *
     * @param element target element
     * @return returns an object with the attributes of a given element
     */
    //This method returns all the attributes of an element as an object
    protected Object getElementObject(WebElement element){
        return ((JavascriptExecutor) driver).executeScript("var items = {}; for (index = 0;" +
                        " index < arguments[0].attributes.length; ++index) " +
                        "{ items[arguments[0].attributes[index].name] = arguments[0].attributes[index].value }; return items;",
                element
        );
    }

    /**
     * Prints all the attributes of a given element
     *
     * @param element target element
     */
    //This method prints all the attributes of a given element
    protected void printElementAttributes(WebElement element){//TODO: update this
        //JSONObject attributeJSON = new JSONObject(strUtils.str2Map(getElementObject(element).toString()));
        //for (Object attribute : attributeJSON.keySet()) log.info();(attribute +" : "+ attributeJSON.get(attribute));
    }

    /**
     * Gets the parent class from a child element using a selector class
     *
     * @param childElement element that generates the parent class
     * @param current empty string (at the beginning)
     * @param parentSelectorClass selector class for selecting the parent elements
     * @return returns the targeted parent element
     */
    protected WebElement getParentByClass(WebElement childElement, String current, String parentSelectorClass) {

        if (current == null) {current = "";}

        String childTag = childElement.getTagName();

        if (childElement.getAttribute("class").contains(parentSelectorClass)) return childElement;

        WebElement parentElement = childElement.findElement(By.xpath(".."));
        List<WebElement> childrenElements = parentElement.findElements(By.xpath("*"));

        int count = 0;
        for (WebElement childrenElement : childrenElements) {
            String childrenElementTag = childrenElement.getTagName();
            if (childTag.equals(childrenElementTag)) count++;
            if (childElement.equals(childrenElement)) {
                return getParentByClass(parentElement, "/" + childTag + "[" + count + "]" + current, parentSelectorClass);
            }
        }
        return null;
    }

    /**
     * Generate a xPath for a given element
     *
     * @param childElement web element gets generated a xPath from
     * @param current empty string (at the beginning)
     * @return returns generated xPath
     */
    protected String generateXPath(@NotNull WebElement childElement, String current) {
        String childTag = childElement.getTagName();
        if (childTag.equals("html")) {return "/html[1]" + current;}
        WebElement parentElement = childElement.findElement(By.xpath(".."));
        List<WebElement> childrenElements = parentElement.findElements(By.xpath("*"));
        int count = 0;
        for (WebElement childrenElement : childrenElements) {
            String childrenElementTag = childrenElement.getTagName();
            if (childTag.equals(childrenElementTag)) count++;
            if (childElement.equals(childrenElement)) {
                return generateXPath(parentElement, "/" + childTag + "[" + count + "]" + current);
            }
        }
        return null;
    }

    /**
     * @deprecated replaced by elementIs(WebElement element, @NotNull ElementState state)
     */
    @Deprecated (since = "1.6.2", forRemoval = true)
    protected List<WebElement> verifyAbsenceOfElementLocatedBy(@NotNull PrimarySelectorType locatorType, String locator, long startTime){

        List<WebElement> elements = switch (locatorType) {
            case xpath -> driver.findElements(By.xpath(locator));
            case css -> driver.findElements(By.cssSelector(locator));
        };

        if ((System.currentTimeMillis() - startTime) > elementTimeout){
            Assert.fail(GRAY+"An element was located unexpectedly"+RESET);
            return elements;
        }
        if (elements.size() > 0){return verifyAbsenceOfElementLocatedBy(locatorType, locator, startTime);}
        else return null;
    }

    /**
     * @deprecated replaced by elementIs(element, state)
     */
    @Deprecated(since = "1.2.7", forRemoval = true)
    protected void waitUntilElementIsNoLongerPresent(WebElement element, long startTime){
        try {
            WebDriver subDriver = driver;
            subDriver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));
            List<WebElement> elementPresence = driver.findElements(By.xpath(generateXPath(element,"")));
            while (elementPresence.size()>0){
                if ((System.currentTimeMillis() - startTime) > elementTimeout)
                    throw new TimeoutException(GRAY+"Element was still present after " + elementTimeout /1000 + " seconds."+RESET);
                elementPresence = subDriver.findElements(By.xpath(generateXPath(element,"")));
            }
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(elementTimeout/1000));
        }
        catch (WebDriverException exception){
            if (System.currentTimeMillis()-startTime<elementTimeout) waitUntilElementIsNoLongerPresent(element, startTime);
            else throw new TimeoutException(GRAY+"Element was still present after " + elementTimeout /1000 + " seconds."+RESET);
        }
        catch (IllegalArgumentException ignored){
            log.success("The element is no longer present!");
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(elementTimeout/1000));
        }
    }

    /**
     * @deprecated replaced by elementIs(element, state)
     */
    @Deprecated(since = "1.2.7", forRemoval = true)
    protected WebElement waitUntilElementIsInvisible(WebElement element, long startTime) {
        if ((System.currentTimeMillis() - startTime) > elementTimeout) return element;
        try {
            wait.until(ExpectedConditions.invisibilityOf(element));
            return null;
        }
        catch (TimeoutException e) {return waitUntilElementIsInvisible(element, startTime);}
    }

    /**
     * @deprecated replaced by elementIs(element, state)
     */
    @Deprecated(since = "1.2.7", forRemoval = true)
    protected WebElement waitUntilElementIsClickable(WebElement element, long initialTime){
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));
        if (System.currentTimeMillis()-initialTime > elementTimeout){
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(elementTimeout/1000));
            return null;
        }
        try {if (!element.isEnabled()){waitUntilElementIsClickable(element, initialTime);}}
        catch (WebDriverException exception){
            return waitUntilElementIsClickable(element, initialTime);
        }
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(elementTimeout/1000));
        return element;
    }

    /**
     * Click element into view by using javascript
     *
     * @param webElement element that gets clicked
     */
    protected void clickWithJS(WebElement webElement) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", webElement);
    }

    /**
     * Scrolls element into view by using javascript
     *
     * @param webElement element that gets scrolled into the view
     */
    protected void scrollWithJS(WebElement webElement) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView();", webElement);
    }

    /**
     * @deprecated replaced by elementIs(element, state)
     */
    @Deprecated(since = "1.2.7", forRemoval = true)
    protected boolean elementIsDisplayed(WebElement element, long startTime) {
        if ((System.currentTimeMillis() - startTime) > 10000) return false;
        try {return element.isDisplayed();}
        catch (Exception e) {
            log.info(e.getLocalizedMessage());
            return elementIsDisplayed(element, startTime);
        }
    }

    /**
     * Acquires web element from a page object by using Java reflections
     *
     * @param fieldName field name of the element, in the page object
     * @param inputClass instance of the page object that the WebElement resides in
     * @return corresponding WebElement from the given page object
     */
    protected  <T> WebElement getElement(String fieldName, Class<T> inputClass){
        return (WebElement) reflection.getFieldValue(fieldName, inputClass);
    }

    /**
     * Custom context checker to re-format an input text or acquire context data
     *
     * @param input string that is to be context checked
     * @return value depending on the context (could be from ContextStore, Properties, Random etc)
     * @deprecated now moved to io.github.umutayb/Utilities
     */
    @Deprecated(since = "1.7.0")
    protected String contextCheck(@NotNull String input){
        TextParser parser = new TextParser();
        if (input.contains("CONTEXT-"))
            input = ContextStore.get(parser.parse("CONTEXT-", null, input)).toString();
        else if (input.contains("RANDOM-")){
            boolean useLetters = input.contains("LETTER");
            boolean useNumbers = input.contains("NUMBER");
            String keyword = "";
            if (input.contains("KEYWORD")) keyword = parser.parse("-K=", "-", input);
            int length = Integer.parseInt(parser.parse("-L=", null, input));
            input = strUtils.generateRandomString(keyword, length, useLetters, useNumbers);
        }
        else if (input.contains("UPLOAD-")){
            String relativePath = parser.parse("UPLOAD-", null, input);
            input = new FileUtilities().getAbsolutePath(relativePath);
        }
        else if (input.contains("PROPERTY-")){
            String propertyName = parser.parse("TEST_PROPERTY-", null, input);
            input = properties.getProperty(propertyName, "NULL");
        }
        return input;
    }

    /**
     * Checks if an event was fired
     * Creates a custom script to listen for an event by generating a unique event key and catches this key in the console
     * Ex: "dataLayerObject.listen(eventName, function(){console.warn(eventKey)});"
     *
     * @param eventName event name of the event that is expected to be fired
     * @param listenerScript script for calling the listener, ex: "dataLayerObject.listen( eventName );"
     * @return true if the specified event was fired.
     */
    protected boolean isEventFired(String eventName, String listenerScript){
        log.info("Listening to '" + eventName + "' event");
        String eventKey = strUtils.generateRandomString(eventName + "#", 6, false, true);
        listenerScript = listenerScript.replace(eventName, "'" + eventName + "', function(){console.warn('" + eventKey +"')}");
        executeScript(listenerScript);
        LogEntries logs = driver.manage().logs().get(LogType.BROWSER);
        for (LogEntry entry: logs.getAll())
            if (entry.toString().contains(eventKey)) {
                log.success("'" + eventName + "' event is fired!");
                return true;
            }
        log.warning(eventName + " event is not fired!");
        return false;
    }

    /**
     * Checks if an event was fired
     *
     * @param eventKey key that is meant to be caught from the console in case the event fires
     * @param listenerScript script for calling the listener, ex: "dataLayerObject.listen('page.info', function(){console.warn(eventKey)});"
     * @return true if the specified event was fired.
     */
    protected boolean isEventFiredByScript(String eventKey, String listenerScript){
        log.info("Listening to '" + strUtils.markup(BLUE, eventKey) + "' event");
        executeScript(listenerScript);
        LogEntries logs = driver.manage().logs().get(LogType.BROWSER);
        for (LogEntry entry: logs.getAll()) if (entry.toString().contains(eventKey)) return true;
        return false;
    }

    /**
     * Executes a JS script and returns the responding object
     *
     * @param script script that is to be executed
     * @return object if the scripts yields one
     */
    protected Object executeScript(String script){
        log.info("Executing script: " + strUtils.highlighted(BLUE, script));
        return ((JavascriptExecutor) driver).executeScript(script);
    }
}
