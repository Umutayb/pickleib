package pickleib.utilities.element.acquisition.design;

import org.openqa.selenium.WebElement;
import pickleib.utilities.element.ElementBundle;
import pickleib.utilities.element.acquisition.ElementAcquisition;
import pickleib.utilities.interfaces.repository.ElementRepository;
import pickleib.utilities.interfaces.repository.PageObjectRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static pickleib.utilities.element.acquisition.ElementAcquisition.*;
import static pickleib.utilities.DriverInspector.getElementDriverType;
import static utils.StringUtilities.Color.BLUE;
import static utils.StringUtilities.Color.GRAY;
import static utils.StringUtilities.contextCheck;
import static utils.StringUtilities.firstLetterDeCapped;
import static utils.StringUtilities.highlighted;

/**
 * Concrete implementation of {@link ElementRepository} designed for the <b>Classic Page Object Model (POM)</b> pattern.
 * <p>
 * This class uses Java Reflection to dynamically locate Page Object instances and their {@code WebElement} fields
 * at runtime. It serves as the bridge between string-based Gherkin steps and the strongly-typed Java Page Objects.
 * </p>
 * *
 * * <p>
 * <b>Naming Convention:</b>
 * The {@code pageName} argument passed to methods is expected to match the class name (e.g., "LoginPage").
 * This class automatically decapitalizes it (e.g., "loginPage") to find the corresponding instance variable
 * within the {@code ObjectRepository}.
 * </p>
 *
 * @param <ObjectRepository> The type of the repository class containing references to all Page Objects.
 *
 * @author  Umut Ay Bora
 * @since   2.0.6
 */
public class PageObjectModel <ObjectRepository extends PageObjectRepository> implements ElementRepository {

    Reflections<ObjectRepository> reflections;

    /**
     * Initializes the PageObjectModel repository.
     *
     * @param pageRepository The class definition of the ObjectRepository to be reflected upon.
     */
    public PageObjectModel(Class<ObjectRepository> pageRepository) {
        reflections = new ElementAcquisition.Reflections<>(pageRepository);
    }

