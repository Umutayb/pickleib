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

import static utils.WebUtilities.Color.BLUE;
import static utils.WebUtilities.Color.GRAY;

@SuppressWarnings("unused")
public class PickleibSteps extends WebUtilities {

    ScreenCaptureUtility capture = new ScreenCaptureUtility();

    public void getUrl(String url) {
        url = contextCheck(url);
        driver.get(url);
    }

    public void toPage(String page){
        String url = driver.getCurrentUrl();
        String pageUrl = url + page;
        navigate(pageUrl);
    }

    public void switchTab() {
        String parentHandle = switchWindowByHandle(null);
        ContextStore.put("parentHandle", parentHandle);
    }

    public void switchToParentTab() {
        switchWindowByHandle(ContextStore.get("parentHandle").toString());
    }

    public void switchTab(String handle) {
        handle = contextCheck(handle);
        String parentHandle = switchWindowByHandle(handle);
        ContextStore.put("parentHandle", parentHandle);
    }

    public void switchTab(Integer handle) {
        String parentHandle = switchWindowByIndex(handle);
        ContextStore.put("parentHandle", parentHandle);
    }

    public void getHTML(String url) {
        url = contextCheck(url);
        log.new Info("Navigating to the email @" + url);
        driver.get(url);
    }

    public void setFrameSize(Integer width, Integer height) {setWindowSize(width,height);}

    public void addLocalStorageValues(Map<String, String> form){
        for (String valueKey: form.keySet()) {
            RemoteExecuteMethod executeMethod = new RemoteExecuteMethod(driver);
            RemoteWebStorage webStorage = new RemoteWebStorage(executeMethod);
            LocalStorage storage = webStorage.getLocalStorage();
            storage.setItem(valueKey, contextCheck(form.get(valueKey)));
        }
    }

    public void addCookies(Map<String, String> cookies){
        for (String cookieName: cookies.keySet()) {
            Cookie cookie = new Cookie(cookieName, contextCheck(cookies.get(cookieName)));
            driver.manage().addCookie(cookie);
        }
    }

    public void refresh() {refreshThePage();}

    public void deleteCookies() {driver.manage().deleteAllCookies();}

    public void browserNavigate(WebUtilities.Navigation direction) {navigateBrowser(direction);}

    public void clickWithText(String text) {clickButtonWithText(text, true);}

    public void clickWithLocator(String text) {
        WebElement element = driver.findElement(By.cssSelector(text));
        clickElement(element, true);
    }

    public void wait(Integer duration) {
        waitFor(duration);
    }

    public void scrollTo(WebUtilities.Direction direction){scroll(direction);}

    public void click(String buttonName, String pageName, Object objectRepository){
        log.new Info("Clicking " +
                highlighted(BLUE, buttonName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName)
        );
        pageName = strUtils.firstLetterDeCapped(pageName);
        clickElement(getElementFromPage(buttonName, pageName, objectRepository), true);
    }

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

