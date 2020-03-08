/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.TransactionLineItem;
import java.util.List;
import javax.ejb.Stateless;

/**
 *
 * @author kaikai
 */
@Stateless
public class TransactionSessionBean implements TransactionSessionBeanLocal {

    @Override
    public List<TransactionLineItem> retrieveTransactionLineItemsByProductId(Long productId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
}
