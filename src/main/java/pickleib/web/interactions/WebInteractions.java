package pickleib.web.interactions;

import context.ContextStore;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import pickleib.driver.DriverFactory;
import pickleib.enums.Direction;
import pickleib.enums.ElementState;
import pickleib.enums.Navigation;
import pickleib.utilities.element.ElementInteractions;
import pickleib.utilities.screenshot.ScreenCaptureUtility;
import pickleib.web.driver.PickleibWebDriver;
import pickleib.web.utilities.WebUtilities;
import records.Bundle;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static utils.StringUtilities.Color.*;

@SuppressWarnings("unused")
public class WebInteractions extends WebUtilities {

    public ElementInteractions interact;
    protected RemoteWebDriver driver;
    protected WebDriverWait wait;

    public WebInteractions(RemoteWebDriver driver, WebDriverWait wait){
        super(driver);
        this.driver = driver;
        this.wait = wait;
        interact = new ElementInteractions(
                driver,
                wait,
                DriverFactory.DriverType.Web
        );
    }

    public WebInteractions(){
        super(PickleibWebDriver.driver);
        this.driver = PickleibWebDriver.driver;
        this.wait = PickleibWebDriver.wait;
        interact = new ElementInteractions(
                driver,
                DriverFactory.DriverType.Web
        );
    }

