/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.managedbean;

import ejb.session.stateless.CategorySessionBeanLocal;
import ejb.session.stateless.ProductSessionBeanLocal;
import entity.Category;
import entity.Product;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import util.exception.CategoryNotFoundException;

/**
 *
 * @author ngjin
 */
@Named(value = "filterProductsByCategoryManagedBean")
@ViewScoped
public class FilterProductsByCategoryManagedBean implements Serializable {

    @EJB
    private CategorySessionBeanLocal categorySessionBeanLocal;
    @EJB
    private ProductSessionBeanLocal productSessionBeanLocal;

    @Inject
    private ViewProductManagedBean viewProductManagedBean;

    private TreeNode treeNode;
    private TreeNode selectedTreeNode;

    private List<Product> products;

    public FilterProductsByCategoryManagedBean() {

    }

    @PostConstruct
    public void postConstruct() {
        List<Category> categories = categorySessionBeanLocal.retrieveAllCategories();
        treeNode = new DefaultTreeNode("Root", null);

        for (Category category : categories) {
            createTreeNode(category, treeNode);
        }

        Long selectedCategoryId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("productFilterCategory");

        if (selectedCategoryId != null) {
            for (TreeNode tn : treeNode.getChildren()) {
                Category c = (Category) tn.getData();

                if (c.getCategoryId().equals(selectedCategoryId)) {
                    selectedTreeNode = tn;
                    break;
                } else {
                    selectedTreeNode = searchTreeNode(selectedCategoryId, tn);
                }
            }
        }

        filterProduct();
    }

    public void filterProduct() {
        if (selectedTreeNode != null) {
            try {
                Category c = (Category) selectedTreeNode.getData();
                products = productSessionBeanLocal.filterProductsByCategory(c.getCategoryId());
            } catch (CategoryNotFoundException ex) {
                products = productSessionBeanLocal.retrieveAllProducts();
            }
        } else {
            products = productSessionBeanLocal.retrieveAllProducts();
        }
    }

    public void viewProductDetails(ActionEvent event) throws IOException {

        Long productIdToView = (Long) event.getComponent().getAttributes().get("productId");
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("productIdToView", productIdToView);
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("backMode", "filterProductsByCategory");
        FacesContext.getCurrentInstance().getExternalContext().redirect("viewProductDetails.xhtml");
    }

    private void createTreeNode(Category category, TreeNode parentTreeNode) {
        TreeNode treeNode = new DefaultTreeNode(category, parentTreeNode);

//        for (Category c : category.getSubCategories()) {
//            createTreeNode(c, treeNode);
//        }
    }

    private TreeNode searchTreeNode(Long selectedCategoryId, TreeNode treeNode) {
        for (TreeNode tn : treeNode.getChildren()) {
            Category c = (Category) tn.getData();

            if (c.getCategoryId().equals(selectedCategoryId)) {
                return tn;
            } else {
                return searchTreeNode(selectedCategoryId, tn);
            }
        }

        return null;
    }

    public ViewProductManagedBean getViewProductManagedBean() {
        return viewProductManagedBean;
    }

    public void setViewProductManagedBean(ViewProductManagedBean viewProductManagedBean) {
        this.viewProductManagedBean = viewProductManagedBean;
    }

    public TreeNode getTreeNode() {
        return treeNode;
    }

    public void setTreeNode(TreeNode treeNode) {
        this.treeNode = treeNode;
    }

    public TreeNode getSelectedTreeNode() {
        return selectedTreeNode;
    }

    public void setSelectedTreeNode(TreeNode selectedTreeNode) {
        this.selectedTreeNode = selectedTreeNode;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

}
