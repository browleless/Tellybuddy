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
public class PhoneNumberNotFoundException extends Exception {

    /**
     * Creates a new instance of <code>PhoneNumberNotFoundException</code>
     * without detail message.
     */
    public PhoneNumberNotFoundException() {
    }

    /**
     * Constructs an instance of <code>PhoneNumberNotFoundException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public PhoneNumberNotFoundException(String msg) {
        super(msg);
    }
}
