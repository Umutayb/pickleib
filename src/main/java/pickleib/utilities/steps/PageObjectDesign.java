package pickleib.utilities.steps;

import pickleib.utilities.element.acquisition.design.PageObjectModel;
import pickleib.utilities.element.interactions.InteractionBase;
import pickleib.utilities.interfaces.repository.PageRepository;
import pickleib.utilities.interfaces.repository.ElementRepository;
import pickleib.utilities.interfaces.repository.PageObjectRepository;

/**
 * A utility class that provides common methods and interactions for web and mobile steps in the context of Pickleib.
 *
 * <p>
 * This class houses the utilities and common functionality for both web and mobile platforms,
 * such as acquiring page objects, getting element interactions, and reflections based on the specific platform type.
 * </p>
 *
 * @param <ObjectRepository> A type extending {@link ElementRepository} which provides the structure and access
 *                           to the underlying page elements and components.
 *
 * @author Umut Ay Bora
 * @since 2.0.6
 */
public class PageObjectDesign<ObjectRepository extends PageObjectRepository>
        extends InteractionBase
        implements PageRepository
{

    PageObjectModel<ObjectRepository> objectRepository;

    /**
     * Constructs an instance of the PageObjectStepUtilities class with the specific object repository.
     *
     * @param objectRepositoryClass The class of the object repository which will be used to initialize
     *                              the page object model, element interactions, and reflections.
     */
    public PageObjectDesign(
            Class<ObjectRepository> objectRepositoryClass,
            boolean mobileDriverActive,
            boolean webDriverActive) {
        super(mobileDriverActive, webDriverActive);
        objectRepository = new PageObjectModel<>(objectRepositoryClass);
    }

    /**
     * Constructs an instance of the PageObjectStepUtilities class with the specific object repository.
     *
     * @param objectRepositoryClass The class of the object repository which will be used to initialize
     *                              the page object model, element interactions, and reflections.
     */
    public PageObjectDesign(Class<ObjectRepository> objectRepositoryClass) {
        super();
        objectRepository = new PageObjectModel<>(objectRepositoryClass);
    }

    @Override
    public PageObjectModel<ObjectRepository> getElementRepository() {
        return objectRepository;
    }
}
