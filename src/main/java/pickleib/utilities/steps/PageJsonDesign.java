package pickleib.utilities.steps;

import com.google.gson.JsonObject;
import pickleib.utilities.element.acquisition.design.PageObjectJson;
import pickleib.utilities.element.interactions.InteractionBase;
import pickleib.utilities.interfaces.repository.PageRepository;
import utils.FileUtilities;

/**
 * Utility that provides step utilities based on Page Objects defined as JSON files.
 *
 * <p>This class is part of the interaction layer between Selenium (or any WebDriver-based)
 * automation and a JSON representation of page objects. It implements {@link PageRepository}
 * so it can be used in step definitions, offering convenient access to the underlying
 * {@link PageObjectJson} repository.</p>
 *
 * <h2>Key responsibilities</h2>
 * <ul>
 *   <li>Loads a {@code PageObjectJson} instance from either an already-parsed JSON
 *       object or from a directory containing a JSON file.</li>
 *   <li>Exposes the repository via {@link #getElementRepository()} for use by step classes.</li>
 *   <li>Optionally activates platform-specific drivers (web, iOS, Android, Windows)
 *       based on configuration flags passed to its constructors.</li>
 * </ul>
 *
 * @author Umut Ay Bora
 * @since 2.0.6
 */
public class PageJsonDesign extends InteractionBase implements PageRepository {

    PageObjectJson objectRepository;

    /**
     * Creates a new {@code PageJsonStepUtilities} using an already parsed {@link JsonObject}.
     *
     * @param pageJson the parsed JSON representation of the page objects,
     *                 must not be {@code null}
     */
    public PageJsonDesign(JsonObject pageJson) {
        this(pageJson, true, true);
    }

    /**
     * Creates a new {@code PageJsonStepUtilities} by loading the JSON from a directory.
     *
     * <p>The method delegates to {@link FileUtilities.Json#parseJsonFile(String)} which
     * reads and parses the file named {@code page.json} (or any other supported name)
     * located in the supplied directory.</p>
     *
     * @param pageJsonDirectory path to a directory that contains the JSON file representing the page objects.
     *                        The directory must exist and contain a valid JSON file.
     * @throws IllegalArgumentException if {@code pageJsonDirectory} is {@code null}
     *                                  or does not point to an existing directory
     */
    public PageJsonDesign(String pageJsonDirectory) {
        this(FileUtilities.Json.parseJsonFile(pageJsonDirectory));
    }

    /**
     * Fully‑qualified constructor that allows explicit control over which drivers are active.
     *
     * @param pageJson          the parsed JSON representation of the page objects,
     *                          must not be {@code null}
     * @param platformDriverActive  flag indicating whether a driver for platforms other than web
     *                              (e.g., iOS, Android) should be initialized
     * @param webDriverActive       flag indicating whether the web driver should be initialized
     */
    public PageJsonDesign(
            JsonObject pageJson,
            boolean platformDriverActive,
            boolean webDriverActive) {
        super(platformDriverActive, webDriverActive);
        this.objectRepository = new PageObjectJson(pageJson);
    }

    /**
     * Returns the {@link PageObjectJson} repository associated with this utility.
     *
     * @return an immutable view of the internal page‑object repository;
     *         never {@code null}
     */
    @Override
    public PageObjectJson getElementRepository() {
        return objectRepository;
    }
}
