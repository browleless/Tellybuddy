package ejb.session.stateless;

import entity.Bill;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.exception.BillNotFoundException;

/**
 *
 * @author tjle2
 */
@Stateless
public class BillSessionBean implements BillSessionBeanLocal {

    @PersistenceContext(unitName = "tellybuddy-ejbPU")
    private EntityManager entityManager;

    @Override
    public Bill retrieveBillById(Long billId) throws BillNotFoundException {
        
        Bill bill = entityManager.find(Bill.class, billId);

        if (bill != null) {
            return bill;
        } else {
            throw new BillNotFoundException("Bill ID " + billId + " does not exist!");
        }
    }
}
