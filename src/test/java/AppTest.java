import org.junit.*;
import org.openqa.selenium.*;
import pickleib.enums.Direction;
import pickleib.enums.ElementState;
import pickleib.exceptions.PickleibException;
import pickleib.exceptions.PickleibVerificationException;
import pickleib.utilities.CommonStepUtilities;
import pickleib.utilities.element.ElementAcquisition;
import pickleib.utilities.element.ElementInteractions;
import pickleib.web.driver.PickleibWebDriver;
import pickleib.web.driver.WebDriverFactory;
import pickleib.web.interactions.WebInteractions;
import utils.PropertyUtility;

import java.time.Duration;

import static org.openqa.selenium.PageLoadStrategy.EAGER;
import static pickleib.driver.DriverFactory.DriverType.Web;
import static pickleib.enums.Navigation.backwards;
import static pickleib.web.driver.PickleibWebDriver.driver;

public class AppTest extends CommonStepUtilities<ObjectRepository> {

    public ElementAcquisition.PageObjectModel<ObjectRepository> acquire;
    public ElementAcquisition.Reflections<ObjectRepository> reflections;
    public ElementInteractions interactions;
    public WebInteractions webInteractions;

    String trainingUrl = "https://www.toolsqa.com/selenium-training/";
    String baseUrl = "https://demoqa.com/";

    /**
     * Constructs an instance of the CommonStepUtilities class with the specific object repository.
     */
    public AppTest() {
        super(ObjectRepository.class);
    }

