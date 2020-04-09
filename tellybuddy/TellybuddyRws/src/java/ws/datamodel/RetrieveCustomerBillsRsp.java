package ws.datamodel;

import entity.Bill;
import java.util.List;

/**
 *
 * @author tjle2
 */
public class RetrieveCustomerBillsRsp {
    
    private List<Bill> bills;

    public RetrieveCustomerBillsRsp() {
    }

    public RetrieveCustomerBillsRsp(List<Bill> bills) {
        this.bills = bills;
    }

    public List<Bill> getBills() {
        return bills;
    }

    public void setBills(List<Bill> bills) {
        this.bills = bills;
    }
}
