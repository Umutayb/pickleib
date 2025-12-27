package pickleib.utilities.element.acquisition;

import context.ContextStore;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import pickleib.exceptions.PickleibException;
import pickleib.utilities.interfaces.repository.PageObjectRepository;
import utils.Printer;
import utils.arrays.lambda.Collectors;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static utils.StringUtilities.*;
import static utils.reflection.ReflectionUtilities.getFieldValue;
import static utils.reflection.ReflectionUtilities.getFields;
import static utils.StringUtilities.Color.*;

/**
 * A core utility class responsible for the low-level retrieval of WebElements.
 * <p>
 * This class serves two primary functions:
 * <ol>
 * <li><b>List Filtering:</b> Finding a specific element within a list of elements based on text or attributes (with built-in waiting logic).</li>
 * <li><b>Reflection:</b> Dynamically locating Page Objects and their fields based on String names (used by the POM design).</li>
 * </ol>
 *
 * @author  Umut Ay Bora
 */
@SuppressWarnings("unused")
public class ElementAcquisition {

    /**
     * Global timeout for element acquisition, retrieved from {@link ContextStore}.
     * Defaults to 15,000ms (15 seconds) if "element-timeout" is not set.
     */
    static long elementTimeout = ContextStore.getInt("element-timeout", 15000);
    static Printer log = new Printer(ElementAcquisition.class);

    /**
     * Acquires a specific element from a list by matching a given attribute value.
     * <p>
     * This method is <b>robust</b>: it includes a polling loop that lasts up to {@code elementTimeout}.
     * If a {@link WebDriverException} (e.g., StaleElementReferenceException) occurs during iteration,
     * the method catches it, logs a warning, and retries until the timeout is reached.
     * </p>
     *
     * @param items          The list of WebElements to search through.
     * @param attributeName  The name of the HTML attribute to inspect (e.g., "href", "class", "id").
     * @param attributeValue The value to search for within that attribute (supports partial matches).
     * @return The matching {@link WebElement}.
     * @throws NoSuchElementException If no matching element is found after the timeout period.
     */
    public static WebElement acquireElementUsingAttributeAmongst(List<WebElement> items, String attributeName, String attributeValue){
        log.info("Acquiring element called " + markup(BLUE, attributeValue) + " using its " + markup(BLUE, attributeName) + " attribute");
        boolean condition = true;
        boolean timeout = false;
        long initialTime = System.currentTimeMillis();
        WebDriverException caughtException = null;
        int counter = 0;
        while (!(System.currentTimeMillis() - initialTime > elementTimeout)){
            try {
                for (WebElement selection : items) {
                    String attribute = selection.getAttribute(attributeName);
                    if (attribute != null &&
                            (attribute.equalsIgnoreCase(attributeValue) || attribute.contains(attributeValue))
                    ) return selection;
                }
            }
            catch (WebDriverException webDriverException){
                if (counter != 0 && webDriverException.getClass().getName().equals(caughtException.getClass().getName()))
                    log.warning("Iterating... (" + webDriverException.getClass().getName() + ")");

                caughtException = webDriverException;
                counter++;
            }
        }
        throw new NoSuchElementException("No element with the attributes '" + attributeName + " : " + attributeValue + "' could be found!");
    }

    /**
     * Acquires a specific element from a list by matching its visible text.
     * <p>
     * Like {@link #acquireElementUsingAttributeAmongst}, this method includes a retry mechanism
     * to handle dynamic page updates or stale elements during the search process.
     * </p>
     *
     * @param items         The list of WebElements to search through.
     * @param selectionName The text content to search for (case-insensitive, supports partial match).
     * @return The matching {@link WebElement}.
     * @throws NoSuchElementException If no element with the specified text is found after the timeout.
     */
    public static WebElement acquireNamedElementAmongst(List<WebElement> items, String selectionName){
        boolean timeout = false;
        long initialTime = System.currentTimeMillis();
        WebDriverException caughtException = null;
        int counter = 0;
        do {
            try {
                for (WebElement selection : items) {
                    String text = selection.getText();
                    if (text.equalsIgnoreCase(selectionName) || text.contains(selectionName)) return selection;
                }
            }
            catch (WebDriverException webDriverException){
                if (counter == 0) {
                    log.warning("Iterating... (" + webDriverException.getClass().getName() + ")");
                    caughtException = webDriverException;
                }
                else if (!webDriverException.getClass().getName().equals(caughtException.getClass().getName())){
                    log.warning("Iterating... (" + webDriverException.getClass().getName() + ")");
                    caughtException = webDriverException;
                }
                counter++;
            }
        }
        while (!(System.currentTimeMillis() - initialTime > elementTimeout));
        throw new NoSuchElementException("No element with text/name '" + selectionName + "' could be found!");
    }

