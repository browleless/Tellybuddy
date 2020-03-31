/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Bill;
import entity.Customer;
import entity.PhoneNumber;
import entity.Plan;
import entity.Subscription;
import entity.UsageDetail;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.enumeration.SubscriptionStatusEnum;
import util.exception.CreateNewSubscriptionException;
import util.exception.CustomerNotFoundException;
import util.exception.CustomerNotYetApproved;
import util.exception.InputDataValidationException;
import util.exception.PhoneNumberInUseException;
import util.exception.PhoneNumberNotFoundException;
import util.exception.PlanAlreadyDisabledException;
import util.exception.PlanNotFoundException;
import util.exception.SubscriptionExistException;
import util.exception.SubscriptionNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UsageDetailNotFoundException;

/**
 *
 * @author markt
 */
@Stateless
@Local
public class SubscriptionSessonBean implements SubscriptionSessonBeanLocal {

    @EJB
    private EmailSessionBeanLocal emailSessionBeanLocal;

    @EJB
    private BillSessionBeanLocal billSessionBeanLocal;

    @EJB
    private UsageDetailSessionBeanLocal usageDetailSessionBeanLocal;

    @EJB
    private CustomerSessionBeanLocal customerSessionBeanLocal;
    @EJB
    private PlanSessionBeanLocal planSessionBeanLocal;
    @EJB
    private PhoneNumberSessionBeanLocal phoneNumberSessionBeanLocal;

    @Resource
    private SessionContext sessionContext;

