package pickleib.utilities;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.StaleElementReferenceException;
import pickleib.exceptions.PickleibException;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class RetryPolicyTest {

    // === execute (void) tests ===

    @Test
    void execute_void_succeeds_on_first_try() {
        AtomicInteger counter = new AtomicInteger(0);
        RetryPolicy.execute(counter::incrementAndGet, 5000);
        assertEquals(1, counter.get());
    }

    @Test
    void execute_void_retries_and_succeeds() {
        AtomicInteger counter = new AtomicInteger(0);
        RetryPolicy.execute(() -> {
            if (counter.incrementAndGet() < 3)
                throw new WebDriverException("stale");
        }, 5000);
        assertEquals(3, counter.get());
    }

    @Test
    void execute_void_throws_on_timeout() {
        assertThrows(PickleibException.class, () ->
            RetryPolicy.execute(() -> {
                throw new WebDriverException("always fails");
            }, 500)
        );
    }

    // === execute (supplier) tests ===

    @Test
    void execute_supplier_returns_value() {
        String result = RetryPolicy.execute(() -> "hello", 5000);
        assertEquals("hello", result);
    }

    @Test
    void execute_supplier_retries_and_returns() {
        AtomicInteger counter = new AtomicInteger(0);
        String result = RetryPolicy.execute(() -> {
            if (counter.incrementAndGet() < 3)
                throw new WebDriverException("stale");
            return "success";
        }, 5000);
        assertEquals("success", result);
    }

    // === pollUntil tests ===

    @Test
    void pollUntil_returns_true_when_condition_met() {
        AtomicInteger counter = new AtomicInteger(0);
        boolean result = RetryPolicy.pollUntil(() -> counter.incrementAndGet() >= 3, 5000);
        assertTrue(result);
    }

    @Test
    void pollUntil_returns_false_on_timeout() {
        boolean result = RetryPolicy.pollUntil(() -> false, 500);
        assertFalse(result);
    }

    @Test
    void pollUntil_handles_exceptions_during_polling() {
        AtomicInteger counter = new AtomicInteger(0);
        boolean result = RetryPolicy.pollUntil(() -> {
            if (counter.incrementAndGet() < 3)
                throw new WebDriverException("stale");
            return true;
        }, 5000);
        assertTrue(result);
    }

    // === pollUntil with hooks tests ===

    @Test
    void pollUntil_with_hooks_calls_beforeEach_and_afterEach() {
        AtomicInteger beforeCount = new AtomicInteger(0);
        AtomicInteger afterCount = new AtomicInteger(0);
        AtomicInteger counter = new AtomicInteger(0);

        boolean result = RetryPolicy.pollUntil(
            () -> counter.incrementAndGet() >= 2,
            5000,
            beforeCount::incrementAndGet,
            afterCount::incrementAndGet,
            null
        );

        assertTrue(result);
        assertTrue(beforeCount.get() >= 2);
        assertTrue(afterCount.get() >= 2);
    }

    @Test
    void pollUntil_with_earlyExit_returns_true_on_matching_exception() {
        boolean result = RetryPolicy.pollUntil(
            () -> { throw new StaleElementReferenceException("gone"); },
            5000,
            null,
            null,
            ex -> ex instanceof StaleElementReferenceException
        );
        assertTrue(result);
    }

    @Test
    void pollUntil_with_earlyExit_continues_on_non_matching_exception() {
        AtomicInteger counter = new AtomicInteger(0);
        boolean result = RetryPolicy.pollUntil(
            () -> {
                if (counter.incrementAndGet() < 3)
                    throw new WebDriverException("not stale");
                return true;
            },
            5000,
            null,
            null,
            ex -> ex instanceof StaleElementReferenceException
        );
        assertTrue(result);
    }
}
