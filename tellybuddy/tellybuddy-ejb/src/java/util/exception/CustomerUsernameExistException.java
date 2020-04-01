package util.exception;

/**
 *
 * @author tjle2
 */
public class CustomerUsernameExistException extends Exception {

    public CustomerUsernameExistException() {
    }

    public CustomerUsernameExistException(String message) {
        super(message);
    }
}
