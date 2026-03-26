import common.ObjectRepository;
import common.StatusWatcher;
import context.ContextStore;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import pickleib.driver.DriverLoader;
import pickleib.enums.Direction;
import pickleib.enums.ElementState;
import pickleib.enums.PageRepositoryDesign;
import pickleib.exceptions.PickleibException;
import pickleib.utilities.element.acquisition.ElementAcquisition;
import pickleib.utilities.interfaces.repository.ElementRepository;
import pickleib.utilities.steps.design.PageJsonDesign;
import pickleib.utilities.steps.design.PageObjectDesign;
import pickleib.web.driver.PickleibWebDriver;
import pickleib.web.driver.WebDriverFactory;
import pickleib.web.interactions.WebInteractions;
import utils.Printer;
import utils.StringUtilities;

import java.util.List;

import static pickleib.enums.ElementState.*;
import static pickleib.enums.Navigation.backwards;
import static pickleib.utilities.screenshot.ScreenCaptureUtility.captureScreen;
import static utils.StringUtilities.highlighted;

@ExtendWith(StatusWatcher.class)
public class AppTest {

    static String testWebsiteUrl;
    WebDriver driver;
    WebInteractions webInteractions;
    Printer log = new Printer(AppTest.class);
    ElementRepository objectRepository;
    PageRepositoryDesign design;

    @BeforeAll
    public static void setup() {
        WebDriverFactory.setHeadless(ContextStore.getBoolean("headless", true));
        WebDriverFactory.setUseWDM(false);
        testWebsiteUrl = ContextStore.get("test-url", "http://127.0.0.1:7457/");
    }

    @BeforeEach
    public void before() {
        this.driver = DriverLoader.loadWebDriver();
        design = PageRepositoryDesign.getDesign(ContextStore.get("page-repository-design", "json"));
        log.info("Page repository design: " + highlighted(StringUtilities.Color.PURPLE, design.name()));
        objectRepository = switch (design) {
            case json -> new PageJsonDesign("src/test/resources/page-repository.json")
                    .getElementRepository();
            case pom -> new PageObjectDesign<>(ObjectRepository.class)
                    .getElementRepository();
        };
        webInteractions = new WebInteractions();
        webInteractions.getUrl(testWebsiteUrl);
    }

    @AfterEach
    public void after(TestInfo testInfo) {
        if (StatusWatcher.TestStatus.isFailed())
            captureScreen(StringUtilities.generateRandomString(
                            testInfo.getDisplayName() + "-",
                    6,
                    false,
                    true),
                    "jpg",
                    (RemoteWebDriver) driver
            );
        PickleibWebDriver.terminate();
    }

    // ==================== Navigation Tests ====================

    @Test
    public void navigateTest() {
        log.info("webInteractions.navigate() test");
        webInteractions.navigate(testWebsiteUrl + "buttons");
        Assertions.assertTrue(driver.getCurrentUrl().contains("buttons"), "navigate() test failed!");
    }

    @Test
    public void navigateBrowserTest() {
        log.info("webInteractions.navigateBrowser(backwards) test");
        webInteractions.navigate(testWebsiteUrl + "forms");
        webInteractions.navigateBrowser(backwards);
        Assertions.assertEquals(testWebsiteUrl, driver.getCurrentUrl(), "navigateBrowser(backwards) test failed!");
    }

    @Test
    public void getUrlTest() {
        log.info("webInteractions.getUrl() test");
        webInteractions.getUrl(testWebsiteUrl + "alerts");
        Assertions.assertTrue(driver.getCurrentUrl().contains("alerts"), "getUrl() test failed!");
    }

    @Test
    public void verifyUrlContainsTest() {
        log.info("webInteractions.verifyUrlContains() test");
        webInteractions.navigate(testWebsiteUrl + "forms");
        webInteractions.verifyUrlContains("forms");
    }

    @Test
    public void verifyCurrentUrlTest() {
        log.info("webInteractions.verifyCurrentUrl() test");
        webInteractions.navigate(testWebsiteUrl + "buttons");
        webInteractions.verifyCurrentUrl(testWebsiteUrl + "buttons");
    }

    // ==================== Click Tests ====================

