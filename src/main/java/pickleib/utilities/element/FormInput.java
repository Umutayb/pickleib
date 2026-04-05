package pickleib.utilities.element;

/**
 * A form input pair mapping an element name to its input value.
 *
 * @param element the element name
 * @param input   the input value
 */
public record FormInput(String element, String input) {}
