/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Customer;
import entity.FamilyGroup;
import entity.Subscription;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.CustomerAlreadyInFamilyGroupException;
import util.exception.CustomerDoesNotBelongToFamilyGroupException;
import util.exception.CustomerNotFoundException;
import util.exception.CustomerNotVerifiedException;
import util.exception.CustomersDoNotHaveSameAddressOrPostalCodeException;
import util.exception.FamilyGroupDonatedUnitsExceededLimitException;
import util.exception.FamilyGroupNotFoundException;
import util.exception.FamilyGroupReachedLimitOf5MembersException;
import util.exception.InsufficientDataUnitsToDonateToFamilyGroupException;
import util.exception.InsufficientDonatedUnitsInFamilyGroupException;
import util.exception.InsufficientSmsUnitsToDonateToFamilyGroupException;
import util.exception.InsufficientTalktimeUnitsToDonateToFamilyGroupException;
import util.exception.SubscriptionNotFoundException;

/**
 *
 * @author ngjin
 */
@Stateless
public class FamilyGroupSessionBean implements FamilyGroupSessionBeanLocal {

    @EJB
    private SubscriptionSessonBeanLocal subscriptionSessonBeanLocal;

    @EJB
    private CustomerSessionBeanLocal customerSessionBeanLocal;

    @PersistenceContext(unitName = "tellybuddy-ejbPU")
    private EntityManager em;

    public FamilyGroupSessionBean() {

    }

    @Override
    public Long createFamilyGroup(FamilyGroup newFamilyGroup, Customer customer) throws CustomerNotFoundException, CustomerNotVerifiedException, CustomerAlreadyInFamilyGroupException {

        try {
            Customer customerToAssociateWith = customerSessionBeanLocal.retrieveCustomerByCustomerId(customer.getCustomerId());

            if (customerToAssociateWith.getFamilyGroup() != null) {
                throw new CustomerAlreadyInFamilyGroupException("Customer already has a family group!");
            }

            if (customerToAssociateWith.getAddress() == null && customerToAssociateWith.getPostalCode() == null) {
                throw new CustomerNotVerifiedException("Customer has not yet been verified, unable to create family group, please wait for the management to verify your account details.");
            }

            newFamilyGroup.getCustomers().add(customerToAssociateWith);

            em.persist(newFamilyGroup);
            em.flush();

            customerToAssociateWith.setFamilyGroup(newFamilyGroup);

            return newFamilyGroup.getFamilyGroupId();
        } catch (CustomerNotFoundException ex) {
            throw new CustomerNotFoundException("Customer Id not foundi in system");
        }
    }

    @Override
    public FamilyGroup retrieveFamilyGroupByFamilyGroupId(Long familyGroupId) throws FamilyGroupNotFoundException {
        FamilyGroup fg = em.find(FamilyGroup.class, familyGroupId);

        if (fg != null) {
            return fg;
        } else {
            throw new FamilyGroupNotFoundException("Family Group " + familyGroupId + " does not exist!");
        }
    }

    @Override
    public List<FamilyGroup> retrieveAllFamilyGroups() {
        Query q = em.createQuery("SELECT  fg FROM FamilyGroup fg");

        return q.getResultList();
    }

    @Override
    public FamilyGroup retrieveFamilyGroupByCustomer(Customer customer) throws FamilyGroupNotFoundException {

        Query query = em.createQuery("SELECT fg FROM FamilyGroup fg WHERE :inCustomer MEMBER OF fg.customers");
        query.setParameter("inCustomer", customer);

        if (query.getResultList().size() == 0) {
            throw new FamilyGroupNotFoundException("Customer does not have a family group yet!");
        } else {
            return (FamilyGroup) query.getSingleResult();
        }
    }

    @Override
    public void updateFamilyPlan(FamilyGroup fg) throws FamilyGroupNotFoundException {
        if (fg.getFamilyGroupId() != null) {
            FamilyGroup fgToUpdate = retrieveFamilyGroupByFamilyGroupId(fg.getFamilyGroupId());

            // only 1 attribute to update 
            fgToUpdate.setDescription(fg.getDescription());
        } else {
            throw new FamilyGroupNotFoundException("Family Group is not updated as it cannot be found!");
        }
    }