    @Test
    public void clickElementTest() {
        log.info("webInteractions.clickElement() test");
        webInteractions.navigate(testWebsiteUrl + "buttons");
        WebElement primaryButton = objectRepository.acquireElementFromPage("primaryButton", "buttonsPage");
        webInteractions.clickElement(primaryButton);
        WebElement resultText = objectRepository.acquireElementFromPage("resultText", "buttonsPage");
        Assertions.assertEquals("Primary", resultText.getText(), "clickElement() test failed!");
    }

    @Test
    public void clickElementWithScrollTest() {
        log.info("webInteractions.clickElement(scroll=true) test");
        webInteractions.navigate(testWebsiteUrl + "buttons");
        WebElement largeButton = objectRepository.acquireElementFromPage("largeButton", "buttonsPage");
        webInteractions.clickElement(largeButton, true);
        WebElement resultText = objectRepository.acquireElementFromPage("resultText", "buttonsPage");
        Assertions.assertEquals("Large", resultText.getText(), "clickElement(scroll=true) test failed!");
    }

    @Test
    public void clickButtonIfPresentTest() {
        log.info("webInteractions.clickButtonIfPresent() test");
        webInteractions.navigate(testWebsiteUrl + "buttons");
        WebElement secondaryButton = objectRepository.acquireElementFromPage("secondaryButton", "buttonsPage");
        webInteractions.clickButtonIfPresent(secondaryButton);
        WebElement resultText = objectRepository.acquireElementFromPage("resultText", "buttonsPage");
        Assertions.assertEquals("Secondary", resultText.getText(), "clickButtonIfPresent() test failed!");
    }

    @Test
    public void clickIfPresentTest() {
        log.info("webInteractions.clickIfPresent() test");
        webInteractions.navigate(testWebsiteUrl + "buttons");
        WebElement dangerButton = objectRepository.acquireElementFromPage("dangerButton", "buttonsPage");
        webInteractions.clickIfPresent(dangerButton);
        WebElement resultText = objectRepository.acquireElementFromPage("resultText", "buttonsPage");
        Assertions.assertEquals("Danger", resultText.getText(), "clickIfPresent() test failed!");
    }

    @Test
    public void clickTowardsTest() {
        log.info("webInteractions.clickTowards() test");
        webInteractions.navigate(testWebsiteUrl + "buttons");
        WebElement ghostButton = objectRepository.acquireElementFromPage("ghostButton", "buttonsPage");
        webInteractions.clickTowards(ghostButton);
        WebElement resultText = objectRepository.acquireElementFromPage("resultText", "buttonsPage");
        Assertions.assertEquals("Ghost", resultText.getText(), "clickTowards() test failed!");
    }

    @Test
    public void clickWithJSTest() {
        log.info("webInteractions.clickWithJS() test");
        webInteractions.navigate(testWebsiteUrl + "buttons");
        WebElement outlineButton = objectRepository.acquireElementFromPage("outlineButton", "buttonsPage");
        webInteractions.clickWithJS(outlineButton);
        WebElement resultText = objectRepository.acquireElementFromPage("resultText", "buttonsPage");
        Assertions.assertEquals("Outline", resultText.getText(), "clickWithJS() test failed!");
    }

    @Test
    public void clickButtonWithTextTest() {
        log.info("webInteractions.clickButtonWithText() test");
        webInteractions.navigate(testWebsiteUrl + "buttons");
        webInteractions.clickButtonWithText("Small");
        WebElement resultText = objectRepository.acquireElementFromPage("resultText", "buttonsPage");
        Assertions.assertEquals("Small", resultText.getText(), "clickButtonWithText() test failed!");
    }

    // ==================== Input Tests ====================

    @Test
    public void fillInputTest() {
        log.info("webInteractions.fillInput() test");
        webInteractions.navigate(testWebsiteUrl + "forms");
        WebElement nameInput = objectRepository.acquireElementFromPage("nameInput", "formsPage");
        webInteractions.fillInput(nameInput, "Test User");
        Assertions.assertEquals("Test User", nameInput.getAttribute("value"), "fillInput() test failed!");
    }

