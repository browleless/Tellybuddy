package ejb.session.stateless;

import entity.Bill;
import entity.Customer;
import entity.Subscription;
import entity.UsageDetail;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
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

    @Override
    public List<Bill> retrieveBillByCustomer(Customer customer) {

        Query query = entityManager.createQuery("SELECT b FROM Bill b WHERE b.customer = :inCustomer");
        query.setParameter("inCustomer", customer);

        return query.getResultList();
    }

    @Override
    public List<Bill> retrieveBillsBySubscription(Subscription subscription) {

        Query query = entityManager.createQuery("SELECT b FROM Bill b WHERE b.usageDetail.subscription = :inSubscription ORDER BY b.date ASC");
        query.setParameter("inSubscription", subscription);

        return query.getResultList();
    }
    
    @Override
    public List<Bill> retrieveCustomerOutstandingBills(Customer customer) {

        Query query = entityManager.createQuery("SELECT b FROM Bill b WHERE b.customer = :inCustomer AND b.paid = FALSE");
        query.setParameter("inCustomer", customer);

        return query.getResultList();
    }
    
    @Override
    public List<Bill> retrieveSubscriptionOutstandingBills(Subscription subscription) {

        Query query = entityManager.createQuery("SELECT b FROM Bill b WHERE b.usageDetail.subscription = :inSubscription AND b.paid = FALSE");
        query.setParameter("inSubscription", subscription);

        return query.getResultList();
    }
}
