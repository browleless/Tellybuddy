/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.datamodel;

import entity.Customer;
import java.util.List;

/**
 *
 * @author kaikai
 */
public class RetrieveCustomerFromFamilyGroupIdRsp {
    private List<Customer> customers;

    public RetrieveCustomerFromFamilyGroupIdRsp() {
    }

    public RetrieveCustomerFromFamilyGroupIdRsp(List<Customer> customers) {
        this.customers = customers;
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
    }

    
    
}
