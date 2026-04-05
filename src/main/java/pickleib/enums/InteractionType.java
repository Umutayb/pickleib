package pickleib.enums;

/**
 * An enum representing the different types of interactions that can be performed on a web element.
 * <p>
 * The available interaction types are click, fill, center, and verify.
 */
public enum InteractionType {
    /** Click on the element. */
    click,
    /** Fill the element with input. */
    fill,
    /** Scroll the element to center. */
    center,
    /** Verify the element state. */
    verify
}
