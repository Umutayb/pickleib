package pickleib.mobile.utilities;

import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.remote.RemoteWebDriver;
import pickleib.enums.Direction;
import pickleib.mobile.driver.PickleibAppiumDriver;
import pickleib.utilities.Utilities;
import utils.StringUtilities;

import java.time.Duration;

import static java.time.Duration.ofMillis;
import static java.util.Collections.singletonList;

public abstract class MobileUtilities extends Utilities {

    /**
     * MobileUtilities for frameworks that use the Pickleib driver
     */
    public MobileUtilities() {
        super(PickleibAppiumDriver.get(), (element) -> centerElement(element, PickleibAppiumDriver.get()));
    }

    /**
     * MobileUtilities for frameworks that do not use the Pickleib driver
     */
    public MobileUtilities(RemoteWebDriver driver) {
        super(driver, (element) -> centerElement(element, driver));
    }

    public RemoteWebDriver driver() {
        return this.driver;
    }

    /**
     * Scrolls an element to the center of the view
     *
     * @param element target element
     * @return returns the targeted element
     */
    //TODO: Implement iterative scroll that will swipe or center depending on if the element can be found in view.
    public static WebElement centerElement(WebElement element, RemoteWebDriver driver) {
        Point center = new Point(
                driver.manage().window().getSize().getWidth() / 2,
                driver.manage().window().getSize().getHeight() / 2
        );

        int verticalScrollDist = element.getLocation().getY() - driver.manage().window().getSize().getHeight() / 2;
        int verticalScrollStep = driver.manage().window().getSize().getHeight() / 3;

        int horizontalScrollDist = element.getLocation().getX() - driver.manage().window().getSize().getWidth() / 2;
        int horizontalScrollStep = driver.manage().window().getSize().getWidth() / 3;

        for (int i = 0; i <= verticalScrollDist / verticalScrollStep; i++) {
            if (i == verticalScrollDist / verticalScrollStep) {
                swipeFromCenter(
                        new Point(
                                center.getX() + horizontalScrollDist % horizontalScrollStep,
                                center.getY() + verticalScrollDist % verticalScrollStep
                        ),
                        driver
                );
            } else {
                swipeFromCenter(
                        new Point(
                                center.getX() + horizontalScrollStep,
                                center.getY() + verticalScrollStep
                        ),
                        driver
                );
            }
        }

        return element;
    }

    public WebElement centerElement (WebElement element) {
        return centerElement(element, driver);
    }

    /**
     * Scrolls in a given direction
     *
     * @param direction target direction (UP or DOWN)
     */
    public void scrollOrSwipeInDirection(@NotNull Direction direction) {
        log.info("Scrolling in " + highlighted(StringUtilities.Color.BLUE, direction.name()) + " direction.");
        swiper(direction);
    }

    /**
     * Swipes in a given direction
     *
     * @param direction target direction (UP or DOWN)
     */
    public void swiper(Direction direction) {
        Point center = new Point(
                driver.manage().window().getSize().getWidth() / 2,
                driver.manage().window().getSize().getHeight() / 2
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

    /**
     * Swipes from one point to another
     *
     * @param pointOfDeparture the point where swiping starts
     * @param pointOfArrival   the point where swiping ends
     */
    public static void swipe(Point pointOfDeparture, Point pointOfArrival, RemoteWebDriver driver) {
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
        performSequence(sequence, System.currentTimeMillis(), driver);
    }

    public void swipe(Point pointOfDeparture, Point pointOfArrival) {
      swipe(pointOfDeparture, pointOfArrival, driver);
    }

    /**
     * Performs sequence
     *
     * @param sequence    target sequence
     * @param initialTime start time
     */
    public static void performSequence(Sequence sequence, long initialTime, RemoteWebDriver driver) {
        try {
            driver.perform(singletonList(sequence));
        } catch (WebDriverException exception) {
            if (!(System.currentTimeMillis() - initialTime > 15000)) {
                performSequence(sequence, initialTime, driver);
            } else throw exception;
        }
    }

    public void performSequence(Sequence sequence, long initialTime) {
        performSequence(sequence, initialTime, driver);
    }

    /**
     * Swipes from the center to the point
     *
     * @param point target point
     */
    public static void swipeFromCenter(Point point, RemoteWebDriver driver) {
        Point center = new Point(
                driver.manage().window().getSize().getWidth() / 2,
                driver.manage().window().getSize().getHeight() / 2
        );
        swipe(center, point, driver);
    }

    public void swipeFromCenter(Point point) {
       swipeFromCenter(point, driver);
    }

    /**
     * Swipes element to the point
     *
     * @param element target element
     * @param point   target point
     * @return returns the swiped element
     */
    public WebElement swipeElement(WebElement element, Point point) {
        Point center = new Point(element.getLocation().x, element.getLocation().y);
        swipe(center, point);
        return element;
    }

    /**
     * Swipes element to the point with coordinates ({xOffset}; {yOffset}).
     *
     * @param element target element
     * @param xOffset x coordinate of target point
     * @param xOffset y coordinate of target point
     * @return returns the swiped element
     */
    public WebElement swipeWithOffset(WebElement element, Integer xOffset, Integer yOffset) {
        Point from = new Point(element.getLocation().x, element.getLocation().y);
        Point to = new Point(element.getLocation().x + xOffset, element.getLocation().y + yOffset);
        swipe(from, to);
        return element;
    }

    /**
     * Swipes {element} to {destinationElement}.
     *
     * @param element            target element before swiping
     * @param destinationElement target element after swiping
     * @return returns the swiped element
     */
    public WebElement swipeFromTo(WebElement element, WebElement destinationElement) {
        Point from = new Point(element.getLocation().x, element.getLocation().y);
        Point to = new Point(destinationElement.getLocation().x, destinationElement.getLocation().y);
        swipe(from, to);
        return element;
    }
}
