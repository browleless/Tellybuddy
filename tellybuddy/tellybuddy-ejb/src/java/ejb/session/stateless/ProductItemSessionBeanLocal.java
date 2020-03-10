/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.ProductItem;
import java.util.List;
import javax.ejb.Local;
import util.exception.InputDataValidationException;
import util.exception.ProductItemExistException;
import util.exception.ProductItemNotFoundException;
import util.exception.ProductNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author kaikai
 */
@Local
public interface ProductItemSessionBeanLocal {

    public ProductItem retrieveProductItemByProductItemId(Long itemId) throws ProductItemNotFoundException;

    public List<ProductItem> retrieveAllProductItemUnderLuxuryProduct(Long luxuryProductId) throws ProductNotFoundException;

    public ProductItem createNewProductItem(ProductItem newProductItem, Long luxuryProductId) throws InputDataValidationException, UnknownPersistenceException, ProductItemExistException;
}
