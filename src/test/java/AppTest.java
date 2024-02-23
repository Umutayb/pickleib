import common.ObjectRepository;
import org.junit.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import pickleib.utilities.element.acquisition.ElementAcquisition;
import pickleib.utilities.steps.PageObjectStepUtilities;
import pickleib.web.driver.PickleibWebDriver;
import pickleib.web.driver.WebDriverFactory;
import pickleib.web.interactions.WebInteractions;
import java.util.List;

import static pickleib.enums.Navigation.backwards;

public class AppTest extends PageObjectStepUtilities<ObjectRepository> {
    static String testWebsiteUrl = "http://0.0.0.0:7450/";
    static WebDriver driver;

    /**
     * Constructs an instance of the CommonStepUtilities class with the specific object repository.
     */
    public AppTest() {
        super(ObjectRepository.class, false, true);
    }

    @Before
    public void before(){
        WebDriverFactory.setHeadless(true);
        WebDriverFactory.setDriverTimeout(120);
        PickleibWebDriver.initialize();
        webInteractions = new WebInteractions();
        driver = webInteractions.driver();
        webInteractions.navigate(testWebsiteUrl);
    }

    @After
    public void after() {
        PickleibWebDriver.terminate();
    }

    @Test
    public void navigateTest() {
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
    public void formTest(){
        List<WebElement> categories = pageObjectReflections.getElementsFromPage("categories", "homePage");
        WebElement forms = ElementAcquisition.acquireNamedElementAmongst(categories, "Forms");
        webInteractions.clickElement(forms);

        WebElement title = pageObjectReflections.getElementFromPage("title", "formsPage");
        Assert.assertEquals("formTest test failed!", "Forms Page", title.getText());
        log.success("The formTest test pass!");
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