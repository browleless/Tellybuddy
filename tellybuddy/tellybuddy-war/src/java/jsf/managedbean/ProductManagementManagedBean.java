/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.managedbean;

import ejb.session.stateless.CategorySessionBeanLocal;
import ejb.session.stateless.ProductItemSessionBeanLocal;
import ejb.session.stateless.ProductSessionBeanLocal;
import ejb.session.stateless.TagSessionBeanLocal;
import entity.Category;
import entity.LuxuryProduct;
import entity.Product;
import entity.ProductItem;
import entity.Tag;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
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
import javax.management.Query;
import javax.servlet.ServletContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.primefaces.shaded.commons.io.FilenameUtils;
import util.exception.CreateNewProductException;
import util.exception.DeleteProductException;
import util.exception.InputDataValidationException;
import util.exception.ProductItemExistException;
import util.exception.ProductNotFoundException;
import util.exception.ProductSkuCodeExistException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author ngjin
 */
@Named(value = "productManagementManagedBean")
@ViewScoped
public class ProductManagementManagedBean implements Serializable {

    @EJB(name = "TagSessionBeanLocal")
    private TagSessionBeanLocal tagSessionBeanLocal;

    @EJB(name = "CategorySessionBeanLocal")
    private CategorySessionBeanLocal categorySessionBeanLocal;

    @EJB(name = "ProductSessionBeanLocal")
    private ProductSessionBeanLocal productSessionBeanLocal;

    @EJB(name = "ProductItemSessionBeanLocal")
    private ProductItemSessionBeanLocal productItemSessionBeanLocal;

    @Inject
    private ViewProductManagedBean viewProductManagedBean;

    private List<Product> allProducts;
    private List<Product> filteredProducts;

    private String productType;

    private Product newProduct;
    private LuxuryProduct newLuxuryProduct;

    private Long categoryIdNew;
    private List<Long> tagIdsNew;
    private List<Category> allCategories;
    private List<Tag> allTags;
    private UploadedFile productImageFile;

    private Product selectedProductToUpdate;
    private Long categoryIdUpdate;
    private List<Long> tagIdsUpdate;

    public ProductManagementManagedBean() {
        this.newProduct = new Product();
        this.newLuxuryProduct = new LuxuryProduct();
    }

    @PostConstruct
    public void postConstruct() {
        this.setAllCategories(categorySessionBeanLocal.retrieveAllLeafCategories());
        this.setAllTags(tagSessionBeanLocal.retrieveAllTags());
        this.setAllProducts(productSessionBeanLocal.retrieveAllProducts());
    }

    public void viewProductDetails(ActionEvent ae) throws IOException {
        Long productIdToView = (Long) ae.getComponent().getAttributes().get("productId");
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("productIdToView", productIdToView);
        FacesContext.getCurrentInstance().getExternalContext().redirect("viewProductDetails.xhtml");
    }

