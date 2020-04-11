/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.datamodel;

import entity.Transaction;
import java.util.List;

/**
 *
 * @author markt
 */
public class RetrieveCustomerTransactionsRsp {
    
    private List<Transaction> transactions;

    public RetrieveCustomerTransactionsRsp() {
    }

    public RetrieveCustomerTransactionsRsp(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public List<Transaction> gettransactions() {
        return transactions;
    }

    public void settransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }
}
