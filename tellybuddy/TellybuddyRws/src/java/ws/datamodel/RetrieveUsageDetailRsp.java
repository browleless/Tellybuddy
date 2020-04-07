package ws.datamodel;

import entity.UsageDetail;

/**
 *
 * @author tjle2
 */
public class RetrieveUsageDetailRsp {

    private UsageDetail usageDetail;

    public RetrieveUsageDetailRsp() {
    }

    public RetrieveUsageDetailRsp(UsageDetail usageDetail) {
        this.usageDetail = usageDetail;
    }

    public UsageDetail getUsageDetail() {
        return usageDetail;
    }

    public void setUsageDetail(UsageDetail usageDetail) {
        this.usageDetail = usageDetail;
    }
}