    public void createNewProduct(ActionEvent ae) {

        if (this.productType.equals("Luxury Product")) {
            this.newLuxuryProduct = new LuxuryProduct();

            //query the database for unique serial number of luxury prod
            String serialNumLuxury = productSessionBeanLocal.retrieveLatestSerialNum();
            Integer uniqueLuxury = Integer.parseInt(serialNumLuxury) + 1;
            String s = uniqueSerialNum(uniqueLuxury);
            this.newLuxuryProduct.setSerialNumber(s);

            //transfer the user input into the properties of newLuxuryProduct
            this.newLuxuryProduct.setSkuCode(newProduct.getSkuCode());
            this.newLuxuryProduct.setName(newProduct.getName());
            this.newLuxuryProduct.setDescription(newProduct.getDescription());
            this.newLuxuryProduct.setQuantityOnHand(newProduct.getQuantityOnHand());
            this.newLuxuryProduct.setReorderQuantity(newProduct.getReorderQuantity());
            this.newLuxuryProduct.setPrice(newProduct.getPrice());

        }

        if (categoryIdNew == 0) {
            categoryIdNew = null;
        }

        String filePath = this.saveUploadedProductImage();
        if (this.productType.equals("Luxury Product")) {
            this.newLuxuryProduct.setProductImagePath(filePath);
        } else {
            this.newProduct.setProductImagePath(filePath);
        }
        try {
//            if (productImageFile == null) {
//                System.out.println("Prodcut has REACHED HERE ______________________________________----------------------");
//
//            }

            Product p;
            if (this.productType.equals("Luxury Product")) {
                p = productSessionBeanLocal.createNewProduct(newLuxuryProduct, categoryIdNew, tagIdsNew);
            } else {
                p = productSessionBeanLocal.createNewProduct(newProduct, categoryIdNew, tagIdsNew);
            }

            allProducts.add(p);

            if (filteredProducts != null) {
                filteredProducts.add(p);
            }

            if (this.productType.equals("Luxury Product")) {
                Integer num = newLuxuryProduct.getQuantityOnHand();
                List<ProductItem> productItems = newLuxuryProduct.getProductItems();

                //query the database for unique serial number of productItem
                String serialNum = productItemSessionBeanLocal.retrieveLatestSerialNum();
                Integer unique = Integer.parseInt(serialNum) + 1;

                for (int i = 0; i < num; i++) {
                    //instantiate every product item
                    String s1 = uniqueSerialNum(unique);
                    ProductItem pi = new ProductItem(s1, this.newLuxuryProduct.getPrice());
                    ProductItem createdPI = pi;
                    pi.setLuxuryProduct(this.newLuxuryProduct);
                    try {
                        createdPI = productItemSessionBeanLocal.createNewProductItem(pi, p.getProductId());
                    } catch (ProductItemExistException ex) {
                        Logger.getLogger(ProductManagementManagedBean.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    productItems.add(createdPI);
                    unique++;
                }
            }
            this.newProduct = new Product();
            this.newLuxuryProduct = new LuxuryProduct();
            categoryIdNew = null;
            tagIdsNew = null;
            productImageFile = null;

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "New product created successfully (Product ID: " + p.getProductId() + ")", null));
        } catch (InputDataValidationException | CreateNewProductException | ProductSkuCodeExistException | UnknownPersistenceException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while creating the new product: " + ex.getMessage(), null));
        }
    }

    private String uniqueSerialNum(int unique) {
        String s = Integer.toString(unique);

        while (s.length() < 10) {
            s = "0" + s;
        }

        return s;
    }

    public void doUpdateProduct(ActionEvent ae) {
        selectedProductToUpdate = (Product) ae.getComponent().getAttributes().get("productToUpdate");

        categoryIdUpdate = selectedProductToUpdate.getCategory().getCategoryId();
        tagIdsUpdate = new ArrayList<>();

        for (Tag tag : selectedProductToUpdate.getTags()) {
            tagIdsUpdate.add(tag.getTagId());
        }
    }

    public void updateProduct(ActionEvent ae) {
        if (categoryIdUpdate == 0) {
            categoryIdUpdate = null;
        }

        try {
            productSessionBeanLocal.updateProduct(selectedProductToUpdate, categoryIdUpdate, tagIdsUpdate);

            for (Category c : allCategories) {
                if (c.getCategoryId().equals(categoryIdUpdate)) {
                    selectedProductToUpdate.setCategory(c);
                    break;
                }
            }

            selectedProductToUpdate.getTags().clear();

            for (Tag t : allTags) {
                if (tagIdsUpdate.contains(t.getTagId())) {
                    selectedProductToUpdate.getTags().add(t);
                }
            }

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Product updated successfully", null));

        } catch (ProductNotFoundException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while updating product: " + ex.getMessage(), null));
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An unexpected error has occurred: " + ex.getMessage(), null));
        }
    }

    public void deleteProduct(ActionEvent ae) {
        try {
            Product productToDelete = (Product) ae.getComponent().getAttributes().get("productToDelete");
            //System.out.println("READ IT: " + productToDelete.getName());
            productSessionBeanLocal.deleteProduct(productToDelete.getProductId());

            allProducts.remove(productToDelete);

            if (filteredProducts != null) {
                filteredProducts.remove(productToDelete);
            }

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Product deleted successfully", null));

        } catch (ProductNotFoundException | DeleteProductException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while deleting product: " + ex.getMessage(), null));
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An unexpected error has occurred: " + ex.getMessage(), null));
        }
    }

