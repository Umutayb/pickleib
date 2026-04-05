package pickleib.exceptions;

/** Runtime exception thrown by Pickleib when an interaction or verification fails. */
public class PickleibException extends RuntimeException {

    /** @param errorMessage the error message */
    public PickleibException(String errorMessage) {super(errorMessage);}
    /** @param errorMessage the cause exception */
    public PickleibException(Exception errorMessage) {super(errorMessage);}

}
