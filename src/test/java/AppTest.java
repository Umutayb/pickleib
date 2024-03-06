import common.ObjectRepository;
import context.ContextStore;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import pages.ElementsPage;
import pages.FormsPage;
import pickleib.enums.Direction;
import pickleib.enums.ElementState;
import pickleib.utilities.element.acquisition.ElementAcquisition;
import pickleib.web.driver.PickleibWebDriver;
import pickleib.web.driver.WebDriverFactory;
import pickleib.web.interactions.WebInteractions;
import utils.Printer;
import utils.StringUtilities;
import utils.arrays.ArrayUtilities;
import java.util.List;
import java.util.Map;

import static pickleib.enums.Navigation.backwards;
import static pickleib.utilities.screenshot.ScreenCaptureUtility.captureScreen;

public class AppTest {
    String testWebsiteUrl = "http://127.0.0.1:8080/";
    WebDriver driver;
    WebInteractions webInteractions;
    Printer log = new Printer(AppTest.class);

    /**
     * Constructs an instance of the CommonStepUtilities class with the specific object repository.
     */

    @Before
    public void before() {
        ContextStore.loadProperties("test.properties", "pickleib.properties");
        WebDriverFactory.setHeadless(Boolean.parseBoolean(ContextStore.get("headless", "true")));
        WebDriverFactory.setUseWDM(false);
        PickleibWebDriver.initialize();
        this.driver = PickleibWebDriver.get();
        webInteractions = new WebInteractions();
        webInteractions.getUrl(testWebsiteUrl);
    }

    @After
    public void after() {
        if (Boolean.parseBoolean(ContextStore.get("takes-snapshots", "false")))
            captureScreen(StringUtilities.generateRandomString("failure#", 6, false, true), "jpg", (RemoteWebDriver) driver);
        PickleibWebDriver.terminate();
    }

    @Test
    public void navigateTest() {
        log.important(ContextStore.items().toString());
        log.info("webInteractions.navigate(page.trainingUrl) test");
        Assert.assertEquals("\"webInteractions.navigate(page.trainingUrl) test failed!", testWebsiteUrl, driver.getCurrentUrl());
        log.success("The webInteractions.navigate(page.trainingUrl) test pass!");
    }

    @Test
    public void navigateBrowserTest() {
        log.info("webInteractions.navigateBrowser(backwards) test");
        webInteractions.toPage("elements");
        webInteractions.navigateBrowser(backwards);
        Assert.assertEquals("webInteractions.navigateBrowser(backwards) test failed!", testWebsiteUrl, driver.getCurrentUrl());
        log.success("The webInteractions.navigateBrowser(backwards) test pass!");
    }

    @Test
    public void formTitleTest() {
        ElementAcquisition.Reflections<ObjectRepository> reflections = new ElementAcquisition.Reflections<>(ObjectRepository.class);
        List<WebElement> categories = reflections.getElementsFromPage("categories", "homePage");
        WebElement forms = ElementAcquisition.acquireNamedElementAmongst(categories, "Forms");
        webInteractions.clickElement(forms);
        WebElement title = reflections.getElementFromPage("title", "formsPage");
        Assert.assertEquals("formTest test failed!", "Forms Page", title.getText());
        log.success("The formTest test pass!");
    }

