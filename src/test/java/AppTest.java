import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import pickleib.utilities.element.ElementAcquisition;
import pickleib.web.driver.PickleibWebDriver;
import pickleib.web.interactions.WebInteractions;

import static pickleib.web.driver.PickleibWebDriver.driver;

public class AppTest {

    ElementAcquisition.PageObjectModel acquisition;
    WebInteractions interactions;
    PageClass page;

    @Before
    public void before(){
        PickleibWebDriver.initialize();
        acquisition = new ElementAcquisition.PageObjectModel(PickleibWebDriver.driver);
        interactions = new WebInteractions();
        page = new PageClass();
        interactions.getUrl(page.baseUrl);
    }
    @After
    public void after() {
        PickleibWebDriver.terminate();
    }
    @Test
    public void navigateTest() {
        interactions.navigate(page.trainingUrl);
        Assert.assertEquals(driver.getCurrentUrl(),page.trainingUrl);
    }
    @Test
    public void setWindowSizeTest(){
        interactions.setWindowSize(1280,720);
        //Assert.....
    }
    @Test
    public void getElementTest() {
        WebElement element = acquisition.acquireListedElementFromPage("Widgets","toolCards","PageClass", new ObjectRepository());
        interactions.clickInteraction(element);
    }
    @Test
    public void clickTest(){
        interactions.toPage("buttons");
        interactions.clickInteraction(page.clickMeButton);
        Assert.assertEquals("You have done a dynamic click",page.dynamicClickMessage.getText());
    }
    @Test
    public void clickByTextTest(){
        interactions.toPage("buttons");
        interactions.clickByText("Click Me");
        Assert.assertEquals("You have done a dynamic click",page.dynamicClickMessage.getText());
    }
    @Test
    public void scrollInContainerTest(){
        interactions.toPage("elements");
        interactions.scrollInContainer(page.accordionLeftPanel,"Interactions");
        Assert.assertTrue(page.interactionsAccordionBar.isDisplayed());
    }
    @Test
    public void switchWindowByHandleTest(){
        interactions.toPage("links");
        driver.findElement(By.cssSelector("#simpleLink")).click();
        String parentTab = driver.getWindowHandles().toArray()[0].toString();
        interactions.switchWindowByHandle(parentTab);
        Assert.assertEquals(parentTab,driver.getWindowHandle());
    }
    @Test
    public void switchWindowByIndexTest(){
        interactions.toPage("links");
        driver.findElement(By.cssSelector("#simpleLink")).click();
        interactions.switchWindowByIndex(0);
        String parentTab = driver.getWindowHandles().toArray()[0].toString();
        Assert.assertEquals(parentTab,driver.getWindowHandle());
    }
    @Test
    public void verifyUrlContainsTest(){
        String text = "qa";
        interactions.verifyUrlContains(text);
        Assert.assertTrue(driver.getCurrentUrl().contains(text));
    }
    @Test
    public void verifyCurrentUrlTest(){
        interactions.verifyCurrentUrl(page.baseUrl);
        Assert.assertEquals(driver.getCurrentUrl(),page.baseUrl);
    }
    @Test
    public void verifyPageTitleTest(){
        String title = "DEMOQA";
        interactions.verifyPageTitle(title);
        Assert.assertEquals(driver.getTitle(),title);
    }
    @Test
    public void clickWithJSTest(){
        interactions.toPage("buttons");
        interactions.clickWithJS(page.clickMeButton);
        Assert.assertEquals("You have done a dynamic click",page.dynamicClickMessage.getText());
    }
    @Test
    public void scrollWithJSTest(){
        interactions.toPage("elements");
        interactions.scrollWithJS(page.interactionsAccordionBar);
        Assert.assertTrue(page.interactionsAccordionBar.isDisplayed());
    }
}