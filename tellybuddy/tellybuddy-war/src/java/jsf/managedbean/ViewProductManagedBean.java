/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.managedbean;

import entity.Product;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.faces.view.ViewScoped;

/**
 *
 * @author ngjin
 */
@Named(value = "viewProductManagedBean")
@ViewScoped
public class ViewProductManagedBean implements Serializable {

    private Product productToView;

    public ViewProductManagedBean() {
        this.productToView = new Product();
    }

    @PostConstruct
    public void postConstruct() {

    }

    public Product getProductToView() {
        return productToView;
    }

    public void setProductToView(Product productToView) {
        this.productToView = productToView;
    }

}
