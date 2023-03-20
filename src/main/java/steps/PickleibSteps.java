package steps;

import com.github.webdriverextensions.WebComponent;
import context.ContextStore;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.html5.LocalStorage;
import org.openqa.selenium.remote.RemoteExecuteMethod;
import org.openqa.selenium.remote.html5.RemoteWebStorage;
import org.openqa.selenium.support.ui.ExpectedConditions;
import utils.ScreenCaptureUtility;
import utils.WebUtilities;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static utils.WebUtilities.Color.BLUE;
import static utils.WebUtilities.Color.GRAY;

@SuppressWarnings("unused")
public class PickleibSteps extends WebUtilities {

    private ScreenCaptureUtility capture = new ScreenCaptureUtility();

    /**
     *
     * Navigate to url: {url}
     *
     * @param url target url
     */
    public void getUrl(String url) {
        url = contextCheck(url);
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
     * Swithches to the next tab
     *
     */
    public void switchToTabByIndex() {
        String parentHandle = switchWindowByHandle(null);
        ContextStore.put("parentHandle", parentHandle);
    }

    /**
     *
     * Switches to a specified parent tab
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
     * @param handle target tab handle
     */
    public void switchToTabByIndex(String handle) {
        handle = contextCheck(handle);
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
        htmlPath = contextCheck(htmlPath);
        log.new Info("Navigating to the email @" + htmlPath);
        driver.get(htmlPath);
    }

    /**
     *
     * Set window width & height as {width} & {height}
     *
     * @param width target width
     * @param height target height
     */
    public void setFrameSize(Integer width, Integer height) {setWindowSize(width,height);}

    /**
     *
     * Adds the given values to LocalStorage
     *
     * @param form Map<String, String>
     */
    public void addLocalStorageValues(Map<String, String> form){
        for (String valueKey: form.keySet()) {
            RemoteExecuteMethod executeMethod = new RemoteExecuteMethod(driver);
            RemoteWebStorage webStorage = new RemoteWebStorage(executeMethod);
            LocalStorage storage = webStorage.getLocalStorage();
            storage.setItem(valueKey, contextCheck(form.get(valueKey)));
        }
    }

    /**
     *
     * Adds the given cookies
     *
     * @param cookies Map<String, String>
     */
    public void addCookies(Map<String, String> cookies){
        for (String cookieName: cookies.keySet()) {
            Cookie cookie = new Cookie(cookieName, contextCheck(cookies.get(cookieName)));
            driver.manage().addCookie(cookie);
        }
    }

    /**
     * Refreshes the page
     */
    public void refresh() {refreshThePage();}

    /**
     * Deletes all cookies
     */
    public void deleteCookies() {driver.manage().deleteAllCookies();}

    /**
     *
     * Navigate browser to {direction}
     *
     * @param direction target direction (up or forwards)
     */
    public void browserNavigate(WebUtilities.Navigation direction) {navigateBrowser(direction);}

    /**
     *
     * Click button with {text} text
     *
     * @param text target text
     */
    public void clickWithText(String text) {clickButtonWithText(text, true);}

    /**
     *
     * Click button with {text} css locator
     *
     * @param text target text
     */
    public void clickWithLocator(String text) {
        WebElement element = driver.findElement(By.cssSelector(text));
        clickElement(element, true);
    }

    /**
     *
     * Wait {duration} seconds
     *
     * @param duration desired duration
     */
    public void wait(Integer duration) {
        waitFor(duration);
    }

    /**
     *
     * Scroll {direction}
     *
     * @param direction target direction (up or down)
     */
    public void scrollTo(WebUtilities.Direction direction){scroll(direction);}

    /**
     *
     * Click the {button name} on the {page name}
     *
     * @param buttonName target button name
     * @param pageName specified page instance name
     * @param objectRepository class that includes specified page instance
     */
    public void click(String buttonName, String pageName, Object objectRepository){
        log.new Info("Clicking " +
                highlighted(BLUE, buttonName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName)
        );
        pageName = strUtils.firstLetterDeCapped(pageName);
        clickElement(getElementFromPage(buttonName, pageName, objectRepository), true);
    }

    /**
     *
     * Acquire the {attribute name} attribute of {element name} on the {page name}
     * (Use 'innerHTML' attributeName to acquire text on an element)
     *
     * @param attributeName acquired attribute name
     * @param elementName target element name
     * @param pageName specified page instance name
     * @param objectRepository class that includes specified page instance
     */
    // Use 'innerHTML' attributeName to acquire text on an element
    public void getAttributeValue(String attributeName, String elementName, String pageName, Object objectRepository){
        log.new Info("Acquiring " +
                highlighted(BLUE,attributeName) +
                highlighted(GRAY," attribute of ") +
                highlighted(BLUE, elementName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName)
        );
        pageName = strUtils.firstLetterDeCapped(pageName);
        WebElement element = getElementFromPage(elementName, pageName, objectRepository);
        String attribute = element.getAttribute(attributeName);
        log.new Info("Attribute -> " + highlighted(BLUE, attributeName) + highlighted(GRAY," : ") + highlighted(BLUE, attribute));
        ContextStore.put(elementName + "-" + attributeName, attribute);
        log.new Info("Attribute saved to the ContextStore as -> '" +
                highlighted(BLUE, elementName + "-" + attributeName) +
                highlighted(GRAY, "' : '") +
                highlighted(BLUE, attribute) +
                highlighted(GRAY, "'")
        );
    }

    /**
     *
     * Acquire attribute {attribute name} from component element {element name} of {component name} component on the {page name}
     * (Use 'innerHTML' attributeName to acquire text on an element)
     *
     * @param attributeName acquired attribute name
     * @param elementName target element name
     * @param componentName specified component name
     * @param pageName specified page instance name
     * @param objectRepository class that includes specified page instance
     */
    public void getAttributeValue(String attributeName, String elementName, String componentName, String pageName, Object objectRepository){
        log.new Info("Acquiring " +
                highlighted(BLUE,attributeName) +
                highlighted(GRAY," attribute of ") +
                highlighted(BLUE, elementName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName)
        );
        pageName = strUtils.firstLetterDeCapped(pageName);
        WebElement element = getElementFromComponent(elementName, componentName, pageName, objectRepository);
        String attribute = element.getAttribute(attributeName);
        log.new Info("Attribute -> " + highlighted(BLUE, attributeName) + highlighted(GRAY," : ") + highlighted(BLUE, attribute));
        ContextStore.put(elementName + "-" + attributeName, attribute);
        log.new Info("Attribute saved to the ContextStore as -> '" +
                highlighted(BLUE, elementName + "-" + attributeName) +
                highlighted(GRAY, "' : '") +
                highlighted(BLUE, attribute) +
                highlighted(GRAY, "'")
        );
    }

    /**
     *
     * Center the {element name} on the {page name}
     *
     * @param elementName target element name
     * @param pageName specified page instance name
     * @param objectRepository class that includes specified page instance
     */
    public void center(String elementName, String pageName, Object objectRepository){
        log.new Info("Clicking " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName)
        );
        pageName = strUtils.firstLetterDeCapped(pageName);
        centerElement(getElementFromPage(elementName, pageName, objectRepository));
    }

    /**
     *
     * Click towards the {button name} on the {page name}
     *
     * @param buttonName target button name
     * @param pageName specified page instance name
     * @param objectRepository class that includes specified page instance
     */
    public void clickTowards(String buttonName, String pageName, Object objectRepository){
        log.new Info("Clicking " +
                highlighted(BLUE, buttonName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName)
        );
        pageName = strUtils.firstLetterDeCapped(pageName);
        clickAtAnOffset(getElementFromPage(buttonName, pageName, objectRepository), 0, 0, false);
    }

    //TODO: Step to scroll element into view

    /**
     *
     * Click component element {button name} of {component name} component on the {page name}
     *
     * @param buttonName target button name
     * @param componentName specified component name
     * @param pageName specified page instance name
     * @param objectRepository class that includes specified page instance
     */
    public void click(String buttonName, String componentName, String pageName, Object objectRepository){
        log.new Info("Clicking " +
                highlighted(BLUE, buttonName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName)
        );
        pageName = strUtils.firstLetterDeCapped(pageName);
        componentName = strUtils.firstLetterDeCapped(componentName);
        clickElement(getElementFromComponent(buttonName, componentName, pageName, objectRepository), true);
    }

