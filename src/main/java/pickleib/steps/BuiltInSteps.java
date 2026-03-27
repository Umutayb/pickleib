package pickleib.steps;

import com.google.common.collect.ImmutableMap;
import context.ContextStore;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.*;
import pickleib.driver.DriverFactory;
import pickleib.enums.Direction;
import pickleib.enums.ElementState;
import pickleib.enums.Navigation;
import pickleib.exceptions.PickleibVerificationException;
import pickleib.platform.driver.PickleibAppiumDriver;
import pickleib.runner.PickleibRunner;
import pickleib.utilities.element.ElementBundle;
import pickleib.utilities.element.interactions.InteractionBase;
import pickleib.utilities.interfaces.PolymorphicUtilities;
import pickleib.utilities.interfaces.repository.ElementRepository;
import pickleib.utilities.interfaces.repository.PageRepository;
import pickleib.web.driver.PickleibWebDriver;

import java.util.*;

import static pickleib.driver.DriverFactory.DriverType.*;
import static pickleib.utilities.DriverInspector.isPlatformElement;
import static utils.StringUtilities.*;
import static utils.StringUtilities.Color.*;
import static utils.StringUtilities.markup;
import static utils.arrays.ArrayUtilities.getRandomItemFrom;

/**
 * Provides ~56 generic Cucumber step definitions that replace the need for a manual CommonSteps class.
 * <p>
 * To activate these steps, add {@code "pickleib.steps"} to Cucumber's glue path. All steps delegate
 * to the interaction utilities inherited from {@link InteractionBase} and obtain elements from the
 * {@link pickleib.runner.PageObjectRegistry} managed by {@link PickleibRunner}.
 * </p>
 *
 * @author Umut Ay Bora
 * @since 2.1.0
 */
public class BuiltInSteps extends InteractionBase implements PageRepository {

    private static ElementRepository elementRepository;

    public BuiltInSteps() {
        super(true, true);
    }

    /**
     * Sets the element repository used by all built-in steps.
     * Call this from your project's Hooks or step class to use a custom repository
     * instead of the PickleibRunner's auto-discovered registry.
     */
    public static void setElementRepository(ElementRepository repository) {
        elementRepository = repository;
    }

    @Override
    public ElementRepository getElementRepository() {
        if (elementRepository != null) return elementRepository;
        return PickleibRunner.getRegistry();
    }

    // ─── Platform / Context ──────────────────────────────────────────────

    @Given("^Set default platform as (appium|selenium)$")
    public void setDefaultPlatform(DriverFactory.DriverType type) {
        defaultPlatform = type;
    }

    @Given("Update context {} -> {}")
    public void updateContext(String key, String value) {
        value = contextCheck(value);
        log.info("Updating context " + markup(BLUE, key) + " to " + markup(BLUE, value));
        ContextStore.put(key, value);
    }

    @Given("Save context value from {} context key to {}")
    public void saveContextValueFromOneContextKeyToAnother(String contextKey, String newContextKey) {
        Object value = ContextStore.get(contextKey);
        ContextStore.put(newContextKey, value);
        log.info("Value of " + markup(BLUE, contextKey) + markup(GREEN, " was saved to ") + markup(BLUE, newContextKey));
    }

    // ─── Navigation ─────────────────────────────────────────────────────

    @Given("Navigate to url: {}")
    public void getUrl(String url) {
        webInteractions.getUrl(url);
    }

    @Given("Navigate to test url")
    public void navigateToTestPage() {
        String url = ContextStore.get("test-url", "");
        webInteractions.navigate(url);
    }

    @Given("Go to the {} page")
    public void toPage(String page) {
        String url = PickleibWebDriver.get().getCurrentUrl();
        String pageUrl = url + page;
        webInteractions.navigate(pageUrl);
    }

    @Given("Refresh the page")
    public void refresh() {
        webInteractions.refresh();
    }

    @Given("^Navigate browser (BACKWARDS|FORWARDS)$")
    public void browserNavigate(Navigation direction) {
        webInteractions.navigateBrowser(direction);
    }

    // ─── Window / Tab ───────────────────────────────────────────────────

    @Given("Switch to the next tab")
    public void switchTab() {
        String parentHandle = webInteractions.switchWindowByHandle(null);
        ContextStore.put("parentHandle", parentHandle);
    }

    @Given("Switch back to the parent tab")
    public void switchToParentTab() {
        webInteractions.switchWindowByHandle(ContextStore.get("parentHandle").toString());
    }

