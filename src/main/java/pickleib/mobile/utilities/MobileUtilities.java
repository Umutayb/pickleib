package pickleib.mobile.utilities;

import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import pickleib.enums.Direction;
import pickleib.enums.ElementState;
import pickleib.exceptions.PickleibException;
import pickleib.utilities.PolymorphicUtilities;
import pickleib.utilities.Utilities;
import pickleib.web.driver.PickleibWebDriver;
import utils.StringUtilities;

import java.time.Duration;

import static java.time.Duration.ofMillis;
import static java.util.Collections.singletonList;
import static utils.StringUtilities.Color.BLUE;

public abstract class MobileUtilities extends Utilities implements PolymorphicUtilities {

    public RemoteWebDriver driver;

    /**
     * MobileUtilities for frameworks that use the Pickleib driver
     */
    public MobileUtilities() {
        super(PickleibWebDriver.get());
        this.driver = PickleibWebDriver.get();
    }

    /**
     * MobileUtilities for frameworks that do not use the Pickleib driver
     */
    public MobileUtilities(RemoteWebDriver driver) {
        super(driver);
    }

    public RemoteWebDriver driver() {
        return this.driver;
    }

    public WebDriverWait driverWait() {
        return PickleibWebDriver.driverWait();
    }

    /**
     * Clicks the specified {@code element} with retry mechanism and optional scrolling.
     *
     * <p>
     * This method attempts to click the given {@code element} with a retry mechanism.
     * It uses an implicit wait of 500 milliseconds during the retry attempts.
     * The method supports an optional {@code scroll} for scrolling before clicking the element.
     * If the {@code scroller} is provided, it scrolls towards the specified location before clicking.
     * </p>
     *
     * <p>
     * The method logs warning messages during the iteration process, indicating WebDriver exceptions.
     * After the maximum time specified by {@code elementTimeout}, if the element is still not clickable,
     * a {@code PickleibException} is thrown, including the last caught WebDriver exception.
     * </p>
     *
     * @param element The target {@code WebElement} to be clicked with retry mechanism.
     * @throws PickleibException If the element is not clickable after the retry attempts, a {@code PickleibException} is thrown
     *                           with the last caught WebDriver exception.
     */
    public void clickElement(WebElement element, boolean scroll) {
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));
        super.clickElement(
                element,
                (targetElement) -> {
                    if (scroll) this.centerElement(targetElement).click();
                    else targetElement.click();
                }
        );
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(elementTimeout));
    }

    /**
     * Clicks the specified {@code element}.
     *
     * <p>
     * This method attempts to click the given {@code element} with a retry mechanism.
     * It uses an implicit wait of 500 milliseconds during the retry attempts.
     * </p>
     *
     * <p>
     * The method logs warning messages during the iteration process, indicating WebDriver exceptions.
     * After the maximum time specified by {@code elementTimeout}, if the element is still not clickable,
     * a {@code PickleibException} is thrown, including the last caught WebDriver exception.
     * </p>
     *
     * @param element The target {@code WebElement} to be clicked with retry mechanism.
     * @throws PickleibException If the element is not clickable after the retry attempts, a {@code PickleibException} is thrown
     *                           with the last caught WebDriver exception.
     */
    public void clickElement(WebElement element) {
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));
        super.clickElement(
                element,
                WebElement::click
        );
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(elementTimeout));
    }

    /**
     * Clicks on a button that contains {button text} text
     * It does not scroll by default.
     *
     * @param buttonText target button text
     */
    public void clickByText(String buttonText) {
        log.info("Clicking button by its text " + highlighted(BLUE, buttonText));
        WebElement element = getElementByText(buttonText);
        clickElement(element);
    }

    /**
     * Clicks on a button that contains {button text} text
     * It does not scroll by default.
     *
     * @param buttonText target button text
     * @param scroll     The {scroll} to be used for scrolling. If {@code null}, the default scrolling behavior is applied.
     */
    public void clickButtonByText(String buttonText, boolean scroll) {
        log.info("Clicking button by its text " + highlighted(BLUE, buttonText));
        WebElement element = getElementByText(buttonText);
        clickElement(element, scroll);
    }

    /**
     * Clicks an element if its present (in enabled state)
     *
     * @param element target element
     * @param scroll  The {@code scroll} to be used for scrolling. If {@code null}, the default scrolling behavior is applied.
     */
    public void clickIfPresent(WebElement element, Boolean scroll) {
        try {
            clickElement(element, scroll);
        } catch (WebDriverException exception) {
            log.warning(exception.getMessage());
        }
    }

    /**
     * Clears and fills a given input
     *
     * @param inputElement target input element
     * @param inputText    input text
     * @param verify       verifies the input text value equals to an expected text if true
     */
    public void clearFillInput(WebElement inputElement, String inputText, boolean verify) {
        fillInputElement(inputElement, inputText, null, verify);
    }

    /**
     * Clears and fills a given input
     *
     * @param inputElement target input element
     * @param inputText    input text
     * @param verify       verifies the input text value equals to an expected text if true
     */
    public void clearFillInput(WebElement inputElement, String inputText, boolean scroll, boolean verify) {
        if (scroll) fillInputElement(inputElement, inputText, this::centerElement, verify);
        else fillInputElement(inputElement, inputText, null, verify);
    }


    /**
     * Scrolls an element to the center of the view
     *
     * @param element target element
     * @return returns the targeted element
     */
    //TODO: Implement iterative scroll that will swipe or center depending on if the element can be found in view.
    public WebElement centerElement(WebElement element) {
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
                        )
                );
            } else {
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

    /**
     * Scrolls in a given direction
     *
     * @param direction target direction (UP or DOWN)
     */
    public void scroll(@NotNull Direction direction) {
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
    public void swipe(Point pointOfDeparture, Point pointOfArrival) {
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


    /**
     * Performs sequence
     *
     * @param sequence    target sequence
     * @param initialTime start time
     */
    public void performSequence(Sequence sequence, long initialTime) {
        try {
            driver.perform(singletonList(sequence));
        } catch (WebDriverException exception) {
            if (!(System.currentTimeMillis() - initialTime > 15000)) {
                log.warning("Recursion! (" + exception.getClass().getName() + ")");
                performSequence(sequence, initialTime);
            } else throw exception;
        }
    }

    /**
     * Swipes from the center to the point
     *
     * @param point target point
     */
    public void swipeFromCenter(Point point) {
        Point center = new Point(
                driver.manage().window().getSize().getWidth() / 2,
                driver.manage().window().getSize().getHeight() / 2
        );
        swipe(center, point);
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

    /**
     * Clears and fills a given input
     *
     * @param inputElement target input element
     * @param inputText    input text
     * @param scroll       scrolls if true
     * @param verify       verifies the input text value equals to an expected text if true
     */
    public void clearFillInput(WebElement inputElement, String inputText, @NotNull boolean scroll, Boolean verify) {
        fillInputElement(inputElement, inputText, scroll, verify);
    }

    /**
     * Clears and fills a given input
     *
     * @param inputElement target input element
     * @param inputText    input text
     */
    public void fillInput(WebElement inputElement, String inputText) {
        // This method clears the input field before filling it
        fillInputElement(inputElement, inputText, false, false);
    }

    /**
     * Clears and fills a given input
     *
     * @param inputElement target input element
     * @param inputText    input text
     */
    public void fillAndVerifyInput(WebElement inputElement, String inputText) {
        // This method clears the input field before filling it
        fillInputElement(inputElement, inputText, false, true);
    }

    /**
     * Clears and fills a given input
     *
     * @param inputElement target input element
     * @param inputText    input text
     */
    protected void fillAndVerifyInput(WebElement inputElement, String inputText, Boolean scroll) {
        // This method clears the input field before filling it
        fillInputElement(inputElement, inputText, scroll, true);
    }

    /**
     * Clears and fills a given input
     *
     * @param inputElement target input element
     * @param inputText    input text
     * @param scroll       scrolls if true
     * @param verify       verifies the input text value equals to an expected text if true
     */
    protected void fillInputElement(WebElement inputElement, String inputText, @NotNull Boolean scroll, Boolean verify) {
        // This method clears the input field before filling it
        elementIs(inputElement, ElementState.displayed);
        if (scroll) centerElement(inputElement).sendKeys(inputText);
        else centerElement(inputElement).sendKeys(inputText);
        log.warning(inputElement.getText());

        assert !verify || inputText.equals(inputElement.getAttribute("value"));
    }
}
