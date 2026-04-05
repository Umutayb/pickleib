package pickleib.steps;

import com.google.common.collect.ImmutableMap;
import context.ContextStore;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.Select;
import pickleib.driver.DriverFactory;
import pickleib.enums.Direction;
import pickleib.enums.ElementState;
import pickleib.enums.Navigation;
import pickleib.exceptions.PickleibVerificationException;
import pickleib.platform.driver.PickleibAppiumDriver;
import pickleib.annotations.PageObject;
import pickleib.annotations.ScreenObject;
import pickleib.runner.ClasspathScanner;
import pickleib.runner.PageObjectRegistry;
import pickleib.runner.PickleibRunner;
import pickleib.utilities.element.ElementBundle;
import pickleib.utilities.element.FormInput;
import pickleib.utilities.element.acquisition.design.PageObjectJson;
import pickleib.utilities.element.interactions.InteractionBase;
import pickleib.utilities.interfaces.PolymorphicUtilities;
import pickleib.utilities.interfaces.repository.ElementRepository;
import pickleib.utilities.interfaces.repository.PageRepository;
import pickleib.web.driver.PickleibWebDriver;

import java.io.*;
import java.nio.file.*;
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

    // Shared with PickleibRunner for consistent auto-detection
    private static final String DEFAULT_PAGE_REPOSITORY = PickleibRunner.DEFAULT_PAGE_REPOSITORY;
    private static final String DEFAULT_SCAN_PACKAGE = PickleibRunner.DEFAULT_SCAN_PACKAGE;
    private static volatile ElementRepository elementRepository;
    private static volatile boolean autoDetected = false;

    static {
        installSkill();
    }

    /** Extracts the Claude Code skill from the classpath to skills/pickleib/SKILL.md if not already present. */
    private static void installSkill() {
        Path target = Paths.get("skills", "pickleib", "SKILL.md");
        if (Files.exists(target)) return;

        try (InputStream resource = BuiltInSteps.class.getClassLoader()
                .getResourceAsStream("META-INF/claude/skills/pickleib/SKILL.md")) {
            if (resource == null) return;
            Files.createDirectories(target.getParent());
            Files.copy(resource, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ignored) {}
    }

    /** Creates a new instance and clears cached page object instances to avoid stale driver proxies between scenarios. */
    public BuiltInSteps() {
        super(true, true);
        // Clear cached page object instances to avoid stale driver proxies between scenarios
        PickleibRunner.getRegistry().clearInstances();
    }

    /**
     * Sets the element repository used by all built-in steps.
     * Call this from your project's Hooks or step class to use a custom repository
     * instead of the PickleibRunner's auto-discovered registry.
     *
     * @param repository the element repository to use
     */
    public static void setElementRepository(ElementRepository repository) {
        elementRepository = repository;
    }

    @Override
    public ElementRepository getElementRepository() {
        if (elementRepository != null) return elementRepository;
        if (PickleibRunner.getRegistry().size() > 0) return PickleibRunner.getRegistry();
        if (!autoDetected) {
            synchronized (BuiltInSteps.class) {
                if (!autoDetected) {
                    autoDetectRepository();
                    autoDetected = true;
                }
            }
            if (elementRepository != null) return elementRepository;
        }
        return PickleibRunner.getRegistry();
    }

    /**
     * Auto-detects the element repository if none was explicitly set.
     * 1. Scans the 'pages' package for @PageObject/@ScreenObject classes
     * 2. Falls back to page-repository.json at the default path
     */
    private void autoDetectRepository() {
        // Try scanning for @PageObject/@ScreenObject classes first
        PageObjectRegistry registry = PickleibRunner.getRegistry();
        List<Class<?>> pageObjects = ClasspathScanner.scanForAnnotatedClasses(PageObject.class, "pages");
        for (Class<?> clazz : pageObjects) {
            PageObject po = clazz.getAnnotation(PageObject.class);
            registry.register(clazz, po.name(), po.platform());
            log.info("Auto-registered @PageObject: " + clazz.getSimpleName());
        }
        List<Class<?>> screenObjects = ClasspathScanner.scanForAnnotatedClasses(ScreenObject.class, "pages");
        for (Class<?> clazz : screenObjects) {
            ScreenObject so = clazz.getAnnotation(ScreenObject.class);
            registry.register(clazz, so.name(), so.platform());
            log.info("Auto-registered @ScreenObject: " + clazz.getSimpleName());
        }
        if (registry.size() > 0) return;

        // Fall back to JSON repository
        if (new java.io.File(DEFAULT_PAGE_REPOSITORY).exists()) {
            try {
                elementRepository = new PageObjectJson(
                        utils.FileUtilities.Json.parseJsonFile(DEFAULT_PAGE_REPOSITORY));
                log.info("Auto-detected page repository at " + DEFAULT_PAGE_REPOSITORY);
            } catch (Exception e) {
                log.warning("Failed to load " + DEFAULT_PAGE_REPOSITORY + ": " + e.getMessage());
            }
        }
    }

    // ─── Platform / Context ──────────────────────────────────────────────

    /** @param type the default driver type to use for interactions */
    @Given("^Set default platform as (appium|selenium)$")
    public void setDefaultPlatform(DriverFactory.DriverType type) {
        defaultPlatform = type;
    }

    /**
     * @param key   the context key
     * @param value the context value
     */
    @Given("Update context {} -> {}")
    public void updateContext(String key, String value) {
        value = contextCheck(value);
        log.info("Updating context " + markup(BLUE, key) + " to " + markup(BLUE, value));
        ContextStore.put(key, value);
    }

    /**
     * @param contextKey    the source context key
     * @param newContextKey the destination context key
     */
    @Given("Save context value from {} context key to {}")
    public void saveContextValueFromOneContextKeyToAnother(String contextKey, String newContextKey) {
        Object value = ContextStore.get(contextKey);
        ContextStore.put(newContextKey, value);
        log.info("Value of " + markup(BLUE, contextKey) + markup(GREEN, " was saved to ") + markup(BLUE, newContextKey));
    }

    // ─── Navigation ─────────────────────────────────────────────────────

    /** @param url the URL to navigate to */
    @Given("Navigate to url: {}")
    public void getUrl(String url) {
        webInteractions.getUrl(url);
    }

    /** Navigates to the URL stored in the {@code test-url} context key. */
    @Given("Navigate to test url")
    public void navigateToTestPage() {
        String url = ContextStore.get("test-url", "");
        webInteractions.navigate(url);
    }

    /** @param page the page path to append to the current URL */
    @Given("Go to the {} page")
    public void toPage(String page) {
        String url = PickleibWebDriver.get().getCurrentUrl();
        String pageUrl = url + page;
        webInteractions.navigate(pageUrl);
    }

    /** Refreshes the current page. */
    @Given("Refresh the page")
    public void refresh() {
        webInteractions.refresh();
    }

    /** @param direction the browser navigation direction (backwards or forwards) */
    @Given("^Navigate browser (backwards|forwards)$")
    public void browserNavigate(Navigation direction) {
        webInteractions.navigateBrowser(direction);
    }

    // ─── Window / Tab ───────────────────────────────────────────────────

    /** Switches to the next browser tab and saves the parent handle to context. */
    @Given("Switch to the next tab")
    public void switchTab() {
        String parentHandle = webInteractions.switchWindowByHandle(null);
        ContextStore.put("parentHandle", parentHandle);
    }

    /** Switches back to the parent tab using the saved handle from context. */
    @Given("Switch back to the parent tab")
    public void switchToParentTab() {
        webInteractions.switchWindowByHandle(ContextStore.get("parentHandle").toString());
    }

    /** @param handle the window handle to switch to */
    @Given("Switch to the tab with handle: {}")
    public void switchTab(String handle) {
        handle = contextCheck(handle);
        String parentHandle = webInteractions.switchWindowByHandle(handle);
        ContextStore.put("parentHandle", parentHandle);
    }

    /** @param handle the tab index to switch to */
    @Given("Switch to the tab number {}")
    public void switchTab(Integer handle) {
        String parentHandle = webInteractions.switchWindowByIndex(handle);
        ContextStore.put("parentHandle", parentHandle);
    }

    /** Switches to the next active window using the Appium driver. */
    @Given("Switch to the next active window")
    public void switchToNextActiveWindow() {
        PickleibAppiumDriver.get().switchTo().window(
                PickleibAppiumDriver.get().getWindowHandles().stream().findAny().orElseThrow()
        );
    }

    /** Saves the current browser URL to the context store under the key {@code currentUrl}. */
    @Given("Save current url to context")
    public void saveCurrentUrl() {
        String currentUrl = webInteractions.driver.getCurrentUrl();
        ContextStore.put("currentUrl", currentUrl);
        log.info("Current URL is saved to context " + currentUrl);
    }

    /**
     * @param width  the window width in pixels
     * @param height the window height in pixels
     */
    @Given("Set window width & height as {} & {}")
    public void setFrameSize(Integer width, Integer height) {
        webInteractions.setWindowSize(width, height);
    }

    // ─── Storage ────────────────────────────────────────────────────────

    /** @param valueTable the key-value pairs to add to local storage */
    @Given("Add the following values to LocalStorage:")
    public void addLocalStorageValues(DataTable valueTable) {
        webInteractions.addLocalStorageValues(valueTable.asMap());
    }

    /** @param cookieTable the cookie name-value pairs to add */
    @Given("Add the following cookies:")
    public void addCookies(DataTable cookieTable) {
        webInteractions.addCookies(cookieTable.asMap());
    }

    /**
     * @param cookieValue the new cookie value
     * @param cookieName  the name of the cookie to update
     */
    @Given("Update value to {} for cookie named {}")
    public void updateCookie(String cookieValue, String cookieName) {
        webInteractions.updateCookies(cookieValue, cookieName);
    }

    /** Deletes all browser cookies. */
    @Given("Delete cookies")
    public void deleteCookies() {
        webInteractions.driver.manage().deleteAllCookies();
    }

    // ─── Click ──────────────────────────────────────────────────────────

    /**
     * @param buttonName the name of the element to click
     * @param pageName   the page object containing the element
     */
    @Given("^(?:Click|Tap) the (\\w+) on the (\\w+)$")
    public void click(String buttonName, String pageName) {
        WebElement element = getElementRepository().acquireElementFromPage(buttonName, pageName);
        getInteractions(element).clickElement(element, buttonName, pageName, !isPlatformElement(element));
    }

    /**
     * @param text       the visible text of the button to click
     * @param driverType the driver type to use (mobile or web), or null for auto-detect
     */
    @Given("^(?:Click|Tap) button with (.+?(?:\\s+.+?)*) text(?: using (mobile|web) driver)?$")
    public void clickWithText(String text, String driverType) {
        getInteractions(getType(driverType)).clickByText(text);
    }

    /** @param text the CSS selector of the button to click */
    @Given("Click button with {} css locator")
    public void clickWithLocator(String text) {
        webInteractions.clickByCssSelector(text);
    }

    /**
     * @param elementName the name of the listed element to click
     * @param listName    the name of the element list
     * @param pageName    the page object containing the list
     */
    @Given("^(?:Click|Tap) listed element (.+?(?:\\s+.+?)*) from (\\w+) list on the (\\w+)$")
    public void clickListedButton(String elementName, String listName, String pageName) {
        WebElement element = getElementRepository().acquireListedElementFromPage(elementName, listName, pageName);
        getInteractions(element).clickElement(element, elementName, pageName);
    }

    /**
     * @param attributeValue the attribute value to match
     * @param attributeName  the attribute name to match against
     * @param listName       the name of the element list
     * @param pageName       the page object containing the list
     */
    @Given("^Click listed attribute element that has (.+?(?:\\s+.+?)*) value for its (\\w+) attribute from (\\w+) list on the (\\w+)$")
    public void clickListedButtonByAttribute(String attributeValue, String attributeName, String listName, String pageName) {
        WebElement element = getElementRepository().acquireListedElementByAttribute(attributeName, attributeValue, listName, pageName);
        getInteractions(element).clickElement(element, attributeName + " attribute named element", pageName);
    }

    /**
     * @param elementName the name of the element to click if present
     * @param pageName    the page object containing the element
     */
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

    /**
     * @param elementName the name of the element to click if enabled
     * @param pageName    the page object containing the element
     */
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

    /**
     * @param elementName the name of the element to click towards
     * @param pageName    the page object containing the element
     */
    @Given("^Click towards the (\\w+) on the (\\w+)$")
    public void clickTowardsElement(String elementName, String pageName) {
        WebElement element = getElementRepository().acquireElementFromPage(elementName, pageName);
        getInteractions(element).clickTowards(element, elementName, pageName);
    }

    /**
     * @param elementName the name of the element inside the iframe to click
     * @param iframeName  the name of the iframe element
     * @param pageName    the page object containing the elements
     */
    @Given("Click i-frame element {} in {} on the {}")
    public void clickIframeElement(String elementName, String iframeName, String pageName) {
        WebElement iframe = getElementRepository().acquireElementFromPage(iframeName, pageName);
        WebElement element = getElementRepository().acquireElementFromPage(elementName, pageName);
        webInteractions.clickIframeElement(iframe, element, elementName, iframeName, pageName);
    }

    // ─── Input ──────────────────────────────────────────────────────────

    /**
     * @param inputName the name of the input element
     * @param pageName  the page object containing the input
     * @param verify    verification mode (verified, un-verified, or null)
     * @param input     the text to fill into the input
     */
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

    /**
     * @param pageName   the page object containing the form inputs
     * @param driverType the driver type to use (mobile or web), or null for auto-detect
     * @param formInputs the list of form input specifications
     */
    @Given("^Fill form input on the (\\w+)(?: using (mobile|web) driver)?$")
    public void fillForm(String pageName, String driverType, List<FormInput> formInputs) {
        List<ElementBundle<String>> inputBundles = getElementRepository().acquireElementList(formInputs, pageName);
        PolymorphicUtilities interactions = driverType != null ?
                getInteractions(DriverFactory.DriverType.getType(driverType)) :
                getInteractions(getRandomItemFrom(inputBundles).element());
        interactions.fillForm(inputBundles, pageName);
    }

    /**
     * @param optionText  the visible text of the option to select
     * @param elementName the name of the select element
     * @param pageName    the page object containing the select element
     */
    @Given("^Select option (.+?(?:\\s+.+?)*) from (\\w+) on the (\\w+)$")
    public void selectOption(String optionText, String elementName, String pageName) {
        optionText = contextCheck(optionText);
        WebElement selectElement = getElementRepository().acquireElementFromPage(elementName, pageName);
        new Select(selectElement).selectByVisibleText(optionText);
        log.info("Selected " + highlighted(BLUE, optionText) + highlighted(GRAY, " from ") +
                highlighted(BLUE, elementName) + highlighted(GRAY, " on ") + highlighted(BLUE, pageName));
    }

    /**
     * @param inputName the name of the listed input element
     * @param listName  the name of the element list
     * @param pageName  the page object containing the list
     * @param input     the text to fill into the input
     */
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

    /**
     * @param inputName the name of the input element inside the iframe
     * @param iframeName the name of the iframe element
     * @param pageName   the page object containing the elements
     * @param inputText  the text to fill into the input
     */
    @Given("Fill iFrame element {} of {} on the {} with text: {}")
    public void fillIframeInput(String inputName, String iframeName, String pageName, String inputText) {
        WebElement iframe = getElementRepository().acquireElementFromPage(iframeName, pageName);
        WebElement element = getElementRepository().acquireElementFromPage(inputName, pageName);
        webInteractions.fillIframeInput(iframe, element, inputName, pageName, inputText);
    }

    // ─── Scroll / Center ────────────────────────────────────────────────

    /**
     * @param direction  the scroll direction
     * @param driverType the driver type to use (mobile or web)
     */
    @Given("^(?:Scroll|Swipe) (up|down|left|right) using (mobile|web) driver$")
    public void scrollTo(Direction direction, String driverType) {
        getInteractions(getType(driverType)).scrollInDirection(direction);
    }

    /**
     * @param elementText the text of the element to find
     * @param listName    the name of the element list to scroll through
     * @param screenName  the page object containing the list
     */
    @Given("^(?:Scroll|Swipe) until listed (.+?(?:\\s+.+?)*) element from (\\w+) list is found on the (\\w+)$")
    public void swipeUntilElementFound(String elementText, String listName, String screenName) {
        List<WebElement> elements = getElementRepository().acquireElementsFromPage(listName, screenName);
        getInteractions(elements.get(0)).scrollInList(elementText, elements);
    }

    /**
     * @param elementText the exact text of the element to find
     * @param driverType  the driver type to use (mobile or web)
     */
    @Given("^(?:Scroll|Swipe) until element with exact text (.+?(?:\\s+.+?)*) is found using (web|mobile) driver$")
    public void swipeUntilElementFound(String elementText, String driverType) {
        getInteractions(getType(driverType)).scrollUntilFound(elementText);
    }

    /**
     * @param elementName the name of the element to center
     * @param pageName    the page object containing the element
     */
    @Given("Center the {} on the {}")
    public void center(String elementName, String pageName) {
        WebElement element = getElementRepository().acquireElementFromPage(elementName, pageName);
        webInteractions.centerElement(element, elementName, pageName);
    }

    /**
     * @param elementName     the name of the listed element to center
     * @param elementListName the name of the element list
     * @param pageName        the page object containing the list
     */
    @Given("Center element named {} on the {} from {}")
    public void centerListedElement(String elementName, String elementListName, String pageName) {
        elementName = contextCheck(elementName);
        WebElement element = getElementRepository().acquireListedElementFromPage(elementName, elementListName, pageName);
        webInteractions.centerElement(element, elementName, pageName);
    }

    // ─── Verification ───────────────────────────────────────────────────

    /**
     * @param elementName  the name of the element to verify
     * @param pageName     the page object containing the element
     * @param expectedText the expected text content
     */
    @Given("^Verify the text of (\\w+) on the (\\w+) to be: (.+?(?:\\s+.+?)*)$")
    public void verifyText(String elementName, String pageName, String expectedText) {
        expectedText = contextCheck(expectedText);
        WebElement element = getElementRepository().acquireElementFromPage(elementName, pageName);
        getInteractions(element).centerElement(element, elementName, pageName);
        pageName = firstLetterDeCapped(pageName);
        getInteractions(element).verifyText(element, elementName, pageName, expectedText);
    }

    /**
     * @param elementName  the name of the element to verify
     * @param pageName     the page object containing the element
     * @param expectedText the text expected to be contained
     */
    @Given("^Verify the text of (\\w+) on the (\\w+) contains: (.+?(?:\\s+.+?)*)$")
    public void verifyContainsText(String elementName, String pageName, String expectedText) {
        WebElement element = getElementRepository().acquireElementFromPage(elementName, pageName);
        getInteractions(element).centerElement(element, elementName, pageName);
        pageName = firstLetterDeCapped(pageName);
        getInteractions(element).verifyElementContainsText(element, elementName, pageName, expectedText);
    }

    /**
     * @param elementName the name of the element to verify absence of
     * @param pageName    the page object containing the element
     * @param driverType  the driver type to use (mobile or web), or null for auto-detect
     */
    @Given("^Verify absence of element (\\w+) on the (\\w+)(?: using (mobile|web) driver)?$")
    public void verifyAbsence(String elementName, String pageName, String driverType) {
        WebElement element = getElementRepository().acquireElementFromPage(elementName, pageName);
        PolymorphicUtilities interactions = driverType != null ?
                getInteractions(DriverFactory.DriverType.getType(driverType)) :
                getInteractions(element);
        interactions.verifyElementState(element, elementName, pageName, ElementState.absent);
    }

    /**
     * @param elementName the name of the element to verify presence of
     * @param pageName    the page object containing the element
     */
    @Given("^Verify presence of element (\\w+) on the (\\w+)$")
    public void verifyPresence(String elementName, String pageName) {
        WebElement element = getElementRepository().acquireElementFromPage(elementName, pageName);
        getInteractions(element).verifyElementState(element, elementName, pageName, ElementState.displayed);
    }

    /**
     * @param elementName   the name of the element to verify
     * @param pageName      the page object containing the element
     * @param expectedState the expected element state
     */
    @Given("^Verify that element (\\w+) on the (\\w+) is in (\\w+) state$")
    public void verifyState(String elementName, String pageName, ElementState expectedState) {
        WebElement element = getElementRepository().acquireElementFromPage(elementName, pageName);
        getInteractions(element).verifyElementState(element, elementName, pageName, expectedState);
    }

    /**
     * @param elementName    the name of the element to verify
     * @param pageName       the page object containing the element
     * @param attributeValue the expected attribute value
     * @param attributeName  the name of the attribute to check
     */
    @Given("^Verify that element (\\w+) on the (\\w+) has (.+?(?:\\s+.+?)*) value for its (.+?(?:\\s+.+?)*) attribute$")
    public void verifyElementContainsAttribute(
            String elementName,
            String pageName,
            String attributeValue,
            String attributeName) {
        WebElement element = getElementRepository().acquireElementFromPage(elementName, pageName);
        getInteractions(element).verifyElementContainsAttribute(element, elementName, pageName, attributeName, attributeValue);
    }

    /**
     * @param attributeName the name of the attribute to check
     * @param elementName   the name of the element to verify
     * @param pageName      the page object containing the element
     * @param value         the value expected to be contained in the attribute
     */
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

    /**
     * @param attributeName  the CSS attribute name to check
     * @param elementName    the name of the element to verify
     * @param pageName       the page object containing the element
     * @param attributeValue the expected CSS attribute value
     */
    @Given("Verify {} css attribute of element {} on the {} is {}")
    public void verifyElementColor(
            String attributeName,
            String elementName,
            String pageName,
            String attributeValue) {
        WebElement element = getElementRepository().acquireElementFromPage(elementName, pageName);
        webInteractions.verifyElementColor(element, attributeName, elementName, pageName, attributeValue);
    }

    /**
     * @param elementName    the name of the listed element to verify
     * @param listName       the name of the element list
     * @param pageName       the page object containing the list
     * @param attributeValue the expected attribute value
     * @param attributeName  the name of the attribute to check
     */
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

    /**
     * @param elementName  the partial text of the listed element to select
     * @param listName     the name of the element list
     * @param pageName     the page object containing the list
     * @param expectedText the text expected to be contained in the element
     */
    @Given("^Select listed element containing partial text (.+?(?:\\s+.+?)*) from the (\\w+) on the (\\w+) and verify its text contains (.+?(?:\\s+.+?)*)$")
    public void verifyListedElementContainsText(
            String elementName,
            String listName,
            String pageName,
            String expectedText) {
        WebElement element = getElementRepository().acquireListedElementFromPage(elementName, listName, pageName);
        getInteractions(element).verifyElementContainsText(element, elementName, pageName, expectedText);
    }

    /**
     * @param listName     the name of the element list to verify
     * @param pageName     the page object containing the list
     * @param expectedText the text expected to be contained in each element
     */
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

    /** @param text the text expected to be contained in the current URL */
    @Given("Verify the page is redirecting to the page {}")
    @Given("Verify the url contains with the text {}")
    public void verifyTextUrl(String text) {
        webInteractions.verifyUrlContains(text);
        log.success("The url contains '" + text + "'");
    }

    // ─── Assertions ─────────────────────────────────────────────────────

    /**
     * @param expectedValue the expected value
     * @param actualValue   the actual value to compare against
     */
    @Given("Assert that value of {} is equal to {}")
    public void verifyText(String expectedValue, String actualValue) {
        log.info("Checking values...");
        expectedValue = contextCheck(expectedValue).replaceAll(",", "");
        actualValue = contextCheck(actualValue).replaceAll(",", "");
        Assertions.assertEquals(expectedValue, actualValue, "Values not match!");
        log.success("Values verified as: " + actualValue);
    }

    /**
     * @param expectedValue the value expected to be contained
     * @param actualValue   the actual value to check against
     */
    @Given("Assert that value of {} is contains {}")
    public void assertContains(String expectedValue, String actualValue) {
        log.info("Checking values...");
        expectedValue = contextCheck(expectedValue).replaceAll(",", "");
        actualValue = contextCheck(actualValue).replaceAll(",", "");
        if (actualValue.contains(expectedValue)) log.success("Values verified as: " + actualValue);
        else throw new PickleibVerificationException("'" + actualValue + "' not contains '" + expectedValue + "'");
    }

    /**
     * @param expectedValue the value expected to differ
     * @param actualValue   the actual value to compare against
     */
    @Given("Assert that value of {} is not equal to {}")
    public void verifyNoText(String expectedValue, String actualValue) {
        log.info("Checking values...");
        expectedValue = contextCheck(expectedValue).replaceAll(",", "");
        actualValue = contextCheck(actualValue).replaceAll(",", "");
        Assertions.assertNotEquals(expectedValue, actualValue, "Values should not match!");
        log.success("Values verified as: " + actualValue);
    }

    /**
     * @param attributeName the attribute name to check
     * @param elementName   the name of the element
     * @param pageName      the page object containing the element
     * @param actualValue   the expected attribute value
     */
    @Given("Assert the value of {} attribute for {} element on {} is equal to {}")
    public void assertAttribute(String attributeName, String elementName, String pageName, String actualValue) {
        log.info("Acquiring the" + attributeName + " value...");
        WebElement element = getElementRepository().acquireElementFromPage(elementName, pageName);
        String value = element.getAttribute(attributeName);
        Assertions.assertEquals(value, actualValue, "Values not match!");
        log.success("Values verified as: " + actualValue);
    }

    // ─── Wait ───────────────────────────────────────────────────────────

    /** @param duration the number of seconds to wait */
    @Given("^Wait (\\d+) seconds$")
    public void wait(double duration) {
        PolymorphicUtilities.waitFor(duration);
    }

    /**
     * @param elementName the name of the element to wait for absence of
     * @param pageName    the page object containing the element
     * @param driverType  the driver type to use (mobile or web), or null for auto-detect
     */
    @Given("^Wait for absence of element (\\w+) on the (\\w+)(?: using (mobile|web) driver)?$")
    public void waitUntilAbsence(String elementName, String pageName, String driverType) {
        WebElement element = getElementRepository().acquireElementFromPage(elementName, pageName);
        PolymorphicUtilities interactions = driverType != null ?
                getInteractions(DriverFactory.DriverType.getType(driverType)) :
                getInteractions(element);
        interactions.waitUntilAbsence(element, elementName, pageName);
    }

    /**
     * @param elementName the name of the element to wait for
     * @param pageName    the page object containing the element
     */
    @Given("^Wait for element (\\w+) on the (\\w+) to be visible$")
    public void waitUntilVisible(String elementName, String pageName) {
        WebElement element = getElementRepository().acquireElementFromPage(elementName, pageName);
        getInteractions(element).waitUntilVisible(element, elementName, pageName);
    }

    /**
     * @param elementName    the name of the element to wait for
     * @param pageName       the page object containing the element
     * @param attributeValue the expected attribute value
     * @param attributeName  the name of the attribute to check
     */
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

    /**
     * @param attributeName the name of the attribute to acquire
     * @param elementName   the name of the element
     * @param pageName      the page object containing the element
     */
    @Given("^Acquire the (\\w+) attribute of (\\w+) on the (\\w+)$")
    public void getAttributeValue(String attributeName, String elementName, String pageName) {
        WebElement element = getElementRepository().acquireElementFromPage(elementName, pageName);
        getInteractions(element).saveAttributeValue(element, attributeName, elementName, pageName);
    }

    // ─── File Upload ────────────────────────────────────────────────────

    /**
     * @param inputName the name of the file input element
     * @param pageName  the page object containing the input
     * @param path      the file path to upload
     */
    @Given("Upload file on input {} on the {} with file: {}")
    public void uploadFile(String inputName, String pageName, String path) {
        WebElement inputElement = getElementRepository().acquireElementFromPage(inputName, pageName);
        webInteractions.fillInputElement(inputElement, path, false, false);
    }

    // ─── JavaScript ─────────────────────────────────────────────────────

    /** @param script the JavaScript command to execute */
    @Given("Execute JS command: {}")
    public void executeJSCommand(String script) {
        webInteractions.executeJSCommand(script);
    }

    /**
     * @param script      the JavaScript to execute on the element
     * @param elementText the visible text of the target element
     */
    @Given("Execute script {string} on element with text {string}")
    public void executeScript(String script, String elementText) {
        WebElement element = webInteractions.getElementContainingText(elementText);
        webInteractions.executeScript(script, element);
    }

    // ─── Mobile ─────────────────────────────────────────────────────────

    /** @param command the mobile editor action to perform */
    @Given("Execute mobile editor command: {}")
    public void executeMobileEditCommand(String command) {
        PickleibAppiumDriver.get().executeScript("mobile:performEditorAction", ImmutableMap.of("action", command));
    }

    /**
     * @param command     the mobile command to execute
     * @param parameter   the parameter key for the command
     * @param elementName the name of the target element
     * @param pageName    the page object containing the element
     */
    @Given("Execute {} mobile command with {} parameter for {} from {}")
    public void executeGenericMobileCommand(String command, String parameter, String elementName, String pageName) {
        Map<String, String> params = new HashMap<>();
        platformInteractions.scrollUntilFound(elementName);
        WebElement element = platformInteractions.getElementByText(elementName, pageName);
        params.put(parameter, element.getAttribute("resourceId"));
        PickleibAppiumDriver.get().executeScript("mobile: " + command, params);
    }

    // ─── Event Listening ────────────────────────────────────────────────

    /**
     * @param eventName    the event name to listen for
     * @param objectScript the object script to print
     */
    @Given("Listen to {} event & print {} object")
    public void listenGetAndPrintObjectStep(String eventName, String objectScript) {
        String listenerScript = "_ddm.listen(" + eventName + ");";
        webInteractions.listenGetAndPrintObject(listenerScript, eventName, objectScript);
    }

    /**
     * @param eventName     the event name to listen for
     * @param nodeSource    the node source to verify
     * @param expectedValue the expected value of the node
     */
    @Given("Listen to {} event & verify value of {} node is {}")
    public void listenGetAndVerifyObjectStep(String eventName, String nodeSource, String expectedValue) {
        log.info("Verifying value of '" + nodeSource + "' node");
        String listenerScript = "_ddm.listen(" + eventName + ");";
        webInteractions.listenGetAndVerifyObject(listenerScript, eventName, nodeSource, expectedValue);
    }

    /**
     * @param eventName the event name to listen for
     * @param nodeTable the data table of node-value pairs to verify
     */
    @Given("Listen to {} event & verify values of the following nodes")
    public void listenGetAndVerifyObjectStep(String eventName, DataTable nodeTable) {
        String listenerScript = "_ddm.listen(" + eventName + ");";
        webInteractions.listenGetAndVerifyObject(listenerScript, eventName, nodeTable.asMaps());
    }

    // ─── String Replacement ─────────────────────────────────────────────

    /**
     * @param attributeText the context value to perform replacement on
     * @param splitValue    the substring to remove
     * @param attributeName the context key to store the result
     */
    @Given("Perform text replacement on {} context by replacing {} value in {}")
    public void replaceAttributeValue(String attributeText, String splitValue, String attributeName) {
        attributeText = contextCheck(attributeText);
        log.info("Acquiring " + highlighted(BLUE, attributeText));
        log.info("Removing -> " + highlighted(BLUE, splitValue));
        ContextStore.put(attributeName, attributeText.replace(splitValue, ""));
        log.info("Updated value -> " + highlighted(GREEN, ContextStore.get(attributeName)));
    }

    // ─── Element Bundle Interaction ─────────────────────────────────────

    /**
     * @param pageName       the page object containing the elements
     * @param driverType     the driver type to use (mobile or web)
     * @param specifications the data table of element interaction specifications
     */
    @Given("^Interact with element on the (\\w+) of (mobile|web) driver?$")
    public void pageElementInteraction(String pageName, String driverType, DataTable specifications) {
        List<ElementBundle<Map<String, String>>> bundles = getElementRepository().acquireElementBundlesFromPage(
                pageName,
                specifications.asMaps()
        );
        getInteractions(getType(driverType)).bundleInteraction(bundles, pageName);
    }
}
