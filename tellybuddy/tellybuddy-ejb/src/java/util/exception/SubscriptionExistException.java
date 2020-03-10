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
public class SubscriptionExistException extends Exception {

    /**
     * Creates a new instance of <code>SubscriptionExistException</code> without
     * detail message.
     */
    public SubscriptionExistException() {
    }

    /**
     * Constructs an instance of <code>SubscriptionExistException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public SubscriptionExistException(String msg) {
        super(msg);
    }
}
