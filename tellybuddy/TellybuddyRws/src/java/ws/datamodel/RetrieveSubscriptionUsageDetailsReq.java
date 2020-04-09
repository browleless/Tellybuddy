package ws.datamodel;

import entity.Subscription;

/**
 *
 * @author tjle2
 */
public class RetrieveSubscriptionUsageDetailsReq {
    
    private String username;
    private String password;
    private Subscription subscription;

    public RetrieveSubscriptionUsageDetailsReq() {
    }

    public RetrieveSubscriptionUsageDetailsReq(String username, String password, Subscription subscription) {
        this.username = username;
        this.password = password;
        this.subscription = subscription;
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
}