    @Override
    public void addFamilyMember(Customer newMember, FamilyGroup fg) throws FamilyGroupReachedLimitOf5MembersException,
            CustomersDoNotHaveSameAddressOrPostalCodeException, CustomerAlreadyInFamilyGroupException, CustomerNotVerifiedException {

        try {
            FamilyGroup familyGroupToUpdate = retrieveFamilyGroupByFamilyGroupId(fg.getFamilyGroupId());
            Customer familyMemberToAdd = customerSessionBeanLocal.retrieveCustomerByCustomerId(newMember.getCustomerId());

            if (familyMemberToAdd.getPostalCode() == null) {
                throw new CustomerNotVerifiedException("Customer has not yet been verified, unable to add to family group, please wait for the management to verify his/her account details before attempting to add again.");
            }

            if (familyMemberToAdd.getFamilyGroup() != null) {
                throw new CustomerAlreadyInFamilyGroupException("Customer already has a family group!");
            }

            String checkPostalCode = familyGroupToUpdate.getCustomers().get(0).getPostalCode();

            //check for the same postal code
            if (familyMemberToAdd.getPostalCode().equals(checkPostalCode)) {
                //check if the family group has reached its limit of 5
                if (familyGroupToUpdate.getNumberOfMembers() < 5) {
                    familyGroupToUpdate.getCustomers().add(familyMemberToAdd);
                    familyMemberToAdd.setFamilyGroup(familyGroupToUpdate);
                    familyGroupToUpdate.setNumberOfMembers(familyGroupToUpdate.getNumberOfMembers() + 1);

                    if (familyGroupToUpdate.getNumberOfMembers() == 2) {
                        familyGroupToUpdate.setDiscountRate(10);
                    } else {
                        familyGroupToUpdate.setDiscountRate(familyGroupToUpdate.getDiscountRate() + 5);
                    }
                } else {
                    throw new FamilyGroupReachedLimitOf5MembersException("New family member cannot be added into family "
                            + "group as family group has reached its limit of 5 members!");
                }
            } else {
                throw new CustomersDoNotHaveSameAddressOrPostalCodeException("Customer " + familyMemberToAdd.getFirstName()
                        + " " + familyMemberToAdd.getLastName() + " cannot join the family group as he/she does not have"
                        + " the same address/postal code as the other member(s) in the family group!");
            }
        } catch (FamilyGroupNotFoundException | CustomerNotFoundException ex) {
            // won't happen
            ex.printStackTrace();
        }
    }

    @Override
    public void removeFamilyMember(Customer familyMember, FamilyGroup fg) throws CustomerDoesNotBelongToFamilyGroupException,
            FamilyGroupNotFoundException {

        try {
            FamilyGroup familyGroupToUpdate = retrieveFamilyGroupByFamilyGroupId(fg.getFamilyGroupId());
            Customer familyMemberToDelete = customerSessionBeanLocal.retrieveCustomerByCustomerId(familyMember.getCustomerId());

            //check if the customer belongs to the family group
            if (familyGroupToUpdate.getCustomers().contains(familyMemberToDelete)) {
                familyGroupToUpdate.getCustomers().remove(familyMemberToDelete);
                familyMemberToDelete.setFamilyGroup(null);

                familyGroupToUpdate.setNumberOfMembers(familyGroupToUpdate.getNumberOfMembers() - 1);
                familyGroupToUpdate.setDiscountRate(familyGroupToUpdate.getDiscountRate() - 5);

                //check if family group only has 1 member, auto delete the family group
                if (familyGroupToUpdate.getNumberOfMembers() == 1) {
                    deleteFamilyGroup(familyGroupToUpdate.getFamilyGroupId());
                }

            } else {
                throw new CustomerDoesNotBelongToFamilyGroupException("Customer: " + familyMemberToDelete.getFirstName()
                        + " " + familyMemberToDelete.getLastName() + " does not belong to Family Group: FamilyGroupID" + familyGroupToUpdate.getFamilyGroupId());
            }
        } catch (CustomerNotFoundException | FamilyGroupNotFoundException ex) {
            // won't happen
            ex.printStackTrace();
        }
    }

