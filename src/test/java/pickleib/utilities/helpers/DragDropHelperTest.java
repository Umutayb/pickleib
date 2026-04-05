package pickleib.utilities.helpers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DragDropHelperTest {

    @Mock RemoteWebDriver driver;
    @Mock WebElement source;
    @Mock WebElement target;

    DragDropHelper helper;

    @BeforeEach
    void setUp() {
        helper = new DragDropHelper(driver);
    }

    @Test
    void dragDropToAction_does_not_throw() {
        // Actions require a real browser — test that method doesn't throw with mocks
        // The actual Selenium Actions builder may throw, so we just verify construction
        assertDoesNotThrow(() -> helper.dragDropToAction(source, target));
    }

    @Test
    void dragDropByAction_does_not_throw() {
        assertDoesNotThrow(() -> helper.dragDropByAction(source, 10, 20));
    }

    @Test
    void dragDropAction_does_not_throw() {
        assertDoesNotThrow(() -> helper.dragDropAction(source, 10, 20));
    }
}
