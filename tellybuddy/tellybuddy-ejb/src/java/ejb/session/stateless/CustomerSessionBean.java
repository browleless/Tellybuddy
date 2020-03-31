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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.enumeration.CustomerStatusEnum;
import util.exception.CustomerExistException;
import util.exception.CustomerNotFoundException;
import util.exception.CustomerUsernameExistException;
import util.exception.InvalidLoginCredentialException;
import util.exception.UnknownPersistenceException;
import util.security.CryptographicHelper;

/**
 *
 * @author admin
 */
@Stateless
@Local
//@DeclareRoles({"employee", "customer"})
public class CustomerSessionBean implements CustomerSessionBeanLocal {

    @EJB(name = "EmailSessionBeanLocal")
    private EmailSessionBeanLocal emailSessionBeanLocal;

    @EJB(name = "SubscriptionSessonBeanLocal")
    private SubscriptionSessonBeanLocal subscriptionSessonBeanLocal;

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
    public Long createCustomer(Customer newCustomer) throws CustomerExistException, CustomerUsernameExistException {

        Query query = em.createQuery("SELECT c FROM Customer c WHERE c.username = :inUsername");
        query.setParameter("inUsername", newCustomer.getUsername());

        if (query.getResultList().size() > 0) {
            throw new CustomerUsernameExistException("Customer with the same username already exists, please try another username.");
        }

        query = em.createQuery("SELECT c FROM Customer c WHERE c.email = :inEmail");
        query.setParameter("inEmail", newCustomer.getEmail());

        if (query.getResultList().size() > 0) {
            throw new CustomerExistException("A customer with the same email already exists! Try with another email or login with your account.");
        }

        query = em.createQuery("SELECT c FROM Customer c WHERE c.nric = :inNewNric OR c.newNric = :inNewNric");
        query.setParameter("inNewNric", newCustomer.getNewNric());

        if (query.getResultList().size() > 0) {
            throw new CustomerExistException("A customer with the same NRIC already exists! Try with another NRIC or login with your account.");
        }

        // never check unique file path since it should be unique with random string added behind if file name is the same
        em.persist(newCustomer);
        em.flush();

        try {
            // for now just hardcoded to send to own account, to change to customer.getEmail() when we want to
            emailSessionBeanLocal.emailCustomerAccountCreationNotification(newCustomer, "Tellybuddy<tellybuddy3106@gmail.com>", "tellybuddy3106@gmail.com");
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        return newCustomer.getCustomerId();
    }

//    @RolesAllowed({"customer"})
    @Override
    public void updateCustomerDetailsForCustomer(Customer customer) throws CustomerNotFoundException {
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
    public void employeeApprovePendingCustomerAndUpdate(Customer customer) throws CustomerNotFoundException {
        Customer customerToUpdate = retrieveCustomerByCustomerId(customer.getCustomerId());
        customerToUpdate.setAddress(customer.getNewAddress());
        customerToUpdate.setNewAddress(null);
        customerToUpdate.setPostalCode(customer.getNewPostalCode());
        customerToUpdate.setNewPostalCode(null);
        customerToUpdate.setNric(customer.getNewNric());
        customerToUpdate.setNewNric(null);
        customerToUpdate.setNricImagePath(customer.getNewNricImagePath());
        customerToUpdate.setNewNricImagePath(null);

        customerToUpdate.setCustomerStatusEnum(CustomerStatusEnum.ACTIVE);
        customerToUpdate.setIsApproved(true);

    }

//    @RolesAllowed({"customer"})
//    @Override
//    public void customerChangeSubscriptionToAPlan(Long customerId, Subscription newSubscription) {
//        //check
//        Customer customer = retrieveCustomerByCustomerId(customerId);
//        List<Subscription> subscriptions = customer.getSubscriptions();
//        Subscription latestSubscription = subscriptions.get(subscriptions.size() - 1);
//        if (latestSubscription.getIsActive() == true) {
//            terminateCustomerSubscriptionToAPlan(customerId);
//        }
//        //front end create subscription object and call createNewSubscriptionMethod in subscriptionSessionBean
//        newSubscription.setIsActive(true);
//        newSubscription.setSubscriptionStartDate(Calendar.getInstance().getTime());
//        customer.getSubscriptions().add(newSubscription);
//        newSubscription.setCustomer(customer);
//    }
    @Override
    public void terminateCustomerSubscriptionToAPlan(Long customerId) throws CustomerNotFoundException {

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
    @Schedule(hour = "6", minute = "0", second = "0", persistent = false)
    public void updateCustomerLoyaltyPoint() {
        //check it at the beginning of every day
        List<Customer> customers = retrieveAllCustomer();
        for (Customer c : customers) {
            Integer months = c.getConsecutiveMonths();
            if (months % 12 == 0 && (months != 0)) {
                //calculation eg.
                //12 months 1000*(12/12)
                //24 months 1000*(24/12)
                c.setLoyaltyPoints(c.getLoyaltyPoints() + (1000 * (months / 12)));
            }
        }

    }

    @Override
    @Schedule(hour = "6", minute = "0", second = "0", persistent = false)
    public void updateCustomerConsecutiveMonths() {
        //check it at the beginning of everyday
        List<Customer> customers = retrieveAllCustomer();

        //get the no. of days in the current month
        Calendar c = Calendar.getInstance();
        Integer monthMaxDays = c.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (Customer customerToUpdate : customers) {
            //check if the customer has any current active subscription
            boolean active = checkForActiveSubscription(customerToUpdate);

            if (active) {
                if (customerToUpdate.getCounter() + 1 == monthMaxDays) {
                    //new monthly cycle starts
                    customerToUpdate.setCounter(0);
                    customerToUpdate.setConsecutiveMonths(customerToUpdate.getConsecutiveMonths() + 1);
                } else {
                    customerToUpdate.setCounter(customerToUpdate.getCounter() + 1);
                }
            } else {
                customerToUpdate.setCounter(0);
                customerToUpdate.setConsecutiveMonths(0);
            }
        }
    }

    public boolean checkForActiveSubscription(Customer customer) {
        List<Subscription> subscriptions = subscriptionSessonBeanLocal.retrieveAllSubscriptionUnderCustomer(customer);
        for (Subscription s : subscriptions) {
            if (s.getIsActive() == true) {
                return true;
            }
        }
        return false;
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
        Query q = em.createQuery("SELECT s.customer FROM Subscription s WHERE s.subscriptionId = :inSubscription");
        q.setParameter("inSubscription", subscriptionId);
        return (Customer) q.getSingleResult();
    }
//    @RolesAllowed({"employee"})

    @Override
    public Customer retrieveCustomerByCustomerId(Long customerId) throws CustomerNotFoundException {
        {
            Customer customer = em.find(Customer.class, customerId);

            if (customer != null) {
                customer.getAnnouncements();
                customer.getQuizAttempts();
                customer.getTransactions();
                customer.getSubscriptions();
                customer.getBills();

                return customer;
            } else {
                throw new CustomerNotFoundException("Customer ID " + customerId + " does not exist!");
            }
        }
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
    
    @Override
    public int retrieveNoActiveSubscriptions(Customer customer){
        int count = 0;
        for(Subscription s:customer.getSubscriptions()){
            if(s.getIsActive()){
                count++;
            }
        }
        return count;
    }
//    @RolesAllowed({"employee", "customer"})
    
    @Override
    public List<Customer> retrieveAllPendingCustomers(){
        Query q = em.createQuery("Select c FROM Customer c WHERE c.customerStatusEnum = :inStatus");
        q.setParameter("inStatus", CustomerStatusEnum.PENDING);
        return q.getResultList();
    }
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

    public void deleteCustomer(Long customerId) {
        em.remove(em.find(Customer.class, customerId));

    }
}
