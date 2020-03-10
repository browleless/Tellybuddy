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
public class FamilyGroupReachedLimitOf5MembersException extends Exception {

    public FamilyGroupReachedLimitOf5MembersException() {
    }

    public FamilyGroupReachedLimitOf5MembersException(String msg) {
        super(msg);
    }
}
