/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Category;
import entity.LuxuryProduct;
import entity.Product;
import entity.ProductItem;
import entity.Tag;
import entity.TransactionLineItem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
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
@Stateless
public class ProductSessionBean implements ProductSessionBeanLocal {

    @EJB(name = "ProductItemSessionBeanLocal")
    private ProductItemSessionBeanLocal productItemSessionBeanLocal;

    @EJB(name = "TransactionSessionBeanLocal")
    private TransactionSessionBeanLocal transactionSessionBeanLocal;

    @EJB(name = "TagSessionBeanLocal")
    private TagSessionBeanLocal tagSessionBeanLocal;

    @EJB(name = "CategorySessionBeanLocal")
    private CategorySessionBeanLocal categorySessionBeanLocal;

    @PersistenceContext(unitName = "tellybuddy-ejbPU")
    private EntityManager em;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public ProductSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public Product createNewProduct(Product newProduct, Long categoryId, List<Long> tagIds) throws ProductSkuCodeExistException, UnknownPersistenceException, InputDataValidationException, CreateNewProductException {
        Set<ConstraintViolation<Product>> constraintViolations = validator.validate(newProduct);

        if (constraintViolations.isEmpty()) {
            try {
                if (categoryId == null) {
                    throw new CreateNewProductException("The new product must be associated a leaf category");
                }

                Category category = categorySessionBeanLocal.retrieveCategoryByCategoryId(categoryId);

                if (!category.getSubCategories().isEmpty()) {
                    throw new CreateNewProductException("Selected category for the new product is not a leaf category");
                }
                //associate newProduct with category
                newProduct.setCategory(category);

                if (tagIds != null && (!tagIds.isEmpty())) {
                    for (Long tagId : tagIds) {
                        Tag tagEntity = tagSessionBeanLocal.retrieveTagByTagId(tagId);
                        //associate newProduct with tags
                        newProduct.addTag(tagEntity);
                    }
                }
                em.persist(newProduct);
                em.flush();

                return newProduct;
            } catch (PersistenceException ex) {
                if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                        throw new ProductSkuCodeExistException();
                    } else {
                        throw new UnknownPersistenceException(ex.getMessage());
                    }
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            } catch (CategoryNotFoundException | TagNotFoundException ex) {
                throw new CreateNewProductException("An error has occurred while creating the new product: " + ex.getMessage());
            }
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }

    @Override
    public List<Product> retrieveAllProducts() {
        Query query = em.createQuery("SELECT p FROM Product p ORDER BY p.skuCode ASC");
        List<Product> products = query.getResultList();

        for (Product product : products) {
            product.getCategory();
        }

        return products;
    }

    @Override
    public List<Product> searchProductsByName(String searchString) {
        Query query = em.createQuery("SELECT p FROM Product p WHERE p.name LIKE :inSearchString ORDER BY p.skuCode ASC");
        query.setParameter("inSearchString", "%" + searchString + "%");
        List<Product> products = query.getResultList();

        //lazy loading
        for (Product product : products) {
            product.getCategory();
            product.getTags().size();
        }

        return products;
    }

    @Override
    public List<Product> filterProductsByCategory(Long categoryId) throws CategoryNotFoundException {
        List<Product> products = new ArrayList<>();
        Category category = categorySessionBeanLocal.retrieveCategoryByCategoryId(categoryId);

        if (category.getSubCategories().isEmpty()) {
            products = category.getProducts();
        } else {
            //parent categories
            for (Category subCategory : category.getSubCategories()) {
                products.addAll(addSubCategoryProducts(subCategory));
            }
        }

        for (Product product : products) {
            product.getCategory();
            product.getTags().size();
        }

        Collections.sort(products, new Comparator<Product>() {
            public int compare(Product p1, Product p2) {
                return p1.getSkuCode().compareTo(p2.getSkuCode());
            }
        });

        return products;
    }

//recurssion
    private List<Product> addSubCategoryProducts(Category category) {
        List<Product> products = new ArrayList<>();

        if (category.getSubCategories().isEmpty()) {
            return category.getProducts();
        } else {
            for (Category subCategory : category.getSubCategories()) {
                products.addAll(addSubCategoryProducts(subCategory));
            }

            return products;
        }
    }

