/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.exception;

/**
 *
 * @author kaikai
 */
public class ProductItemNotFoundException extends Exception {

    /**
     * Creates a new instance of <code>ProductItemNotFoundException</code>
     * without detail message.
     */
    public ProductItemNotFoundException() {
    }

    /**
     * Constructs an instance of <code>ProductItemNotFoundException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public ProductItemNotFoundException(String msg) {
        super(msg);
    }
}
