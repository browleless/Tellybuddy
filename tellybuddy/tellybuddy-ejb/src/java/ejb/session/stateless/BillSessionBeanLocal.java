/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Bill;
import javax.ejb.Local;
import util.exception.BillNotFoundException;

/**
 *
 * @author tjle2
 */
@Local
public interface BillSessionBeanLocal {

    public Bill retrieveBillById(Long billId) throws BillNotFoundException;
    
}
