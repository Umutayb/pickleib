import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import pickleib.utilities.element.ElementAcquisition;
import pickleib.web.driver.PickleibWebDriver;
import pickleib.web.interactions.WebInteractions;

import static pickleib.enums.Navigation.backwards;
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
        Assert.assertEquals("navigateTest FAIL",driver.getCurrentUrl(),page.trainingUrl);
    }
    @Test
    public void setWindowSizeTest(){
        int width = 802;
        int height = 502;
        interactions.setWindowSize(width-2,height-2);
        String size = "("+ width +", "+ height +")";
        Assert.assertEquals("setWindowSizeTest FAIL",driver.manage().window().getSize().toString(),size);
    }
    @Test
    public void navigateBrowserTest(){
        interactions.toPage("buttons");
        interactions.navigateBrowser(backwards);
        Assert.assertEquals("navigateBrowserTest FAIL",driver.getCurrentUrl(),page.baseUrl);
    }@Test
    public void clickTest(){
        interactions.toPage("buttons");
        interactions.clickInteraction(page.clickMeButton);
        Assert.assertEquals("clickTest FAIL","You have done a dynamic click",page.dynamicClickMessage.getText());
    }
    @Test
    public void clickByTextTest(){
        interactions.toPage("buttons");
        interactions.clickByText("Click Me");
        Assert.assertEquals("clickByTextTest FAIL","You have done a dynamic click",page.dynamicClickMessage.getText());
    }
    @Test
    public void scrollInContainerTest(){
        interactions.toPage("elements");
        interactions.scrollInContainer(page.accordionLeftPanel,"Interactions");
        Assert.assertTrue("scrollInContainerTest FAIL",page.interactionsAccordionBar.isDisplayed());
    }
    @Test
    public void switchWindowByHandleTest(){
        interactions.toPage("links");
        driver.findElement(By.cssSelector("#simpleLink")).click();
        String parentTab = driver.getWindowHandles().toArray()[0].toString();
        interactions.switchWindowByHandle(parentTab);
        Assert.assertEquals("switchWindowByHandleTest FAIL",parentTab,driver.getWindowHandle());
    }
    @Test
    public void switchWindowByIndexTest(){
        interactions.toPage("links");
        driver.findElement(By.cssSelector("#simpleLink")).click();
        interactions.switchWindowByIndex(0);
        String parentTab = driver.getWindowHandles().toArray()[0].toString();
        Assert.assertEquals("switchWindowByIndexTest FAIL",parentTab,driver.getWindowHandle());
    }
    @Test
    public void verifyUrlContainsTest(){
        String text = "qa";
        interactions.verifyUrlContains(text);
        Assert.assertTrue("verifyUrlContainsTest FAIL",driver.getCurrentUrl().contains(text));
    }
    @Test
    public void verifyCurrentUrlTest(){
        interactions.verifyCurrentUrl(page.baseUrl);
        Assert.assertEquals("verifyCurrentUrlTest FAIL",driver.getCurrentUrl(),page.baseUrl);
    }
    @Test
    public void verifyPageTitleTest(){
        String title = "DEMOQA";
        interactions.verifyPageTitle(title);
        Assert.assertEquals("verifyPageTitleTest FAIL",driver.getTitle(),title);
    }
    @Test
    public void clickWithJSTest(){
        interactions.toPage("buttons");
        interactions.clickWithJS(page.clickMeButton);
        Assert.assertEquals("clickWithJSTest FAIL","You have done a dynamic click",page.dynamicClickMessage.getText());
    }
    @Test
    public void scrollWithJSTest(){
        interactions.toPage("elements");
        interactions.scrollWithJS(page.interactionsAccordionBar);
        Assert.assertTrue("scrollWithJSTest FAIL",page.interactionsAccordionBar.isDisplayed());
    }
}