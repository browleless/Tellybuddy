/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.datamodel;

/**
 *
 * @author kaikai
 */
public class AllocateUnitsForNextMonthRsp {
    
    private Long subscriptionId;

    public AllocateUnitsForNextMonthRsp() {
    }

    public AllocateUnitsForNextMonthRsp(Long subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public Long getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(Long subscriptionId) {
        this.subscriptionId = subscriptionId;
    }
    
}
