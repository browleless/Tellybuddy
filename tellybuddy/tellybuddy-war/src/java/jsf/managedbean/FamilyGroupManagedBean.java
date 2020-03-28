/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.managedbean;

import ejb.session.stateless.FamilyGroupSessionBeanLocal;
import entity.Customer;
import entity.FamilyGroup;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.primefaces.model.chart.MeterGaugeChartModel;

/**
 *
 * @author markt
 */
@Named(value = "familyGroupManagedBean")
@ViewScoped
public class FamilyGroupManagedBean implements Serializable {

    @EJB
    private FamilyGroupSessionBeanLocal familyGroupSessionBeanLocal;

    private List<FamilyGroup> familyGroups;
    private FamilyGroup familyGroupToView;
    private MeterGaugeChartModel donatedUnitsMeterGauge;
    private List<Customer> membersToView;
    
    public FamilyGroupManagedBean() {
    }

    @PostConstruct
    public void postConstruct() {
        setFamilyGroups(familyGroupSessionBeanLocal.retrieveAllFamilyGroups());
        setFamilyGroupToView(familyGroups.get(0));
        createMeterGaugeModels();
    }

    public FamilyGroupSessionBeanLocal getFamilyGroupSessionBeanLocal() {
        return familyGroupSessionBeanLocal;
    }

    public void setFamilyGroupSessionBeanLocal(FamilyGroupSessionBeanLocal familyGroupSessionBeanLocal) {
        this.familyGroupSessionBeanLocal = familyGroupSessionBeanLocal;
    }

    public List<FamilyGroup> getFamilyGroups() {
        return familyGroups;
    }

    public void setFamilyGroups(List<FamilyGroup> familyGroups) {
        this.familyGroups = familyGroups;
    }

    public FamilyGroup getFamilyGroupToView() {
        return familyGroupToView;
    }

    public void setFamilyGroupToView(FamilyGroup familyGroupToView) {
        this.familyGroupToView = familyGroupToView;
    }
//    public void retrieveFamilyGroupCustomers(){
//        setMembersToView(this.getFamilyGroupToView().getCustomers());
//        setMemberToView(membersToView.get(0));
//        if(membersToView == null | memberToView == null){
//            System.out.println("NULLLLLL_-------------------");
//        } else{
//            System.out.println("THERE ARE MEMBERS:   - " + membersToView.size());
//            for(Customer c: membersToView){
//                System.out.println(c.getFirstName());
//            }
//        }
//    }

    private MeterGaugeChartModel initMeterGaugeModel() {
        List<Number> intervals = new ArrayList<Number>();

        intervals.add(20);
        intervals.add(50);
        intervals.add(120);
        intervals.add(220);

        return new MeterGaugeChartModel(this.familyGroupToView.getDonatedUnits(), intervals);
    }

    private void createMeterGaugeModels() {
        donatedUnitsMeterGauge = initMeterGaugeModel();
        this.donatedUnitsMeterGauge.setTitle("Donated Units");
        this.donatedUnitsMeterGauge.setGaugeLabel("/units");
        this.donatedUnitsMeterGauge.setGaugeLabelPosition("bottom");
    }

    public MeterGaugeChartModel getDonatedUnitsMeterGauge() {
        return donatedUnitsMeterGauge;
    }

    public void setDonatedUnitsMeterGauge(MeterGaugeChartModel donatedUnitsMeterGauge) {
        this.donatedUnitsMeterGauge = donatedUnitsMeterGauge;
    }

    public List<Customer> getMembersToView() {
        return membersToView;
    }

    public void setMembersToView(List<Customer> membersToView) {
        this.membersToView = membersToView;
    }
}
