/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.managedbean;

import ejb.session.stateless.EmployeeSessionBeanLocal;
import entity.Employee;
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


}