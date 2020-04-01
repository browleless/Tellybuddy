/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.managedbean;

import ejb.session.stateless.SubscriptionSessonBeanLocal;
import entity.Subscription;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import org.primefaces.model.chart.PieChartModel;
import util.enumeration.SubscriptionStatusEnum;
import util.exception.SubscriptionNotFoundException;

/**
 *
 * @author markt
 */
@Named(value = "subscriptionManagementManagedBean")
@ViewScoped
public class SubscriptionManagementManagedBean implements Serializable {

    @EJB
    private SubscriptionSessonBeanLocal subscriptionSessonBeanLocal;

    private List<Subscription> subscriptions;
    private List<Subscription> filteredSubscriptions;

    private Subscription subscriptionToView;

    private PieChartModel allocationModel = new PieChartModel();
    private String selectedFilter;

    public SubscriptionManagementManagedBean() {
        this.selectedFilter = "All";
    }

    @PostConstruct
    public void postConstruct() {
        setSubscriptions(subscriptionSessonBeanLocal.retrieveAllSubscriptions());
    }

    public void assignModel() {
        allocationModel.setTitle("Subscription Units Allocation");
        allocationModel.setLegendPosition("ne");
        allocationModel.setFill(false);
        allocationModel.setShadow(false);
        allocationModel.setShowDataLabels(true);
//        allocationModel.setDiameter(150);

        allocationModel.set("Data Units", subscriptionToView.getAllocatedData() + subscriptionToView.getDataUnits().get("addOn") + subscriptionToView.getDataUnits().get("familyGroup") + subscriptionToView.getDataUnits().get("quizExtraUnits") - subscriptionToView.getDataUnits().get("donated"));
        allocationModel.set("Talk Time Units", subscriptionToView.getAllocatedTalkTime() + subscriptionToView.getTalkTimeUnits().get("addOn") + subscriptionToView.getTalkTimeUnits().get("familyGroup") + subscriptionToView.getTalkTimeUnits().get("quizExtraUnits") - subscriptionToView.getTalkTimeUnits().get("donated"));
        allocationModel.set("Sms Units", subscriptionToView.getAllocatedSms() + subscriptionToView.getSmsUnits().get("addOn") + subscriptionToView.getSmsUnits().get("familyGroup") + subscriptionToView.getSmsUnits().get("quizExtraUnits") - subscriptionToView.getSmsUnits().get("donated"));

//        try {
//            if (allocationModel.getData().get("Data Units") == null) {
//                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while approving subscription: ", null));
//            }
//        } catch (Exception e) {
//            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Successfully loaded pie", null));
//        }
    }

    public void approveSubscriptionRequest(ActionEvent ae) {
        try {
            subscriptionSessonBeanLocal.approveSubsriptionRequest(subscriptionToView);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Subscription approval successful", null));
        } catch (SubscriptionNotFoundException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while approving subscription: " + ex.getMessage(), null));
        }
        doFilter();
    }

    public void terminateSubscriptionRequest(ActionEvent ae) {
        subscriptionSessonBeanLocal.terminateSubscription(subscriptionToView.getCustomer().getCustomerId(), subscriptionToView.getSubcscriptionId());
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Subscription termination successful", null));
        doFilter();
    }

    public void doFilter() {

        if (selectedFilter.equals("All")) {
            setSubscriptions(subscriptionSessonBeanLocal.retrieveAllSubscriptions());

        } else if (selectedFilter.equals("Pending")) {
            setSubscriptions(subscriptionSessonBeanLocal.retrieveSubscriptionsByFilter(SubscriptionStatusEnum.PENDING));
        } else if (selectedFilter.equals("Active")) {
            setSubscriptions(subscriptionSessonBeanLocal.retrieveSubscriptionsByFilter(SubscriptionStatusEnum.ACTIVE));
        } else if (selectedFilter.equals("Terminating")) {
            setSubscriptions(subscriptionSessonBeanLocal.retrieveSubscriptionsByFilter(SubscriptionStatusEnum.TERMINATING));
        } else {
            setSubscriptions(subscriptionSessonBeanLocal.retrieveSubscriptionsByFilter(SubscriptionStatusEnum.DISABLED));
        }
    }

    public List<Subscription> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(List<Subscription> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public Subscription getSubscriptionToView() {
        return subscriptionToView;
    }

    public void setSubscriptionToView(Subscription subscriptionToView) {
        this.subscriptionToView = subscriptionToView;
    }

    public SubscriptionSessonBeanLocal getSubscriptionSessonBeanLocal() {
        return subscriptionSessonBeanLocal;
    }

    public void setSubscriptionSessonBeanLocal(SubscriptionSessonBeanLocal subscriptionSessonBeanLocal) {
        this.subscriptionSessonBeanLocal = subscriptionSessonBeanLocal;
    }

    public PieChartModel getAllocationModel() {
        return allocationModel;
    }

    public void setAllocationModel(PieChartModel allocationModel) {
        this.allocationModel = allocationModel;
    }

    public List<Subscription> getFilteredSubscriptions() {
        return filteredSubscriptions;
    }

    public void setFilteredSubscriptions(List<Subscription> filteredSubscriptions) {
        this.filteredSubscriptions = filteredSubscriptions;
    }

    public String getSelectedFilter() {
        return selectedFilter;
    }

    public void setSelectedFilter(String selectedFilter) {
        this.selectedFilter = selectedFilter;
    }

}
