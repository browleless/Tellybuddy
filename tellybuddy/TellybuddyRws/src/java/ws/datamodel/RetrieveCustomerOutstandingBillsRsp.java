package ws.datamodel;

import entity.Bill;
import java.util.List;

/**
 *
 * @author tjle2
 */
public class RetrieveCustomerOutstandingBillsRsp {
    
    private List<Bill> bills;

    public RetrieveCustomerOutstandingBillsRsp() {
    }

    public RetrieveCustomerOutstandingBillsRsp(List<Bill> bills) {
        this.bills = bills;
    }

    public List<Bill> getBills() {
        return bills;
    }

    public void setBills(List<Bill> bills) {
        this.bills = bills;
    }
}
