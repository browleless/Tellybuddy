package ws.datamodel;

import entity.DiscountCode;

/**
 *
 * @author tjle2
 */
public class RetrieveDiscountCodeRsp {

    private DiscountCode discountCode;

    public RetrieveDiscountCodeRsp() {
    }

    public RetrieveDiscountCodeRsp(DiscountCode discountCode) {
        this.discountCode = discountCode;
    }

    public DiscountCode getDiscountCode() {
        return discountCode;
    }

    public void setDiscountCode(DiscountCode discountCode) {
        this.discountCode = discountCode;
    }
}
