package ws.datamodel;

import entity.Plan;
import java.util.List;

/**
 *
 * @author tjle2
 */
public class RetrieveAllActiveFlashPlansRsp {
 
    private List<Plan> plans;

    public RetrieveAllActiveFlashPlansRsp() {
    }

    public RetrieveAllActiveFlashPlansRsp(List<Plan> plans) {
        this.plans = plans;
    }

    public List<Plan> getPlans() {
        return plans;
    }

    public void setPlans(List<Plan> plans) {
        this.plans = plans;
    }
}
