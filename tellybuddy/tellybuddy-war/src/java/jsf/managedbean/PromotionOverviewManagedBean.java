/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.managedbean;

import ejb.session.stateless.PlanSessionBeanLocal;
import ejb.session.stateless.ProductSessionBeanLocal;
import entity.Plan;
import entity.Product;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

/**
 *
 * @author kaikai
 */
@Named(value = "promotionOverviewManagedBean")
@ViewScoped
public class PromotionOverviewManagedBean implements Serializable {

    @EJB(name = "PlanSessionBeanLocal")
    private PlanSessionBeanLocal planSessionBeanLocal;

    @EJB(name = "ProductSessionBeanLocal")
    private ProductSessionBeanLocal productSessionBeanLocal;

    private ScheduleModel scheduleModel;
    private ScheduleEvent scheduleEvent;

    public PromotionOverviewManagedBean() {
        scheduleModel = new DefaultScheduleModel();
        scheduleEvent = new DefaultScheduleEvent();
    }

    @PostConstruct
    public void postConstruct() {
        List<Plan> promotionPlans = planSessionBeanLocal.retrieveAllActiveFlashPlans();
        for (Plan p : promotionPlans) {
            scheduleModel.addEvent(new DefaultScheduleEvent(p.getName(), p.getStartTime(), p.getEndTime()));
        }
        List<Product> promotionProducts = productSessionBeanLocal.retrieveAllDiscountedProducts();
        for (Product p : promotionProducts) {
            scheduleModel.addEvent(new DefaultScheduleEvent(p.getName(), p.getDealStartTime(), p.getDealEndTime()));
        }
    }

    public ScheduleModel getScheduleModel() {
        return scheduleModel;
    }

    public void setScheduleModel(ScheduleModel scheduleModel) {
        this.scheduleModel = scheduleModel;
    }

    public ScheduleEvent getScheduleEvent() {
        return scheduleEvent;
    }

    public void setScheduleEvent(ScheduleEvent scheduleEvent) {
        this.scheduleEvent = scheduleEvent;
    }

    public void addEvent(ActionEvent actionEvent) {
        if (scheduleEvent.getId() == null) {
            scheduleModel.addEvent(scheduleEvent);
        } else {
            scheduleModel.updateEvent(scheduleEvent);
        }

        scheduleEvent = new DefaultScheduleEvent();
    }

    public void onEventSelect(SelectEvent selectEvent) {
        scheduleEvent = (ScheduleEvent) selectEvent.getObject();
    }

    public void onDateSelect(SelectEvent selectEvent) {
        scheduleEvent = new DefaultScheduleEvent("", (Date) selectEvent.getObject(), (Date) selectEvent.getObject());
    }

    public void onEventMove(ScheduleEntryMoveEvent scheduleEvent) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event moved", "Day delta:" + scheduleEvent.getDayDelta() + ", Minute delta:" + scheduleEvent.getMinuteDelta());

        addMessage(message);
    }

    public void onEventResize(ScheduleEntryResizeEvent scheduleEvent) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event resized", "Day delta:" + scheduleEvent.getDayDelta() + ", Minute delta:" + scheduleEvent.getMinuteDelta());

        addMessage(message);
    }

    private void addMessage(FacesMessage message) {
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    private Calendar today() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0, 0, 0);

        return calendar;
    }
}
