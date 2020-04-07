/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Customer;
import java.util.List;
import javax.ejb.Local;
import util.exception.CustomerExistException;
import util.exception.CustomerNotFoundException;
import util.exception.CustomerUsernameExistException;
import util.exception.InvalidLoginCredentialException;

/**
 *
 * @author admin
 */
@Local
public interface CustomerSessionBeanLocal {

    public Long createCustomer(Customer newCustomer) throws CustomerExistException, CustomerUsernameExistException;

    public void updateCustomerDetailsForCustomer(Customer customer) throws CustomerNotFoundException;

    public void employeeApprovePendingCustomerAndUpdate(Customer customer) throws CustomerNotFoundException;

    public void updateCustomerTransaction();

    public void updateCustomerLoyaltyPoint();

    public List<Customer> retrieveAllCustomer();

    public List<Customer> retrieveListOfPendingCustomer();

    public Customer retrieveCustomerFromSubscription(Long subscriptionId);

    public Customer retrieveCustomerByCustomerId(Long customerId) throws CustomerNotFoundException;

    public Customer retrieveCustomerByUsername(String username) throws CustomerNotFoundException;

    public List<Customer> retrieveCustomerFromFamilyGroupId(Long familyGroupId);

    public Customer customerLogin(String username, String password) throws InvalidLoginCredentialException;

    public void updateCustomerConsecutiveMonths();

    public int retrieveNoActiveSubscriptions(Customer customer);

    public List<Customer> retrieveAllPendingCustomers();

    //   public void customerChangeSubscriptionToAPlan(Long customerId, Subscription newSubscription);
    public void terminateCustomerSubscriptionToAPlan(Long customerId) throws CustomerNotFoundException;

    public Customer retrieveCustomerByEmail(String email) throws CustomerNotFoundException;

    public Customer retrieveCustomerBySalt(String salt) throws CustomerNotFoundException;

    public void updateCustomerPassword(Customer customer) throws CustomerNotFoundException;

}
