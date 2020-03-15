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
import javax.ejb.Local;
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
@Local
public interface FamilyGroupSessionBeanLocal {

    public Long createFamilyGroup(FamilyGroup newFamilyGroup) throws CustomersDoNotHaveSameAddressOrPostalCodeException;

    public FamilyGroup retrieveFamilyGroupByFamilyGroupId(Long familyGroupId) throws FamilyGroupNotFoundException;

    public List<FamilyGroup> retrieveAllFamilyGroups();

    public void updateFamilyPlan(FamilyGroup fg) throws FamilyGroupNotFoundException;

    public void addFamilyMember(Customer newMember, FamilyGroup fg) throws FamilyGroupReachedLimitOf5MembersException, CustomersDoNotHaveSameAddressOrPostalCodeException;

    public void removeFamilyMember(Customer familyMember, FamilyGroup fg) throws CustomerDoesNotBelongToFamilyGroupException, FamilyGroupNotFoundException;

    public void donateUnits(Customer familyMember, Subscription s, FamilyGroup fg, Integer smsUnits, Integer dataUnits, Integer talktimeUnits)
            throws CustomerDoesNotBelongToFamilyGroupException, FamilyGroupDonatedUnitsExceededLimitException,
            InsufficientSmsUnitsToDonateToFamilyGroupException, InsufficientDataUnitsToDonateToFamilyGroupException,
            InsufficientTalktimeUnitsToDonateToFamilyGroupException;

    public void useUnits(Customer familyMember, Subscription s, FamilyGroup fg, Integer smsUnits, Integer dataUnits, Integer talktimeUnits) throws CustomerDoesNotBelongToFamilyGroupException,
            InsufficientDonatedUnitsInFamilyGroupException;

}