    @Given("Switch to the tab with handle: {}")
    public void switchTab(String handle) {
        handle = contextCheck(handle);
        String parentHandle = webInteractions.switchWindowByHandle(handle);
        ContextStore.put("parentHandle", parentHandle);
    }

    @Given("Switch to the tab number {}")
    public void switchTab(Integer handle) {
        String parentHandle = webInteractions.switchWindowByIndex(handle);
        ContextStore.put("parentHandle", parentHandle);
    }

    @Given("Switch to the next active window")
    public void switchToNextActiveWindow() {
        PickleibAppiumDriver.get().switchTo().window(
                PickleibAppiumDriver.get().getWindowHandles().stream().findAny().orElseThrow()
        );
    }

    @Given("Save current url to context")
    public void saveCurrentUrl() {
        String currentUrl = webInteractions.driver.getCurrentUrl();
        ContextStore.put("currentUrl", currentUrl);
        log.info("Current URL is saved to context " + currentUrl);
    }

    @Given("Set window width & height as {} & {}")
    public void setFrameSize(Integer width, Integer height) {
        webInteractions.setWindowSize(width, height);
    }

    // ─── Storage ────────────────────────────────────────────────────────

    @Given("Add the following values to LocalStorage:")
    public void addLocalStorageValues(DataTable valueTable) {
        webInteractions.addLocalStorageValues(valueTable.asMap());
    }

    @Given("Add the following cookies:")
    public void addCookies(DataTable cookieTable) {
        webInteractions.addCookies(cookieTable.asMap());
    }

    @Given("Update value to {} for cookie named {}")
    public void updateCookie(String cookieValue, String cookieName) {
        webInteractions.updateCookies(cookieValue, cookieName);
    }

    @Given("Delete cookies")
    public void deleteCookies() {
        webInteractions.driver.manage().deleteAllCookies();
    }

    // ─── Click ──────────────────────────────────────────────────────────

    @Given("^(?:Click|Tap) the (\\w+) on the (\\w+)$")
    public void click(String buttonName, String pageName) {
        WebElement element = getElementRepository().acquireElementFromPage(buttonName, pageName);
        getInteractions(element).clickElement(element, buttonName, pageName, !isPlatformElement(element));
    }

    @Given("^(?:Click|Tap) button with (.+?(?:\\s+.+?)*) text(?: using (Mobile|Web) driver)?$")
    public void clickWithText(String text, String driverType) {
        getInteractions(getType(driverType)).clickByText(text);
    }

    @Given("Click button with {} css locator")
    public void clickWithLocator(String text) {
        webInteractions.clickByCssSelector(text);
    }

    @Given("^(?:Click|Tap) listed element (.+?(?:\\s+.+?)*) from (\\w+) list on the (\\w+)$")
    public void clickListedButton(String elementName, String listName, String pageName) {
        WebElement element = getElementRepository().acquireListedElementFromPage(elementName, listName, pageName);
        getInteractions(element).clickElement(element, elementName, pageName);
    }

    @Given("^Click listed attribute element that has (.+?(?:\\s+.+?)*) value for its (\\w+) attribute from (\\w+) list on the (\\w+)$")
    public void clickListedButtonByAttribute(String attributeValue, String attributeName, String listName, String pageName) {
        WebElement element = getElementRepository().acquireListedElementByAttribute(attributeName, attributeValue, listName, pageName);
        getInteractions(element).clickElement(element, attributeName + " attribute named element", pageName);
    }

    @Given("^If present, click the (\\w+) on the (\\w+)$")
    public void clickIfPresent(String elementName, String pageName) {
        try {
            WebElement element = getElementRepository().acquireElementFromPage(elementName, pageName);
            if (getInteractions(element).elementIs(element, elementName, pageName, ElementState.displayed))
                getInteractions(element).clickElement(element, elementName, pageName);
        } catch (WebDriverException ignored) {
            log.warning("The " + elementName + " was not present");
        }
    }

    @Given("^If enabled, (?:click|tap) the (\\w+) on the (\\w+)$")
    public void clickIfEnabled(String elementName, String pageName) {
        try {
            WebElement element = getElementRepository().acquireElementFromPage(elementName, pageName);
            if (getInteractions(element).elementIs(element, elementName, pageName, ElementState.enabled))
                getInteractions(element).clickElement(element, elementName, pageName);
        } catch (WebDriverException ignored) {
            log.warning("The " + elementName + " was not present");
        }
    }

