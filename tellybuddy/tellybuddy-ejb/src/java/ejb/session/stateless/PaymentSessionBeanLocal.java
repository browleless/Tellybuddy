package ejb.session.stateless;

import entity.Bill;
import entity.Payment;
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

    public Long createNewPayment(Payment newPayment);

    public Long createNewBillPayment(Bill bill) throws BillAlreadyPaidException, CustomerStoredCreditCardException, BillNotFoundException;

    public Payment retrievePaymentById(Long paymentId) throws PaymentNotFoundException;

    public List<Payment> retrieveAllPayments();

    public List<Payment> retrievePaymentsBetweenDates(Date startDate, Date endDate);

    public void deletePayment(Payment payment) throws PaymentNotFoundException, DeletePaymentException;

}
