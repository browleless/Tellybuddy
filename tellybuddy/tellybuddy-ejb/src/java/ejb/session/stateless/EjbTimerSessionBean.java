/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.PhoneNumber;
import entity.Product;
import java.util.List;
import java.util.Random;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.exception.PhoneNumberExistException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author kaikai
 */
@Stateless
public class EjbTimerSessionBean implements EjbTimerSessionBeanLocal {

    @EJB(name = "PhoneNumberSessionBeanLocal")
    private PhoneNumberSessionBeanLocal phoneNumberSessionBeanLocal;

    @EJB(name = "ProductSessionBeanLocal")
    private ProductSessionBeanLocal productSessionBeanLocal;

    @Schedule(hour = "*", minute = "*/5", info = "productEntityReorderQuantityCheckTimer")
    public void productReorderQuantityCheckTimer() {

        List<Product> products = productSessionBeanLocal.retrieveAllProducts();
        for (Product product : products) {
            if (product.getQuantityOnHand().compareTo(product.getReorderQuantity()) <= 0) {
                System.out.println("********** Product " + product.getSkuCode() + " requires reordering: QOH = " + product.getQuantityOnHand() + "; RQ = " + product.getReorderQuantity());
            }
        }
    }

    @Schedule(hour = "*", minute = "*/5", info = "addNewPhoneNumbersTimer")
    public void addNewPhoneNumbers() {

        List<PhoneNumber> phoneNumbers = phoneNumberSessionBeanLocal.retrieveListOfAvailablePhoneNumbers();

        if (phoneNumbers.size() < 15) {
            int numberOfPhoneNumbersToCreate = 15 - phoneNumbers.size();
            for (int i = 0; i < numberOfPhoneNumbersToCreate; i++) {
                try {
                    Random random = new Random();
                    int randomFirstDigit = random.nextInt((9 - 8) + 1) + 8;
                    int randomSevenDigits = 1000000 + random.nextInt(9000000);
                    String newNumber = "" + randomFirstDigit + randomSevenDigits;
                    PhoneNumber newPhoneNumber = new PhoneNumber(newNumber);
                    phoneNumberSessionBeanLocal.createNewPhoneNumber(newPhoneNumber);
                } catch (PhoneNumberExistException | UnknownPersistenceException ex) {
                    i--;
                    continue;
                }
            }
        }
    }
}