    @Given("^Click towards the (\\w+) on the (\\w+)$")
    public void clickTowardsElement(String elementName, String pageName) {
        WebElement element = getElementRepository().acquireElementFromPage(elementName, pageName);
        getInteractions(element).clickTowards(element, elementName, pageName);
    }

    @Given("Click i-frame element {} in {} on the {}")
    public void clickIframeElement(String elementName, String iframeName, String pageName) {
        WebElement iframe = getElementRepository().acquireElementFromPage(iframeName, pageName);
        WebElement element = getElementRepository().acquireElementFromPage(elementName, pageName);
        webInteractions.clickIframeElement(iframe, element, elementName, iframeName, pageName);
    }

    // ─── Input ──────────────────────────────────────────────────────────

    @Given("^Fill input (\\w+) on the (\\w+) with (?:(un-verified|verified) )?text: (.+?(?:\\s+.+?)*)$")
    public void fill(String inputName, String pageName, String verify, String input) {
        input = contextCheck(input);
        WebElement inputElement = getElementRepository().acquireElementFromPage(inputName, pageName);
        PolymorphicUtilities interactions = getInteractions(inputElement);
        interactions.fillInputElement(
                inputElement,
                inputName,
                pageName,
                input,
                !isPlatformElement(inputElement),
                true,
                Objects.equals(verify, "verified")
        );
    }

    @Given("^Fill form input on the (\\w+)(?: using (Mobile|Web) driver)?$")
    public void fillForm(String pageName, String driverType, DataTable table) {
        List<ElementBundle<String>> inputBundles = getElementRepository().acquireElementList(table.asMaps(), pageName);
        PolymorphicUtilities interactions = driverType != null ?
                getInteractions(DriverFactory.DriverType.getType(driverType)) :
                getInteractions(getRandomItemFrom(inputBundles).element());
        interactions.fillForm(inputBundles, pageName);
    }

    @Given("^Fill listed input (\\w+) from (\\w+) list on the (\\w+) with text: (.+?(?:\\s+.+?)*)$")
    public void fillListedInput(String inputName, String listName, String pageName, String input) {
        WebElement inputElement = getElementRepository().acquireListedElementFromPage(inputName, listName, pageName);
        PolymorphicUtilities interactions = getInteractions(inputElement);
        interactions.fillInputElement(
                inputElement,
                inputName,
                pageName,
                input,
                !isPlatformElement(inputElement),
                !isPlatformElement(inputElement),
                true
        );
    }

    @Given("Fill iFrame element {} of {} on the {} with text: {}")
    public void fillIframeInput(String inputName, String iframeName, String pageName, String inputText) {
        WebElement iframe = getElementRepository().acquireElementFromPage(iframeName, pageName);
        WebElement element = getElementRepository().acquireElementFromPage(inputName, pageName);
        webInteractions.fillIframeInput(iframe, element, inputName, pageName, inputText);
    }

    // ─── Scroll / Center ────────────────────────────────────────────────

    @Given("^(?:Scroll|Swipe) (up|down|left|right) using (Mobile|Web) driver$")
    public void scrollTo(Direction direction, DriverFactory.DriverType driverType) {
        getInteractions(driverType).scrollInDirection(direction);
    }

    @Given("^(?:Scroll|Swipe) until listed (.+?(?:\\s+.+?)*) element from (\\w+) list is found on the (\\w+)$")
    public void swipeUntilElementFound(String elementText, String listName, String screenName) {
        List<WebElement> elements = getElementRepository().acquireElementsFromPage(listName, screenName);
        getInteractions(elements.get(0)).scrollInList(elementText, elements);
    }

    @Given("^(?:Scroll|Swipe) until element with exact text (.+?(?:\\s+.+?)*) is found using (Web|Mobile) driver$")
    public void swipeUntilElementFound(String elementText, DriverFactory.DriverType driverType) {
        getInteractions(driverType).scrollUntilFound(elementText);
    }

    @Given("Center the {} on the {}")
    public void center(String elementName, String pageName) {
        WebElement element = getElementRepository().acquireElementFromPage(elementName, pageName);
        webInteractions.centerElement(element, elementName, pageName);
    }

    @Given("Center element named {} on the {} from {}")
    public void centerListedElement(String elementName, String elementListName, String pageName) {
        elementName = contextCheck(elementName);
        WebElement element = getElementRepository().acquireListedElementFromPage(elementName, elementListName, pageName);
        webInteractions.centerElement(element, elementName, pageName);
    }

