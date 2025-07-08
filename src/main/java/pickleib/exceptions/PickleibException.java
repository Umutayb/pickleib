package pickleib.exceptions;

public class PickleibException extends RuntimeException {

    public PickleibException(String errorMessage) {super(errorMessage);}
    public PickleibException(Exception errorMessage) {super(errorMessage);}

}
