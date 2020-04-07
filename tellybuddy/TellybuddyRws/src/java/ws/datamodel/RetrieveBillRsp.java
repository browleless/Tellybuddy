package ws.datamodel;

import entity.Bill;

/**
 *
 * @author tjle2
 */
public class RetrieveBillRsp {
    
    private Bill bill;

    public RetrieveBillRsp() {
    }

    public RetrieveBillRsp(Bill bill) {
        this.bill = bill;
    }

    public Bill getBill() {
        return bill;
    }

    public void setBill(Bill bill) {
        this.bill = bill;
    }
}
