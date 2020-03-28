package ejb.session.stateless;

import entity.PhoneNumber;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.exception.DeletePhoneNumberException;
import util.exception.PhoneNumberExistException;
import util.exception.PhoneNumberNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author tjle2
 */
@Stateless
public class PhoneNumberSessionBean implements PhoneNumberSessionBeanLocal {

    @PersistenceContext(unitName = "tellybuddy-ejbPU")
    private EntityManager entityManager;

    @Override
    public Long createNewPhoneNumber(PhoneNumber newPhoneNumber) throws PhoneNumberExistException, UnknownPersistenceException {

        try {
            entityManager.persist(newPhoneNumber);
            entityManager.flush();

            return newPhoneNumber.getPhoneNumberId();
        } catch (PersistenceException ex) {
            if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                    throw new PhoneNumberExistException("A similar phone number already exists!");
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            } else {
                throw new UnknownPersistenceException(ex.getMessage());
            }
        }
    }

    @Override
    public PhoneNumber retrievePhoneNumberByPhoneNumberId(Long phoneNumberId) throws PhoneNumberNotFoundException {

        PhoneNumber phoneNumber = entityManager.find(PhoneNumber.class, phoneNumberId);

        if (phoneNumber != null) {
            return phoneNumber;
        } else {
            throw new PhoneNumberNotFoundException("Phone Number Id " + phoneNumberId + " does not exist");
        }
    }

    @Override
    public List<PhoneNumber> retrieveListOfAvailablePhoneNumbers() {

        Query query = entityManager.createQuery("SELECT pn FROM PhoneNumber pn WHERE pn.inUse = FALSE");
        return query.getResultList();
    }

    @Override
    public void updatePhoneNumber(PhoneNumber phoneNumber) throws PhoneNumberNotFoundException {

        try {
            PhoneNumber phoneNumberToUpdate = retrievePhoneNumberByPhoneNumberId(phoneNumber.getPhoneNumberId());
            phoneNumberToUpdate.setInUse(phoneNumber.getInUse());
            phoneNumberToUpdate.setPhoneNumber(phoneNumber.getPhoneNumber());
        } catch (PhoneNumberNotFoundException ex) {
            throw new PhoneNumberNotFoundException("Phone Number Id not provided for update");
        }
    }

    @Override
    public void deletePhoneNumber(PhoneNumber phoneNumber) throws PhoneNumberNotFoundException, DeletePhoneNumberException {

        try {
            PhoneNumber phoneNumberToDelete = retrievePhoneNumberByPhoneNumberId(phoneNumber.getPhoneNumberId());
            if (phoneNumberToDelete.getSubscription().getIsActive()) {
                throw new DeletePhoneNumberException("Phone number " + phoneNumberToDelete.getPhoneNumber() + " is still in use!");
            } else {
                entityManager.remove(phoneNumberToDelete);
            }
        } catch (PhoneNumberNotFoundException ex) {
            throw new PhoneNumberNotFoundException("Phone Number Id not provided for update");
        }
    }

    @Override
    public void changePhoneNumber(PhoneNumber oldPhoneNumber, PhoneNumber newPhoneNumber) throws PhoneNumberNotFoundException {

        try {
            oldPhoneNumber = retrievePhoneNumberByPhoneNumberId(oldPhoneNumber.getPhoneNumberId());
            newPhoneNumber = retrievePhoneNumberByPhoneNumberId(newPhoneNumber.getPhoneNumberId());

            // disassociate old to new and associate new to old
            oldPhoneNumber.getSubscription().setPhoneNumber(newPhoneNumber);
            newPhoneNumber.setSubscription(oldPhoneNumber.getSubscription());
            oldPhoneNumber.setSubscription(null);

        } catch (PhoneNumberNotFoundException ex) {
            throw new PhoneNumberNotFoundException("Phone Number Id not provided for update");
        }
    }
}
