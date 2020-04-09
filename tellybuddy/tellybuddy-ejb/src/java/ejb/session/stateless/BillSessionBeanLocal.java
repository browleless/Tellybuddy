/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Bill;
import entity.Customer;
import entity.UsageDetail;
import java.util.List;
import javax.ejb.Local;
import util.exception.BillNotFoundException;
import util.exception.CustomerNotFoundException;
import util.exception.UsageDetailNotFoundException;

/**
 *
 * @author tjle2
 */
@Local
public interface BillSessionBeanLocal {

    public Bill retrieveBillByBillId(Long billId) throws BillNotFoundException;

    public Bill createNewBill(Bill newBill, UsageDetail usageDetail, Customer customer) throws CustomerNotFoundException, UsageDetailNotFoundException;

    public List<Bill> retrieveBillByCustomer(Customer customer);
    
}
