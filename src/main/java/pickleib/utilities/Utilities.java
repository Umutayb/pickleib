package pickleib.utilities;

import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.remote.RemoteWebDriver;
import pickleib.enums.Direction;
import pickleib.enums.ElementState;
import pickleib.exceptions.PickleibException;
import pickleib.mobile.driver.AppiumDriverFactory;
import pickleib.mobile.driver.PickleibAppiumDriver;
import pickleib.web.driver.PickleibWebDriver;
import pickleib.web.driver.WebDriverFactory;
import utils.Printer;
import utils.StringUtilities;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import static utils.StringUtilities.Color.*;

@SuppressWarnings("unused")
public interface Utilities {

    StringUtilities strUtils = new StringUtilities();
    Printer log = new Printer(Utilities.class);
    RemoteWebDriver driver = getDriver();
    long elementTimeout = getElementTimeout();


    /**
     * Gets the name of the method that called the API.
     *
     * @return the name of the method that called the API
     */
    private static String getCallingClassName(){
        StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
        for (int i=1; i<stElements.length; i++) {
            StackTraceElement ste = stElements[i];
            if (!ste.getClassName().equals(Utilities.class.getName()) && ste.getClassName().indexOf("java.lang.Thread")!=0) {
                return ste.getClassName();
            }
        }
        return null;
    }

    static private RemoteWebDriver getDriver(){
        String className = getCallingClassName();
        switch (Objects.requireNonNull(className)) {
            case "pickleib.mobile.utilities.MobileUtilities",
                    "pickleib.mobile.steps.MobileStepBase",
                    "pickleib.mobile.interactions.MobileInteractions" -> {
                return PickleibAppiumDriver.driver;
            }
            case "pickleib.web.utilities.WebUtilities",
                    "pickleib.web.steps.WebStepBase",
                    "pickleib.web.interactions.WebInteractions" -> {
                return PickleibWebDriver.driver;
            }
            case "pickleib.utilities.ElementAcquisition$PageObjectModel",
                    "pickleib.utilities.ElementAcquisition$PageObjectJson",
                    "pickleib.utilities.ElementAcquisition$Reflections"-> {
                return ElementAcquisition.driver;
            }
            default -> throw new RuntimeException("Unknown caller: " + className);
        }
    }

    static long getElementTimeout(){
        String className = getCallingClassName();
        switch (Objects.requireNonNull(className)) {
            case "pickleib.mobile.utilities.MobileUtilities",
                    "pickleib.mobile.steps.MobileStepBase",
                    "pickleib.mobile.interactions.MobileInteractions" -> {
                return AppiumDriverFactory.elementTimeout;
            }
            case "pickleib.web.utilities.WebUtilities",
                    "pickleib.web.steps.WebStepBase",
                    "pickleib.web.interactions.WebInteractions" -> {
                return WebDriverFactory.elementTimeout;
            }
            case "pickleib.utilities.ElementAcquisition$PageObjectModel",
                    "pickleib.utilities.ElementAcquisition$PageObjectJson",
                    "pickleib.utilities.ElementAcquisition$Reflections"-> {
                return ElementAcquisition.elementTimeout;
            }
            default -> throw new RuntimeException("Unknown caller: " + className);
        }
    }

    /**
     * Highlights a given text with a specified color (resets to plain)
     *
     * @param color target color
     * @param text target text
     */
    default String highlighted(StringUtilities.Color color, CharSequence text){
        StringJoiner colorFormat = new StringJoiner("", color.getValue(), RESET.getValue());
        return String.valueOf(colorFormat.add(text));
    }

    /**
     * Acquires a specified attribute of a given element
     *
     * @param element target element
     * @param attribute target attribute
     * @return returns the element attribute
     */
    default String getAttribute(WebElement element, String attribute){return element.getAttribute(attribute);}

