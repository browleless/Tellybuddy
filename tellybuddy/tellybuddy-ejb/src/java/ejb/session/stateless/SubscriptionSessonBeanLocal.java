/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Customer;
import entity.Subscription;
import java.util.List;
import java.util.Set;
import javax.ejb.Local;
import javax.validation.ConstraintViolation;
import util.enumeration.SubscriptionStatusEnum;
import util.exception.CreateNewSubscriptionException;
import util.exception.CustomerNotYetApproved;
import util.exception.InputDataValidationException;
import util.exception.PhoneNumberInUseException;
import util.exception.PlanAlreadyDisabledException;
import util.exception.SubscriptionExistException;
import util.exception.SubscriptionNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author markt
 */
public interface SubscriptionSessonBeanLocal {

    public Subscription createNewSubscription(Subscription newSubscription, Long planId, Long customerId, Long phoneNumberId) throws InputDataValidationException, UnknownPersistenceException, CustomerNotYetApproved, SubscriptionExistException, PhoneNumberInUseException, PlanAlreadyDisabledException, CreateNewSubscriptionException;

    public void updateSubscription(Subscription subscription) throws util.exception.SubscriptionNotFoundException, InputDataValidationException;

    public Subscription retrieveSubscriptionBySubscriptionId(Long subscriptionId) throws util.exception.SubscriptionNotFoundException;

    public List<Subscription> retrieveSubscriptionsOfFamilyByFamilyGroupId(Long familyGroupId);

    public void terminateSubscription(Long customerId, Long subscriptionId);

    public List<Subscription> retrieveAllSubscriptionUnderCustomer(Customer customer);

    public List<Subscription> retrieveSubscriptionsByFilter(SubscriptionStatusEnum filterString);

    public List<Subscription> retrieveAllPendingSubscriptions();

    public List<Subscription> retrieveAllSubscriptions();

    public void approveSubsriptionRequest(Subscription subscription) throws SubscriptionNotFoundException;

    public Subscription allocateUnitsForNextMonth(Subscription subscription, Integer dataUnits, Integer smsUnits, Integer talktimeUnits) throws SubscriptionNotFoundException;

    public void requestToTerminateSubscription(Subscription subscription) throws SubscriptionNotFoundException;

    public Subscription amendAllocatedUnits(Subscription subscription) throws SubscriptionNotFoundException, InputDataValidationException;

    //public Subscription amendAddOnUnits(Subscription subscription) throws SubscriptionNotFoundException, InputDataValidationException;
    public Subscription allocateAddOnUnitsForCurrentMonth(Subscription subscription, Integer dataunits, Integer smsUnits, Integer talktimeUnits) throws SubscriptionNotFoundException, InputDataValidationException;


    public List<Subscription> retrieveAllActiveSubscriptionUnderCustomer(Customer customer);

}
