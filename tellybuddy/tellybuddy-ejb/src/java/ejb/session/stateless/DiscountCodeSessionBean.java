/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.DiscountCode;
import javax.ejb.Local;
import javax.ejb.Stateless;

/**
 *
 * @author markt
 */
@Stateless
@Local(DiscountCodeSessionBeanLocal.class)
public class DiscountCodeSessionBean implements DiscountCodeSessionBeanLocal {

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @Override
    public DiscountCode retrieveDiscountCodeByName(String discountCodeName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
