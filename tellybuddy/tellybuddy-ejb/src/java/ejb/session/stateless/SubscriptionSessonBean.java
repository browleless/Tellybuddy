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
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Schedule;
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

    //  private Subscription currentSubscription;
    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public SubscriptionSessonBean() {
        this.validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
        //     currentSubscription = new Subscription();
    }

    @Override
    public Subscription createNewSubscription(Subscription newSubscription, Long planId, Long customerId, Long phoneNumberId) throws InputDataValidationException, UnknownPersistenceException, CustomerNotYetApproved, SubscriptionExistException, PhoneNumberInUseException, PlanAlreadyDisabledException, CreateNewSubscriptionException {
        Set<ConstraintViolation<Subscription>> constraintViolations = validator.validate(newSubscription);

        if (constraintViolations.isEmpty()) {
            try {
                Customer customer = customerSessionBeanLocal.retrieveCustomerByCustomerId(customerId);

//                if(!customer.getIsApproved()){
//                    throw new CustomerNotYetApproved("Please wait for approval before subscribing to a new plan!");
//                }
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

        Subscription subscriptionToApprove = retrieveSubscriptionBySubscriptionId(subscription.getSubscriptionId());
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
            Subscription subscriptionToUpdate = retrieveSubscriptionBySubscriptionId(subscription.getSubscriptionId());

            BigDecimal addOnPrice = BigDecimal.ZERO;
            int totalAddOnUnits = subscriptionToUpdate.getDataUnits().get("addOn") + subscriptionToUpdate.getSmsUnits().get("addOn") + subscriptionToUpdate.getTalkTimeUnits().get("addOn");

            if (totalAddOnUnits > 0) {
                addOnPrice = subscriptionToUpdate.getPlan().getAddOnPrice().multiply(BigDecimal.valueOf(totalAddOnUnits));
            }

            Integer subscriptionTotalAllowedData = (subscriptionToUpdate.getAllocatedData() + subscriptionToUpdate.getDataUnits().get("addOn") + subscriptionToUpdate.getDataUnits().get("familyGroup") + subscriptionToUpdate.getDataUnits().get("quizExtraUnits") - subscriptionToUpdate.getDataUnits().get("donated")) * subscriptionToUpdate.getPlan().getDataConversionRate();
            Integer subscriptionTotalAllowedSms = (subscriptionToUpdate.getAllocatedSms() + subscriptionToUpdate.getSmsUnits().get("addOn") + subscriptionToUpdate.getSmsUnits().get("familyGroup") + subscriptionToUpdate.getSmsUnits().get("quizExtraUnits") - subscriptionToUpdate.getSmsUnits().get("donated")) * subscriptionToUpdate.getPlan().getSmsConversionRate();
            Integer subscriptionTotalAllowedTalktime = (subscriptionToUpdate.getAllocatedTalkTime() + subscriptionToUpdate.getTalkTimeUnits().get("addOn") + subscriptionToUpdate.getTalkTimeUnits().get("familyGroup") + subscriptionToUpdate.getTalkTimeUnits().get("quizExtraUnits") - subscriptionToUpdate.getTalkTimeUnits().get("donated")) * subscriptionToUpdate.getPlan().getTalktimeConversionRate();

            // latest usage detail for the month
            UsageDetail currentUsageDetail = subscriptionToUpdate.getUsageDetails().get(subscriptionToUpdate.getUsageDetails().size() - 1);
            
            // store latest allowed quota as info will be lost in the next cycle
            currentUsageDetail.setAllowedDataUsage(BigDecimal.valueOf(subscriptionTotalAllowedData.doubleValue() / 1000));
            currentUsageDetail.setAllowedSmsUsage(subscriptionTotalAllowedSms);
            currentUsageDetail.setAllowedTalktimeUsage(subscriptionTotalAllowedTalktime);
            
            BigDecimal totalExceedPenaltyPrice = BigDecimal.ZERO;

            if (currentUsageDetail.getDataUsage().multiply(BigDecimal.valueOf(1000)).intValue() > subscriptionTotalAllowedData) {
                // hardcoded $3.50 per exceeded gb, int division on purpose
                totalExceedPenaltyPrice = totalExceedPenaltyPrice.add(BigDecimal.valueOf(Math.ceil((double) (currentUsageDetail.getDataUsage().multiply(BigDecimal.valueOf(1000)).intValue() - subscriptionTotalAllowedData) / 1000)).multiply(BigDecimal.valueOf(3.5)));
            }

            if (currentUsageDetail.getSmsUsage() > subscriptionTotalAllowedSms) {
                // hardcoded $0.05 per exceeded SMS
                totalExceedPenaltyPrice = totalExceedPenaltyPrice.add(BigDecimal.valueOf(currentUsageDetail.getSmsUsage() - subscriptionTotalAllowedSms).multiply(BigDecimal.valueOf(0.05)));
            }

            if (currentUsageDetail.getTalktimeUsage().setScale(0, RoundingMode.CEILING).intValue() > subscriptionTotalAllowedTalktime) {
                // hardcoded $0.10 per exceeded min
                totalExceedPenaltyPrice = totalExceedPenaltyPrice.add(currentUsageDetail.getTalktimeUsage().setScale(0, RoundingMode.CEILING).subtract(BigDecimal.valueOf(subscriptionTotalAllowedTalktime)).multiply(BigDecimal.valueOf(0.1)));
            }

            Integer familyGroupDiscountRate = 0;

            if (subscriptionToUpdate.getCustomer().getFamilyGroup() != null) {
                // family group discount rate applied
                familyGroupDiscountRate = subscriptionToUpdate.getCustomer().getFamilyGroup().getDiscountRate();
            }

            Bill bill = new Bill(subscriptionToUpdate.getPlan().getPrice(), new Date(), addOnPrice, totalExceedPenaltyPrice, familyGroupDiscountRate);
            bill = billSessionBeanLocal.createNewBill(bill, currentUsageDetail, subscriptionToUpdate.getCustomer());

            // send email asynchronously
            // currently send to ownself for debugging, ot replace with actual customer email
            emailSessionBeanLocal.emailBillNotificationAsync(bill, subscriptionTotalAllowedData, subscriptionTotalAllowedSms, subscriptionTotalAllowedTalktime, "Tellybuddy<tellybuddy3106@gmail.com>", "tellybuddy3106@gmail.com");

            // reset everything else
            // if customer got adjust for next month then update
            if (subscriptionToUpdate.getDataUnits().get("nextMonth") != 0 && subscriptionToUpdate.getSmsUnits().get("nextMonth") != 0 && subscriptionToUpdate.getTalkTimeUnits().get("nextMonth") != 0) {
                amendAllocatedUnits(subscriptionToUpdate);
            }

            // reset donated units
            subscriptionToUpdate.getDataUnits().put("donated", 0);
            subscriptionToUpdate.getSmsUnits().put("donated", 0);
            subscriptionToUpdate.getTalkTimeUnits().put("donated", 0);

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
    
    @Schedule(second="*/10", minute="*", hour="*")
    public void incrementUsageDetail() {
        for(Subscription s: this.retrieveSubscriptionsByFilter(SubscriptionStatusEnum.ACTIVE)){
            UsageDetail currentUsageDetail = s.getUsageDetails().get(s.getUsageDetails().size() - 1);
            
            currentUsageDetail.setDataUsage(currentUsageDetail.getDataUsage().add(BigDecimal.valueOf(0.015)));
            currentUsageDetail.setSmsUsage(currentUsageDetail.getSmsUsage());
            currentUsageDetail.setTalktimeUsage(currentUsageDetail.getTalktimeUsage().add(BigDecimal.valueOf(0.010)));
        }
    }

    @Override
    public void updateSubscription(Subscription subscription) throws SubscriptionNotFoundException, InputDataValidationException {
        Set<ConstraintViolation<Subscription>> constraintViolations = validator.validate(subscription);

        if (constraintViolations.isEmpty()) {
            Subscription subscriptionToUpdate = retrieveSubscriptionBySubscriptionId(subscription.getSubscriptionId());
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

    @Override
    public Subscription amendAllocatedUnits(Subscription subscription) throws SubscriptionNotFoundException, InputDataValidationException {
        Subscription subscriptionToAmend = retrieveSubscriptionBySubscriptionId(subscription.getSubscriptionId());

        HashMap<String, Integer> smsUnits = subscriptionToAmend.getSmsUnits();
        HashMap<String, Integer> talkTimeUnits = subscriptionToAmend.getTalkTimeUnits();
        HashMap<String, Integer> dataUnits = subscriptionToAmend.getDataUnits();

        smsUnits.put("allocated", smsUnits.get("nextMonth"));
        talkTimeUnits.put("allocated", talkTimeUnits.get("nextMonth"));
        dataUnits.put("allocated", dataUnits.get("nextMonth"));

        smsUnits.put("nextMonth", 0);
        talkTimeUnits.put("nextMonth", 0);
        dataUnits.put("nextMonth", 0);
        updateSubscription(subscriptionToAmend);
        return subscriptionToAmend;
    }

    @Override
    public Subscription allocateUnitsForNextMonth(Subscription subscription, Integer dataUnits, Integer smsUnits, Integer talktimeUnits) throws SubscriptionNotFoundException {

        Subscription subscriptionToAmend = retrieveSubscriptionBySubscriptionId(subscription.getSubscriptionId());

        subscriptionToAmend.getDataUnits().put("nextMonth", dataUnits);
        subscriptionToAmend.getSmsUnits().put("nextMonth", smsUnits);
        subscriptionToAmend.getTalkTimeUnits().put("nextMonth", talktimeUnits);
        return subscriptionToAmend;
    }

    @Override
    public Subscription allocateQuizExtraUnits(Subscription subscription, Integer dataUnits, Integer smsUnits, Integer talktimeUnits) throws SubscriptionNotFoundException {

        Subscription subscriptionToAmend = retrieveSubscriptionBySubscriptionId(subscription.getSubscriptionId());

        subscriptionToAmend.getDataUnits().put("quizExtraUnits", dataUnits);
        subscriptionToAmend.getSmsUnits().put("quizExtraUnits", smsUnits);
        subscriptionToAmend.getTalkTimeUnits().put("quizExtraUnits", talktimeUnits);
        return subscriptionToAmend;
    }

    @Override
    public Subscription allocateAddOnUnitsForCurrentMonth(Subscription subscription, Integer dataunits, Integer smsUnits, Integer talktimeUnits) throws SubscriptionNotFoundException, InputDataValidationException {
        Subscription subscriptionToAmend = retrieveSubscriptionBySubscriptionId(subscription.getSubscriptionId());
        subscriptionToAmend.getDataUnits().put("addOn", dataunits + subscriptionToAmend.getDataUnits().get("addOn"));
        subscriptionToAmend.getSmsUnits().put("addOn", smsUnits + subscriptionToAmend.getSmsUnits().get("addOn"));
        subscriptionToAmend.getTalkTimeUnits().put("addOn", talktimeUnits + subscriptionToAmend.getTalkTimeUnits().get("addOn"));
        return subscriptionToAmend;
    }

    @Override
    public void requestToTerminateSubscription(Subscription subscription) throws SubscriptionNotFoundException {

        Subscription subscriptionToAmend = retrieveSubscriptionBySubscriptionId(subscription.getSubscriptionId());

        subscriptionToAmend.setSubscriptionStatusEnum(SubscriptionStatusEnum.TERMINATING);
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
    public List<Subscription> retrieveAllSubscriptionsWithBillsUnderCustomer(Customer customer) {
        Query q = em.createQuery("SELECT s FROM Subscription s WHERE s.customer = :inCustomer AND EXISTS (SELECT b FROM Bill b WHERE b.usageDetail.subscription = s)");
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
    public List<Subscription> retrieveAllPendingSubscriptions() {
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
