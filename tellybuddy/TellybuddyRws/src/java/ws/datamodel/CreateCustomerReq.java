package ws.datamodel;

import entity.Customer;

/**
 *
 * @author tjle2
 */
public class CreateCustomerReq {
    
    private Customer customer;

    public CreateCustomerReq() {
    }

    public CreateCustomerReq(Customer customer) {
        this.customer = customer;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
