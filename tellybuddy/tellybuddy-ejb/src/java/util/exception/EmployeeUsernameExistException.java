package util.exception;

/**
 *
 * @author tjle2
 */
public class EmployeeUsernameExistException extends Exception {

    public EmployeeUsernameExistException() {
    }

    public EmployeeUsernameExistException(String message) {
        super(message);
    }
}
