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
public class CreateNewSubscriptionException extends Exception {

    /**
     * Creates a new instance of <code>CreateNewSubscriptionException</code>
     * without detail message.
     */
    public CreateNewSubscriptionException() {
    }

    /**
     * Constructs an instance of <code>CreateNewSubscriptionException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public CreateNewSubscriptionException(String msg) {
        super(msg);
    }
}
