package ejb.session.stateless;

import entity.Bill;
import entity.Customer;
import entity.UsageDetail;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.exception.BillNotFoundException;
import util.exception.CustomerNotFoundException;
import util.exception.UsageDetailNotFoundException;

/**
 *
 * @author tjle2
 */
@Stateless
public class BillSessionBean implements BillSessionBeanLocal {

    @EJB
    private UsageDetailSessionBeanLocal usageDetailSessionBeanLocal;

    @EJB
    private CustomerSessionBeanLocal customerSessionBeanLocal;

    @PersistenceContext(unitName = "tellybuddy-ejbPU")
    private EntityManager entityManager;

    @Override
    public Bill createNewBill(Bill newBill, UsageDetail usageDetail, Customer customer) throws CustomerNotFoundException, UsageDetailNotFoundException {
        
        Customer customerToAssociateWith = customerSessionBeanLocal.retrieveCustomerByCustomerId(customer.getCustomerId());
        UsageDetail usageDetailToAssociateWith = usageDetailSessionBeanLocal.retrieveUsageDetailByUsageDetailId(usageDetail.getUsageDetailId());
        
        newBill.setCustomer(customerToAssociateWith);
        newBill.setUsageDetail(usageDetailToAssociateWith);
        
        entityManager.persist(newBill);
        entityManager.flush();
        
        customerToAssociateWith.getBills().add(newBill);
        usageDetailToAssociateWith.setBill(newBill);
        
        return newBill;
    }
    
    @Override
    public Bill retrieveBillByBillId(Long billId) throws BillNotFoundException {
        
        Bill bill = entityManager.find(Bill.class, billId);

        if (bill != null) {
            return bill;
        } else {
            throw new BillNotFoundException("Bill ID " + billId + " does not exist!");
        }
    }
}
