/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Customer;
import entity.Subscription;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exception.CustomerNotFoundException;
import util.exception.InvalidLoginCredentialException;
import util.security.CryptographicHelper;

/**
 *
 * @author admin
 */
@Stateless
@Local
//@DeclareRoles({"employee", "customer"})
public class CustomerSessionBean implements CustomerSessionBeanLocal {

    @PersistenceContext(unitName = "tellybuddy-ejbPU")
    private EntityManager em;
    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public CustomerSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

//    @RolesAllowed({"customer"})
    @Override
    public Long createCustomer(Customer newCustomer) {
        em.persist(newCustomer);
        em.flush();
        return newCustomer.getCustomerId();
    }

//    @RolesAllowed({"customer"})
    @Override
    public void updateCustomerDetailsForCustomer(Customer customer) {
        Customer customerToUpdate = retrieveCustomerByCustomerId(customer.getCustomerId());
        customerToUpdate.setPassword(customer.getPassword());
        customerToUpdate.setFirstName(customer.getFirstName());
        customerToUpdate.setLastName(customer.getLastName());
        customerToUpdate.setAge(customer.getAge());
        customerToUpdate.setNewAddress(customer.getNewAddress());
        customerToUpdate.setNewPostalCode(customer.getNewPostalCode());
        customerToUpdate.setNewNric(customer.getNewNric());
        customerToUpdate.setNewNricImagePath(customer.getNewNricImagePath());
    }

//    @RolesAllowed({"employee"})
    @Override
    public void employeeApprovePendingCustomerAndUpdate(Customer customer) {
        Customer customerToUpdate = retrieveCustomerByCustomerId(customer.getCustomerId());
        customerToUpdate.setAddress(customer.getNewAddress());
        customer.setNewAddress(null);
        customerToUpdate.setPostalCode(customer.getNewPostalCode());
        customer.setNewPostalCode(null);
        customerToUpdate.setNric(customer.getNewNric());
        customer.setNewNric(null);
        customerToUpdate.setNricImagePath(customer.getNewNricImagePath());
        customer.setNewNricImagePath(null);
    }

//    @RolesAllowed({"customer"})
    @Override
    public void customerChangeSubscriptionToAPlan(Long customerId, Subscription newSubscription) {
        //check
        Customer customer = retrieveCustomerByCustomerId(customerId);
        List<Subscription> subscriptions = customer.getSubscriptions();
        Subscription latestSubscription = subscriptions.get(subscriptions.size() - 1);
        if (latestSubscription.getIsActive() == true) {
            terminateCustomerSubscriptionToAPlan(customerId);
        }
        //front end create subscription object and call createNewSubscriptionMethod in subscriptionSessionBean
        newSubscription.setIsActive(true);
        newSubscription.setSubscriptionStartDate(Calendar.getInstance().getTime());
        customer.getSubscriptions().add(newSubscription);
        newSubscription.setCustomer(customer);
    }

    @Override
    public void terminateCustomerSubscriptionToAPlan(Long customerId) {

        Customer customer = retrieveCustomerByCustomerId(customerId);
        List<Subscription> subscriptions = customer.getSubscriptions();
        Date today = Calendar.getInstance().getTime();
        Subscription latestSubscription = subscriptions.get(subscriptions.size() - 1);
        latestSubscription.setSubscriptionEndDate(today);
        latestSubscription.setIsActive(false);
    }

//add loyalty points is in EJB timer
//    @RolesAllowed({"employee"})
    @Override
    public void updateCustomerTransaction() {
        //void transaction
        //change the amount
    }

    //updateCustomerBill is written in the bill sessionbean, take in billId and customerId
    
//    @RolesAllowed({"employee"})
    @Override
    public void updateCustomerLoyaltyPoint(Long customerId, Integer loyaltyPointsToAdd) {
        Customer customerToUpdate = retrieveCustomerByCustomerId(customerId);
        customerToUpdate.setLoyaltyPoints(customerToUpdate.getLoyaltyPoints() + loyaltyPointsToAdd);
    }

//    @RolesAllowed({"employee"})
    @Override
    public List<Customer> retrieveAllCustomer() {
        Query q = em.createQuery("SELECT c FROM Customer c");
        return q.getResultList();
    }
//    @RolesAllowed({"employee"})

    @Override
    public List<Customer> retrieveListOfPendingCustomer() {
        Query q = em.createQuery("SELECT c FROM Customer c WHERE c.newNric IS NOT NULL OR c.newAddress IS NOT NULL OR c.newPostalCode IS NOT NULL OR c.newNricImagePath IS NOT NULL");
        return q.getResultList();
    }
//    @RolesAllowed({"employee"})

    @Override
    public Customer retrieveCustomerFromSubscription(Long subscriptionId) {
        Query q = em.createQuery("SELECT s.customer FROM Subscription s WHERE s.subcscriptionId = :inSubscription");
        q.setParameter("inSubscription", subscriptionId);
        return (Customer) q.getSingleResult();
    }
//    @RolesAllowed({"employee"})

    @Override
    public Customer retrieveCustomerByCustomerId(Long customerId) {
        Query q = em.createQuery("SELECT c FROM Customer c WHERE c.customerId = :inCustomerId");
        q.setParameter("inCustomerId", customerId);
        return (Customer) q.getSingleResult();
    }
//    @RolesAllowed({"employee"})

    @Override
    public Customer retrieveCustomerByUsername(String username) throws CustomerNotFoundException {
        Query q = em.createQuery("SELECT c FROM Customer c WHERE c.username = :inUsername");
        q.setParameter("inUsername", username);
        try {
            return (Customer) q.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new CustomerNotFoundException("Customer Username " + username + " does not exist!");
        }
    }
//    @RolesAllowed({"employee", "customer"})

    @Override
    public List<Customer> retrieveCustomerFromFamilyGroupId(Long familyGroupId) {
        Query q = em.createQuery("SELECT fg.customers FROM FamilyGroup fg WHERE fg.familyGroupId = :inFamilyGroupId");
        q.setParameter("inFamilyGroupId", familyGroupId);
        return q.getResultList();
    }
//    @RolesAllowed({"customer"})

    @Override
    public Customer customerLogin(String username, String password) throws InvalidLoginCredentialException {

        try {
            Customer customer = retrieveCustomerByUsername(username);
            String passwordHash = CryptographicHelper.getInstance().byteArrayToHexString(CryptographicHelper.getInstance().doMD5Hashing(password + customer.getSalt()));
            if (customer.getPassword().equals(passwordHash)) {
                return customer;
            } else {
                throw new InvalidLoginCredentialException("Username does not exist or invalid password!");
            }
        } catch (CustomerNotFoundException ex) {
            throw new InvalidLoginCredentialException("Username does not exist or invalid password!");
        }
    }

}
