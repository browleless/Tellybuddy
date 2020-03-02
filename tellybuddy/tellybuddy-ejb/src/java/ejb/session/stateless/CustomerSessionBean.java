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
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.CustomerNotFoundException;
import util.exception.InvalidLoginCredentialException;

/**
 *
 * @author admin
 */
@Stateless
public class CustomerSessionBean implements CustomerSessionBeanLocal {

    @PersistenceContext(unitName = "tellybuddy-ejbPU")
    private EntityManager em;

    public Long createCustomer(Customer newCustomer) {
        em.persist(newCustomer);
        em.flush();
        return newCustomer.getCustomerId();
    }

    public void updateCustomerDetailsForCustomer(Customer customer) {
        Customer customerToUpdate = retrieveCustomerByCustomerId(customer.getCustomerId());
        customerToUpdate.setPassword(customer.getPassword());
        customerToUpdate.setFirstName(customer.getFirstName());
        customerToUpdate.setLastName(customer.getLastName());
        customerToUpdate.setAge(customer.getAge());
        customerToUpdate.setAddress(customer.getAddress());

    }

    public void updateCustomerNRIC() {
        //employee才有的权限，customer可以request to change nric并上传新的nric照片，employee验证过后才可以update

    }

    public void updateCustomerPostalCode() {
        //employee才有的权限，customer可以request to change nric并上传新的nric照片，employee验证过后才可以update
    }

    public void updateCustomerSubscription() {

    }

    public void updateCustomerTransaction() {
//        在transaction sb里更新transaction的细节
//                之后叫updateCustomerTransaction，把updatedTransaction和
    }

    public void updateCustomerBill(Customer customer, Bill billToUpdate) {

    }

    public void updateCustomerLoyaltyPoint(Long customerId, Integer loyaltyPointsToAdd) {
        Customer customerToUpdate = retrieveCustomerByCustomerId(customerId);
        customerToUpdate.setLoyaltyPoints(customerToUpdate.getLoyaltyPoints() + loyaltyPointsToAdd);
    }

    public List<Customer> retrieveAllCustomer() {
        Query q = em.createQuery("SELECT c FROM Customer c");
        return q.getResultList();
    }

    public Customer retrieveCustomerFromSubscription(Long subscriptionId) {
        Query q = em.createQuery("SELECT s.customer FROM Subscription s WHERE s.subcscriptionId = :inSubscription");
        q.setParameter("inSubscription", subscriptionId);
        return (Customer) q.getSingleResult();
    }

    public Customer retrieveCustomerByCustomerId(Long customerId) {
        Query q = em.createQuery("SELECT c FROM Customer c WHERE c.customerId = :inCustomerId");
        q.setParameter("inCustomerId", customerId);
        return (Customer) q.getSingleResult();
    }

    public Customer retrieveCustomerByUsername(String username) throws CustomerNotFoundException {
        Query q = em.createQuery("SELECT c FROM Customer c WHERE c.username = :inUsername");
        q.setParameter("inUsername", username);
        try {
            return (Customer) q.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new CustomerNotFoundException("Customer Username " + username + " does not exist!");
        }
    }

    public List<Customer> retrieveCustomerFromFamilyGroupId(Long familyGroupId) {
        Query q = em.createQuery("SELECT fg.customers FROM FamilyGroup fg WHERE fg.familyGroupId = :inFamilyGroupId");
        q.setParameter("inFamilyGroupId", familyGroupId);
        return q.getResultList();
    }

    public Customer customerLogin(String username, String password) throws InvalidLoginCredentialException {
        try {
            Customer customer = retrieveCustomerByUsername(username);

            if (customer.getPassword().equals(password)) {
                return customer;
            } else {
                throw new InvalidLoginCredentialException("Username does not exist or invalid password!");
            }
        } catch (CustomerNotFoundException ex) {
            throw new InvalidLoginCredentialException("Username does not exist or invalid password!");
        }
    }

    public void deleteCustomer(Long customerId) {

    }

}
