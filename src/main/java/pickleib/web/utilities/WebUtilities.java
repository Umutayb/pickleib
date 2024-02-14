package pickleib.web.utilities;

import collections.Bundle;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.failsafe.internal.util.Assert;
import io.appium.java_client.functions.ExpectedCondition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.*;
import org.openqa.selenium.html5.LocalStorage;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.remote.RemoteExecuteMethod;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.html5.RemoteWebStorage;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import pickleib.enums.ElementState;
import pickleib.enums.Navigation;
import pickleib.exceptions.PickleibException;
import pickleib.utilities.Utilities;
import pickleib.web.driver.PickleibWebDriver;
import utils.StringUtilities;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static utils.StringUtilities.Color.*;
import static utils.StringUtilities.*;

public abstract class WebUtilities extends Utilities {

    /**
     * WebUtilities for frameworks that use the Pickleib driver
     */
    public WebUtilities() {
        super(PickleibWebDriver.get());
        this.driver = PickleibWebDriver.get();
    }

    /**
     * WebUtilities for frameworks that do not use the Pickleib driver
     */
    public WebUtilities(RemoteWebDriver driver) {
        super(driver);
    }

    public RemoteWebDriver driver() {
        return this.driver;
    }

    public FluentWait<RemoteWebDriver> driverWait() {
        return PickleibWebDriver.getWait();
    }

    /**
     * Clicks an {element} with optional scrolling after waiting for its state to be enabled.
     *
     * @param element     target element
     * @param scroll      scrolls if true
     */
    public void clickElement(WebElement element, boolean scroll) {
        clickElement(
                element,
                (targetElement) -> {
                    if (scroll) this.centerElement(targetElement).click();
                    else targetElement.click();
                }
        );
    }

    /**
     * Clicks an {element} after waiting for its state to be enabled.
     *
     * @param element     target element
     */
    public void clickElement(WebElement element) {
        super.clickElement(
                element,
                WebElement::click
        );
    }

    /**
     * Clicks an element if its present (in enabled state)
     *
     * @param element target element
     * @param scroll  The {@code scroll} to be used for scrolling.
     */
    public void clickIfPresent(WebElement element, Boolean scroll) {
        try {
            clickElement(element, scroll);
        } catch (WebDriverException exception) {
            log.warning(exception.getMessage());
        }
    }

    /**
     * Clears and fills a given input
     *
     * @param inputElement target input element
     * @param inputText    input text
     * @param verify       verifies the input text value equals to an expected text if true
     */
    public void clearFillInput(WebElement inputElement, String inputText, boolean verify) {
        fillInputElement(inputElement, inputText, null, verify);
    }

    /**
     * Clears and fills a given input
     *
     * @param inputElement target input element
     * @param inputText    input text
     * @param verify       verifies the input text value equals to an expected text if true
     */
    public void clearFillInput(WebElement inputElement, String inputText, boolean scroll, boolean verify) {
        if (scroll) fillInputElement(inputElement, inputText, this::centerElement, verify);
        else fillInputElement(inputElement, inputText, null, verify);
    }

    /**
     * Navigates to a given url
     *
     * @param url target url
     */
    @SuppressWarnings("UnusedReturnValue")
    public String navigate(String url) {
        try {
            log.info("Navigating to " + highlighted(BLUE, url));

            if (!url.contains("http")) url = "https://" + url;

            driver.get(url);
        } catch (Exception gamma) {
            driver.quit();
            throw new PickleibException("Unable to navigate to the \"" + StringUtilities.highlighted(YELLOW, url) + "\"");
        }
        return url;
    }

    /**
     * Sets the window size
     *
     * @param width  windows width
     * @param height windows height
     */
    public void setWindowSize(Integer width, Integer height) {
        driver.manage().window().setSize(new Dimension(width, height));
    }

    /**
     * Navigates browsers in a given direction
     *
     * @param direction backwards or forwards
     */
    public void navigateBrowser(Navigation direction) {
        try {
            log.info("Navigating " + StringUtilities.highlighted(BLUE, direction.name()));

            switch (direction) {
                case forwards -> driver.navigate().forward();
                case backwards -> driver.navigate().back();
                default -> throw new EnumConstantNotPresentException(Navigation.class, direction.name());
            }
        } catch (Exception e) {
            throw new PickleibException("Unable to navigate browser \"" + StringUtilities.highlighted(YELLOW, direction.name()) + "\" due to: " + e);
        }
    }

