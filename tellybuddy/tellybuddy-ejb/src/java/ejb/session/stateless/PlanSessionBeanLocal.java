/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Plan;
import java.util.List;
import javax.ejb.Local;
import util.exception.PlanAlreadyDisabledException;

/**
 *
 * @author ngjin
 */
@Local
public interface PlanSessionBeanLocal {

    public Long createNewPlan(Plan newPlan);

    public List<Plan> retrieveAllPlans();

    public List<Plan> retrieveAllValidPlans();

    public Plan retrievePlanByPlanId(Long planID);

    public void updatePlan(Plan plan) throws PlanAlreadyDisabledException;

    public void deletePlan(Long planId);
    
}
