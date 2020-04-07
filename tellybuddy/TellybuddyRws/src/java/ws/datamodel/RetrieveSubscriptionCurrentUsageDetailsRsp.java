package ws.datamodel;

import entity.UsageDetail;

/**
 *
 * @author tjle2
 */
public class RetrieveSubscriptionCurrentUsageDetailsRsp {

    private UsageDetail usageDetail;

    public RetrieveSubscriptionCurrentUsageDetailsRsp() {
    }

    public RetrieveSubscriptionCurrentUsageDetailsRsp(UsageDetail usageDetail) {
        this.usageDetail = usageDetail;
    }

    public UsageDetail getUsageDetail() {
        return usageDetail;
    }

    public void setUsageDetail(UsageDetail usageDetail) {
        this.usageDetail = usageDetail;
    }
}
