package ws.datamodel;

import entity.Customer;

/**
 *
 * @author tjle2
 */
public class RetrieveCustomerOutstandingBillsReq {
    
    private String username;
    private String password;
    private Customer customer;

    public RetrieveCustomerOutstandingBillsReq() {
    }

    public RetrieveCustomerOutstandingBillsReq(String username, String password, Customer customer) {
        this.username = username;
        this.password = password;
        this.customer = customer;
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

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
