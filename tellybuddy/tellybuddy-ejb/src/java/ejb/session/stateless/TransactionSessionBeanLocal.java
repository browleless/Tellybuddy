/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.TransactionLineItem;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author kaikai
 */
@Local
public interface TransactionSessionBeanLocal {
    
    public List<TransactionLineItem> retrieveTransactionLineItemsByProductId(Long productId);
}