    @Test
    public void completeFormSubmissionTest() {//TODO: Try soft assertions
        ElementAcquisition.Reflections<ObjectRepository> reflections = new ElementAcquisition.Reflections<>(ObjectRepository.class);
        List<WebElement> categories = reflections.getElementsFromPage("categories", "homePage");
        WebElement forms = ElementAcquisition.acquireNamedElementAmongst(categories, "Forms");
        webInteractions.clickElement(forms);

        WebElement title = reflections.getElementFromPage("title", "formsPage");
        WebElement nameInput = reflections.getElementFromPage("nameInput", "formsPage");
        WebElement emailInput = reflections.getElementFromPage("emailInput", "formsPage");
        WebElement genderDropdown = reflections.getElementFromPage("genderDropdown", "formsPage");
        List<WebElement> genderOptions = reflections.getElementsFromPage("genderOptions", "formsPage");
        WebElement mobileInput = reflections.getElementFromPage("mobileInput", "formsPage");
        WebElement dateOfBirthInput = reflections.getElementFromPage("dateOfBirthInput", "formsPage");
        WebElement hobbiesInput = reflections.getElementFromPage("hobbiesInput", "formsPage");
        WebElement addressInput = reflections.getElementFromPage("addressInput", "formsPage");
        WebElement cityInput = reflections.getElementFromPage("cityInput", "formsPage");
        WebElement submitButton = reflections.getElementFromPage("submitButton", "formsPage");

        Assert.assertEquals("formTest test failed!", "Forms Page", title.getText());

        WebElement genderSelection = ArrayUtilities.getRandomItemFrom(genderOptions);

        String name = "Automated Tester";
        String email = "AutomatedTester@email.com";
        String gender = genderSelection.getText();
        String mobile = "0000000000";
        String hobbies = "Reading, Riding, Cooking";
        String address = "Prinsenstraat, 1015 DB";
        String city = "Amsterdam";

        Map<String, String> entries = Map.of(
                "Name", name,
                "Email", email,
                "Mobile", mobile,
                "Date of Birth", "2024-2-23", // value format changes due to website date formatting
                "Hobbies", hobbies,
                "Current Address", address,
                "Gender", gender
        );

        webInteractions.fillInputElement(nameInput, name, true, true, true);
        webInteractions.fillInputElement(emailInput, email, true, true, true);
        webInteractions.clickElement(genderDropdown);
        webInteractions.clickElement(genderSelection);
        webInteractions.fillInputElement(mobileInput, mobile, true, true, true);

        webInteractions.clickElement(dateOfBirthInput);
        WebElement monthButton = reflections.getElementFromPage("datePickerMonthsButton", "formsPage");
        webInteractions.clickElement(monthButton);
        List<WebElement> cells = reflections.getElementsFromPage("datePickerCells", "formsPage");
        webInteractions.clickElement(ElementAcquisition.acquireNamedElementAmongst(cells, "Feb"));
        WebElement yearButton = reflections.getElementFromPage("datePickerYearsButton", "formsPage");
        webInteractions.clickElement(yearButton);
        webInteractions.clickElement(ElementAcquisition.acquireNamedElementAmongst(cells, "2024"));
        webInteractions.clickElement(ElementAcquisition.acquireNamedElementAmongst(cells, "23"));
        WebElement datePickerSubmitButton = reflections.getElementFromPage("datePickerSubmitButton", "formsPage");
        webInteractions.clickElement(datePickerSubmitButton);

        webInteractions.fillInputElement(hobbiesInput, hobbies, true, true, true);
        webInteractions.fillInputElement(addressInput, address, true, true, true);
        webInteractions.fillInputElement(cityInput, city, true, true, true);
        webInteractions.clickElement(submitButton);

        WebElement submissionModal = reflections.getElementFromPage("table", "formsPage");
        webInteractions.wait.until(ExpectedConditions.visibilityOf(submissionModal));

        List<WebElement> submissionEntries = reflections.getElementsFromPage("submissionEntries", "formsPage");

        for (String entryKey : entries.keySet()) {
            WebElement entryValueElement = FormsPage.getEntryValue(entryKey, submissionEntries);
            Assert.assertEquals("Data mismatch!", entries.get(entryKey), entryValueElement.getText());
        }

        log.success("The completeFormSubmissionTest() passed!");
    }

    @Test
    public void scrollInContainerTest() {//TODO: Try soft assertions
        ElementAcquisition.Reflections<ObjectRepository> reflections = new ElementAcquisition.Reflections<>(ObjectRepository.class);
        List<WebElement> categories = reflections.getElementsFromPage("categories", "homePage");
        WebElement interactions = ElementAcquisition.acquireNamedElementAmongst(categories, "Interactions");
        webInteractions.clickElement(interactions);
        List<WebElement> tools = reflections.getElementsFromPage("tools", "interactionsPage");
        WebElement dropdownTool = ElementAcquisition.acquireNamedElementAmongst(tools, "Dropdown");
        webInteractions.clickElement(dropdownTool);
        WebElement countriesDropDown = reflections.getElementFromPage("countriesDropDown", "dropDownPage");
        webInteractions.clickElement(countriesDropDown);
        WebElement countriesContainer = reflections.getElementFromPage("countriesContainer", "dropDownPage");
        List<WebElement> countriesList = reflections.getElementsFromPage("countriesList", "dropDownPage");
        String countrySelection = "Ukraine";
        WebElement preSelection = ElementAcquisition.acquireNamedElementAmongst(countriesList, countrySelection);
        Assert.assertFalse("Selected country is already in view!!", webInteractions.elementIsInView(preSelection));
        WebElement country = webInteractions.scrollInContainer(countriesContainer, countriesList, countrySelection);
        Assert.assertTrue("Selected country is not in view!!", webInteractions.elementIsInView(country));
        log.success("scrollInContainerTest() pass!");
    }

