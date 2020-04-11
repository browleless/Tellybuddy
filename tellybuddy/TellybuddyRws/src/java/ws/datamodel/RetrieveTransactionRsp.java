/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.datamodel;

import entity.Transaction;

/**
 *
 * @author markt
 */
public class RetrieveTransactionRsp {
    private Transaction transaction;

    public RetrieveTransactionRsp() {
    }
    
    public RetrieveTransactionRsp(Transaction transaction) {
        this.transaction = transaction;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }
    
    
}
