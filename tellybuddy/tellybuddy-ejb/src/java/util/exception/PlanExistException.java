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
public class PlanExistException extends Exception {

    public PlanExistException() {
    }

    public PlanExistException(String msg) {
        super(msg);
    }
}
