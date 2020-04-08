package ws.datamodel;

/**
 *
 * @author tjle2
 */
public class MakeBillPaymentRsp {
    
    private Long paymentId;

    public MakeBillPaymentRsp() {
    }

    public MakeBillPaymentRsp(Long paymentId) {
        this.paymentId = paymentId;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }
}
