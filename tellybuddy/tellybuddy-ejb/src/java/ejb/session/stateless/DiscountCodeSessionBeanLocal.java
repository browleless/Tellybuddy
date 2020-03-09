/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.DiscountCode;
import javax.ejb.Local;

/**
 *
 * @author markt
 */
public interface DiscountCodeSessionBeanLocal {
        public DiscountCode retrieveDiscountCodeByName(String discountCodeName);
}