    /**
     * Acquires a {@link WebElement} from a specific page object instance using reflection.
     *
     * @param elementName The name of the field in the Page Object class (e.g., "submitButton").
     * @param pageName    The name of the Page Object class (e.g., "LoginPage").
     * @return The found WebElement.
     */
    public WebElement acquireElementFromPage(String elementName, String pageName){
        log.info("Acquiring element " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY," from the ") +
                highlighted(BLUE, pageName)
        );
        pageName = firstLetterDeCapped(pageName);
        elementName = contextCheck(elementName);
        return reflections.getElementFromPage(elementName, pageName);
    }

    /**
     * Acquires a list of {@link WebElement}s from a specific page object instance.
     *
     * @param elementName The name of the list field in the Page Object class.
     * @param pageName    The name of the Page Object class.
     * @return The list of found WebElements.
     */
    public List<WebElement> acquireElementsFromPage(String elementName, String pageName){
        log.info("Acquiring element " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY," from the ") +
                highlighted(BLUE, pageName)
        );
        pageName = firstLetterDeCapped(pageName);
        elementName = contextCheck(elementName);
        return reflections.getElementsFromPage(elementName, pageName);
    }

    /**
     * Acquires a specific element from a list of WebElements defined on a page.
     * <p>
     * This is useful for selecting a specific item from a menu or list based on its text content.
     * </p>
     *
     * @param elementName The text content to search for within the list (or context key resolving to text).
     * @param listName    The name of the List&lt;WebElement&gt; field on the page.
     * @param pageName    The name of the Page Object class.
     * @return The matching WebElement.
     */
    public WebElement acquireListedElementFromPage(
            String elementName,
            String listName,
            String pageName
    ) {
        log.info("Acquiring listed element named " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY," selected from ") +
                highlighted(BLUE, listName) +
                highlighted(GRAY," on  ") +
                highlighted(BLUE, pageName)
        );

        pageName = firstLetterDeCapped(pageName);
        elementName = contextCheck(elementName);
        List<WebElement> elements = reflections.getElementsFromPage(
                listName,
                pageName
        );
        return acquireNamedElementAmongst(elements, elementName);
    }

    /**
     * Finds an element within a list on a page by matching a specific attribute value.
     *
     * @param attributeName  The HTML attribute to inspect (e.g., "href", "id", "class").
     * @param attributeValue The value expected in the attribute.
     * @param listName       The name of the List&lt;WebElement&gt; field on the page.
     * @param pageName       The name of the Page Object class.
     * @return The matching WebElement.
     */
    public WebElement acquireListedElementByAttribute(
            String attributeName,
            String attributeValue,
            String listName,
            String pageName
    ) {
        log.info("Acquiring element by " +
                highlighted(BLUE, attributeName) +
                highlighted(GRAY," attribute selected from ") +
                highlighted(BLUE, listName) +
                highlighted(GRAY, " list on the ") +
                highlighted(BLUE, pageName)
        );
        attributeName = contextCheck(attributeName);
        pageName = firstLetterDeCapped(pageName);
        List<WebElement> elements = reflections.getElementsFromPage(
                listName,
                firstLetterDeCapped(pageName)
        );
        return acquireElementUsingAttributeAmongst(elements, attributeName, attributeValue);
    }

    /**
     * Converts a list of form input specifications (usually from a Cucumber DataTable) into a list of ElementBundles.
     * <p>
     * This iterates over the provided map, looks up the "Input Element" field in the Page Object,
     * and bundles it with the "Input" data for processing by {@code bundleInteraction}.
     * </p>
     *
     * @param signForms A List of Maps (DataTable) containing keys "Input Element" and "Input".
     * @param pageName  The name of the Page Object class.
     * @return A list of {@link ElementBundle}s ready for interaction.
     */
    public List<ElementBundle<String>> acquireElementList(List<Map<String, String>> signForms, String pageName) {
        log.info("Acquiring element list from " + highlighted(BLUE, pageName));
        pageName = firstLetterDeCapped(pageName);
        List<ElementBundle<String>> bundles = new ArrayList<>();
        for (Map<String, String> form : signForms) {
            String inputName = form.get("Input Element");
            String input = contextCheck(form.get("Input"));
            WebElement element = reflections.getElementFromPage(inputName, pageName);
            ElementBundle<String> bundle = new ElementBundle<>(
                    element,
                    inputName,
                    getElementDriverType(element).name(),
                    input
            );
            bundles.add(bundle);
        }
        return bundles;
    }

    /**
     * Creates an {@link ElementBundle} for a single element on a page.
     *
     * @param elementFieldName The name of the field in the Page Object.
     * @param pageName         The name of the Page Object class.
     * @param specifications   A map of metadata regarding the element.
     * @return A bundled object containing the element, its name, and its driver type.
     */
    public ElementBundle<Map<String, String>> acquireElementBundleFromPage(
            String elementFieldName,
            String pageName,
            Map<String, String> specifications
    ){
        log.info("Acquiring element bundle from " + highlighted(BLUE, pageName));
        WebElement element = acquireElementFromPage(elementFieldName, pageName);
        return new ElementBundle<>(element, elementFieldName, getElementDriverType(element).name(), specifications);
    }

    /**
     * Creates a list of {@link ElementBundle}s based on a list of specifications.
     *
     * @param pageName       The name of the Page Object class.
     * @param specifications A list of maps, where each map must contain an "Element Name" key.
     * @return A list of bundles.
     */
    public List<ElementBundle<Map<String, String>>> acquireElementBundlesFromPage(
            String pageName,
            List<Map<String, String>> specifications
    ){
        log.info("Acquiring element bundle from " + highlighted(BLUE, pageName));
        List<ElementBundle<Map<String, String>>> bundles = new ArrayList<>();
        for (Map<String, String> specification:specifications) {
            bundles.add(acquireElementBundleFromPage(specification.get("Element Name"), pageName, specification));
        }
        return bundles;
    }
}
