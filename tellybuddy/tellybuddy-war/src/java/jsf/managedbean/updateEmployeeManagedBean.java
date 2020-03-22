/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.managedbean;

import ejb.session.stateless.EmployeeSessionBeanLocal;
import entity.Employee;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import util.exception.EmployeeNotFoundException;

/**
 *
 * @author kaikai
 */
@Named(value = "updateEmployeeManagedBean")
@RequestScoped
public class updateEmployeeManagedBean {

    @EJB(name = "EmployeeSessionBeanLocal")
    private EmployeeSessionBeanLocal employeeSessionBeanLocal;

    private Employee currentEmployee;
    private Employee employeeToUpdate;

    public updateEmployeeManagedBean() {

    }

    @PostConstruct
    public void postConstruct() {
        employeeToUpdate = new Employee();
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        Map<String, Object> sessionMap = externalContext.getSessionMap();
        currentEmployee = (Employee) sessionMap.get("currentEmployee");
        employeeToUpdate = currentEmployee;
    }

    public void updateEmployee(ActionEvent event) {

        try {
            employeeSessionBeanLocal.updateEmployee(getEmployeeToUpdate());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Employee updated successfully", null));
        } catch (EmployeeNotFoundException ex) {
            Logger.getLogger(updateEmployeeManagedBean.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public Employee getCurrentEmployee() {
        return currentEmployee;
    }

    public void setCurrentEmployee(Employee currentEmployee) {
        this.currentEmployee = currentEmployee;
    }

    public Employee getEmployeeToUpdate() {
        return employeeToUpdate;
    }

    public void setEmployeeToUpdate(Employee employeeToUpdate) {
        this.employeeToUpdate = employeeToUpdate;
    }
    

//    public void upload() {
//        if (file != null) {
//            FacesMessage message = new FacesMessage("Successful", file.getFileName() + " is uploaded.");
//            FacesContext.getCurrentInstance().addMessage(null, message);
//        }
//    }
//
//    public void handleFileUpload(FileUploadEvent event) {
//        FacesMessage msg = new FacesMessage("Successful", event.getFile().getFileName() + " is uploaded.");
//        try {
//            event.getFile().getInputstream();
//        } catch (IOException ex) {
//            Logger.getLogger(updateEmployeeManagedBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        FacesContext.getCurrentInstance().addMessage(null, msg);
//    }
}
