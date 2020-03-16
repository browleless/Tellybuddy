/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Customer;
import entity.PhoneNumber;
import entity.Plan;
import entity.Subscription;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exception.CreateNewSubscriptionException;
import util.exception.InputDataValidationException;
import util.exception.PhoneNumberInUseException;
import util.exception.PhoneNumberNotFoundException;
import util.exception.PlanAlreadyDisabledException;
import util.exception.PlanNotFoundException;
import util.exception.SubscriptionExistException;
import util.exception.SubscriptionNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author markt
 */
@Stateless
@Local
public class SubscriptionSessonBean implements SubscriptionSessonBeanLocal {

    @EJB
    private CustomerSessionBeanLocal customerSessionBeanLocal;
    @EJB
    private PlanSessionBeanLocal planSessionBeanLocal;
    @EJB
    private PhoneNumberSessionBeanLocal phoneNumberSessionBeanLocal;

    @PersistenceContext(unitName = "tellybuddy-ejbPU")
    private EntityManager em;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public SubscriptionSessonBean() {
        this.validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public Subscription createNewSubscription(Subscription newSubscription, Long planId, Long customerId, Long phoneNumberId) throws InputDataValidationException, UnknownPersistenceException, SubscriptionExistException, PhoneNumberInUseException, PlanAlreadyDisabledException, CreateNewSubscriptionException {
        Set<ConstraintViolation<Subscription>> constraintViolations = validator.validate(newSubscription);

        if (constraintViolations.isEmpty()) {
            try {
                Customer customer = customerSessionBeanLocal.retrieveCustomerByCustomerId(customerId);

                Plan plan = planSessionBeanLocal.retrievePlanByPlanId(planId);
                if (plan.getIsDisabled()) {
                    throw new PlanAlreadyDisabledException("Selected Plan has been discontinued! Please try again with a different plan!");
                }

                PhoneNumber phoneNumber = phoneNumberSessionBeanLocal.retrievePhoneNumberByPhoneNumberId(phoneNumberId);
                if (phoneNumber.getSubscription() != null) {
                    throw new PhoneNumberInUseException("Selected Phone Number is currently in use!");
                }

                newSubscription.setCustomer(customer);
                newSubscription.setPlan(plan);
                newSubscription.setPhoneNumber(phoneNumber);
                
                customer.getSubscriptions().add(newSubscription);
                phoneNumber.setSubscription(newSubscription);
                
//                HashMap<String, Integer> dataUnits = new HashMap<>();
//                dataUnits.put("")
//                HashMap<String, Integer> smsUnits = new HashMap<>();
//                HashMap<String, Integer> talkTimeUnits = new HashMap<>();
//                
                
                em.persist(newSubscription);
                em.flush();
                return newSubscription;

            } catch (PersistenceException ex) {
                if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                        throw new SubscriptionExistException();
                    } else {
                        throw new UnknownPersistenceException(ex.getMessage());
                    }
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            } catch (PlanNotFoundException | PhoneNumberNotFoundException  ex) {
                throw new CreateNewSubscriptionException("An error has occurred while creating the new subscription: " + ex.getMessage());
            }
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }

    }

    @Override
    public void updateSubscription(Subscription subscription) throws SubscriptionNotFoundException, InputDataValidationException {
        Set<ConstraintViolation<Subscription>> constraintViolations = validator.validate(subscription);

        if (constraintViolations.isEmpty()) {
            Subscription subscriptionToUpdate = retrieveSubscriptionBySubscriptionId(subscription.getSubcscriptionId());
            subscriptionToUpdate.setCustomer(subscription.getCustomer());
            subscriptionToUpdate.setPlan(subscription.getPlan());
            subscriptionToUpdate.setPhoneNumber(subscription.getPhoneNumber());
            
            subscriptionToUpdate.setSubscriptionStartDate(subscription.getSubscriptionStartDate());
            subscriptionToUpdate.setSubscriptionEndDate(subscription.getSubscriptionEndDate());
            
            subscriptionToUpdate.setIsActive(subscription.getIsActive());

            subscriptionToUpdate.setDataUnits(subscription.getDataUnits());
            subscriptionToUpdate.setSmsUnits(subscription.getSmsUnits());
            subscriptionToUpdate.setTalkTimeUnits(subscription.getTalkTimeUnits());

        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }

    }
    //to be done at the start of every month; before this just updateSubscription only
    @Override
    public Subscription amendAllocationOfUniis(Subscription subscription) throws SubscriptionNotFoundException, InputDataValidationException{
        Subscription subscriptionToAmend = retrieveSubscriptionBySubscriptionId(subscription.getSubcscriptionId());
        
        HashMap<String,Integer> smsUnits = subscriptionToAmend.getSmsUnits();
        HashMap<String,Integer> talkTimeUnits = subscriptionToAmend.getTalkTimeUnits();
        HashMap<String,Integer> dataUnits = subscriptionToAmend.getDataUnits();
        
        smsUnits.put("allocated", smsUnits.get("nextMonth"));
        talkTimeUnits.put("allocated", talkTimeUnits.get("nextMonth"));
        dataUnits.put("allocated", dataUnits.get("nextMonth"));
        
        //reset to 0 at the start of every month
        smsUnits.put("nextMonth", 0);
        talkTimeUnits.put("nextMonth", 0);
        dataUnits.put("nextMonth", 0);
        
        updateSubscription(subscriptionToAmend);
        return subscriptionToAmend;
    }
    
    @Override
    public Subscription retrieveSubscriptionBySubscriptionId(Long subscriptionId) throws SubscriptionNotFoundException {
        Subscription subscription = em.find(Subscription.class, subscriptionId);

        if (subscription != null) {
            subscription.getUsageDetails().size();

            return subscription;
        } else {
            throw new SubscriptionNotFoundException("Subscription ID " + subscriptionId + " does not exist!");
        }
    }

    @Override
    public List<Subscription> retrieveAllSubscriptionUnderCustomer(Customer customer){
        Query q = em.createQuery("SELECT s FROM Subscription s WHERE s.customer = :inCustomer");
        q.setParameter("inCustomer",customer);
        return q.getResultList();
    }
    
    @Override
    public List<Subscription> retrieveAllCustomer() {
        Query q = em.createQuery("SELECT s FROM Subscription s");
        return q.getResultList();
    }
    
    @Override
    public List<Subscription> retrieveSubscriptionsOfFamilyByFamilyGroupId(Long familyGroupId) {
        Query q = em.createQuery("SELECT s FROM Subscription s WHERE s.customer.familyGroup.familyGroupId = :inFamilyGroupId");
        q.setParameter("inFamilyGroupId", familyGroupId);
        return q.getResultList();
    }
    
    @Override
    public void terminateSubscription(Long customerId, Long subscriptionId){
        Subscription subscriptionToTerminate = em.find(Subscription.class, subscriptionId);
        Date today = Calendar.getInstance().getTime();
        subscriptionToTerminate.setSubscriptionEndDate(today);
        subscriptionToTerminate.setIsActive(false);
        
        PhoneNumber phoneNumber = subscriptionToTerminate.getPhoneNumber();
        subscriptionToTerminate.setPhoneNumber(null);
        phoneNumber.setSubscription(null);
    }
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<Subscription>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }
}
