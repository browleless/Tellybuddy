/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Plan;
import java.util.List;
import java.util.Set;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exception.PlanAlreadyDisabledException;
import util.exception.PlanExistException;
import util.exception.PlanNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.InputDataValidationException;

/**
 *
 * @author ngjin
 */
@Stateless
public class PlanSessionBean implements PlanSessionBeanLocal {

    @PersistenceContext(unitName = "tellybuddy-ejbPU")
    private EntityManager em;

    private final ValidatorFactory validatorFactory;

    private final Validator validator;

    public PlanSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public Long createNewPlan(Plan newPlan) throws PlanExistException, UnknownPersistenceException,
            InputDataValidationException {
        //check uniqueness of the planName using bean validator before persisting

        Set<ConstraintViolation<Plan>> constraintViolations = validator.validate(newPlan);

        if (constraintViolations.isEmpty()) {
            try {
                em.persist(newPlan);
                em.flush();

                return newPlan.getPlanId();
            } catch (PersistenceException ex) {
                if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                        throw new PlanExistException("Plan cannot be created as there is already a plan created with the same plan name!");
                    } else {
                        throw new UnknownPersistenceException(ex.getMessage());
                    }
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            }
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }

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
    public Plan retrievePlanByPlanId(Long planID) throws PlanNotFoundException {

        Plan plan = em.find(Plan.class, planID);

        if (plan != null) {
            return plan;
        } else {
            throw new PlanNotFoundException("Plan ID " + planID + " does not exist!");
        }
    }

    @Override
    public void updatePlan(Plan plan) throws PlanAlreadyDisabledException, PlanNotFoundException {
        //assumption here is that we cannot change the planName once edited because each planName must be unique
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
        } else {
            throw new PlanNotFoundException("Plan ID " + plan.getPlanId() + " cannot be updated as it does not exist!");
        }
    }

    @Override
    public void deletePlan(Long planId) throws PlanNotFoundException {
        Plan planToDelete = retrievePlanByPlanId(planId);

        if (!planToDelete.getIsInUse()) {
            em.remove(planToDelete);
        } else {
            planToDelete.setIsDisabled(true);
        }
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<Plan>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }

}
