package pickleib.platform.utilities;

import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.pagefactory.ByAll;
import pickleib.enums.Direction;
import pickleib.platform.driver.PickleibAppiumDriver;
import pickleib.utilities.Utilities;
import pickleib.utilities.interfaces.functions.LocateElement;
import java.time.Duration;
import java.util.List;

import static java.time.Duration.ofMillis;
import static java.util.Collections.singletonList;
import static utils.StringUtilities.highlighted;
import static utils.StringUtilities.Color.*;

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
     * Centers the specified WebElement within the viewable area of the RemoteWebDriver's window.
     * If the element is not initially visible, this method scrolls to center it.
     *
     * @param element The WebElement to be centered.
     * @param driver  The RemoteWebDriver instance.
     * @return The centered WebElement.
     * @throws WebDriverException if WebDriver encounters an exception while attempting to center the element.
     *                            If the exception occurs within the retry timeout (15 seconds),
     *                            the method retries the centering operation.
     *                            If the retry timeout is exceeded, the WebDriverException is thrown.
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

    /**
     * Centers the specified WebElement within the viewable area of the default RemoteWebDriver's window.
     * If the element is not initially visible, this method scrolls to center it.
     *
     * @param element The WebElement to be centered.
     * @return The centered WebElement.
     * @throws WebDriverException if WebDriver encounters an exception while attempting to center the element.
     *                            If the exception occurs within the retry timeout (15 seconds),
     *                            the method retries the centering operation.
     *                            If the retry timeout is exceeded, the WebDriverException is thrown.
     */
    public WebElement centerElement(WebElement element) {
        return centerElement(element, driver);
    }

    /**
     * Scrolls the view until the specified element is found and visible.
     *
     * This method continuously scrolls in the 'up' direction and attempts to locate the element using
     * the provided LocateElement strategy. The process is repeated until the element is found and
     * displayed or the time limit is reached.
     *
     * @param locator The LocateElement strategy used to find the target WebElement.
     * @return The located WebElement if found and displayed.
     * @throws RuntimeException if the element is not found within the specified timeout.
     *
     * @see LocateElement
     * @see Direction
     */
    public WebElement scrollUntilFound(LocateElement locator) {
        log.info("Scrolling until the element is found.");
        long initialTime = System.currentTimeMillis();
        do {
            try {
                WebElement element = locator.locate();
                if (element.isDisplayed()) return element;
                else scrollInDirection(Direction.up);
            } catch (WebDriverException ignored) {
                scrollInDirection(Direction.up);
            }
        }
        while (System.currentTimeMillis() - initialTime < elementTimeout * 5);
        throw new RuntimeException("Element could not be located!");
    }

    /**
     * Swipes upward until the specified WebElement is found or a timeout is reached.
     *
     * <p>
     * This method continuously swipes upward until the specified WebElement is found or a timeout occurs.
     * If the element is found, it is returned. If the element is not found within the specified timeout,
     * a RuntimeException is thrown.
     * </p>
     *
     * @param elementText The text of WebElement to be located.
     * @return The located WebElement.
     *
     * @throws RuntimeException if the element could not be located within the specified timeout.
     * @throws WebDriverException if WebDriver encounters an exception while interacting with the element.
     *                            If an exception occurs during the swipe operation, the method retries the swipe.
     *                            If the element is not found after the specified timeout, the WebDriverException is thrown.
     */
    public WebElement scrollUntilFound(String elementText) {
        log.info("Scrolling until an element with text " +
                highlighted(BLUE, elementText) +
                highlighted(GRAY, " is found.")
        );
        return scrollUntilFound(() -> getElementByText(elementText));
    }

    /**
     * Scrolls the view until the specified WebElement is found and visible.
     *
     * This method continuously scrolls in the 'up' direction and attempts to locate the element
     * using the provided WebElement instance. The process is repeated until the element is found and
     * displayed or the time limit is reached.
     *
     * @param element The WebElement instance to be located.
     * @return The located WebElement if found and displayed.
     * @throws RuntimeException if the element is not found within the specified timeout.
     */
    public WebElement scrollUntilFound(WebElement element) {
        log.info("Scrolling until the element is found.");
        return scrollUntilFound(() -> element);
    }

    /**
     * Scrolls through a list of elements until an element with the specified text is found and displayed.
     * Uses the provided locators to identify the elements in the list.
     *
     * @param elementText The text of the element to be found.
     * @param locators    Additional locators to identify the list of elements.
     * @return WebElement representing the found element, or null if not found within the specified time.
     */
    public WebElement scrollInList(String elementText, By... locators) {
        log.info("Scrolling the list to element with text: " + highlighted(BLUE, elementText));
        return scrollInList(elementText, driver.findElements(new ByAll(locators)));
    }

    /**
     * Scrolls through a list of elements until an element with the specified text is found and displayed.
     * Uses a provided list of elements to perform the scroll action.
     *
     * @param elementText The text of the element to be found.
     * @param elements    The list of elements to scroll through.
     * @return WebElement representing the found element, or null if not found within the specified time.
     *
     * Note: Works better with Android. Try scrollUntilFound() for iOS.
     */
    public WebElement scrollInList(String elementText, List<WebElement> elements) {
        log.info("Scrolling the list to element with text: " + highlighted(BLUE, elementText));
        long initialTime = System.currentTimeMillis();
        do {
            try {
                WebElement element = getElementByText(elementText);
                if (element.isDisplayed())
                    return element;
                else
                    throw new WebDriverException("Element is not displayed!");
            }
            catch (WebDriverException ignored) {
                log.info("Swiping...");
                swipeFromTo(elements.get(elements.size() - 1), elements.get(0));
            }
        }
        while (System.currentTimeMillis() - initialTime < elementTimeout * 5);
        throw new RuntimeException("Element '" + elementText + "' could not be located!");
    }

    /**
     * Swipes in the specified direction.
     *
     * @param direction The direction in which to swipe.
     */
    public void scrollInDirection(Direction direction) {
        log.info("Swiping " + highlighted(BLUE, direction.name().toLowerCase()));
        Point center = new Point(
                driver.manage().window().getSize().getWidth() / 2,
                driver.manage().window().getSize().getHeight() / 2
        );

        Point destination = switch (direction) {
            case up -> new Point(
                    center.getX(),
                    center.getY() - (3 * (driver.manage().window().getSize().getHeight() / 5))
            );
            case down -> new Point(
                    center.getX(),
                    center.getY() + (3 * (driver.manage().window().getSize().getHeight() / 5))
            );
            case right -> new Point(
                    center.getX() - (3 * (driver.manage().window().getSize().getWidth() / 4)),
                    center.getY()
            );
            case left -> new Point(
                    center.getX() + (3 * (driver.manage().window().getSize().getWidth() / 4)),
                    center.getY()
            );
        };
        swipe(center, destination);
    }

    /**
     * Performs a swipe gesture from the point of departure to the point of arrival on the RemoteWebDriver.
     *
     * @param pointOfDeparture The starting point of the swipe gesture.
     * @param pointOfArrival   The ending point of the swipe gesture.
     * @param driver           The RemoteWebDriver on which the swipe gesture is to be performed.
     * @throws WebDriverException if WebDriver encounters an exception while performing the swipe gesture.
     *                            If the exception occurs within the retry timeout (15 seconds),
     *                            the method retries the swipe gesture execution.
     *                            If the retry timeout is exceeded, the WebDriverException is thrown.
     */
    public static void swipe(Point pointOfDeparture, Point pointOfArrival, RemoteWebDriver driver) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence sequence = new Sequence(finger, 1);
        sequence.addAction(finger.createPointerMove(
                Duration.ofMillis(0),
                PointerInput.Origin.viewport(), pointOfDeparture.x, pointOfDeparture.y)
        );
        sequence.addAction(finger.createPointerDown(PointerInput.MouseButton.MIDDLE.asArg()));
        sequence.addAction(new Pause(finger, ofMillis(250)));
        sequence.addAction(finger.createPointerMove(
                Duration.ofMillis(750),
                PointerInput.Origin.viewport(), pointOfArrival.x, pointOfArrival.y)
        );
        sequence.addAction(new Pause(finger, ofMillis(250)));
        sequence.addAction(finger.createPointerUp(PointerInput.MouseButton.MIDDLE.asArg()));
        performSequence(sequence, System.currentTimeMillis(), driver);
        sequence.addAction(new Pause(finger, ofMillis(250)));
    }

    /**
     * Performs a swipe gesture from the point of departure to the point of arrival using the default driver.
     *
     * @param pointOfDeparture The starting point of the swipe gesture.
     * @param pointOfArrival   The ending point of the swipe gesture.
     * @throws WebDriverException if WebDriver encounters an exception while performing the swipe gesture.
     *                            If the exception occurs within the retry timeout (15 seconds),
     *                            the method retries the swipe gesture execution.
     *                            If the retry timeout is exceeded, the WebDriverException is thrown.
     */
    public void swipe(Point pointOfDeparture, Point pointOfArrival) {
        swipe(pointOfDeparture, pointOfArrival, driver);
    }

    /**
     * Performs the specified Sequence on the RemoteWebDriver, with retry logic.
     *
     * @param sequence    The Sequence of actions to be performed.
     * @param initialTime The initial time when the method was called (used for retry timeout).
     * @param driver      The RemoteWebDriver on which the Sequence is to be performed.
     * @throws WebDriverException if WebDriver encounters an exception while performing the Sequence.
     *                            If the exception occurs within the retry timeout (15 seconds),
     *                            the method retries the Sequence execution.
     *                            If the retry timeout is exceeded, the WebDriverException is thrown.
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

    /**
     * Performs the specified Sequence with the given initial time.
     *
     * @param sequence    The Sequence of actions to be performed.
     * @param initialTime The initial time when the method was called (used for retry timeout).
     * @throws WebDriverException if WebDriver encounters an exception while performing the Sequence.
     *                            If the exception occurs within the retry timeout (15 seconds),
     *                            the method retries the Sequence execution.
     *                            If the retry timeout is exceeded, the WebDriverException is thrown.
     */
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
