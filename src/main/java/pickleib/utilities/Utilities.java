package pickleib.utilities;

import com.fasterxml.jackson.databind.ObjectMapper;
import context.ContextStore;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.html5.LocalStorage;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.remote.RemoteExecuteMethod;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.html5.RemoteWebStorage;
import pickleib.enums.Direction;
import pickleib.enums.ElementState;
import pickleib.exceptions.PickleibException;
import pickleib.mobile.interactions.MobileInteractions;
import pickleib.utilities.screenshot.ScreenCaptureUtility;
import pickleib.web.interactions.WebInteractions;
import records.Bundle;
import utils.*;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;
import static pickleib.utilities.element.ElementAcquisition.*;
import static utils.StringUtilities.Color.*;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public abstract class Utilities {

    public static ReflectionUtilities reflectionUtils = new ReflectionUtilities();
    public ScreenCaptureUtility capture = new ScreenCaptureUtility();
    public StringUtilities strUtils = new StringUtilities();
    public ObjectMapper objectMapper = new ObjectMapper();
    public Printer log = new Printer(this.getClass());
    public TextParser parser = new TextParser();

    public RemoteWebDriver driver;

    public long elementTimeout = Long.parseLong(PropertyUtility.getProperty("element-timeout", "15000"));

    protected Utilities(RemoteWebDriver driver){
        this.driver = driver;
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
     * @return returns the element attribute
     */
    protected String getAttribute(WebElement element, String attribute){return element.getAttribute(attribute);}

    //TODO check the warning log in the loop
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
            }
        }
        while (!(System.currentTimeMillis() - initialTime > elementTimeout));
        if (counter > 0) log.warning("Iterated " + counter + " time(s)!");
        log.warning(caughtException.getMessage());
        throw new PickleibException(caughtException);
    }

    /**
     *
     * If present, click element {element name} on the {page name}
     *
     * @param element target element
     */
    public void clickButtonIfPresent(WebElement element){
        try {if (elementIs(element, ElementState.displayed)) clickElement(element, true);}
        catch (WebDriverException ignored){log.warning("The element was not present!");}
    }

    /**
     *
     * Click on a button that contains {button text} text
     *
     * @param buttonText target button text
     * @param scroll scrolls if true
     */
    public void clickButtonByItsText(String buttonText, Boolean scroll) {
        log.info("Clicking button by its text " + highlighted(BLUE, buttonText));
        WebElement element = getElementByText(buttonText);
        centerElement(element);
        clickElement(element, scroll);
    }

    /**
     *
     * Press {target key} key on {element name} element of the {}
     *
     * @param keys target keys
     * @param elementName target element name
     * @param pageName specified page instance name
     */
    public void pressKeysOnElement(WebElement element, String elementName, String pageName, Keys... keys){
        String combination = Keys.chord(keys);
        log.info("Pressing " + strUtils.markup(BLUE, combination) + " keys on " + strUtils.markup(BLUE, elementName) + " element.");
        element.sendKeys(combination);
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
        clickElement(element, false);
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
     * @param verify verifies the input text value equals to an expected text if true
     */
    protected void clearFillInput(WebElement inputElement, String inputText, @NotNull Boolean scroll, Boolean verify){
        fillInputElement(inputElement, inputText, scroll, verify);
    }

    /**
     * Clears and fills a given input
     *
     * @param inputElement target input element
     * @param inputText input text
     */
    protected void fillInput(WebElement inputElement, String inputText){
        // This method clears the input field before filling it
        fillInputElement(inputElement, inputText, false, false);
    }

    /**
     * Clears and fills a given input
     *
     * @param inputElement target input element
     * @param inputText input text
     */
    protected void fillAndVerifyInput(WebElement inputElement, String inputText){
        // This method clears the input field before filling it
        fillInputElement(inputElement, inputText, false, true);
    }

    /**
     * Clears and fills a given input
     *
     * @param inputElement target input element
     * @param inputText input text
     */
    protected void fillAndVerifyInput(WebElement inputElement, String inputText, Boolean scroll){
        // This method clears the input field before filling it
        fillInputElement(inputElement, inputText, scroll, true);
    }

    /**
     * Clears and fills a given input
     *
     * @param inputElement target input element
     * @param inputText input text
     * @param scroll scrolls if true
     * @param verify verifies the input text value equals to an expected text if true
     */
    protected void fillInputElement(WebElement inputElement, String inputText, @NotNull Boolean scroll, Boolean verify){
        // This method clears the input field before filling it
        elementIs(inputElement, ElementState.displayed);
        if (scroll) centerElement(inputElement).sendKeys(inputText);
        else centerElement(inputElement).sendKeys(inputText);
        if (verify) Assert.assertEquals(inputText, inputElement.getAttribute("value"));
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
     * @return returns true if an element is in the expected state
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
            if (condition || (counter > 1 && negativeCheck)) return true;
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
            }
            timeout = System.currentTimeMillis() - initialTime > elementTimeout;
        }
        while (timeout);
        return element;
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
        catch (NoSuchElementException exception){
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
     * @param fileName file name (including a file extension)
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
    protected abstract WebElement centerElement(WebElement element);

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
     * Checks if an event was fired
     * Create a custom script to listen for an event by generating a unique event key and catches this key in the console
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
     * @return object if the scripts yield one
     */
    protected Object executeScript(String script){
        log.info("Executing script: " + strUtils.highlighted(BLUE, script));
        return ((JavascriptExecutor) driver).executeScript(script);
    }

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

    /**
     *
     * Adds given values to the local storage
     *
     * @param form Map(String, String)
     */
    public void addValuesToLocalStorage(Map<String, String> form){
        for (String valueKey: form.keySet()) {
            RemoteExecuteMethod executeMethod = new RemoteExecuteMethod(driver);
            RemoteWebStorage webStorage = new RemoteWebStorage(executeMethod);
            LocalStorage storage = webStorage.getLocalStorage();
            storage.setItem(valueKey, strUtils.contextCheck(form.get(valueKey)));
        }
    }

    /**
     *
     * Adds given cookies
     *
     * @param cookies Map(String, String)
     */
    public void putCookies(Map<String, String> cookies){
        for (String cookieName: cookies.keySet()) {
            Cookie cookie = new Cookie(cookieName, strUtils.contextCheck(cookies.get(cookieName)));
            driver.manage().addCookie(cookie);
        }
    }

    /**
     * Deletes all cookies
     */
    public void deleteAllCookies() {driver.manage().deleteAllCookies();}

    /**
     *
     * Acquire attribute {attribute name} from element {element name} on the {page name}
     * (Use 'innerHTML' attributeName to acquire text on an element)
     *
     * @param element target element
     * @param attributeName acquired attribute name
     * @param elementName target element name
     * @param pageName specified page instance name
     */
    public void updateContextByElementAttribute(
            WebElement element,
            String attributeName,
            String elementName,
            String pageName){
        log.info("Acquiring " +
                highlighted(BLUE,attributeName) +
                highlighted(GRAY," attribute of ") +
                highlighted(BLUE, elementName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName)
        );
        String attribute = element.getAttribute(attributeName);
        log.info("Attribute -> " + highlighted(BLUE, attributeName) + highlighted(GRAY," : ") + highlighted(BLUE, attribute));
        ContextStore.put(elementName + "-" + attributeName, attribute);
        log.info("Element attribute saved to the ContextStore as -> '" +
                highlighted(BLUE, elementName + "-" + attributeName) +
                highlighted(GRAY, "' : '") +
                highlighted(BLUE, attribute) +
                highlighted(GRAY, "'")
        );
    }

    /**
     *
     * Verify the text of {element name} on the {page name} to be: {expected text}
     *
     * @param element target element
     * @param expectedText expected text
     */
    public void verifyElementText(WebElement element, String expectedText){
        expectedText = strUtils.contextCheck(expectedText);
        Assert.assertEquals("Element text is not " + expectedText + "!", expectedText, element.getText());
        log.success("Text of the element " + expectedText + " was verified!");
    }

    /**
     *
     * Verify the text of {element name} on the {page name} to be: {expected text}
     *
     * @param element target element
     * @param expectedText expected text
     */
    public void verifyElementContainsText(WebElement element, String expectedText){
        expectedText = strUtils.contextCheck(expectedText);
        Assert.assertTrue("Element text does not contain" + expectedText + "!", element.getText().contains(expectedText));
        log.success("The element text does contain " + expectedText + " text!");
    }

    /**
     *
     * Verify the text of an element from the list on the {page name}
     *
     * @param pageName specified page instance name
     */
    public void verifyListedElementText(
            List<Bundle<WebElement, String, String>> bundles,
            String pageName){
        for (Bundle<WebElement, String, String> bundle : bundles) {
            String elementName = bundle.beta();
            String expectedText = bundle.theta();
            log.info("Performing text verification for " +
                    highlighted(BLUE, elementName) +
                    highlighted(GRAY," on the ") +
                    highlighted(BLUE, pageName) +
                    highlighted(GRAY, " with the text: ") +
                    highlighted(BLUE, expectedText)
            );
            Assert.assertEquals("The " + bundle.alpha().getText() + " does not contain text '", expectedText, bundle.alpha().getText());
            log.success("Text of the element" + bundle.alpha().getText() + " was verified!");
        }
    }

    /**
     *
     * Fill form input on the {page name}
     *
     * @param bundles list of bundles where input element, input name and input texts are stored
     * @param pageName specified page instance name
     */
    public void fillInputForm(List<Bundle<WebElement, String, String>> bundles, String pageName){
        String inputName;
        String input;
        for (Bundle<WebElement, String, String> bundle : bundles) {
            log.info("Filling " +
                    highlighted(BLUE, bundle.theta()) +
                    highlighted(GRAY," on the ") +
                    highlighted(BLUE, pageName) +
                    highlighted(GRAY, " with the text: ") +
                    highlighted(BLUE, bundle.beta())
            );
            pageName = strUtils.firstLetterDeCapped(pageName);
            clearFillInput(bundle.alpha(), //Input Element
                    bundle.beta(), //Input Text
                    false,
                    true
            );
        }
    }

    /**
     *
     * Verify that element {element name} on the {page name} has {attribute value} value for its {attribute name} attribute
     *
     * @param element target element
     * @param attributeValue expected attribute value
     * @param attributeName target attribute name
     */
    public boolean elementContainsAttribute(
            WebElement element,
            String attributeName,
            String attributeValue) {

        long initialTime = System.currentTimeMillis();
        String caughtException = null;
        int counter = 0;
        attributeValue = strUtils.contextCheck(attributeValue);
        do {
            try {
                if (Objects.equals(element.getAttribute(attributeName), attributeValue))
                    return element.getAttribute(attributeName).contains(attributeValue);
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
                waitFor(0.5);
                counter++;
            }
        }
        while (!(System.currentTimeMillis() - initialTime > elementTimeout));
        if (counter > 0) log.warning("Iterated " + counter + " time(s)!");
        log.warning("Element does not contain " +
                highlighted(BLUE, attributeName) +
                highlighted(GRAY, " -> ") +
                highlighted(BLUE, attributeValue) +
                highlighted(GRAY, " attribute pair.")
        );
        log.warning(caughtException);
        return false;
    }

    public static class InteractionUtilities extends Utilities {

        public enum DriverType{
            Web,
            Mobile
        }

        public DriverType driverType;

        public InteractionUtilities(RemoteWebDriver driver, DriverType driverType){
            super(driver);
            this.driverType = driverType;
        }

        @Override
        public WebElement centerElement(WebElement element) {
            WebInteractions webInteractions = new WebInteractions();
            MobileInteractions mobileInteractions = new MobileInteractions();
            return switch (driverType){
                case Web -> webInteractions.center(element);
                case Mobile -> mobileInteractions.center(element);
            };
        }

        public void click(WebElement element){
            clickElement(element, true);
        }

        public void fill(WebElement element, String input){
            clearFillInput(element, input, true, true);
        }

        public boolean elementStateIs(WebElement element, ElementState expectedState){
            return elementIs(element, expectedState);
        }

        public WebElement acquireElementFromList(List<WebElement> elements, String selectionText){
            return acquireNamedElementAmongst(elements, selectionText);
        }

        public WebElement acquireElementFromList(List<WebElement> elements, String attributeName, String attributeValue){
            return acquireElementUsingAttributeAmongst(elements, attributeName, attributeValue); //innerHTML for text
        }

        public <Component extends WebElement> Component acquireComponentFromList(List<Component> items, String selectionName){
            return acquireNamedComponentAmongst(items, selectionName);
        }

        public <Component extends WebElement> Component acquireComponentFromList(List<Component> items,
                                                              String attributeName,
                                                              String attributeValue,
                                                              String elementFieldName){
            return acquireComponentByElementAttributeAmongst(items, attributeName, attributeValue, elementFieldName);
        }
    }
}
