package ws.datamodel;

import entity.FamilyGroup;

/**
 *
 * @author tjle2
 */
public class RetrieveCustomerFamilyGroupRsp {

    private FamilyGroup familyGroup;

    public RetrieveCustomerFamilyGroupRsp() {
    }

    public RetrieveCustomerFamilyGroupRsp(FamilyGroup familyGroup) {
        this.familyGroup = familyGroup;
    }

    public FamilyGroup getFamilyGroup() {
        return familyGroup;
    }

    public void setFamilyGroup(FamilyGroup familyGroup) {
        this.familyGroup = familyGroup;
    }
}
