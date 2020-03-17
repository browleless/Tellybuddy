package jsf.managedbean;

import ejb.session.stateless.PlanSessionBeanLocal;
import entity.Plan;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.primefaces.event.SelectEvent;
import util.exception.InputDataValidationException;
import util.exception.PlanAlreadyDisabledException;
import util.exception.PlanExistException;
import util.exception.PlanNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author tjle2
 */
@Named(value = "flashDealManagementManagedBean")
@ViewScoped

public class FlashDealManagementManagedBean implements Serializable {

    @EJB(name = "PlanSessionBeanLocal")
    private PlanSessionBeanLocal planSessionBeanLocal;

    private List<Plan> plans;
    private List<Plan> filteredPlans;

    private Date dateTimeNow;
    private Date dateToday;

    private Plan newPlan;

    private Plan planToView;
    private Plan planToUpdate;

    private String selectedFilter;

    public FlashDealManagementManagedBean() {

        newPlan = new Plan();
        dateTimeNow = new Date();
        dateToday = new Date();
        selectedFilter = "Ongoing";
        dateToday.setHours(0);
        dateToday.setMinutes(0);
        dateToday.setSeconds(0);
    }

    @PostConstruct
    public void postConstruct() {

        setPlans(planSessionBeanLocal.retrieveAllActiveFlashPlans());
    }

    public void createNewFlashPlan(ActionEvent event) {

        try {
            Long newPlanId = planSessionBeanLocal.createNewPlan(getNewPlan());
            if ((newPlan.getStartTime().before(dateTimeNow) && selectedFilter.equals("Ongoing")) || newPlan.getStartTime().after(dateTimeNow) && selectedFilter.equals("Upcoming")) {
                getPlans().add(getNewPlan());
            }
            setNewPlan(new Plan());

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "New flash plan created successfully (Plan ID: " + newPlanId + ")", null));
        } catch (InputDataValidationException | PlanExistException | UnknownPersistenceException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred creating new flash plan: " + ex.getMessage(), null));
        }
    }

    public void updateFlashPlan(ActionEvent event) {

        try {
            planSessionBeanLocal.updatePlan(getPlanToUpdate());
            if ((planToUpdate.getStartTime().before(dateTimeNow) && selectedFilter.equals("Upcoming")) || planToUpdate.getStartTime().after(dateTimeNow) && selectedFilter.equals("Ongoing")) {
                getPlans().remove(getPlanToUpdate());
            }

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Flash plan updated successfully", null));
        } catch (PlanAlreadyDisabledException | PlanNotFoundException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while updating selected plan: " + ex.getMessage(), null));
        }
    }

    public void deleteFlashPlan(ActionEvent event) {

        try {
            Plan planToDelete = (Plan) event.getComponent().getAttributes().get("planToDelete");
            planSessionBeanLocal.deletePlan(planToDelete.getPlanId());
            getPlans().remove(planToDelete);

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Flash plan deleted successfully", null));
        } catch (PlanNotFoundException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while deleting plan: " + ex.getMessage(), null));
        }
    }

    public long calculateTimerTime(Plan plan) {

        if (plan.getStartTime().before(new Date())) {
            return (plan.getEndTime().getTime() - dateTimeNow.getTime()) / 1000;
        } else {
            return (plan.getStartTime().getTime() - dateTimeNow.getTime()) / 1000;
        }
    }

    public void doFilter() {

        if (selectedFilter.equals("Ongoing")) {
            plans = planSessionBeanLocal.retrieveAllActiveFlashPlans();
        } else if (selectedFilter.equals("Upcoming")) {
            plans = planSessionBeanLocal.retrieveAllUpcomingFlashPlans();
        }
    }

    public List<Plan> getPlans() {
        return plans;
    }

    public void setPlans(List<Plan> plans) {
        this.plans = plans;
    }

    public Date getDateTimeNow() {
        return dateTimeNow;
    }

    public void setDateTimeNow(Date dateTimeNow) {
        this.dateTimeNow = dateTimeNow;
    }

    public Plan getNewPlan() {
        return newPlan;
    }

    public void setNewPlan(Plan newPlan) {
        this.newPlan = newPlan;
    }

    public Plan getPlanToView() {
        return planToView;
    }

    public void setPlanToView(Plan planToView) {
        this.planToView = planToView;
    }

    public Plan getPlanToUpdate() {
        return planToUpdate;
    }

    public void setPlanToUpdate(Plan planToUpdate) {
        this.planToUpdate = planToUpdate;
    }

    public List<Plan> getFilteredPlans() {
        return filteredPlans;
    }

    public void setFilteredPlans(List<Plan> filteredPlans) {
        this.filteredPlans = filteredPlans;
    }

    public Date getDateToday() {
        return dateToday;
    }

    public void setDateToday(Date dateToday) {
        this.dateToday = dateToday;
    }

    public String getSelectedFilter() {
        return selectedFilter;
    }

    public void setSelectedFilter(String selectedFilter) {
        this.selectedFilter = selectedFilter;
    }

}
