package jsf.managedbean;

import entity.Plan;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.faces.view.ViewScoped;

/**
 *
 * @author tjle2
 */
@Named(value = "viewPlanManagedBean")
@ViewScoped

public class ViewPlanManagedBean implements Serializable {

    private Plan planToView;

    public ViewPlanManagedBean() {

        planToView = new Plan();
    }

    @PostConstruct
    public void postConstruct() {
    }

    public Plan getPlanToView() {
        return planToView;
    }

    public void setPlanToView(Plan planToView) {
        this.planToView = planToView;
    }

}
