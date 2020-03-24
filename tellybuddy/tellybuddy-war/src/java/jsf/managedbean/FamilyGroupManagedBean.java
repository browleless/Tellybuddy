/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.managedbean;

import ejb.session.stateless.FamilyGroupSessionBeanLocal;
import entity.FamilyGroup;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.view.ViewScoped;

/**
 *
 * @author markt
 */
@Named(value = "familyGroupManagedBean")
@ViewScoped
public class FamilyGroupManagedBean implements Serializable{

    @EJB
    private FamilyGroupSessionBeanLocal familyGroupSessionBeanLocal;

    private List<FamilyGroup> familyGroups;
    private FamilyGroup familyGroupToView;

    public FamilyGroupManagedBean() {

    }

    @PostConstruct
    public void postConstruct() {
        setFamilyGroups(familyGroupSessionBeanLocal.retrieveAllFamilyGroups());
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

}
