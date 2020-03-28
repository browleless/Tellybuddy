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
import util.exception.CustomersDoNotHaveSameAddressOrPostalCodeException;
import util.exception.FamilyGroupDonatedUnitsExceededLimitException;
import util.exception.FamilyGroupNotFoundException;
import util.exception.FamilyGroupReachedLimitOf5MembersException;
import util.exception.InsufficientDataUnitsToDonateToFamilyGroupException;
import util.exception.InsufficientDonatedUnitsInFamilyGroupException;
import util.exception.InsufficientSmsUnitsToDonateToFamilyGroupException;
import util.exception.InsufficientTalktimeUnitsToDonateToFamilyGroupException;

/**
 *
 * @author ngjin
 */
@Stateless
public class FamilyGroupSessionBean implements FamilyGroupSessionBeanLocal {

    @EJB
    private CustomerSessionBeanLocal customerSessionBeanLocal;

    @PersistenceContext(unitName = "tellybuddy-ejbPU")
    private EntityManager em;

    public FamilyGroupSessionBean() {

    }

    @Override
    public FamilyGroup createFamilyGroup(String desc, Customer customer) throws CustomersDoNotHaveSameAddressOrPostalCodeException, CustomerAlreadyInFamilyGroupException {
        try {
            Customer customerToJoin = customerSessionBeanLocal.retrieveCustomerByCustomerId(customer.getCustomerId());

            FamilyGroup newFamilyGroup = new FamilyGroup(desc);
            newFamilyGroup.getCustomers().add(customerToJoin);
            customer.setFamilyGroup(newFamilyGroup);
            em.persist(newFamilyGroup);
            em.flush();
            return newFamilyGroup;
        } catch (Exception ex) {
        }
//        if(customer.getFamilyGroup() != null){
//            throw new CustomerAlreadyInFamilyGroupException();
//        }
//        

//        for (Customer c : newFamilyGroup.getCustomers()) {
//            if (!c.getAddress().equals(checkAddress) || !c.getPostalCode().equals(checkPostalCode)) {
//                throw new CustomersDoNotHaveSameAddressOrPostalCodeException("Family Group cannot be created as "
//                        + "the customers: " + newFamilyGroup.getCustomers().get(0).getFirstName()
//                        + newFamilyGroup.getCustomers().get(0).getLastName() + " and "
//                        + c.getFirstName() + c.getLastName() + " do not have the same address/postal code!");
//            }
//        }
        return null;
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
            CustomersDoNotHaveSameAddressOrPostalCodeException {
        String checkAddress = fg.getCustomers().get(0).getAddress();
        String checkPostalCode = fg.getCustomers().get(0).getPostalCode();

        //check for the same address and postal code
        //Rmb change bck to get Address and postal code
        if (newMember.getNewAddress().equals(checkAddress) && newMember.getNewPostalCode().equals(checkPostalCode)) {
            //check if the family group has reached its limit of 5
            if (fg.getNumberOfMembers() < 5) {
                fg.getCustomers().add(newMember);
                newMember.setFamilyGroup(fg);
                fg.setNumberOfMembers(fg.getNumberOfMembers() + 1);
            } else {
                throw new FamilyGroupReachedLimitOf5MembersException("New family member cannot be added into family "
                        + "group as family group has reached its limit of 5 members!");
            }
        } else {
            throw new CustomersDoNotHaveSameAddressOrPostalCodeException("Customer " + newMember.getFirstName()
                    + " " + newMember.getLastName() + "cannot join the family group as he/she does not have"
                    + " the same address/postal code as the other member(s) in the family group!");
        }
    }

    @Override
    public void removeFamilyMember(Customer familyMember, FamilyGroup fg) throws CustomerDoesNotBelongToFamilyGroupException,
            FamilyGroupNotFoundException {
        //check if the customer belongs to the family group
        if (fg.getCustomers().contains(familyMember)) {
            fg.getCustomers().remove(familyMember);
            familyMember.setFamilyGroup(null);

            fg.setNumberOfMembers(fg.getNumberOfMembers() - 1);

            //check if family group only has 1 member, auto delete the family group
            if (fg.getNumberOfMembers() == 1) {
                deleteFamilyGroup(fg.getFamilyGroupId());
            }

        } else {
            throw new CustomerDoesNotBelongToFamilyGroupException("Customer: " + familyMember.getFirstName()
                    + " " + familyMember.getLastName() + " does not belong to Family Group: FamilyGroupID" + fg.getFamilyGroupId());
        }
    }

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

        //check if the family member belongs to the family group
        if (fg.getCustomers().contains(familyMember)) {
            //check if selected subscription has enough units to donate to family group
            if (smsUnits != 0) {
                if (s.getSmsUnits().get("allocated") >= smsUnits) {
                    //check if donatedUnits has already reached its upper limit of 1000 units 
                    if (fg.getDonatedUnits() + smsUnits > 1000) {
                        throw new FamilyGroupDonatedUnitsExceededLimitException("Family Group has reached limit of 1000 "
                                + "donated units, can no longer add donated units to the family group!");
                    } else {
                        //donated successfully
                        fg.setDonatedUnits(fg.getDonatedUnits() + smsUnits);
                        //add into the usage of the subscription 
                        s.getSmsUnits().replace("usage", s.getSmsUnits().get("usage") + smsUnits);
                    }
                } else {
                    throw new InsufficientSmsUnitsToDonateToFamilyGroupException("Family Member '" + familyMember.getFirstName()
                            + " " + familyMember.getLastName() + "' has insufficient sms units to donate to family group!");
                }
            }

            if (dataUnits != 0) {
                if (s.getDataUnits().get("allocated") >= dataUnits) {
                    //check if donatedUnits has already reached its upper limit of 1000 limits
                    if (fg.getDonatedUnits() + dataUnits > 1000) {
                        throw new FamilyGroupDonatedUnitsExceededLimitException("Family Group has reached limit of 1000 "
                                + "donated units, can no longer add donated units to the family group!");
                    } else {
                        //donated successfully
                        fg.setDonatedUnits(fg.getDonatedUnits() + dataUnits);
                        //add into the usage of the subscription
                        s.getDataUnits().replace("usage", s.getDataUnits().get("usage") + dataUnits);
                    }
                } else {
                    throw new InsufficientDataUnitsToDonateToFamilyGroupException("Family Member '" + familyMember.getFirstName()
                            + " " + familyMember.getLastName() + "' has insufficient data units to donate to family group!");
                }
            }

            if (talktimeUnits != 0) {
                if (s.getTalkTimeUnits().get("allocated") >= talktimeUnits) {
                    //check if donatedUnits has already reached its upper limit of 1000 limits
                    if (fg.getDonatedUnits() + talktimeUnits > 1000) {
                        throw new FamilyGroupDonatedUnitsExceededLimitException("Family Group has reached limit of 1000 "
                                + "donated units, can no longer add donated units to the family group!");
                    } else {
                        //donated successfully
                        fg.setDonatedUnits(fg.getDonatedUnits() + talktimeUnits);
                        //add into the usage of the subscription
                        s.getTalkTimeUnits().replace("usage", s.getTalkTimeUnits().get("usage") + talktimeUnits);
                    }
                } else {
                    throw new InsufficientTalktimeUnitsToDonateToFamilyGroupException("Family Member '" + familyMember.getFirstName()
                            + " " + familyMember.getLastName() + "' has insufficient talktime units to donate to family group!");
                }
            }
        } else {
            throw new CustomerDoesNotBelongToFamilyGroupException("Customer: " + familyMember.getFirstName()
                    + " " + familyMember.getLastName() + " does not belong to Family Group: FamilyGroupID" + fg.getFamilyGroupId());
        }
    }

