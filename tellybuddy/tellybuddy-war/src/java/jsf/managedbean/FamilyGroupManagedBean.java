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
    private MeterGaugeChartModel dataUnitsMeterGauge;
    private MeterGaugeChartModel smsUnitsMeterGauge;
    private MeterGaugeChartModel talktimeUnitsMeterGauge;
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

    private MeterGaugeChartModel initMeterGaugeModel(String type) {
        List<Number> intervals = new ArrayList<Number>();

        intervals.add(20);
        intervals.add(50);
        intervals.add(120);
        intervals.add(220);
        MeterGaugeChartModel temp;
        if (type.toLowerCase().equals("data")) {
            temp = new MeterGaugeChartModel(this.familyGroupToView.getDonatedDataUnits(), intervals);
            temp.setTitle("Donated data Units");
        } else if (type.toLowerCase().equals("sms")) {
            temp = new MeterGaugeChartModel(this.familyGroupToView.getDonatedSMSUnits(), intervals);
            temp.setTitle("Donated SMS Units");
        } else {
            temp = new MeterGaugeChartModel(this.familyGroupToView.getDonatedTalkTimeUnits(), intervals);
            temp.setTitle("Donated TalkTime Units");
        }
        temp.setGaugeLabel("/units");
        temp.setGaugeLabelPosition("bottom");
        return temp;
    }

    private void createMeterGaugeModels() {
        dataUnitsMeterGauge = initMeterGaugeModel("data");
        smsUnitsMeterGauge = initMeterGaugeModel("sms");
        talktimeUnitsMeterGauge = initMeterGaugeModel("talktime");

    }


    public List<Customer> getMembersToView() {
        return membersToView;
    }

    public void setMembersToView(List<Customer> membersToView) {
        this.membersToView = membersToView;
    }

    public MeterGaugeChartModel getDataUnitsMeterGauge() {
        return dataUnitsMeterGauge;
    }

    public void setDataUnitsMeterGauge(MeterGaugeChartModel dataUnitsMeterGauge) {
        this.dataUnitsMeterGauge = dataUnitsMeterGauge;
    }

    public MeterGaugeChartModel getSmsUnitsMeterGauge() {
        return smsUnitsMeterGauge;
    }

    public void setSmsUnitsMeterGauge(MeterGaugeChartModel smsUnitsMeterGauge) {
        this.smsUnitsMeterGauge = smsUnitsMeterGauge;
    }

    public MeterGaugeChartModel getTalktimeUnitsMeterGauge() {
        return talktimeUnitsMeterGauge;
    }

    public void setTalktimeUnitsMeterGauge(MeterGaugeChartModel talktimeUnitsMeterGauge) {
        this.talktimeUnitsMeterGauge = talktimeUnitsMeterGauge;
    }
}