    // ─── Verification ───────────────────────────────────────────────────

    @Given("^Verify the text of (\\w+) on the (\\w+) to be: (.+?(?:\\s+.+?)*)$")
    public void verifyText(String elementName, String pageName, String expectedText) {
        expectedText = contextCheck(expectedText);
        WebElement element = getElementRepository().acquireElementFromPage(elementName, pageName);
        getInteractions(element).centerElement(element, elementName, pageName);
        pageName = firstLetterDeCapped(pageName);
        getInteractions(element).verifyText(element, elementName, pageName, expectedText);
    }

    @Given("^Verify the text of (\\w+) on the (\\w+) contains: (.+?(?:\\s+.+?)*)$")
    public void verifyContainsText(String elementName, String pageName, String expectedText) {
        WebElement element = getElementRepository().acquireElementFromPage(elementName, pageName);
        getInteractions(element).centerElement(element, elementName, pageName);
        pageName = firstLetterDeCapped(pageName);
        getInteractions(element).verifyElementContainsText(element, elementName, pageName, expectedText);
    }

    @Given("^Verify absence of element (\\w+) on the (\\w+)(?: using (Mobile|Web) driver)?$")
    public void verifyAbsence(String elementName, String pageName, String driverType) {
        WebElement element = getElementRepository().acquireElementFromPage(elementName, pageName);
        PolymorphicUtilities interactions = driverType != null ?
                getInteractions(DriverFactory.DriverType.getType(driverType)) :
                getInteractions(element);
        interactions.verifyElementState(element, elementName, pageName, ElementState.absent);
    }

    @Given("^Verify presence of element (\\w+) on the (\\w+)$")
    public void verifyPresence(String elementName, String pageName) {
        WebElement element = getElementRepository().acquireElementFromPage(elementName, pageName);
        getInteractions(element).verifyElementState(element, elementName, pageName, ElementState.displayed);
    }

    @Given("^Verify that element (\\w+) on the (\\w+) is in (\\w+) state$")
    public void verifyState(String elementName, String pageName, ElementState expectedState) {
        WebElement element = getElementRepository().acquireElementFromPage(elementName, pageName);
        getInteractions(element).verifyElementState(element, elementName, pageName, expectedState);
    }

    @Given("^Verify that element (\\w+) on the (\\w+) has (.+?(?:\\s+.+?)*) value for its (.+?(?:\\s+.+?)*) attribute$")
    public void verifyElementContainsAttribute(
            String elementName,
            String pageName,
            String attributeValue,
            String attributeName) {
        WebElement element = getElementRepository().acquireElementFromPage(elementName, pageName);
        getInteractions(element).verifyElementContainsAttribute(element, elementName, pageName, attributeName, attributeValue);
    }

    @Given("^Verify that (\\w+) attribute of element (\\w+) on the (\\w+) contains (.+?(?:\\s+.+?)*) value")
    public void verifyElementAttributeContainsValue(
            String attributeName,
            String elementName,
            String pageName,
            String value) {
        value = contextCheck(value);
        WebElement element = getElementRepository().acquireElementFromPage(elementName, pageName);
        webInteractions.verifyElementAttributeContainsValue(element, attributeName, elementName, pageName, value);
        log.info("-> " + markup(BLUE, value));
    }

    @Given("Verify {} css attribute of element {} on the {} is {}")
    public void verifyElementColor(
            String attributeName,
            String elementName,
            String pageName,
            String attributeValue) {
        WebElement element = getElementRepository().acquireElementFromPage(elementName, pageName);
        webInteractions.verifyElementColor(element, attributeName, elementName, pageName, attributeValue);
    }

    @Given("^Verify that element ([A-Za-z0-9 ]+) from (\\w+) list on the (\\w+) has (.+?(?:\\s+.+?)*) value for its (\\w+) attribute$")
    public void verifyListedElementContainsAttribute(
            String elementName,
            String listName,
            String pageName,
            String attributeValue,
            String attributeName) {
        WebElement element = getElementRepository().acquireListedElementFromPage(elementName, listName, pageName);
        getInteractions(element).verifyElementContainsAttribute(element, elementName, pageName, attributeName, attributeValue);
    }

