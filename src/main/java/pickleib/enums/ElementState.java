package pickleib.enums;

import lombok.Getter;

/**
 * Expected states for element verification.
 */
@Getter
public enum ElementState {
    /** Element is enabled for interaction. */
    enabled,
    /** Element is visible on the page. */
    displayed,
    /** Element is selected (e.g. checkbox). */
    selected,
    /** Element is disabled. */
    disabled,
    /** Element is not selected. */
    unselected,
    /** Element is not present in the DOM. */
    absent
}
