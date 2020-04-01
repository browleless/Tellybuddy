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
public class CustomerNotYetApproved extends Exception {

    /**
     * Creates a new instance of <code>CustomerNotYetApproved</code> without
     * detail message.
     */
    public CustomerNotYetApproved() {
    }

    /**
     * Constructs an instance of <code>CustomerNotYetApproved</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public CustomerNotYetApproved(String msg) {
        super(msg);
    }
}
