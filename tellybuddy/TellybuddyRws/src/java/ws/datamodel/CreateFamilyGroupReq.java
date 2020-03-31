package ws.datamodel;

import entity.Customer;
import entity.FamilyGroup;

/**
 *
 * @author tjle2
 */
public class CreateFamilyGroupReq {

    private String username;
    private String password;
    private FamilyGroup familyGroup;
    private Customer customer;

    public CreateFamilyGroupReq() {
    }

    public CreateFamilyGroupReq(String username, String password, FamilyGroup familyGroup, Customer customer) {
        this.username = username;
        this.password = password;
        this.familyGroup = familyGroup;
        this.customer = customer;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public FamilyGroup getFamilyGroup() {
        return familyGroup;
    }

    public void setFamilyGroup(FamilyGroup familyGroup) {
        this.familyGroup = familyGroup;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