    /**
     *
     * Center the {element name} on the {page name}
     *
     * @param element target element
     * @param elementName target element name
     * @param pageName specified page instance name
     */
    public void center(WebElement element, String elementName, String pageName){
        log.info("Centering " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY," on ") +
                highlighted(BLUE, pageName)
        );
        center(element);
    }

    /**
     * Center a given element
     *
     * @param element target element
     * @return the given element
     */
    public WebElement center(WebElement element){
        centerElement(element);
        return element;
    }

    private final ScreenCaptureUtility capture = new ScreenCaptureUtility();

    /**
     *
     * Navigate to url: {url}
     *
     * @param url target url
     */
    public void getUrl(String url) {
        url = strUtils.contextCheck(url);
        driver.get(url);
    }

    /**
     *
     * Go to the {page} page
     *
     * @param page target page
     */
    public void toPage(String page){
        String url = driver.getCurrentUrl();
        String pageUrl = url + page;
        navigate(pageUrl);
    }

    /**
     *
     * Switch to the next tab
     *
     */
    public void switchToNextTab() {
        String parentHandle = switchWindowByHandle(null);
        ContextStore.put("parentHandle", parentHandle);
    }

    /**
     *
     * Switch to a specified parent tab
     *
     */
    public void switchToParentTab() {
        switchWindowByHandle(ContextStore.get("parentHandle").toString());
    }

    /**
     *
     * Switch to the tab with handle: {handle}
     * Switches a specified tab by tab handle
     *
     * @param handle target a tab handle
     */
    public void switchToTabByHandle(String handle) {
        handle = strUtils.contextCheck(handle);
        String parentHandle = switchWindowByHandle(handle);
        ContextStore.put("parentHandle", parentHandle);
    }

    /**
     *
     * Switch to the tab number {tab index}
     * Switches tab by index
     *
     * @param handle target tab index
     */
    public void switchToTabByIndex(Integer handle) {
        String parentHandle = switchWindowByIndex(handle);
        ContextStore.put("parentHandle", parentHandle);
    }

    /**
     * Get HTML at {htmlPath}
     * Acquires the HTML from a given directory
     *
     * @param htmlPath target directory
     */
    public void getHTML(String htmlPath) {
        htmlPath = strUtils.contextCheck(htmlPath);
        log.info("Navigating to the email @" + htmlPath);
        driver.get(htmlPath);
    }

    /**
     *
     * Set window width and height as {width} and {height}
     *
     * @param width target width
     * @param height target height
     */
    public void setFrameSize(Integer width, Integer height) {setWindowSize(width,height);}

    /**
     * Refreshes the page
     */
    public void refresh() {refreshThePage();}

    /**
     *
     * Navigate browser in {direction} direction
     *
     * @param direction target direction (backwards or forwards)
     */
    public void browserNavigate(Navigation direction) {navigateBrowser(direction);}

    /**
     *
     * Click button includes {button text} text with css locator
     *
     * @param cssSelector target text
     */
    public void clickByCssSelector(String cssSelector) {
        clickButtonByCssSelector(cssSelector);
    }

    /**
     *
     * Scroll {direction}
     *
     * @param direction target direction (up or down)
     */
    public void scrollInDirection(Direction direction){scroll(direction);}

    /**
     *
     * Perform a JS click on an element {element name} on the {page name}
     *
     * @param element target element
     * @param elementName target element name
     * @param pageName specified page instance name
     */
    public void performJSClick(WebElement element, String elementName, String pageName){
        log.info("Clicking " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName)
        );
        clickWithJS(centerElement(element));
    }

    /**
     *
     * Click iFrame element {element name} in {iframe name} on the {page name}
     *
     * @param iframe target iframe
     * @param element target element
     * @param elementName target element name
     * @param iframeName target iframe name
     * @param pageName specified page instance name
     */
    public void clickIframeElement(WebElement iframe, WebElement element, String elementName, String iframeName, String pageName){
        log.info("Clicking i-frame element " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName)
        );
        clickIframeButton(iframe, element);
    }

    /**
     *
     * Fill iFrame element {element name} of {iframe name} on the {page name} with text: {input text}
     *
     * @param iframe target iframe
     * @param element target element
     * @param inputName target element name
     * @param pageName specified page instance name
     * @param inputText input text
     */
    public void fillIframeInput(
            WebElement iframe,
            WebElement element,
            String inputName,
            String pageName,
            String inputText){
        log.info("Filling " +
                highlighted(BLUE, inputName) +
                highlighted(GRAY," i-frame element input on the ") +
                highlighted(BLUE, pageName) +
                highlighted(GRAY, " with the text: ") +
                highlighted(BLUE, inputText)
        );
        fillIframeInputElement(iframe, element, inputText);
    }

    /**
     *
     * Fill {iframe name} iframe form input on the {page name}
     *
     * @param bundles list of bundles where input element, input name and input texts are stored
     * @param iFrame target element
     * @param iframeName target iframe name
     * @param pageName specified page instance name
     */
    public void fillFormIframe(
            List<Bundle<WebElement, String, String>> bundles,
            WebElement iFrame,
            String iframeName,
            String pageName){
        fillIframeForm(
                bundles,
                iFrame,
                iframeName,
                pageName
        );
    }

    /**
     *
     * Execute JS command: {script}
     *
     * @param script JS script
     */
    public void executeJSCommand(String script) {
        executeScript(script);
    }

    /**
     *
     * Listen to {event name} event and print {specified script} object
     * example -> listenerScript = "_ddm.listen(" + eventName + ");";
     *
     * @param eventName target event name
     * @param objectScript object script
     */
    //@Given("Listen to {} event & print {} object")
    public void listenGetAndPrintObject(String listenerScript, String eventName, String objectScript)  {
        objectScript = "return " + objectScript;
        if (isEventFired(eventName, listenerScript)) {
            Object object = executeScript(objectScript);
            log.info(object.toString());
        }
    }

    /**
     *
     * Listen to {event name} event and verify value of {node source} node is {expected value}
     * example -> listenerScript = "_ddm.listen(" + eventName + ");";
     *
     * @param eventName evet name
     * @param nodeSource node source
     * @param expectedValue expected value
     */
    public void listenGetAndVerifyObject(String listenerScript, String eventName, String nodeSource, String expectedValue)  {
        log.info("Verifying value of '" + nodeSource + "' node");
        String nodeScript = "return " + nodeSource;
        if (isEventFired(eventName, listenerScript)) {
            log.info("Verifying value of '" + highlighted(BLUE, nodeSource) + highlighted(GRAY, "' node"));
            Object object = executeScript(nodeScript);

            Pattern sourcePattern = Pattern.compile(expectedValue);
            Matcher nodeValueMatcher = sourcePattern.matcher(object.toString());

            Assert.assertTrue("Node values do not match! Expected: " + expectedValue + ", Found: " + object, nodeValueMatcher.find());
            log.success("Value of '" + nodeSource + "' is verified to be '" + object + "'");
        }
        else log.warning("'" + eventName + "' event is not fired!");
    }

    /**
     *
     * Listen to {event name} event and verify the values of the following nodes
     * example -> listenerScript = "_ddm.listen(" + eventName + ");";
     *
     * @param eventName event name
     * @param nodeList target node list
     */
    public void listenGetAndVerifyObject(String listenerScript, String eventName, List<Map<String, String>> nodeList)  {
        if (isEventFired(eventName, listenerScript)) {
            for (Map<String, String> nodeMap:nodeList) {
                String nodeSource = nodeMap.get("Node Source");
                String nodeValue = nodeMap.get("Node Value");

                log.info("Verifying value of '" + highlighted(BLUE, nodeSource) + highlighted(GRAY, "' node"));
                String nodeScript = "return " + nodeSource;
                Object object = executeScript(nodeScript);

                Pattern sourcePattern = Pattern.compile(nodeValue);
                Matcher nodeValueMatcher = sourcePattern.matcher(object.toString());

                Assert.assertTrue("Node values do not match! Expected: " + nodeValue + ", Found: " + object, nodeValueMatcher.find());
                log.success("Value of '" + nodeSource + "' is verified to be '" + object + "'");
            }
        }
        else throw new RuntimeException("'" + eventName + "' event is not fired!");
    }
}