    @Test
    public void scrollInDirectionTest(){
        ElementAcquisition.Reflections< ObjectRepository > reflections = new ElementAcquisition.Reflections<>(ObjectRepository.class);
        List<WebElement> categories = reflections.getElementsFromPage("categories", "homePage");
        WebElement interactions = ElementAcquisition.acquireNamedElementAmongst(categories, "Interactions");
        webInteractions.clickElement(interactions);
        List<WebElement> tools = reflections.getElementsFromPage("tools", "interactionsPage");
        WebElement dropdownTool = ElementAcquisition.acquireNamedElementAmongst(tools, "Tall Page");
        webInteractions.clickElement(dropdownTool);
        WebElement logo = reflections.getElementFromPage("logo", "tallPage");
        Assert.assertFalse("Logo is already in view!", webInteractions.elementIsInView(logo));
        webInteractions.scrollInDirection(Direction.down);
        webInteractions.scrollInDirection(Direction.down);
        Assert.assertTrue("Logo is not in view!", webInteractions.elementIsInView(logo));
    }

    @Test
    public void centerElementTest() {
        ElementAcquisition.Reflections<ObjectRepository> reflections = new ElementAcquisition.Reflections<>(ObjectRepository.class);
        List<WebElement> categories = reflections.getElementsFromPage("categories", "homePage");
        WebElement interactions = ElementAcquisition.acquireNamedElementAmongst(categories, "Interactions");
        webInteractions.clickElement(interactions);
        List<WebElement> tools = reflections.getElementsFromPage("tools", "interactionsPage");
        WebElement dropdownTool = ElementAcquisition.acquireNamedElementAmongst(tools, "Tall Page");
        webInteractions.clickElement(dropdownTool);
        WebElement logo = reflections.getElementFromPage("logo", "tallPage");
        Assert.assertFalse("Logo is already in view!", webInteractions.elementIsInView(logo));
        webInteractions.centerElement(logo);
        Assert.assertTrue("Logo is not in view!", webInteractions.elementIsInView(logo));
    }

    @Test
    public void openNewTabTest(){
        ElementAcquisition.Reflections< ObjectRepository > reflections = new ElementAcquisition.Reflections<>(ObjectRepository.class);
        List<WebElement> categories = reflections.getElementsFromPage("categories", "homePage");
        WebElement alertsAndWindows = ElementAcquisition.acquireNamedElementAmongst(categories, "Alerts, Frame & Windows");
        webInteractions.clickElement(alertsAndWindows);
        List<WebElement> buttons = reflections.getElementsFromPage("buttons", "AlertAndWindowsPage");
        WebElement newTabButton = ElementAcquisition.acquireNamedElementAmongst(buttons, "New Tab");
        webInteractions.clickElement(newTabButton);
        webInteractions.switchToNextTab();
        webInteractions.verifyCurrentUrl(testWebsiteUrl);
        log.success("openNewTabTest() pass!");
    }

    @Test
    public void openNewWindowTest(){
        ElementAcquisition.Reflections< ObjectRepository > reflections = new ElementAcquisition.Reflections<>(ObjectRepository.class);
        List<WebElement> categories = reflections.getElementsFromPage("categories", "homePage");
        WebElement alertsAndWindows = ElementAcquisition.acquireNamedElementAmongst(categories, "Alerts, Frame & Windows");
        webInteractions.clickElement(alertsAndWindows);
        List<WebElement> buttons = reflections.getElementsFromPage("buttons", "AlertAndWindowsPage");
        WebElement newWindowButton = ElementAcquisition.acquireNamedElementAmongst(buttons, "New Window");
        webInteractions.clickElement(newWindowButton);
        webInteractions.switchWindowByIndex(1);
        webInteractions.verifyCurrentUrl(testWebsiteUrl);
        log.success("openNewWindowTest() pass!");
    }

