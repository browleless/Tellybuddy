package ejb.session.stateless;

import entity.Bill;
import entity.Customer;
import entity.Payment;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;
import util.exception.BillAlreadyPaidException;
import util.exception.BillNotFoundException;
import util.exception.CustomerStoredCreditCardException;
import util.exception.DeletePaymentException;
import util.exception.PaymentNotFoundException;

/**
 *
 * @author tjle2
 */
@Local
public interface PaymentSessionBeanLocal {

    public Long createNewBillPayment(Payment payment, Bill bill) throws BillAlreadyPaidException, BillNotFoundException;

    public Payment createNewPayment(String creditCardNo, String cvv, BigDecimal amount);

//    public Long automateBillPayment(Bill bill) throws BillAlreadyPaidException, CustomerStoredCreditCardException, BillNotFoundException;

    public Payment retrievePaymentByPaymentId(Long paymentId) throws PaymentNotFoundException;

    public List<Payment> retrieveAllPayments();

    public List<Payment> retrievePaymentsBetweenDates(Date startDate, Date endDate);

    public void deletePayment(Payment payment) throws PaymentNotFoundException, DeletePaymentException;

    public List<Payment> retrieveCustomerPayments(Customer customer);

}
