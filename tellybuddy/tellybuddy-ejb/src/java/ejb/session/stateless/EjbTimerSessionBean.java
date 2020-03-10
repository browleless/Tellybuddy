/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Product;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Stateless;

/**
 *
 * @author kaikai
 */
@Stateless
public class EjbTimerSessionBean implements EjbTimerSessionBeanLocal {

    @EJB(name = "ProductSessionBeanLocal")
    private ProductSessionBeanLocal productSessionBeanLocal;

    
    @Schedule(hour = "*", minute = "*/5", info = "productEntityReorderQuantityCheckTimer")
    public void productReorderQuantityCheckTimer() {

        List<Product> products = productSessionBeanLocal.retrieveAllProducts();
        for (Product product : products) {
            if (product.getQuantityOnHand().compareTo(product.getReorderQuantity()) <= 0) {
                System.out.println("********** Product " + product.getSkuCode() + " requires reordering: QOH = " + product.getQuantityOnHand() + "; RQ = " + product.getReorderQuantity());
            }
        }
    }

    
}
