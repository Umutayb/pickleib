package pickleib.web.driver;

import org.junit.jupiter.api.Test;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

class ThreadSafeDriverTest {

    @Test
    void threads_get_isolated_driver_instances() throws Exception {
        CountDownLatch latch = new CountDownLatch(2);

        Thread t1 = new Thread(() -> {
            assertNull(PickleibWebDriver.get(), "New thread should have null driver");
            latch.countDown();
        });

        Thread t2 = new Thread(() -> {
            assertNull(PickleibWebDriver.get(), "New thread should have null driver");
            latch.countDown();
        });

        t1.start();
        t2.start();
        latch.await();
    }

    @Test
    void terminate_removes_threadlocal_reference() {
        assertDoesNotThrow(PickleibWebDriver::terminate);
        assertNull(PickleibWebDriver.get());
    }
}
