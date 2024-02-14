package pickleib.mobile;

import context.ContextStore;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.DefaultFieldDecorator;
import pickleib.mobile.driver.PickleibAppiumDriver;
import pickleib.mobile.utilities.MobileUtilities;
import pickleib.web.driver.PickleibWebDriver;

import java.time.Duration;

public abstract class PickleibScreenObject extends MobileUtilities {
    /**
     * PickleibScreenObject for frameworks that use the Pickleib driver
     *
     */
    protected PickleibScreenObject(){
        super(PickleibAppiumDriver.get(), scroller);
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
     */
    protected PickleibScreenObject(RemoteWebDriver driver){
        super(driver, scroller);
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
     */
    protected <CustomFieldDecorator extends DefaultFieldDecorator> PickleibScreenObject(CustomFieldDecorator fieldDecorator){
        super(PickleibWebDriver.get(), scroller);
        PageFactory.initElements(fieldDecorator, this);
    }

    /**
     * PickleibScreenObject for frameworks with custom field decorator that do not use the Pickleib driver
     *
     */
    protected <CustomFieldDecorator extends DefaultFieldDecorator> PickleibScreenObject(
            CustomFieldDecorator fieldDecorator,
            RemoteWebDriver driver
    ){
        super(driver, scroller);
        PageFactory.initElements(fieldDecorator, this);
    }
}
