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
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 *
 * @author tjle2
 */
@Entity
public class TransactionLineItem implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionLineItemId;
    
    @Column(nullable = false, precision = 6, scale = 2)
    @NotNull
    @Digits(integer = 4, fraction = 2)
    @DecimalMin("0.00")
    private BigDecimal price;
    
    @Column(nullable = false)
    @NotNull
    @Positive
    @Min(1)
    private Integer quantity;
    
    @Column(nullable = false, precision = 7, scale = 2)
    @NotNull
    @Digits(integer = 5, fraction = 2)
    @DecimalMin("0.00")
    private BigDecimal subtotal;
    
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private Transaction transaction;
    
    @OneToOne
    private Subscription subscription;
    
    @OneToOne
    private ProductItem productItem;
    
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private Product product;

    public TransactionLineItem() {
    }

    public TransactionLineItem(BigDecimal price, Integer quantity, BigDecimal subtotal) {
        this();
        this.price = price;
        this.quantity = quantity;
        this.subtotal = subtotal;
    }
    
    public Long getTransactionLineItemId() {
        return transactionLineItemId;
    }

    public void setTransactionLineItemId(Long transactionLineItemId) {
        this.transactionLineItemId = transactionLineItemId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (transactionLineItemId != null ? transactionLineItemId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the transactionLineItemId fields are not set
        if (!(object instanceof TransactionLineItem)) {
            return false;
        }
        TransactionLineItem other = (TransactionLineItem) object;
        if ((this.transactionLineItemId == null && other.transactionLineItemId != null) || (this.transactionLineItemId != null && !this.transactionLineItemId.equals(other.transactionLineItemId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.TransactionLineItem[ id=" + transactionLineItemId + " ]";
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    public ProductItem getProductItem() {
        return productItem;
    }

    public void setProductItem(ProductItem productItem) {
        this.productItem = productItem;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
    
}
