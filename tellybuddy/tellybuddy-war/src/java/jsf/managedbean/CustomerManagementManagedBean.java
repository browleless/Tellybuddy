/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.managedbean;

import ejb.session.stateless.CustomerSessionBeanLocal;
import entity.Customer;
import entity.Subscription;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import util.exception.CustomerNotFoundException;

/**
 *
 * @author markt
 */
@Named(value = "customerManagementManagedBean")
@ViewScoped
public class CustomerManagementManagedBean implements Serializable {

    @EJB
    private CustomerSessionBeanLocal customerSessionBeanLocal;

    private List<Customer> customers;
    private Customer newCustomer;

    private Customer customerToView;
    private Customer customerToUpdate;
    private List<Subscription> subscriptionsToView;

    public CustomerManagementManagedBean() {
        newCustomer = new Customer();
    }

    @PostConstruct
    public void postConstruct() {
        setCustomers(customerSessionBeanLocal.retrieveAllCustomer());
    }

    public void verifyCustomer(ActionEvent event) {

        try {
            customerSessionBeanLocal.employeeApprovePendingCustomerAndUpdate(customerToUpdate);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Customer verified successfully", null));
        } catch (CustomerNotFoundException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while updating customer: " + ex.getMessage(), null));
        }
        setCustomers(customerSessionBeanLocal.retrieveAllCustomer());
    }

//    public void createNewPlan(ActionEvent event) {
//
//        try {
//            Long newCustomerId = customerSessionBeanLocal.createCustomer(getNewCustomer());
//             setNewCustomer(new Customer());
//
//            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "New Customer created successfully (Customer ID: " + newCustomerId + ")", null));
//        } catch (CustomerExistException ex) {
//            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred creating new plan: " + ex.getMessage(), null));
//        }
//    }
    public void updateCustomer(ActionEvent event) {
        try {
            customerSessionBeanLocal.updateCustomerDetailsForCustomer(getCustomerToUpdate());
            setCustomerToUpdate(null);

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Plan updated successfully", null));
        } catch (CustomerNotFoundException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while updating seleted customer: " + ex.getMessage(), null));
        }
    }

    public CustomerSessionBeanLocal getCustomerSessionBeanLocal() {
        return customerSessionBeanLocal;
    }

    public void setCustomerSessionBeanLocal(CustomerSessionBeanLocal customerSessionBeanLocal) {
        this.customerSessionBeanLocal = customerSessionBeanLocal;
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
    }

    public Customer getNewCustomer() {
        return newCustomer;
    }

    public void setNewCustomer(Customer newCustomer) {
        this.newCustomer = newCustomer;
    }

    public Customer getCustomerToUpdate() {
        return customerToUpdate;
    }

    public void setCustomerToUpdate(Customer customerToUpdate) {
        this.customerToUpdate = customerToUpdate;
    }

    public Customer getCustomerToView() {
        return customerToView;
    }

    public void setCustomerToView(Customer customerToView) {
        this.customerToView = customerToView;
    }

    public List<Subscription> getSubscriptionsToView() {
        return subscriptionsToView;
    }

    public void setSubscriptionsToView(List<Subscription> subscriptionsToView) {
        this.subscriptionsToView = subscriptionsToView;
    }

}
