/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.managedbean;

import ejb.session.stateless.AnnouncementSessionBeanLocal;
import ejb.session.stateless.CustomerSessionBeanLocal;
import ejb.session.stateless.EmployeeSessionBeanLocal;
import ejb.session.stateless.SubscriptionSessonBeanLocal;
import ejb.session.stateless.TransactionSessionBeanLocal;
import entity.Announcement;
import entity.Customer;
import entity.Employee;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.primefaces.model.charts.line.LineChartModel;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.axes.cartesian.CartesianScales;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearAxes;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearTicks;
import org.primefaces.model.charts.bar.BarChartDataSet;
import org.primefaces.model.charts.bar.BarChartModel;
import org.primefaces.model.charts.bar.BarChartOptions;
import org.primefaces.model.charts.line.LineChartDataSet;
import org.primefaces.model.charts.optionconfig.legend.Legend;
import org.primefaces.model.charts.optionconfig.legend.LegendLabel;
import org.primefaces.model.charts.optionconfig.title.Title;
import org.primefaces.model.charts.pie.PieChartDataSet;
import org.primefaces.model.charts.pie.PieChartModel;
import org.primefaces.model.charts.pie.PieChartOptions;
import util.exception.EmployeeNotFoundException;

/**
 *
 * @author markt
 */
@Named(value = "dashboardManagedBean")
@ViewScoped
public class DashboardManagedBean implements Serializable {

    @EJB
    private EmployeeSessionBeanLocal employeeSessionBeanLocal;

    @EJB
    private SubscriptionSessonBeanLocal subscriptionSessonBeanLocal;

    @EJB
    private AnnouncementSessionBeanLocal announcementSessionBeanLocal;

    @EJB
    private TransactionSessionBeanLocal transactionSessionBeanLocal;

    @EJB
    private CustomerSessionBeanLocal customerSessionBeanLocal;

    private Employee currentEmployee;

    private LineChartModel subscriptionGrowthModel;

    private PieChartModel subscriptionLinesModel;

    private BarChartModel salesGrowthModel;

    private List<Announcement> employeeAnnouncements;

    private List<String> stickyNotes;

    private String newStickyNote = "";

    private String stickyNoteToDelete;

    @PostConstruct
    public void postConstruct() {
        this.currentEmployee = (Employee) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("currentEmployee");
        this.createLineModel();
        this.createPieModel();
        this.createBarModel();
        this.retrieveCurrentEmployeeAnnouncements();
        this.stickyNotes = currentEmployee.getStickyNotes();
    }

    public void addStickyNote() {
        if (stickyNotes.size() < 5) {
            stickyNotes.add(newStickyNote);
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Maximum of 5 sticky notes reached!", null));
        }
        try {
            employeeSessionBeanLocal.updateStickyNotes(stickyNotes, currentEmployee);
        } catch (EmployeeNotFoundException ex) {

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Unknown error occurred", null));
        }

        this.newStickyNote = "";
    }

    public void deleteStickyNote() {
        this.stickyNotes.remove(stickyNoteToDelete);

        try {
            employeeSessionBeanLocal.updateStickyNotes(stickyNotes, currentEmployee);
        } catch (EmployeeNotFoundException ex) {

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Unknown error occurred", null));
        }
        this.newStickyNote = "";
    }

    public void retrieveCurrentEmployeeAnnouncements() {
        this.employeeAnnouncements = announcementSessionBeanLocal.retrieveAllActiveAnnoucementsForEmployees();
    }

    public int retrievePendingCustomers() {
        return customerSessionBeanLocal.retrieveAllPendingCustomers().size();
    }

    public int retrievePendingSubscriptions() {
        return subscriptionSessonBeanLocal.retrieveAllPendingSubscriptions().size();
    }