    @Override
    public void deleteFamilyGroup(Long familyGroupId) throws FamilyGroupNotFoundException {
        FamilyGroup fgToDelete = retrieveFamilyGroupByFamilyGroupId(familyGroupId);

        for (Customer c : fgToDelete.getCustomers()) {
            c.setFamilyGroup(null);
        }

        em.remove(fgToDelete);
    }

    @Override
    public void donateUnits(Customer familyMember, Subscription s, FamilyGroup fg,
            Integer smsUnits, Integer dataUnits, Integer talktimeUnits)
            throws CustomerDoesNotBelongToFamilyGroupException, FamilyGroupDonatedUnitsExceededLimitException,
            InsufficientSmsUnitsToDonateToFamilyGroupException, InsufficientDataUnitsToDonateToFamilyGroupException,
            InsufficientTalktimeUnitsToDonateToFamilyGroupException {

        try {

            FamilyGroup familyGroupToUpdate = retrieveFamilyGroupByFamilyGroupId(fg.getFamilyGroupId());
            Subscription subscriptionToUpdate = subscriptionSessonBeanLocal.retrieveSubscriptionBySubscriptionId(s.getSubscriptionId());

            //check if the family member belongs to the family group
            if (familyGroupToUpdate.getCustomers().contains(familyMember)) {
                //check if selected subscription has enough units to donate to family group
                if (smsUnits != 0) {
                    if (subscriptionToUpdate.getSmsUnits().get("allocated") >= smsUnits) {
                        //check if donatedUnits has already reached its upper limit of 1000 units 
                        if (familyGroupToUpdate.getDonatedUnits() + smsUnits > 1000) {
                            throw new FamilyGroupDonatedUnitsExceededLimitException("Family Group has reached limit of 1000 "
                                    + "donated units, can no longer add donated units to the family group!");
                        } else {
                            //donated successfully
                            familyGroupToUpdate.setDonatedUnits(familyGroupToUpdate.getDonatedUnits() + smsUnits);
                            //add into the donated of the subscription 
                            subscriptionToUpdate.getSmsUnits().replace("donated", subscriptionToUpdate.getSmsUnits().get("donated") + smsUnits);
                        }
                    } else {
                        throw new InsufficientSmsUnitsToDonateToFamilyGroupException("Family Member '" + familyMember.getFirstName()
                                + " " + familyMember.getLastName() + "' has insufficient sms units to donate to family group!");
                    }
                }

                if (dataUnits != 0) {
                    if (subscriptionToUpdate.getDataUnits().get("allocated") >= dataUnits) {
                        //check if donatedUnits has already reached its upper limit of 1000 limits
                        if (familyGroupToUpdate.getDonatedUnits() + dataUnits > 1000) {
                            throw new FamilyGroupDonatedUnitsExceededLimitException("Family Group has reached limit of 1000 "
                                    + "donated units, can no longer add donated units to the family group!");
                        } else {
                            //donated successfully
                            familyGroupToUpdate.setDonatedUnits(familyGroupToUpdate.getDonatedUnits() + dataUnits);
                            //add into the donated of the subscription
                            subscriptionToUpdate.getDataUnits().replace("donated", subscriptionToUpdate.getDataUnits().get("donated") + dataUnits);
                        }
                    } else {
                        throw new InsufficientDataUnitsToDonateToFamilyGroupException("Family Member '" + familyMember.getFirstName()
                                + " " + familyMember.getLastName() + "' has insufficient data units to donate to family group!");
                    }
                }

                if (talktimeUnits != 0) {
                    if (subscriptionToUpdate.getTalkTimeUnits().get("allocated") >= talktimeUnits) {
                        //check if donatedUnits has already reached its upper limit of 1000 limits
                        if (familyGroupToUpdate.getDonatedUnits() + talktimeUnits > 1000) {
                            throw new FamilyGroupDonatedUnitsExceededLimitException("Family Group has reached limit of 1000 "
                                    + "donated units, can no longer add donated units to the family group!");
                        } else {
                            //donated successfully
                            familyGroupToUpdate.setDonatedUnits(familyGroupToUpdate.getDonatedUnits() + talktimeUnits);
                            //add into the donated of the subscription
                            subscriptionToUpdate.getTalkTimeUnits().replace("donated", subscriptionToUpdate.getTalkTimeUnits().get("donated") + talktimeUnits);
                        }
                    } else {
                        throw new InsufficientTalktimeUnitsToDonateToFamilyGroupException("Family Member '" + familyMember.getFirstName()
                                + " " + familyMember.getLastName() + "' has insufficient talktime units to donate to family group!");
                    }
                }
            } else {
                throw new CustomerDoesNotBelongToFamilyGroupException("Customer: " + familyMember.getFirstName()
                        + " " + familyMember.getLastName() + " does not belong to Family Group: FamilyGroupID" + familyGroupToUpdate.getFamilyGroupId());
            }
        } catch (FamilyGroupNotFoundException | SubscriptionNotFoundException ex) {
            // won't happen
            ex.printStackTrace();
        }
    }

