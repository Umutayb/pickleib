package pickleib.utilities.interfaces.repository;


/**
 * Interface that exposes the {@link ElementRepository} responsible for acquiring page-level
 * elements from Page Object Models or JSON structures.
 *
 * <p>This contract is primarily used by step‑definition and interaction classes so they can
 * retrieve {@code WebElement}s or bundles of elements without coupling to a concrete
 * implementation. </p>
 *
 * <p>The {@link #getElementRepository()} method must return an instance that implements the
 * element‑acquisition operations described in {@link ElementRepository}.</p>
 *
 * @author  Umut Ay Bora
 * @since   2.0.6
 */
public interface PageRepository {

    ElementRepository getElementRepository();
}