    @Test
    public void clearFillInputTest() {
        log.info("webInteractions.clearFillInput() test");
        webInteractions.navigate(testWebsiteUrl + "forms");
        WebElement nameInput = objectRepository.acquireElementFromPage("nameInput", "formsPage");
        webInteractions.fillInput(nameInput, "Initial");
        webInteractions.clearFillInput(nameInput, "Updated");
        Assertions.assertEquals("Updated", nameInput.getAttribute("value"), "clearFillInput() test failed!");
    }

    @Test
    public void fillAndVerifyTest() {
        log.info("webInteractions.fillAndVerify() test");
        webInteractions.navigate(testWebsiteUrl + "forms");
        WebElement emailInput = objectRepository.acquireElementFromPage("emailInput", "formsPage");
        webInteractions.fillAndVerify(emailInput, "test@test.com", false, true, true);
    }

    @Test
    public void clearInputFieldTest() {
        log.info("webInteractions.clearInputField() test");
        webInteractions.navigate(testWebsiteUrl + "forms");
        WebElement nameInput = objectRepository.acquireElementFromPage("nameInput", "formsPage");
        webInteractions.fillInput(nameInput, "Some text");
        webInteractions.clearInputField(nameInput);
        Assertions.assertEquals("", nameInput.getAttribute("value"), "clearInputField() test failed!");
    }

    // ==================== Element State Tests ====================

    @Test
    public void elementIsDisplayedTest() {
        log.info("webInteractions.elementIs(displayed) test");
        webInteractions.navigate(testWebsiteUrl + "buttons");
        WebElement primaryButton = objectRepository.acquireElementFromPage("primaryButton", "buttonsPage");
        Assertions.assertTrue(webInteractions.elementIs(primaryButton, displayed), "elementIs(displayed) test failed!");
    }

    @Test
    public void elementIsEnabledTest() {
        log.info("webInteractions.elementIs(enabled) test");
        webInteractions.navigate(testWebsiteUrl + "buttons");
        WebElement primaryButton = objectRepository.acquireElementFromPage("primaryButton", "buttonsPage");
        Assertions.assertTrue(webInteractions.elementIs(primaryButton, enabled), "elementIs(enabled) test failed!");
    }

    @Test
    public void elementIsDisabledTest() {
        log.info("webInteractions.elementIs(disabled) test");
        webInteractions.navigate(testWebsiteUrl + "buttons");
        WebElement disabledButton = objectRepository.acquireElementFromPage("disabledButton", "buttonsPage");
        Assertions.assertTrue(webInteractions.elementIs(disabledButton, disabled), "elementIs(disabled) test failed!");
    }

    @Test
    public void verifyElementStateTest() {
        log.info("webInteractions.verifyElementState() test");
        webInteractions.navigate(testWebsiteUrl + "buttons");
        WebElement primaryButton = objectRepository.acquireElementFromPage("primaryButton", "buttonsPage");
        webInteractions.verifyElementState(primaryButton, displayed);
    }

    @Test
    public void verifyElementTextTest() {
        log.info("webInteractions.verifyElementText() test");
        webInteractions.navigate(testWebsiteUrl + "buttons");
        WebElement primaryButton = objectRepository.acquireElementFromPage("primaryButton", "buttonsPage");
        webInteractions.verifyElementText(primaryButton, "Primary");
    }

    @Test
    public void verifyElementContainsTextTest() {
        log.info("webInteractions.verifyElementContainsText() test");
        webInteractions.navigate(testWebsiteUrl + "buttons");
        WebElement primaryButton = objectRepository.acquireElementFromPage("primaryButton", "buttonsPage");
        webInteractions.verifyElementContainsText(primaryButton, "primaryButton", "buttonsPage", "Prim");
    }

    // ==================== Attribute Tests ====================

    @Test
    public void elementContainsAttributeTest() {
        log.info("webInteractions.elementContainsAttribute() test");
        webInteractions.navigate(testWebsiteUrl + "buttons");
        WebElement disabledButton = objectRepository.acquireElementFromPage("disabledButton", "buttonsPage");
        Assertions.assertTrue(
                webInteractions.elementContainsAttribute(disabledButton, "data-testid", "btn-disabled"),
                "elementContainsAttribute() test failed!"
        );
    }

