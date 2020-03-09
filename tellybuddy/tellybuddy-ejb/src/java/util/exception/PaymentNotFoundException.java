package util.exception;

/**
 *
 * @author tjle2
 */
public class PaymentNotFoundException extends Exception {

    public PaymentNotFoundException() {
    }

    public PaymentNotFoundException(String message) {
        super(message);
    }
}