    /**
     * Clicks an element after waiting for its state to be enabled
     *
     * @param element target element
     * @param scroll scrolls if true
     */
    default void clickElement(WebElement element, Boolean scroll){
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
            }
        }
        while (!(System.currentTimeMillis() - initialTime > elementTimeout));
        if (counter > 0) log.warning("Iterated " + counter + " time(s)!");
        log.warning(caughtException.getMessage());
        throw new PickleibException(caughtException);
    }

    /**
     * Click coordinates specified by the given offsets from the center of a given element
     *
     * @param element target element
     */
    default void clickTowards(WebElement element){
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
    default void clickElement(WebElement element){
        clickElement(element, false);
    }

    /**
     * Clicks an element if its present (in enabled state)
     *
     * @param element target element
     * @param scroll scrolls if true
     */
    default void clickIfPresent(WebElement element, Boolean scroll){
        try {clickElement(element, scroll);}
        catch (WebDriverException exception){log.warning(exception.getMessage());}
    }

    /**
     * Clears and fills a given input
     *
     * @param inputElement target input element
     * @param inputText input text
     * @param scroll scrolls if true
     * @param verify verifies the input text value equals to an expected text if true
     */
    default void clearFillInput(WebElement inputElement, String inputText, @NotNull Boolean scroll, Boolean verify){
        // This method clears the input field before filling it
        elementIs(inputElement, ElementState.displayed);
        if (scroll) clearInputField(centerElement(inputElement)).sendKeys(inputText);
        else clearInputField(inputElement).sendKeys(inputText);
        if (verify) Assert.assertEquals(inputText, inputElement.getAttribute("value"));
    }

    /**
     * Verifies a given element is in expected state
     *
     * @param element target element
     * @param state expected state
     * @return returns the element if its in expected state
     */
    default WebElement verifyElementState(WebElement element, ElementState state){
        Assert.assertTrue("Element is not in " + state.name() + " state!", elementIs(element, state));
        log.success("Element state is verified to be: " + state.name());
        return element;
    }

    /**
     * Waits until a given element is in expected state
     *
     * @param element target element
     * @param state expected state
     * @return returns true if an element is in the expected state
     */
    default Boolean elementIs(WebElement element, @NotNull ElementState state){
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));
        long initialTime = System.currentTimeMillis();
        String caughtException = null;
        boolean timeout;
        boolean condition = false;
        boolean negativeCheck = false;
        int counter = 0;
        do {
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
        while (!(System.currentTimeMillis() - initialTime > elementTimeout));
        if (counter > 0) log.warning("Iterated " + counter + " time(s)!");
        return false;
    }

    /**
     * Hovers cursor over of a given element
     *
     * @param element target element
     * @return returns the selected element
     */
    default WebElement hoverOver(WebElement element){
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
            }
            timeout = System.currentTimeMillis() - initialTime > elementTimeout;
        }
        while (timeout);
        return element;
    }

    /**
     * Acquire a component amongst a list of components by its name
     *
     * @param items list of components
     * @param selectionName component name
     * @return returns the selected component
     */
    default <T> T acquireNamedComponentAmongst(List<T> items, String selectionName){
        log.info("Acquiring component called " + strUtils.highlighted(BLUE, selectionName));
        boolean timeout = false;
        long initialTime = System.currentTimeMillis();
        WebDriverException caughtException = null;
        int counter = 0;
        while (!(System.currentTimeMillis() - initialTime > elementTimeout)){
            try {
                for (T selection : items) {
                    String text = ((WebElement) selection).getText();
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

    /**
     * Acquire listed element by its name
     *
     * @param items list that includes target element
     * @param selectionName element name
     * @return returns the selected element
     */
    default WebElement acquireNamedElementAmongst(List<WebElement> items, String selectionName){
        log.info("Acquiring element called " + strUtils.highlighted(BLUE, selectionName));
        boolean timeout = false;
        long initialTime = System.currentTimeMillis();
        WebDriverException caughtException = null;
        int counter = 0;
        while (!(System.currentTimeMillis() - initialTime > elementTimeout)){
            try {
                for (WebElement selection : items) {
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
        throw new NoSuchElementException("No element with text/name '" + selectionName + "' could be found!");
    }

    /**
     * Acquire a listed element by its attribute
     *
     * @param items list that includes target element
     * @param attributeName attribute name
     * @param attributeValue attribute value
     * @return returns the selected element
     */
    default WebElement acquireElementUsingAttributeAmongst(List<WebElement> items, String attributeName, String attributeValue){
        log.info("Acquiring element called " + strUtils.markup(BLUE, attributeValue) + " using its " + strUtils.markup(BLUE, attributeName) + " attribute");
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
     * Clicks an element acquired by text
     *
     * @param buttonText target element text
     * @param scroll scrolls if true
     */
    default void clickButtonWithText(String buttonText, Boolean scroll){clickElement(getElementByText(buttonText), scroll);}

    /**
     * Clears an input element
     *
     * @param element target element
     */
    default WebElement clearInputField(@NotNull WebElement element){
        int textLength = element.getAttribute("value").length();
        for(int i = 0; i < textLength; i++){element.sendKeys(Keys.BACK_SPACE);}
        return element;
    }

    /**
     * Acquires an element by its text
     *
     * @param elementText target element text
     */
    default WebElement getElementByText(String elementText){
        try {
            return driver.findElement(By.xpath("//*[text()='" +elementText+ "']"));
        }
        catch (NoSuchElementException exception){
            throw new NoSuchElementException(GRAY+exception.getMessage()+RESET);
        }
    }

    /**
     * Acquires an element that contains a certain text
     *
     * @param elementText target element text
     */
    default WebElement getElementContainingText(String elementText){
        try {
            return driver.findElement(By.xpath("//*[contains(text(), '" +elementText+ "')]"));
        }
        catch (NoSuchElementException exception){
            throw new NoSuchElementException(GRAY+exception.getMessage()+RESET);
        }
    }

    /**
     * Drags and drops a given element on top of another element
     *
     * @param element element that drags
     * @param destinationElement target element
     */
    default void dragDropToAction(WebElement element, WebElement destinationElement){

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
    default void dragDropByAction(WebElement element, int xOffset, int yOffset){

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
    default void dragDropAction(WebElement element, int xOffset, int yOffset){

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
    default void refreshThePage(){
        driver.navigate().refresh();}

    /**
     * Click coordinates specified by the given offsets from the center of a given element
     *
     * @param element target element
     * @param xOffset x offset from the center of the element
     * @param yOffset y offset from the center of the element
     */
    @SuppressWarnings("SameParameterValue")
    default void clickAtAnOffset(WebElement element, int xOffset, int yOffset){
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
    default Alert getAlert(){return driver.switchTo().alert();}

    /**
     * Uploads a given file
     *
     * @param fileUploadInput upload element
     * @param directory absolute file directory (excluding the file name)
     * @param fileName file name (including a file extension)
     */
    default void uploadFile(@NotNull WebElement fileUploadInput, String directory, String fileName){fileUploadInput.sendKeys(directory+"/"+fileName);}

    /**
     * Combines the given keys
     *
     * @param keys key inputs
     */
    default String combineKeys(Keys... keys) {
        return Keys.chord(keys);
    }

    /**
     * Waits for a certain while
     *
     * @param seconds duration as a double
     */
    //This method makes the thread wait for a certain while
    default void waitFor(double seconds){
        if (seconds > 1) log.info("Waiting for " + strUtils.markup(BLUE, String.valueOf(seconds)) + " seconds");
        try {Thread.sleep((long) (seconds* 1000L));}
        catch (InterruptedException exception){Assert.fail(GRAY+exception.getLocalizedMessage()+RESET);}
    }

    /**
     * Scrolls an element to the center of the view
     *
     * @param element target element
     * @return returns the targeted element
     */
    //This method scrolls an element to the center of the view
    default WebElement centerElement(WebElement element){
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
    default void scroll(@NotNull Direction direction){
        log.info("Scrolling " + strUtils.highlighted(BLUE, direction.name().toLowerCase()));
        String script = switch (direction) {
            case up -> "window.scrollBy(0,-document.body.scrollHeight)";
            case down -> "window.scrollBy(0,document.body.scrollHeight)";
        };
        ((JavascriptExecutor) driver).executeScript(script);
    }

    /**
     * Transform a given element to an object using javascript
     *
     * @param element target element
     * @return returns an object with the attributes of a given element
     */
    //This method returns all the attributes of an element as an object
    default Object getElementObject(WebElement element){
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
    default void printElementAttributes(WebElement element){//TODO: update this
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
    default WebElement getParentByClass(WebElement childElement, String current, String parentSelectorClass) {

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
    default String generateXPath(@NotNull WebElement childElement, String current) {
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
     * Checks if an event was fired
     * Create a custom script to listen for an event by generating a unique event key and catches this key in the console
     * Ex: "dataLayerObject.listen(eventName, function(){console.warn(eventKey)});"
     *
     * @param eventName event name of the event that is expected to be fired
     * @param listenerScript script for calling the listener, ex: "dataLayerObject.listen( eventName );"
     * @return true if the specified event was fired.
     */
    default boolean isEventFired(String eventName, String listenerScript){
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
    default boolean isEventFiredByScript(String eventKey, String listenerScript){
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
     * @return object if the scripts yield one
     */
    default Object executeScript(String script){
        log.info("Executing script: " + strUtils.highlighted(BLUE, script));
        return ((JavascriptExecutor) driver).executeScript(script);
    }
}
