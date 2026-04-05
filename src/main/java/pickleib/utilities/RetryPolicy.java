package pickleib.utilities;

import org.openqa.selenium.WebDriverException;
import pickleib.exceptions.PickleibException;
import utils.Printer;

import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Centralized retry/poll utility that replaces duplicated do-while timeout loops
 * throughout the codebase. Provides consistent retry behavior, logging, and
 * exception handling for all WebDriver interactions.
 */
public class RetryPolicy {

    private static final Printer log = new Printer(RetryPolicy.class);

    /**
     * Retries a void action until it succeeds or the timeout is reached.
     * Throws PickleibException wrapping the last caught exception on timeout.
     */
    public static void execute(Runnable action, long timeoutMs) {
        execute(() -> { action.run(); return null; }, timeoutMs);
    }

    /**
     * Retries a supplier action until it returns a value or the timeout is reached.
     * Throws PickleibException wrapping the last caught exception on timeout.
     */
    public static <T> T execute(Supplier<T> action, long timeoutMs) {
        long startTime = System.currentTimeMillis();
        WebDriverException lastException = null;
        int counter = 0;

        do {
            try {
                return action.get();
            } catch (WebDriverException e) {
                if (counter == 0) log.warning("Retrying due to: " + e.getClass().getSimpleName());
                lastException = e;
                counter++;
            }
        } while (System.currentTimeMillis() - startTime < timeoutMs);

        log.warning("Failed after " + counter + " attempt(s): " + lastException.getClass().getSimpleName());
        throw new PickleibException(lastException);
    }

    /**
     * Polls until a condition returns true or the timeout is reached.
     * Returns false on timeout (does not throw).
     */
    public static boolean pollUntil(BooleanSupplier condition, long timeoutMs) {
        return pollUntil(condition, timeoutMs, null, null, null);
    }

    /**
     * Polls until a condition returns true or the timeout is reached, with optional
     * per-iteration hooks and early exit on specific exceptions.
     *
     * @param condition  the condition to poll
     * @param timeoutMs  maximum time to poll in milliseconds
     * @param beforeEach optional hook to run before each poll attempt (e.g., set implicit wait)
     * @param afterEach  optional hook to run after each poll attempt (e.g., restore implicit wait)
     * @param earlyExit  optional predicate; if a WebDriverException matches, return true immediately
     * @return true if the condition was met or earlyExit matched, false on timeout
     */
    public static boolean pollUntil(
            BooleanSupplier condition,
            long timeoutMs,
            Runnable beforeEach,
            Runnable afterEach,
            Predicate<WebDriverException> earlyExit) {

        long startTime = System.currentTimeMillis();
        WebDriverException lastException = null;
        int counter = 0;

        do {
            try {
                if (beforeEach != null) beforeEach.run();
                boolean result = condition.getAsBoolean();
                if (result) return true;
            } catch (WebDriverException e) {
                if (earlyExit != null && earlyExit.test(e)) return true;
                if (counter == 0) log.warning("Retrying due to: " + e.getClass().getSimpleName());
                lastException = e;
                counter++;
            } finally {
                if (afterEach != null) afterEach.run();
            }
        } while (System.currentTimeMillis() - startTime < timeoutMs);

        if (counter > 0) log.warning("Failed after " + counter + " attempt(s): " + lastException.getClass().getSimpleName());
        return false;
    }
}
