import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;
import pickleib.driver.DriverFactory;
import pickleib.enums.ElementState;
import pickleib.exceptions.PickleibException;
import pickleib.utilities.element.ElementInteractions;
import pickleib.web.driver.PickleibWebDriver;
import pickleib.web.driver.WebDriverFactory;
import pickleib.web.interactions.WebInteractions;

import java.time.Duration;

import static pickleib.enums.Navigation.backwards;
import static pickleib.web.driver.PickleibWebDriver.driver;
import static pickleib.web.driver.PickleibWebDriver.log;

public class AppTest {

    ElementInteractions interactions;
    WebInteractions webInteractions;
    PageClass page;

    @Before
    public void before(){
        WebDriverFactory.setHeadless(true);
        WebDriverFactory.setDriverTimeout(120);
        WebDriverFactory.setUseWDM(true);
        PickleibWebDriver.initialize();
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(120));
        interactions = new ElementInteractions(driver, DriverFactory.DriverType.Web);
        webInteractions = new WebInteractions();
        page = new PageClass();
    }

    @After
    public void after() {
        PickleibWebDriver.terminate();
    }

    @Test
    public void navigateTest() {
        log.info("webInteractions.navigate(page.trainingUrl) test");
        webInteractions.navigate(page.trainingUrl);
        Assert.assertEquals("\"webInteractions.navigate(page.trainingUrl) test failed!", driver.getCurrentUrl(),page.trainingUrl);
        log.success("webInteractions.navigate(page.trainingUrl) test pass!");
    }

    @Test
    public void navigateBrowserTest(){
        log.info("webInteractions.navigateBrowser(backwards) test");
        webInteractions.getUrl(page.baseUrl);
        webInteractions.toPage("buttons");
        webInteractions.navigateBrowser(backwards);
        Assert.assertEquals("webInteractions.navigateBrowser(backwards) test failed!", driver.getCurrentUrl(),page.baseUrl);
        log.success("webInteractions.navigateBrowser(backwards) test pass!");
    }

    @Test
    public void clickTest(){
        log.info("interactions.clickInteraction(page.clickMeButton) test");
        webInteractions.getUrl(page.baseUrl + "buttons");
        interactions.clickInteraction(page.clickMeButton);
        Assert.assertEquals("interactions.clickInteraction(page.clickMeButton) test failed!","You have done a dynamic click", page.dynamicClickMessage.getText());
        log.success("interactions.clickInteraction(page.clickMeButton) test pass!");
    }

    @Test
    public void negativeClickTest(){
        log.info("interactions.clickInteraction(page.clickMeButton) test");
        webInteractions.getUrl(page.baseUrl + "buttons");
        try {
            interactions.clickInteraction(page.unClickableButton);
        }
        catch (WebDriverException | PickleibException ignored) {}
        finally {
            Assert.assertTrue("Successful click message element is unexpectedly found!", interactions.elementStateIs(page.dynamicClickMessage, ElementState.absent));
            log.success("interactions.clickInteraction(page.clickMeButton) test pass!");
        }
    }

    @Test
    public void clickByTextTest(){
        log.info("interactions.clickByText(\"Click Me\") test");
        webInteractions.getUrl(page.baseUrl + "buttons");
        interactions.clickByText("Click Me");
        Assert.assertEquals("interactions.clickByText(\"Click Me\") test failed!","You have done a dynamic click", page.dynamicClickMessage.getText());
        log.success("interactions.clickByText(\"Click Me\") test pass!");
    }

    @Test
    public void scrollInContainerTest(){
        log.info("webInteractions.scrollInContainer(page.accordionLeftPanel,\"Interactions\") test");
        webInteractions.getUrl(page.baseUrl + "elements");
        webInteractions.scrollInContainer(page.accordionLeftPanel,"Interactions");
        Assert.assertTrue("webInteractions.scrollInContainer(page.accordionLeftPanel,\"Interactions\") test failed!", page.interactionsAccordionBar.isDisplayed());
        log.success("webInteractions.scrollInContainer(page.accordionLeftPanel,\"Interactions\") test pass!");
    }

    @Test
    public void switchWindowByHandleTest(){
        log.info("webInteractions.switchWindowByHandle(parentTab) test");
        webInteractions.getUrl(page.baseUrl + "links");
        driver.findElement(By.cssSelector("#simpleLink")).click();
        String parentTab = driver.getWindowHandles().toArray()[0].toString();
        webInteractions.switchWindowByHandle(parentTab);
        Assert.assertEquals("webInteractions.switchWindowByHandle(parentTab) test failed!", parentTab,driver.getWindowHandle());
        log.success("webInteractions.switchWindowByHandle(parentTab) test pass!");
    }

    @Test
    public void switchWindowByIndexTest(){
        log.info("webInteractions.switchWindowByIndex(0) test");
        webInteractions.getUrl(page.baseUrl + "links");
        driver.findElement(By.cssSelector("#simpleLink")).click();
        webInteractions.switchWindowByIndex(0);
        String parentTab = driver.getWindowHandles().toArray()[0].toString();
        Assert.assertEquals("webInteractions.switchWindowByIndex(0) test failed!", parentTab,driver.getWindowHandle());
        log.success("webInteractions.switchWindowByIndex(0) test pass!");
    }

    @Test
    public void verifyUrlContainsTest(){
        log.info("webInteractions.verifyUrlContains(text) test");
        String text = "qa";
        webInteractions.getUrl(page.baseUrl);
        webInteractions.verifyUrlContains(text);
        Assert.assertTrue("webInteractions.verifyUrlContains(text) test failed!", driver.getCurrentUrl().contains(text));
        log.success("webInteractions.verifyUrlContains(text) test pass!");
    }

    @Test
    public void verifyCurrentUrlTest(){
        log.info("interactions.verifyCurrentUrl(page.baseUrl) test");
        webInteractions.getUrl(page.baseUrl + "links");
        interactions.verifyCurrentUrl(page.baseUrl + "links");
        Assert.assertEquals("interactions.verifyCurrentUrl(page.baseUrl) test failed!", driver.getCurrentUrl(),page.baseUrl + "links");
        log.success("interactions.verifyCurrentUrl(page.baseUrl) test pass!");
    }

    @Test
    public void verifyPageTitleTest(){
        log.info("webInteractions.verifyPageTitle(title) test");
        webInteractions.getUrl(page.baseUrl + "links");
        String title = "DEMOQA";
        webInteractions.verifyPageTitle(title);
        Assert.assertEquals("webInteractions.verifyPageTitle(title) test failed!", driver.getTitle(),title);
        log.success("webInteractions.verifyPageTitle(title) test pass!");
    }

    @Test
    public void clickWithJSTest(){
        log.info("webInteractions.clickWithJS(page.clickMeButton) test");
        webInteractions.getUrl(page.baseUrl + "buttons");
        webInteractions.clickWithJS(page.clickMeButton);
        Assert.assertEquals("webInteractions.clickWithJS(page.clickMeButton) test failed!","You have done a dynamic click", page.dynamicClickMessage.getText());
        log.success("webInteractions.clickWithJS(page.clickMeButton) test pass!");
    }

    @Test
    public void scrollWithJSTest(){
        log.info("webInteractions.scrollWithJS(page.interactionsAccordionBar) test");
        webInteractions.getUrl(page.baseUrl + "elements");
        webInteractions.scrollWithJS(page.interactionsAccordionBar);
        Assert.assertTrue("webInteractions.scrollWithJS(page.interactionsAccordionBar) test failed!", page.interactionsAccordionBar.isDisplayed());
        log.success("webInteractions.scrollWithJS(page.interactionsAccordionBar) test pass!");
    }
}