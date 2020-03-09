package util.exception;

/**
 *
 * @author tjle2
 */
public class BillAlreadyPaidException extends Exception {

    public BillAlreadyPaidException() {
    }

    public BillAlreadyPaidException(String message) {
        super(message);
    }
}
