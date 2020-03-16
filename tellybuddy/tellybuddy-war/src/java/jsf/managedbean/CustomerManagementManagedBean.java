/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.managedbean;

import ejb.session.stateless.CustomerSessionBeanLocal;
import entity.Customer;
import entity.Plan;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import util.exception.CustomerExistException;
import util.exception.CustomerNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.PlanAlreadyDisabledException;
import util.exception.PlanExistException;
import util.exception.PlanNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author markt
 */
@Named(value = "customerManagementManagedBean")
@SessionScoped
public class CustomerManagementManagedBean implements Serializable {

    @EJB
    private CustomerSessionBeanLocal customerSessionBeanLocal;

    @Inject
    private ViewPlanManagedBean viewPlanManagedBean;

    private List<Customer> customers;
    private Customer newCustomer;

    private Customer customerToUpdate;

    public CustomerManagementManagedBean() {
        newCustomer = new Customer();
    }
    
    @PostConstruct
    public void postConstruct(){
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

    public ViewPlanManagedBean getViewPlanManagedBean() {
        return viewPlanManagedBean;
    }

    public void setViewPlanManagedBean(ViewPlanManagedBean viewPlanManagedBean) {
        this.viewPlanManagedBean = viewPlanManagedBean;
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
    
    
    
    
}
