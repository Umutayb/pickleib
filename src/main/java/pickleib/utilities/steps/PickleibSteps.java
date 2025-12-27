package pickleib.utilities.steps;


import com.google.gson.JsonObject;
import pickleib.utilities.element.interactions.InteractionBase;
import pickleib.utilities.interfaces.repository.ElementRepository;
import pickleib.utilities.interfaces.repository.PageObjectRepository;
import pickleib.utilities.interfaces.repository.PageRepository;
import pickleib.utilities.steps.design.PageJsonDesign;
import pickleib.utilities.steps.design.PageObjectDesign;
import utils.FileUtilities;

/**
 * The foundational abstract base class for Cucumber Step Definitions in the Pickleib framework.
 * <p>
 * This class acts as the bridge between the **Action Layer** (inherited from {@link InteractionBase})
 * and the **Repository Layer** (managed by {@link ElementRepository}).
 * </p>
 * <p>
 * It supports two distinct architectural patterns for element management, determined by which constructor is called:
 * <ol>
 * <li><b>JSON Design (Low-Code):</b> Elements are defined in a JSON file.</li>
 * <li><b>Page Object Design (Classic):</b> Elements are defined in Java classes using {@code @FindBy}.</li>
 * </ol>
 *
 *
 */
public abstract class PickleibSteps extends InteractionBase implements PageRepository {

    /**
     * The strategy used to locate elements (either via JSON parsing or Java Reflection).
     */
    ElementRepository elementRepository;

    /**
     * Constructor for the <b>JSON-based (Low-Code)</b> design pattern.
     * <p>
     * Initializes the step definition to look up elements dynamically from a {@code page-repository.json} file.
     * This removes the need for creating Java Page Object classes for every page.
     * </p>
     *
     * @param pageRepositoryJson     The parsed JSON object containing page and selector definitions.
     * @param initialiseBrowser      If {@code true}, initializes the Web interaction capabilities.
     * @param initialiseAppiumDriver If {@code true}, initializes the Mobile/Desktop interaction capabilities.
     */
    public PickleibSteps(JsonObject pageRepositoryJson, boolean initialiseBrowser, boolean initialiseAppiumDriver){
        this.elementRepository = new PageJsonDesign(
                pageRepositoryJson,
                initialiseAppiumDriver,
                initialiseBrowser
        ).getElementRepository();
    }

    /**
     * Constructor for the <b>JSON-based (Low-Code)</b> design pattern.
     * <p>
     * Initializes the step definition to look up elements dynamically from a {@code page-repository.json} file.
     * This removes the need for creating Java Page Object classes for every page.
     * </p>
     *
     * @param pageRepositoryJsonDir  The file path to the JSON file containing page and selector definitions.
     * @param initialiseBrowser      If {@code true}, initializes the Web interaction capabilities.
     * @param initialiseAppiumDriver If {@code true}, initializes the Mobile/Desktop interaction capabilities.
     */
    public PickleibSteps(String pageRepositoryJsonDir, boolean initialiseBrowser, boolean initialiseAppiumDriver){
        this(FileUtilities.Json.parseJsonFile(pageRepositoryJsonDir), initialiseAppiumDriver, initialiseBrowser);
    }

    /**
     * Constructor for the <b>JSON-based (Low-Code)</b> design pattern.
     * <p>
     * Initializes the step definition to look up elements dynamically from a {@code page-repository.json} file.
     * This removes the need for creating Java Page Object classes for every page.
     * </p>
     *
     * @param pageRepositoryJsonDir  The file path to the JSON file containing page and selector definitions.
     */
    public PickleibSteps(String pageRepositoryJsonDir){
        this(FileUtilities.Json.parseJsonFile(pageRepositoryJsonDir), true, true);
    }

    /**
     * Constructor for the <b>Classic Page Object Model (POM)</b> design pattern.
     * <p>
     * Initializes the step definition to use Java Reflection to find elements within a strongly-typed
     * {@link PageObjectRepository} class.
     * </p>
     *
     * @param objectRepository       The class definition of the repository containing Page Object instances (e.g., {@code ObjectRepository.class}).
     * @param initialiseBrowser      If {@code true}, initializes the Web interaction capabilities.
     * @param initialiseAppiumDriver If {@code true}, initializes the Mobile/Desktop interaction capabilities.
     * @param <ObjectRepository>                    The type of the ObjectRepository class.
     */
    public <ObjectRepository extends PageObjectRepository> PickleibSteps(
            Class<ObjectRepository> objectRepository,
            boolean initialiseBrowser,
            boolean initialiseAppiumDriver
    ){
        this.elementRepository = new PageObjectDesign<>(
                objectRepository,
                initialiseAppiumDriver,
                initialiseBrowser
        ).getElementRepository();
    }

    /**
     * Constructor for the <b>Classic Page Object Model (POM)</b> design pattern.
     * <p>
     * Initializes the step definition to use Java Reflection to find elements within a strongly-typed
     * {@link PageObjectRepository} class.
     * </p>
     *
     * @param objectRepository       The class definition of the repository containing Page Object instances (e.g., {@code ObjectRepository.class}).
     * @param <ObjectRepository>                    The type of the ObjectRepository class.
     */
    public <ObjectRepository extends PageObjectRepository> PickleibSteps(
            Class<ObjectRepository> objectRepository
    ){
        this.elementRepository = new PageObjectDesign<>(
                objectRepository,
                true,
                true
        ).getElementRepository();
    }

    /**
     * Retrieves the active element repository.
     * <p>
     * This method is used by the framework to locate elements during step execution, regardless of
     * whether they are sourced from JSON or Java classes.
     * </p>
     *
     * @return The active {@link ElementRepository} implementation.
     */
    @Override
    public ElementRepository getElementRepository() {
        return elementRepository;
    }
}
