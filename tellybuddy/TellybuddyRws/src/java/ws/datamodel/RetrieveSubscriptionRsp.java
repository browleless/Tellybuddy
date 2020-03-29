package ws.datamodel;

import entity.Subscription;

/**
 *
 * @author tjle2
 */
public class RetrieveSubscriptionRsp {
    
    private Subscription subscription;

    public RetrieveSubscriptionRsp() {
    }

    public RetrieveSubscriptionRsp(Subscription subscription) {
        this.subscription = subscription;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }
}
