package common;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.junit.jupiter.api.extension.TestWatcher;
import utils.Printer;
import utils.StringUtilities;

import java.util.Objects;

public class StatusWatcher implements TestWatcher, TestExecutionExceptionHandler {

    Printer log = new Printer(StatusWatcher.class);

    public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        TestStatus.markFailed();
        throw throwable;
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        log.warning("FAILED: " + context.getDisplayName());
        log.info(StringUtilities.highlighted(StringUtilities.Color.RED, Objects.requireNonNull(context.getExecutionException().orElse(null)).getMessage()));
    }

    @Override
    public void testSuccessful(ExtensionContext context) {
        log.success("PASSED: " + context.getDisplayName());
    }

    @Override
    public void testAborted(ExtensionContext context, Throwable cause) {
        TestStatus.clear();
    }

    public static class TestStatus {
        private static final ThreadLocal<Boolean> failed = ThreadLocal.withInitial(() -> false);
        public static void markFailed() { failed.set(true); }
        public static boolean isFailed() { return failed.get(); }
        public static void clear() { failed.remove(); }

    }

}
