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
public class TransactionAlreadyVoidedRefundedException extends Exception {

    /**
     * Creates a new instance of
     * <code>TransactionAlreadyVoidedRefundedException</code> without detail
     * message.
     */
    public TransactionAlreadyVoidedRefundedException() {
    }

    /**
     * Constructs an instance of
     * <code>TransactionAlreadyVoidedRefundedException</code> with the specified
     * detail message.
     *
     * @param msg the detail message.
     */
    public TransactionAlreadyVoidedRefundedException(String msg) {
        super(msg);
    }
}
