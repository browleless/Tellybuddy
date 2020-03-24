/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.managedbean;

import ejb.session.stateless.SubscriptionSessonBeanLocal;
import entity.Customer;
import entity.Subscription;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.Dependent;

/**
 *
 * @author markt
 */
@Named(value = "subscriptionManagedBean")
@Dependent
public class SubscriptionManagedBean {

    @EJB
    private SubscriptionSessonBeanLocal subscriptionSessonBeanLocal;

    private List<Subscription> subscriptions;
    private Subscription subscriptionToApprove;

    private Subscription subscriptionToView;
            
    @PostConstruct
    public void postConstruct(){
         setSubscriptions(subscriptionSessonBeanLocal.retrieveAllSubscriptions());
    }
    public SubscriptionManagedBean() {
    }

    public List<Subscription> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(List<Subscription> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public Subscription getSubscriptionToApprove() {
        return subscriptionToApprove;
    }

    public void setSubscriptionToApprove(Subscription subscriptionToApprove) {
        this.subscriptionToApprove = subscriptionToApprove;
    }

    public Subscription getSubscriptionToView() {
        return subscriptionToView;
    }

    public void setSubscriptionToView(Subscription subscriptionToView) {
        this.subscriptionToView = subscriptionToView;
    }
    
}
