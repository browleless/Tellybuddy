package ws.datamodel;

import entity.Customer;

/**
 *
 * @author tjle2
 */
public class ChangePasswordReq {
    
    private Customer customer;

    public ChangePasswordReq() {
    }

    public ChangePasswordReq(Customer customer) {
        this.customer = customer;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
