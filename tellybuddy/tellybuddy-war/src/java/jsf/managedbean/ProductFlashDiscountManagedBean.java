/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.managedbean;

import ejb.session.stateless.ProductSessionBeanLocal;
import entity.Product;
import entity.Tag;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import util.exception.CategoryNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.ProductNotFoundException;
import util.exception.TagNotFoundException;
import util.exception.UpdateProductException;

/**
 *
 * @author kaikai
 */
@Named(value = "productFlashDiscountManagedBean")
@ViewScoped
public class ProductFlashDiscountManagedBean implements Serializable {

    @EJB(name = "ProductSessionBeanLocal")
    private ProductSessionBeanLocal productSessionBeanLocal;
    private List<Product> discountedProducts;
    private List<Product> filteredDiscountedProducts;
    private Product discountedProductToView;
    private Product discountedProductToUpdate;
    private Product discountedProduct;

    
    
    private Date dateTimeNow;
    private Date dateToday;

    

    public ProductFlashDiscountManagedBean() {
        dateTimeNow = new Date();
        dateToday = new Date();
        dateToday.setHours(0);
        dateToday.setMinutes(0);
        dateToday.setSeconds(0);
    
    }

 
    @PostConstruct
    public void postConstruct() {
        setDiscountedProducts(productSessionBeanLocal.retrieveAllDiscountedProducts());
    }
    

    public void createNewProductFlashDeal(ActionEvent event){
         discountedProducts.add(discountedProduct);
    }
    public void updateDiscountedProduct(ActionEvent event){
        try {
            //get list of tag ids
            List<Tag> tags = getDiscountedProductToUpdate().getTags();
            List<Long> tagId = new ArrayList<>();
            for(Tag tag : tags){
                tagId.add(tag.getTagId());
            }
            productSessionBeanLocal.updateProduct(getDiscountedProductToUpdate(),getDiscountedProductToUpdate().getCategory().getCategoryId(),tagId);
     
            if(discountedProductToUpdate.getDealEndTime().after(dateTimeNow)){
                discountedProducts.remove(discountedProductToUpdate);
            }
        } catch (ProductNotFoundException ex) {
            Logger.getLogger(ProductFlashDiscountManagedBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CategoryNotFoundException ex) {
            Logger.getLogger(ProductFlashDiscountManagedBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TagNotFoundException ex) {
            Logger.getLogger(ProductFlashDiscountManagedBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UpdateProductException ex) {
            Logger.getLogger(ProductFlashDiscountManagedBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InputDataValidationException ex) {
            Logger.getLogger(ProductFlashDiscountManagedBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void deleteDiscountedProduct(ActionEvent event) {
            Product discountedProductToDelete = (Product) event.getComponent().getAttributes().get("discountedProductToDelete");
            productSessionBeanLocal.deactivatePromotion(discountedProductToDelete);
            getDiscountedProducts().remove(discountedProductToDelete);
    }
       
    public Date getDateTimeNow() {
        return dateTimeNow;
    }

    public void setDateTimeNow(Date dateTimeNow) {
        this.dateTimeNow = dateTimeNow;
    }

    public Date getDateToday() {
        return dateToday;
    }

    public void setDateToday(Date dateToday) {
        this.dateToday = dateToday;
    }
    
    public long calculateTimerTime(Product discountedProduct) {

        if (discountedProduct.getDealStartTime().before(new Date())) {
            return (discountedProduct.getDealEndTime().getTime() - dateTimeNow.getTime()) / 1000;
        } else {
            return (discountedProduct.getDealStartTime().getTime() - dateTimeNow.getTime()) / 1000;
        }
    }
    
    public List<Product> getDiscountedProducts() {
        return discountedProducts;
    }

    public void setDiscountedProducts(List<Product> discountedProducts) {
        this.discountedProducts = discountedProducts;
    }

    public List<Product> getFilteredDiscountedProducts() {
        return filteredDiscountedProducts;
    }

    public void setFilteredDiscountedProducts(List<Product> filteredDiscountedProducts) {
        this.filteredDiscountedProducts = filteredDiscountedProducts;
    }

    public Product getDiscountedProductToView() {
        return discountedProductToView;
    }

    public void setDiscountedProductToView(Product discountedProductToView) {
        this.discountedProductToView = discountedProductToView;
    }

    public Product getDiscountedProductToUpdate() {
        return discountedProductToUpdate;
    }

    public void setDiscountedProductToUpdate(Product discountedProductToUpdate) {
        this.discountedProductToUpdate = discountedProductToUpdate;
    }

    public Product getDiscountedProduct() {
        return discountedProduct;
    }

    public void setDiscountedProduct(Product discountedProduct) {
        this.discountedProduct = discountedProduct;
    }
}
