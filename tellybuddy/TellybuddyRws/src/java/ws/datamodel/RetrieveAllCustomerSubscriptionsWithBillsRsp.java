package ws.datamodel;

import entity.Subscription;
import java.util.List;

/**
 *
 * @author tjle2
 */
public class RetrieveAllCustomerSubscriptionsWithBillsRsp {
    
    private List<Subscription> subscriptions;

    public RetrieveAllCustomerSubscriptionsWithBillsRsp() {
    }

    public RetrieveAllCustomerSubscriptionsWithBillsRsp(List<Subscription> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public List<Subscription> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(List<Subscription> subscriptions) {
        this.subscriptions = subscriptions;
    }
}
