/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.datamodel;

/**
 *
 * @author markt
 */
public class RefundTransactionRsp {

    /**
     *
     * @author markt
     */
    private Long transactionId;

    public RefundTransactionRsp() {
    }

    public RefundTransactionRsp(Long transactionId) {
        this.transactionId = transactionId;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

}