    @Override
    public void useUnits(Customer familyMember, Subscription s, FamilyGroup fg,
            Integer smsUnits, Integer dataUnits, Integer talktimeUnits)
            throws CustomerDoesNotBelongToFamilyGroupException, InsufficientDonatedUnitsInFamilyGroupException {

        //check if the family member belongs to the family group
        if (fg.getCustomers().contains(familyMember)) {
            //check if there is units for the fam member to use
            if (smsUnits != 0) {
                if (fg.getDonatedUnits() >= smsUnits) {
                    // add the requested units to the subscription line of the family member
                    s.getSmsUnits().replace("addOn", smsUnits);
                    fg.setDonatedUnits(fg.getDonatedUnits() - smsUnits);
                } else {
                    throw new InsufficientDonatedUnitsInFamilyGroupException("Family Group has insufficient units for "
                            + "family member '" + familyMember.getFirstName() + " " + familyMember.getLastName() + "' to use!");
                }
            }

            if (dataUnits != 0) {
                if (fg.getDonatedUnits() >= dataUnits) {
                    // add the requested units to the subscription line of the family member
                    s.getDataUnits().replace("addOn", dataUnits);
                    fg.setDonatedUnits(fg.getDonatedUnits() - dataUnits);
                } else {
                    throw new InsufficientDonatedUnitsInFamilyGroupException("Family Group has insufficient units for "
                            + "family member '" + familyMember.getFirstName() + " " + familyMember.getLastName() + "' to use!");
                }
            }

            if (talktimeUnits != 0) {
                if (fg.getDonatedUnits() >= talktimeUnits) {
                    // add the requested units to the subscription line of the family member
                    s.getTalkTimeUnits().replace("addOn", talktimeUnits);
                    fg.setDonatedUnits(fg.getDonatedUnits() - talktimeUnits);
                } else {
                    throw new InsufficientDonatedUnitsInFamilyGroupException("Family Group has insufficient units for "
                            + "family member '" + familyMember.getFirstName() + " " + familyMember.getLastName() + "' to use!");
                }
            }
        } else {
            throw new CustomerDoesNotBelongToFamilyGroupException("Customer: " + familyMember.getFirstName()
                    + " " + familyMember.getLastName() + " does not belong to Family Group: FamilyGroupID" + fg.getFamilyGroupId());
        }
    }
}
