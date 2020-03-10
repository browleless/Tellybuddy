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
public class PhoneNumberInUseException extends Exception {

    /**
     * Creates a new instance of <code>PhoneNumberInUseException</code> without
     * detail message.
     */
    public PhoneNumberInUseException() {
    }

    /**
     * Constructs an instance of <code>PhoneNumberInUseException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public PhoneNumberInUseException(String msg) {
        super(msg);
    }
}
