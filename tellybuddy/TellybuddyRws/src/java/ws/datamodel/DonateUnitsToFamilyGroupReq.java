package ws.datamodel;

import entity.Customer;
import entity.FamilyGroup;
import entity.Subscription;

/**
 *
 * @author tjle2
 */
public class DonateUnitsToFamilyGroupReq {

    private String username;
    private String password;
    private Integer dataUnits;
    private Integer smsUnits;
    private Integer talktimeUnits;
    private Subscription subscription;
    private Customer customer;
    private FamilyGroup familyGroup;

    public DonateUnitsToFamilyGroupReq() {
    }

    public DonateUnitsToFamilyGroupReq(String username, String password, Integer dataUnits, Integer smsUnits, Integer talktimeUnits, Subscription subscription, Customer customer, FamilyGroup familyGroup) {
        this.username = username;
        this.password = password;
        this.dataUnits = dataUnits;
        this.smsUnits = smsUnits;
        this.talktimeUnits = talktimeUnits;
        this.subscription = subscription;
        this.customer = customer;
        this.familyGroup = familyGroup;
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

    public Integer getDataUnits() {
        return dataUnits;
    }

    public void setDataUnits(Integer dataUnits) {
        this.dataUnits = dataUnits;
    }

    public Integer getSmsUnits() {
        return smsUnits;
    }

    public void setSmsUnits(Integer smsUnits) {
        this.smsUnits = smsUnits;
    }

    public Integer getTalktimeUnits() {
        return talktimeUnits;
    }

    public void setTalktimeUnits(Integer talktimeUnits) {
        this.talktimeUnits = talktimeUnits;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public FamilyGroup getFamilyGroup() {
        return familyGroup;
    }

    public void setFamilyGroup(FamilyGroup familyGroup) {
        this.familyGroup = familyGroup;
    }
}
