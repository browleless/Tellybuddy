package ws.datamodel;

import entity.Customer;
import java.util.List;

/**
 *
 * @author tjle2
 */
public class RetrieveQuizUnattemptedFamilyMembersRsp {
    
    private List<Customer> familyGroupMembers;

    public RetrieveQuizUnattemptedFamilyMembersRsp() {
    }

    public RetrieveQuizUnattemptedFamilyMembersRsp(List<Customer> familyGroupMembers) {
        this.familyGroupMembers = familyGroupMembers;
    }

    public List<Customer> getFamilyGroupMembers() {
        return familyGroupMembers;
    }

    public void setFamilyGroupMembers(List<Customer> familyGroupMembers) {
        this.familyGroupMembers = familyGroupMembers;
    }
}
