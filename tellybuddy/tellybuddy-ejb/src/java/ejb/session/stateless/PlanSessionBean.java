/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Plan;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.PlanAlreadyDisabledException;

/**
 *
 * @author ngjin
 */
@Stateless
public class PlanSessionBean implements PlanSessionBeanLocal {

    @PersistenceContext(unitName = "tellybuddy-ejbPU")
    private EntityManager em;

    @Override
    public Long createNewPlan(Plan newPlan) {
        em.persist(newPlan);
        em.flush();

        return newPlan.getPlanId();
    }

    @Override
    public List<Plan> retrieveAllPlans() {
        Query q = em.createQuery("SELECT p FROM Plan p");

        return q.getResultList();
    }

    @Override
    public List<Plan> retrieveAllValidPlans() {
        Query q = em.createQuery("SELECT p FROM Plan p WHERE p.isDisabled = FALSE"
                + "ORDER BY p.startTime ASC, p.endTime ASC");

        return q.getResultList();
    }

    @Override
    public Plan retrievePlanByPlanId(Long planID) {
        Query q = em.createQuery("SELECT p FROM Plan p WHERE p.planId = :inPlanId");
        q.setParameter("inPlanId", planID);

        return (Plan) q.getSingleResult();
    }

    @Override
    public void updatePlan(Plan plan) throws PlanAlreadyDisabledException {
        if (plan.getPlanId() != null) {
            Plan planToUpdate = retrievePlanByPlanId(plan.getPlanId());

            if (!planToUpdate.getIsDisabled()) {
                planToUpdate.setAddOnPrice(plan.getAddOnPrice());
                planToUpdate.setDataConversionRate(plan.getDataConversionRate());
                planToUpdate.setEndTime(plan.getEndTime());
                planToUpdate.setPrice(plan.getPrice());
                planToUpdate.setSmsConversionRate(plan.getSmsConversionRate());
                planToUpdate.setTalktimeConversionRate(plan.getTalktimeConversionRate());
                planToUpdate.setTotalBasicUnits(plan.getTotalBasicUnits());

            } else { //plan is already disabled, cannot update
                throw new PlanAlreadyDisabledException("Plan " + plan.getPlanId() + "cannot be updated as it has already been disabled!");
            }
        }
    }

    @Override
    public void deletePlan(Long planId) {
        Plan planToDelete = retrievePlanByPlanId(planId);

        if (!planToDelete.getIsInUse()) {
            em.remove(planToDelete);
        } else {
            planToDelete.setIsDisabled(true);
        }
    }

}
