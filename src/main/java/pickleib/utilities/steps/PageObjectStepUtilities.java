package pickleib.utilities.steps;

import pickleib.utilities.element.acquisition.ElementAcquisition;
import pickleib.utilities.element.interactions.InteractionBase;
import pickleib.utilities.interfaces.repository.PageRepository;

/**
 * A utility class that provides common methods and interactions for web and mobile steps in the context of Pickleib.
 *
 * <p>
 * This class houses the utilities and common functionality for both web and mobile platforms,
 * such as acquiring page objects, getting element interactions, and reflections based on the specific platform type.
 * </p>
 *
 * @param <ObjectRepository> A type extending {@link PageRepository} which provides the structure and access
 *                           to the underlying page elements and components.
 *
 * @author Umut Ay Bora
 * @since 1.8.7
 */
public class PageObjectStepUtilities<ObjectRepository extends PageRepository> extends InteractionBase {

    public ElementAcquisition.PageObjectModel<ObjectRepository> objectRepository;
    public ElementAcquisition.Reflections<ObjectRepository> pageObjectReflections;

    /**
     * Constructs an instance of the PageObjectStepUtilities class with the specific object repository.
     *
     * @param objectRepositoryClass The class of the object repository which will be used to initialize
     *                              the page object model, element interactions, and reflections.
     */
    public PageObjectStepUtilities(
            Class<ObjectRepository> objectRepositoryClass,
            boolean mobileDriverActive,
            boolean webDriverActive) {
        super(mobileDriverActive, webDriverActive);
        objectRepository = new ElementAcquisition.PageObjectModel<>(objectRepositoryClass);
        pageObjectReflections = new ElementAcquisition.Reflections<>(objectRepositoryClass);
    }

    /**
     * Constructs an instance of the PageObjectStepUtilities class with the specific object repository.
     *
     * @param objectRepositoryClass The class of the object repository which will be used to initialize
     *                              the page object model, element interactions, and reflections.
     */
    public PageObjectStepUtilities(Class<ObjectRepository> objectRepositoryClass) {
        super();
        objectRepository = new ElementAcquisition.PageObjectModel<>(objectRepositoryClass);
        pageObjectReflections = new ElementAcquisition.Reflections<>(objectRepositoryClass);
    }
}