    public BigDecimal retrieveMonthlyTransactions() {
        return transactionSessionBeanLocal.retrieveAllMonthlyTransactions().stream().map(x -> x.getTotalPrice()).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void createLineModel() {
        ChartData data = new ChartData();
        this.subscriptionGrowthModel = new LineChartModel();
        LineChartDataSet dataSet = new LineChartDataSet();
        List<Number> values = new ArrayList<>();
        values.add(65);
        values.add(59);
        values.add(80);
        values.add(81);
        values.add(56);
        values.add(55);
        values.add(40);
        dataSet.setData(values);
        dataSet.setFill(false);
        dataSet.setLabel("Subscription Growth");
        dataSet.setBorderColor("rgb(75, 192, 192)");
        dataSet.setLineTension(0.1);
        data.addChartDataSet(dataSet);

        List<String> labels = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();
        labels.add(currentDate.minusMonths(6).getMonth().toString());
        labels.add(currentDate.minusMonths(5).getMonth().toString());
        labels.add(currentDate.minusMonths(4).getMonth().toString());
        labels.add(currentDate.minusMonths(3).getMonth().toString());
        labels.add(currentDate.minusMonths(2).getMonth().toString());
        labels.add(currentDate.minusMonths(1).getMonth().toString());
        labels.add(currentDate.getMonth().toString());
        data.setLabels(labels);
        this.subscriptionGrowthModel.setData(data);
    }

    private void createPieModel() {
        this.subscriptionLinesModel = new PieChartModel();
        ChartData data = new ChartData();

        PieChartDataSet dataSet = new PieChartDataSet();
        List<String> bgColors = new ArrayList<>();
        bgColors.add("rgb(255, 99, 132)");
        bgColors.add("rgb(54, 162, 235)");
        bgColors.add("rgb(255, 205, 86)");
        dataSet.setBackgroundColor(bgColors);

        List<String> labels = new ArrayList<>();

//        ArrayList<Integer> subscriptionLines = new ArrayList<>(20);
        int[] subscriptionLines = new int[20];
        for (Customer c : customerSessionBeanLocal.retrieveAllCustomer()) {
            int count = customerSessionBeanLocal.retrieveNoActiveSubscriptions(c);
            subscriptionLines[count]++;
        }
        ArrayList<Number> subscriptionValues = new ArrayList<>();

        for (int i = 0; i < subscriptionLines.length; i++) {
            if (subscriptionLines[i] != 0) {
                subscriptionValues.add(subscriptionLines[i]);
                labels.add("Active: " + i);
            }
        }
        data.setLabels(labels);
        dataSet.setData(subscriptionValues);
        data.addChartDataSet(dataSet);

        PieChartOptions options = new PieChartOptions();
        Title title = new Title();
        title.setDisplay(true);
        title.setText("No. of Customers with # Active Subscriptions");
        options.setTitle(title);
        subscriptionLinesModel.setOptions(options);
        subscriptionLinesModel.setData(data);
    }

    public void createBarModel() {
        salesGrowthModel = new BarChartModel();
        ChartData data = new ChartData();

        BarChartDataSet barDataSet = new BarChartDataSet();
        barDataSet.setLabel("$ (in Millions)");

        List<Number> values = new ArrayList<>();
        values.add(65);
        values.add(59);
        values.add(80);
        values.add(81);
        values.add(56);
        values.add(55);
        values.add(40);
        barDataSet.setData(values);

        List<String> bgColor = new ArrayList<>();
        bgColor.add("rgba(255, 159, 64, 0.2)");
        bgColor.add("rgba(255, 159, 64, 0.2)");
        bgColor.add("rgba(255, 159, 64, 0.2)");
        bgColor.add("rgba(255, 159, 64, 0.2)");
        bgColor.add("rgba(255, 159, 64, 0.2)");
        bgColor.add("rgba(255, 159, 64, 0.2)");
        bgColor.add("rgba(255, 159, 64, 0.2)");
        barDataSet.setBackgroundColor(bgColor);

        List<String> borderColor = new ArrayList<>();
        borderColor.add("rgb(255, 159, 64)");
        borderColor.add("rgb(255, 159, 64)");
        borderColor.add("rgb(255, 159, 64)");
        borderColor.add("rgb(255, 159, 64)");
        borderColor.add("rgb(255, 159, 64)");
        borderColor.add("rgb(255, 159, 64)");
        borderColor.add("rgb(255, 159, 64)");
        barDataSet.setBorderColor(borderColor);
        barDataSet.setBorderWidth(1);

        data.addChartDataSet(barDataSet);

        List<String> labels = new ArrayList<>();
        labels.add("January");
        labels.add("February");
        labels.add("March");
        labels.add("April");
        labels.add("May");
        labels.add("June");
        labels.add("July");
        data.setLabels(labels);
        salesGrowthModel.setData(data);

        //Options
        BarChartOptions options = new BarChartOptions();
        CartesianScales cScales = new CartesianScales();
        CartesianLinearAxes linearAxes = new CartesianLinearAxes();
        CartesianLinearTicks ticks = new CartesianLinearTicks();
        ticks.setBeginAtZero(true);
        linearAxes.setTicks(ticks);
        cScales.addYAxesData(linearAxes);
        options.setScales(cScales);

        Title title = new Title();
        title.setDisplay(true);
        title.setText("Monthly Sales Revenue");
        options.setTitle(title);

        Legend legend = new Legend();
        legend.setDisplay(true);
        legend.setPosition("top");
        LegendLabel legendLabels = new LegendLabel();
        legendLabels.setFontStyle("bold");
        legendLabels.setFontColor("#2980B9");
        legendLabels.setFontSize(12);
        legend.setLabels(legendLabels);
        options.setLegend(legend);

        salesGrowthModel.setOptions(options);
    }

    public Employee getCurrentEmployee() {
        return currentEmployee;
    }

    public void setCurrentEmployee(Employee currentEmployee) {
        this.currentEmployee = currentEmployee;
    }

    public CustomerSessionBeanLocal getCustomerSessionBeanLocal() {
        return customerSessionBeanLocal;
    }

    public void setCustomerSessionBeanLocal(CustomerSessionBeanLocal customerSessionBeanLocal) {
        this.customerSessionBeanLocal = customerSessionBeanLocal;
    }

    public TransactionSessionBeanLocal getTransactionSessionBeanLocal() {
        return transactionSessionBeanLocal;
    }

    public void setTransactionSessionBeanLocal(TransactionSessionBeanLocal transactionSessionBeanLocal) {
        this.transactionSessionBeanLocal = transactionSessionBeanLocal;
    }

    public LineChartModel getSubscriptionGrowthModel() {
        return subscriptionGrowthModel;
    }

    public void setSubscriptionGrowthModel(LineChartModel subscriptionGrowthModel) {
        this.subscriptionGrowthModel = subscriptionGrowthModel;
    }

    public PieChartModel getSubscriptionLinesModel() {
        return subscriptionLinesModel;
    }

    public void setSubscriptionLinesModel(PieChartModel subscriptionLinesModel) {
        this.subscriptionLinesModel = subscriptionLinesModel;
    }

    public BarChartModel getSalesGrowthModel() {
        return salesGrowthModel;
    }

    public void setSalesGrowthModel(BarChartModel salesGrowthModel) {
        this.salesGrowthModel = salesGrowthModel;
    }

    public AnnouncementSessionBeanLocal getAnnouncementSessionBeanLocal() {
        return announcementSessionBeanLocal;
    }

    public void setAnnouncementSessionBeanLocal(AnnouncementSessionBeanLocal announcementSessionBeanLocal) {
        this.announcementSessionBeanLocal = announcementSessionBeanLocal;
    }

    public List<Announcement> getEmployeeAnnouncements() {
        return employeeAnnouncements;
    }

    public void setEmployeeAnnouncements(List<Announcement> employeeAnnouncements) {
        this.employeeAnnouncements = employeeAnnouncements;
    }

    public SubscriptionSessonBeanLocal getSubscriptionSessonBeanLocal() {
        return subscriptionSessonBeanLocal;
    }

    public void setSubscriptionSessonBeanLocal(SubscriptionSessonBeanLocal subscriptionSessonBeanLocal) {
        this.subscriptionSessonBeanLocal = subscriptionSessonBeanLocal;
    }

    public List<String> getStickyNotes() {
        return stickyNotes;
    }

    public void setStickyNotes(List<String> stickyNotes) {
        this.stickyNotes = stickyNotes;
    }

    public String getNewStickyNote() {
        return newStickyNote;
    }

    public void setNewStickyNote(String newStickyNote) {
        this.newStickyNote = newStickyNote;
    }

    public String getStickyNoteToDelete() {
        return stickyNoteToDelete;
    }

    public void setStickyNoteToDelete(String stickyNoteToDelete) {
        this.stickyNoteToDelete = stickyNoteToDelete;
    }

    public EmployeeSessionBeanLocal getEmployeeSessionBeanLocal() {
        return employeeSessionBeanLocal;
    }

    public void setEmployeeSessionBeanLocal(EmployeeSessionBeanLocal employeeSessionBeanLocal) {
        this.employeeSessionBeanLocal = employeeSessionBeanLocal;
    }

}
