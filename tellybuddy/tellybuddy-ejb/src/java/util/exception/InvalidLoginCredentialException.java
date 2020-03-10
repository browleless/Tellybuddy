package util.exception;

/**
 *
 * @author tjle2
 */
public class InvalidLoginCredentialException extends Exception {

    public InvalidLoginCredentialException() {
    }

    public InvalidLoginCredentialException(String message) {
        super(message);
    }
}