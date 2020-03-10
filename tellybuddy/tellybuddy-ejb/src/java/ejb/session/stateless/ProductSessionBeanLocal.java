/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.LuxuryProduct;
import entity.Product;
import entity.ProductItem;
import java.util.List;
import javax.ejb.Local;
import util.exception.CategoryNotFoundException;
import util.exception.CreateNewProductException;
import util.exception.DeleteProductException;
import util.exception.InputDataValidationException;
import util.exception.ProductInsufficientQuantityOnHandException;
import util.exception.ProductItemNotFoundException;
import util.exception.ProductNotFoundException;
import util.exception.ProductSkuCodeExistException;
import util.exception.SerialNumberExistException;
import util.exception.TagNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateProductException;

/**
 *
 * @author kaikai
 */
@Local
public interface ProductSessionBeanLocal {

    public List<Product> retrieveAllProducts();

    public Product createNewProduct(Product newProduct, Long categoryId, List<Long> tagIds) throws ProductSkuCodeExistException, UnknownPersistenceException, InputDataValidationException, CreateNewProductException;

    public List<Product> searchProductsByName(String searchString);

    public List<Product> filterProductsByCategory(Long categoryId) throws CategoryNotFoundException;

    public List<Product> filterProductsByTags(List<Long> tagIds, String condition);

    public Product retrieveProductByProductId(Long productId) throws ProductNotFoundException;

    public Product retrieveProductByProductSkuCode(String skuCode) throws ProductNotFoundException;

    public void updateProduct(Product product, Long categoryId, List<Long> tagIds) throws ProductNotFoundException, CategoryNotFoundException, TagNotFoundException, UpdateProductException, InputDataValidationException;

    public void deleteProduct(Long productId) throws ProductNotFoundException, DeleteProductException;

    public void debitQuantityOnHand(Long productId, Integer quantityToDebit) throws ProductNotFoundException, ProductInsufficientQuantityOnHandException;

    public void creditQuantityOnHand(Long productId, Integer quantityToCredit) throws ProductNotFoundException;

    public void updateLuxuryProduct(LuxuryProduct luxuryProduct, Long categoryId, List<Long> tagIds, List<Long> itemIds) throws ProductNotFoundException, CategoryNotFoundException, TagNotFoundException, UpdateProductException, InputDataValidationException,ProductItemNotFoundException;

    public void creditQuantityOnHandForLuxuryProduct(Long productId, Integer quantityToCredit, List<ProductItem> pis) throws ProductNotFoundException;

 
    
    
}
