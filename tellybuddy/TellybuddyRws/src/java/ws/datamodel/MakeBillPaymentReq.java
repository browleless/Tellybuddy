package ws.datamodel;

import entity.Bill;
import entity.Payment;

/**
 *
 * @author tjle2
 */
public class MakeBillPaymentReq {
    
    private String username;
    private String password;
    private Payment payment;
    private Bill bill;

    public MakeBillPaymentReq() {
    }

    public MakeBillPaymentReq(String username, String password, Bill bill, Payment payment) {
        this.username = username;
        this.password = password;
        this.bill = bill;
        this.payment = payment;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Bill getBill() {
        return bill;
    }

    public void setBill(Bill bill) {
        this.bill = bill;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }
}
