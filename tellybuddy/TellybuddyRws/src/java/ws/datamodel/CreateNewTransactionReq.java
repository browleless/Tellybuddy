/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.datamodel;

import entity.Customer;
import entity.Transaction;

/**
 *
 * @author markt
 */
public class CreateNewTransactionReq {

    private Transaction newTransaction;
    private String username;
    private String password;
    private Long customerId;
    private String discountCodeName;
    private String creditCardNo;
    private String cvv;

    public CreateNewTransactionReq(Transaction newTransaction, String username, String password, Long customerId, String discountCodeName, String creditCardNo, String cvv) {
        this.newTransaction = newTransaction;
        this.username = username;
        this.password = password;
        this.customerId = customerId;
        this.discountCodeName = discountCodeName;
        this.creditCardNo = creditCardNo;
        this.cvv = cvv;
    }
    

    public CreateNewTransactionReq() {
    }

    public Transaction getNewTransaction() {
        return newTransaction;
    }

    public void setNewTransaction(Transaction newTransaction) {
        this.newTransaction = newTransaction;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getDiscountCodeName() {
        return discountCodeName;
    }

    public void setDiscountCodeName(String discountCodeName) {
        this.discountCodeName = discountCodeName;
    }

    public String getCreditCardNo() {
        return creditCardNo;
    }

    public void setCreditCardNo(String creditCardNo) {
        this.creditCardNo = creditCardNo;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }


}
