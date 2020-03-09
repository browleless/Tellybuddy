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
public class DiscountCodeAlreadyExpiredException extends Exception {

    public DiscountCodeAlreadyExpiredException() {
    }

    public DiscountCodeAlreadyExpiredException(String msg) {
        super(msg);
    }
}