    @Before
    public void before() {
        WebDriverFactory.setHeadless(false);
        WebDriverFactory.setDriverTimeout(120);
        WebDriverFactory.setUseWDM(true);
        WebDriverFactory.setLoadStrategy(EAGER);
        PickleibWebDriver.initialize();
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(120));
        acquire = new ElementAcquisition.PageObjectModel<>(PickleibWebDriver.driver, ObjectRepository.class);
        reflections = new ElementAcquisition.Reflections<>(PickleibWebDriver.driver, ObjectRepository.class);
        interactions = new ElementInteractions(PickleibWebDriver.driver, Web);
        webInteractions = new WebInteractions();
    }

    @After
    public void after() {
        PickleibWebDriver.terminate();
    }

    @Test
    public void navigateTest() {
        log.info("webInteractions.navigate(page.trainingUrl) test");
        webInteractions.navigate(trainingUrl);
        Assert.assertEquals("\"webInteractions.navigate(page.trainingUrl) test failed!", driver.getCurrentUrl(), trainingUrl);
        log.success("The webInteractions.navigate(page.trainingUrl) test pass!");
    }

    @Test
    public void navigateBrowserTest() {
        log.info("webInteractions.navigateBrowser(backwards) test");
        webInteractions.getUrl(baseUrl);
        webInteractions.toPage("buttons");
        webInteractions.navigateBrowser(backwards);
        Assert.assertEquals("webInteractions.navigateBrowser(backwards) test failed!", driver.getCurrentUrl(), baseUrl);
        log.success("The webInteractions.navigateBrowser(backwards) test pass!");
    }

    @Test
    public void clickTest() {
        WebElement clickMeButton = this.getAcquisition(Web).acquireElementFromPage("clickMeButton", "PageClass");
        WebElement dynamicClickMessage = this.getAcquisition(Web).acquireElementFromPage("dynamicClickMessage", "PageClass");

        log.info("interactions.clickInteraction(page.clickMeButton) test");
        webInteractions.getUrl(baseUrl + "buttons");
        interactions.clickInteraction(clickMeButton);
        Assert.assertEquals("interactions.clickInteraction(page.clickMeButton) test failed!", "You have done a dynamic click", dynamicClickMessage.getText());
        log.success("The interactions.clickInteraction(page.clickMeButton) test pass!");
    }

    @Test
    public void negativeClickTest() {
        WebElement unClickableButton = this.getAcquisition(Web).acquireElementFromPage("unClickableButton", "PageClass");
        WebElement dynamicClickMessage = this.getAcquisition(Web).acquireElementFromPage("dynamicClickMessage", "PageClass");

        log.info("interactions.clickInteraction(page.clickMeButton) test");
        webInteractions.getUrl(baseUrl + "buttons");
        try {
            interactions.clickInteraction(unClickableButton);
        } catch (WebDriverException | PickleibException ignored) {
        } finally {
            Assert.assertTrue("Successful click message element is unexpectedly found!", interactions.elementStateIs(dynamicClickMessage, ElementState.absent));
            log.success("The interactions.clickInteraction(page.clickMeButton) test pass!");
        }
    }

    @Test
    public void clickByTextTest() {
        WebElement dynamicClickMessage = this.getAcquisition(Web).acquireElementFromPage("dynamicClickMessage", "PageClass");

        log.info("interactions.clickByText(\"Click Me\") test");
        webInteractions.getUrl(baseUrl + "buttons");
        interactions.clickByText("Click Me");
        Assert.assertEquals("interactions.clickByText(\"Click Me\") test failed!", "You have done a dynamic click", dynamicClickMessage.getText());
        log.success("The interactions.clickByText(\"Click Me\") test pass!");
    }

    @Test
    public void scrollInContainerTest() {
        PageClass page = new PageClass();
        log.info("webInteractions.scrollInContainer(page.accordionLeftPanel,\"Interactions\") test");
        webInteractions.getUrl(baseUrl + "elements");
        webInteractions.scrollInContainer(page.accordionLeftPanel, "Interactions");
        Assert.assertTrue("webInteractions.scrollInContainer(page.accordionLeftPanel,\"Interactions\") test failed!", page.interactionsAccordionBar.isDisplayed());
        log.success("The webInteractions.scrollInContainer(page.accordionLeftPanel,\"Interactions\") test pass!");
    }

    @Test
    public void switchWindowByHandleTest() {
        log.info("webInteractions.switchWindowByHandle(parentTab) test");
        webInteractions.getUrl(baseUrl + "links");
        driver.findElement(By.cssSelector("#simpleLink")).click();
        String parentTab = driver.getWindowHandles().toArray()[0].toString();
        webInteractions.switchWindowByHandle(parentTab);
        Assert.assertEquals("webInteractions.switchWindowByHandle(parentTab) test failed!", parentTab, driver.getWindowHandle());
        log.success("The webInteractions.switchWindowByHandle(parentTab) test pass!");
    }

    @Test
    public void switchWindowByIndexTest() {
        log.info("webInteractions.switchWindowByIndex(0) test");
        webInteractions.getUrl(baseUrl + "links");
        driver.findElement(By.cssSelector("#simpleLink")).click();
        webInteractions.switchWindowByIndex(0);
        String parentTab = driver.getWindowHandles().toArray()[0].toString();
        Assert.assertEquals("webInteractions.switchWindowByIndex(0) test failed!", parentTab, driver.getWindowHandle());
        log.success("The webInteractions.switchWindowByIndex(0) test pass!");
    }

    @Test
    public void verifyUrlContainsTest() {
        log.info("webInteractions.verifyUrlContains(text) test");
        String text = "qa";
        webInteractions.getUrl(baseUrl);
        webInteractions.verifyUrlContains(text);
        Assert.assertTrue("webInteractions.verifyUrlContains(text) test failed!", driver.getCurrentUrl().contains(text));
        log.success("The webInteractions.verifyUrlContains(text) test pass!");
    }

    @Test
    public void verifyCurrentUrlTest() {
        log.info("interactions.verifyCurrentUrl(baseUrl) test");
        webInteractions.getUrl(baseUrl + "links");
        interactions.verifyCurrentUrl(baseUrl + "links");
        Assert.assertEquals("interactions.verifyCurrentUrl(baseUrl) test failed!", driver.getCurrentUrl(), baseUrl + "links");
        log.success("The interactions.verifyCurrentUrl(baseUrl) test pass!");
    }

    @Test
    public void verifyPageTitleTest() {
        log.info("webInteractions.verifyPageTitle(title) test");
        webInteractions.getUrl(baseUrl + "links");
        String title = "DEMOQA";
        webInteractions.verifyPageTitle(title);
        Assert.assertEquals("webInteractions.verifyPageTitle(title) test failed!", driver.getTitle(), title);
        log.success("The webInteractions.verifyPageTitle(title) test pass!");
    }

    @Test
    public void clickWithJSTest() {
        WebElement element = this.getAcquisition(Web).acquireElementFromPage("clickMeButton", "PageClass");
        WebElement dynamicClickMessage = this.getAcquisition(Web).acquireElementFromPage("dynamicClickMessage", "PageClass");

        log.info("webInteractions.clickWithJS(page.clickMeButton) test");
        webInteractions.getUrl(baseUrl + "buttons");
        webInteractions.clickWithJS(element);
        Assert.assertEquals("webInteractions.clickWithJS(page.clickMeButton) test failed!", "You have done a dynamic click", dynamicClickMessage.getText());
        log.success("The webInteractions.clickWithJS(page.clickMeButton) test pass!");
    }

    @Test
    public void scrollWithJSTest() {
        log.info("webInteractions.scrollWithJS(page.interactionsAccordionBar) test");
        webInteractions.getUrl(baseUrl + "elements");
        WebElement element = this.getAcquisition(Web).acquireElementFromPage("interactionsAccordionBar", "PageClass");

        webInteractions.scrollWithJS(element);
        Assert.assertTrue("webInteractions.scrollWithJS(page.interactionsAccordionBar) test failed!", element.isDisplayed());
        log.success("The webInteractions.scrollWithJS(page.interactionsAccordionBar) test pass!");
    }

    @Test
    public void acquireNamedElementAmongstTest() {
        log.info("acquire.acquireListedElementFromPage(elementName, listName, pageName) test");
        webInteractions.getUrl(baseUrl);
        WebElement element = this.getAcquisition(Web).acquireListedElementFromPage("Forms", "toolCards", "pageClass");
        interactions.clickInteraction(element);
        WebElement header = this.getAcquisition(Web).acquireElementFromPage("headerTitle", "PageClass");
        Assert.assertTrue("acquire.acquireListedElementFromPage(elementName, listName, pageName) test failed!", header.getText().contains("Forms"));
        log.success("The acquire.acquireListedElementFromPage(elementName, listName, pageName) test pass!");
    }

    @Test
    public void acquireNamedElementAmongstNegativeTest() {
        log.info("acquire.acquireListedElementFromPage(elementName, listName, pageName) negative test");
        webInteractions.getUrl(baseUrl);
        try {
            WebElement wrongElement = acquire.acquireListedElementFromPage("anythingElse", "toolCards", "pageClass");
            interactions.clickInteraction(wrongElement);
        } catch (NoSuchElementException ignored) {
            log.success("Element is expectedly not found!");
        } finally {
            WebElement element = acquire.acquireListedElementFromPage("Forms", "toolCards", "pageClass");
            Assert.assertTrue("acquire.acquireListedElementFromPage(elementName, listName, pageName) test failed!", element.getText().contains("Forms"));
            log.success("The acquire.acquireListedElementFromPage(elementName, listName, pageName) negative test pass!");
        }
    }

    @Test
    public void verifyElementAttributeContainsValueTest() {
        log.info("verifyElementAttributeContainsValue(element, elementName, pageName, attributeName, value) test");
        webInteractions.getUrl(baseUrl);
        WebElement element = acquire.acquireListedElementFromPage("Forms", "toolCards", "pageClass");
        interactions.verifyElementAttributeContainsValue(element, "card", "pageClass", "innerHTML", "Form");
        log.success("The verifyElementAttributeContainsValue(element, elementName, pageName, attributeName, value) test pass!");
    }

    @Test
    public void verifyElementAttributeContainsValueNegativeTest() {
        log.info("verifyElementAttributeContainsValue(element, elementName, pageName, attributeName, value) negative test");
        webInteractions.getUrl(baseUrl);
        try {
            WebElement element = acquire.acquireListedElementFromPage("Forms", "toolCards", "pageClass");
            interactions.verifyElementAttributeContainsValue(element, "card", "pageClass", "innerHTML", "Test");
        } catch (PickleibVerificationException ignored) {
            log.success("Attribute verification is (un)successful!");
            return;
        }
        throw new PickleibVerificationException("Attribute verification failed!");
    }

    @Test
    public void propertyReaderTest(){
        Assert.assertNull("prop4 is not read", PropertyUtility.getProperty("prop4"));
        Assert.assertNull("prop5 is not read", PropertyUtility.getProperty("prop5"));
        PropertyUtility.loadProperties("src/test/resources/test.properties");
        log.info("prop1 is read as: " + PropertyUtility.getProperty("prop1"));
        log.info("prop2 is read as: " + PropertyUtility.getProperty("prop2"));
        log.info("prop3 is read as: " + PropertyUtility.getProperty("prop3"));
        log.info("prop4 is read as: " + PropertyUtility.getProperty("prop4"));
        log.info("prop5 is read as: " + PropertyUtility.getProperty("prop5"));
        Assert.assertNotNull("prop1 is not read", PropertyUtility.getProperty("prop1"));
        Assert.assertNotNull("prop2 is not read", PropertyUtility.getProperty("prop2"));
        Assert.assertNotNull("prop3 is not read", PropertyUtility.getProperty("prop3"));
        Assert.assertNotNull("prop4 is not read", PropertyUtility.getProperty("prop4"));
        Assert.assertNotNull("prop5 is not read", PropertyUtility.getProperty("prop5"));
        log.success("Property test1 pass!");
    }

    @Test
    public void scrollOrSwipeInDirectionScrollTest() {
        log.info("webInteractions.scrollOrSwipeInDirection(direction) test");
        webInteractions.getUrl(baseUrl + "elements");
        webInteractions.scrollOrSwipeInDirection(Direction.down);
        WebElement element = acquire.acquireListedElementFromPage("Interactions", "toolElements", "pageClass");
        webInteractions.clickInteraction(element);
        log.success("The webInteractions.scrollOrSwipeInDirection(direction) test pass!");
    }

    @Test
    public void scrollOrSwipeInDirectionNegativeScrollTest() {
        log.info("webInteractions.scrollOrSwipeInDirection(direction) test");
        webInteractions.getUrl(baseUrl + "elements");
        try {
            webInteractions.scrollOrSwipeInDirection(Direction.right);
        } catch (NullPointerException ignored) {
        }
        log.success("The webInteractions.scrollOrSwipeInDirection(direction) negative test pass!");
    }
}