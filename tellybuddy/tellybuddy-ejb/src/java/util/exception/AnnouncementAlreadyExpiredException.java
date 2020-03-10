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
public class AnnouncementAlreadyExpiredException extends Exception {

    public AnnouncementAlreadyExpiredException() {
    }

    public AnnouncementAlreadyExpiredException(String msg) {
        super(msg);
    }
}
