package pickleib.runner;

import org.junit.jupiter.api.extension.*;
import pickleib.annotations.PageObject;
import pickleib.annotations.Pickleib;
import pickleib.annotations.ScreenObject;
import pickleib.enums.Platform;
import utils.Printer;
import java.util.List;

/**
 * JUnit 5 Extension that powers the @Pickleib annotation.
 * Handles classpath scanning for @PageObject/@ScreenObject classes,
 * auto-registration into PageObjectRegistry, and @ContextValue injection.
 *
 * Usage:
 * <pre>
 * {@literal @}Pickleib(scanPackages = "pages")
 * {@literal @}ExtendWith(PickleibRunner.class)
 * public class MyTest { ... }
 * </pre>
 */
public class PickleibRunner implements BeforeAllCallback, TestInstancePostProcessor {

    private static final Printer log = new Printer(PickleibRunner.class);
    private static final PageObjectRegistry registry = new PageObjectRegistry();

    public static PageObjectRegistry getRegistry() {
        return registry;
    }

    @Override
    public void beforeAll(ExtensionContext context) {
        Class<?> testClass = context.getRequiredTestClass();
        Pickleib annotation = testClass.getAnnotation(Pickleib.class);
        if (annotation == null) return;

        String[] packages = annotation.scanPackages();
        if (packages.length == 0) {
            String pkg = testClass.getPackageName();
            if (pkg.contains(".")) pkg = pkg.substring(0, pkg.indexOf('.'));
            packages = new String[]{pkg};
        }

        // Scan for @PageObject classes
        List<Class<?>> pageObjects = ClasspathScanner.scanForAnnotatedClasses(PageObject.class, packages);
        for (Class<?> clazz : pageObjects) {
            PageObject po = clazz.getAnnotation(PageObject.class);
            registry.register(clazz, po.name(), po.platform());
            log.info("Registered @PageObject: " + clazz.getSimpleName());
        }

        // Scan for @ScreenObject classes
        List<Class<?>> screenObjects = ClasspathScanner.scanForAnnotatedClasses(ScreenObject.class, packages);
        for (Class<?> clazz : screenObjects) {
            ScreenObject so = clazz.getAnnotation(ScreenObject.class);
            registry.register(clazz, so.name(), so.platform());
            log.info("Registered @ScreenObject: " + clazz.getSimpleName());
        }

        log.info("Pickleib initialized with " + registry.size() + " page objects");
    }

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) {
        ContextValueInjector.injectFields(testInstance);
    }
}
