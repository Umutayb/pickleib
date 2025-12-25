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
import org.openqa.selenium.support.ui.WebDriverWait;
import pickleib.enums.Direction;
import pickleib.enums.ElementState;
import pickleib.enums.Navigation;
import pickleib.exceptions.PickleibException;
import pickleib.utilities.Utilities;
import pickleib.utilities.element.acquisition.ElementAcquisition;
import pickleib.utilities.interfaces.functions.LocateElement;
import pickleib.web.driver.PickleibWebDriver;
import utils.StringUtilities;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static utils.StringUtilities.Color.*;
import static utils.StringUtilities.*;

/**
 * A specialized utility class for Web UI automation that extends the generic {@link Utilities}.
 * <p>
 * This class provides Web-specific implementations for interactions that require direct access to
 * the {@link RemoteWebDriver} or JavaScript execution. Key features include:
 * <ul>
 * <li><b>JavaScript Injection:</b> For scrolling, clicking, and event listening.</li>
 * <li><b>Window Management:</b> Tab switching, resizing, and navigation history.</li>
 * <li><b>DOM Traversal:</b> Handling iFrames and shadow roots (implicitly via JS).</li>
 * <li><b>Browser Storage:</b> Managing Cookies and Local Storage.</li>
 * </ul>
 * </p>
 *
 * @author Umut Ay Bora
 */
public abstract class WebUtilities extends Utilities {

    /**
     * Constructor for frameworks that rely on the Singleton {@link PickleibWebDriver}.
     * <p>
     * Initializes the Scroller lambda to use {@link #centerElement(WebElement, RemoteWebDriver)}.
     * </p>
     */
    public WebUtilities() {
        super(PickleibWebDriver.get(), (element) -> centerElement(element, PickleibWebDriver.get()));
    }

    /**
     * Constructor for frameworks using a custom or transient {@link RemoteWebDriver}.
     *
     * @param driver The active driver instance.
     */
    public WebUtilities(RemoteWebDriver driver) {
        super(driver, (element) -> centerElement(element, driver));
    }

    public RemoteWebDriver driver() {
        return this.driver;
    }

    /**
     * Navigates to a specific URL, ensuring the protocol is present.
     *
     * @param url The target URL (e.g., "google.com" or "https://google.com").
     * @return The processed URL.
     * @throws PickleibException If navigation fails.
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
     * Navigates the browser history.
     *
     * @param direction {@link Navigation#backwards} or {@link Navigation#forwards}.
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
     * Iterates through a list of elements, centering each one, until an element containing the text is found.
     *
     * @param elementText The text to search for.
     * @param elements    The list of elements to check.
     * @return The matching WebElement.
     * @throws RuntimeException If the element is not found.
     */
    public WebElement scrollInList(String elementText, List<WebElement> elements) {
        log.info("Scrolling the list to element with text: " + highlighted(BLUE, elementText));
        for (WebElement element : elements) {
            centerElement(element);
            if (element.getText().contains(elementText)) {
                return element;
            }
        }
        throw new RuntimeException("Element '" + elementText + "' could not be located!");
    }

    /**
     * Scrolls the view downwards repeatedly until the locator finds a visible element.
     * <p>
     * <b>Timeout:</b> This method waits up to 5x the standard {@code elementTimeout}.
     * </p>
     *
     * @param locator A functional interface {@link LocateElement} that attempts to find the element.
     * @return The located element.
     * @throws RuntimeException If the element is not found within the extended timeout.
     */
    public WebElement scrollUntilFound(LocateElement locator) {
        log.info("Scrolling until an element is found");
        long initialTime = System.currentTimeMillis();
        do {
            try {
                WebElement element = locator.locate();
                if (element.isDisplayed()) return element;
                else throw new WebDriverException("Element is not displayed (yet)!");
            } catch (WebDriverException ignored) {
                scrollInDirection(Direction.down);
            }
        }
        while (System.currentTimeMillis() - initialTime < elementTimeout * 5);
        throw new RuntimeException("Element could not be located!");
    }

    /**
     * Scrolls the browser viewport using JavaScript.
     *
     * @param direction {@link Direction#up} or {@link Direction#down}.
     */
    public void scrollInDirection(@NotNull Direction direction) {
        log.info("Scrolling " + highlighted(BLUE, direction.name().toLowerCase()));
        String script = switch (direction) {
            case up -> "window.scrollBy(0,-window.innerHeight * 0.9)";
            case down -> "window.scrollBy(0,window.innerHeight * 0.9)";
            case left, right -> null;
        };
        driver.executeScript(script);
    }