    @Override
    public List<Product> filterProductsByTags(List<Long> tagIds, String condition) {
        List<Product> products = new ArrayList<>();

        if (tagIds == null || tagIds.isEmpty() || (!condition.equals("AND") && !condition.equals("OR"))) {
            return products;
        } else {
            if (condition.equals("OR")) {
                Query query = em.createQuery("SELECT DISTINCT p FROM Product p, IN (p.tags) t WHERE t.tagId IN :inTagIds ORDER BY p.skuCode ASC");
                query.setParameter("inTagIds", tagIds);
                products = query.getResultList();
            } else // AND
            {
                String selectClause = "SELECT p FROM Product p";
                String whereClause = "";
                Boolean firstTag = true;
                Integer tagCount = 1;

                for (Long tagId : tagIds) {
                    selectClause += ", IN (p.tags) t" + tagCount;

                    if (firstTag) {
                        whereClause = "WHERE t1.tagId = " + tagId;
                        firstTag = false;
                    } else {
                        whereClause += " AND t" + tagCount + ".tagId = " + tagId;
                    }

                    tagCount++;
                }

                String jpql = selectClause + " " + whereClause + " ORDER BY p.skuCode ASC";
                Query query = em.createQuery(jpql);
                products = query.getResultList();
            }

            for (Product product : products) {
                product.getCategory();
                product.getTags().size();
            }

            Collections.sort(products, new Comparator<Product>() {
                public int compare(Product p1, Product p2) {
                    return p1.getSkuCode().compareTo(p2.getSkuCode());
                }
            });

            return products;
        }
    }

    @Override
    public Product retrieveProductByProductId(Long productId) throws ProductNotFoundException {
        Product product = em.find(Product.class, productId);

        if (product != null) {
            product.getCategory();
            product.getTags().size();

            return product;
        } else {
            throw new ProductNotFoundException("Product ID " + productId + " does not exist!");
        }
    }

    @Override
    public Product retrieveProductByProductSkuCode(String skuCode) throws ProductNotFoundException {
        Query query = em.createQuery("SELECT p FROM Product p WHERE p.skuCode = :inSkuCode");
        query.setParameter("inSkuCode", skuCode);

        try {
            Product product = (Product) query.getSingleResult();
            product.getCategory();
            product.getTags().size();

            return product;
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new ProductNotFoundException("Sku Code " + skuCode + " does not exist!");
        }
    }

    @Override
    public void updateProduct(Product product, Long categoryId, List<Long> tagIds) throws ProductNotFoundException, CategoryNotFoundException, TagNotFoundException, UpdateProductException, InputDataValidationException {
        if (product != null && product.getProductId() != null) {
            Set<ConstraintViolation<Product>> constraintViolations = validator.validate(product);

            if (constraintViolations.isEmpty()) {
                Product productToUpdate = retrieveProductByProductId(product.getProductId());

                if (productToUpdate.getSkuCode().equals(product.getSkuCode())) {
                    if (categoryId != null && (!productToUpdate.getCategory().getCategoryId().equals(categoryId))) {
                        Category categoryToUpdate = categorySessionBeanLocal.retrieveCategoryByCategoryId(categoryId);

                        if (!categoryToUpdate.getSubCategories().isEmpty()) {
                            throw new UpdateProductException("Selected category for the new product is not a leaf category");
                        }

                        productToUpdate.setCategory(categoryToUpdate);
                    }

                    if (tagIds != null) {
                        for (Tag tag : productToUpdate.getTags()) {
                            tag.getProducts().remove(productToUpdate);
                        }

                        productToUpdate.getTags().clear();

                        for (Long tagId : tagIds) {
                            Tag tagEntity = tagSessionBeanLocal.retrieveTagByTagId(tagId);
                            productToUpdate.addTag(tagEntity);
                        }
                    }
                    productToUpdate.setName(product.getName());
                    productToUpdate.setDescription(product.getDescription());
                    productToUpdate.setQuantityOnHand(product.getQuantityOnHand());
                    productToUpdate.setPrice(product.getPrice());
                    productToUpdate.setReorderQuantity(product.getReorderQuantity());
                    //productToUpdate.setProductRating((product.getProductRating()));
                } else {
                    throw new UpdateProductException("SKU Code of product record to be updated does not match the existing record");
                }
            } else {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }
        } else {
            throw new ProductNotFoundException("Product ID not provided for product to be updated");
        }
    }

