/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author tjle2
 */
@Entity
public class ProductItem implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productItemId;
    
    @Column(nullable = false, length = 10)
    @NotNull
    @Size(min = 10, max = 10)
    private String serialNumber;
    
    @Column(nullable = false, precision = 6, scale = 2)
    @NotNull
    @Digits(integer = 4, fraction = 2)
    @DecimalMin("0.00")
    private BigDecimal price;
    
    @OneToOne(mappedBy = "productItem")
    private TransactionLineItem transactionLineItem;
    
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private LuxuryProduct luxuryProduct;

    public ProductItem() {
    }

    public ProductItem(String serialNumber, BigDecimal price) {
        this();
        this.serialNumber = serialNumber;
        this.price = price;
    }
    
    public Long getProductItemId() {
        return productItemId;
    }

    public void setProductItemId(Long productItemId) {
        this.productItemId = productItemId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (productItemId != null ? productItemId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the productItemId fields are not set
        if (!(object instanceof ProductItem)) {
            return false;
        }
        ProductItem other = (ProductItem) object;
        if ((this.productItemId == null && other.productItemId != null) || (this.productItemId != null && !this.productItemId.equals(other.productItemId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.ProductItem[ id=" + productItemId + " ]";
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public TransactionLineItem getTransactionLineItem() {
        return transactionLineItem;
    }

    public void setTransactionLineItem(TransactionLineItem transactionLineItem) {
        this.transactionLineItem = transactionLineItem;
    }

    public LuxuryProduct getLuxuryProduct() {
        return luxuryProduct;
    }

    public void setLuxuryProduct(LuxuryProduct luxuryProduct) {
        this.luxuryProduct = luxuryProduct;
    }
    
}