    public void center(String elementName, String pageName, Object objectRepository){
        log.new Info("Clicking " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName)
        );
        pageName = strUtils.firstLetterDeCapped(pageName);
        centerElement(getElementFromPage(elementName, pageName, objectRepository));
    }

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

    //@Given("Fill component input {} of {} component on the {} with text: {}")
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

    //@Given("Fill component form input on the {}")
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

    //@Given("Fill component form input of {} component on the {}")
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

    //@Given("Fill iFrame element {} of {} on the {} with text: {}")
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

    //@Given("Click i-frame element {} in {} on the {}")
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

    //@Given("Fill listed component input {} of {} component on the {} with text: {}")
    public void fillListedInput(String inputName, String listName, String componentName, String pageName, String input, Object objectRepository){
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
        List<WebElement> elements = getElementsFromComponent(listName, componentName, pageName, objectRepository);
        WebElement element = acquireNamedElementAmongst(elements, inputName);
        clearFillInput(element, input, false, true);
    }

    //@Given("Verify the text of {} on the {} to be: {}")
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

    //@Given("Verify text of element list {} on the {}")
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

    //@Given("Verify text of the component element {} of {} on the {} to be: {}")
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

    //@Given("Verify text of component element list {} of {} on the {}")
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

    //@Given("Verify presence of element {} on the {}")
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

    //@Given("Verify presence of the component element {} of {} on the {}")
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

    //@Given("Checking the presence of the element text on the {}")
    public void verifyPresenceText(String pageName, List<Map<String, String>> signForms) {
        String elementText;
        for (Map<String, String> form : signForms) {
            elementText = contextCheck(form.get("Input"));
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

    //@Given("Close the browser")
    public void closeBrowser(){
        driver.quit();
    }
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

    //@Given("Verify that component element {} of {} on the {} is in {} state")
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

    //@Given("If present, verify that component element {} of {} on the {} is in {} state")
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

    //@Given("Wait for absence of element {} on the {}")
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

    //@Given("Wait for absence of component element {} of {} on the {}")
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

    //@Given("Wait for element {} on the {} to be visible")
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

    //@Given("Wait for component element {} of {} on the {} to be visible")
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

    //@Given("Wait until element {} on the {} has {} value for its {} attribute")
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

    //@Given("Wait until component element {} of {} on the {} has {} value for its {} attribute")
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

    //@Given("Verify that element {} on the {} has {} value for its {} attribute")
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

    //@Given("Verify {} css attribute of element {} on the {} is {} ")
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

    //@Given("Verify that component element {} of {} on the {} has {} value for its {} attribute")
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

    //@Given("Select component by {} named {} from {} component list on the {} and verify that it has {} value for its {} attribute")
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

    //@Given("Select component named {} from {} component list on the {} and verify that the {} element has {} value for its {} attribute")
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

    //@Given("Select component named {} from {} component list on the {} and verify listed element {} of {} has {} value for its {} attribute")
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

    //@Given("Verify that element {} from {} list on the {} has {} value for its {} attribute")
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

    //@Given("Verify text of listed element {} from the {} on the {} is equal to {}")
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

    //@Given("Verify text of listed element from the {} on the {}")
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

    //@Given("Verify text of listed component element {} from the {} of {} on the {} is equal to {}")
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

    //@Given("Verify text of listed component element from the {} of {} on the {}")
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

    //@Given("Verify presence of listed component element {} of {} from {} list on the {}")
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

    //@Given("Verify that component element {} of {} from {} list on the {} has {} value for its {} attribute")
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

    //@Given("Verify the page is redirecting to the page {}")
    public void verifyCurrentUrl(String url) {
        url = contextCheck(url);
        log.new Info("The url contains " + url);
        Assert.assertTrue("The page is not redirected to: " + url, driver.getCurrentUrl().contains(url));
    }

    //@Given("Verify the url contains with the text {}")
    public void verifyTextUrl(String text) {
        log.new Info("The url contains " + text);
        Assert.assertTrue("The page is not directed to: " + text ,driver.getCurrentUrl().contains(text));
    }

    //@Given("Click the specific text {} button")
    public void clickButtonWithText(String buttonText, Boolean scroll) {
        this.clickElement(this.getElementByText(buttonText), scroll);
    }

    //@Given("Verify the booking email on the myReservation page to be {}")
    public void checkEmail(String buttonText, Boolean scroll) {
        this.clickElement(this.getElementByText(buttonText), scroll);
    }

    //@Given("Update context {} -> {}")
    public void updateContext(String key, String value){
        ContextStore.put(key, contextCheck(value));
    }

    //@Given("Clear component input field {} from {} component on the {}")
    public void clearInputField(String elementName, String componentName, String pageName, Object objectRepository){
        componentName = strUtils.firstLetterDeCapped(componentName);
        pageName = strUtils.firstLetterDeCapped(pageName);
        WebElement element = getElementFromComponent(elementName, componentName, pageName, objectRepository);
        element.clear();
    }

    //@Given("Press {} key on {} element of the {}")
    public void pressKey(Keys key, String elementName, String pageName, Object objectRepository){
        pageName = strUtils.firstLetterDeCapped(pageName);
        WebElement element = getElementFromPage(elementName, pageName, objectRepository);
        element.sendKeys(key);
    }

    //@Given("Press {} key on component element {} from {} component on the {}")
    public void pressKey(Keys key, String elementName, String componentName, String pageName, Object objectRepository){
        componentName = strUtils.firstLetterDeCapped(componentName);
        pageName = strUtils.firstLetterDeCapped(pageName);
        WebElement element = getElementFromComponent(elementName, componentName, pageName, objectRepository);
        element.sendKeys(key);
    }

    //@Given("Execute JS command: {}")
    public void executeJSCommand(String script) {
        executeScript(script);
    }

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
     * @param inputName
     * @param componentName
     * @param pageName
     * @param input
     * @param objectRepository
     */
    //@Given("Upload file on component input {} of {} component on the {} with file: {}")
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
}
