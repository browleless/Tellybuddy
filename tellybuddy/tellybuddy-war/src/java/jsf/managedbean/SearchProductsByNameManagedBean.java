/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.managedbean;

import ejb.session.stateless.ProductSessionBeanLocal;
import entity.Product;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;

/**
 *
 * @author ngjin
 */
@Named(value = "searchProductsByNameManagedBean")
@ViewScoped
public class SearchProductsByNameManagedBean implements Serializable {

    @EJB
    private ProductSessionBeanLocal productSessionBeanLocal;

    @Inject
    private ViewProductManagedBean viewProductManagedBean;

    private String searchString;
    private List<Product> products;

    public SearchProductsByNameManagedBean() {
    }

    @PostConstruct
    public void postConstruct() {
        loadProducts();
    }

    public void loadProducts() {
        searchString = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("productSearchString");

        if (searchString == null || searchString.trim().length() == 0) {
            products = productSessionBeanLocal.retrieveAllProducts();
        } else {
            products = productSessionBeanLocal.searchProductsByName(searchString);
        }
    }

    public void searchProduct() {
        if (searchString == null || searchString.trim().length() == 0) {
            products = productSessionBeanLocal.retrieveAllProducts();
        } else {
            products = productSessionBeanLocal.searchProductsByName(searchString);
        }
    }

    public ViewProductManagedBean getViewProductManagedBean() {
        return viewProductManagedBean;
    }

    public void setViewProductManagedBean(ViewProductManagedBean viewProductManagedBean) {
        this.viewProductManagedBean = viewProductManagedBean;
    }

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

}
