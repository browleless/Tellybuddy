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
public class ProductItemExistException extends Exception {

    /**
     * Creates a new instance of <code>ProductItemExistException</code> without
     * detail message.
     */
    public ProductItemExistException() {
    }

    /**
     * Constructs an instance of <code>ProductItemExistException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public ProductItemExistException(String msg) {
        super(msg);
    }
}
