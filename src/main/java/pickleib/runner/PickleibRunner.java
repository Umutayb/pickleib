package pickleib.runner;

import org.junit.jupiter.api.extension.*;
import pickleib.annotations.PageObject;
import pickleib.annotations.Pickleib;
import pickleib.annotations.ScreenObject;
import pickleib.steps.BuiltInSteps;
import pickleib.utilities.element.acquisition.design.PageObjectJson;
import utils.FileUtilities;
import utils.Printer;
import java.io.File;
import java.util.List;

/**
 * JUnit 5 Extension that powers the @Pickleib annotation.
 * <p>
 * When used with {@code @Pickleib} (no parameters), auto-detects the element repository:
 * <ol>
 *   <li>Looks for {@code src/test/resources/page-repository.json} — if found, uses JSON repository</li>
 *   <li>Otherwise scans for {@code @PageObject}/{@code @ScreenObject} classes in the {@code pages} package</li>
 *   <li>If neither is found, logs a warning with setup instructions</li>
 * </ol>
 *
 * Usage:
 * <pre>
 * {@literal @}Pickleib
 * {@literal @}ExtendWith(PickleibRunner.class)
 * public class MyTest { ... }
 * </pre>
 */
public class PickleibRunner implements BeforeAllCallback, TestInstancePostProcessor {

    private static final Printer log = new Printer(PickleibRunner.class);
    private static final PageObjectRegistry registry = new PageObjectRegistry();
    public static final String DEFAULT_PAGE_REPOSITORY = "src/test/resources/page-repository.json";
    public static final String DEFAULT_SCAN_PACKAGE = "pages";

    public static PageObjectRegistry getRegistry() {
        return registry;
    }

    @Override
    public void beforeAll(ExtensionContext context) {
        Class<?> testClass = context.getRequiredTestClass();
        Pickleib annotation = testClass.getAnnotation(Pickleib.class);
        if (annotation == null) return;

        // Explicit configuration takes priority
        String pageRepository = annotation.pageRepository();
        String[] packages = annotation.scan();
        boolean jsonLoaded = false;

        if (!pageRepository.isEmpty()) {
            loadJsonRepository(pageRepository);
            jsonLoaded = true;
        }

        if (packages.length > 0) {
            scanAndRegister(packages);
        }

        // Auto-detect: scan page objects first, fall back to JSON
        if (pageRepository.isEmpty() && packages.length == 0) {
            scanAndRegister(new String[]{DEFAULT_SCAN_PACKAGE});
            if (registry.size() == 0 && new File(DEFAULT_PAGE_REPOSITORY).exists()) {
                loadJsonRepository(DEFAULT_PAGE_REPOSITORY);
                jsonLoaded = true;
            }
        }

        if (!jsonLoaded && registry.size() == 0) {
            log.warning("No element repository found. To get started, either:\n" +
                    "  1. Create src/test/resources/page-repository.json (JSON approach)\n" +
                    "  2. Create @PageObject classes in the 'pages' package (Page Object approach)\n" +
                    "  See https://github.com/Umutayb/Pickleib#-getting-started for details.");
        }

        log.info("Pickleib initialized" +
                (jsonLoaded ? " with JSON repository" : " with " + registry.size() + " page objects"));
    }

    private void loadJsonRepository(String path) {
        PageObjectJson jsonRepo = new PageObjectJson(FileUtilities.Json.parseJsonFile(path));
        BuiltInSteps.setElementRepository(jsonRepo);
        log.info("Loaded page repository from " + path);
    }

    private void scanAndRegister(String[] packages) {
        List<Class<?>> pageObjects = ClasspathScanner.scanForAnnotatedClasses(PageObject.class, packages);
        for (Class<?> clazz : pageObjects) {
            PageObject po = clazz.getAnnotation(PageObject.class);
            registry.register(clazz, po.name(), po.platform());
            log.info("Registered @PageObject: " + clazz.getSimpleName());
        }

        List<Class<?>> screenObjects = ClasspathScanner.scanForAnnotatedClasses(ScreenObject.class, packages);
        for (Class<?> clazz : screenObjects) {
            ScreenObject so = clazz.getAnnotation(ScreenObject.class);
            registry.register(clazz, so.name(), so.platform());
            log.info("Registered @ScreenObject: " + clazz.getSimpleName());
        }
    }

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) {
        ContextValueInjector.injectFields(testInstance);
    }
}
