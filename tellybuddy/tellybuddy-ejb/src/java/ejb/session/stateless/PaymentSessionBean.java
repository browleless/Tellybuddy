package ejb.session.stateless;

import entity.Bill;
import entity.Customer;
import entity.Payment;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.BillAlreadyPaidException;
import util.exception.BillNotFoundException;
import util.exception.CustomerStoredCreditCardException;
import util.exception.DeletePaymentException;
import util.exception.PaymentNotFoundException;

/**
 *
 * @author tjle2
 */
@Stateless
public class PaymentSessionBean implements PaymentSessionBeanLocal {

    @EJB
    private BillSessionBeanLocal billSessionBeanLocal;

    @PersistenceContext(unitName = "tellybuddy-ejbPU")
    private EntityManager entityManager;

    @Override
    public Long createNewPayment(Payment newPayment) {

        entityManager.persist(newPayment);
        entityManager.flush();

        return newPayment.getPaymentId();
    }

    @Override
    public Long createNewBillPayment(Bill bill) throws BillAlreadyPaidException, CustomerStoredCreditCardException, BillNotFoundException {

        if (bill.getPaid()) {
            throw new BillAlreadyPaidException("Bill id " + bill.getBillId() + " has already been paid for");
        } else {
            Customer customer = bill.getCustomer();
            Bill billToPay = billSessionBeanLocal.retrieveBillById(bill.getBillId());

            if ((customer.getCreditCardNumber() == null && customer.getCvv() == null && customer.getCreditCardExpiryDate() == null) || customer.getCreditCardExpiryDate().before(new Date())) {
                throw new CustomerStoredCreditCardException("Customer either has no saved credit card or credit card has expired!");
            }

            Payment newPayment = new Payment(customer.getCreditCardNumber(), customer.getCvv(), new Date(), bill.getPrice());

            entityManager.persist(newPayment);
            entityManager.flush();
            billToPay.setPayment(newPayment);
            billToPay.setPaid(Boolean.TRUE);

            return newPayment.getPaymentId();
        }
    }

    @Override
    public Payment retrievePaymentById(Long paymentId) throws PaymentNotFoundException {

        Payment payment = entityManager.find(Payment.class, paymentId);

        if (payment != null) {
            return payment;
        } else {
            throw new PaymentNotFoundException("Payment ID " + paymentId + " does not exist!");
        }
    }

    @Override
    public List<Payment> retrieveAllPayments() {

        Query query = entityManager.createQuery("SELECT p FROM Payment p ORDER BY p.datePaid");
        return query.getResultList();
    }

    @Override
    public List<Payment> retrievePaymentsBetweenDates(Date startDate, Date endDate) {

        Query query = entityManager.createQuery("SELECT p FROM Payment p WHERE p.datePaid BETWEEN :inStartDate AND :inEndDate ORDER BY p.datePaid");
        query.setParameter("inStartDate", startDate);
        query.setParameter("inEndDate", endDate);

        return query.getResultList();
    }

    @Override
    public void deletePayment(Payment payment) throws PaymentNotFoundException, DeletePaymentException {

        try {
            Payment paymentToDelete = retrievePaymentById(payment.getPaymentId());

            Query query = entityManager.createQuery("SELECT b FROM Bill b WHERE b.payment = :inPayment");
            query.setParameter("inPayment", paymentToDelete);

            if (query.getSingleResult() != null) {
                throw new DeletePaymentException("Payment still tagged to a Bill!");
            }

            query = entityManager.createQuery("SELECT t FROM Transaction t WHERE t.payment = :inPayment");
            query.setParameter("inPayment", paymentToDelete);

            if (query.getSingleResult() != null) {
                throw new DeletePaymentException("Payment still tagged to a Transaction!");
            }

            entityManager.remove(paymentToDelete);

        } catch (PaymentNotFoundException ex) {
            throw new PaymentNotFoundException("Payment Id not provided for delete");
        }
    }
}
