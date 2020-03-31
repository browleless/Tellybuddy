package ws.datamodel;

import entity.PhoneNumber;
import java.util.List;

/**
 *
 * @author tjle2
 */
public class RetrieveAllAvailablePhoneNumbersRsp {

    private List<PhoneNumber> phoneNumbers;

    public RetrieveAllAvailablePhoneNumbersRsp() {
    }

    public RetrieveAllAvailablePhoneNumbersRsp(List<PhoneNumber> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public List<PhoneNumber> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(List<PhoneNumber> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }
}