    @Given("^Select listed element containing partial text (.+?(?:\\s+.+?)*) from the (\\w+) on the (\\w+) and verify its text contains (.+?(?:\\s+.+?)*)$")
    public void verifyListedElementContainsText(
            String elementName,
            String listName,
            String pageName,
            String expectedText) {
        WebElement element = getElementRepository().acquireListedElementFromPage(elementName, listName, pageName);
        getInteractions(element).verifyElementContainsText(element, elementName, pageName, expectedText);
    }

    @Given("Perform text verification for listed elements of {} list on the {} contains {}")
    public void textVerificationForListedElement(
            String listName,
            String pageName,
            String expectedText) {
        pageName = firstLetterDeCapped(pageName);
        log.info("Performing text verification for elements of the '" +
                highlighted(BLUE, listName) +
                highlighted(GRAY, "' list on the ") +
                highlighted(BLUE, pageName)
        );
        List<WebElement> elementList = getElementRepository().acquireElementsFromPage(listName, pageName);
        for (WebElement element : elementList) {
            if (element.getText().contains(expectedText))
                log.success("Element contains '" + expectedText + "' text!");
            else
                log.warning("Element not contains '" + expectedText + "' text! -> " + markup(PURPLE, element.getText()));
        }
    }

    @Given("Verify the page is redirecting to the page {}")
    @Given("Verify the url contains with the text {}")
    public void verifyTextUrl(String text) {
        webInteractions.verifyUrlContains(text);
        log.success("The url contains '" + text + "'");
    }

    // ─── Assertions ─────────────────────────────────────────────────────

    @Given("Assert that value of {} is equal to {}")
    public void verifyText(String expectedValue, String actualValue) {
        log.info("Checking values...");
        expectedValue = contextCheck(expectedValue).replaceAll(",", "");
        actualValue = contextCheck(actualValue).replaceAll(",", "");
        Assertions.assertEquals(expectedValue, actualValue, "Values not match!");
        log.success("Values verified as: " + actualValue);
    }

    @Given("Assert that value of {} is contains {}")
    public void assertContains(String expectedValue, String actualValue) {
        log.info("Checking values...");
        expectedValue = contextCheck(expectedValue).replaceAll(",", "");
        actualValue = contextCheck(actualValue).replaceAll(",", "");
        if (actualValue.contains(expectedValue)) log.success("Values verified as: " + actualValue);
        else throw new PickleibVerificationException("'" + actualValue + "' not contains '" + expectedValue + "'");
    }

    @Given("Assert that value of {} is not equal to {}")
    public void verifyNoText(String expectedValue, String actualValue) {
        log.info("Checking values...");
        expectedValue = contextCheck(expectedValue).replaceAll(",", "");
        actualValue = contextCheck(actualValue).replaceAll(",", "");
        Assertions.assertNotEquals(expectedValue, actualValue, "Values should not match!");
        log.success("Values verified as: " + actualValue);
    }

    @Given("Assert the value of {} attribute for {} element on {} is equal to {}")
    public void assertAttribute(String attributeName, String elementName, String pageName, String actualValue) {
        log.info("Acquiring the" + attributeName + " value...");
        WebElement element = getElementRepository().acquireElementFromPage(elementName, pageName);
        String value = element.getAttribute(attributeName);
        Assertions.assertEquals(value, actualValue, "Values not match!");
        log.success("Values verified as: " + actualValue);
    }

    // ─── Wait ───────────────────────────────────────────────────────────

    @Given("^Wait (\\d+) seconds$")
    public void wait(double duration) {
        PolymorphicUtilities.waitFor(duration);
    }

    @Given("^Wait for absence of element (\\w+) on the (\\w+)(?: using (Mobile|Web) driver)?$")
    public void waitUntilAbsence(String elementName, String pageName, String driverType) {
        WebElement element = getElementRepository().acquireElementFromPage(elementName, pageName);
        PolymorphicUtilities interactions = driverType != null ?
                getInteractions(DriverFactory.DriverType.getType(driverType)) :
                getInteractions(element);
        interactions.waitUntilAbsence(element, elementName, pageName);
    }

    @Given("^Wait for element (\\w+) on the (\\w+) to be visible$")
    public void waitUntilVisible(String elementName, String pageName) {
        WebElement element = getElementRepository().acquireElementFromPage(elementName, pageName);
        getInteractions(element).waitUntilVisible(element, elementName, pageName);
    }

