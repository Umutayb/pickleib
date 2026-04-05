package pickleib.exceptions;

/** Thrown when a Pickleib element or text verification fails. */
public class PickleibVerificationException extends PickleibException{

    /** @param errorMessage the verification failure message */
    public PickleibVerificationException(String errorMessage) {
        super(errorMessage);
    }

    /** @param errorMessage the cause exception */
    public PickleibVerificationException(Exception errorMessage) {
        super(errorMessage);
    }
}
