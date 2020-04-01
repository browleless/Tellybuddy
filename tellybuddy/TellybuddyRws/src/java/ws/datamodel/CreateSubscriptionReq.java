package ws.datamodel;

import entity.Subscription;

/**
 *
 * @author tjle2
 */
public class CreateSubscriptionReq {

    private String username;
    private String password;
    private Subscription subscription;
    private Long customerId;
    private Long phoneNumberId;
    private Long planId;

    public CreateSubscriptionReq() {
    }

    public CreateSubscriptionReq(String username, String password, Subscription subscription, Long customerId, Long phoneNumberId, Long planId) {
        this.username = username;
        this.password = password;
        this.subscription = subscription;
        this.customerId = customerId;
        this.phoneNumberId = phoneNumberId;
        this.planId = planId;
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

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getPhoneNumberId() {
        return phoneNumberId;
    }

    public void setPhoneNumberId(Long phoneNumberId) {
        this.phoneNumberId = phoneNumberId;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }
}
