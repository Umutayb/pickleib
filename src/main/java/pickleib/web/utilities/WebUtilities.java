package pickleib.web.utilities;

import org.jetbrains.annotations.Nullable;
import org.junit.Assert;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.DefaultFieldDecorator;
import pickleib.enums.Navigation;
import pickleib.utilities.Utilities;
import pickleib.web.driver.PickleibWebDriver;
import java.util.ArrayList;
import java.util.List;

import static utils.StringUtilities.Color.*;

public abstract class WebUtilities extends Utilities {

    /**
     * WebUtilities for frameworks that use the Pickleib driver
     *
     */
    protected WebUtilities(){
        super(PickleibWebDriver.driver);
        PageFactory.initElements(driver, this);
    }

    /**
     * WebUtilities for frameworks that do not use the Pickleib driver
     *
     */
    protected WebUtilities(RemoteWebDriver driver){
        super(driver);
        PageFactory.initElements(driver, this);
    }


    /**
     * WebUtilities for frameworks with custom field decorator that use the Pickleib driver
     *
     */
    protected <T extends DefaultFieldDecorator> WebUtilities(T fieldDecorator){
        super(PickleibWebDriver.driver);
        PageFactory.initElements(fieldDecorator, this);
    }

    /**
     * WebUtilities for frameworks with custom field decorator that do not use the Pickleib driver
     *
     */
    protected <T extends DefaultFieldDecorator> WebUtilities(T fieldDecorator, RemoteWebDriver driver){
        super(driver);
        PageFactory.initElements(fieldDecorator, this);
    }

    /**
     * Navigates to a given url
     *
     * @param url target url
     */
    @SuppressWarnings("UnusedReturnValue")
    public String navigate(String url){
        try {
            log.info("Navigating to " + highlighted(BLUE, url));

            if (!url.contains("http")) url = "https://"+url;

            driver.get(url);
        }
        catch (Exception gamma){
            Assert.fail("Unable to navigate to the \""+strUtils.highlighted(YELLOW, url)+"\"");
            driver.quit();
        }
        return url;
    }

    /**
     * Sets the window size
     *
     * @param width windows width
     * @param height windows height
     */
    public void setWindowSize(Integer width, Integer height) {
        driver.manage().window().setSize(new Dimension(width,height));
    }

    /**
     * Navigates browsers in a given direction
     *
     * @param direction backwards or forwards
     */
    public void navigateBrowser(Navigation direction){
        try {
            log.info("Navigating " + strUtils.highlighted(BLUE, direction.name()));

            switch (direction) {
                case forwards -> driver.navigate().forward();
                case backwards -> driver.navigate().back();
                default -> throw new EnumConstantNotPresentException(Navigation.class, direction.name());
            }
        }
        catch (Exception e){
            Assert.fail("Unable to navigate browser \"" + strUtils.highlighted(YELLOW, direction.name())+"\" due to: " + e);
        }
    }

    /**
     * Scrolls through a list of elements until an element containing a given text is found
     *
     * @param list target element list
     * @param elementText target element text
     */
    public void scrollInContainer(List<WebElement> list, String elementText){
        for (WebElement element : list) {
            scrollWithJS(element);
            if (element.getText().contains(elementText)) {
                break;
            }
        }
    }

    /**
     * Switches driver focus by using a tab handle
     *
     * @param handle target tab/window
     */
    public String switchWindowByHandle(@Nullable String handle){
        log.info("Switching to the next tab");
        String parentWindowHandle = driver.getWindowHandle();
        if (handle == null)
            for (String windowHandle: driver.getWindowHandles()) {
                if (!windowHandle.equalsIgnoreCase(parentWindowHandle))
                    driver = (RemoteWebDriver) driver.switchTo().window((windowHandle));
            }
        else driver = (RemoteWebDriver) driver.switchTo().window(handle);
        return parentWindowHandle;
    }

    /**
     * Switches driver focus by using tab index
     *
     * @param tabIndex target tab/window
     */
    public String switchWindowByIndex(Integer tabIndex){
        log.info("Switching the tab with the window index: " + tabIndex);
        String parentWindowHandle = driver.getWindowHandle();
        List<String> handles = new ArrayList<>(driver.getWindowHandles());
        String handle = handles.get(tabIndex);
        driver = (RemoteWebDriver) driver.switchTo().window(handle);
        return parentWindowHandle;
    }


    /**
     * Verifies the current url contains the given url
     *
     * @param url target url
     */
    public void verifyUrlContains(String url){
        Assert.assertTrue(driver.getCurrentUrl().contains(url));
    }

    /**
     * Verifies the current url equals to given url
     *
     * @param url target url
     */
    public void verifyCurrentUrl(String url){
        Assert.assertTrue(driver.getCurrentUrl().equalsIgnoreCase(url));
    }

    /**
     * Verifies the given page title
     *
     * @param pageTitle target page
     */
    //This method verifies the page title
    public void verifyPageTitle(String pageTitle){
        Assert.assertTrue(driver.getTitle().contains(pageTitle));
    }


    /**
     * Click an element into view by using javascript
     *
     * @param webElement element that gets clicked
     */
    public void clickWithJS(WebElement webElement) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", webElement);
    }

    /**
     * Scrolls element into view by using javascript
     *
     * @param webElement element that gets scrolled into the view
     */
    public void scrollWithJS(WebElement webElement) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView();", webElement);
    }
}
