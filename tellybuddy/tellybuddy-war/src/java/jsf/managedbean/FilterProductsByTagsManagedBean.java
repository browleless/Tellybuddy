/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.managedbean;

import ejb.session.stateless.ProductSessionBeanLocal;
import ejb.session.stateless.TagSessionBeanLocal;
import entity.Product;
import entity.Tag;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;

/**
 *
 * @author ngjin
 */
@Named(value = "filterProductsByTagsManagedBean")
@ViewScoped
public class FilterProductsByTagsManagedBean implements Serializable {

    @EJB
    private TagSessionBeanLocal tagSessionBeanLocal;
    @EJB
    private ProductSessionBeanLocal productSessionBeanLocal;

    @Inject
    private ViewProductManagedBean viewProductManagedBean;

    private String condition;
    private List<Long> selectedTagIds;
    private List<SelectItem> selectItems;
    private List<Product> products;

    public FilterProductsByTagsManagedBean() {
        condition = "OR";
    }

    @PostConstruct
    public void postConstruct() {
        List<Tag> tags = tagSessionBeanLocal.retrieveAllTags();
        setSelectItems(new ArrayList<>());

        for (Tag tag : tags) {
            getSelectItems().add(new SelectItem(tag.getTagId(), tag.getName(), tag.getName()));
        }

        // Optional demonstration of the use of custom converter
        // FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("TagEntityConverter_tagEntities", tagEntities);
        setCondition((String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("productFilterCondition"));
        setSelectedTagIds((List<Long>) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("productFilterTags"));

        filterProduct();
    }

    @PreDestroy
    public void preDestroy() {
        // FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("TagEntityConverter_tagEntities", null);
        // FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("TagEntityConverter_tagEntities", null);
    }

    public void filterProduct() {
        if (getSelectedTagIds() != null && getSelectedTagIds().size() > 0) {
            setProducts(productSessionBeanLocal.filterProductsByTags(getSelectedTagIds(), getCondition()));
        } else {
            setProducts(productSessionBeanLocal.retrieveAllProducts());
        }
    }

    public void viewProductDetails(ActionEvent event) throws IOException {

        Long productIdToView = (Long) event.getComponent().getAttributes().get("productId");
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("productIdToView", productIdToView);
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("backMode", "filterProductsByTags");
        FacesContext.getCurrentInstance().getExternalContext().redirect("viewProductDetails.xhtml");
    }

    public ViewProductManagedBean getViewProductManagedBean() {
        return viewProductManagedBean;
    }

    public void setViewProductManagedBean(ViewProductManagedBean viewProductManagedBean) {
        this.viewProductManagedBean = viewProductManagedBean;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public List<Long> getSelectedTagIds() {
        return selectedTagIds;
    }

    public void setSelectedTagIds(List<Long> selectedTagIds) {
        this.selectedTagIds = selectedTagIds;
    }

    public List<SelectItem> getSelectItems() {
        return selectItems;
    }

    public void setSelectItems(List<SelectItem> selectItems) {
        this.selectItems = selectItems;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

}