    @Given("^Wait until element (\\w+) on the (\\w+) has (.+?(?:\\s+.+?)*) value for its (\\w+) attribute$")
    public void waitUntilElementContainsAttribute(
            String elementName,
            String pageName,
            String attributeValue,
            String attributeName) {
        attributeValue = contextCheck(attributeValue);
        WebElement element = getElementRepository().acquireElementFromPage(elementName, pageName);
        try {
            getInteractions(element).waitUntilElementContainsAttribute(element, elementName, pageName, attributeName, attributeValue);
        } catch (WebDriverException ignored) {
        }
    }

    // ─── Attribute ──────────────────────────────────────────────────────

    @Given("^Acquire the (\\w+) attribute of (\\w+) on the (\\w+)$")
    public void getAttributeValue(String attributeName, String elementName, String pageName) {
        WebElement element = getElementRepository().acquireElementFromPage(elementName, pageName);
        getInteractions(element).saveAttributeValue(element, attributeName, elementName, pageName);
    }

    // ─── File Upload ────────────────────────────────────────────────────

    @Given("Upload file on input {} on the {} with file: {}")
    public void uploadFile(String inputName, String pageName, String path) {
        WebElement inputElement = getElementRepository().acquireElementFromPage(inputName, pageName);
        webInteractions.fillInputElement(inputElement, path, false, false);
    }

    // ─── JavaScript ─────────────────────────────────────────────────────

    @Given("Execute JS command: {}")
    public void executeJSCommand(String script) {
        webInteractions.executeJSCommand(script);
    }

    @Given("Execute script {string} on element with text {string}")
    public void executeScript(String script, String elementText) {
        WebElement element = webInteractions.getElementContainingText(elementText);
        webInteractions.executeScript(script, element);
    }

    // ─── Mobile ─────────────────────────────────────────────────────────

    @Given("Execute mobile editor command: {}")
    public void executeMobileEditCommand(String command) {
        PickleibAppiumDriver.get().executeScript("mobile:performEditorAction", ImmutableMap.of("action", command));
    }

    @Given("Execute {} mobile command with {} parameter for {} from {}")
    public void executeGenericMobileCommand(String command, String parameter, String elementName, String pageName) {
        Map<String, String> params = new HashMap<>();
        platformInteractions.scrollUntilFound(elementName);
        WebElement element = platformInteractions.getElementByText(elementName, pageName);
        params.put(parameter, element.getAttribute("resourceId"));
        PickleibAppiumDriver.get().executeScript("mobile: " + command, params);
    }

    // ─── Event Listening ────────────────────────────────────────────────

    @Given("Listen to {} event & print {} object")
    public void listenGetAndPrintObjectStep(String eventName, String objectScript) {
        String listenerScript = "_ddm.listen(" + eventName + ");";
        webInteractions.listenGetAndPrintObject(listenerScript, eventName, objectScript);
    }

    @Given("Listen to {} event & verify value of {} node is {}")
    public void listenGetAndVerifyObjectStep(String eventName, String nodeSource, String expectedValue) {
        log.info("Verifying value of '" + nodeSource + "' node");
        String listenerScript = "_ddm.listen(" + eventName + ");";
        webInteractions.listenGetAndVerifyObject(listenerScript, eventName, nodeSource, expectedValue);
    }

    @Given("Listen to {} event & verify values of the following nodes")
    public void listenGetAndVerifyObjectStep(String eventName, DataTable nodeTable) {
        String listenerScript = "_ddm.listen(" + eventName + ");";
        webInteractions.listenGetAndVerifyObject(listenerScript, eventName, nodeTable.asMaps());
    }

    // ─── String Replacement ─────────────────────────────────────────────

    @Given("Perform text replacement on {} context by replacing {} value in {}")
    public void replaceAttributeValue(String attributeText, String splitValue, String attributeName) {
        attributeText = contextCheck(attributeText);
        log.info("Acquiring " + highlighted(BLUE, attributeText));
        log.info("Removing -> " + highlighted(BLUE, splitValue));
        ContextStore.put(attributeName, attributeText.replace(splitValue, ""));
        log.info("Updated value -> " + highlighted(GREEN, ContextStore.get(attributeName)));
    }

    // ─── Element Bundle Interaction ─────────────────────────────────────

    @Given("^Interact with element on the (\\w+) of (Mobile|Web) driver?$")
    public void pageElementInteraction(String pageName, String driverType, DataTable specifications) {
        List<ElementBundle<Map<String, String>>> bundles = getElementRepository().acquireElementBundlesFromPage(
                pageName,
                specifications.asMaps()
        );
        getInteractions(getType(driverType)).bundleInteraction(bundles, pageName);
    }
}
