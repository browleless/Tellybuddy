/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.datamodel;

import entity.Subscription;
import java.util.List;

/**
 *
 * @author kaikai
 */
public class RetrieveActiveSubscriptionUnderCustomerRsp {
    
    List<Subscription> activeSubscriptions;

    public RetrieveActiveSubscriptionUnderCustomerRsp() {
    }

    public RetrieveActiveSubscriptionUnderCustomerRsp(List<Subscription> activeSubscriptions) {
        this.activeSubscriptions = activeSubscriptions;
    }

    public List<Subscription> getActiveSubscriptions() {
        return activeSubscriptions;
    }

    public void setActiveSubscriptions(List<Subscription> activeSubscriptions) {
        this.activeSubscriptions = activeSubscriptions;
    }

    
    
}