    /**
     * Scrolls through a list of elements until an element containing a given text is found
     *
     * @param list        target element list
     * @param elementText target element text
     */
    public void scrollInContainer(List<WebElement> list, String elementText) {
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
    public String switchWindowByHandle(@Nullable String handle) {
        log.info("Switching to the next tab");
        String parentWindowHandle = driver.getWindowHandle();
        if (handle == null)
            for (String windowHandle : driver.getWindowHandles()) {
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
    public String switchWindowByIndex(Integer tabIndex) {
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
    public void verifyUrlContains(String url) {
        Assert.isTrue(driver.getCurrentUrl().contains(StringUtilities.contextCheck(url)),
                "The url does not contain '" + url + "'! -> " + driver.getCurrentUrl()
        );
    }

    /**
     * Verifies the current url equals to given url
     *
     * @param url target url
     */
    public void verifyCurrentUrl(String url) {
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
    public void verifyPageTitle(String pageTitle) {
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
    public WebElement centerElement(WebElement element) {
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
    public JsonObject getElementJson(WebElement element) {
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
    public JSONObject getElementJSON(WebElement element) {
        try {
            String object = ((JavascriptExecutor) driver).executeScript(
                    "var items = {}; " +
                            "for (index = 0; index < arguments[0].attributes.length; ++index) " +
                            "{ items[arguments[0].attributes[index].name] = arguments[0].attributes[index].value }; " +
                            "return JSON.stringify(items);",
                    element
            ).toString();
            return (JSONObject) new JSONParser().parse(object);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Prints all the attributes of a given element
     *
     * @param element target element
     */
    public void printElementAttributes(WebElement element) {
        JSONObject attributeJSON = getElementJSON(element);
        for (Object attribute : attributeJSON.keySet())
            log.info(attribute + " : " + attributeJSON.get(attribute));
    }

    /**
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
     * Click on a button that contains {button text} text
     * It does not scroll by default.
     *
     * @param buttonText target button text
     */
    public void clickByText(String buttonText) {
        log.info("Clicking button by its text " + highlighted(BLUE, buttonText));
        WebElement element = getElementByText(buttonText);
        clickElement(element, this::centerElement);
    }

    /**
     * Clicks on a button that contains {button text} text
     * It does not scroll by default.
     *
     * @param buttonText target button text
     * @param scroll     The {scroll} to be used for scrolling.
     */
    public void clickButtonByItsText(String buttonText, boolean scroll) {
        log.info("Clicking button by its text " + highlighted(BLUE, buttonText));
        WebElement element = getElementByText(buttonText);
        clickElement(element, scroll);
    }

    /**
     * Click iFrame element {element name} in {iframe name} on the {page name}
     *
     * @param iframe  target iframe
     * @param element target element
     */
    public void clickIframeButton(WebElement iframe, WebElement element) {
        driver.switchTo().frame(iframe);
        centerElement(element);
        clickElement(element);
        driver.switchTo().parentFrame();
    }

    /**
     * Fill iFrame element {element name} of {iframe name} on the {page name} with text: {input text}
     *
     * @param iframe    target iframe
     * @param element   target element
     * @param inputText input text
     */
    public void fillIframeInputElement(
            WebElement iframe,
            WebElement element,
            String inputText) {
        inputText = StringUtilities.contextCheck(inputText);
        elementIs(iframe, ElementState.displayed);
        driver.switchTo().frame(iframe);
        clearFillInput(element, inputText, this::centerElement, true);
        driver.switchTo().parentFrame();
    }

    /**
     * Fill {iframe name} iframe form input on the {page name}
     *
     * @param bundles  list of bundles where input element, input name and input texts are stored
     * @param iFrame   target element
     * @param pageName specified page instance name
     */
    public void fillIframeForm(
            List<Bundle<WebElement, String, String>> bundles,
            WebElement iFrame,
            String pageName) {
        for (Bundle<WebElement, String, String> bundle : bundles) {
            log.info("Filling " +
                    highlighted(BLUE, bundle.theta()) +
                    highlighted(GRAY, " on the ") +
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
    public WebElement hoverOver(WebElement element) {
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
            } catch (WebDriverException webDriverException) {
                if (counter == 0) {
                    log.warning("Iterating... (" + webDriverException.getClass().getName() + ")");
                    caughtException = webDriverException.getClass().getName();
                    counter++;
                } else if (!webDriverException.getClass().getName().equals(caughtException)) {
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
     * Navigate to url: {url}
     *
     * @param url target url
     */
    public void getUrl(String url) {
        url = contextCheck(url);
        driver.get(url);
    }

    /**
     * Checks if an event was fired
     * Create a custom script to listen for an event by generating a unique event key and catches this key in the console
     * Ex: "dataLayerObject.listen(eventName, function(){console.warn(eventKey)});"
     *
     * @param eventName      event name of the event that is expected to be fired
     * @param listenerScript script for calling the listener, ex: "dataLayerObject.listen( eventName );"
     * @return true if the specified event was fired.
     */
    public boolean isEventFired(String eventName, String listenerScript) {
        log.info("Listening to '" + eventName + "' event");
        String eventKey = generateRandomString(eventName + "#", 6, false, true);
        listenerScript = listenerScript.replace(eventName, "'" + eventName + "', function(){console.warn('" + eventKey + "')}");
        executeScript(listenerScript);
        LogEntries logs = driver.manage().logs().get(LogType.BROWSER);
        for (LogEntry entry : logs.getAll())
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
     * @param eventKey       key that is meant to be caught from the console in case the event fires
     * @param listenerScript script for calling the listener, ex: "dataLayerObject.listen('page.info', function(){console.warn(eventKey)});"
     * @return true if the specified event was fired.
     */
    public boolean isEventFiredByScript(String eventKey, String listenerScript) {
        log.info("Listening to '" + markup(BLUE, eventKey) + "' event");
        executeScript(listenerScript);
        LogEntries logs = driver.manage().logs().get(LogType.BROWSER);
        for (LogEntry entry : logs.getAll()) if (entry.toString().contains(eventKey)) return true;
        return false;
    }

    /**
     * Executes a JS script and returns the responding object
     *
     * @param script script that is to be executed
     * @return object if the scripts yield one
     */
    public Object executeScript(String script) {
        log.info("Executing script: " + highlighted(BLUE, script));
        return ((JavascriptExecutor) driver).executeScript(script);
    }

    /**
     * Adds given values to the local storage
     *
     * @param form Map(String, String)
     */
    public void addValuesToLocalStorage(Map<String, String> form) {
        for (String valueKey : form.keySet()) {
            RemoteExecuteMethod executeMethod = new RemoteExecuteMethod(driver);
            RemoteWebStorage webStorage = new RemoteWebStorage(executeMethod);
            LocalStorage storage = webStorage.getLocalStorage();
            storage.setItem(valueKey, contextCheck(form.get(valueKey)));
        }
    }

    /**
     * Adds given cookies
     *
     * @param cookies Map(String, String)
     */
    public void addCookies(Map<String, String> cookies) {
        for (String cookieName : cookies.keySet()) {
            Cookie cookie = new Cookie(cookieName, contextCheck(cookies.get(cookieName)));
            driver.manage().addCookie(cookie);
        }
    }

    /**
     * Updates given cookies
     *
     * @param cookieValue
     * @param cookieName
     */
    public void updateCookies(String cookieValue, String cookieName) {
        Cookie cookie = driver.manage().getCookieNamed(cookieName);
        driver.manage().deleteCookie(cookie);
        driver.manage().addCookie(
                new Cookie.Builder(cookie.getName(), cookieValue)
                        .domain(cookie.getDomain())
                        .expiresOn(cookie.getExpiry())
                        .path(cookie.getPath())
                        .isSecure(cookie.isSecure())
                        .build()
        );
    }

    /**
     * Deletes all cookies
     */
    public void deleteAllCookies() {
        driver.manage().deleteAllCookies();
    }

    /**
     * Switches to present alert
     *
     * @return returns the alert
     */
    public Alert getAlert() {
        return driver.switchTo().alert();
    }

    /**
     * Uploads a given file
     *
     * @param fileUploadInput upload element
     * @param directory       absolute file directory (excluding the file name)
     * @param fileName        file name (including a file extension)
     */
    public void uploadFile(@NotNull WebElement fileUploadInput, String directory, String fileName) {
        fileUploadInput.sendKeys(directory + "/" + fileName);
    }

    /**
     * Waits actively for the page to load up to 10 seconds
     */
    protected void waitUntilLoads(int waitingTime) {
        long startTime = System.currentTimeMillis();
        String url = driver.getCurrentUrl();
        log.info("Waiting for page to be loaded -> " + markup(BLUE, url));

        ExpectedCondition<Boolean> pageLoadCondition = driverLoad ->
        {
            assert driverLoad != null;
            return ((JavascriptExecutor) driverLoad).executeScript("return document.readyState").equals("complete");
        };

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(waitingTime));
        wait.until(pageLoadCondition);
        long elapsedTime = System.currentTimeMillis() - startTime;
        int elapsedTimeSeconds = (int) ((double) elapsedTime / 1000);
        log.info("The page is loaded in " + elapsedTimeSeconds + " second(s)");
    }
}
