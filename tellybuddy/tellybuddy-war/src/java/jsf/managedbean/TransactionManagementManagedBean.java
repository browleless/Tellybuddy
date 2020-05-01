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
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import util.exception.TransactionAlreadyVoidedRefundedException;
import util.exception.TransactionNotFoundException;
import util.exception.TransactionUnableToBeRefundedException;

/**
 *
 * @author markt
 */
@Named(value = "transactionManagementManagedBean")
@ViewScoped
public class TransactionManagementManagedBean implements Serializable {

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
    public void postConstruct() {
        setTransactions(transactionSessionBeanLocal.retrieveAllTransactions());
    }

    public void approveRefundRequest(Long transactionId) {
        try {
            transactionSessionBeanLocal.refundTransaction(transactionId);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Transaction refunded successfully", null));
            setTransactions(transactionSessionBeanLocal.retrieveAllTransactions());
        } catch (TransactionNotFoundException | TransactionAlreadyVoidedRefundedException | TransactionUnableToBeRefundedException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while refunding transaction: " + ex.getMessage(), null));
        }
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
