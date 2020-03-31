package ws.datamodel;

/**
 *
 * @author tjle2
 */
public class CreateFamilyGroupRsp {

    private Long familyGroupId;

    public CreateFamilyGroupRsp() {
    }

    public CreateFamilyGroupRsp(Long familyGroupId) {
        this.familyGroupId = familyGroupId;
    }

    public Long getFamilyGroupId() {
        return familyGroupId;
    }

    public void setFamilyGroupId(Long familyGroupId) {
        this.familyGroupId = familyGroupId;
    }
}
