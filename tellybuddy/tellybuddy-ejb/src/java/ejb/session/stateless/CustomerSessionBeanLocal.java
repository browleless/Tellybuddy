/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Bill;
import entity.Customer;
import entity.Subscription;
import java.util.List;
import javax.ejb.Local;
import util.exception.CustomerNotFoundException;
import util.exception.InvalidLoginCredentialException;

/**
 *
 * @author admin
 */
@Local
public interface CustomerSessionBeanLocal {

    public Long createCustomer(Customer newCustomer);

    public void updateCustomerDetailsForCustomer(Customer customer);

    public void employeeApprovePendingCustomerAndUpdate(Customer customer);

    public void updateCustomerSubscription(String customerId, Subscription subscription);

    public void updateCustomerTransaction();

    public void updateCustomerBill(Customer customer, Bill billToUpdate);

    public void updateCustomerLoyaltyPoint(Long customerId, Integer loyaltyPointsToAdd);

    public List<Customer> retrieveAllCustomer();

    public List<Customer> retrieveListOfPendingCustomer();

    public Customer retrieveCustomerFromSubscription(Long subscriptionId);

    public Customer retrieveCustomerByCustomerId(Long customerId);

    public Customer retrieveCustomerByUsername(String username) throws CustomerNotFoundException;

    public List<Customer> retrieveCustomerFromFamilyGroupId(Long familyGroupId);

    public Customer customerLogin(String username, String password) throws InvalidLoginCredentialException;
    
}
