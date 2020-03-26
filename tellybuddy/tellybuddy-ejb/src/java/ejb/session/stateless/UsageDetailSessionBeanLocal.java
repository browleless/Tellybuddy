package ejb.session.stateless;

import entity.Subscription;
import entity.UsageDetail;
import javax.ejb.Local;
import util.exception.SubscriptionNotFoundException;
import util.exception.UsageDetailNotFoundException;

/**
 *
 * @author tjle2
 */
@Local
public interface UsageDetailSessionBeanLocal {

    public Long createNewUsageDetail(Subscription subcription) throws SubscriptionNotFoundException;

    public UsageDetail retrieveUsageDetailByUsageDetailId(Long usageDetailId) throws UsageDetailNotFoundException;

    public void updateUsageDetail(UsageDetail usageDetail) throws UsageDetailNotFoundException;
    
}
