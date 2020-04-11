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
public class CreateNewTransactionRsp {
    private Long newTransactionId;

    public CreateNewTransactionRsp() {
    }
    public CreateNewTransactionRsp(Long newTransactionId) {
        this.newTransactionId = newTransactionId;
    }
    
    public Long getNewTransactionId() {
        return newTransactionId;
    }

    public void setNewTransactionId(Long newTransactionId) {
        this.newTransactionId = newTransactionId;
    }
}
