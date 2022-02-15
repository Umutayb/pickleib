package utils;

import com.github.webdriverextensions.WebDriverExtensionFieldDecorator;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import com.gargoylesoftware.htmlunit.*;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.json.simple.JSONObject;
import static resources.Colors.*;
import org.openqa.selenium.*;
import utils.driver.Driver;
import org.junit.Assert;
import java.util.List;

public abstract class WebUtilities extends Driver { //TODO: Write a method which creates a unique css selector for elements

    Printer log = new Printer(WebUtilities.class);

    public TextParser parser = new TextParser();
    public Properties properties = new Properties();
    public EmailUtilities email = new EmailUtilities();
    public FileUtilities fileUtils = new FileUtilities();
    public JsonUtilities jsonUtils = new JsonUtilities();
    public XPathUtilities xpathUtils = new XPathUtilities();
    public StringUtilities strUtils = new StringUtilities();
    public NumericUtilities numeric = new NumericUtilities();
    public TerminalUtilities terminal = new TerminalUtilities();
    public ApiUtilities apiUtils = new ApiUtilities(properties.getProperty("default.url"),properties.getProperty("default.uri"));

    public WebUtilities(){
        PageFactory.initElements(new WebDriverExtensionFieldDecorator(driver), this);
        try {properties.load(new FileReader("src/test/resources/test.properties"));}
        catch (IOException e) {e.printStackTrace();}
    }

    public String getAttribute(WebElement element, String attribute){return element.getAttribute(attribute);}

    public String navigate(String url){
        try {
            log.new Info("Navigating to "+RESET+BLUE+url);

            if (!url.contains("http"))
                url = "https://"+url;

            driver.get(url);
        }
        catch (Exception gamma){
            Assert.fail(YELLOW+"Unable to navigate to the \""+url+"\""+RESET);
            driver.quit();
        }
        return url;
    }

    public void navigateBrowser(String direction){
        try {
            log.new Info("Navigating "+direction);

            switch (direction.toLowerCase()){
                case "forward":
                    driver.navigate().forward();
                    break;

                case "backwards":
                    driver.navigate().back();
                    break;

                default:
                    Assert.fail(GRAY+"No such direction was defined in -navigateBrowser- method."+RESET);
            }
        }
        catch (Exception ignored){
            Assert.fail(YELLOW+"Unable to navigate \""+direction+"\""+RESET);
            driver.quit();
        }
    }

    //This method clicks an element after waiting it and scrolling it to the center of the view
    public void clickElement(WebElement element){
        try {centerElement(waitUntilElementIsClickable(element, System.currentTimeMillis())).click();}
        catch (ElementNotFoundException e){Assert.fail(GRAY+e.getMessage()+RESET);}
    }

    //This method is for filling an input field, it waits for the element, scrolls to it, clears it and then fills it
    public void clearFillInput(WebElement inputElement, String inputText, Boolean verify){
        try {
            // This method clears the input field before filling it
            clearInputField(centerElement(waitUntilElementIsVisible(inputElement, System.currentTimeMillis()))).sendKeys(inputText);

            if (verify)
                Assert.assertEquals(inputElement.getAttribute("value"), inputText);

        }
        catch (ElementNotFoundException e){Assert.fail(GRAY+e.getMessage()+RESET);}
    }

    public WebElement hoverOver(WebElement element, Long initialTime){
        if (System.currentTimeMillis()-initialTime > 10000)
            return null;
        centerElement(element);
        Actions actions = new Actions(driver);
        try{actions.moveToElement(element).build().perform();}
        catch (StaleElementReferenceException ignored) {hoverOver(element,initialTime);}
        return element;
    }

    public void loopAndClick(List<WebElement> list,String buttonName){clickElement(loopNMatch(list,buttonName));}

    public WebElement loopNMatch(List<WebElement> elementList, String itemText){
        for (WebElement item:elementList) {
            System.out.println(item.getText());
            if (item.getText().equalsIgnoreCase(itemText) || item.getText().contains(itemText))
                return item;
        }
        Assert.fail(GRAY+"Item could not be located!"+RESET);
        return null;
    }

    public String switchWindowHandle(String handle){
        String parentWindowHandle = driver.getWindowHandle();
        if (handle == null)
            for (String windowHandle:driver.getWindowHandles()) {
                if (!windowHandle.equalsIgnoreCase(parentWindowHandle))
                    driver = (RemoteWebDriver) driver.switchTo().window((windowHandle));
            }
        else
            driver = (RemoteWebDriver) driver.switchTo().window(handle);
        return parentWindowHandle;
    }

    //This method clicks a button with a certain text on it
    public void clickButtonWithText(String buttonText){clickElement(getElementWithText(buttonText));}

    //This method clears an input field /w style
    public WebElement clearInputField(WebElement element){
        int textLength = element.getAttribute("value").length();
        for(int i = 0; i < textLength; i++){
            element.sendKeys(Keys.BACK_SPACE);
        }
        return element;
    }

    //This method returns an element with a certain text on it
    public WebElement getElementWithText(String elementText){
        try {
            return driver.findElement(By.xpath("//*[text()='" +elementText+ "']"));
        }
        catch (ElementNotFoundException e){
            Assert.fail(GRAY+e.getMessage()+RESET);
            return null;
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
        builder.moveToElement(element, xOffset, yOffset)
                .click()
                .build()
                .perform();

    }

    public Alert getAlert(){return driver.switchTo().alert();}

    public void uploadFile(WebElement fileUploadInput, String directory, String fileName){fileUploadInput.sendKeys(directory+"/"+fileName);}

    public String combineKeys(Keys key1, Keys key2){return Keys.chord(key1,key2);}

    //This method makes the thread wait for a certain while
    public void waitFor(double seconds){
        if (seconds > 1)
            log.new Info("Waiting for "+BLUE+seconds+GRAY+" seconds");
        try {
            Thread.sleep((long) (seconds* 1000L));
        }
        catch (InterruptedException exception){
            Assert.fail(GRAY+exception.getLocalizedMessage()+RESET);
        }
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
                element);
    }

