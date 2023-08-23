package pickleib.exceptions;

public class PickleibVerificationException extends PickleibException{

    public PickleibVerificationException(String errorMessage) {
        super(errorMessage);
    }

    public PickleibVerificationException(Exception errorMessage) {
        super(errorMessage);
    }
}
