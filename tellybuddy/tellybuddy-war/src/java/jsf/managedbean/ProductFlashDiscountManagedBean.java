/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.managedbean;

import ejb.session.stateless.ProductSessionBeanLocal;
import entity.Category;
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
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
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

    @Inject
    private ViewProductManagedBean viewProductManagedBean;

    public ViewProductManagedBean getViewProductManagedBean() {
        return viewProductManagedBean;
    }

    public void setViewProductManagedBean(ViewProductManagedBean viewProductManagedBean) {
        this.viewProductManagedBean = viewProductManagedBean;
    }

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

    public void createNewProductFlashDeal(ActionEvent event) {
        productSessionBeanLocal.activatePromotion(discountedProduct);
        discountedProducts.add(discountedProduct);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "New flash discount created successfully", null));
    }

    public void updateDiscountedProduct(ActionEvent event) {
        productSessionBeanLocal.updateProduct(discountedProductToUpdate);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "New flash discount updated successfully", null));
    }

    public void deleteDiscountedProduct(ActionEvent event) {
        Product discountedProductToDelete = (Product) event.getComponent().getAttributes().get("discountedProductToDelete");
        productSessionBeanLocal.deactivatePromotion(discountedProductToDelete);
        getDiscountedProducts().remove(discountedProductToDelete);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "New flash discount removed successfully", null));
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