    //This method prints all the attributes of a given element
    public void printElementAttributes(WebElement element){
        JSONObject attributeJSON = new JSONObject(strUtils.str2Map(getElementObject(element).toString()));
        for (Object attribute : attributeJSON.keySet()) {
            log.new Info(attribute +" : "+ attributeJSON.get(attribute));
        }
    }

    public WebElement getParentByClass(WebElement childElement, String current, String parentSelectorClass) {

        if (current == null) {current = "";}

        String childTag = childElement.getTagName();

        if (childElement.getAttribute("class").contains(parentSelectorClass))
            return childElement;

        WebElement parentElement = childElement.findElement(By.xpath(".."));
        List<WebElement> childrenElements = parentElement.findElements(By.xpath("*"));

        int count = 0;
        for (WebElement childrenElement : childrenElements) {
            String childrenElementTag = childrenElement.getTagName();
            if (childTag.equals(childrenElementTag)) {
                count++;
            }
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
            if (childTag.equals(childrenElementTag)) {
                count++;
            }
            if (childElement.equals(childrenElement)) {
                return generateXPath(parentElement, "/" + childTag + "[" + count + "]" + current);
            }
        }
        return null;
    }

    public WebElement waitUntilElementIsVisible(WebElement element, long initialTime){
        driver.manage().timeouts().implicitlyWait(500, TimeUnit.MILLISECONDS);
        if (System.currentTimeMillis()-initialTime>15000){
            driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
            return null;
        }
        try {
            if (!element.isDisplayed()){waitUntilElementIsVisible(element, initialTime);}
        }
        catch (StaleElementReferenceException|NoSuchElementException|TimeoutException exception){
            waitUntilElementIsVisible(element, initialTime);
        }
        driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        return element;
    }

    public List<WebElement> verifyAbsenceOfElementLocatedBy(String locatorType, String locator, long startTime){

        List<WebElement> elements;

        switch (locatorType.toLowerCase()){
            case "xpath":
                elements = driver.findElements(By.xpath(locator));
                break;

            case "css":
                elements = driver.findElements(By.cssSelector(locator));
                break;

            default:
                Assert.fail(GRAY+"No such locator type was defined in Helper.java @verifyAbsenceOfElementLocatedBy."+RESET);
                return null;
        }

        if ((System.currentTimeMillis() - startTime) > 15000){
            Assert.fail(GRAY+"An element was located unexpectedly"+RESET);
            return elements;
        }
        if (elements.size() > 0){return verifyAbsenceOfElementLocatedBy(locatorType, locator, startTime);}
        else
            return null;
    }

    public void waitUntilElementIsNoLongerPresent(WebElement element, long startTime){
        try {
            WebDriver subDriver = driver;
            subDriver.manage().timeouts().implicitlyWait(500, TimeUnit.MILLISECONDS);
            List<WebElement> elementPresence = driver.findElements(By.xpath(generateXPath(element,"")));
            while (elementPresence.size()>0){
                if ((System.currentTimeMillis() - startTime) > 15000)
                    Assert.fail(GRAY+"Element was still present after "+(System.currentTimeMillis() - startTime)/1000+" seconds."+RESET);
                elementPresence = subDriver.findElements(By.xpath(generateXPath(element,"")));
            }
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        }
        catch (StaleElementReferenceException exception) {
            if (System.currentTimeMillis()-startTime<=15000)
                waitUntilElementIsNoLongerPresent(element, startTime);
            else
                Assert.fail(GRAY+"Element was still present after "+(System.currentTimeMillis() - startTime)/1000+" seconds."+RESET);
        }
        catch (NoSuchElementException | IllegalArgumentException ignored){
            log.new Success("The element is no longer present!");
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        }
    }

    public WebElement waitUntilElementIsInvisible(WebElement element, long startTime) {
        if ((System.currentTimeMillis() - startTime) > 15000)
            return element;
        try {
            wait.until(ExpectedConditions.invisibilityOf(element));
            return null;
        }
        catch (TimeoutException e) {return waitUntilElementIsInvisible(element, startTime);}
    }

    public WebElement waitUntilElementIsClickable(WebElement element, long initialTime){
        driver.manage().timeouts().implicitlyWait(500, TimeUnit.MILLISECONDS);
        if (System.currentTimeMillis()-initialTime>15000){
            driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
            return null;
        }
        try {
            if (!element.isEnabled()){waitUntilElementIsClickable(element, initialTime);}
        }
        catch (StaleElementReferenceException|NoSuchElementException|TimeoutException exception){
            waitUntilElementIsClickable(element, initialTime);
        }
        driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        return element;
    }

    public void clickWithJS(WebElement webElement) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", webElement);
    }

    public void scrollWithJS(WebElement webElement) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView();", webElement);
    }

    public boolean elementIsDisplayed(WebElement element, long startTime) {
        if ((System.currentTimeMillis() - startTime) > 10000)
            return false;
        try {
            return element.isDisplayed();
        } catch (Exception e) {
            log.new Info(e);
            return elementIsDisplayed(element, startTime);
        }
    }
}