    /**
     *
     * Center component element {element name} of {component name} component on the {page name}
     *
     * @param elementName target element name
     * @param componentName specified component name
     * @param pageName specified page instance name
     * @param objectRepository class that includes specified page instance
     */
    public void center(String elementName, String componentName, String pageName, Object objectRepository){
        log.new Info("Clicking " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName)
        );
        pageName = strUtils.firstLetterDeCapped(pageName);
        componentName = strUtils.firstLetterDeCapped(componentName);
        centerElement(getElementFromComponent(elementName, componentName, pageName, objectRepository));
    }

    /**
     *
     * Click towards component element {button name} of {component name} component on the {page name}
     *
     * @param buttonName target button name
     * @param componentName specified component name
     * @param pageName specified page instance name
     * @param objectRepository class that includes specified page instance
     */
    public void clickTowards(String buttonName, String componentName, String pageName, Object objectRepository){
        pageName = strUtils.firstLetterDeCapped(pageName);
        componentName = strUtils.firstLetterDeCapped(componentName);
        WebElement element = getElementFromComponent(buttonName, componentName, pageName, objectRepository);
        log.new Info("Clicking towards " +
                highlighted(BLUE, buttonName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName)
        );
        clickAtAnOffset(element, 0, 0, false);
    }

    /**
     *
     * Perform a JS click on element {button name} on the {page name}
     *
     * @param buttonName target button name
     * @param pageName specified page instance name
     * @param objectRepository class that includes specified page instance
     */
    public void performJSClick(String buttonName, String pageName, Object objectRepository){
        log.new Info("Clicking " +
                highlighted(BLUE, buttonName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName)
        );
        pageName = strUtils.firstLetterDeCapped(pageName);
        WebElement element = getElementFromPage(buttonName, pageName, objectRepository);
        clickWithJS(centerElement(element));
    }

    /**
     *
     * Perform a JS click on component element {button name} of {component name} component on the {page name}
     *
     * @param buttonName target button name
     * @param componentName specified component name
     * @param pageName specified page instance name
     * @param objectRepository class that includes specified page instance
     */
    public void performJSClick(String buttonName, String componentName, String pageName, Object objectRepository){
        log.new Info("Clicking " +
                highlighted(BLUE, buttonName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName)
        );
        pageName = strUtils.firstLetterDeCapped(pageName);
        componentName = strUtils.firstLetterDeCapped(componentName);
        WebElement element = getElementFromComponent(buttonName, componentName, pageName, objectRepository);
        clickWithJS(centerElement(element));
    }

    /**
     *
     * If present, click the {button name} on the {page name}
     *
     * @param buttonName target button name
     * @param pageName specified page instance name
     * @param objectRepository class that includes specified page instance
     */
    public void clickIfPresent(String buttonName, String pageName, Object objectRepository){
        log.new Info("Clicking " +
                highlighted(BLUE, buttonName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName) +
                highlighted(GRAY, ", if present...")
        );
        pageName = strUtils.firstLetterDeCapped(pageName);
        try {
            WebElement element = getElementFromPage(buttonName, pageName, objectRepository);
            if (elementIs(element, WebUtilities.ElementState.DISPLAYED)) clickElement(element, true);
        }
        catch (WebDriverException ignored){log.new Warning("The " + buttonName + " was not present");}
    }

    /**
     *
     * If present, click component element {button name} of {component name} component on the {page name}
     *
     * @param buttonName target button name
     * @param componentName specified component name
     * @param pageName specified page instance name
     * @param objectRepository class that includes specified page instance
     */
    public void clickIfPresent(String buttonName, String componentName, String pageName, Object objectRepository){
        log.new Info("Clicking " +
                highlighted(BLUE, buttonName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName) +
                highlighted(GRAY, ", if present...")
        );
        pageName = strUtils.firstLetterDeCapped(pageName);
        componentName = strUtils.firstLetterDeCapped(componentName);
        try {
            WebElement element = getElementFromComponent(buttonName, componentName, pageName, objectRepository);
            if (elementIs(element, WebUtilities.ElementState.DISPLAYED)) clickElement(element, true);
        }
        catch (WebDriverException ignored){log.new Warning("The " + buttonName + " was not present");}
    }

    /**
     *
     * Click listed element {button name} from {list name} list on the {page name}
     *
     * @param buttonName target button name
     * @param listName specified list name
     * @param pageName specified page instance name
     * @param objectRepository class that includes specified page instance
     */
    public void clickListedButton(String buttonName, String listName, String pageName, Object objectRepository){
        pageName = strUtils.firstLetterDeCapped(pageName);
        listName = strUtils.firstLetterDeCapped(listName);
        buttonName = contextCheck(buttonName);
        List<WebElement> elements = getElementsFromPage(
                listName,
                strUtils.firstLetterDeCapped(pageName),
                objectRepository
        );
        WebElement element = acquireNamedElementAmongst(elements, buttonName);
        log.new Info("Clicking listed button " +
                highlighted(BLUE, buttonName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName)
        );
        clickElement(element, true);
    }

    /**
     *
     * Click listed component element {button name} of {component name} from {component list name} list on the {page name}
     *
     * @param buttonName target button name
     * @param componentName specified component name
     * @param listName specified component list name
     * @param pageName specified page instance name
     * @param objectRepository class that includes specified page instance
     */
    public void clickListedButton(String buttonName, String componentName, String listName, String pageName, Object objectRepository){
        componentName = strUtils.firstLetterDeCapped(componentName);
        pageName = strUtils.firstLetterDeCapped(pageName);
        listName = strUtils.firstLetterDeCapped(listName);
        buttonName = contextCheck(buttonName);
        List<WebElement> elements = getElementsFromComponent(
                listName,
                componentName,
                pageName,
                objectRepository
        );
        WebElement element = acquireNamedElementAmongst(elements, buttonName);
        log.new Info("Clicking listed button " +
                highlighted(BLUE, buttonName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName)
        );
        clickElement(element, true);
    }

    /**
     *
     * Select component named {component name} from {component list name} component list on the {page name} and click the {button name} element
     *
     * @param selectionName specified component name
     * @param listName specified component list name
     * @param pageName specified page instance name
     * @param buttonName target button name
     * @param objectRepository class that includes specified page instance
     */
    public void clickButtonAmongstComponents(String selectionName, String listName, String pageName, String buttonName, Object objectRepository){
        pageName = strUtils.firstLetterDeCapped(pageName);
        listName = strUtils.firstLetterDeCapped(listName);
        WebElement element = getElementAmongstNamedComponentsFromPage(
                buttonName,
                selectionName,
                listName,
                pageName,
                objectRepository
        );
        log.new Info("Clicking listed button " +
                highlighted(BLUE, buttonName) +
                highlighted(GRAY," of selected ") +
                highlighted(BLUE, selectionName) +
                highlighted(GRAY," component on the ") +
                highlighted(BLUE, pageName)
        );
        clickElement(element, true);
    }

    /**
     *
     * Select exact component named {component name} from {component list name} component list on the {page name} and click the {button name} element
     *
     * @param selectionName specified component name
     * @param listName specified component list name
     * @param pageName specified page instance name
     * @param buttonName target button name
     * @param objectRepository class that includes specified page instance
     */
    public void clickButtonAmongstExactNamedComponents(String selectionName, String listName, String pageName, String buttonName, Object objectRepository){
        pageName = strUtils.firstLetterDeCapped(pageName);
        listName = strUtils.firstLetterDeCapped(listName);
        List<WebComponent> components = getComponentsFromPage(listName, pageName, objectRepository);
        WebComponent component = acquireExactNamedComponentAmongst(components, selectionName, buttonName);
        WebElement element = getElementFromComponent(buttonName, component);
        log.new Info("Clicking listed button " +
                highlighted(BLUE, buttonName) +
                highlighted(GRAY," of selected ") +
                highlighted(BLUE, selectionName) +
                highlighted(GRAY," component on the ") +
                highlighted(BLUE, pageName)
        );
        clickElement(element, true);
    }

    /**
     *
     * Select component named {component name} from {component list name} component list on the {page name} and click listed element {button name} of {element list name}
     *
     * @param componentName specified component name
     * @param componentListName specified component list name
     * @param pageName specified page instance name
     * @param buttonName target button name
     * @param elementListName target element list name
     * @param objectRepository class that includes specified page instance
     */
    public void clickListedButtonAmongstComponents(
            String componentName,
            String componentListName,
            String pageName,
            String buttonName,
            String elementListName,
            Object objectRepository) {
        buttonName = contextCheck(buttonName);
        componentName = contextCheck(componentName);
        pageName = strUtils.firstLetterDeCapped(pageName);
        componentListName = strUtils.firstLetterDeCapped(componentListName);
        List<WebComponent> components = getComponentsFromPage(componentListName, pageName, objectRepository);
        WebComponent component = acquireNamedComponentAmongst(components, componentName);
        List<WebElement> elements = getElementsFromComponent(elementListName, component);
        WebElement element = acquireNamedElementAmongst(elements, buttonName);
        log.new Info("Clicking listed button " +
                highlighted(BLUE, buttonName) +
                highlighted(GRAY," of selected ") +
                highlighted(BLUE, componentName) +
                highlighted(GRAY," component on the ") +
                highlighted(BLUE, pageName)
        );
        clickElement(element, true);
    }

    /**
     *
     * Click listed attribute element that has {attribute value} value for its {attribute name} attribute from {list name} list on the {page name}
     *
     * @param attributeValue expected attribute value
     * @param attributeName target attribute name
     * @param listName target list name
     * @param pageName specified page instance name
     * @param objectRepository class that includes specified page instance
     */
    public void clickListedButtonByAttribute(String attributeValue, String attributeName, String listName, String pageName, Object objectRepository) {
        List<WebElement> elements = getElementsFromPage(
                listName,
                strUtils.firstLetterDeCapped(pageName),
                objectRepository
        );
        WebElement element = acquireElementUsingAttributeAmongst(elements, attributeName, attributeValue);
        log.new Info("Clicking " +
                highlighted(BLUE, attributeName) +
                highlighted(GRAY, " on the ") +
                highlighted(BLUE, pageName)
        );
        clickElement(element, true);
    }

    /**
     *
     * Click listed attribute element of {component name} component that has {attribute value} value for its {attribute name} attribute from {list name} list on the {page name}
     *
     * @param componentName specified component name
     * @param attributeValue expected attribute value
     * @param attributeName target attribute name
     * @param listName target list name
     * @param pageName specified page instance name
     * @param objectRepository class that includes specified page instance
     */
    public void clickListedButtonByAttribute(String componentName, String attributeValue, String attributeName, String listName, String pageName, Object objectRepository) {
        List<WebElement> elements = getElementsFromComponent(
                listName,
                strUtils.firstLetterDeCapped(componentName),
                strUtils.firstLetterDeCapped(pageName),
                objectRepository
        );
        WebElement element = acquireElementUsingAttributeAmongst(elements, attributeName, attributeValue);
        log.new Info("Clicking " +
                highlighted(BLUE, attributeName) +
                highlighted(GRAY, " on the ") +
                highlighted(BLUE, pageName)
        );
        clickElement(element, true);
    }

    /**
     *
     * Fill listed input {input name} from {list name} list on the {page name} with text: {input text}
     *
     * @param inputName target input element name
     * @param listName target list name
     * @param pageName specified page instance name
     * @param input input text
     * @param objectRepository class that includes specified page instance
     */
    public void fillListedInput(String inputName, String listName, String pageName, String input, Object objectRepository){
        input = contextCheck(input);
        log.new Info("Filling " +
                highlighted(BLUE, inputName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName) +
                highlighted(GRAY, " with the text: ") +
                highlighted(BLUE, input)
        );
        pageName = strUtils.firstLetterDeCapped(pageName);
        List<WebElement> elements = getElementsFromPage(listName, pageName, objectRepository);
        WebElement element = acquireNamedElementAmongst(elements, inputName);
        clearFillInput(element, input, false, true);
    }

    /**
     *
     * Fill component input {input name} of {component name} component on the {page name} with text: {input text}
     *
     * @param inputName target input element name
     * @param componentName specified component name
     * @param pageName specified page instance name
     * @param input input text
     * @param objectRepository class that includes specified page instance
     */
    public void fill(String inputName, String componentName, String pageName, String input, Object objectRepository){
        input = contextCheck(input);
        log.new Info("Filling " +
                highlighted(BLUE, inputName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName) +
                highlighted(GRAY, " with the text: ") +
                highlighted(BLUE, input)
        );
        pageName = strUtils.firstLetterDeCapped(pageName);
        componentName = strUtils.firstLetterDeCapped(componentName);
        clearFillInput(
                getElementFromComponent(inputName, componentName, pageName, objectRepository), //Element
                input,
                false,
                true
        );
    }

    /**
     *
     * Fill component form input on the {page name}
     *
     * @param pageName specified page instance name
     * @param signForms table that has key as "Input" and value as "Input Element" (dataTable.asMaps())
     * @param objectRepository class that includes specified page instance
     */
    public void fillForm(String pageName, List<Map<String, String>> signForms, Object objectRepository){
        String inputName;
        String input;
        for (Map<String, String> form : signForms) {
            inputName = form.get("Input Element");
            input = contextCheck(form.get("Input"));
            log.new Info("Filling " +
                    highlighted(BLUE, inputName) +
                    highlighted(GRAY," on the ") +
                    highlighted(BLUE, pageName) +
                    highlighted(GRAY, " with the text: ") +
                    highlighted(BLUE, input)
            );
            pageName = strUtils.firstLetterDeCapped(pageName);
            clearFillInput(getElementFromPage(inputName, pageName, objectRepository), //Element
                    input,
                    false,
                    true
            );
        }
    }

    /**
     *
     * Fill component form input of {component name} component on the {page name}
     *
     * @param componentName specified component name
     * @param pageName specified page instance name
     * @param forms table that has key as "Input" and value as "Input Element" (dataTable.asMaps())
     * @param objectRepository class that includes specified page instance
     */
    public void fillForm(String componentName, String pageName, List<Map<String, String>> forms, Object objectRepository){
        String inputName;
        String input;
        for (Map<String, String> form : forms) {
            inputName = form.get("Input Element");
            input = contextCheck(form.get("Input"));
            log.new Info("Filling " +
                    highlighted(BLUE, inputName) +
                    highlighted(GRAY," on the ") +
                    highlighted(BLUE, pageName) +
                    highlighted(GRAY, " with the text: ") +
                    highlighted(BLUE, input)
            );
            pageName = strUtils.firstLetterDeCapped(pageName);
            componentName = strUtils.firstLetterDeCapped(componentName);
            clearFillInput(
                    getElementFromComponent(inputName, componentName, pageName, objectRepository), //Input element
                    input,
                    false,
                    true
            );
        }
    }

    /**
     *
     * Fill iFrame element {element name} of {iframe name} on the {page name} with text: {input text}
     *
     * @param inputName target element name
     * @param iframeName target iframe name
     * @param pageName specified page instance name
     * @param inputText input text
     * @param objectRepository class that includes specified page instance
     */
    public void fillIframeInput(String inputName,String iframeName,String pageName, String inputText, Object objectRepository){
        inputText = contextCheck(inputText);
        log.new Info("Filling " +
                highlighted(BLUE, inputName) +
                highlighted(GRAY," i-frame element input on the ") +
                highlighted(BLUE, pageName) +
                highlighted(GRAY, " with the text: ") +
                highlighted(BLUE, inputText)
        );
        pageName = strUtils.firstLetterDeCapped(pageName);
        WebElement iframe = getElementFromPage(iframeName, pageName, objectRepository);
        elementIs(iframe, WebUtilities.ElementState.DISPLAYED);
        driver.switchTo().frame(iframe);
        WebElement element = getElementFromPage(inputName, pageName, objectRepository);
        clearFillInput(element, inputText,true,true);
        driver.switchTo().parentFrame();
    }

    /**
     *
     * Click i-frame element {element name} in {iframe name} on the {page name}
     *
     * @param elementName target element name
     * @param iframeName target iframe name
     * @param pageName specified page instance name
     * @param objectRepository class that includes specified page instance
     */
    public void clickIframeElement(String elementName,String iframeName,String pageName, Object objectRepository){
        log.new Info("Clicking the i-frame element " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName)
        );
        pageName = strUtils.firstLetterDeCapped(pageName);
        WebElement iframe = getElementFromPage(iframeName, pageName, objectRepository);
        driver.switchTo().frame(iframe);
        WebElement element = getElementFromPage(elementName, pageName, objectRepository);
        click(element);
    }

    /**
     *
     * Fill {iframe} iframe component form input of {component name} component on the {page name}
     *
     * @param iframeName target iframe name
     * @param componentName specified component name
     * @param pageName specified page instance name
     * @param forms table that has key as "Input" and value as "Input Element" (dataTable.asMaps())
     * @param objectRepository class that includes specified page instance
     */
    //@Given("Fill iframe component form input of {} component on the {}")
    public void fillFormIframe(String iframeName, String componentName, String pageName, List<Map<String, String>> forms, Object objectRepository){
        String inputName;
        String input;
        for (Map<String, String> form : forms) {
            inputName = form.get("Input Element");
            input = contextCheck(form.get("Input"));
            log.new Info("Filling " +
                    highlighted(BLUE, inputName) +
                    highlighted(GRAY," on the ") +
                    highlighted(BLUE, pageName) +
                    highlighted(GRAY, " with the text: ") +
                    highlighted(BLUE, input)
            );
            pageName = strUtils.firstLetterDeCapped(pageName);
            componentName = strUtils.firstLetterDeCapped(componentName);
            WebElement element = getElementFromPage(iframeName, pageName, objectRepository);
            driver.switchTo().frame(element);

            clearFillInput(
                    getElementFromComponent(inputName, componentName, pageName, objectRepository), //Input element
                    input,
                    false,
                    true
            );
        }
    }

    /**
     *
     * Verify the text of {element name} on the {page name} to be: {expected text}
     *
     * @param elementName target element name
     * @param pageName specified page instance name
     * @param expectedText expected text
     * @param objectRepository class that includes specified page instance
     */
    public void verifyText(String elementName, String pageName, String expectedText, Object objectRepository){
        expectedText = contextCheck(expectedText);
        log.new Info("Performing text verification for " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName)
        );
        pageName = strUtils.firstLetterDeCapped(pageName);
        Assert.assertEquals(expectedText, getElementFromPage(elementName, pageName, objectRepository).getText());
        log.new Success("Text of the element " + elementName + " was verified!");
    }

    /**
     *
     * Verify text of element list {list name} on the {page name}
     *
     * @param listName target list name
     * @param pageName specified page instance name
     * @param signForms table that has key as "Input" and value as "Input Element" (dataTable.asMaps())
     * @param objectRepository class that includes specified page instance
     */
    public void verifyListedText(String listName, String pageName, List<Map<String, String>> signForms, Object objectRepository){
        String elementName;
        String expectedText;
        for (Map<String, String> form : signForms) {
            elementName = form.get("Input Element");
            expectedText = contextCheck(form.get("Input"));
            log.new Info("Performing text verification for " +
                    highlighted(BLUE, elementName) +
                    highlighted(GRAY," on the ") +
                    highlighted(BLUE, pageName) +
                    highlighted(GRAY, " with the text: ") +
                    highlighted(BLUE, expectedText)
            );
            pageName = strUtils.firstLetterDeCapped(pageName);
            List<WebElement> elements = getElementsFromPage(listName, pageName, objectRepository);
            WebElement element = acquireNamedElementAmongst(elements, elementName);
            Assert.assertEquals("The " + element.getText() + " does not contain text '",expectedText, element.getText());
            log.new Success("Text of the element" + element.getText() + " was verified!");

        }
    }

    /**
     *
     * Verify text of the component element {element name} of {component name} on the {page name} to be: {expected text}
     *
     * @param elementName target element name
     * @param componentName specified component name
     * @param pageName specified page instance name
     * @param expectedText expected text
     * @param objectRepository class that includes specified page instance
     */
    public void verifyText(String elementName, String componentName, String pageName, String expectedText, Object objectRepository){
        expectedText = contextCheck(expectedText);
        log.new Info("Performing text verification for " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName)
        );
        pageName = strUtils.firstLetterDeCapped(pageName);
        componentName = strUtils.firstLetterDeCapped(componentName);
        WebElement element = getElementFromComponent(elementName, componentName, pageName, objectRepository);
        elementIs(element, WebUtilities.ElementState.DISPLAYED);
        Assert.assertEquals("The " + elementName + " does not contain text '",
                expectedText,
                centerElement(element).getText()
        );
        log.new Success("Text of the element " + elementName + " was verified!");
    }

    /**
     *
     * Verify text of component element list {list name} of {component name} on the {page name}
     *
     * @param listName target list name
     * @param componentName specified component name
     * @param pageName specified page instance name
     * @param forms table that has key as "Input" and value as "Input Element" (dataTable.asMaps())
     * @param objectRepository class that includes specified page instance
     */
    public void verifyListedText(String listName,String componentName, String pageName, List<Map<String, String>> forms, Object objectRepository){
        String elementName;
        String expectedText;
        for (Map<String, String> form : forms) {
            elementName = form.get("Input Element");
            expectedText = contextCheck(form.get("Input"));
            log.new Info("Performing text verification for " +
                    highlighted(BLUE, elementName) +
                    highlighted(GRAY," on the ") +
                    highlighted(BLUE, pageName) +
                    highlighted(GRAY, " with the text: ") +
                    highlighted(BLUE, expectedText)
            );
            pageName = strUtils.firstLetterDeCapped(pageName);
            componentName = strUtils.firstLetterDeCapped(componentName);
            List<WebElement> elements = getElementsFromComponent(listName, componentName, pageName, objectRepository);
            WebElement element = acquireNamedElementAmongst(elements, elementName);
            Assert.assertEquals("The " + element.getText() + " does not contain text '",expectedText,element.getText());
            log.new Success("Text of the element " + element.getText() + " was verified!");
        }
    }

    /**
     *
     * Verify presence of element {element name} on the {page name}
     *
     * @param elementName target element name
     * @param pageName specified page instance name
     * @param objectRepository class that includes specified page instance
     */
    public void verifyPresence(String elementName, String pageName, Object objectRepository){
        log.new Info("Verifying presence of " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName)
        );
        pageName = strUtils.firstLetterDeCapped(pageName);
        WebElement element = getElementFromPage(elementName, pageName, objectRepository);
        verifyElementState(element, WebUtilities.ElementState.DISPLAYED);
        log.new Success("Presence of the element " + elementName + " was verified!");
    }

    /**
     *
     * Verify presence of the component element {element name} of {component name} on the {page name}
     *
     * @param elementName target element name
     * @param componentName specified component name
     * @param pageName specified page instance name
     * @param objectRepository class that includes specified page instance
     */
    public void verifyPresence(String elementName, String componentName, String pageName, Object objectRepository){
        log.new Info("Verifying presence of " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName)
        );
        pageName = strUtils.firstLetterDeCapped(pageName);
        componentName = strUtils.firstLetterDeCapped(componentName);
        WebElement element = getElementFromComponent(elementName, componentName, pageName, objectRepository);
        verifyElementState(element, WebUtilities.ElementState.DISPLAYED);
        log.new Success("Presence of the element " + elementName + " was verified!");
    }

    /**
     *
     * Checking the presence of the element texts on the {page name}
     *
     * @param pageName specified page instance name
     * @param signForms table that has key as "Text" (dataTable.asMaps())
     */
    public void verifyPresenceText(String pageName, List<Map<String, String>> signForms) {
        String elementText;
        for (Map<String, String> form : signForms) {
            elementText = contextCheck(form.get("Text"));
            log.new Info("Performing text verification for " +
                    highlighted(BLUE, elementText) +
                    highlighted(GRAY, " on the ") +
                    highlighted(BLUE, pageName)
            );

            WebElement element = getElementContainingText(elementText);
            verifyElementState(element, WebUtilities.ElementState.ENABLED);
            log.new Success("Presence of the element text " + elementText + " was verified!");
        }
    }

    /**
     * Closes the browser
     */
    public void closeBrowser(){
        driver.quit();
    }

    /**
     *
     * Verify that element {element name} on the {page name} is in {expected state} state
     *
     * @param elementName target element name
     * @param pageName specified page instance name
     * @param expectedState expected state
     * @param objectRepository class that includes specified page instance
     */
    //@Given("Verify that element {} on the {} is in {} state")
    public void verifyState(String elementName, String pageName, String expectedState, Object objectRepository){
        log.new Info("Verifying " +
                highlighted(BLUE, expectedState) +
                highlighted(GRAY," state of ") +
                highlighted(BLUE, elementName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName)
        );
        pageName = strUtils.firstLetterDeCapped(pageName);
        WebElement element = getElementFromPage(elementName, pageName, objectRepository);
        verifyElementState(element, WebUtilities.ElementState.valueOf(expectedState));
        log.new Success("The element '" + elementName + "' was verified to be enabled!");
    }

    /**
     *
     * Verify that component element {element name} of {component name} on the {page name} is in {expected state} state
     *
     * @param elementName target element name
     * @param componentName specified component name
     * @param pageName specified page instance name
     * @param expectedState expected state
     * @param objectRepository class that includes specified page instance
     */
    public void verifyState(String elementName, String componentName, String pageName, String expectedState, Object objectRepository){
        log.new Info("Verifying " +
                highlighted(BLUE, expectedState) +
                highlighted(GRAY," state of ")+
                highlighted(BLUE, elementName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName)
        );
        expectedState = expectedState.toUpperCase();
        pageName = strUtils.firstLetterDeCapped(pageName);
        componentName = strUtils.firstLetterDeCapped(componentName);
        WebElement element = getElementFromComponent(elementName, componentName, pageName, objectRepository);
        verifyElementState(element, WebUtilities.ElementState.valueOf(expectedState));
        log.new Success("The element " + elementName + " was verified to be enabled!");
    }

    /**
     *
     * If present, verify that component element {element name} of {component name} on the {page name} is in {expected state} state
     *
     * @param elementName target element name
     * @param pageName specified page instance name
     * @param expectedState expected state
     * @param objectRepository class that includes specified page instance
     */
    public void verifyIfPresentElement(String elementName, String componentName, String pageName, WebUtilities.ElementState expectedState, Object objectRepository){
        log.new Info("Verifying " +
                highlighted(BLUE, expectedState.name()) +
                highlighted(GRAY," state of ")+
                highlighted(BLUE, elementName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName)
        );
        pageName = strUtils.firstLetterDeCapped(pageName);
        componentName = strUtils.firstLetterDeCapped(componentName);
        try {
            WebElement element = getElementFromComponent(elementName, componentName, pageName, objectRepository);
            if (elementIs(element, WebUtilities.ElementState.DISPLAYED)) verifyElementState(element, expectedState);
        }
        catch (WebDriverException ignored){log.new Warning("The " + elementName + " was not present");}
    }

    /**
     *
     * Wait for absence of element {element name} on the {page name}
     *
     * @param elementName target element name
     * @param pageName specified page instance name
     * @param objectRepository class that includes specified page instance
     */
    public void waitUntilAbsence(String elementName, String pageName, Object objectRepository){
        log.new Info("Waiting for the absence of " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName)
        );
        pageName = strUtils.firstLetterDeCapped(pageName);
        WebElement element = getElementFromPage(elementName, pageName, objectRepository);
        elementIs(element, WebUtilities.ElementState.ABSENT);
    }

    /**
     *
     * Wait for absence of component element {element name} of {component name} on the {page name}
     *
     * @param elementName target element name
     * @param componentName specified component name
     * @param pageName specified page instance name
     * @param objectRepository class that includes specified page instance
     */
    public void waitUntilAbsence(String elementName, String componentName, String pageName, Object objectRepository){
        log.new Info("Waiting for the absence of " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName)
        );
        pageName = strUtils.firstLetterDeCapped(pageName);
        componentName = strUtils.firstLetterDeCapped(componentName);
        WebElement element = getElementFromComponent(elementName, componentName,pageName, objectRepository);
        elementIs(element, WebUtilities.ElementState.ABSENT);
    }

    /**
     *
     * Wait for element {element name} on the {page name} to be visible
     *
     * @param elementName target element name
     * @param pageName specified page instance name
     * @param objectRepository class that includes specified page instance
     */
    public void waitUntilVisible(String elementName, String pageName, Object objectRepository){
        log.new Info("Waiting for the absence of " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName)
        );
        pageName = strUtils.firstLetterDeCapped(pageName);
        WebElement element = getElementFromPage(elementName,pageName, objectRepository);
        elementIs(element, WebUtilities.ElementState.DISPLAYED);
    }

    /**
     *
     * Wait for component element {element name} of {component name} on the {page name} to be visible
     *
     * @param elementName target element name
     * @param componentName specified component name
     * @param pageName specified page instance name
     * @param objectRepository class that includes specified page instance
     */
    public void waitUntilVisible(String elementName, String componentName, String pageName, Object objectRepository){
        log.new Info("Waiting for the absence of " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName)
        );
        pageName = strUtils.firstLetterDeCapped(pageName);
        componentName = strUtils.firstLetterDeCapped(componentName);
        WebElement element = getElementFromComponent(elementName, componentName, pageName, objectRepository);
        elementIs(element, WebUtilities.ElementState.DISPLAYED);
    }

    /**
     *
     * Wait until element {element name} on the {page name} has {attribute value} value for its {attribute name} attribute
     *
     * @param elementName target element name
     * @param pageName specified page instance name
     * @param attributeValue expected attribute value
     * @param attributeName target attribute name
     * @param objectRepository class that includes specified page instance
     */
    public void waitUntilElementContainsAttribute(
            String elementName,
            String pageName,
            String attributeValue,
            String attributeName,
            Object objectRepository) {
        attributeValue = contextCheck(attributeValue);
        pageName = strUtils.firstLetterDeCapped(pageName);
        WebElement element = getElementFromPage(elementName,pageName, objectRepository);
        log.new Info("Waiting for the absence of " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName)
        );
        try {wait.until((ExpectedConditions.attributeContains(element, attributeName, attributeValue)));}
        catch (WebDriverException ignored) {}
    }

    /**
     *
     * Wait until component element {element name} of {component name} on the {page name} has {attribute value} value for its {attribute name} attribute
     *
     * @param elementName target element name
     * @param componentName specified component name
     * @param pageName specified page instance name
     * @param attributeValue expected attribute value
     * @param attributeName target attribute name
     * @param objectRepository class that includes specified page instance
     */
    public void waitUntilElementContainsAttribute(
            String elementName,
            String componentName,
            String pageName,
            String attributeValue,
            String attributeName,
            Object objectRepository) {
        attributeValue = contextCheck(attributeValue);
        pageName = strUtils.firstLetterDeCapped(pageName);
        componentName = strUtils.firstLetterDeCapped(componentName);
        WebElement element = getElementFromComponent(elementName, componentName,pageName, objectRepository);
        log.new Info("Waiting for the presence of " +
                highlighted(BLUE, attributeName) +
                highlighted(GRAY, " attribute of ") +
                highlighted(BLUE, elementName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName)
        );
        try {wait.until(ExpectedConditions.attributeContains(element, attributeName, attributeValue));}
        catch (WebDriverException ignored) {}
    }

    /**
     *
     * Verify that element {element name} on the {page name} has {attribute value} value for its {attribute name} attribute
     *
     * @param elementName target element name
     * @param pageName specified page instane name
     * @param attributeValue expected attribute value
     * @param attributeName target attribute name
     * @param objectRepository class that includes specified page instance
     */
    public void verifyElementContainsAttribute(
            String elementName,
            String pageName,
            String attributeValue,
            String attributeName,
            Object objectRepository) {

        pageName = strUtils.firstLetterDeCapped(pageName);
        attributeValue = contextCheck(attributeValue);
        WebElement element = getElementFromPage(elementName,pageName, objectRepository);
        log.new Info("Verifying " +
                highlighted(BLUE, attributeName) +
                highlighted(GRAY, " attribute of ") +
                highlighted(BLUE, elementName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName)
        );
        Assert.assertTrue(
                "The " + attributeName + " attribute of element " + elementName + " could not be verified." +
                        "\nExpected value: " + attributeValue + "\nActual value: " + element.getAttribute(attributeName),
                wait.until(ExpectedConditions.attributeContains(element, attributeName, attributeValue))
        );
        log.new Success("Value of '" + attributeName + "' attribute is verified to be '" + attributeValue + "'!");
    }

    /**
     *
     * Verify {attribute name} css attribute of element {element name} on the {page name} is {attribute value}
     *
     * @param attributeName target attribute name
     * @param elementName target attribute name
     * @param pageName specified page instance name
     * @param attributeValue expected attribute value
     * @param objectRepository class that includes specified page instance
     */
    //@Given("Verify {} css attribute of element {} on the {} is {}")
    public void verifyElementColor(
            String attributeName,
            String elementName,
            String pageName,
            String attributeValue,
            Object objectRepository) {
        attributeValue = contextCheck(attributeValue);
        pageName = strUtils.firstLetterDeCapped(pageName);
        WebElement element = getElementFromPage(elementName,pageName, objectRepository);
        log.new Info("Verifying " +
                highlighted(BLUE, attributeName) +
                highlighted(GRAY, " attribute of ") +
                highlighted(BLUE, elementName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName)
        );
        Assert.assertEquals(
                "The " + attributeName + " attribute of element " + elementName + " could not be verified." +
                        "\nExpected value: " + attributeValue + "\nActual value: " + element.getCssValue(attributeName),
                attributeValue
        );
        log.new Success("Value of '" + attributeName + "' attribute is verified to be '" + attributeValue + "'!");
    }

    /**
     *
     * Verify that component element {element name} of {component name} on the {page name} has {attribute value} value for its {attribute name} attribute
     *
     * @param elementName target element name
     * @param componentName specified component name
     * @param pageName specified page instance name
     * @param attributeValue target attribute value
     * @param attributeName target attribute name
     * @param objectRepository class that includes specified page instance
     */
    public void verifyElementContainsAttribute(
            String elementName,
            String componentName,
            String pageName,
            String attributeValue,
            String attributeName,
            Object objectRepository) {
        attributeValue = contextCheck(attributeValue);
        pageName = strUtils.firstLetterDeCapped(pageName);
        componentName = strUtils.firstLetterDeCapped(componentName);
        WebElement element = getElementFromComponent(elementName, componentName,pageName, objectRepository);
        log.new Info("Verifying " +
                highlighted(BLUE, attributeName) +
                highlighted(GRAY, " attribute of ") +
                highlighted(BLUE, elementName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName)
        );
        Assert.assertTrue(
                "The " + attributeName + " attribute of element " + elementName + " could not be verified." +
                        "\nExpected value: " + attributeValue + "\nActual value: " + element.getAttribute(attributeName),
                wait.until(ExpectedConditions.attributeContains(element, attributeName, attributeValue))
        );
        log.new Success("Value of '" + attributeName + "' attribute is verified to be '" + attributeValue + "'!");
    }

    /**
     *
     * Select component by {element field name} named {selection name} from {list name} component list on the {page name} and verify that it has {attribute value} value for its {attribute name} attribute
     *
     * @param elementFieldName target element field name
     * @param selectionName specified component name
     * @param listName specified component list name
     * @param pageName specified page instance name
     * @param attributeValue target attribute value
     * @param attributeName target attribute name
     * @param objectRepository class that includes specified page instance
     */
    public void verifySelectedComponentContainsAttribute(
            String elementFieldName,
            String selectionName,
            String listName,
            String pageName,
            String attributeValue,
            String attributeName,
            Object objectRepository) {
        attributeValue = contextCheck(attributeValue);
        pageName = strUtils.firstLetterDeCapped(pageName);
        listName = strUtils.firstLetterDeCapped(listName);
        List<WebComponent> components = getComponentsFromPage(listName, pageName, objectRepository);
        WebComponent component = acquireExactNamedComponentAmongst(components, selectionName, elementFieldName);
        log.new Info("Verifying " +
                highlighted(BLUE, attributeName) +
                highlighted(GRAY, " attribute of ") +
                highlighted(BLUE, selectionName) +
                highlighted(GRAY," component on the ") +
                highlighted(BLUE, pageName)
        );
        Assert.assertTrue(
                "The " + attributeName + " attribute of element " + selectionName + " could not be verified." +
                        "\nExpected value: " + attributeValue + "\nActual value: " + component.getAttribute(attributeName),
                wait.until(ExpectedConditions.attributeContains(component, attributeName, attributeValue))
        );
        log.new Success("Value of '" + attributeName + "' attribute is verified to be '" + attributeValue + "'!");
    }

    /**
     *
     * Select component named {selection name} from {list name} component list on the {page name} and verify that the {element name} element has {attrbiute value} value for its {attribute name} attribute
     *
     * @param selectionName target component name
     * @param listName target component list name
     * @param pageName specified page instance name
     * @param elementName target element name
     * @param attributeValue expected attribute value
     * @param attributeName target attribute name
     * @param objectRepository class that includes specified page instance
     */
    public void verifySelectedComponentElementContainsAttribute(
            String selectionName,
            String listName,
            String pageName,
            String elementName,
            String attributeValue,
            String attributeName,
            Object objectRepository) {
        attributeValue = contextCheck(attributeValue);
        pageName = strUtils.firstLetterDeCapped(pageName);
        listName = strUtils.firstLetterDeCapped(listName);
        WebElement element = getElementAmongstComponentsFromPage(
                elementName,
                selectionName,
                listName,
                pageName,
                objectRepository
        );
        log.new Info("Verifying " +
                highlighted(BLUE, attributeName) +
                highlighted(GRAY, " attribute of ") +
                highlighted(BLUE, selectionName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName)
        );
        Assert.assertTrue(
                "The " + attributeName + " attribute of element " + selectionName + " could not be verified." +
                        "\nExpected value: " + attributeValue + "\nActual value: " + element.getAttribute(attributeName),
                wait.until(ExpectedConditions.attributeContains(element, attributeName, attributeValue))
        );
        log.new Success("Value of '" + attributeName + "' attribute is verified to be '" + attributeValue + "'!");
    }

    /**
     *
     * Select component named {component name} from {component list name} component list on the {page name} and verify listed element {element name} of {element list name} has {attribute value} value for its {attribute name} attribute
     *
     * @param componentName target component name
     * @param componentListName specified component list name
     * @param pageName specified page instance name
     * @param elementName target element name
     * @param elementListName specified element list name
     * @param attributeValue expected attribute value
     * @param attributeName target attribute name
     * @param objectRepository class that includes specified page instance
     */
    public void verifySelectedComponentContainsAttribute(
            String componentName,
            String componentListName,
            String pageName,
            String elementName,
            String elementListName,
            String attributeValue,
            String attributeName,
            Object objectRepository) {
        elementName = contextCheck(elementName);
        componentName = contextCheck(componentName);
        attributeValue = contextCheck(attributeValue);
        pageName = strUtils.firstLetterDeCapped(pageName);
        componentListName = strUtils.firstLetterDeCapped(componentListName);
        List<WebComponent> components = getComponentsFromPage(componentListName, pageName, objectRepository);
        WebComponent component = acquireNamedComponentAmongst(components, componentName);
        List<WebElement> elements = getElementsFromComponent(elementListName, component);
        WebElement element = acquireNamedElementAmongst(elements, elementName);
        log.new Info("Verifying " +
                highlighted(BLUE, attributeName) +
                highlighted(GRAY, " attribute of ") +
                highlighted(BLUE, componentName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName)
        );
        Assert.assertTrue(
                "The " + attributeName + " attribute of element " + componentName + " could not be verified." +
                        "\nExpected value: " + attributeValue + "\nActual value: " + element.getAttribute(attributeName),
                wait.until(ExpectedConditions.attributeContains(element, attributeName, attributeValue))
        );
        log.new Success("Value of '" + attributeName + "' attribute is verified to be '" + attributeValue + "'!");
    }

    /**
     *
     * Verify that element {element name} from {list name} list on the {page name} has {attribute value} value for its {attribute name} attribute
     *
     * @param elementName target element name
     * @param listName target list name
     * @param pageName specified page instance name
     * @param attributeValue expected attribute value
     * @param attributeName target attribute name
     * @param objectRepository class that includes specified page instance
     */
    public void verifyListedElementContainsAttribute(
            String elementName,
            String listName,
            String pageName,
            String attributeValue,
            String attributeName,
            Object objectRepository) {
        attributeValue = contextCheck(attributeValue);
        pageName = strUtils.firstLetterDeCapped(pageName);
        List<WebElement> elements = getElementsFromPage(listName, pageName, objectRepository);
        WebElement element = acquireNamedElementAmongst(elements, elementName);
        log.new Info("Verifying " +
                highlighted(BLUE, attributeName) +
                highlighted(GRAY, " attribute of ") +
                highlighted(BLUE, elementName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName)
        );
        Assert.assertTrue(
                "The " + attributeName + " attribute of element " + elementName + " could not be verified." +
                        "\nExpected value: " + attributeValue + "\nActual value: " + element.getAttribute(attributeName),
                wait.until(ExpectedConditions.attributeContains(element, attributeName, attributeValue))
        );
        log.new Success("Value of '" + attributeName + "' attribute is verified to be '" + attributeValue + "'!");
    }

    /**
     *
     * Verify text of listed element {element name} from the {list name} on the {page name} is equal to {expected name}
     *
     * @param elementName target element name
     * @param listName target element name
     * @param pageName specified page instance name
     * @param expectedText expected text
     * @param objectRepository class that includes specified page instance
     */
    public void verifyListedElementContainsText(
            String elementName,
            String listName,
            String pageName,
            String expectedText,
            Object objectRepository) {
        expectedText = contextCheck(expectedText);
        pageName = strUtils.firstLetterDeCapped(pageName);
        List<WebElement> elements = getElementsFromPage(listName, pageName, objectRepository);
        WebElement element = acquireNamedElementAmongst(elements, elementName);
        log.new Info("Verifying text of " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName)
        );
        Assert.assertTrue(
                "The " + elementName + " does not contain text '" + expectedText + "' ",
                element.getText().contains(expectedText)
        );
        log.new Success("Text of '" + elementName + "' verified as '" + expectedText + "'!");
    }

    /**
     *
     * Verify text of listed element from the {list name} on the {page name}
     *
     * @param listName target list name
     * @param pageName specified page instance name
     * @param signForms table that has key as "Input" and value as "Input Element" (dataTable.asMaps())
     * @param objectRepository class that includes specified page instance
     */
    public void verifyListedElementContainsText(String listName, String pageName, List<Map<String, String>> signForms, Object objectRepository){
        String elementName;
        String expectedText;
        for (Map<String, String> form : signForms) {
            elementName = form.get("Input Element");
            expectedText = contextCheck(form.get("Input"));
            pageName = strUtils.firstLetterDeCapped(pageName);
            List<WebElement> elements = getElementsFromPage(listName, pageName, objectRepository);
            WebElement element = acquireNamedElementAmongst(elements, elementName);
            log.new Info("Performing text verification for " +
                    highlighted(BLUE, elementName) +
                    highlighted(GRAY," on the ") +
                    highlighted(BLUE, pageName) +
                    highlighted(GRAY, " with the text: ") +
                    highlighted(BLUE, expectedText)
            );
            Assert.assertTrue(
                    "The " + elementName + " does not contain text '" + expectedText + "' ",
                    element.getText().contains(expectedText)
            );
            log.new Success("Text of '" + elementName + "' verified as '" + expectedText + "'!");
        }
    }

    /**
     *
     * Verify text of listed component element {element name} from the {list name} of {component name} on the {page name} is equal to {expected text}
     *
     * @param elementName target element
     * @param listName target list name
     * @param componentName specified component name
     * @param pageName specified page instance name
     * @param expectedText expected text
     * @param objectRepository class that includes specified page instance
     */
    public void verifyListedComponentElementContainsText(
            String elementName,
            String listName,
            String componentName,
            String pageName,
            String expectedText,
            Object objectRepository) {
        pageName = strUtils.firstLetterDeCapped(pageName);
        componentName = strUtils.firstLetterDeCapped(componentName);
        List<WebElement> elements = getElementsFromComponent(listName, componentName, pageName, objectRepository);
        WebElement element = acquireNamedElementAmongst(elements, elementName);
        log.new Info("Verifying text of " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName)
        );
        Assert.assertTrue(
                "The " + elementName + " does not contain text '" + expectedText + "' ",
                element.getText().contains(expectedText)
        );
        log.new Success("Text of '" + elementName + "' verified as '" + expectedText + "'!");
    }

    /**
     *
     * Verify text of listed component element from the {list name} of {component name} on the {page name}
     *
     * @param listName target list name
     * @param componentName specified component name
     * @param pageName specified page instance name
     * @param signForms table that has key as "Input" and value as "Input Element" (dataTable.asMaps())
     * @param objectRepository class that includes specified page instance
     */
    public void verifyListedComponentElementContainsText(String listName, String componentName, String pageName, List<Map<String, String>> signForms, Object objectRepository){
        String elementName;
        String expectedText;
        for (Map<String, String> form : signForms) {
            elementName = form.get("Input Element");
            expectedText = contextCheck(form.get("Input"));
            pageName = strUtils.firstLetterDeCapped(pageName);
            componentName = strUtils.firstLetterDeCapped(componentName);
            List<WebElement> elements = getElementsFromComponent(listName, componentName, pageName, objectRepository);
            WebElement element = acquireNamedElementAmongst(elements, elementName);
            log.new Info("Performing text verification for " +
                    highlighted(BLUE, elementName) +
                    highlighted(GRAY, " on the ") +
                    highlighted(BLUE, pageName) +
                    highlighted(GRAY, " with the text: ") +
                    highlighted(BLUE, expectedText)
            );
            Assert.assertTrue(
                    "The " + elementName + " does not contain text '" + expectedText + "' ",
                    element.getText().contains(expectedText)
            );
            log.new Success("Text of '" + elementName + "' verified as '" + expectedText + "'!");
        }
    }

    /**
     *
     * Verify presence of listed component element {element text} of {list name} from {component name} list on the {page name}
     *
     * @param elementText target element text
     * @param listName target list name
     * @param componentName specified component name
     * @param pageName specified page instance name
     * @param objectRepository class that includes specified page instance
     */
    public void verifyListedComponentElementContainsText(String elementText, String listName, String componentName, String pageName, Object objectRepository){
        elementText = contextCheck(elementText);
        pageName = strUtils.firstLetterDeCapped(pageName);
        componentName = strUtils.firstLetterDeCapped(componentName);
        List<WebElement> elements = getElementsFromComponent(listName, componentName, pageName, objectRepository);
        WebElement element = acquireNamedElementAmongst(elements, elementText);
        log.new Info("Performing text verification for " +
                highlighted(BLUE, elementText) +
                highlighted(GRAY, " on the ") +
                highlighted(BLUE, pageName) +
                highlighted(GRAY, " with the text: ") +
                highlighted(BLUE, elementText)
        );
        Assert.assertTrue(
                "The " + elementText + " does not contain text '" + elementText + "' ",
                element.getText().contains(elementText)
        );
        log.new Success("Text of '" + elementText + "' verified as '" + elementText + "'!");
    }

    /**
     *
     * Verify that component element {element name} of {component name} from {list name} list on the {page name} has {attribute value} value for its {attribute name} attribute
     *
     * @param elementName target element name
     * @param componentName specified component name
     * @param listName target list name
     * @param pageName specified page instance name
     * @param attributeValue expected attribute value
     * @param attributeName target attribute name
     * @param objectRepository class that includes specified page instance
     */
    public void verifyListedElementContainsAttribute(
            String elementName,
            String componentName,
            String listName,
            String pageName,
            String attributeValue,
            String attributeName,
            Object objectRepository) {
        attributeValue = contextCheck(attributeValue);
        pageName = strUtils.firstLetterDeCapped(pageName);
        componentName = strUtils.firstLetterDeCapped(componentName);
        List<WebElement> elements = getElementsFromComponent(listName, componentName, pageName, objectRepository);
        WebElement element = acquireNamedElementAmongst(elements, elementName);
        log.new Info("Verifying " +
                highlighted(BLUE, attributeName) +
                highlighted(GRAY, " attribute of ") +
                highlighted(BLUE, elementName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName)
        );
        Assert.assertTrue(
                "The " + attributeName + " attribute of element " + elementName + " could not be verified." +
                        "\nExpected value: " + attributeValue + "\nActual value: " + element.getAttribute(attributeName),
                wait.until(ExpectedConditions.attributeContains(element, attributeName, attributeValue))
        );
        log.new Success("Value of '" + attributeName + "' attribute is verified to be '" + attributeValue + "'!");
    }

    /**
     *
     * Verify the page is redirecting to the page {target url}
     *
     * @param url target url
     */
    public void verifyCurrentUrl(String url) {
        url = contextCheck(url);
        log.new Info("The url contains " + url);
        Assert.assertTrue("The page is not redirected to: " + url, driver.getCurrentUrl().contains(url));
    }

    /**
     *
     * Verify the url contains with the text {target text}
     *
     * @param text target text
     */
    public void verifyTextUrl(String text) {
        log.new Info("The url contains " + text);
        Assert.assertTrue("The page is not directed to: " + text ,driver.getCurrentUrl().contains(text));
    }

    //@Given("Click the specific text {} button")
    public void clickButtonWithText(String buttonText, Boolean scroll) {
        this.clickElement(this.getElementByText(buttonText), scroll);
    }

    /**
     *
     * Update context {key} -> {value}
     *
     * @param key Context key
     * @param value Context value
     */
    public void updateContext(String key, String value){
        ContextStore.put(key, contextCheck(value));
    }

    /**
     *
     * Clear component input field {element name} from {component name} component on the {page name}
     *
     * @param elementName target element
     * @param componentName specified component name
     * @param pageName specified page instance name
     * @param objectRepository class that includes specified page instance
     */
    public void clearInputField(String elementName, String componentName, String pageName, Object objectRepository){
        componentName = strUtils.firstLetterDeCapped(componentName);
        pageName = strUtils.firstLetterDeCapped(pageName);
        WebElement element = getElementFromComponent(elementName, componentName, pageName, objectRepository);
        element.clear();
    }

    /**
     *
     * Press {target key} key on {element name} element of the {}
     *
     * @param key target key
     * @param elementName target element name
     * @param pageName specified page instance name
     * @param objectRepository class that includes specified page instance
     */
    public void pressKey(Keys key, String elementName, String pageName, Object objectRepository){
        pageName = strUtils.firstLetterDeCapped(pageName);
        WebElement element = getElementFromPage(elementName, pageName, objectRepository);
        element.sendKeys(key);
    }

    /**
     *
     * Press {target key} key on component element {element name} from {component name} component on the {page name}
     *
     * @param key target key
     * @param elementName target element name
     * @param componentName component name
     * @param pageName specified page instance name
     * @param objectRepository class that includes specified page instance
     */
    public void pressKey(Keys key, String elementName, String componentName, String pageName, Object objectRepository){
        componentName = strUtils.firstLetterDeCapped(componentName);
        pageName = strUtils.firstLetterDeCapped(pageName);
        WebElement element = getElementFromComponent(elementName, componentName, pageName, objectRepository);
        element.sendKeys(key);
    }

    /**
     *
     * Execute JS command: {script}
     *
     * @param script JS script
     */
    //@Given("Execute JS command: {}")
    public void executeJSCommand(String script) {
        executeScript(script);
    }

    /**
     *
     * Listen to {event name} event & print {specified script} object
     *
     * @param eventName target event name
     * @param objectScript object script
     */
    //@Given("Listen to {} event & print {} object")
    public void listenGetAndPrintObject(String eventName, String objectScript)  {
        String listenerScript = "_ddm.listen(" + eventName + ");";
        objectScript = "return " + objectScript;
        if (isEventFired(eventName, listenerScript)) {
            Object object = executeScript(objectScript);
            log.new Info(object);
        }
    }

    /**
     *
     * Upload file on component input {input element field name} of {component name} component on the {page name} with file: {target file path}
     *
     * @param inputName input element field name
     * @param componentName specified component name
     * @param pageName specified page instance name
     * @param input target file path
     * @param objectRepository class that includes specified page instance
     */
    public void fillInputWithFile(String inputName, String componentName, String pageName, String input, Object objectRepository){
        input = contextCheck(input);
        log.new Info("Filling " +
                highlighted(BLUE, inputName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName) +
                highlighted(GRAY, " with the text: ") +
                highlighted(BLUE, input)
        );
        pageName = strUtils.firstLetterDeCapped(pageName);
        componentName = strUtils.firstLetterDeCapped(componentName);
        clearFillInput(
                getElementFromComponent(inputName, componentName, pageName, objectRepository), //Element
                input,
                false,
                false
        );
    }

    /**
     *
     * Listen to {event name} event & verify value of {node source} node is {expected value}
     *
     * @param eventName evet name
     * @param nodeSource node source
     * @param expectedValue expected value
     */
    public void listenGetAndVerifyObject(String eventName, String nodeSource, String expectedValue)  {
        log.new Info("Verifying value of '" + nodeSource + "' node");
        String listenerScript = "_ddm.listen(" + eventName + ");";
        String nodeScript = "return " + nodeSource;
        if (isEventFired(eventName, listenerScript)) {
            Object object = executeScript(nodeScript);
            Pattern sourcePattern = Pattern.compile(expectedValue);
            Matcher nodeValueMatcher = sourcePattern.matcher(object.toString());
            Assert.assertTrue("Node values do not match! Expected: " + object + ", Found: " + object, nodeValueMatcher.find());
            log.new Success("Value of '" + nodeSource + "' is verified to be '" + object + "'");
        }
        else log.new Warning("'" + eventName + "' event is not fired!");
    }

    /**
     *
     * Listen to {evetn name} event & verify values of the following nodes
     *
     * @param eventName event name
     * @param nodeList target node list
     */
    public void listenGetAndVerifyObject(String eventName,  List<Map<String, String>> nodeList)  {
        String listenerScript = "_ddm.listen(" + eventName + ");";

        if (isEventFired(eventName, listenerScript)) {
            for (Map<String, String> nodeMap:nodeList) {
                String nodeSource = nodeMap.get("Node Source");
                String nodeValue = nodeMap.get("Node Value");

                log.new Info("Verifying value of '" + highlighted(BLUE, nodeSource) + highlighted(GRAY, "' node"));
                String nodeScript = "return " + nodeSource;
                Object object = executeScript(nodeScript);

                Pattern sourcePattern = Pattern.compile(nodeValue);
                Matcher nodeValueMatcher = sourcePattern.matcher(object.toString());

                Assert.assertTrue("Node values do not match! Expected: " + nodeValue + ", Found: " + object, nodeValueMatcher.find());
                log.new Success("Value of '" + nodeSource + "' is verified to be '" + object + "'");
            }
        }
        else throw new RuntimeException("'" + eventName + "' event is not fired!");
    }

}
