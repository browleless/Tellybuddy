package ejb.session.stateless;

import entity.Bill;
import entity.Customer;
import entity.Payment;
import java.math.BigDecimal;
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
    public Long createNewBillPayment(Payment payment, Bill bill) throws BillAlreadyPaidException, BillNotFoundException {

        if (bill.getPaid()) {
            throw new BillAlreadyPaidException("Bill id " + bill.getBillId() + " has already been paid for");
        } else {
            Bill billToPay = billSessionBeanLocal.retrieveBillByBillId(bill.getBillId());

            Payment newPayment = new Payment(payment.getCreditCardNumber(), payment.getCvv(), new Date(), payment.getAmount());
            billToPay.getCustomer().setLoyaltyPoints(billToPay.getCustomer().getLoyaltyPoints() - (billToPay.getPrice().add(billToPay.getAddOnPrice()).add(billToPay.getExceedPenaltyPrice()).multiply(BigDecimal.valueOf((double) (100 - billToPay.getFamilyDiscountRate()) / 100))).subtract(payment.getAmount()).intValue());

            entityManager.persist(newPayment);
            entityManager.flush();
            billToPay.setPayment(newPayment);
            billToPay.setPaid(Boolean.TRUE);

            return newPayment.getPaymentId();
        }
    }

    @Override
    public Payment createNewPayment(String creditCardNo, String cvv, BigDecimal amount) {

        Payment newPayment = new Payment(creditCardNo, cvv, new Date(), amount);

        entityManager.persist(newPayment);
        entityManager.flush();

        return newPayment;
    }

//    @Override
//    public Long automateBillPayment(Bill bill) throws BillAlreadyPaidException, CustomerStoredCreditCardException, BillNotFoundException {
//
//        if (bill.getPaid()) {
//            throw new BillAlreadyPaidException("Bill id " + bill.getBillId() + " has already been paid for");
//        } else {
//            Bill billToPay = billSessionBeanLocal.retrieveBillByBillId(bill.getBillId());
//            Customer customer = billToPay.getCustomer();
//
//            if ((customer.getCreditCardNumber() == null && customer.getCvv() == null && customer.getCreditCardExpiryDate() == null) || customer.getCreditCardExpiryDate().before(new Date())) {
//                throw new CustomerStoredCreditCardException("Customer either has no saved credit card or credit card has expired!");
//            }
//
//            Payment newPayment = new Payment(customer.getCreditCardNumber(), customer.getCvv(), new Date(), billToPay.getPrice().add(billToPay.getAddOnPrice()).add(billToPay.getExceedPenaltyPrice()));
//
//            entityManager.persist(newPayment);
//            entityManager.flush();
//            billToPay.setPayment(newPayment);
//            billToPay.setPaid(Boolean.TRUE);
//
//            return newPayment.getPaymentId();
//        }
//    }
    @Override
    public Payment retrievePaymentByPaymentId(Long paymentId) throws PaymentNotFoundException {

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
    public List<Payment> retrieveCustomerPayments(Customer customer) {

        Query query = entityManager.createQuery("SELECT p FROM Payment p WHERE EXISTS (SELECT b FROM Bill b WHERE b.payment = p AND b.customer = :inCustomer) OR EXISTS (SELECT t FROM Transaction t WHERE t.payment = p AND t.customer = :inCustomer) ORDER BY p.datePaid ASC");
        query.setParameter("inCustomer", customer);

        return query.getResultList();
    }

    @Override
    public void deletePayment(Payment payment) throws PaymentNotFoundException, DeletePaymentException {

        try {
            Payment paymentToDelete = retrievePaymentByPaymentId(payment.getPaymentId());

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