    /**
     * A helper record that utilizes Java Reflection to interact with the {@link PageObjectRepository}.
     * <p>
     * This component allows the framework to convert string representations of pages (e.g., "LoginPage")
     * into actual instantiated objects, and string representations of elements (e.g., "usernameInput")
     * into actual {@link WebElement} instances.
     * </p>
     *
     *
     * @param pageRepositoryClass The class definition of the ObjectRepository to be reflected upon.
     * @param <ObjectRepository>  The type of the repository.
     */
    public record Reflections<ObjectRepository extends PageObjectRepository>(
            Class<ObjectRepository> pageRepositoryClass) {

        /**
         * Instantiates a Page Object class based on its name found in the ObjectRepository.
         *
         * @param pageName The name of the field in the ObjectRepository representing the page.
         * @return A new instance of the Page Object.
         * @throws RuntimeException If the class cannot be instantiated (e.g., private constructor, missing class).
         */
        Object getPageObject(String pageName) {
            try {
                return Arrays.stream(pageRepositoryClass.getDeclaredFields())
                        .filter(field -> field.getName().equalsIgnoreCase(pageName))
                        .collect(Collectors.toSingleton())
                        .getType()
                        .getDeclaredConstructor()
                        .newInstance();
            } catch (
                    InstantiationException |
                    IllegalAccessException |
                    NoSuchMethodException |
                    InvocationTargetException e
            ) {
                throw new RuntimeException(e);
            }
        }

        /**
         * Acquires a single {@link WebElement} from a given page using reflection.
         *
         * @param elementFieldName The name of the WebElement field in the Page Object.
         * @param pageName         The name of the page (case-insensitive).
         * @return The retrieved WebElement.
         * @throws PickleibException If the page object or the element field cannot be found.
         */
        public WebElement getElementFromPage(String elementFieldName, String pageName) {
            pageName = firstLetterDeCapped(pageName);
            Map<String, Object> pageFields;
            Object pageObject = getPageObject(pageName);
            if (!getFields(pageObject).isEmpty())
                pageFields = getFields(pageObject);
            else
                throw new PickleibException("ObjectRepository does not contain an instance of " + pageName + " object!");
            if (pageFields.get(elementFieldName) == null)
                throw new PickleibException("The " + highlighted(YELLOW, pageName) + " page object does not contain " + highlighted(YELLOW, elementFieldName) + " element!");
            return (WebElement) pageFields.get(elementFieldName);
        }

        /**
         * Acquires a list of {@link WebElement}s from a given page using reflection.
         *
         * @param elementListFieldName The name of the List field in the Page Object.
         * @param pageName             The name of the page (case-insensitive).
         * @return The retrieved list of WebElements.
         * @throws PickleibException If the page object or the element list field cannot be found.
         */
        @SuppressWarnings("unchecked")
        public List<WebElement> getElementsFromPage(String elementListFieldName, String pageName) {
            Map<String, Object> pageFields;
            pageName = firstLetterDeCapped(pageName);
            Object pageObject = getPageObject(pageName);
            if (!getFields(pageObject).isEmpty())
                pageFields = getFields(pageObject);
            else
                throw new PickleibException("ObjectRepository does not contain an instance of " + pageName + " object!");
            if (pageFields.get(elementListFieldName) == null)
                throw new PickleibException("The " + highlighted(YELLOW, pageName) + " page object does not contain " + highlighted(YELLOW, elementListFieldName) + " element list!");
            return (List<WebElement>) pageFields.get(elementListFieldName);
        }

        /**
         * Generic utility to acquire a field value from a class instance using reflection.
         *
         * @param fieldName  The name of the field to retrieve.
         * @param inputClass The class type of the page object.
         * @param <PageObject> The type of the page object.
         * @return The value of the field cast to a {@link WebElement}.
         */
        public <PageObject> WebElement getElement(String fieldName, Class<PageObject> inputClass) {
            return (WebElement) getFieldValue(fieldName, inputClass);
        }
    }
}