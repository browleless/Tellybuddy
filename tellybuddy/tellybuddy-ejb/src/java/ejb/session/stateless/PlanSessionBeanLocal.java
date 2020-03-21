/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Plan;
import java.util.List;
import javax.ejb.Local;
import util.exception.InputDataValidationException;
import util.exception.PlanAlreadyDisabledException;
import util.exception.PlanExistException;
import util.exception.PlanNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author ngjin
 */
@Local
public interface PlanSessionBeanLocal {

    public Long createNewPlan(Plan newPlan) throws PlanExistException, UnknownPersistenceException, InputDataValidationException;

    public List<Plan> retrieveAllPlans();

    public List<Plan> retrieveAllValidPlans();

    public Plan retrievePlanByPlanId(Long planID) throws PlanNotFoundException;

    public void updatePlan(Plan plan) throws PlanAlreadyDisabledException, PlanNotFoundException;

    public void deletePlan(Long planId) throws PlanNotFoundException;

    public List<Plan> retrieveAllActiveFlashPlans();

    public List<Plan> retrieveAllUpcomingFlashPlans();

}