    @Test
    public void elementAttributeContainsValueTest() {
        log.info("webInteractions.elementAttributeContainsValue() test");
        webInteractions.navigate(testWebsiteUrl + "buttons");
        WebElement primaryButton = objectRepository.acquireElementFromPage("primaryButton", "buttonsPage");
        Assertions.assertTrue(
                webInteractions.elementAttributeContainsValue(primaryButton, "data-testid", "btn-primary"),
                "elementAttributeContainsValue() test failed!"
        );
    }

    @Test
    public void getAttributeTest() {
        log.info("webInteractions.getAttribute() test");
        webInteractions.navigate(testWebsiteUrl + "buttons");
        WebElement primaryButton = objectRepository.acquireElementFromPage("primaryButton", "buttonsPage");
        String value = webInteractions.getAttribute(primaryButton, "data-testid");
        Assertions.assertEquals("btn-primary", value, "getAttribute() test failed!");
    }

    // ==================== Scroll Tests ====================

    @Test
    public void scrollInDirectionTest() {
        log.info("webInteractions.scrollInDirection(down) test");
        webInteractions.navigate(testWebsiteUrl + "tall");
        WebElement lastSection = objectRepository.acquireElementFromPage("lastSection", "tallPage");
        Assertions.assertFalse(webInteractions.elementIsInView(lastSection), "Last section is already in view!");
        int maxScrolls = 30;
        while (!webInteractions.elementIsInView(lastSection) && maxScrolls-- > 0)
            webInteractions.scrollInDirection(Direction.down);
        Assertions.assertTrue(webInteractions.elementIsInView(lastSection), "Last section is not in view after scrolling!");
    }

    @Test
    public void centerElementTest() {
        log.info("webInteractions.centerElement() test");
        webInteractions.navigate(testWebsiteUrl + "tall");
        WebElement lastSection = objectRepository.acquireElementFromPage("lastSection", "tallPage");
        Assertions.assertFalse(webInteractions.elementIsInView(lastSection), "Last section is already in view!");
        webInteractions.centerElement(lastSection);
        Assertions.assertTrue(webInteractions.elementIsInView(lastSection), "Last section is not in view after centering!");
    }

    @Test
    public void scrollInListTest() {
        log.info("webInteractions.scrollInList() test");
        webInteractions.navigate(testWebsiteUrl + "tall");
        List<WebElement> sectionHeadings = objectRepository.acquireElementsFromPage("sectionHeadings", "tallPage");
        String targetText = "Section 10";
        WebElement foundElement = webInteractions.scrollInList(targetText, sectionHeadings);
        Assertions.assertNotNull(foundElement, "Element not found via scrollInList!");
        Assertions.assertTrue(foundElement.getText().contains(targetText), "Incorrect element found via scrollInList!");
    }

    @Test
    public void scrollInList_elementNotFoundTest() {
        log.info("webInteractions.scrollInList() element not found test");
        webInteractions.navigate(testWebsiteUrl + "tall");
        List<WebElement> sectionHeadings = objectRepository.acquireElementsFromPage("sectionHeadings", "tallPage");
        Assertions.assertThrows(RuntimeException.class, () ->
                webInteractions.scrollInList("NonExistentSection", sectionHeadings),
                "Expected RuntimeException was not thrown"
        );
    }

    @Test
    public void elementIsInViewTest() {
        log.info("webInteractions.elementIsInView() test");
        WebElement pageTitle = objectRepository.acquireElementFromPage("pageTitle", "homePage");
        Assertions.assertTrue(webInteractions.elementIsInView(pageTitle), "Title should be in view!");
    }

    // ==================== Window/Tab Tests ====================

    @Test
    public void openNewTabTest() {
        log.info("switchToNextTab test");
        webInteractions.navigate(testWebsiteUrl + "alerts");
        List<WebElement> buttons = objectRepository.acquireElementsFromPage("buttons", "alertsPage");
        WebElement newTabButton = ElementAcquisition.acquireNamedElementAmongst(buttons, "New Tab");
        webInteractions.clickElement(newTabButton);
        webInteractions.switchToNextTab();
        webInteractions.verifyCurrentUrl(testWebsiteUrl);
    }