    public void upload(FileUploadEvent event) {
        this.productImageFile = event.getFile();
        if (productImageFile != null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Successfully uploaded file: " + productImageFile.getFileName(), null));
//            System.out.println(filePath);
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "File upload unsuccessful. Please try again!", null));
//            System.out.println("Uploaded file stilll null!!");
        }
    }

    public String saveUploadedProductImage() {

            String absolutePathToProductImages = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/").substring(0, FacesContext.getCurrentInstance().getExternalContext().getRealPath("/").indexOf("\\dist")) + "\\tellybuddy-war\\web\\management\\products\\productImages";
            Path folder = Paths.get(absolutePathToProductImages);
            System.out.println(absolutePathToProductImages);

//            Path folder = Paths.get("C:\\Users\\markt\\Documents\\GitHub\\Tellybuddy\\tellybuddy\\tellybuddy-war\\web\\management\\products\\productImages");
            try {
                String filename = FilenameUtils.getBaseName(productImageFile.getFileName());
                String extension = FilenameUtils.getExtension(productImageFile.getFileName());
                Path file = Files.createTempFile(folder, filename + "-", "." + extension);
                InputStream input = productImageFile.getInputstream();

                Files.copy(input, file, StandardCopyOption.REPLACE_EXISTING);

//                return filename + "-" + "." + extension;
                System.out.println(file.toString());
                String fullPath = folder + "\\" + file.getFileName().toString();
                return fullPath;
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
            return null;

//            System.out.println("Uploaded file successfully saved in " + file);
    }

    public List<Product> getAllProducts() {
        return allProducts;
    }

    public void setAllProducts(List<Product> allProducts) {
        this.allProducts = allProducts;
    }

    public List<Product> getFilteredProducts() {
        return filteredProducts;
    }

    public void setFilteredProducts(List<Product> filteredProducts) {
        this.filteredProducts = filteredProducts;
    }

    public Long getCategoryIdNew() {
        return categoryIdNew;
    }

    public void setCategoryIdNew(Long categoryIdNew) {
        this.categoryIdNew = categoryIdNew;
    }

    public List<Long> getTagIdsNew() {
        return tagIdsNew;
    }

    public void setTagIdsNew(List<Long> tagIdsNew) {
        this.tagIdsNew = tagIdsNew;
    }

    public List<Category> getAllCategories() {
        return allCategories;
    }

    public void setAllCategories(List<Category> allCategories) {
        this.allCategories = allCategories;
    }

    public List<Tag> getAllTags() {
        return allTags;
    }

    public void setAllTags(List<Tag> allTags) {
        this.allTags = allTags;
    }

    public Product getSelectedProductToUpdate() {
        return selectedProductToUpdate;
    }

    public void setSelectedProductToUpdate(Product selectedProductToUpdate) {
        this.selectedProductToUpdate = selectedProductToUpdate;
    }

    public Long getCategoryIdUpdate() {
        return categoryIdUpdate;
    }

    public void setCategoryIdUpdate(Long categoryIdUpdate) {
        this.categoryIdUpdate = categoryIdUpdate;
    }

    public List<Long> getTagIdsUpdate() {
        return tagIdsUpdate;
    }

    public void setTagIdsUpdate(List<Long> tagIdsUpdate) {
        this.tagIdsUpdate = tagIdsUpdate;
    }

    public UploadedFile getProductImageFile() {
        return productImageFile;
    }

    public void setProductImageFile(UploadedFile productImageFile) {
        this.productImageFile = productImageFile;
    }

    public TagSessionBeanLocal getTagSessionBeanLocal() {
        return tagSessionBeanLocal;
    }

    public void setTagSessionBeanLocal(TagSessionBeanLocal tagSessionBeanLocal) {
        this.tagSessionBeanLocal = tagSessionBeanLocal;
    }

    public CategorySessionBeanLocal getCategorySessionBeanLocal() {
        return categorySessionBeanLocal;
    }

    public void setCategorySessionBeanLocal(CategorySessionBeanLocal categorySessionBeanLocal) {
        this.categorySessionBeanLocal = categorySessionBeanLocal;
    }

    public ProductSessionBeanLocal getProductSessionBeanLocal() {
        return productSessionBeanLocal;
    }

    public void setProductSessionBeanLocal(ProductSessionBeanLocal productSessionBeanLocal) {
        this.productSessionBeanLocal = productSessionBeanLocal;
    }

    public ViewProductManagedBean getViewProductManagedBean() {
        return viewProductManagedBean;
    }

    public void setViewProductManagedBean(ViewProductManagedBean viewProductManagedBean) {
        this.viewProductManagedBean = viewProductManagedBean;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public Product getNewProduct() {
        return newProduct;
    }

    public void setNewProduct(Product newProduct) {
        this.newProduct = newProduct;
    }

}
