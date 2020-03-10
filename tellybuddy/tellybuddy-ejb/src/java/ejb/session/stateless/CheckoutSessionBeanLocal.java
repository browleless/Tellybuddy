/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Product;
import entity.ProductItem;
import entity.Subscription;
import entity.Transaction;
import entity.TransactionLineItem;
import java.math.BigDecimal;
import java.util.List;
import javax.ejb.Local;
import util.exception.CreateNewSaleTransactionException;
import util.exception.CustomerNotFoundException;

/**
 *
 * @author markt
 */
@Local
public interface CheckoutSessionBeanLocal {

    public BigDecimal addItem(Product product, ProductItem productItem, Subscription subscription, Integer quantity);

    public Transaction doCheckout(Long customerId, String discountCodeName) throws CustomerNotFoundException, CreateNewSaleTransactionException;

    public void clearShoppingCart();

    public List<TransactionLineItem> getTransactionLineItems();

    public void setTransactionLineItems(List<TransactionLineItem> transactionLineItems);

    public Integer getTotalLineItem();

    public void setTotalLineItem(Integer totalLineItem);

    public Integer getTotalQuantity();

    public void setTotalQuantity(Integer totalQuantity);

    public BigDecimal getTotalAmount();

    public void setTotalAmount(BigDecimal totalAmount);

}
