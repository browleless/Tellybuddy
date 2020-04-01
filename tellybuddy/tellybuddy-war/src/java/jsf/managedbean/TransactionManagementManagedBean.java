/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.managedbean;

import ejb.session.stateless.TransactionSessionBeanLocal;
import entity.Transaction;
import entity.TransactionLineItem;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.view.ViewScoped;

/**
 *
 * @author markt
 */
@Named(value = "transactionManagementManagedBean")
@ViewScoped
public class TransactionManagementManagedBean implements Serializable{

    @EJB
    private TransactionSessionBeanLocal transactionSessionBeanLocal;
    private List<Transaction> transactions;
    private List<TransactionLineItem> transactionToViewLineItems;
    /**
     * Creates a new instance of TransactionManagementManagedBean
     */
    public TransactionManagementManagedBean() {
    }
    
    @PostConstruct
    public void postConstruct(){
        setTransactions(transactionSessionBeanLocal.retrieveAllTransactions());
    }

    public TransactionSessionBeanLocal getTransactionSessionBeanLocal() {
        return transactionSessionBeanLocal;
    }

    public void setTransactionSessionBeanLocal(TransactionSessionBeanLocal transactionSessionBeanLocal) {
        this.transactionSessionBeanLocal = transactionSessionBeanLocal;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public List<TransactionLineItem> getTransactionToViewLineItems() {
        return transactionToViewLineItems;
    }

    public void setTransactionToViewLineItems(List<TransactionLineItem> transactionToViewLineItems) {
        this.transactionToViewLineItems = transactionToViewLineItems;
    }
    
}
