/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Product;
import entity.ProductItem;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.ProductItemNotFoundException;
import util.exception.ProductNotFoundException;

/**
 *
 * @author kaikai
 */
@Stateless
public class ProductItemSessionBean implements ProductItemSessionBeanLocal {

    @PersistenceContext(unitName = "tellybuddy-ejbPU")
    private EntityManager em;




//    @Override
//    public ProductItem createNewProductItem(ProductItem){
//        
//    }

    @Override
    public ProductItem retrieveProductItemByProductItemId(Long itemId) throws ProductItemNotFoundException {
        ProductItem productItem = em.find(ProductItem.class, itemId);

        if (productItem != null) {
            productItem.getLuxuryProduct();

            return productItem;
        } else {
            throw new ProductItemNotFoundException("Product ID " + itemId + " does not exist!");
        }
    }

    @Override
    public List<ProductItem> retrieveAllProductItemUnderLuxuryProduct(Long luxuryProductId) throws ProductNotFoundException {

        Query query = em.createQuery("SELECT pi FROM ProductItem pi WHERE pi.luxuryProduct.productId = :inLuxuryProductId");
        query.setParameter("inLuxuryProductId", luxuryProductId);

        List<ProductItem> productItems = query.getResultList();
        return productItems;
    }
}
