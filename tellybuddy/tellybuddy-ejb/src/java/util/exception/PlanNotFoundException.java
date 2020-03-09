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
public class PlanNotFoundException extends Exception {

    public PlanNotFoundException() {
    }

    public PlanNotFoundException(String msg) {
        super(msg);
    }
}
