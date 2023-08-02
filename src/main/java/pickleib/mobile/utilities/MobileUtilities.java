package pickleib.mobile.utilities;

import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.PageFactory;
import pickleib.enums.SwipeDirection;
import pickleib.mobile.driver.PickleibAppiumDriver;
import pickleib.utilities.Utilities;
import utils.PropertyUtility;
import java.time.Duration;
import static java.time.Duration.ofMillis;
import static java.util.Collections.singletonList;

public abstract class MobileUtilities extends Utilities {

    /**
     * MobileUtilities for frameworks that use the Pickleib drivers
     *
     */
    protected MobileUtilities(){
        super(PickleibAppiumDriver.driver);
        PageFactory.initElements(
                new AppiumFieldDecorator(
                        driver,
                        Duration.ofSeconds(Long.parseLong(PropertyUtility.getProperty(
                                "element-timeout",
                                "15000"
                        )))
                ),
                this
        );
    }

    /**
     * MobileUtilities for frameworks that do not use the Pickleib drivers
     *
     */
    protected MobileUtilities(RemoteWebDriver driver){
        super(driver);
        PageFactory.initElements(
                new AppiumFieldDecorator(
                        driver,
                        Duration.ofSeconds(Long.parseLong(PropertyUtility.getProperty(
                                "element-timeout",
                                "15000"
                        )))
                ),
                this
        );
    }

    @Override
    public WebElement centerElement(WebElement element){

        Point center = new Point(
                driver.manage().window().getSize().getWidth()/2,
                driver.manage().window().getSize().getHeight()/2
        );

        int verticalScrollDist = element.getLocation().getY() - driver.manage().window().getSize().getHeight()/2;
        int verticalScrollStep = driver.manage().window().getSize().getHeight()/3;

        int horizontalScrollDist = element.getLocation().getX() - driver.manage().window().getSize().getWidth()/2;
        int horizontalScrollStep = driver.manage().window().getSize().getWidth()/3;

        for (int i = 0; i <= verticalScrollDist / verticalScrollStep; i++) {
            if (i == verticalScrollDist / verticalScrollStep){
                swipeFromCenter(
                        new Point(
                                center.getX() + horizontalScrollDist % horizontalScrollStep,
                                center.getY() + verticalScrollDist % verticalScrollStep
                        )
                );
            }
            else {
                swipeFromCenter(
                        new Point(
                                center.getX() + horizontalScrollStep,
                                center.getY() + verticalScrollStep
                        )
                );
            }
        }

        return element;
    }

    public void swiper(SwipeDirection direction){
        Point center = new Point(
                driver.manage().window().getSize().getWidth()/2,
                driver.manage().window().getSize().getHeight()/2
        );

        Point destination = switch (direction) {
            case up -> new Point(
                    center.getX(),
                    center.getY() + (3 * (driver.manage().window().getSize().getHeight() / 4))
            );
            case down -> new Point(
                    center.getX(),
                    center.getY() - (3 * (driver.manage().window().getSize().getHeight() / 4))
            );
            case left -> new Point(
                    center.getX() - (3 * (driver.manage().window().getSize().getWidth() / 4)),
                    center.getY()
            );
            case right -> new Point(
                    center.getX() + (3 * (driver.manage().window().getSize().getWidth() / 4)),
                    center.getY()
            );
        };
        swipe(center, destination);
    }

    public void swipe(Point pointOfDeparture, Point pointOfArrival){
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence sequence = new Sequence(finger, 1);
        sequence.addAction(finger.createPointerMove(
                Duration.ofMillis(0),
                PointerInput.Origin.viewport(), pointOfDeparture.x, pointOfDeparture.y)
        );
        sequence.addAction(finger.createPointerDown(PointerInput.MouseButton.MIDDLE.asArg()));
        sequence.addAction(new Pause(finger, ofMillis(750)));
        sequence.addAction(finger.createPointerMove(
                Duration.ofMillis(250),
                PointerInput.Origin.viewport(), pointOfArrival.x, pointOfArrival.y)
        );
        sequence.addAction(finger.createPointerUp(PointerInput.MouseButton.MIDDLE.asArg()));
        performSequence(sequence, System.currentTimeMillis());
    }

    public void performSequence(Sequence sequence, long initialTime){
        try {driver.perform(singletonList(sequence));}
        catch (WebDriverException exception){
            if (!(System.currentTimeMillis() - initialTime > 15000)) {
                log.warning("Recursion! (" + exception.getClass().getName() + ")");
                performSequence(sequence, initialTime);
            }
            else throw exception;
        }
    }

    public void swipeFromCenter(Point point){
        Point center = new Point(
                driver.manage().window().getSize().getWidth()/2,
                driver.manage().window().getSize().getHeight()/2
        );
        swipe(center, point);
    }

    public WebElement swipeElement(WebElement element, Point point){
        Point center = new Point(element.getLocation().x, element.getLocation().y);
        swipe(center, point);
        return element;
    }

    public WebElement swipeWithOffset(WebElement element, Integer xOffset, Integer yOffset){
        Point from = new Point(element.getLocation().x, element.getLocation().y);
        Point to = new Point(element.getLocation().x + xOffset, element.getLocation().y + yOffset);
        swipe(from, to);
        return element;
    }

    public WebElement swipeFromTo(WebElement element, WebElement destinationElement){
        Point from = new Point(element.getLocation().x, element.getLocation().y);
        Point to = new Point(destinationElement.getLocation().x, destinationElement.getLocation().y);
        swipe(from, to);
        return element;
    }
}
