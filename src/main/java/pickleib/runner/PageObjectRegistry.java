package pickleib.runner;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.PageFactory;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import pickleib.utilities.element.ElementBundle;
import pickleib.utilities.element.acquisition.ElementAcquisition;
import pickleib.utilities.interfaces.repository.ElementRepository;
import pickleib.web.driver.PickleibWebDriver;
import pickleib.platform.driver.PickleibAppiumDriver;
import utils.Printer;

import java.lang.reflect.Field;
import java.time.Duration;
import java.util.*;

import static utils.StringUtilities.firstLetterDeCapped;

public class PageObjectRegistry implements ElementRepository {

    private static final Printer log = new Printer(PageObjectRegistry.class);

    // Maps page name (lowercase) -> metadata
    private final Map<String, PageObjectMetadata> registry = new HashMap<>();

    // Thread-local cache of instantiated page objects
    private final ThreadLocal<Map<String, Object>> instances = ThreadLocal.withInitial(HashMap::new);

    record PageObjectMetadata(Class<?> pageClass, String name, String platform) {}

    public void register(Class<?> pageClass, String name, String platform) {
        String key = (name.isEmpty() ? pageClass.getSimpleName() : name).toLowerCase();
        registry.put(key, new PageObjectMetadata(pageClass, name.isEmpty() ? pageClass.getSimpleName() : name, platform));
    }

    public boolean isRegistered(String pageName) {
        return registry.containsKey(pageName.toLowerCase());
    }

    public Class<?> getPageClass(String pageName) {
        PageObjectMetadata meta = registry.get(pageName.toLowerCase());
        if (meta == null) throw new NoSuchElementException("Page '" + pageName + "' not found in registry");
        return meta.pageClass();
    }

    public int size() {
        return registry.size();
    }

    /**
     * Gets or creates a page object instance for the current thread.
     * Handles PageFactory initialization based on platform.
     */
    private Object getOrCreateInstance(String pageName) {
        pageName = firstLetterDeCapped(pageName);
        String key = pageName.toLowerCase();
        Map<String, Object> threadInstances = instances.get();

        if (threadInstances.containsKey(key)) {
            return threadInstances.get(key);
        }

        PageObjectMetadata meta = registry.get(key);
        if (meta == null) throw new NoSuchElementException("Page '" + pageName + "' not found in registry");

        try {
            Object instance = meta.pageClass().getDeclaredConstructor().newInstance();

            // Initialize @FindBy fields based on platform
            String platform = meta.platform().toLowerCase();
            if (platform.equals("web")) {
                RemoteWebDriver driver = PickleibWebDriver.get();
                if (driver != null) PageFactory.initElements(driver, instance);
            } else {
                // android, ios, mobile
                if (PickleibAppiumDriver.get() != null) {
                    PageFactory.initElements(
                        new AppiumFieldDecorator(PickleibAppiumDriver.get(), Duration.ofSeconds(15)),
                        instance
                    );
                }
            }

            threadInstances.put(key, instance);
            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate page object: " + meta.pageClass().getSimpleName(), e);
        }
    }

    /**
     * Gets a WebElement field from a page object by field name.
     */
    private WebElement getField(Object pageObject, String fieldName) {
        try {
            Field field = findField(pageObject.getClass(), fieldName);
            if (field == null) throw new NoSuchElementException(
                "Element '" + fieldName + "' not found on " + pageObject.getClass().getSimpleName()
            );
            field.setAccessible(true);
            return (WebElement) field.get(pageObject);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private List<WebElement> getListField(Object pageObject, String fieldName) {
        try {
            Field field = findField(pageObject.getClass(), fieldName);
            if (field == null) throw new NoSuchElementException(
                "Element list '" + fieldName + "' not found on " + pageObject.getClass().getSimpleName()
            );
            field.setAccessible(true);
            return (List<WebElement>) field.get(pageObject);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Field findField(Class<?> clazz, String fieldName) {
        // Case-insensitive field search
        for (Field field : clazz.getDeclaredFields()) {
            if (field.getName().equalsIgnoreCase(fieldName)) return field;
        }
        // Check superclass too
        if (clazz.getSuperclass() != null && clazz.getSuperclass() != Object.class) {
            return findField(clazz.getSuperclass(), fieldName);
        }
        return null;
    }

    // === ElementRepository implementation ===

    @Override
    public WebElement acquireElementFromPage(String elementName, String pageName) {
        Object page = getOrCreateInstance(pageName);
        return getField(page, elementName);
    }

    @Override
    public List<WebElement> acquireElementsFromPage(String elementListName, String pageName) {
        Object page = getOrCreateInstance(pageName);
        return getListField(page, elementListName);
    }

    @Override
    public WebElement acquireListedElementFromPage(String elementName, String listName, String pageName) {
        List<WebElement> elements = acquireElementsFromPage(listName, pageName);
        return ElementAcquisition.acquireNamedElementAmongst(elements, elementName);
    }

    @Override
    public WebElement acquireListedElementByAttribute(String attributeName, String attributeValue, String listName, String pageName) {
        List<WebElement> elements = acquireElementsFromPage(listName, pageName);
        return ElementAcquisition.acquireElementUsingAttributeAmongst(elements, attributeName, attributeValue);
    }

    @Override
    public List<ElementBundle<String>> acquireElementList(List<Map<String, String>> signForms, String pageName) {
        List<ElementBundle<String>> bundles = new ArrayList<>();
        for (Map<String, String> map : signForms) {
            String elementName = map.get("Input Element");
            String inputText = map.get("Input");
            WebElement element = acquireElementFromPage(elementName, pageName);
            bundles.add(new ElementBundle<>(element, elementName, "web", inputText));
        }
        return bundles;
    }

    @Override
    public ElementBundle<Map<String, String>> acquireElementBundleFromPage(
            String elementFieldName, String pageName, Map<String, String> specifications) {
        WebElement element = acquireElementFromPage(elementFieldName, pageName);
        return new ElementBundle<>(element, elementFieldName, "web", specifications);
    }

    @Override
    public List<ElementBundle<Map<String, String>>> acquireElementBundlesFromPage(
            String pageName, List<Map<String, String>> specifications) {
        List<ElementBundle<Map<String, String>>> bundles = new ArrayList<>();
        for (Map<String, String> spec : specifications) {
            String elementName = spec.get("Element Name");
            if (elementName != null) {
                bundles.add(acquireElementBundleFromPage(elementName, pageName, spec));
            }
        }
        return bundles;
    }
}
