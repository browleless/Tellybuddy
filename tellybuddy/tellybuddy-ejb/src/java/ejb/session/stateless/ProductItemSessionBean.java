/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.LuxuryProduct;
import entity.ProductItem;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exception.InputDataValidationException;
import util.exception.ProductItemExistException;
import util.exception.ProductItemNotFoundException;
import util.exception.ProductNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author kaikai
 */
@Stateless
public class ProductItemSessionBean implements ProductItemSessionBeanLocal {

    @EJB(name = "ProductSessionBeanLocal")
    private ProductSessionBeanLocal productSessionBeanLocal;

    @PersistenceContext(unitName = "tellybuddy-ejbPU")
    private EntityManager em;
    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public ProductItemSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public ProductItem createNewProductItem(ProductItem newProductItem, Long luxuryProductId) throws InputDataValidationException, UnknownPersistenceException, ProductItemExistException {
        Set<ConstraintViolation<ProductItem>> constraintViolations = validator.validate(newProductItem);

        if (constraintViolations.isEmpty()) {
            try {
                if (luxuryProductId != null) {
                    LuxuryProduct luxuryProduct = (LuxuryProduct) productSessionBeanLocal.retrieveProductByProductId(luxuryProductId);
                    newProductItem.setLuxuryProduct(luxuryProduct);
                    luxuryProduct.getProductItems().add(newProductItem);
                }

                em.persist(newProductItem);
                em.flush();

                return newProductItem;
            } catch (PersistenceException ex) {
                if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                        throw new ProductItemExistException();
                    } else {
                        throw new UnknownPersistenceException(ex.getMessage());
                    }
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            } catch (ProductNotFoundException ex) {
                Logger.getLogger(ProductItemSessionBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }

    @Override
    public ProductItem retrieveProductItemByProductItemId(Long itemId) throws ProductItemNotFoundException {
        ProductItem productItem = em.find(ProductItem.class, itemId);

        if (productItem != null) {
            productItem.getLuxuryProduct();

            return productItem;
        } else {
            throw new ProductItemNotFoundException("Product item ID " + itemId + " does not exist!");
        }
    }

    @Override
    public List<ProductItem> retrieveAllProductItemUnderLuxuryProduct(Long luxuryProductId) throws ProductNotFoundException {

        Query query = em.createQuery("SELECT pi FROM ProductItem pi WHERE pi.luxuryProduct.productId = :inLuxuryProductId");
        query.setParameter("inLuxuryProductId", luxuryProductId);

        List<ProductItem> productItems = query.getResultList();
        return productItems;
    }

    @Override
    public String retrieveLatestSerialNum() {
        Query q = em.createQuery("SELECT p FROM ProductItem p ORDER BY p.serialNumber desc");

        ProductItem pi = (ProductItem) q.getResultList().get(0);

        if (pi == null) {
            return "0000000000";
        } else {
            return pi.getSerialNumber();
        }

    }

//    public ProductItem retrieveListOfProductItemByTransactionLineItemId(Long transaction){
//        
//    }
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<ProductItem>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }
}
