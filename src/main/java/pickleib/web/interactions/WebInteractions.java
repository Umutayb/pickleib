package pickleib.web.interactions;

import context.ContextStore;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import pickleib.driver.DriverFactory;
import pickleib.enums.Direction;
import pickleib.enums.ElementState;
import pickleib.enums.Navigation;
import pickleib.exceptions.PickleibException;
import pickleib.utilities.Interactions;
import pickleib.utilities.element.ElementInteractions;
import pickleib.utilities.screenshot.ScreenCaptureUtility;
import pickleib.web.driver.PickleibWebDriver;
import pickleib.web.utilities.WebUtilities;
import collections.Bundle;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static utils.StringUtilities.Color.*;

@SuppressWarnings("unused")
public class WebInteractions extends WebUtilities implements Interactions {

    public ElementInteractions interact;
    protected WebDriverWait wait;
    boolean scroll = false;

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

    public boolean isScrolling() {
        return scroll;
    }

    public void setScroll(boolean scroll) {
        this.scroll = scroll;
    }

    public void addLocalStorageValues(Map<String, String> form) {
        interact.addLocalStorageValues(form);
    }

    public void addCookies(Map<String, String> cookies) {
        interact.addCookies(cookies);
    }

    public void deleteCookies() {
        interact.deleteCookies();
    }

    public void clickByText(String text) {
        interact.clickByText(text);
    }

    public void waitForSeconds(Integer duration) {
        interact.waitForSeconds(duration);
    }

    public void clickInteraction(WebElement button, String buttonName, String pageName) {
        interact.clickInteraction(button, buttonName, pageName);
    }

    public void clickInteraction(WebElement button) {
        interact.clickInteraction(button);

    }

    public void saveAttributeValue(WebElement element, String attributeName, String elementName, String pageName) {
        interact.saveAttributeValue(element, attributeName, elementName, pageName);
    }

    public void clickTowards(WebElement element, String elementName, String pageName) {
        interact.clickTowards(element, elementName, pageName);
    }

    public void clickIfPresent(WebElement element, String elementName, String pageName) {
        interact.clickIfPresent(element, elementName, pageName);
    }

    public void basicFill(WebElement inputElement, String inputName, String pageName, String input, boolean verify) {
        interact.basicFill(inputElement, inputName, pageName, input, verify);
    }

    public void fillForm(List<Bundle<WebElement, String, String>> bundles, String pageName) {
        interact.fillForm(bundles, pageName);
    }

    public void verifyText(WebElement element, String elementName, String pageName, String expectedText) {
        interact.verifyText(element, elementName, pageName, expectedText);
    }

    public void verifyContainsText(WebElement element, String elementName, String pageName, String expectedText) {
        interact.verifyContainsText(element, elementName, pageName, expectedText);
    }

    public void verifyListedText(List<Bundle<WebElement, String, String>> bundles, String pageName) {
        interact.verifyListedText(bundles, pageName);
    }

    public void verifyPresence(WebElement element, String elementName, String pageName) {
        interact.verifyPresence(element, elementName, pageName);
    }

    public void quitDriver() {
        interact.quitDriver();
    }

    public void verifyState(WebElement element, String elementName, String pageName, ElementState expectedState) {
        interact.verifyState(element, elementName, pageName, expectedState);
    }

    public boolean elementIs(WebElement element, String elementName, String pageName, ElementState expectedState) {
        return interact.elementIs(element, elementName, pageName, expectedState);
    }

    public void waitUntilAbsence(WebElement element, String elementName, String pageName) {
        interact.waitUntilAbsence(element, elementName, pageName);
    }

    public void waitUntilVisible(WebElement element, String elementName, String pageName) {
        interact.waitUntilVisible(element, elementName, pageName);
    }

    public void waitUntilElementContainsAttribute(WebElement element, String elementName, String pageName, String attributeName, String attributeValue) {
        interact.waitUntilElementContainsAttribute(element, elementName, pageName, attributeName, attributeValue);
    }

    public void verifyElementContainsAttribute(WebElement element, String elementName, String pageName, String attributeName, String attributeValue) {
        interact.verifyElementContainsAttribute(element, elementName, pageName, attributeName, attributeValue);
    }

    public void verifyElementAttributeContainsValue(WebElement element, String attributeName, String elementName, String pageName, String value) {
        interact.verifyElementAttributeContainsValue(element, attributeName, elementName, pageName, value);
    }

    public void verifyElementColor(WebElement element, String attributeName, String elementName, String pageName, String attributeValue) {
        interact.verifyElementColor(element, attributeName, elementName, pageName, attributeValue);
    }

    public void verifyPresenceOfListedElements(List<Bundle<WebElement, String, String>> bundles, WebElement element, List<WebElement> elements, String pageName, List<Map<String, String>> signForms) {
        interact.verifyPresenceOfListedElements(bundles, element, elements, pageName, signForms);
    }

    public void clickButtonByText(String buttonText, Boolean scroll) {
        interact.clickButtonByText(buttonText, scroll);
    }

    public void updateContext(String key, String value) {
        interact.updateContext(key, value);
    }

    public void pressKey(WebElement element, String elementName, String pageName, Keys... keys) {
        interact.pressKey(element, elementName, pageName, keys);
    }

    public void fillInputWithFile(WebElement inputElement, String inputName, String pageName, String absoluteFilePath) {
        interact.fillInputWithFile(inputElement, inputName, pageName, absoluteFilePath);
    }

    public void bundleInteraction(List<Bundle<String, WebElement, Map<String, String>>> bundles, String pageName) {
        interact.bundleInteraction(bundles, pageName);
    }

    public void scrollOrSwipeInDirection(Direction direction) {
        scroll(direction);
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
     * Scroll in a given direction
     *
     * @param direction target direction (UP or DOWN)
     */
    protected void scroll(@NotNull Direction direction){
        log.info("Scrolling " + strUtils.highlighted(BLUE, direction.name().toLowerCase()));
        String script = switch (direction) {
            case up -> "window.scrollBy(0,-document.body.scrollHeight)";
            case down -> "window.scrollBy(0,document.body.scrollHeight)";
            case left, right -> null;
        };
        ((JavascriptExecutor) driver).executeScript(script);
    }

    public void waitUntilPageLoads(int waitingTime) {
        waitUntilLoads(waitingTime);
    }

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

            if (!nodeValueMatcher.find())
                throw new PickleibException("Node values do not match! Expected: " + expectedValue + ", Found: " + object);
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

                if (!nodeValueMatcher.find())
                    throw new PickleibException("Node values do not match! Expected: " + nodeValue + ", Found: " + object);
                log.success("Value of '" + nodeSource + "' is verified to be '" + object + "'");
            }
        }
        else throw new RuntimeException("'" + eventName + "' event is not fired!");
    }
}