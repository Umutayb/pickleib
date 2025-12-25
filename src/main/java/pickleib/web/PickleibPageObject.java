package pickleib.web;

import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.pagefactory.DefaultFieldDecorator;
import pickleib.web.driver.PickleibWebDriver;
import pickleib.web.utilities.WebUtilities;

/**
 * The abstract base class for all Web Page Objects in the Pickleib framework.
 *
 * <p>
 * This class serves two main purposes:
 * </p>
 * <ol>
 *   <li>
 *     It inherits all the powerful interaction methods from {@link WebUtilities}
 *     (e.g., click, fill, scroll).
 *   </li>
 *   <li>
 *     It automatically initializes {@link org.openqa.selenium.WebElement} fields
 *     annotated with {@link FindBy} using Selenium's {@link PageFactory}.
 *   </li>
 * </ol>
 *
 * <p>
 * <b>Usage:</b><br>
 * Extend this class when creating a new Page Object representing a web page.
 * </p>
 *
 * <pre>
 * public class LoginPage extends PickleibPageObject {
 *
 *     &#64;FindBy(id = "username")
 *     public WebElement usernameInput;
 * }
 * </pre>
 *
 * @author Umut Ay Bora
 */
public abstract class PickleibPageObject extends WebUtilities {

    /**
     * Default constructor for frameworks using the Singleton {@link PickleibWebDriver}.
     * <p>
     * Automatically fetches the active driver from {@code PickleibWebDriver.get()} and initializes elements.
     * </p>
     */
    protected PickleibPageObject(){
        super(PickleibWebDriver.get());
        PageFactory.initElements(driver, this);
    }

    /**
     * Constructor for frameworks passing a specific {@link RemoteWebDriver} instance.
     * <p>
     * Useful for parallel execution or when managing drivers manually.
     * </p>
     *
     * @param driver The active driver instance.
     */
    protected PickleibPageObject(RemoteWebDriver driver){
        super(driver);
        PageFactory.initElements(driver, this);
    }

    /**
     * Constructor allowing a custom {@link DefaultFieldDecorator} with the Singleton driver.
     * <p>
     * Use this if you need custom element location strategies (e.g., for Appium or specialized decorators).
     * </p>
     *
     * @param fieldDecorator The custom field decorator.
     * @param <CustomFieldDecorator> Type extending DefaultFieldDecorator.
     */
    protected <CustomFieldDecorator extends DefaultFieldDecorator> PickleibPageObject(CustomFieldDecorator fieldDecorator){
        super(PickleibWebDriver.get());
        PageFactory.initElements(fieldDecorator, this);
    }

    /**
     * Constructor allowing both a custom {@link DefaultFieldDecorator} and a specific driver.
     *
     * @param fieldDecorator The custom field decorator.
     * @param driver         The active driver instance.
     * @param <CustomFieldDecorator> Type extending DefaultFieldDecorator.
     */
    protected <CustomFieldDecorator extends DefaultFieldDecorator> PickleibPageObject(
            CustomFieldDecorator fieldDecorator,
            RemoteWebDriver driver
    ){
        super(driver);
        PageFactory.initElements(fieldDecorator, this);
    }
}
