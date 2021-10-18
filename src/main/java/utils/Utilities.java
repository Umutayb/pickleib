package utils;

import com.github.webdriverextensions.WebDriverExtensionFieldDecorator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.interactions.Actions;
import com.gargoylesoftware.htmlunit.*;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;
import static resources.Colors.*;
import org.openqa.selenium.*;
import org.junit.Assert;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.driver.Driver;
import java.util.List;
import java.io.File;
import java.util.concurrent.TimeUnit;

public abstract class Utilities extends Driver { //TODO: Write a method which creates a unique css selector for elements

    public Utilities(){PageFactory.initElements(new WebDriverExtensionFieldDecorator(driver), this);}

    Log log = LogFactory.getLog(Utilities.class);

    StringUtilities strUtils = new StringUtilities();
    NumericUtilities numeric = new NumericUtilities();

    public String getAttribute(WebElement element, String attribute){return element.getAttribute(attribute);}

    public String navigate(String url){
        try {

            log.info(GRAY+"Navigating to "+RESET+BLUE+url+RESET);

            if (!url.contains("http"))
                url = "https://"+url;

            driver.get(url);

        }catch (Exception gamma){

            Assert.fail(YELLOW+"Unable to navigate to the \""+url+"\""+RESET);
            driver.quit();

        }
        return url;
    }

    public void navigateBrowser(String direction){
        try {

            log.info(GRAY+"Navigating "+direction+RESET);

            switch (direction.toLowerCase()){
                case "forward":
                    driver.navigate().forward();
                    break;

                case "backwards":
                    driver.navigate().back();
                    break;

                default:
                    Assert.fail("No such direction was defined in -navigateBrowser- method.");
            }

        }catch (Exception gamma){

            Assert.fail(YELLOW+"Unable to navigate \""+direction+"\""+RESET);
            driver.quit();

        }
    }

    //This method clicks an element after waiting it and scrolling it to the center of the view
    public void clickElement(WebElement element){
        try {

            centerElement(waitUntilElementIsClickable(element, System.currentTimeMillis())).click();

        }catch (ElementNotFoundException e){
            Assert.fail(e.getMessage());
        }
    }

    //This method is for filling an input field, it waits for the element, scrolls to it, clears it and then fills it
    public void clearFillInput(WebElement inputElement, String inputText, Boolean verify){
        try {
            // This method clears the input field before filling it
            clearInputField(centerElement(waitUntilElementIsVisible(inputElement, System.currentTimeMillis()))).sendKeys(inputText);

            if (verify)
                Assert.assertEquals(inputElement.getAttribute("value"), inputText);

        }catch (ElementNotFoundException e){
            Assert.fail(e.getMessage());
        }

    }

    public void hoverOver(WebElement element, long startTime){
        if (System.currentTimeMillis()-startTime > 10000)
            return;
        Actions actions = new Actions(driver);
        try {
            actions.moveToElement(element)
                    .build()
                    .perform();
        }
        catch (StaleElementReferenceException stale){
            hoverOver(element, startTime);
        }
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

        }catch (ElementNotFoundException e){
            Assert.fail(e.getMessage());
            return null;
        }
    }

    //This method performs click, hold, drag and drop action on a certain element
    public void dragDropAction(WebElement element, int xOffset, int yOffset){

        centerElement(element);

        Actions builder = new Actions(driver);
        builder.moveToElement(element)
                .clickAndHold(element)
                .dragAndDropBy
                        (element, xOffset, yOffset)
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

    //This method makes the thread wait for a certain while
    public void waitFor(double seconds){
        if (seconds > 1)
            log.info(GRAY+"Waiting for "+BLUE+seconds+GRAY+" seconds"+RESET);
        try {
            Thread.sleep((long) (seconds* 1000L));
        }
        catch (InterruptedException exception){
            Assert.fail(exception.getLocalizedMessage());
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
            log.info(attribute +" : "+ attributeJSON.get(attribute));
        }
    }

    public WebElement getParentByClass(WebElement childElement, String current, String parentSelectorClass) {

        if (current == null) {
            current = "";
        }

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
        if (childTag.equals("html")) {
            return "/html[1]" + current;
        }
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

    public WebElement waitUntilElementIsVisible(WebElement element, long startTime) {
        driver.manage().timeouts().implicitlyWait(500, TimeUnit.MILLISECONDS);
        wait = new WebDriverWait(driver, 1);
        if ((System.currentTimeMillis() - startTime) > 10000){
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
            wait = new WebDriverWait(driver, 15);
            return null;
        }
        try {
            wait.until(ExpectedConditions.visibilityOf(element));
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
            wait = new WebDriverWait(driver, 15);
            return element;
        } catch (StaleElementReferenceException | TimeoutException e) {
            return waitUntilElementIsVisible(element, startTime);
        }
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
                Assert.fail("No such locator type was defined in Helper.java @verifyAbsenceOfElementLocatedBy.");
                return null;
        }

        if ((System.currentTimeMillis() - startTime) > 15000){
            Assert.fail("An element was located unexpectedly");
            return elements;
        }

        if (elements.size() > 0){
            return verifyAbsenceOfElementLocatedBy(locatorType, locator, startTime);
        }
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
                    Assert.fail("Loading animation was still present after "+(System.currentTimeMillis() - startTime)/1000+" seconds.");
                elementPresence = subDriver.findElements(By.xpath(generateXPath(element,"")));
            }
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        }
        catch (StaleElementReferenceException exception) {
            if (System.currentTimeMillis()-startTime<=15000)
                waitUntilElementIsNoLongerPresent(element, startTime);
            else
                Assert.fail("Loading animation was still present after "+(System.currentTimeMillis() - startTime)/1000+" seconds.");
        }
        catch (NoSuchElementException | IllegalArgumentException ignored){
            log.info(GRAY+"INFO: The element is no longer present!"+RESET);
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        }
    }

    public WebElement waitUntilElementIsInvisible(WebElement element, long startTime) {
        if ((System.currentTimeMillis() - startTime) > 15000)
            return element;
        try {
            wait.until(ExpectedConditions.invisibilityOf(element));
            return null;
        } catch (TimeoutException e) {
            return waitUntilElementIsInvisible(element, startTime);
        }
    }

    public WebElement waitUntilElementIsClickable(WebElement element, long startTime) {
        driver.manage().timeouts().implicitlyWait(500, TimeUnit.MILLISECONDS);
        wait = new WebDriverWait(driver, 1);
        if ((System.currentTimeMillis() - startTime) > 15000){
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
            wait = new WebDriverWait(driver, 15);
            return null;
        }
        try {
            wait.until(ExpectedConditions.elementToBeClickable(element));
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
            wait = new WebDriverWait(driver, 15);
            return element;
        } catch (StaleElementReferenceException | ElementClickInterceptedException | TimeoutException e) {
            log.info(e.getMessage());
            return waitUntilElementIsClickable(element, startTime);
        }
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
            log.info(e);
            return elementIsDisplayed(element, startTime);
        }
    }

    public void captureScreen(String specName) {
        try {
            log.info(GRAY+"Capturing page"+RESET);

            String name = specName+"#"+numeric.randomNumber(1,10000)+".jpg";
            File sourceFile = new File("Screenshots");
            File fileDestination  = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(fileDestination, new File(sourceFile, name));

            log.info(GRAY+"Screenshot saved as; "+name+" at the \"Screenshots\" file."+RESET);

        }catch (Exception gamma){
            Assert.fail(YELLOW+"Could not capture screen"+RED+"\n\t"+gamma+RESET);
            driver.quit();
        }
    }
}
