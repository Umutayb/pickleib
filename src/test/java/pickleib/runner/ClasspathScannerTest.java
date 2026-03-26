package pickleib.runner;

import org.junit.jupiter.api.Test;
import pickleib.annotations.PageObject;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ClasspathScannerTest {

    @Test
    void scanForAnnotatedClasses_returns_empty_for_nonexistent_package() {
        List<Class<?>> results = ClasspathScanner.scanForAnnotatedClasses(
            PageObject.class, "com.nonexistent.package"
        );
        assertTrue(results.isEmpty());
    }

    @Test
    void scanForAnnotatedClasses_returns_empty_when_no_matches() {
        // pickleib.annotations package has no @PageObject-annotated classes
        List<Class<?>> results = ClasspathScanner.scanForAnnotatedClasses(
            PageObject.class, "pickleib.annotations"
        );
        assertTrue(results.isEmpty());
    }

    @Test
    void scanForAnnotatedClasses_handles_empty_packages() {
        List<Class<?>> results = ClasspathScanner.scanForAnnotatedClasses(PageObject.class);
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }
}
