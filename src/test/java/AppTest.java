import org.junit.*;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import pickleib.enums.ElementState;
import pickleib.exceptions.PickleibException;
import pickleib.utilities.CommonStepUtilities;
import pickleib.utilities.element.ElementAcquisition;
import pickleib.utilities.element.ElementInteractions;
import pickleib.web.driver.PickleibWebDriver;
import pickleib.web.driver.WebDriverFactory;
import pickleib.web.interactions.WebInteractions;
import java.time.Duration;

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
     *
     */
    public AppTest() {
        super(ObjectRepository.class);
    }

    @Before
    public void before(){
        WebDriverFactory.setHeadless(true);
        WebDriverFactory.setDriverTimeout(120);
        WebDriverFactory.setUseWDM(true);
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
        Assert.assertEquals("\"webInteractions.navigate(page.trainingUrl) test failed!", driver.getCurrentUrl(),trainingUrl);
        log.success("The webInteractions.navigate(page.trainingUrl) test pass!");
    }

    @Test
    public void navigateBrowserTest(){
        log.info("webInteractions.navigateBrowser(backwards) test");
        webInteractions.getUrl(baseUrl);
        webInteractions.toPage("buttons");
        webInteractions.navigateBrowser(backwards);
        Assert.assertEquals("webInteractions.navigateBrowser(backwards) test failed!", driver.getCurrentUrl(),baseUrl);
        log.success("The webInteractions.navigateBrowser(backwards) test pass!");
    }

    @Test
    public void clickTest(){
        WebElement clickMeButton = this.getAcquisition(Web).acquireElementFromPage("clickMeButton", "PageClass");
        WebElement dynamicClickMessage = this.getAcquisition(Web).acquireElementFromPage("dynamicClickMessage", "PageClass");

        log.info("interactions.clickInteraction(page.clickMeButton) test");
        webInteractions.getUrl(baseUrl + "buttons");
        interactions.clickInteraction(clickMeButton);
        Assert.assertEquals("interactions.clickInteraction(page.clickMeButton) test failed!","You have done a dynamic click", dynamicClickMessage.getText());
        log.success("The interactions.clickInteraction(page.clickMeButton) test pass!");
    }

    @Test
    public void negativeClickTest(){
        WebElement unClickableButton = this.getAcquisition(Web).acquireElementFromPage("unClickableButton", "PageClass");
        WebElement dynamicClickMessage = this.getAcquisition(Web).acquireElementFromPage("dynamicClickMessage", "PageClass");

        log.info("interactions.clickInteraction(page.clickMeButton) test");
        webInteractions.getUrl(baseUrl + "buttons");
        try {
            interactions.clickInteraction(unClickableButton);
        }
        catch (WebDriverException | PickleibException ignored) {}
        finally {
            Assert.assertTrue("Successful click message element is unexpectedly found!", interactions.elementStateIs(dynamicClickMessage, ElementState.absent));
            log.success("The interactions.clickInteraction(page.clickMeButton) test pass!");
        }
    }

    @Test
    public void clickByTextTest(){
        WebElement dynamicClickMessage = this.getAcquisition(Web).acquireElementFromPage("dynamicClickMessage", "PageClass");

        log.info("interactions.clickByText(\"Click Me\") test");
        webInteractions.getUrl(baseUrl + "buttons");
        interactions.clickByText("Click Me");
        Assert.assertEquals("interactions.clickByText(\"Click Me\") test failed!","You have done a dynamic click", dynamicClickMessage.getText());
        log.success("The interactions.clickByText(\"Click Me\") test pass!");
    }

    @Test
    public void scrollInContainerTest(){
        PageClass page = new PageClass();
        log.info("webInteractions.scrollInContainer(page.accordionLeftPanel,\"Interactions\") test");
        webInteractions.getUrl(baseUrl + "elements");
        webInteractions.scrollInContainer(page.accordionLeftPanel,"Interactions");
        Assert.assertTrue("webInteractions.scrollInContainer(page.accordionLeftPanel,\"Interactions\") test failed!", page.interactionsAccordionBar.isDisplayed());
        log.success("The webInteractions.scrollInContainer(page.accordionLeftPanel,\"Interactions\") test pass!");
    }

    @Test
    public void switchWindowByHandleTest(){
        log.info("webInteractions.switchWindowByHandle(parentTab) test");
        webInteractions.getUrl(baseUrl + "links");
        driver.findElement(By.cssSelector("#simpleLink")).click();
        String parentTab = driver.getWindowHandles().toArray()[0].toString();
        webInteractions.switchWindowByHandle(parentTab);
        Assert.assertEquals("webInteractions.switchWindowByHandle(parentTab) test failed!", parentTab,driver.getWindowHandle());
        log.success("The webInteractions.switchWindowByHandle(parentTab) test pass!");
    }

    @Test
    public void switchWindowByIndexTest(){
        log.info("webInteractions.switchWindowByIndex(0) test");
        webInteractions.getUrl(baseUrl + "links");
        driver.findElement(By.cssSelector("#simpleLink")).click();
        webInteractions.switchWindowByIndex(0);
        String parentTab = driver.getWindowHandles().toArray()[0].toString();
        Assert.assertEquals("webInteractions.switchWindowByIndex(0) test failed!", parentTab,driver.getWindowHandle());
        log.success("The webInteractions.switchWindowByIndex(0) test pass!");
    }

    @Test
    public void verifyUrlContainsTest(){
        log.info("webInteractions.verifyUrlContains(text) test");
        String text = "qa";
        webInteractions.getUrl(baseUrl);
        webInteractions.verifyUrlContains(text);
        Assert.assertTrue("webInteractions.verifyUrlContains(text) test failed!", driver.getCurrentUrl().contains(text));
        log.success("The webInteractions.verifyUrlContains(text) test pass!");
    }

    @Test
    public void verifyCurrentUrlTest(){
        log.info("interactions.verifyCurrentUrl(baseUrl) test");
        webInteractions.getUrl(baseUrl + "links");
        interactions.verifyCurrentUrl(baseUrl + "links");
        Assert.assertEquals("interactions.verifyCurrentUrl(baseUrl) test failed!", driver.getCurrentUrl(),baseUrl + "links");
        log.success("The interactions.verifyCurrentUrl(baseUrl) test pass!");
    }

    @Test
    public void verifyPageTitleTest(){
        log.info("webInteractions.verifyPageTitle(title) test");
        webInteractions.getUrl(baseUrl + "links");
        String title = "DEMOQA";
        webInteractions.verifyPageTitle(title);
        Assert.assertEquals("webInteractions.verifyPageTitle(title) test failed!", driver.getTitle(),title);
        log.success("The webInteractions.verifyPageTitle(title) test pass!");
    }

    @Test
    public void clickWithJSTest(){
        WebElement element = this.getAcquisition(Web).acquireElementFromPage("clickMeButton", "PageClass");
        WebElement dynamicClickMessage = this.getAcquisition(Web).acquireElementFromPage("dynamicClickMessage", "PageClass");

        log.info("webInteractions.clickWithJS(page.clickMeButton) test");
        webInteractions.getUrl(baseUrl + "buttons");
        webInteractions.clickWithJS(element);
        Assert.assertEquals("webInteractions.clickWithJS(page.clickMeButton) test failed!","You have done a dynamic click", dynamicClickMessage.getText());
        log.success("The webInteractions.clickWithJS(page.clickMeButton) test pass!");
    }

    @Test
    public void scrollWithJSTest(){
        log.info("webInteractions.scrollWithJS(page.interactionsAccordionBar) test");
        webInteractions.getUrl(baseUrl + "elements");
        WebElement element = this.getAcquisition(Web).acquireElementFromPage("interactionsAccordionBar", "PageClass");

        webInteractions.scrollWithJS(element);
        Assert.assertTrue("webInteractions.scrollWithJS(page.interactionsAccordionBar) test failed!", element.isDisplayed());
        log.success("The webInteractions.scrollWithJS(page.interactionsAccordionBar) test pass!");
    }

    @Test
    public void acquireNamedElementAmongstTest(){
        log.info("acquire.acquireListedElementFromPage(elementName, listName, pageName) test");
        webInteractions.getUrl(baseUrl);
        WebElement element = this.getAcquisition(Web).acquireListedElementFromPage("Forms", "toolCards", "pageClass");
        interactions.clickInteraction(element);
        WebElement header = this.getAcquisition(Web).acquireElementFromPage("headerTitle", "PageClass");
        Assert.assertTrue("acquire.acquireListedElementFromPage(elementName, listName, pageName) test failed!", header.getText().contains("Forms"));
        log.success("The acquire.acquireListedElementFromPage(elementName, listName, pageName) test pass!");
    }

    @Test
    public void acquireNamedElementAmongstNegativeTest(){
        log.info("acquire.acquireListedElementFromPage(elementName, listName, pageName) negative test");
        webInteractions.getUrl(baseUrl);
        try {
            WebElement wrongElement = acquire.acquireListedElementFromPage("anythingElse", "toolCards", "pageClass");
            interactions.clickInteraction(wrongElement);

        }
        catch (NoSuchElementException ignored) {
            log.success("Element is expectedly not found!");
        }
        finally {
            WebElement element = acquire.acquireListedElementFromPage("Forms", "toolCards", "pageClass");
            Assert.assertTrue("acquire.acquireListedElementFromPage(elementName, listName, pageName) test failed!", element.getText().contains("Forms"));
            log.success("The acquire.acquireListedElementFromPage(elementName, listName, pageName) negative test pass!");
        }
    }
}