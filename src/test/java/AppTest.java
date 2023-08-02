import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import pickleib.driver.DriverFactory;
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
        PickleibWebDriver.initialize();
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(120));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(120));
        interactions = new ElementInteractions(driver, DriverFactory.DriverType.Web);
        webInteractions = new WebInteractions();
        page = new PageClass();
        webInteractions.getUrl(page.baseUrl);
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
        webInteractions.toPage("buttons");
        webInteractions.navigateBrowser(backwards);
        Assert.assertEquals("webInteractions.navigateBrowser(backwards) test failed!", driver.getCurrentUrl(),page.baseUrl);
        log.success("webInteractions.navigateBrowser(backwards) test pass!");
    }

    @Test
    public void clickTest(){
        log.info("interactions.clickInteraction(page.clickMeButton) test");
        webInteractions.toPage("buttons");
        interactions.clickInteraction(page.clickMeButton);
        Assert.assertEquals("interactions.clickInteraction(page.clickMeButton) test failed!","You have done a dynamic click", page.dynamicClickMessage.getText());
        log.success("interactions.clickInteraction(page.clickMeButton) test pass!");
    }

    @Test
    public void clickByTextTest(){
        log.info("interactions.clickByText(\"Click Me\") test");
        webInteractions.toPage("buttons");
        interactions.clickByText("Click Me");
        Assert.assertEquals("interactions.clickByText(\"Click Me\") test failed!","You have done a dynamic click", page.dynamicClickMessage.getText());
        log.success("interactions.clickByText(\"Click Me\") test pass!");
    }

    @Test
    public void scrollInContainerTest(){
        log.info("webInteractions.scrollInContainer(page.accordionLeftPanel,\"Interactions\") test");
        webInteractions.toPage("elements");
        webInteractions.scrollInContainer(page.accordionLeftPanel,"Interactions");
        Assert.assertTrue("webInteractions.scrollInContainer(page.accordionLeftPanel,\"Interactions\") test failed!", page.interactionsAccordionBar.isDisplayed());
        log.success("webInteractions.scrollInContainer(page.accordionLeftPanel,\"Interactions\") test pass!");
    }

    @Test
    public void switchWindowByHandleTest(){
        log.info("webInteractions.switchWindowByHandle(parentTab) test");
        webInteractions.toPage("links");
        driver.findElement(By.cssSelector("#simpleLink")).click();
        String parentTab = driver.getWindowHandles().toArray()[0].toString();
        webInteractions.switchWindowByHandle(parentTab);
        Assert.assertEquals("webInteractions.switchWindowByHandle(parentTab) test failed!", parentTab,driver.getWindowHandle());
        log.success("webInteractions.switchWindowByHandle(parentTab) test pass!");
    }

    @Test
    public void switchWindowByIndexTest(){
        log.info("webInteractions.switchWindowByIndex(0) test");
        webInteractions.toPage("links");
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
        webInteractions.verifyUrlContains(text);
        Assert.assertTrue("webInteractions.verifyUrlContains(text) test failed!", driver.getCurrentUrl().contains(text));
        log.success("webInteractions.verifyUrlContains(text) test pass!");
    }

    @Test
    public void verifyCurrentUrlTest(){
        log.info("interactions.verifyCurrentUrl(page.baseUrl) test");
        interactions.verifyCurrentUrl(page.baseUrl);
        Assert.assertEquals("interactions.verifyCurrentUrl(page.baseUrl) test failed!", driver.getCurrentUrl(),page.baseUrl);
        log.success("interactions.verifyCurrentUrl(page.baseUrl) test pass!");
    }

    @Test
    public void verifyPageTitleTest(){
        log.info("webInteractions.verifyPageTitle(title) test");
        String title = "DEMOQA";
        webInteractions.verifyPageTitle(title);
        Assert.assertEquals("webInteractions.verifyPageTitle(title) test failed!", driver.getTitle(),title);
        log.success("webInteractions.verifyPageTitle(title) test pass!");
    }

    @Test
    public void clickWithJSTest(){
        log.info("webInteractions.clickWithJS(page.clickMeButton) test");
        webInteractions.toPage("buttons");
        webInteractions.clickWithJS(page.clickMeButton);
        Assert.assertEquals("webInteractions.clickWithJS(page.clickMeButton) test failed!","You have done a dynamic click", page.dynamicClickMessage.getText());
        log.success("webInteractions.clickWithJS(page.clickMeButton) test pass!");
    }

    @Test
    public void scrollWithJSTest(){
        log.info("webInteractions.scrollWithJS(page.interactionsAccordionBar) test");
        webInteractions.toPage("elements");
        webInteractions.scrollWithJS(page.interactionsAccordionBar);
        Assert.assertTrue("webInteractions.scrollWithJS(page.interactionsAccordionBar) test failed!", page.interactionsAccordionBar.isDisplayed());
        log.success("webInteractions.scrollWithJS(page.interactionsAccordionBar) test pass!");
    }
}