    @Test
    public void dismissAlertTest(){
        ElementAcquisition.Reflections< ObjectRepository > reflections = new ElementAcquisition.Reflections<>(ObjectRepository.class);
        List<WebElement> categories = reflections.getElementsFromPage("categories", "homePage");
        WebElement alertsAndWindows = ElementAcquisition.acquireNamedElementAmongst(categories, "Alerts, Frame & Windows");
        webInteractions.clickElement(alertsAndWindows);
        List<WebElement> buttons = reflections.getElementsFromPage("buttons", "AlertAndWindowsPage");
        WebElement newWindowsWithMessageButton = ElementAcquisition.acquireNamedElementAmongst(buttons, "New Window Message");
        webInteractions.clickElement(newWindowsWithMessageButton);
        webInteractions.getAlert().getText().equals("New window message!");
        webInteractions.getAlert().dismiss();
        webInteractions.switchToNextTab();
        webInteractions.verifyCurrentUrl(testWebsiteUrl + "alerts");
        log.success("dismissAlertTest() pass!");
    }

    @Test
    public void acceptAlertTest(){
        ElementAcquisition.Reflections< ObjectRepository > reflections = new ElementAcquisition.Reflections<>(ObjectRepository.class);
        List<WebElement> categories = reflections.getElementsFromPage("categories", "homePage");
        WebElement alertsAndWindows = ElementAcquisition.acquireNamedElementAmongst(categories, "Alerts, Frame & Windows");
        webInteractions.clickElement(alertsAndWindows);
        List<WebElement> buttons = reflections.getElementsFromPage("buttons", "AlertAndWindowsPage");
        WebElement newWindowsWithMessageButton = ElementAcquisition.acquireNamedElementAmongst(buttons, "New Window Message");
        webInteractions.clickElement(newWindowsWithMessageButton);
        webInteractions.getAlert().getText().equals("New window message!");
        webInteractions.getAlert().accept();
        webInteractions.waitUntilPageLoads(5);
        webInteractions.switchToNextTab();
        webInteractions.verifyCurrentUrl(testWebsiteUrl);
        log.success("acceptAlertTest() pass!");
    }


//  @Test
//  public void clickTest() {
//      List<WebElement> categories = pageObjectReflections.getElementsFromPage("clickMeButton", "pages.PageClass");
//      WebElement dynamicClickMessage = this.getAcquisition(Web).acquireElementFromPage("dynamicClickMessage", "pages.PageClass");

//      log.info("interactions.clickInteraction(page.clickMeButton) test");
//      webInteractions.getUrl(baseUrl + "buttons");
//      interactions.clickInteraction(clickMeButton);
//      Assert.assertEquals("interactions.clickInteraction(page.clickMeButton) test failed!", "You have done a dynamic click", dynamicClickMessage.getText());
//      log.success("The interactions.clickInteraction(page.clickMeButton) test pass!");
//  }
//
//    @Test
//    public void negativeClickTest() {
//        WebElement unClickableButton = this.getAcquisition(Web).acquireElementFromPage("unClickableButton", "pages.PageClass");
//        WebElement dynamicClickMessage = this.getAcquisition(Web).acquireElementFromPage("dynamicClickMessage", "pages.PageClass");
//
//        log.info("interactions.clickInteraction(page.clickMeButton) test");
//        webInteractions.getUrl(baseUrl + "buttons");
//        try {
//            interactions.clickInteraction(unClickableButton);
//        } catch (WebDriverException | PickleibException ignored) {
//        } finally {
//            Assert.assertTrue("Successful click message element is unexpectedly found!", interactions.elementStateIs(dynamicClickMessage, ElementState.absent));
//            log.success("The interactions.clickInteraction(page.clickMeButton) test pass!");
//        }
//    }
//
//    @Test
//    public void clickByTextTest() {
//        WebElement dynamicClickMessage = this.getAcquisition(Web).acquireElementFromPage("dynamicClickMessage", "pages.PageClass");
//
//        log.info("interactions.clickByText(\"Click Me\") test");
//        webInteractions.getUrl(baseUrl + "buttons");
//        interactions.clickByText("Click Me");
//        Assert.assertEquals("interactions.clickByText(\"Click Me\") test failed!", "You have done a dynamic click", dynamicClickMessage.getText());
//        log.success("The interactions.clickByText(\"Click Me\") test pass!");
//    }
//
//    @Test
//    public void scrollInContainerTest() {
//        pages.PageClass page = new pages.PageClass();
//        log.info("webInteractions.scrollInContainer(page.accordionLeftPanel,\"Interactions\") test");
//        webInteractions.getUrl(baseUrl + "elements");
//        webInteractions.scrollInContainer(page.accordionLeftPanel, "Interactions");
//        Assert.assertTrue("webInteractions.scrollInContainer(page.accordionLeftPanel,\"Interactions\") test failed!", page.interactionsAccordionBar.isDisplayed());
//        log.success("The webInteractions.scrollInContainer(page.accordionLeftPanel,\"Interactions\") test pass!");
//    }
//
//    @Test
//    public void switchWindowByHandleTest() {
//        log.info("webInteractions.switchWindowByHandle(parentTab) test");
//        webInteractions.getUrl(baseUrl + "links");
//        driver.findElement(By.cssSelector("#simpleLink")).click();
//        String parentTab = driver.getWindowHandles().toArray()[0].toString();
//        webInteractions.switchWindowByHandle(parentTab);
//        Assert.assertEquals("webInteractions.switchWindowByHandle(parentTab) test failed!", parentTab, driver.getWindowHandle());
//        log.success("The webInteractions.switchWindowByHandle(parentTab) test pass!");
//    }
//
//    @Test
//    public void switchWindowByIndexTest() {
//        log.info("webInteractions.switchWindowByIndex(0) test");
//        webInteractions.getUrl(baseUrl + "links");
//        driver.findElement(By.cssSelector("#simpleLink")).click();
//        webInteractions.switchWindowByIndex(0);
//        String parentTab = driver.getWindowHandles().toArray()[0].toString();
//        Assert.assertEquals("webInteractions.switchWindowByIndex(0) test failed!", parentTab, driver.getWindowHandle());
//        log.success("The webInteractions.switchWindowByIndex(0) test pass!");
//    }
//
//    @Test
//    public void verifyUrlContainsTest() {
//        log.info("webInteractions.verifyUrlContains(text) test");
//        String text = "qa";
//        webInteractions.getUrl(baseUrl);
//        webInteractions.verifyUrlContains(text);
//        Assert.assertTrue("webInteractions.verifyUrlContains(text) test failed!", driver.getCurrentUrl().contains(text));
//        log.success("The webInteractions.verifyUrlContains(text) test pass!");
//    }
//
//    @Test
//    public void verifyCurrentUrlTest() {
//        log.info("interactions.verifyCurrentUrl(baseUrl) test");
//        webInteractions.getUrl(baseUrl + "links");
//        interactions.verifyCurrentUrl(baseUrl + "links");
//        Assert.assertEquals("interactions.verifyCurrentUrl(baseUrl) test failed!", driver.getCurrentUrl(), baseUrl + "links");
//        log.success("The interactions.verifyCurrentUrl(baseUrl) test pass!");
//    }
//
//    @Test
//    public void verifyPageTitleTest() {
//        log.info("webInteractions.verifyPageTitle(title) test");
//        webInteractions.getUrl(baseUrl + "links");
//        String title = "DEMOQA";
//        webInteractions.verifyPageTitle(title);
//        Assert.assertEquals("webInteractions.verifyPageTitle(title) test failed!", driver.getTitle(), title);
//        log.success("The webInteractions.verifyPageTitle(title) test pass!");
//    }
//
//    @Test
//    public void clickWithJSTest() {
//        WebElement element = this.getAcquisition(Web).acquireElementFromPage("clickMeButton", "pages.PageClass");
//        WebElement dynamicClickMessage = this.getAcquisition(Web).acquireElementFromPage("dynamicClickMessage", "pages.PageClass");
//
//        log.info("webInteractions.clickWithJS(page.clickMeButton) test");
//        webInteractions.getUrl(baseUrl + "buttons");
//        webInteractions.clickWithJS(element);
//        Assert.assertEquals("webInteractions.clickWithJS(page.clickMeButton) test failed!", "You have done a dynamic click", dynamicClickMessage.getText());
//        log.success("The webInteractions.clickWithJS(page.clickMeButton) test pass!");
//    }
//
//    @Test
//    public void scrollWithJSTest() {
//        log.info("webInteractions.scrollWithJS(page.interactionsAccordionBar) test");
//        webInteractions.getUrl(baseUrl + "elements");
//        WebElement element = this.getAcquisition(Web).acquireElementFromPage("interactionsAccordionBar", "pages.PageClass");
//
//        webInteractions.scrollWithJS(element);
//        Assert.assertTrue("webInteractions.scrollWithJS(page.interactionsAccordionBar) test failed!", element.isDisplayed());
//        log.success("The webInteractions.scrollWithJS(page.interactionsAccordionBar) test pass!");
//    }
//
//    @Test
//    public void acquireNamedElementAmongstTest() {
//        log.info("acquire.acquireListedElementFromPage(elementName, listName, pageName) test");
//        webInteractions.getUrl(baseUrl);
//        WebElement element = this.getAcquisition(Web).acquireListedElementFromPage("Forms", "toolCards", "pageClass");
//        interactions.clickInteraction(element);
//        WebElement header = this.getAcquisition(Web).acquireElementFromPage("headerTitle", "pages.PageClass");
//        Assert.assertTrue("acquire.acquireListedElementFromPage(elementName, listName, pageName) test failed!", header.getText().contains("Forms"));
//        log.success("The acquire.acquireListedElementFromPage(elementName, listName, pageName) test pass!");
//    }
//
//    @Test
//    public void acquireNamedElementAmongstNegativeTest() {
//        log.info("acquire.acquireListedElementFromPage(elementName, listName, pageName) negative test");
//        webInteractions.getUrl(baseUrl);
//        try {
//            WebElement wrongElement = acquire.acquireListedElementFromPage("anythingElse", "toolCards", "pageClass");
//            interactions.clickInteraction(wrongElement);
//        } catch (NoSuchElementException ignored) {
//            log.success("Element is expectedly not found!");
//        } finally {
//            WebElement element = acquire.acquireListedElementFromPage("Forms", "toolCards", "pageClass");
//            Assert.assertTrue("acquire.acquireListedElementFromPage(elementName, listName, pageName) test failed!", element.getText().contains("Forms"));
//            log.success("The acquire.acquireListedElementFromPage(elementName, listName, pageName) negative test pass!");
//        }
//    }
//
//    @Test
//    public void verifyElementAttributeContainsValueTest() {
//        log.info("verifyElementAttributeContainsValue(element, elementName, pageName, attributeName, value) test");
//        webInteractions.getUrl(baseUrl);
//        WebElement element = acquire.acquireListedElementFromPage("Forms", "toolCards", "pageClass");
//        interactions.verifyElementAttributeContainsValue(element, "card", "pageName", "innerHTML", "Form");
//        log.success("The verifyElementAttributeContainsValue(element, elementName, pageName, attributeName, value) test pass!");
//    }
//
//    @Test
//    public void verifyElementAttributeContainsValueNegativeTest() {
//        log.info("verifyElementAttributeContainsValue(element, elementName, pageName, attributeName, value) negative test");
//        webInteractions.getUrl(baseUrl);
//        try {
//            WebElement element = acquire.acquireListedElementFromPage("Forms", "toolCards", "pageClass");
//            interactions.verifyElementAttributeContainsValue(element, "card", "pageName", "innerHTML", "Test");
//        } catch (PickleibVerificationException ignored) {
//            log.success("Attribute verification is (un)successful!");
//            return;
//        }
//        throw new PickleibVerificationException("Attribute verification failed!");
//    }
//
//    @Test
//    public void propertyReaderTest(){
//        Assert.assertNull("prop4 is not read", PropertyUtility.getProperty("prop4"));
//        Assert.assertNull("prop5 is not read", PropertyUtility.getProperty("prop5"));
//        PropertyUtility.loadProperties("src/test/resources/test.properties");
//        log.info("prop1 is read as: " + PropertyUtility.getProperty("prop1"));
//        log.info("prop2 is read as: " + PropertyUtility.getProperty("prop2"));
//        log.info("prop3 is read as: " + PropertyUtility.getProperty("prop3"));
//        log.info("prop4 is read as: " + PropertyUtility.getProperty("prop4"));
//        log.info("prop5 is read as: " + PropertyUtility.getProperty("prop5"));
//        Assert.assertNotNull("prop1 is not read", PropertyUtility.getProperty("prop1"));
//        Assert.assertNotNull("prop2 is not read", PropertyUtility.getProperty("prop2"));
//        Assert.assertNotNull("prop3 is not read", PropertyUtility.getProperty("prop3"));
//        Assert.assertNotNull("prop4 is not read", PropertyUtility.getProperty("prop4"));
//        Assert.assertNotNull("prop5 is not read", PropertyUtility.getProperty("prop5"));
//        log.success("Property test1 pass!");
//    }
//
}