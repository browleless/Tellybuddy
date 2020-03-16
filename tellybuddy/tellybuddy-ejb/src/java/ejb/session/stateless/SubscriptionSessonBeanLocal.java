/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Subscription;
import java.util.List;
import java.util.Set;
import javax.ejb.Local;
import javax.validation.ConstraintViolation;
import util.exception.CreateNewSubscriptionException;
import util.exception.InputDataValidationException;
import util.exception.PhoneNumberInUseException;
import util.exception.PlanAlreadyDisabledException;
import util.exception.SubscriptionExistException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author markt
 */
public interface SubscriptionSessonBeanLocal {

    public Subscription createNewSubscription(Subscription newSubscription, Long planId, Long customerId, Long phoneNumberId) throws InputDataValidationException, UnknownPersistenceException, SubscriptionExistException, PhoneNumberInUseException, PlanAlreadyDisabledException,CreateNewSubscriptionException;

    public void updateSubscription(Subscription subscription) throws util.exception.SubscriptionNotFoundException, InputDataValidationException;

    public Subscription amendAllocationOfUniis(Subscription subscription) throws util.exception.SubscriptionNotFoundException, InputDataValidationException;

    public Subscription retrieveSubscriptionBySubscriptionId(Long subscriptionId) throws util.exception.SubscriptionNotFoundException;

    public List<Subscription> retrieveAllCustomer();

    public List<Subscription> retrieveSubscriptionsOfFamilyByFamilyGroupId(Long familyGroupId);

    public void terminateSubscription(Long customerId, Long subscriptionId);

}
