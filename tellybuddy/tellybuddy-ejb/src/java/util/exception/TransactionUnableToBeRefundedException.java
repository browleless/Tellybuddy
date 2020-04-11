/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.exception;

/**
 *
 * @author markt
 */
public class TransactionUnableToBeRefundedException extends Exception {

    /**
     * Creates a new instance of
     * <code>TransactionUnableToBeRefundedException</code> without detail
     * message.
     */
    public TransactionUnableToBeRefundedException() {
    }

    /**
     * Constructs an instance of
     * <code>TransactionUnableToBeRefundedException</code> with the specified
     * detail message.
     *
     * @param msg the detail message.
     */
    public TransactionUnableToBeRefundedException(String msg) {
        super(msg);
    }
}
