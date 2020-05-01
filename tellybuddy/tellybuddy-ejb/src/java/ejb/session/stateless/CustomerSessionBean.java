/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Customer;
import entity.PhoneNumber;
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
import static util.enumeration.CustomerStatusEnum.UPDATING;
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

public class CustomerSessionBean implements CustomerSessionBeanLocal {

    @EJB(name = "PhoneNumberSessionBeanLocal")
    private PhoneNumberSessionBeanLocal phoneNumberSessionBeanLocal;

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

    @Override
    public Long createCustomer(Customer newCustomer) throws CustomerExistException, CustomerUsernameExistException {

        Customer customer = new Customer(newCustomer.getUsername(), newCustomer.getPassword(), newCustomer.getFirstName(), newCustomer.getLastName(), newCustomer.getAge(),
                newCustomer.getNewAddress(), newCustomer.getNewPostalCode(), newCustomer.getEmail(), newCustomer.getNewNric(), "", "", new Date(), "");

        Query query = em.createQuery("SELECT c FROM Customer c WHERE c.username = :inUsername");
        query.setParameter("inUsername", customer.getUsername());

        if (query.getResultList().size() > 0) {
            throw new CustomerUsernameExistException("Customer with the same username already exists, please try another username.");
        }

        query = em.createQuery("SELECT c FROM Customer c WHERE c.email = :inEmail");
        query.setParameter("inEmail", customer.getEmail());

        if (query.getResultList().size() > 0) {
            throw new CustomerExistException("A customer with the same email already exists! Try with another email or login with your account.");
        }

        query = em.createQuery("SELECT c FROM Customer c WHERE c.nric = :inNewNric OR c.newNric = :inNewNric");
        query.setParameter("inNewNric", customer.getNewNric());

        if (query.getResultList().size() > 0) {
            throw new CustomerExistException("A customer with the same NRIC already exists! Try with another NRIC or login with your account.");
        }

        // never check unique file path since it should be unique with random string added behind if file name is the same
        em.persist(customer);
        em.flush();

        try {
            // for now just hardcoded to send to own account, to change to customer.getEmail() when we want to
            emailSessionBeanLocal.emailCustomerAccountCreationNotification(customer, "Tellybuddy<tellybuddy3106@gmail.com>", "tellybuddy3106@gmail.com");
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        return customer.getCustomerId();
    }

    @Override
    public void updateCustomerDetailsForCustomer(Customer customer) throws CustomerNotFoundException {
        try {
            Customer customerToUpdate = retrieveCustomerByCustomerId(customer.getCustomerId());
            customerToUpdate.setFirstName(customer.getFirstName());
            customerToUpdate.setLastName(customer.getLastName());
            customerToUpdate.setAge(customer.getAge());
            if (customer.getNewAddress() != null) {
                customerToUpdate.setCustomerStatusEnum(CustomerStatusEnum.UPDATING);
                customerToUpdate.setNewAddress(customer.getNewAddress());
            }

            if (customer.getNewPostalCode() != null) {
                customerToUpdate.setCustomerStatusEnum(CustomerStatusEnum.UPDATING);
                customerToUpdate.setNewPostalCode(customer.getNewPostalCode());
            }
            if (customer.getNewNric() != null) {
                customerToUpdate.setCustomerStatusEnum(CustomerStatusEnum.UPDATING);
                customerToUpdate.setNewNric(customer.getNewNric());
            }

            customerToUpdate.setNewNricBackImagePath(customer.getNewNricBackImagePath());
            // em.flush();
        } catch (Exception ex) {
            throw new CustomerNotFoundException(ex.getMessage());
        }
    }

    @Override
    public void updateCustomerPassword(Customer customer) throws CustomerNotFoundException {

        Customer customerToUpdate = retrieveCustomerByCustomerId(customer.getCustomerId());

        String newSalt = CryptographicHelper.getInstance().generateRandomString(32);

        customerToUpdate.setSalt(newSalt);
        customerToUpdate.setPassword(CryptographicHelper.getInstance().byteArrayToHexString(CryptographicHelper.getInstance().doMD5Hashing(customer.getPassword() + newSalt)));
    }

    @Override
    public void employeeApprovePendingCustomerAndUpdate(Customer customer) throws CustomerNotFoundException {
        Customer customerToUpdate = retrieveCustomerByCustomerId(customer.getCustomerId());

        if (customerToUpdate.getNewAddress() != null) {
            customerToUpdate.setAddress(customer.getNewAddress());
            customerToUpdate.setNewAddress(null);
        }
        
        if (customerToUpdate.getNewPostalCode()!= null) {
            customerToUpdate.setPostalCode(customer.getNewPostalCode());
            customerToUpdate.setNewPostalCode(null);
        }
        
        if (customerToUpdate.getNewNric()!= null) {
            customerToUpdate.setNric(customer.getNewNric());
            customerToUpdate.setNewNric(null);
        }
        
        if (customerToUpdate.getNricBackImagePath() != null) {
            customerToUpdate.setNricFrontImagePath(customer.getNewNricFrontImagePath());
            customerToUpdate.setNricBackImagePath(customer.getNewNricBackImagePath());
            customerToUpdate.setNewNricBackImagePath(null);
            customerToUpdate.setNewNricFrontImagePath(null);
        }

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
    //updateCustomerBill is written in the bill sessionbean, take in billId and customerId
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

    @Override
    public boolean checkForActiveSubscription(Customer customer) {
        List<Subscription> subscriptions = subscriptionSessonBeanLocal.retrieveAllSubscriptionUnderCustomer(customer);
        for (Subscription s : subscriptions) {
            if (s.getIsActive() == true) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Customer> retrieveAllCustomer() {
        Query q = em.createQuery("SELECT c FROM Customer c");
        return q.getResultList();
    }

    @Override
    public List<Customer> retrieveListOfPendingCustomer() {
        Query q = em.createQuery("SELECT c FROM Customer c WHERE c.newNric IS NOT NULL OR c.newAddress IS NOT NULL OR c.newPostalCode IS NOT NULL OR c.newNricImagePath IS NOT NULL");
        return q.getResultList();
    }

    @Override
    public Customer retrieveCustomerFromSubscription(Long subscriptionId) {
        Query q = em.createQuery("SELECT s.customer FROM Subscription s WHERE s.subscriptionId = :inSubscription");
        q.setParameter("inSubscription", subscriptionId);
        return (Customer) q.getSingleResult();
    }

    @Override
    public Customer retrieveCustomerByCustomerId(Long customerId) throws CustomerNotFoundException {

        Query q = em.createQuery("SELECT c FROM Customer c WHERE c.customerId = :inCustomer");
        q.setParameter("inCustomer", customerId);
        Customer customer = (Customer) q.getSingleResult();
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
    public Customer retrieveCustomerByEmail(String email) throws CustomerNotFoundException {
        Query query = em.createQuery("SELECT c FROM Customer c WHERE c.email = :inEmail");
        query.setParameter("inEmail", email);
        try {
            return (Customer) query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new CustomerNotFoundException("Customer Email " + email + " does not exist!");
        }
    }

    @Override
    public Customer retrieveCustomerBySalt(String salt) throws CustomerNotFoundException {
        Query query = em.createQuery("SELECT c FROM Customer c WHERE c.salt = :inSalt");
        query.setParameter("inSalt", salt);
        try {
            return (Customer) query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new CustomerNotFoundException("Customer salt " + salt + " does not exist!");
        }
    }

    @Override
    public int retrieveNoActiveSubscriptions(Customer customer) {
        int count = 0;
        for (Subscription s : customer.getSubscriptions()) {
            if (s.getIsActive()) {
                count++;
            }
        }
        return count;
    }

    @Override
    public List<Customer> retrieveAllPendingCustomers() {
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
