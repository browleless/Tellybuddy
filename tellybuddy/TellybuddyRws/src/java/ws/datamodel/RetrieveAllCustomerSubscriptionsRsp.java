package ws.datamodel;

import entity.Subscription;
import java.util.List;

/**
 *
 * @author tjle2
 */
public class RetrieveAllCustomerSubscriptionsRsp {
    
    private List<Subscription> subscriptions;

    public RetrieveAllCustomerSubscriptionsRsp() {
    }

    public RetrieveAllCustomerSubscriptionsRsp(List<Subscription> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public List<Subscription> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(List<Subscription> subscriptions) {
        this.subscriptions = subscriptions;
    }
}
