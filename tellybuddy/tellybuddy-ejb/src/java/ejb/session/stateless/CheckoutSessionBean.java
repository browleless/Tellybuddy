/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import ejb.session.stateless.TransactionSessionBeanLocal;
import entity.LuxuryProduct;
import entity.Product;
import entity.ProductItem;
import entity.Subscription;
import entity.Transaction;
import entity.TransactionLineItem;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.Remove;
import util.exception.CreateNewSaleTransactionException;
import util.exception.CustomerNotFoundException;

/**
 *
 * @author markt
 */
@Stateless
@Local(CheckoutSessionBeanLocal.class)
public class CheckoutSessionBean implements CheckoutSessionBeanLocal{

    @EJB
    private TransactionSessionBeanLocal transactionSessionBeanLocal;

    private List<TransactionLineItem> transactionLineItems;
    private Integer totalLineItem;    
    private Integer totalQuantity;    
    private BigDecimal totalAmount;   

    
    
    public CheckoutSessionBean() 
    {
        initialiseState();
    }
    
    
    
    @Remove
    public void remove()
    {        
    }
    
    
    
    private void initialiseState()
    {
        transactionLineItems = new ArrayList<>();
        totalLineItem = 0;
        totalQuantity = 0;
        totalAmount = new BigDecimal("0.00");
    }
    
    
    
    @Override
    public BigDecimal addItem(Product product, ProductItem productItem, Subscription subscription, Integer quantity)
    {
        
        ++totalLineItem;
        
        BigDecimal subTotal;
        TransactionLineItem newTransactionLineItem;
        
        if(subscription != null){
            
            BigDecimal adminFee = new BigDecimal("10.70");
            subTotal = adminFee.multiply(new BigDecimal(quantity));
            
            newTransactionLineItem = new TransactionLineItem(adminFee, quantity, subTotal);
            newTransactionLineItem.setSubscription(subscription);
        } else{
            subTotal = product.getPrice().multiply(new BigDecimal(quantity));
            
            newTransactionLineItem = new TransactionLineItem(product.getPrice(), quantity, subTotal);
            newTransactionLineItem.setProduct(product);
            
            if (productItem != null) {
                LuxuryProduct luxuryProduct = (LuxuryProduct) product;
                newTransactionLineItem.setProductItem(productItem);
            }
        }

        transactionLineItems.add(newTransactionLineItem);
        totalQuantity += quantity;
        totalAmount = totalAmount.add(subTotal);
        
        return subTotal;
    }
    
    
    
    @Override
    public Transaction doCheckout(Long customerId, String discountCodeName) throws CustomerNotFoundException, CreateNewSaleTransactionException
    {
        Transaction newTransaction = new Transaction(totalAmount, new Date(), transactionLineItems);
        for(TransactionLineItem t:newTransaction.getTransactionLineItems()){
            t.setTransaction(newTransaction);
        }
        newTransaction = transactionSessionBeanLocal.createNewTransaction(customerId, newTransaction, discountCodeName);
        initialiseState();
        
        return newTransaction;
    }
    
    
    
    @Override
    public void clearShoppingCart()
    {
        initialiseState();
    }

    
    
    @Override
    public List<TransactionLineItem> getTransactionLineItems() {
        return transactionLineItems;
    }

    @Override
    public void setTransactionLineItems(List<TransactionLineItem> transactionLineItems) {
        this.transactionLineItems = transactionLineItems;
    }

    @Override
    public Integer getTotalLineItem() {
        return totalLineItem;
    }

    @Override
    public void setTotalLineItem(Integer totalLineItem) {
        this.totalLineItem = totalLineItem;
    }

    @Override
    public Integer getTotalQuantity() {
        return totalQuantity;
    }

    @Override
    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    @Override
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    @Override
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}