    @PersistenceContext(unitName = "tellybuddy-ejbPU")
    private EntityManager em;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public SubscriptionSessonBean() {
        this.validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public Subscription createNewSubscription(Subscription newSubscription, Long planId, Long customerId, Long phoneNumberId) throws InputDataValidationException, UnknownPersistenceException, CustomerNotYetApproved, SubscriptionExistException, PhoneNumberInUseException, PlanAlreadyDisabledException, CreateNewSubscriptionException {
        Set<ConstraintViolation<Subscription>> constraintViolations = validator.validate(newSubscription);

        if (constraintViolations.isEmpty()) {
            try {
                Customer customer = customerSessionBeanLocal.retrieveCustomerByCustomerId(customerId);

                if(!customer.getIsApproved()){
                    throw new CustomerNotYetApproved("Please wait for approval before subscribing to a new plan!");
                }
                
                Plan plan = planSessionBeanLocal.retrievePlanByPlanId(planId);
                plan.setIsInUse(true);

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
                phoneNumber.setInUse(Boolean.TRUE);
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
            } catch (PlanNotFoundException | PhoneNumberNotFoundException | CustomerNotFoundException ex) {
                throw new CreateNewSubscriptionException("An unexpected error has occurred while creating the new subscription: " + ex.getMessage());
            }
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }

    }
    public void approveSubsriptionRequest(Subscription subscription) throws SubscriptionNotFoundException {

        Subscription subscriptionToApprove = retrieveSubscriptionBySubscriptionId(subscription.getSubcscriptionId());
        subscriptionToApprove.setIsActive(Boolean.TRUE);

        subscriptionToApprove.setSubscriptionStatusEnum(SubscriptionStatusEnum.ACTIVE);
        subscriptionToApprove.setSubscriptionStartDate(Calendar.getInstance().getTime());
        usageDetailSessionBeanLocal.createNewUsageDetail(subscriptionToApprove);

        Date dateInAMonthsTime = new Date();
        dateInAMonthsTime.setMonth((new Date().getMonth() + 1) % 12);

        TimerService timerService = sessionContext.getTimerService();
        // currently set to add 10s for testing, to replace with dateInAMonthsTime whenever we want
        // create timer for 1 month mark, to generate bill and reset allocation etc.
        timerService.createSingleActionTimer(new Date(new Date().getTime() + 10000), new TimerConfig(subscriptionToApprove, true));

    }

    @Timeout
    public void handleTimeout(Timer timer) {

        Subscription subscription = (Subscription) timer.getInfo();

        try {
            Subscription subscriptionToUpdate = retrieveSubscriptionBySubscriptionId(subscription.getSubcscriptionId());

            BigDecimal addOnPrice = BigDecimal.ZERO;
            int totalAddOnUnits = subscriptionToUpdate.getDataUnits().get("addOn") + subscriptionToUpdate.getSmsUnits().get("addOn") + subscriptionToUpdate.getTalkTimeUnits().get("addOn");

            if (totalAddOnUnits > 0) {
                addOnPrice = subscriptionToUpdate.getPlan().getAddOnPrice().multiply(BigDecimal.valueOf(totalAddOnUnits));
            }

            Integer subscriptionTotalAllowedData = (subscriptionToUpdate.getAllocatedData() + subscriptionToUpdate.getDataUnits().get("addOn") + subscriptionToUpdate.getDataUnits().get("familyGroup")) * subscriptionToUpdate.getPlan().getDataConversionRate();
            Integer subscriptionTotalAllowedSms = (subscriptionToUpdate.getAllocatedSms() + subscriptionToUpdate.getSmsUnits().get("addOn") + subscriptionToUpdate.getSmsUnits().get("familyGroup")) * subscriptionToUpdate.getPlan().getSmsConversionRate();
            Integer subscriptionTotalAllowedTalktime = (subscriptionToUpdate.getAllocatedTalkTime() + subscriptionToUpdate.getTalkTimeUnits().get("addOn") + subscriptionToUpdate.getTalkTimeUnits().get("familyGroup")) * subscriptionToUpdate.getPlan().getTalktimeConversionRate();

            // latest usage detail for the month
            UsageDetail currentUsageDetail = subscriptionToUpdate.getUsageDetails().get(subscriptionToUpdate.getUsageDetails().size() - 1);

            BigDecimal totalExceedPenaltyPrice = BigDecimal.ZERO;

            if (currentUsageDetail.getDataUsage().multiply(BigDecimal.valueOf(1000)).intValue() > subscriptionTotalAllowedData) {
                // hardcoded $3.50 per exceeded gb, int division on purpose
                totalExceedPenaltyPrice.add(BigDecimal.valueOf(Math.ceil((double) (currentUsageDetail.getDataUsage().multiply(BigDecimal.valueOf(1000)).intValue() - subscriptionTotalAllowedData) / 1000) * 3.50));
            }

            if (currentUsageDetail.getSmsUsage() > subscriptionTotalAllowedSms) {
                // hardcoded $0.05 per exceeded SMS
                totalExceedPenaltyPrice.add(BigDecimal.valueOf((currentUsageDetail.getSmsUsage() - subscriptionTotalAllowedSms) * 0.05));
            }

            if (currentUsageDetail.getTalktimeUsage() > subscriptionTotalAllowedTalktime) {
                // hardcoded $0.10 per exceeded min
                totalExceedPenaltyPrice.add(BigDecimal.valueOf((currentUsageDetail.getTalktimeUsage() - subscriptionTotalAllowedTalktime) * 0.10));
            }

            Bill bill = new Bill(subscriptionToUpdate.getPlan().getPrice(), new Date(), addOnPrice, totalExceedPenaltyPrice);
            bill = billSessionBeanLocal.createNewBill(bill, currentUsageDetail, subscriptionToUpdate.getCustomer());
            
            // send email asynchronously
            // currently send to ownself for debugging, ot replace with actual customer email
            emailSessionBeanLocal.emailBillNotificationAsync(bill, subscriptionTotalAllowedData, subscriptionTotalAllowedSms, subscriptionTotalAllowedTalktime, "Tellybuddy<tellybuddy3106@gmail.com>", "tellybuddy3106@gmail.com");

            // reset everything else
            // if customer got adjust for next month then update
            if (subscriptionToUpdate.getDataUnits().get("nextMonth") != 0 && subscriptionToUpdate.getSmsUnits().get("nextMonth") != 0 && subscriptionToUpdate.getTalkTimeUnits().get("nextMonth") != 0) {
                amendAllocationOfUniis(subscriptionToUpdate);
            }

            // reset donated to allocated
            if (subscriptionToUpdate.getDataUnits().get("donated") != 0) {
                subscriptionToUpdate.getDataUnits().put("allocated", subscriptionToUpdate.getDataUnits().get("allocated") + subscriptionToUpdate.getDataUnits().get("donated"));
                subscriptionToUpdate.getDataUnits().put("donated", 0);
            }

            // reset donated to allocated
            if (subscriptionToUpdate.getSmsUnits().get("donated") != 0) {
                subscriptionToUpdate.getSmsUnits().put("allocated", subscriptionToUpdate.getSmsUnits().get("allocated") + subscriptionToUpdate.getSmsUnits().get("donated"));
                subscriptionToUpdate.getSmsUnits().put("donated", 0);
            }

            // reset donated to allocated
            if (subscriptionToUpdate.getTalkTimeUnits().get("donated") != 0) {
                subscriptionToUpdate.getTalkTimeUnits().put("allocated", subscriptionToUpdate.getTalkTimeUnits().get("allocated") + subscriptionToUpdate.getTalkTimeUnits().get("donated"));
                subscriptionToUpdate.getTalkTimeUnits().put("donated", 0);
            }

            //reset purchased add on units
            subscriptionToUpdate.getDataUnits().put("addOn", 0);
            subscriptionToUpdate.getSmsUnits().put("addOn", 0);
            subscriptionToUpdate.getTalkTimeUnits().put("addOn", 0);

            //reset units gotten from family group
            subscriptionToUpdate.getDataUnits().put("familyGroup", 0);
            subscriptionToUpdate.getSmsUnits().put("familyGroup", 0);
            subscriptionToUpdate.getTalkTimeUnits().put("familyGroup", 0);

            // create new usage detail tracking for next month
            usageDetailSessionBeanLocal.createNewUsageDetail(subscriptionToUpdate);

            Date dateInAMonthsTime = new Date();
            dateInAMonthsTime.setMonth((new Date().getMonth() + 1) % 12);

            // for the next timer cycle
            TimerService timerService = sessionContext.getTimerService();
            timerService.createSingleActionTimer(dateInAMonthsTime, new TimerConfig(subscriptionToUpdate, true));

        } catch (SubscriptionNotFoundException | InputDataValidationException | CustomerNotFoundException | UsageDetailNotFoundException | InterruptedException ex) {
            // won't happen
            ex.printStackTrace();
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
    public Subscription amendAllocationOfUniis(Subscription subscription) throws SubscriptionNotFoundException, InputDataValidationException {
        Subscription subscriptionToAmend = retrieveSubscriptionBySubscriptionId(subscription.getSubcscriptionId());

        HashMap<String, Integer> smsUnits = subscriptionToAmend.getSmsUnits();
        HashMap<String, Integer> talkTimeUnits = subscriptionToAmend.getTalkTimeUnits();
        HashMap<String, Integer> dataUnits = subscriptionToAmend.getDataUnits();

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
    public List<Subscription> retrieveSubscriptionsByFilter(SubscriptionStatusEnum filterString) {
        Query query = em.createQuery("Select s FROM Subscription s WHERE s.subscriptionStatusEnum = :filterString");
        query.setParameter("filterString", filterString);
        return query.getResultList();
    }

    @Override
    public List<Subscription> retrieveAllSubscriptionUnderCustomer(Customer customer) {
        Query q = em.createQuery("SELECT s FROM Subscription s WHERE s.customer = :inCustomer");
        q.setParameter("inCustomer", customer);
        return q.getResultList();
    }

    @Override
    public List<Subscription> retrieveAllSubscriptions() {
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
    public List<Subscription> retrieveAllPendingSubscriptions(){
         Query q = em.createQuery("Select s FROM Subscription s WHERE s.subscriptionStatusEnum = :inStatus");
        q.setParameter("inStatus", SubscriptionStatusEnum.PENDING);
        return q.getResultList();
    }

    @Override
    public void terminateSubscription(Long customerId, Long subscriptionId) {
        try {
            Subscription subscriptionToTerminate = retrieveSubscriptionBySubscriptionId(subscriptionId);
            Date terminatingDate = Calendar.getInstance().getTime();

            subscriptionToTerminate.setSubscriptionStatusEnum(SubscriptionStatusEnum.DISABLED);
            subscriptionToTerminate.setSubscriptionEndDate(terminatingDate);
            subscriptionToTerminate.setIsActive(false);

            PhoneNumber phoneNumber = subscriptionToTerminate.getPhoneNumber();
            phoneNumber.setSubscription(null);
            phoneNumber.setInUse(Boolean.FALSE);
            
        } catch (SubscriptionNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<Subscription>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }
}
