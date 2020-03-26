/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.PhoneNumber;
import java.util.List;
import javax.ejb.Local;
import util.exception.DeletePhoneNumberException;
import util.exception.PhoneNumberExistException;
import util.exception.PhoneNumberNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author tjle2
 */
@Local
public interface PhoneNumberSessionBeanLocal {

    public Long createNewPhoneNumber(PhoneNumber newPhoneNumber) throws PhoneNumberExistException, UnknownPersistenceException;

    public PhoneNumber retrievePhoneNumberByPhoneNumberId(Long phoneNumberId) throws PhoneNumberNotFoundException;

    public List<PhoneNumber> retrieveListOfAvailablePhoneNumbers();

    public void updatePhoneNumber(PhoneNumber phoneNumber) throws PhoneNumberNotFoundException;

    public void deletePhoneNumber(PhoneNumber phoneNumber) throws PhoneNumberNotFoundException, DeletePhoneNumberException;

    public void changePhoneNumber(PhoneNumber oldPhoneNumber, PhoneNumber newPhoneNumber) throws PhoneNumberNotFoundException;
    
}