    @Override
    public void updateLuxuryProduct(LuxuryProduct luxuryProduct, Long categoryId, List<Long> tagIds, List<Long> itemIds) throws ProductNotFoundException, CategoryNotFoundException, TagNotFoundException, UpdateProductException, InputDataValidationException, ProductItemNotFoundException {
        if (luxuryProduct != null && luxuryProduct.getProductId() != null) {
            Set<ConstraintViolation<Product>> constraintViolations = validator.validate(luxuryProduct);

            if (constraintViolations.isEmpty()) {
                LuxuryProduct luxuryProductToUpdate = (LuxuryProduct) retrieveProductByProductId(luxuryProduct.getProductId());

                if (luxuryProductToUpdate.getSkuCode().equals(luxuryProduct.getSkuCode())) {
                    if (categoryId != null && (!luxuryProductToUpdate.getCategory().getCategoryId().equals(categoryId))) {
                        Category categoryToUpdate = categorySessionBeanLocal.retrieveCategoryByCategoryId(categoryId);

                        if (!categoryToUpdate.getSubCategories().isEmpty()) {
                            throw new UpdateProductException("Selected category for the new product is not a leaf category");
                        }

                        luxuryProductToUpdate.setCategory(categoryToUpdate);
                    }

                    if (tagIds != null) {
                        for (Tag tag : luxuryProductToUpdate.getTags()) {
                            tag.getProducts().remove(luxuryProductToUpdate);
                        }

                        luxuryProductToUpdate.getTags().clear();

                        for (Long tagId : tagIds) {
                            Tag tagEntity = tagSessionBeanLocal.retrieveTagByTagId(tagId);
                            luxuryProductToUpdate.addTag(tagEntity);
                        }
                    }

                    //luxuryProduct need to remove productItem
                    if (itemIds != null) {
                        for (ProductItem productItem : luxuryProductToUpdate.getProductItems()) {
                            productItem.setLuxuryProduct(null);
                        }
                        luxuryProductToUpdate.getProductItems().clear();

                        for (Long itemId : itemIds) {
                            try {
                                ProductItem productItem = productItemSessionBeanLocal.retrieveProductItemByProductItemId(itemId);
                                luxuryProductToUpdate.getProductItems().add(productItem);
                            } catch (ProductItemNotFoundException ex) {
                                throw new ProductItemNotFoundException("product item entered does not have existing product item");
                            }
                        }
                    }

                    luxuryProductToUpdate.setName(luxuryProduct.getName());
                    luxuryProductToUpdate.setDescription(luxuryProduct.getDescription());
                    luxuryProductToUpdate.setQuantityOnHand(luxuryProduct.getQuantityOnHand());
                    luxuryProductToUpdate.setPrice(luxuryProduct.getPrice());
                    luxuryProductToUpdate.setReorderQuantity(luxuryProduct.getReorderQuantity());

                } //productToUpdate.setProductRating((product.getProductRating()));
                else {
                    throw new UpdateProductException("SKU Code of product record to be updated does not match the existing record");
                }
            } else {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }
        } else {
            throw new ProductNotFoundException("Product ID not provided for product to be updated");
        }
    }

