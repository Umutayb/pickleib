package pickleib.web;

import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.DefaultFieldDecorator;
import pickleib.web.driver.PickleibWebDriver;
import pickleib.web.utilities.WebUtilities;

public abstract class PickleibPageObject extends WebUtilities {
    /**
     * WebUtilities for frameworks that use the Pickleib driver
     *
     */
    protected PickleibPageObject(){
        super(PickleibWebDriver.get(), scroller);
        PageFactory.initElements(driver, this);
    }

    /**
     * WebUtilities for frameworks that do not use the Pickleib driver
     *
     */
    protected PickleibPageObject(RemoteWebDriver driver){
        super(driver, scroller);
        PageFactory.initElements(driver, this);
    }

    /**
     * WebUtilities for frameworks with custom field decorator that use the Pickleib driver
     *
     */
    protected <CustomFieldDecorator extends DefaultFieldDecorator> PickleibPageObject(CustomFieldDecorator fieldDecorator){
        super(PickleibWebDriver.get(), scroller);
        PageFactory.initElements(fieldDecorator, this);
    }

    /**
     * WebUtilities for frameworks with custom field decorator that do not use the Pickleib driver
     *
     */
    protected <CustomFieldDecorator extends DefaultFieldDecorator> PickleibPageObject(
            CustomFieldDecorator fieldDecorator,
            RemoteWebDriver driver
    ){
        super(driver, scroller);
        PageFactory.initElements(fieldDecorator, this);
    }
}
