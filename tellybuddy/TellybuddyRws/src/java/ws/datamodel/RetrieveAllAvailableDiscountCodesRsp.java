package ws.datamodel;

import entity.DiscountCode;
import java.util.List;

/**
 *
 * @author tjle2
 */
public class RetrieveAllAvailableDiscountCodesRsp {

    private List<DiscountCode> discountCodes;

    public RetrieveAllAvailableDiscountCodesRsp() {
    }

    public RetrieveAllAvailableDiscountCodesRsp(List<DiscountCode> discountCodes) {
        this.discountCodes = discountCodes;
    }

    public List<DiscountCode> getDiscountCodes() {
        return discountCodes;
    }

    public void setDiscountCodes(List<DiscountCode> discountCodes) {
        this.discountCodes = discountCodes;
    }
}
