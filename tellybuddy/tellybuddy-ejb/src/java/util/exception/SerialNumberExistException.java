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
public class SerialNumberExistException extends Exception {

    /**
     * Creates a new instance of <code>SerialNumberExistException</code> without
     * detail message.
     */
    public SerialNumberExistException() {
    }

    /**
     * Constructs an instance of <code>SerialNumberExistException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public SerialNumberExistException(String msg) {
        super(msg);
    }
}
