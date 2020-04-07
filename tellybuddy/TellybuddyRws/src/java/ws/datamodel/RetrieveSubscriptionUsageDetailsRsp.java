package ws.datamodel;

import entity.UsageDetail;
import java.util.List;

/**
 *
 * @author tjle2
 */
public class RetrieveSubscriptionUsageDetailsRsp {
    
    private List<UsageDetail> usageDetails;

    public RetrieveSubscriptionUsageDetailsRsp() {
    }

    public RetrieveSubscriptionUsageDetailsRsp(List<UsageDetail> usageDetails) {
        this.usageDetails = usageDetails;
    }

    public List<UsageDetail> getUsageDetails() {
        return usageDetails;
    }

    public void setUsageDetails(List<UsageDetail> usageDetails) {
        this.usageDetails = usageDetails;
    }
}
