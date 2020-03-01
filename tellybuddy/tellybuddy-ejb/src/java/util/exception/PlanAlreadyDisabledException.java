/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.exception;

/**
 *
 * @author ngjin
 */
public class PlanAlreadyDisabledException extends Exception {

    public PlanAlreadyDisabledException() {
    }

    public PlanAlreadyDisabledException(String msg) {
        super(msg);
    }
}
