/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.datamodel;

import entity.Customer;

/**
 *
 * @author kaikai
 */
public class RetrieveCurrentCustomerRsp {
    private Customer customer;

    public RetrieveCurrentCustomerRsp() {
    }

    public RetrieveCurrentCustomerRsp(Customer customer) {
        this.customer = customer;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
    
    
}
