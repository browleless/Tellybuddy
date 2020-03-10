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
public class AnnouncementNotFoundException extends Exception {

    public AnnouncementNotFoundException() {
    }

    public AnnouncementNotFoundException(String msg) {
        super(msg);
    }
}
