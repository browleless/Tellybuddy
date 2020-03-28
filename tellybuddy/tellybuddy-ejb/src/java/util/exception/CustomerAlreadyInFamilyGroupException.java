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
public class CustomerAlreadyInFamilyGroupException extends Exception {

    /**
     * Creates a new instance of
     * <code>CustomerAlreadyInFamilyGroupException</code> without detail
     * message.
     */
    public CustomerAlreadyInFamilyGroupException() {
    }

    /**
     * Constructs an instance of
     * <code>CustomerAlreadyInFamilyGroupException</code> with the specified
     * detail message.
     *
     * @param msg the detail message.
     */
    public CustomerAlreadyInFamilyGroupException(String msg) {
        super(msg);
    }
}
