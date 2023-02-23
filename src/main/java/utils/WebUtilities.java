package utils;

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
import org.json.simple.JSONObject;
import static resources.Colors.*;
import static utils.WebUtilities.Color.BLUE;
import org.openqa.selenium.*;
import java.util.*;
import context.ContextStore;
import utils.driver.Driver;
import java.time.Duration;
import org.junit.Assert;
import resources.Colors;
import exceptions.PickleibException;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public abstract class WebUtilities extends Driver {
    //TODO: Write a method which creates a unique css selector for elements
    //TODO: Method has to loop through the parents of the element and add tag names back to back, then add unique
    //TODO: attributes of the element at the lowest level (target element)
    //TODO: Write a method that acquires all attributes of an element

    public TextParser parser = new TextParser();
    public Printer log = new Printer(this.getClass());
    public StringUtilities strUtils = new StringUtilities();
    public ObjectUtilities objectUtils = new ObjectUtilities();

    public enum Color {CYAN, RED, GREEN, YELLOW, PURPLE, GRAY, BLUE}
    public enum Navigation {BACKWARDS, FORWARDS}
    public enum ElementState {ENABLED, DISPLAYED, SELECTED, DISABLED, UNSELECTED, ABSENT}
    public enum Direction {UP, DOWN}
    public enum Locator {XPATH, CSS}

    public static Properties properties = PropertyUtility.properties;

    public static long elementTimeout;

    public WebUtilities(){
        PageFactory.initElements(new WebDriverExtensionFieldDecorator(driver), this);
        elementTimeout = Long.parseLong(properties.getProperty("element-timeout", "15000"));
    }

    public WebUtilities(WebDriver driver){
        Driver.driver = (RemoteWebDriver) driver;
        PageFactory.initElements(new WebDriverExtensionFieldDecorator(driver), this);
        elementTimeout = Long.parseLong(properties.getProperty("element-timeout", "15000"));
    }

    public String getAttribute(WebElement element, String attribute){return element.getAttribute(attribute);}

    public WebElement getElementFromPage(String elementFieldName, String pageName, Object objectRepository){
        Map<String, Object> pageFields;
        Object pageObject = objectUtils.getFields(objectRepository).get(pageName);
        if (pageObject != null) pageFields = objectUtils.getFields(pageObject);
        else throw new PickleibException("ObjectRepository does not contain an instance of " + pageName + " object!");
        return (WebElement) pageFields.get(elementFieldName);
    }

    @SuppressWarnings("unchecked")
    public List<WebElement> getElementsFromPage(String elementFieldName, String pageName, Object objectRepository){
        Map<String, Object> pageFields;
        Object pageObject = objectUtils.getFields(objectRepository).get(pageName);
        if (pageObject != null) pageFields = objectUtils.getFields(pageObject);
        else throw new PickleibException("ObjectRepository does not contain an instance of " + pageName + " object!");
        return (List<WebElement>) pageFields.get(elementFieldName);
    }

    public WebElement getElementAmongstComponentsFromPage(
            String elementFieldName,
            String selectionName,
            String componentListName,
            String pageName,
            Object objectRepository){
        List<WebComponent> componentList = getComponentsFromPage(componentListName, pageName, objectRepository);
        WebComponent component = acquireNamedComponentAmongst(componentList, selectionName);
        Map<String, Object> componentFields = objectUtils.getFields(component);
        return (WebElement) componentFields.get(elementFieldName);
    }

    @SuppressWarnings("unchecked")
    public List<WebElement> getElementsAmongstComponentsFromPage(
            String elementFieldName,
            String selectionName,
            String componentListName,
            String pageName,
            Object objectRepository){
        List<WebComponent> componentList = getComponentsFromPage(componentListName, pageName, objectRepository);
        WebComponent component = acquireNamedComponentAmongst(componentList, selectionName);
        Map<String, Object> componentFields = objectUtils.getFields(component);
        return (List<WebElement>) componentFields.get(elementFieldName);
    }


    public WebElement getElementAmongstNamedComponentsFromPage(
            String elementFieldName,
            String selectionName,
            String componentListName,
            String pageName,
            Object objectRepository){
        List<WebComponent> componentList = getComponentsFromPage(componentListName, pageName, objectRepository);
        WebComponent component = acquireNamedComponentAmongst(componentList, selectionName);
        Map<String, Object> componentFields = objectUtils.getFields(component);
        return (WebElement) componentFields.get(elementFieldName);
    }

    @SuppressWarnings("unchecked")
    public List<WebElement> getElementsAmongstNamedComponentsFromPage(
            String listFieldName,
            String selectionName,
            String componentListName,
            String pageName,
            Object objectRepository){
        List<WebComponent> componentList = getComponentsFromPage(componentListName, pageName, objectRepository);
        WebComponent component = acquireNamedComponentAmongst(componentList, selectionName);
        Map<String, Object> componentFields = objectUtils.getFields(component);
        return (List<WebElement>) componentFields.get(listFieldName);
    }

    @Deprecated(since = "1.6.2")
    public WebElement getElementAmongstExactComponentsFromPage(
            String elementFieldName,
            String elementIdentifier,
            String componentListName,
            String pageName,
            Object objectRepository){
        WebComponent component = acquireExactNamedComponentAmongst(elementIdentifier, elementFieldName, componentListName, pageName, objectRepository);
        Map<String, Object> componentFields = objectUtils.getFields(component);
        return (WebElement) componentFields.get(elementFieldName);
    }

    public Map<String, Object> getComponentFieldsFromPage(String componentName, String pageName, Object objectRepository){
        Map<String, Object> componentFields;
        Object pageObject = objectUtils.getFields(objectRepository).get(pageName);
        if (pageObject != null) componentFields = objectUtils.getFields(pageObject);
        else throw new PickleibException("ObjectRepository does not contain an instance of " + pageName + " object!");
        return objectUtils.getFields(componentFields.get(componentName));
    }

    @SuppressWarnings("unchecked")
    public List<WebComponent> getComponentsFromPage(String componentListName, String pageName, Object objectRepository){
        Map<String, Object> pageFields;
        Map<String, Object> componentFields;
        Object pageObject = objectUtils.getFields(objectRepository).get(pageName);
        if (pageObject != null) pageFields = objectUtils.getFields(pageObject);
        else throw new PickleibException("ObjectRepository does not contain an instance of " + pageName + " object!");
        return (List<WebComponent>) pageFields.get(componentListName);
    }

    public WebElement getElementFromComponent(String elementFieldName, String componentName, String pageName, Object objectRepository){
        return (WebElement) getComponentFieldsFromPage(componentName, pageName, objectRepository).get(elementFieldName);
    }


    public WebElement getElementFromComponent(String elementFieldName, Object component){
        return (WebElement) getComponentFields(component).get(elementFieldName);
    }

    @SuppressWarnings("unchecked")
    public List<WebElement> getElementsFromComponent(String elementFieldName, String componentName, String pageName, Object objectRepository){
        return (List<WebElement>) getComponentFieldsFromPage(componentName, pageName, objectRepository).get(elementFieldName);
    }

    @SuppressWarnings("unchecked")
    public List<WebElement> getElementsFromComponent(String elementListFieldName, Object component){
        return (List<WebElement>) getComponentFields(component).get(elementListFieldName);
    }

    public Map<String, Object> getComponentFields(Object componentName){
        return  objectUtils.getFields(componentName);
    }

    public String navigate(String url){
        try {
            log.new Info("Navigating to "+RESET+BLUE+url+RESET);

            if (!url.contains("http")) url = "https://"+url;

            driver.get(url);
        }
        catch (Exception gamma){
            Assert.fail("Unable to navigate to the \""+highlighted(Color.YELLOW, url)+"\"");
            driver.quit();
        }
        return url;
    }

    public void setWindowSize(Integer width, Integer height) {
        driver.manage().window().setSize(new Dimension(width,height));
    }

    public String highlighted(Color color, String text){return (objectUtils.getFieldValue(color.name(), Colors.class) + text + RESET);}

    public void navigateBrowser(Navigation direction){
        try {
            log.new Info("Navigating "+highlighted(Color.BLUE, direction.name()));

            switch (direction) {
                case FORWARDS -> driver.navigate().forward();
                case BACKWARDS -> driver.navigate().back();
                default -> throw new EnumConstantNotPresentException(Navigation.class, direction.name());
            }
        }
        catch (Exception e){
            Assert.fail("Unable to navigate browser \""+highlighted(Color.YELLOW, direction.name())+"\" due to: " + e);
        }
    }

    @Deprecated(since = "1.2.7")
    public WebElement waitUntilElementIsVisible(WebElement element, long initialTime){
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));
        try {if (!element.isDisplayed()){throw new InvalidElementStateException("Element is not displayed!");}}
        catch (WebDriverException exception){
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(elementTimeout/1000));
            if (!(System.currentTimeMillis()-initialTime > elementTimeout)){
                log.new Warning("Recursion! (" + exception.getClass().getName() + ")");
                waitUntilElementIsVisible(element, initialTime);
            }
            else throw new NoSuchElementException("The element could not be located!");
        }
        return element;
    }

    //This method clicks an element after waiting its state to be enabled and scrolling it to the center of the view
    public void clickElement(WebElement element, Boolean scroll){
        long initialTime = System.currentTimeMillis();
        WebDriverException caughtException = null;
        boolean timeout;
        int counter = 0;
        waitUntilElementIs(element, ElementState.ENABLED, false);
        do {
            timeout = System.currentTimeMillis()-initialTime > elementTimeout;
            try {
                if (scroll) centerElement(element).click();
                else element.click();
                return;
            }
            catch (WebDriverException webDriverException){
                if (counter == 0) {
                    log.new Warning("Iterating... (" + webDriverException.getClass().getName() + ")");
                    caughtException = webDriverException;
                }
                else if (!webDriverException.getClass().getName().equals(caughtException.getClass().getName())){
                    log.new Warning("Iterating... (" + webDriverException.getClass().getName() + ")");
                    caughtException = webDriverException;
                }
                counter++;
            }
        }
        while (!timeout);
        if (counter > 0) log.new Warning("Iterated " + counter + " time(s)!");
        log.new Warning(caughtException.getMessage());
        throw new PickleibException(caughtException);
    }

    //This method clicks an element after waiting its state to be enabled and scrolling it to the center of the view
    public void clickIfPresent(WebElement element, Boolean scroll){
        try {
            long initialTime = System.currentTimeMillis();
            WebDriverException caughtException = null;
            boolean timeout;
            int counter = 0;
            waitUntilElementIs(element, ElementState.ENABLED, false);
            do {
                timeout = System.currentTimeMillis()-initialTime > elementTimeout;
                try {
                    if (scroll) centerElement(element).click();
                    else element.click();
                    return;
                }
                catch (WebDriverException webDriverException){
                    if (counter == 0) {
                        log.new Warning("Iterating... (" + webDriverException.getClass().getName() + ")");
                        caughtException = webDriverException;
                    }
                    else if (!webDriverException.getClass().getName().equals(caughtException.getClass().getName())){
                        log.new Warning("Iterating... (" + webDriverException.getClass().getName() + ")");
                        caughtException = webDriverException;
                    }
                    counter++;
                }
            }
            while (!timeout);
            if (counter > 0) log.new Warning("Iterated " + counter + " time(s)!");
            log.new Warning(caughtException.getMessage());
        }
        catch (WebDriverException exception){log.new Warning(exception.getMessage());}
    }

    public void clearFillInput(WebElement inputElement, String inputText, @NotNull Boolean scroll, Boolean verify){
        // This method clears the input field before filling it
        if (scroll) clearInputField(centerElement(waitUntilElementIs(inputElement, ElementState.DISPLAYED, false)))
                .sendKeys(inputText);
        else clearInputField(waitUntilElementIs(inputElement, ElementState.DISPLAYED, false))
                .sendKeys(inputText);

        if (verify) Assert.assertEquals(inputText, inputElement.getAttribute("value"));
    }

    public WebElement waitUntilElementIs(WebElement element, ElementState state, @NotNull Boolean strict){
        if (strict) Assert.assertTrue("Element is not in " + state.name() + " state!", elementIs(element, state));
        else elementIs(element, state);
        return element;
    }

    public Boolean elementIs(WebElement element, @NotNull ElementState state){
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
                    case ENABLED -> {
                        negativeCheck = false;
                        condition = element.isEnabled();
                    }
                    case DISPLAYED -> {
                        negativeCheck = false;
                        condition = element.isDisplayed();
                    }
                    case SELECTED -> {
                        negativeCheck = false;
                        condition = element.isSelected();
                    }
                    case DISABLED -> {
                        negativeCheck = true;
                        condition = !element.isEnabled();
                    }
                    case UNSELECTED -> {
                        negativeCheck = true;
                        condition = !element.isSelected();
                    }
                    case ABSENT -> {
                        negativeCheck = true;
                        condition = !element.isDisplayed();
                    }
                    default -> throw new EnumConstantNotPresentException(ElementState.class, state.name());
                }
            }
            catch (WebDriverException webDriverException){
                if (counter == 0) {
                    log.new Warning("Iterating... (" + webDriverException.getClass().getName() + ")");
                    caughtException = webDriverException.getClass().getName();
                }
                else if (!webDriverException.getClass().getName().equals(caughtException)){
                    log.new Warning("Iterating... (" + webDriverException.getClass().getName() + ")");
                    caughtException = webDriverException.getClass().getName();
                }
                counter++;
            }
        }
        while (!timeout);
        if (counter > 0) log.new Warning("Iterated " + counter + " time(s)!");
        return false;
    }

    @Deprecated(since = "1.2.7")
    public WebElement hoverOver(WebElement element, Long initialTime){
        if (System.currentTimeMillis() - initialTime > elementTimeout) return null;
        centerElement(element);
        Actions actions = new Actions(driver);
        try {actions.moveToElement(element).build().perform();}
        catch (WebDriverException ignored) {hoverOver(element, initialTime);}
        return element;
    }

    public WebElement hoverOver(WebElement element){
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
                    log.new Warning("Iterating... (" + webDriverException.getClass().getName() + ")");
                    caughtException = webDriverException.getClass().getName();
                    counter++;
                }
                else if (!webDriverException.getClass().getName().equals(caughtException)){
                    log.new Warning("Iterating... (" + webDriverException.getClass().getName() + ")");
                    caughtException = webDriverException.getClass().getName();
                    counter++;
                }
                timeout = System.currentTimeMillis()-initialTime > elementTimeout;
            }
        }
        while (timeout);
        return element;
    }

    public void loopAndClick(List<WebElement> list, String buttonName, Boolean scroll){
        clickElement(acquireNamedElementAmongst(list,buttonName), scroll);
    }

    public <T> T acquireNamedComponentAmongst(List<T> items, String selectionName){
        log.new Info("Acquiring component called " + highlighted(Color.BLUE, selectionName));
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

    public <T> T acquireComponentByElementAttributeAmongst(
            List<T> items,
            String attributeName,
            String attributeValue,
            String elementFieldName
    ){
        log.new Info("Acquiring component by attribute " + highlighted(Color.BLUE, attributeName + " -> " + attributeValue));
        boolean timeout = false;
        long initialTime = System.currentTimeMillis();
        while (!timeout){
            for (T component : items) {
                Map<String, Object> componentFields = objectUtils.getFields(component);
                WebElement element = (WebElement) componentFields.get(elementFieldName);
                String attribute = element.getAttribute(attributeName);
                if (attribute.equals(attributeValue)) return component;
            }
            if (System.currentTimeMillis() - initialTime > elementTimeout) timeout = true;
        }
        throw new NoSuchElementException("No component with " + attributeName + " : " + attributeValue + " could be found!");
    }

    public <T> T acquireExactNamedComponentAmongst(
            List<T> items,
            String elementText,
            String elementFieldName
    ){
        log.new Info("Acquiring component called " + highlighted(Color.BLUE, elementText));
        boolean timeout = false;
        long initialTime = System.currentTimeMillis();
        while (!timeout){
            for (T component : items) {
                Map<String, Object> componentFields = objectUtils.getFields(component);
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
     *
     * @deprecated replaced by acquireExactNamedComponentAmongst(components, elementText, elementFieldName)
     */
    @Deprecated(since = "1.6.2")
    public WebComponent acquireExactNamedComponentAmongst(
            String elementText,
            String elementFieldName,
            String componentListName,
            String pageName,
            Object objectRepository){
        log.new Info("Acquiring component called " + highlighted(Color.BLUE, elementText));
        boolean timeout = false;
        long initialTime = System.currentTimeMillis();
        while (!timeout){
            for (WebComponent component : getComponentsFromPage(componentListName, pageName, objectRepository)) {
                Map<String, Object> componentFields = objectUtils.getFields(component);
                WebElement element = (WebElement) componentFields.get(elementFieldName);
                String text = element.getText();
                String name = element.getAccessibleName();
                if (text.equalsIgnoreCase(elementText) || name.equalsIgnoreCase(elementText)) return component;
            }
            if (System.currentTimeMillis() - initialTime > elementTimeout) timeout = true;
        }
        throw new NoSuchElementException("No component with text/name '" + elementText + "' could be found!");
    }

    public WebElement acquireNamedElementAmongst(List<WebElement> items, String selectionName){
        log.new Info("Acquiring element called " + highlighted(Color.BLUE, selectionName));
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

    @Deprecated(since = "1.2.7")
    public WebElement acquireNamedElementAmongst(@NotNull List<WebElement> items, String selectionName, long initialTime){
        log.new Info("Acquiring element called " + highlighted(Color.BLUE, selectionName));
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
                log.new Warning("Recursion! (" + exception.getClass().getName() + ")");
                return acquireNamedElementAmongst(items, selectionName, initialTime);
            }
            throw exception;
        }
    }

    @Deprecated(since = "1.2.7")
    public <T> T acquireNamedComponentAmongst(@NotNull List<T> items, String selectionName, long initialTime){
        log.new Info("Acquiring element called " + highlighted(Color.BLUE, selectionName));
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
                log.new Warning("Recursion! (" + exception.getClass().getName() + ")");
                return acquireNamedComponentAmongst(items, selectionName, initialTime);
            }
            throw exception;
        }
    }

    public WebElement acquireElementUsingAttributeAmongst(List<WebElement> items, String attributeName, String attributeValue){
        log.new Info("Acquiring element called " + highlighted(Color.BLUE, attributeValue) + " using its " + highlighted(Color.BLUE, attributeName) + " attribute");
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

    @Deprecated(since = "1.2.7")
    public WebElement acquireElementUsingAttributeAmongst(@NotNull List<WebElement> elements, String attributeName, String attributeValue, long initialTime){
        log.new Info("Acquiring element called " + highlighted(Color.BLUE, attributeValue) + " using its " + highlighted(Color.BLUE, attributeName) + " attribute");
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
                log.new Warning("Recursion! (" + exception.getClass().getName() + ")");
                return acquireElementUsingAttributeAmongst(elements, attributeName, attributeValue, initialTime);
            }
            throw exception;
        }
    }

    public String switchWindowByHandle(@Nullable String handle){
        log.new Info("Switching to the next tab");
        String parentWindowHandle = driver.getWindowHandle();
        if (handle == null)
            for (String windowHandle:driver.getWindowHandles()) {
                if (!windowHandle.equalsIgnoreCase(parentWindowHandle))
                    driver = (RemoteWebDriver) driver.switchTo().window((windowHandle));
            }
        else driver = (RemoteWebDriver) driver.switchTo().window(handle);
        return parentWindowHandle;
    }

    public String switchWindowByIndex(Integer tabIndex){
        log.new Info("Switching the tab with the window index: " + tabIndex);
        String parentWindowHandle = driver.getWindowHandle();
        List<String> handles = new ArrayList<>(driver.getWindowHandles());
        String handle = handles.get(tabIndex);
        driver = (RemoteWebDriver) driver.switchTo().window(handle);
        return parentWindowHandle;
    }

    //This method clicks a button with a certain text on it
    public void clickButtonWithText(String buttonText, Boolean scroll){clickElement(getElementByText(buttonText), scroll);}

    //This method clears an input field /w style
    public WebElement clearInputField(@NotNull WebElement element){
        int textLength = element.getAttribute("value").length();
        for(int i = 0; i < textLength; i++){element.sendKeys(Keys.BACK_SPACE);}
        return element;
    }

    //This method returns an element with a certain text on it
    public WebElement getElementByText(String elementText){
        try {
            return driver.findElement(By.xpath("//*[text()='" +elementText+ "']"));
        }
        catch (ElementNotFoundException | NoSuchElementException exception){
            throw new NoSuchElementException(GRAY+exception.getMessage()+RESET);
        }
    }

    //This method returns an element with a certain text on it
    public WebElement getElementContainingText(String elementText){
        try {
            return driver.findElement(By.xpath("//*[contains(text(), '" +elementText+ "')]"));
        }
        catch (ElementNotFoundException | NoSuchElementException exception){
            throw new NoSuchElementException(GRAY+exception.getMessage()+RESET);
        }
    }

    //This method performs click, hold, drag and drop action on a certain element
    public void dragDropToAction(WebElement element, WebElement destinationElement){

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

    //This method performs click, hold, dragAndDropBy action on at a certain offset
    public void dragDropByAction(WebElement element, int xOffset, int yOffset){

        centerElement(element);

        Actions action = new Actions(driver);
        action.moveToElement(element)
                .clickAndHold(element)
                .dragAndDropBy(element, xOffset, yOffset)
                .build()
                .perform();
        waitFor(0.5);
    }

    //This method performs click, hold, drag and drop action on at a certain offset
    public void dragDropAction(WebElement element, int xOffset, int yOffset){

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

    //This method refreshes the current page
    public void refreshThePage(){driver.navigate().refresh();}

    //This method clicks an element at an offset
    public void clickAtAnOffset(WebElement element, int xOffset, int yOffset, boolean scroll){

        if (scroll) centerElement(element);

        Actions builder = new org.openqa.selenium.interactions.Actions(driver);
        builder
                .moveToElement(element, xOffset, yOffset)
                .click()
                .build()
                .perform();
    }

    public Alert getAlert(){return driver.switchTo().alert();}

    public void uploadFile(@NotNull WebElement fileUploadInput, String directory, String fileName){fileUploadInput.sendKeys(directory+"/"+fileName);}

    public String combineKeys(Keys key1, Keys key2){return Keys.chord(key1,key2);}

    //This method makes the thread wait for a certain while
    public void waitFor(double seconds){
        if (seconds > 1) log.new Info("Waiting for "+BLUE+seconds+GRAY+" seconds");
        try {Thread.sleep((long) (seconds* 1000L));}
        catch (InterruptedException exception){Assert.fail(GRAY+exception.getLocalizedMessage()+RESET);}
    }

    //This method scrolls an element to the center of the view
    public WebElement centerElement(WebElement element){
        String scrollScript = "var viewPortHeight = Math.max(document.documentElement.clientHeight, window.innerHeight || 0);"
                + "var elementTop = arguments[0].getBoundingClientRect().top;"
                + "window.scrollBy(0, elementTop-(viewPortHeight/2));";

        ((JavascriptExecutor) driver).executeScript(scrollScript, element);

        waitFor(0.3);
        return element;
    }

    public void scroll(@NotNull Direction direction){
        log.new Info("Scrolling " + highlighted(Color.BLUE, direction.name().toLowerCase()));
        String script = switch (direction) {
            case UP -> "window.scrollBy(0,-document.body.scrollHeight)";
            case DOWN -> "window.scrollBy(0,document.body.scrollHeight)";
        };
        ((JavascriptExecutor) driver).executeScript(script);
    }

    //This method verifies current url
    public void verifyUrl(String url){
        Assert.assertTrue(driver.getCurrentUrl().contains(url));
    }

    //This method verifies the page title
    public void verifyPageTitle(String pageTitle){
        Assert.assertTrue(driver.getTitle().contains(pageTitle));
    }

    //This method returns all the attributes of an element as an object
    public Object getElementObject(WebElement element){
        return ((JavascriptExecutor) driver).executeScript("var items = {}; for (index = 0;" +
                        " index < arguments[0].attributes.length; ++index) " +
                        "{ items[arguments[0].attributes[index].name] = arguments[0].attributes[index].value }; return items;",
                element
        );
    }

    //This method prints all the attributes of a given element
    public void printElementAttributes(WebElement element){
        JSONObject attributeJSON = new JSONObject(strUtils.str2Map(getElementObject(element).toString()));
        for (Object attribute : attributeJSON.keySet()) log.new Info(attribute +" : "+ attributeJSON.get(attribute));
    }

    public WebElement getParentByClass(WebElement childElement, String current, String parentSelectorClass) {

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

    public String generateXPath(@NotNull WebElement childElement, String current) {
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

    public List<WebElement> verifyAbsenceOfElementLocatedBy(@NotNull Locator locatorType, String locator, long startTime){

        List<WebElement> elements = switch (locatorType) {
            case XPATH -> driver.findElements(By.xpath(locator));
            case CSS -> driver.findElements(By.cssSelector(locator));
        };

        if ((System.currentTimeMillis() - startTime) > elementTimeout){
            Assert.fail(GRAY+"An element was located unexpectedly"+RESET);
            return elements;
        }
        if (elements.size() > 0){return verifyAbsenceOfElementLocatedBy(locatorType, locator, startTime);}
        else return null;
    }

    @Deprecated(since = "1.2.7")
    public void waitUntilElementIsNoLongerPresent(WebElement element, long startTime){
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
            log.new Success("The element is no longer present!");
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(elementTimeout/1000));
        }
    }

    @Deprecated(since = "1.2.7")
    public WebElement waitUntilElementIsInvisible(WebElement element, long startTime) {
        if ((System.currentTimeMillis() - startTime) > elementTimeout) return element;
        try {
            wait.until(ExpectedConditions.invisibilityOf(element));
            return null;
        }
        catch (TimeoutException e) {return waitUntilElementIsInvisible(element, startTime);}
    }

    @Deprecated(since = "1.2.7")
    public WebElement waitUntilElementIsClickable(WebElement element, long initialTime){
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

    public void clickWithJS(WebElement webElement) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", webElement);
    }

    public void scrollWithJS(WebElement webElement) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView();", webElement);
    }

    @Deprecated(since = "1.2.7")
    public boolean elementIsDisplayed(WebElement element, long startTime) {
        if ((System.currentTimeMillis() - startTime) > 10000) return false;
        try {return element.isDisplayed();}
        catch (Exception e) {
            log.new Info(e);
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
    public <T> WebElement getElement(String fieldName, Class<T> inputClass){
        return (WebElement) objectUtils.getFieldValue(fieldName, inputClass);
    }

    /**
     * Custom context checker to re-format an input text or acquire context data
     *
     * @param input string that is to be context checked
     * @return value depending on the context (could be from ContextStore, Properties, Random etc)
     */
    public String contextCheck(@NotNull String input){
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
    public boolean isEventFired(String eventName, String listenerScript){
        String eventKey = strUtils.generateRandomString(eventName + "#", 6, false, true);
        listenerScript = listenerScript.replace(eventName, "'" + eventName + "', function(){console.warn('" + eventKey +"')}");
        executeScript(listenerScript);
        LogEntries logs = driver.manage().logs().get(LogType.BROWSER);
        for (LogEntry entry: logs.getAll())
            if (entry.toString().contains(eventKey)) {
                log.new Success("'" + eventName + "' event is fired!");
                return true;
            }
        log.new Warning(eventName + " event is not fired!");
        return false;
    }

    /**
     * Checks if an event was fired
     *
     * @param eventKey key that is meant to be caught from the console in case the event fires
     * @param listenerScript script for calling the listener, ex: "dataLayerObject.listen('page.info', function(){console.warn(eventKey)});"
     * @return true if the specified event was fired.
     */
    public boolean isEventFiredByScript(String eventKey, String listenerScript){
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
    public Object executeScript(String script){
        log.new Info("Executing script: " + highlighted(BLUE,script));
        return ((JavascriptExecutor) driver).executeScript(script);
    }
}
