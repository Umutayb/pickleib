package pickleib.web.utilities;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.failsafe.internal.util.Assert;
import org.jetbrains.annotations.Nullable;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.DefaultFieldDecorator;
import pickleib.enums.ElementState;
import pickleib.enums.Navigation;
import pickleib.exceptions.PickleibException;
import pickleib.utilities.Utilities;
import pickleib.web.driver.PickleibWebDriver;
import collections.Bundle;
import utils.StringUtilities;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import static utils.StringUtilities.Color.*;

public abstract class WebUtilities extends Utilities {

    /**
     * WebUtilities for frameworks that use the Pickleib driver
     *
     */
    protected WebUtilities(){
        super(PickleibWebDriver.driver);
        PageFactory.initElements(driver, this);
    }

    /**
     * WebUtilities for frameworks that do not use the Pickleib driver
     *
     */
    protected WebUtilities(RemoteWebDriver driver){
        super(driver);
        PageFactory.initElements(driver, this);
    }

    /**
     * WebUtilities for frameworks with custom field decorator that use the Pickleib driver
     *
     */
    protected <CustomFieldDecorator extends DefaultFieldDecorator> WebUtilities(CustomFieldDecorator fieldDecorator){
        super(PickleibWebDriver.driver);
        PageFactory.initElements(fieldDecorator, this);
    }

    /**
     * WebUtilities for frameworks with custom field decorator that do not use the Pickleib driver
     *
     */
    protected <CustomFieldDecorator extends DefaultFieldDecorator> WebUtilities(
            CustomFieldDecorator fieldDecorator,
            RemoteWebDriver driver
    ){
        super(driver);
        PageFactory.initElements(fieldDecorator, this);
    }

    /**
     * Clicks the specified {@code element} with retry mechanism and optional scrolling.
     *
     * <p>
     * This method attempts to click the given {@code element} with a retry mechanism.
     * It uses an implicit wait of 500 milliseconds during the retry attempts.
     * The method supports an optional {@code scroller} for scrolling before clicking the element.
     * If the {@code scroller} is provided, it scrolls towards the specified location before clicking.
     * </p>
     *
     * <p>
     * The method logs warning messages during the iteration process, indicating WebDriver exceptions.
     * After the maximum time specified by {@code elementTimeout}, if the element is still not clickable,
     * a {@code PickleibException} is thrown, including the last caught WebDriver exception.
     * </p>
     *
     * @param element   The target {@code WebElement} to be clicked with retry mechanism.
     * @throws PickleibException If the element is not clickable after the retry attempts, a {@code PickleibException} is thrown
     *                          with the last caught WebDriver exception.
     */
    public void clickElement(WebElement element, Boolean scroll){
        if (scroll) clickElement(element, this::centerElement);
        else clickElement(element);
    }

    /**
     * Clears and fills a given input
     *
     * @param inputElement target input element
     * @param inputText input text
     * @param verify verifies the input text value equals to an expected text if true
     */
    protected void clearFillInput(WebElement inputElement, String inputText, boolean verify){
        fillInputElement(inputElement, inputText, null, verify);
    }

    /**
     * Clears and fills a given input
     *
     * @param inputElement target input element
     * @param inputText input text
     * @param verify verifies the input text value equals to an expected text if true
     */
    protected void clearFillInput(WebElement inputElement, String inputText, boolean scroll, boolean verify){
        if (scroll) fillInputElement(inputElement, inputText, this::centerElement, verify);
        else fillInputElement(inputElement, inputText, null, verify);
    }

    /**
     * Navigates to a given url
     *
     * @param url target url
     */
    @SuppressWarnings("UnusedReturnValue")
    public String navigate(String url){
        try {
            log.info("Navigating to " + highlighted(BLUE, url));

            if (!url.contains("http")) url = "https://"+url;

            driver.get(url);
        }
        catch (Exception gamma){
            driver.quit();
            throw new PickleibException("Unable to navigate to the \"" + StringUtilities.highlighted(YELLOW, url) + "\"");
        }
        return url;
    }

    /**
     * Sets the window size
     *
     * @param width windows width
     * @param height windows height
     */
    public void setWindowSize(Integer width, Integer height) {
        driver.manage().window().setSize(new Dimension(width,height));
    }

    /**
     * Navigates browsers in a given direction
     *
     * @param direction backwards or forwards
     */
    public void navigateBrowser(Navigation direction){
        try {
            log.info("Navigating " + StringUtilities.highlighted(BLUE, direction.name()));

            switch (direction) {
                case forwards -> driver.navigate().forward();
                case backwards -> driver.navigate().back();
                default -> throw new EnumConstantNotPresentException(Navigation.class, direction.name());
            }
        }
        catch (Exception e){
            throw new PickleibException("Unable to navigate browser \"" + StringUtilities.highlighted(YELLOW, direction.name())+"\" due to: " + e);
        }
    }

    /**
     * Scrolls through a list of elements until an element containing a given text is found
     *
     * @param list target element list
     * @param elementText target element text
     */
    public void scrollInContainer(List<WebElement> list, String elementText){
        for (WebElement element : list) {
            scrollWithJS(element);
            if (element.getText().contains(elementText)) {
                break;
            }
        }
    }