    @Test
    public void switchWindowByIndexTest() {
        log.info("webInteractions.switchWindowByIndex() test");
        webInteractions.navigate(testWebsiteUrl + "alerts");
        List<WebElement> buttons = objectRepository.acquireElementsFromPage("buttons", "alertsPage");
        WebElement newWindowButton = ElementAcquisition.acquireNamedElementAmongst(buttons, "New Window");
        webInteractions.clickElement(newWindowButton);
        webInteractions.switchWindowByIndex(1);
        webInteractions.verifyCurrentUrl(testWebsiteUrl);
    }

    @Test
    public void switchWindowByHandleTest() {
        log.info("webInteractions.switchWindowByHandle() test");
        webInteractions.navigate(testWebsiteUrl + "alerts");
        String parentHandle = driver.getWindowHandle();
        List<WebElement> buttons = objectRepository.acquireElementsFromPage("buttons", "alertsPage");
        WebElement newTabButton = ElementAcquisition.acquireNamedElementAmongst(buttons, "New Tab");
        webInteractions.clickElement(newTabButton);
        webInteractions.switchToNextTab();
        webInteractions.switchWindowByHandle(parentHandle);
        Assertions.assertEquals(parentHandle, driver.getWindowHandle(), "switchWindowByHandle() test failed!");
    }

    // ==================== Alert Tests ====================

    @Test
    public void dismissAlertTest() {
        log.info("alert.dismiss() test");
        webInteractions.navigate(testWebsiteUrl + "alerts");
        WebElement clickMeButton = objectRepository.acquireElementFromPage("clickMeButton", "alertsPage");
        webInteractions.clickElement(clickMeButton);
        Assertions.assertEquals("Single click!", webInteractions.getAlert().getText(), "Alert text does not match!");
        webInteractions.getAlert().dismiss();
        try {
            webInteractions.getAlert().dismiss();
        } catch (Exception exception) {
            if (!(exception instanceof NoAlertPresentException))
                Assertions.fail("dismissAlertTest failed! Exception: " + exception.getLocalizedMessage());
        }
    }

    @Test
    public void acceptAlertTest() {
        log.info("alert.accept() test");
        webInteractions.navigate(testWebsiteUrl + "alerts");
        List<WebElement> buttons = objectRepository.acquireElementsFromPage("buttons", "alertsPage");
        WebElement newWindowMessageButton = ElementAcquisition.acquireNamedElementAmongst(buttons, "New Window Message");
        webInteractions.clickElement(newWindowMessageButton);
        Assertions.assertEquals("New window message!", webInteractions.getAlert().getText(), "Alert text does not match!");
        webInteractions.getAlert().accept();
        webInteractions.waitUntilPageLoads(5);
        webInteractions.switchToNextTab();
        webInteractions.verifyCurrentUrl(testWebsiteUrl);
    }

    // ==================== JavaScript Tests ====================

    @Test
    public void executeScriptTest() {
        log.info("webInteractions.executeScript() test");
        Object result = webInteractions.executeScript("return document.title;");
        Assertions.assertEquals("vue-test-app", result, "executeScript() test failed!");
    }

    // ==================== Element Acquisition Tests (JSON) ====================

    @Test
    public void acquireElementFromPageTest() {
        log.info("objectRepository.acquireElementFromPage() test");
        WebElement title = objectRepository.acquireElementFromPage("pageTitle", "homePage");
        Assertions.assertEquals("UI Components Test Suite", title.getText(), "acquireElementFromPage() test failed!");
    }

    @Test
    public void acquireElementsFromPageTest() {
        log.info("objectRepository.acquireElementsFromPage() test");
        List<WebElement> categories = objectRepository.acquireElementsFromPage("categories", "homePage");
        Assertions.assertEquals(8, categories.size(), "acquireElementsFromPage() test failed - expected 8 categories!");
    }

    @Test
    public void acquireListedElementFromPageTest() {
        log.info("objectRepository.acquireListedElementFromPage() test");
        WebElement formsCard = objectRepository.acquireListedElementFromPage("Forms", "categories", "homePage");
        Assertions.assertTrue(formsCard.getText().contains("Forms"), "acquireListedElementFromPage() test failed!");
    }

    @Test
    public void acquireListedElementByAttributeTest() {
        log.info("objectRepository.acquireListedElementByAttribute() test");
        WebElement formsCard = objectRepository.acquireListedElementByAttribute(
                "href", testWebsiteUrl + "forms", "categories", "homePage"
        );
        Assertions.assertTrue(formsCard.getText().contains("Forms"), "acquireListedElementByAttribute() test failed!");
    }