    /**
     * Swipes upward until the specified WebElement is found or a timeout is reached.
     *
     * <p>
     * This method continuously swipes upward until the specified WebElement is found or a timeout occurs.
     * If the element is found, it is returned. If the element is not found within the specified timeout,
     * a RuntimeException is thrown.
     * </p>
     *
     * @param elementText The text of WebElement to be located.
     * @return The located WebElement.
     * @throws RuntimeException   if the element could not be located within the specified timeout.
     * @throws WebDriverException if WebDriver encounters an exception while interacting with the element.
     *                            If an exception occurs during the swipe operation, the method retries the swipe.
     *                            If the element is not found after the specified timeout, the WebDriverException is thrown.
     */
        public WebElement scrollUntilFound(String elementText) {
            log.info("Scrolling until an element with text " +
                    highlighted(BLUE, elementText) +
                    highlighted(GRAY, " is found.")
            );
            return scrollUntilFound(() -> getElementByText(elementText));
        }

    /**
     * Scrolls the view until the specified WebElement is found and visible.
     * This method continuously scrolls in the 'up' direction and attempts to locate the element
     * using the provided WebElement instance. The process is repeated until the element is found and
     * displayed or the time limit is reached.
     *
     * @param element The WebElement instance to be located.
     * @return The located WebElement if found and displayed.
     * @throws RuntimeException if the element is not found within the specified timeout.
     */
        public WebElement scrollUntilFound(WebElement element) {
            log.info("Scrolling until the element is found.");
            return scrollUntilFound(() -> element);
        }

    /**
     * Switches the driver's focus to a specific window/tab handle.
     *
     * @param handle The window handle ID. If null, switches to the next available window.
     * @return The handle of the parent window (before switching).
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
     * Switches the driver's focus to a tab based on its index.
     *
     * @param tabIndex The 0-based index of the tab.
     * @return The handle of the parent window.
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
     * Forces a click on an element using JavaScript.
     * <p>
     * Useful when elements are obscured by overlays or not interactive according to Selenium's standard checks.
     * </p>
     *
     * @param webElement The element to click.
     */
    public void clickWithJS(WebElement webElement) {
        driver.executeScript("arguments[0].click();", webElement);
    }

    /**
     * Forces an element into view using JavaScript's {@code scrollIntoView()}.
     *
     * @param webElement The element to scroll to.
     */
    public void scrollWithJS(WebElement webElement) {
        driver.executeScript("arguments[0].scrollIntoView();", webElement);
    }

    /**
     * Scrolls the element to the exact center of the viewport using JavaScript calculations.
     * <p>
     * This is often more reliable than {@code scrollIntoView} for keeping elements away from
     * sticky headers or footers.
     * </p>
     *
     *
     *
     * @param element The element to center.
     * @param driver  The driver to execute the script.
     * @return The element.
     */
    //This method scrolls an element to the center of the view
    public static WebElement centerElement(WebElement element, RemoteWebDriver driver) {
        String scrollScript = "var viewPortHeight = Math.max(document.documentElement.clientHeight, window.innerHeight || 0);"
                + "var elementTop = arguments[0].getBoundingClientRect().top;"
                + "window.scrollBy(0, elementTop-(viewPortHeight/2));";

        driver.executeScript(scrollScript, element);

        waitFor(0.3);
        return element;
    }

    /**
     * Scrolls the element to the exact center of the viewport using JavaScript calculations, using the instance driver.
     * <p>
     * This is often more reliable than {@code scrollIntoView} for keeping elements away from
     * sticky headers or footers.
     * </p>
     *
     *
     *
     * @param element The element to center.
     * @return The element.     */
    public WebElement centerElement(WebElement element) {
        return centerElement(element, driver);
    }

    /**
     * Extracts all attributes of an element and converts them to a {@link JsonObject}.
     *
     * @param element The target element.
     * @return A JsonObject containing all HTML attributes.
     */
    public JsonObject getElementJson(WebElement element) {
        String object = driver.executeScript(
                "var items = {}; " +
                        "for (index = 0; index < arguments[0].attributes.length; ++index) " +
                        "{ items[arguments[0].attributes[index].name] = arguments[0].attributes[index].value }; " +
                        "return JSON.stringify(items);",
                element
        ).toString();
        return (JsonObject) JsonParser.parseString(object);
    }

