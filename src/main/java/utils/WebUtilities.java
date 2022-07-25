package utils;

import com.github.webdriverextensions.WebDriverExtensionFieldDecorator;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import com.gargoylesoftware.htmlunit.*;
import io.cucumber.core.api.Scenario;
import org.json.simple.JSONObject;
import static resources.Colors.*;
import org.openqa.selenium.*;
import java.util.Properties;
import utils.driver.Driver;
import java.time.Duration;
import org.junit.Assert;
import resources.Colors;
import java.util.List;

public abstract class WebUtilities extends Driver { //TODO: Write a method which creates a unique css selector for elements

    Printer log = new Printer(WebUtilities.class);

    public TextParser parser = new TextParser();
    public Properties properties;
    public Scenario scenario;
    public StringUtilities strUtils = new StringUtilities();
    public ObjectUtilities objectUtils = new ObjectUtilities();

    public enum Color {CYAN, RED, GREEN, YELLOW, PURPLE, GRAY, BLUE}
    public enum Navigation {BACKWARDS, FORWARDS}
    public enum Locator {XPATH, CSS}

    public WebUtilities(){
        PageFactory.initElements(new WebDriverExtensionFieldDecorator(driver), this);
        properties = FileUtilities.properties;
    }

    public String getAttribute(WebElement element, String attribute){return element.getAttribute(attribute);}

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

