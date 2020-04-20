package ws.datamodel;

import entity.Bill;
import java.util.List;

/**
 *
 * @author tjle2
 */
public class RetrieveSubscriptionOutstandingBillsRsp {

    private List<Bill> bills;

    public RetrieveSubscriptionOutstandingBillsRsp() {
    }

    public RetrieveSubscriptionOutstandingBillsRsp(List<Bill> bills) {
        this.bills = bills;
    }

    public List<Bill> getBills() {
        return bills;
    }

    public void setBills(List<Bill> bills) {
        this.bills = bills;
    }
}