    @Override
    public void useUnits(Customer familyMember, Subscription s, FamilyGroup fg,
            Integer smsUnits, Integer dataUnits, Integer talktimeUnits)
            throws CustomerDoesNotBelongToFamilyGroupException, InsufficientDonatedUnitsInFamilyGroupException {

        try {

            FamilyGroup familyGroupToUpdate = retrieveFamilyGroupByFamilyGroupId(fg.getFamilyGroupId());
            Subscription subscriptionToUpdate = subscriptionSessonBeanLocal.retrieveSubscriptionBySubscriptionId(s.getSubscriptionId());

            //check if the family member belongs to the family group
            if (familyGroupToUpdate.getCustomers().contains(familyMember)) {
                //check if there is units for the fam member to use
                if (smsUnits != 0) {
                    if (familyGroupToUpdate.getDonatedUnits() >= smsUnits) {
                        // add the requested units to the subscription line of the family member
                        subscriptionToUpdate.getSmsUnits().replace("familyGroup", subscriptionToUpdate.getSmsUnits().get("familyGroup") + smsUnits);
                        familyGroupToUpdate.setDonatedUnits(familyGroupToUpdate.getDonatedUnits() - smsUnits);
                    } else {
                        throw new InsufficientDonatedUnitsInFamilyGroupException("Family Group has insufficient units for "
                                + "family member '" + familyMember.getFirstName() + " " + familyMember.getLastName() + "' to use!");
                    }
                }

                if (dataUnits != 0) {
                    if (familyGroupToUpdate.getDonatedUnits() >= dataUnits) {
                        // add the requested units to the subscription line of the family member
                        subscriptionToUpdate.getDataUnits().replace("familyGroup", subscriptionToUpdate.getDataUnits().get("familyGroup") + dataUnits);
                        familyGroupToUpdate.setDonatedUnits(familyGroupToUpdate.getDonatedUnits() - dataUnits);
                    } else {
                        throw new InsufficientDonatedUnitsInFamilyGroupException("Family Group has insufficient units for "
                                + "family member '" + familyMember.getFirstName() + " " + familyMember.getLastName() + "' to use!");
                    }
                }

                if (talktimeUnits != 0) {
                    if (familyGroupToUpdate.getDonatedUnits() >= talktimeUnits) {
                        // add the requested units to the subscription line of the family member
                        subscriptionToUpdate.getTalkTimeUnits().replace("familyGroup", subscriptionToUpdate.getTalkTimeUnits().get("familyGroup") + talktimeUnits);
                        familyGroupToUpdate.setDonatedUnits(familyGroupToUpdate.getDonatedUnits() - talktimeUnits);
                    } else {
                        throw new InsufficientDonatedUnitsInFamilyGroupException("Family Group has insufficient units for "
                                + "family member '" + familyMember.getFirstName() + " " + familyMember.getLastName() + "' to use!");
                    }
                }
            } else {
                throw new CustomerDoesNotBelongToFamilyGroupException("Customer: " + familyMember.getFirstName()
                        + " " + familyMember.getLastName() + " does not belong to Family Group: FamilyGroupID" + familyGroupToUpdate.getFamilyGroupId());
            }
        } catch (FamilyGroupNotFoundException | SubscriptionNotFoundException ex) {
            // won't happen
            ex.printStackTrace();
        }
    }
}
