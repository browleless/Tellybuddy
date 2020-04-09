/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Customer;
import entity.Transaction;
import entity.TransactionLineItem;
import java.util.List;
import javax.ejb.Local;
import util.exception.CreateNewSaleTransactionException;
import util.exception.CustomerNotFoundException;
import util.exception.DiscountCodeNotFoundException;
import util.exception.TransactionAlreadyVoidedRefundedException;
import util.exception.TransactionNotFoundException;

/**
 * @markt
 */
@Local
public interface TransactionSessionBeanLocal {

    public Transaction createNewTransaction(Long customerId, Transaction newTransaction, String discountCodeName) throws CustomerNotFoundException, CreateNewSaleTransactionException, DiscountCodeNotFoundException;

    public List<Transaction> retrieveAllTransactions();

    public List<TransactionLineItem> retrieveTransactionLineItemsByProductId(Long productId);

    public Transaction retrieveTransactionByTransactionId(Long transactionId) throws TransactionNotFoundException;
    
    public List<Transaction> retrieveTransactionsByCustomer(Customer customer);

    public void updateTransaction(Transaction transaction);

    public void voidRefundTransaction(Long saleTransactionId) throws TransactionNotFoundException, TransactionAlreadyVoidedRefundedException;

    public void deleteTransaction(Transaction transaction);

    public List<Transaction> retrieveAllMonthlyTransactions();
}