            switch (direction){
                case FORWARDS:
                    driver.navigate().forward();
                    break;

                case BACKWARDS:
                    driver.navigate().back();
                    break;

                default:
                    throw new EnumConstantNotPresentException(Navigation.class, direction.name());
            }
        }
        catch (Exception e){
            Assert.fail("Unable to navigate browser \""+highlighted(Color.YELLOW, direction.name())+"\" due to: " + e);
        }
    }

    //This method clicks an element after waiting it and scrolling it to the center of the view
    public void clickElement(WebElement element){
        try {waitAndClickIfElementIsClickable(centerElement(element), System.currentTimeMillis());}
        catch (ElementNotFoundException e){log.new Error(e.getMessage(),e);}
    }

    public void waitAndClickIfElementIsClickable(WebElement element, long initialTime){
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));
        if (System.currentTimeMillis()-initialTime>15000) driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
        try {
            if (!element.isEnabled()){waitUntilElementIsClickable(element, initialTime);}
            else element.click();
        }
        catch (ElementClickInterceptedException|StaleElementReferenceException|NoSuchElementException|TimeoutException exception){
            log.new Warning("Recursion! (" + exception.getClass().getName() + ")");
            waitUntilElementIsClickable(element, initialTime);
        }
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
    }

    //This method is for filling an input field, it waits for the element, scrolls to it, clears it and then fills it
    public void clearFillInput(WebElement inputElement, String inputText, Boolean verify){
        try {
            // This method clears the input field before filling it
            clearInputField(centerElement(waitUntilElementIsVisible(inputElement, System.currentTimeMillis()))).sendKeys(inputText);

            if (verify) Assert.assertEquals(inputElement.getAttribute("value"), inputText);
        }
        catch (ElementNotFoundException e){Assert.fail(GRAY+e.getMessage()+RESET);}
    }

    public WebElement hoverOver(WebElement element, Long initialTime){
        if (System.currentTimeMillis()-initialTime > 10000) return null;
        centerElement(element);
        Actions actions = new Actions(driver);
        try {actions.moveToElement(element).build().perform();}
        catch (StaleElementReferenceException ignored) {hoverOver(element,initialTime);}
        return element;
    }

    public void loopAndClick(List<WebElement> list,String buttonName){clickElement(acquireNamedElementAmongst(list,buttonName));}

    public WebElement acquireNamedElementAmongst(List<WebElement> items, String selectionName){
        log.new Info("Acquiring element called " + highlighted(Color.BLUE, selectionName));
        for (WebElement selection : items) {
            String name = selection.getAccessibleName();
            String text = selection.getText();
            if (
                    name.equalsIgnoreCase(selectionName) ||
                    name.contains(selectionName)         ||
                    text.equalsIgnoreCase(selectionName) ||
                    text.contains(selectionName)
            )
                return selection;
        }
        throw new NoSuchElementException("No element with text/name '" + selectionName + "' could be found!");
    }

    public WebElement acquireElementUsingAttributeAmongst(List<WebElement> elements, String attributeName, String attributeValue){
        log.new Info("Acquiring element called " + highlighted(Color.BLUE, attributeValue) + " using its " + highlighted(Color.BLUE, attributeName) + " attribute");
        for (WebElement selection : elements) {
            String attribute = selection.getAttribute(attributeName);
            if (attribute.equalsIgnoreCase(attributeValue) || attribute.contains(attributeValue))
                return selection;
        }
        throw new NoSuchElementException("No element with the attributes '" + attributeName + " : " + attributeValue + "' could be found!");
    }

    public String switchWindowHandle(String handle){
        String parentWindowHandle = driver.getWindowHandle();
        if (handle == null)
            for (String windowHandle:driver.getWindowHandles()) {
                if (!windowHandle.equalsIgnoreCase(parentWindowHandle))
                    driver = (RemoteWebDriver) driver.switchTo().window((windowHandle));
            }
        else driver = (RemoteWebDriver) driver.switchTo().window(handle);
        return parentWindowHandle;
    }

    //This method clicks a button with a certain text on it
    public void clickButtonWithText(String buttonText){clickElement(getElementByText(buttonText));}

    //This method clears an input field /w style
    public WebElement clearInputField(WebElement element){
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
    public void clickAtAnOffset(WebElement element, int xOffset, int yOffset){

        centerElement(element);

        Actions builder = new org.openqa.selenium.interactions.Actions(driver);
        builder
                .moveToElement(element, xOffset, yOffset)
                .click()
                .build()
                .perform();
    }

    public Alert getAlert(){return driver.switchTo().alert();}

    public void uploadFile(WebElement fileUploadInput, String directory, String fileName){fileUploadInput.sendKeys(directory+"/"+fileName);}

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

        waitFor(0.5);
        return element;
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

    public String generateXPath(WebElement childElement, String current) {
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

    public WebElement waitUntilElementIsVisible(WebElement element, long initialTime){
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));
        if (System.currentTimeMillis()-initialTime>15000){
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
            return null;
        }
        try {if (!element.isDisplayed()){waitUntilElementIsVisible(element, initialTime);}}
        catch (StaleElementReferenceException|NoSuchElementException|TimeoutException exception){
            waitUntilElementIsVisible(element, initialTime);
        }
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
        return element;
    }

    public List<WebElement> verifyAbsenceOfElementLocatedBy(Locator locatorType, String locator, long startTime){

        List<WebElement> elements;

        switch (locatorType){
            case XPATH:
                elements = driver.findElements(By.xpath(locator));
                break;

            case CSS:
                elements = driver.findElements(By.cssSelector(locator));
                break;

            default: throw new EnumConstantNotPresentException(Locator.class, locatorType.name());
        }

        if ((System.currentTimeMillis() - startTime) > 15000){
            Assert.fail(GRAY+"An element was located unexpectedly"+RESET);
            return elements;
        }
        if (elements.size() > 0){return verifyAbsenceOfElementLocatedBy(locatorType, locator, startTime);}
        else return null;
    }

    public void waitUntilElementIsNoLongerPresent(WebElement element, long startTime){
        try {
            WebDriver subDriver = driver;
            subDriver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));
            List<WebElement> elementPresence = driver.findElements(By.xpath(generateXPath(element,"")));
            while (elementPresence.size()>0){
                if ((System.currentTimeMillis() - startTime) > 15000)
                    throw new TimeoutException(GRAY+"Element was still present after "+(System.currentTimeMillis() - startTime)/1000+" seconds."+RESET);
                elementPresence = subDriver.findElements(By.xpath(generateXPath(element,"")));
            }
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
        }
        catch (StaleElementReferenceException exception) {
            if (System.currentTimeMillis()-startTime<=15000) waitUntilElementIsNoLongerPresent(element, startTime);
            else throw new TimeoutException(GRAY+"Element was still present after "+(System.currentTimeMillis() - startTime)/1000+" seconds."+RESET);
        }
        catch (NoSuchElementException | IllegalArgumentException ignored){
            log.new Success("The element is no longer present!");
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
        }
    }

    public WebElement waitUntilElementIsInvisible(WebElement element, long startTime) {
        if ((System.currentTimeMillis() - startTime) > 15000) return element;
        try {
            wait.until(ExpectedConditions.invisibilityOf(element));
            return null;
        }
        catch (TimeoutException e) {return waitUntilElementIsInvisible(element, startTime);}
    }

    public WebElement waitUntilElementIsClickable(WebElement element, long initialTime){
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));
        if (System.currentTimeMillis()-initialTime>15000){
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
            return null;
        }
        try {if (!element.isEnabled()){waitUntilElementIsClickable(element, initialTime);}}
        catch (StaleElementReferenceException|NoSuchElementException|TimeoutException exception){
            waitUntilElementIsClickable(element, initialTime);
        }
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
        return element;
    }

    public void clickWithJS(WebElement webElement) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", webElement);
    }

    public void scrollWithJS(WebElement webElement) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView();", webElement);
    }

    public boolean elementIsDisplayed(WebElement element, long startTime) {
        if ((System.currentTimeMillis() - startTime) > 10000) return false;
        try {return element.isDisplayed();}
        catch (Exception e) {
            log.new Info(e);
            return elementIsDisplayed(element, startTime);
        }
    }

    public <T> WebElement getElement(String fieldName, Class<T> inputClass){
        return (WebElement) objectUtils.getFieldValue(fieldName, inputClass);
    }
}
