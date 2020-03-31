package ws.datamodel;

/**
 *
 * @author tjle2
 */
public class CreateSubscriptionRsp {

    private Long subscriptionId;

    public CreateSubscriptionRsp() {
    }

    public CreateSubscriptionRsp(Long subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public Long getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(Long subscriptionId) {
        this.subscriptionId = subscriptionId;
    }
}
