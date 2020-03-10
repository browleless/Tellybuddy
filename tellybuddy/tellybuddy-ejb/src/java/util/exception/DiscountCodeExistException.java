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
public class DiscountCodeExistException extends Exception {

    public DiscountCodeExistException() {
    }

    public DiscountCodeExistException(String msg) {
        super(msg);
    }
}
