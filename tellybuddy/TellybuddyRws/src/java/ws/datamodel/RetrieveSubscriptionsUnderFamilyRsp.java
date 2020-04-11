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
public class RetrieveSubscriptionsUnderFamilyRsp {
   
    private List<Subscription> subscriptions;

    public RetrieveSubscriptionsUnderFamilyRsp() {
    }

    public RetrieveSubscriptionsUnderFamilyRsp(List<Subscription> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public List<Subscription> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(List<Subscription> subscriptions) {
        this.subscriptions = subscriptions;
    }

    
}