    @Override
    public void deleteProduct(Long productId) throws ProductNotFoundException, DeleteProductException {
        Product productToRemove = retrieveProductByProductId(productId);

        if (productToRemove instanceof LuxuryProduct) {
            LuxuryProduct luxuryProductToRemove = (LuxuryProduct) productToRemove;
            //remove category
            luxuryProductToRemove.getCategory().getProducts().remove(luxuryProductToRemove);
            //remove tag
            for (Tag tag : luxuryProductToRemove.getTags()) {
                tag.getProducts().remove(luxuryProductToRemove);
            }
            luxuryProductToRemove.getTags().clear();
            em.remove(luxuryProductToRemove);
            //remove productItem
            for (ProductItem productItem : luxuryProductToRemove.getProductItems()) {
                productItem.setLuxuryProduct(null);
            }
            luxuryProductToRemove.getProductItems().clear();

        } else {
            List<TransactionLineItem> transactionLineItems = transactionSessionBeanLocal.retrieveTransactionLineItemsByProductId(productId);
            if (transactionLineItems.isEmpty()) {
                productToRemove.getCategory().getProducts().remove(productToRemove);

                for (Tag tag : productToRemove.getTags()) {
                    tag.getProducts().remove(productToRemove);
                }

                productToRemove.getTags().clear();

                em.remove(productToRemove);
            } else {
                throw new DeleteProductException("Product ID " + productId + " is associated with existing sale transaction line item(s) and cannot be deleted!");
            }
        }
    }
    //only delete the luxury product itself together with its list of productItem
    //do not have to check in the transactionlineItem because debitQuantityOnHand will remove those productItem that is sold

    @Override
    public void debitQuantityOnHand(Long productId, Integer quantityToDebit) throws ProductNotFoundException, ProductInsufficientQuantityOnHandException {
        Product product = retrieveProductByProductId(productId);
        if (product instanceof LuxuryProduct) {
            LuxuryProduct luxuryProduct = (LuxuryProduct) product;
            if (luxuryProduct.getQuantityOnHand() >= quantityToDebit) {
                luxuryProduct.setQuantityOnHand(luxuryProduct.getQuantityOnHand() - quantityToDebit);
                //remove the productItem
                for (Integer x = 0; x < quantityToDebit; x++) {
                    luxuryProduct.getProductItems().remove(x);
                }
                //need to keep track of the productItem that is debited
                //link the debit item to the transactionLineItem
            } else {
                throw new ProductInsufficientQuantityOnHandException("Product " + luxuryProduct.getSkuCode() + " quantity on hand is " + luxuryProduct.getQuantityOnHand() + " versus quantity to debit of " + quantityToDebit);
            }
        } else {
            if (product.getQuantityOnHand() >= quantityToDebit) {
                product.setQuantityOnHand(product.getQuantityOnHand() - quantityToDebit);
                //link the product to the transactionLineItem
            } else {
                throw new ProductInsufficientQuantityOnHandException("Product " + product.getSkuCode() + " quantity on hand is " + product.getQuantityOnHand() + " versus quantity to debit of " + quantityToDebit);
            }
        }
    }

    @Override
    public void creditQuantityOnHand(Long productId, Integer quantityToCredit) throws ProductNotFoundException {
        Product product = retrieveProductByProductId(productId);
        product.setQuantityOnHand(product.getQuantityOnHand() + quantityToCredit);
    }

    @Override
    public void creditQuantityOnHandForLuxuryProduct(Long productId, Integer quantityToCredit, List<ProductItem> pis) throws ProductNotFoundException {
        LuxuryProduct luxuryProduct = (LuxuryProduct) retrieveProductByProductId(productId);
        luxuryProduct.setQuantityOnHand(luxuryProduct.getQuantityOnHand() + quantityToCredit);
        for (ProductItem productItem : pis) {
            luxuryProduct.getProductItems().add(productItem);
        }
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<Product>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }

}