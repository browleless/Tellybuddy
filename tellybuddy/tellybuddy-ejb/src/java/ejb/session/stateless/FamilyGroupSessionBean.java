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
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.CustomerDoesNotBelongToFamilyGroupException;
import util.exception.CustomersDoNotHaveSameAddressOrPostalCodeException;
import util.exception.FamilyGroupNotFoundException;
import util.exception.FamilyGroupReachedLimitOf5MembersException;

/**
 *
 * @author ngjin
 */
@Stateless
public class FamilyGroupSessionBean implements FamilyGroupSessionBeanLocal {

    @PersistenceContext(unitName = "tellybuddy-ejbPU")
    private EntityManager em;

    public FamilyGroupSessionBean() {

    }

    @Override
    public Long createFamilyGroup(FamilyGroup newFamilyGroup) throws CustomersDoNotHaveSameAddressOrPostalCodeException {

        //check if all customers have the same addressn & postalCode fields
        String checkAddress = newFamilyGroup.getCustomers().get(0).getAddress();
        String checkPostalCode = newFamilyGroup.getCustomers().get(0).getPostalCode();

        for (Customer c : newFamilyGroup.getCustomers()) {
            if (!c.getAddress().equals(checkAddress) || !c.getPostalCode().equals(checkPostalCode)) {
                throw new CustomersDoNotHaveSameAddressOrPostalCodeException("Family Group cannot be created as "
                        + "the customers: " + newFamilyGroup.getCustomers().get(0).getFirstName()
                        + newFamilyGroup.getCustomers().get(0).getLastName() + " and "
                        + c.getFirstName() + c.getLastName() + " do not have the same address/postal code!");
            }
        }

        em.persist(newFamilyGroup);
        em.flush();

        return newFamilyGroup.getFamilyGroupId();
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
        if (newMember.getAddress().equals(checkAddress) && newMember.getPostalCode().equals(checkPostalCode)) {
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
            throws CustomerDoesNotBelongToFamilyGroupException {
        //check if the family member belongs to the family group
        if (fg.getCustomers().contains(familyMember)) {
            //check if family member has enought units to donate 

            //use the subscription SB
            //create UnitsExceededLimitException()
        } else {
            throw new CustomerDoesNotBelongToFamilyGroupException("Customer: " + familyMember.getFirstName()
                    + " " + familyMember.getLastName() + " does not belong to Family Group: FamilyGroupID" + fg.getFamilyGroupId());
        }
    }

    @Override
    public void useUnits(Customer familyMember, Subscription s, FamilyGroup fg,
            Integer smsUnits, Integer dataUnits, Integer talktimeUnits)
            throws CustomerDoesNotBelongToFamilyGroupException {

        //check if the family member belongs to the family group
        if (fg.getCustomers().contains(familyMember)) {
            //check if there is units for the fam member to use

            //use the subscription SB -- pick a subscription line to add the units into
            //create InsufficientUnitsException()
        } else {
            throw new CustomerDoesNotBelongToFamilyGroupException("Customer: " + familyMember.getFirstName()
                    + " " + familyMember.getLastName() + " does not belong to Family Group: FamilyGroupID" + fg.getFamilyGroupId());
        }
    }
}
