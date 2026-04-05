package pickleib.platform;

import context.ContextStore;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.DefaultFieldDecorator;
import pickleib.platform.driver.PickleibAppiumDriver;
import pickleib.platform.utilities.PlatformUtilities;
import pickleib.web.driver.PickleibWebDriver;

import java.time.Duration;

/** Base class for mobile/desktop screen page objects with automatic Appium field decoration. */
public abstract class PickleibScreenObject extends PlatformUtilities {
    /**
     * PickleibScreenObject for frameworks that use the Pickleib driver
     */
    protected PickleibScreenObject(){
        super(PickleibAppiumDriver.get());
        PageFactory.initElements(
                new AppiumFieldDecorator(
                        PickleibAppiumDriver.get(),
                        Duration.ofSeconds(
                                Long.parseLong(ContextStore.get("element-timeout", "15000"))/1000
                        )
                ),
                this
        );
    }

    /**
     * PickleibScreenObject for frameworks that do not use the Pickleib driver
     *
     * @param driver the RemoteWebDriver instance
     */
    protected PickleibScreenObject(RemoteWebDriver driver){
        super(driver);
        PageFactory.initElements(
                new AppiumFieldDecorator(
                        driver,
                        Duration.ofSeconds(
                                Long.parseLong(ContextStore.get("element-timeout", "15000"))/1000
                        )
                ),
                this
        );
    }

    /**
     * PickleibScreenObject for frameworks with custom field decorator that use the Pickleib driver
     *
     * @param <CustomFieldDecorator> the field decorator type
     * @param fieldDecorator the custom field decorator
     */
    protected <CustomFieldDecorator extends DefaultFieldDecorator> PickleibScreenObject(CustomFieldDecorator fieldDecorator){
        super(PickleibWebDriver.get());
        PageFactory.initElements(fieldDecorator, this);
    }

    /**
     * PickleibScreenObject for frameworks with custom field decorator that do not use the Pickleib driver
     *
     * @param <CustomFieldDecorator> the field decorator type
     * @param fieldDecorator the custom field decorator
     * @param driver the RemoteWebDriver instance
     */
    protected <CustomFieldDecorator extends DefaultFieldDecorator> PickleibScreenObject(
            CustomFieldDecorator fieldDecorator,
            RemoteWebDriver driver
    ){
        super(driver);
        PageFactory.initElements(fieldDecorator, this);
    }
}