    // ==================== Error Cases ====================

    @Test
    public void pseudoElementJsonTest() {
        if (design.equals(PageRepositoryDesign.json)) {
            try {
                objectRepository.acquireElementsFromPage("PSEUDO_ELEMENT", "homePage");
            } catch (PickleibException exception) {
                log.warning(exception.getMessage());
                Assertions.assertEquals(
                        "\"PSEUDO_ELEMENT\" does not exist, or has no locators in \"HomePage\"!",
                        exception.getMessage(),
                        "Unexpected exception!"
                );
            }
        } else log.info("Skipping json design specific test.");
    }

    @Test
    public void pseudoPageJsonTest() {
        if (design.equals(PageRepositoryDesign.json)) {
            try {
                objectRepository.acquireElementsFromPage("PSEUDO_ELEMENT", "PSEUDO_PAGE");
            } catch (PickleibException exception) {
                log.warning(exception.getMessage());
                Assertions.assertEquals(
                        "\"PSEUDO_PAGE\" does not exist in page object repository json!",
                        exception.getMessage(),
                        "Unexpected exception!"
                );
            }
        } else log.info("Skipping json design specific test.");
    }

    // ==================== Form Submission Test ====================

    @Test
    public void completeFormSubmissionTest() {
        webInteractions.navigate(testWebsiteUrl + "forms");

        WebElement title = objectRepository.acquireElementFromPage("title", "formsPage");
        WebElement nameInput = objectRepository.acquireElementFromPage("nameInput", "formsPage");
        WebElement emailInput = objectRepository.acquireElementFromPage("emailInput", "formsPage");
        WebElement genderDropdown = objectRepository.acquireElementFromPage("genderDropdown", "formsPage");
        List<WebElement> genderOptions = objectRepository.acquireElementsFromPage("genderOptions", "formsPage");
        WebElement mobileInput = objectRepository.acquireElementFromPage("mobileInput", "formsPage");
        WebElement hobbiesInput = objectRepository.acquireElementFromPage("hobbiesInput", "formsPage");
        WebElement addressInput = objectRepository.acquireElementFromPage("addressInput", "formsPage");
        WebElement cityInput = objectRepository.acquireElementFromPage("cityInput", "formsPage");
        WebElement submitButton = objectRepository.acquireElementFromPage("submitButton", "formsPage");

        Assertions.assertEquals("Submission Form", title.getText(), "Form title mismatch!");

        webInteractions.fillInputElement(nameInput, "Automated Tester", true, true);
        webInteractions.fillInputElement(emailInput, "test@email.com", true, true);
        webInteractions.clickElement(genderDropdown);
        webInteractions.clickElement(genderOptions.get(0));
        webInteractions.fillInputElement(mobileInput, "0000000000", true, true);
        webInteractions.fillInputElement(hobbiesInput, "Reading, Coding", true, true);
        webInteractions.fillInputElement(addressInput, "123 Test Street", true, true);
        webInteractions.fillInputElement(cityInput, "Amsterdam", true, true);
        webInteractions.clickElement(submitButton);
    }

    // ==================== Homepage Navigation Test ====================

    @Test
    public void homepageCategoryNavigationTest() {
        log.info("Homepage category navigation test");
        List<WebElement> categories = objectRepository.acquireElementsFromPage("categories", "homePage");
        WebElement formsCard = ElementAcquisition.acquireNamedElementAmongst(categories, "Forms");
        webInteractions.clickElement(formsCard);
        webInteractions.verifyUrlContains("forms");
    }

    // ==================== Element State: absent ====================

    @Test
    public void elementIsAbsentTest() {
        log.info("webInteractions.elementIs(absent) test");
        webInteractions.navigate(testWebsiteUrl + "buttons");
        // The loading button's spinner should not make the result text absent
        // but let's test absent with the submission table which doesn't exist on buttons page
        // We test absent by checking an element that is hidden
        webInteractions.navigate(testWebsiteUrl + "forms");
        WebElement table = objectRepository.acquireElementFromPage("table", "formsPage");
        Assertions.assertTrue(webInteractions.elementIs(table, absent), "elementIs(absent) test failed - table should be absent before form submit!");
    }
}