    /**
     * Switches driver focus by using a tab handle
     *
     * @param handle target tab/window
     */
    public String switchWindowByHandle(@Nullable String handle){
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
    public String switchWindowByIndex(Integer tabIndex){
        log.info("Switching the tab with the window index: " + tabIndex);
        String parentWindowHandle = driver.getWindowHandle();
        List<String> handles = new ArrayList<>(driver.getWindowHandles());
        String handle = handles.get(tabIndex);
        driver = (RemoteWebDriver) driver.switchTo().window(handle);
        return parentWindowHandle;
    }

    /**
     * Verifies the current url contains the given url
     *
     * @param url target url
     */
    public void verifyUrlContains(String url){
        Assert.isTrue(driver.getCurrentUrl().contains(StringUtilities.contextCheck(url)),
                "The url does not contain '" + url + "'! -> " + driver.getCurrentUrl()
        );
    }

    /**
     * Verifies the current url equals to given url
     *
     * @param url target url
     */
    public void verifyCurrentUrl(String url){
        Assert.isTrue(driver.getCurrentUrl().equalsIgnoreCase(StringUtilities.contextCheck(url)),
                "The url does not match with '" + url + "'! -> " + driver.getCurrentUrl()
        );
    }

    /**
     * Verifies the given page title
     *
     * @param pageTitle target page
     */
    //This method verifies the page title
    public void verifyPageTitle(String pageTitle){
        Assert.isTrue(driver.getTitle().contains(StringUtilities.contextCheck(pageTitle)),
                "The page title does not contain '" + pageTitle + "'! -> " + driver.getTitle()
        );
    }

    /**
     * Click an element into view by using javascript
     *
     * @param webElement element that gets clicked
     */
    public void clickWithJS(WebElement webElement) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", webElement);
    }

    /**
     * Scrolls element into view by using javascript
     *
     * @param webElement element that gets scrolled into the view
     */
    public void scrollWithJS(WebElement webElement) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView();", webElement);
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
     * Transform a given element to a JsonObject using javascript and JsonParser
     *
     * @param element target element
     * @return returns an object with the attributes of a given element
     */
    //This method returns all the attributes of an element as an object
    protected JsonObject getElementJson(WebElement element){
        String object = ((JavascriptExecutor) driver).executeScript(
                "var items = {}; " +
                        "for (index = 0; index < arguments[0].attributes.length; ++index) " +
                        "{ items[arguments[0].attributes[index].name] = arguments[0].attributes[index].value }; " +
                        "return JSON.stringify(items);",
                element
        ).toString();
        return (JsonObject) JsonParser.parseString(object);
    }

    /**
     * Transform a given element to a JSONObject using javascript and JSONParser
     *
     * @param element target element
     * @return returns an object with the attributes of a given element
     */
    protected JSONObject getElementJSON(WebElement element){
        try {
            String object = ((JavascriptExecutor) driver).executeScript(
                    "var items = {}; " +
                            "for (index = 0; index < arguments[0].attributes.length; ++index) " +
                            "{ items[arguments[0].attributes[index].name] = arguments[0].attributes[index].value }; " +
                            "return JSON.stringify(items);",
                    element
            ).toString();
            return (JSONObject) new JSONParser().parse(object);
        }
        catch (ParseException e) {throw new RuntimeException(e);}
    }

    /**
     * Prints all the attributes of a given element
     *
     * @param element target element
     */
    protected void printElementAttributes(WebElement element){
        JSONObject attributeJSON = getElementJSON(element);
        for (Object attribute : attributeJSON.keySet())
            log.info(attribute +" : "+ attributeJSON.get(attribute));
    }

    /**
     *
     * Click button includes {button text} text with css locator
     *
     * @param cssSelector target text
     */
    public void clickButtonByCssSelector(String cssSelector) {
        WebElement element = driver.findElement(By.cssSelector(cssSelector));
        centerElement(element);
        clickElement(element, true);
    }

    /**
     *
     * Click iFrame element {element name} in {iframe name} on the {page name}
     *
     * @param iframe target iframe
     * @param element target element
     */
    public void clickIframeButton(WebElement iframe, WebElement element){
        driver.switchTo().frame(iframe);
        centerElement(element);
        clickElement(element);
        driver.switchTo().parentFrame();
    }

    /**
     *
     * Fill iFrame element {element name} of {iframe name} on the {page name} with text: {input text}
     *
     * @param iframe target iframe
     * @param element target element
     * @param inputText input text
     */
    public void fillIframeInputElement(
            WebElement iframe,
            WebElement element,
            String inputText){
        inputText = StringUtilities.contextCheck(inputText);
        elementIs(iframe, ElementState.displayed);
        driver.switchTo().frame(iframe);
        clearFillInput(element, inputText, this::centerElement,true);
        driver.switchTo().parentFrame();
    }

    /**
     *
     * Fill {iframe name} iframe form input on the {page name}
     *
     * @param bundles list of bundles where input element, input name and input texts are stored
     * @param iFrame target element
     * @param pageName specified page instance name
     */
    public void fillIframeForm(
            List<Bundle<WebElement, String, String>> bundles,
            WebElement iFrame,
            String pageName){
        for (Bundle<WebElement, String, String> bundle : bundles) {
            log.info("Filling " +
                    highlighted(BLUE, bundle.theta()) +
                    highlighted(GRAY," on the ") +
                    highlighted(BLUE, pageName) +
                    highlighted(GRAY, " with the text: ") +
                    highlighted(BLUE, bundle.beta())
            );
            pageName = StringUtilities.firstLetterDeCapped(pageName);
            driver.switchTo().frame(iFrame);

            clearFillInput(
                    bundle.alpha(),
                    bundle.beta(),
                    this::centerElement,
                    true
            );
        }
        driver.switchTo().parentFrame();
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
}