    /**
     * Extracts all attributes of an element and converts them to a {@link JSONObject}.
     *
     * @param element The target element.
     * @return A JsonObject containing all HTML attributes.
     */
    public JSONObject getElementJSON(WebElement element) {
        try {
            String object = driver.executeScript(
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
     * Locates an element by CSS, centers it, and clicks it.
     *
     * @param cssSelector The CSS selector string.
     */
    public void clickButtonByCssSelector(String cssSelector) {
        WebElement element = driver.findElement(By.cssSelector(cssSelector));
        centerElement(element);
        clickElement(element, true);
    }

    /**
     * Clicks an element located inside an iframe.
     * <p>
     * Handles the context switch to the frame and back to parent.
     * </p>
     *
     *
     *
     * @param iframe  The iframe WebElement.
     * @param element The element inside the iframe.
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
        log.info("Filling " + highlighted(BLUE, inputText));
        inputText = StringUtilities.contextCheck(inputText);
        elementIs(iframe, ElementState.displayed);
        driver.switchTo().frame(iframe);
        clearFillInput(element, inputText, true);
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
                    true
            );
        }
        driver.switchTo().parentFrame();
    }

    /**
     * Simulates a hover action (Mouse Over) on an element.
     * <p>
     * Includes a retry mechanism for stability.
     * </p>
     *
     * @param element The element to hover over.
     * @return The element.
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
     * Listens for a JavaScript event on the frontend.
     * <p>
     * Injects a script that listens for the specific {@code eventName} and logs a unique key to the console.
     * The method then reads the browser logs to verify if the event fired.

     * Ex: "dataLayerObject.listen(eventName, function(){console.warn(eventKey)});"
     * </p>
     *
     *
     *
     * @param eventName      The name of the event (e.g., "gtm.load").
     * @param listenerScript The JS code to attach the listener (must contain the placeholder for event name).
     * @return {@code true} if the event fired.
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
     *
     * @return object if the scripts yield one
     */
    public Object executeScript(String script, Object... args) {
        log.info("Executing script: " + highlighted(BLUE, script));
        return driver.executeScript(script, args);
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
     * Updates a specific cookie by deleting the old one and adding a new one with the same properties.
     *
     * @param cookieValue The new value.
     * @param cookieName  The name of the cookie.
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

    /**
     * Scrolls the container element to bring the target element into view.
     *
     * <p>
     * This method scrolls the specified container element to bring the target element into view.
     * It calculates the distance between the first element in the list and the target element,
     * then scrolls the container by that distance to ensure the target element is in view.
     * </p>
     *
     * @param container The container WebElement to be scrolled.
     * @param elements The list of WebElements containing the target element.
     * @param targetElementText The text of the target element to bring into view.
     * @return The WebElement representing the target element after scrolling.
     */
    public WebElement scrollInContainer(WebElement container, List<WebElement> elements, String targetElementText) {
        log.info("Scrolling " + targetElementText + " in view");
        WebElement targetElement = ElementAcquisition.acquireNamedElementAmongst(elements, targetElementText);
        WebElement firstElement = elements.get(0);
        double distance = Math.abs(firstElement.getLocation().getY() - targetElement.getLocation().getY());
        driver.executeScript("arguments[0].scrollBy(0, " + distance + ");", container);
        return  targetElement;
    }

    /**
     * Checks if the specified WebElement is fully in view within the current browser window.
     * <p>
     * The script calculates the element's bounding rectangle and checks if its
     * top, left, bottom, and right coordinates are within the viewport.
     * </p>
     *
     * @param element The WebElement to be checked for visibility.
     * @return {@code true} if the element is fully in view, {@code false} otherwise.
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/Element/getBoundingClientRect">getBoundingClientRect()</a>
     */
    public boolean elementIsInView(WebElement element) {
        String script = "var rect = arguments[0].getBoundingClientRect();" +
                "    return (" +
                "        rect.top >= 0 &&" +
                "        rect.left >= 0 &&" +
                "        rect.bottom <= (window.innerHeight || document.documentElement.clientHeight) &&\n" +
                "        rect.right <= (window.innerWidth || document.documentElement.clientWidth)\n" +
                "    );";

        return Boolean.parseBoolean(driver.executeScript(script, element).toString());
    }
}
