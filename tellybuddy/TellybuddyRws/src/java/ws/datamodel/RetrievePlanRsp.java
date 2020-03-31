package ws.datamodel;

import entity.Plan;

/**
 *
 * @author tjle2
 */
public class RetrievePlanRsp {

    private Plan plan;

    public RetrievePlanRsp() {
    }

    public RetrievePlanRsp(Plan plan) {
        this.plan = plan;
    }

    public Plan getPlan() {
        return plan;
    }

    public void setPlan(Plan plan) {
        this.plan = plan;
    }
}
