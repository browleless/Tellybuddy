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
public class SubscriptionNotFoundException extends Exception {

    /**
     * Creates a new instance of <code>SubscriptionNotFoundException</code>
     * without detail message.
     */
    public SubscriptionNotFoundException() {
    }

    /**
     * Constructs an instance of <code>SubscriptionNotFoundException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public SubscriptionNotFoundException(String msg) {
        super(msg);
    }
}
