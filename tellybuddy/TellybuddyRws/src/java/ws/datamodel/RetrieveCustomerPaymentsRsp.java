package ws.datamodel;

import entity.Payment;
import java.util.List;

/**
 *
 * @author tjle2
 */
public class RetrieveCustomerPaymentsRsp {
    
    private List<Payment> payments;

    public RetrieveCustomerPaymentsRsp() {
    }

    public RetrieveCustomerPaymentsRsp(List<Payment> payments) {
        this.payments = payments;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }
}
