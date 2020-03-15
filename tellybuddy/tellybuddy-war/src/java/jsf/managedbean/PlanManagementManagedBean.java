package jsf.managedbean;

import ejb.session.stateless.PlanSessionBeanLocal;
import entity.Plan;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import util.exception.InputDataValidationException;
import util.exception.PlanAlreadyDisabledException;
import util.exception.PlanExistException;
import util.exception.PlanNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author tjle2
 */
@Named(value = "planManagementManagedBean")
@ViewScoped

public class PlanManagementManagedBean implements Serializable {

    @EJB
    private PlanSessionBeanLocal planSessionBeanLocal;

    @Inject
    private ViewPlanManagedBean viewPlanManagedBean;

    private List<Plan> plans;
    private List<Plan> filteredPlans;

    private Plan newPlan;

    private Plan planToUpdate;

    public PlanManagementManagedBean() {

        newPlan = new Plan();
    }

    @PostConstruct
    public void postConstruct() {

        setPlans(planSessionBeanLocal.retrieveAllPlans());
    }
    
    public void createNewPlan(ActionEvent event) {

        try {
            Long newPlanId = planSessionBeanLocal.createNewPlan(getNewPlan());
            setNewPlan(new Plan());

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "New plan created successfully (Plann ID: " + newPlanId + ")", null));
        } catch (InputDataValidationException | PlanExistException | UnknownPersistenceException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred creating new plan: " + ex.getMessage(), null));
        }
    }

    public void updatePlan(ActionEvent event) {

        try {
            planSessionBeanLocal.updatePlan(getPlanToUpdate());

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Plan updated successfully", null));
        } catch (PlanAlreadyDisabledException | PlanNotFoundException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while updating seleted plan: " + ex.getMessage(), null));
        }
    }

    public void deletePlan(ActionEvent event) {

        try {
            Plan planToDelete = (Plan) event.getComponent().getAttributes().get("planToDelete");
            planSessionBeanLocal.deletePlan(planToDelete.getPlanId());

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Plan deleted successfully", null));
        } catch (PlanNotFoundException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while deleting plan: " + ex.getMessage(), null));
        }
    }

    public ViewPlanManagedBean getViewPlanManagedBean() {
        return viewPlanManagedBean;
    }

    public void setViewPlanManagedBean(ViewPlanManagedBean viewPlanManagedBean) {
        this.viewPlanManagedBean = viewPlanManagedBean;
    }

    public List<Plan> getPlans() {
        return plans;
    }

    public void setPlans(List<Plan> plans) {
        this.plans = plans;
    }

    public List<Plan> getFilteredPlans() {
        return filteredPlans;
    }

    public void setFilteredPlans(List<Plan> filteredPlans) {
        this.filteredPlans = filteredPlans;
    }

    public Plan getNewPlan() {
        return newPlan;
    }

    public void setNewPlan(Plan newPlan) {
        this.newPlan = newPlan;
    }

    public Plan getPlanToUpdate() {
        return planToUpdate;
    }

    public void setPlanToUpdate(Plan planToUpdate) {
        this.planToUpdate = planToUpdate;
    }

}
