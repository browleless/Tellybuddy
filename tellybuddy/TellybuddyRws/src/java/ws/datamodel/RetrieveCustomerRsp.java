package ws.datamodel;

import entity.Customer;

/**
 *
 * @author tjle2
 */
public class RetrieveCustomerRsp {
    
    private Customer customer;

    public RetrieveCustomerRsp() {
    }

    public RetrieveCustomerRsp(Customer customer) {
        this.customer = customer;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
