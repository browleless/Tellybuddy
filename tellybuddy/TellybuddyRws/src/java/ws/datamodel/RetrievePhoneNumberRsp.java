package ws.datamodel;

import entity.PhoneNumber;

/**
 *
 * @author tjle2
 */
public class RetrievePhoneNumberRsp {

    private PhoneNumber phoneNumber;

    public RetrievePhoneNumberRsp() {
    }

    public RetrievePhoneNumberRsp(PhoneNumber phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public PhoneNumber getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(PhoneNumber phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
