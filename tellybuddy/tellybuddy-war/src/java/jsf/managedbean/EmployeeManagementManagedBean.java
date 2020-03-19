/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.managedbean;

import ejb.session.stateless.EmployeeSessionBeanLocal;
import entity.Employee;
import static java.lang.System.console;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import util.exception.EmployeeNotFoundException;
import util.exception.EmployeeUsernameExistException;
import util.exception.InputDataValidationException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author kaikai
 */
@Named(value = "employeeManagementManagedBean")
@RequestScoped
public class EmployeeManagementManagedBean {

    @EJB(name = "EmployeeSessionBeanLocal")
    private EmployeeSessionBeanLocal employeeSessionBeanLocal;
    private List<Employee> employees;
    private List<Employee> filteredEmployees;
    private Employee newEmployee;

    public EmployeeManagementManagedBean() {
        newEmployee = new Employee();
    }

    @PostConstruct
    public void postConstruct() {

        setEmployees(employeeSessionBeanLocal.retrieveAllEmployees());
    }

    public void createNewEmployee(ActionEvent event) {

        try {
            System.out.println("reach here");
            Long newEmployeeId = employeeSessionBeanLocal.createNewEmployee(getNewEmployee());
            employees.add(newEmployee);
            setNewEmployee(new Employee());

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "New employee created successfully (Employee ID: " + newEmployeeId + ")", null));
        } catch (EmployeeUsernameExistException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred creating new employee: " + ex.getMessage(), null));
        } catch (UnknownPersistenceException ex) {
            Logger.getLogger(EmployeeManagementManagedBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InputDataValidationException ex) {
            Logger.getLogger(EmployeeManagementManagedBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void deleteEmployee(ActionEvent event) {

        try {
            Employee employeeToDelete = (Employee) event.getComponent().getAttributes().get("employeeToDelete");
            employeeSessionBeanLocal.deleteEmployee(employeeToDelete);
            employees.remove(employeeToDelete);

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Employee deleted successfully", null));
        } catch (EmployeeNotFoundException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while deleting employee: " + ex.getMessage(), null));
        }
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    public List<Employee> getFilteredEmployees() {
        return filteredEmployees;
    }

    public void setFilteredEmployees(List<Employee> filteredEmployees) {
        this.filteredEmployees = filteredEmployees;
    }

    public Employee getNewEmployee() {
        return newEmployee;
    }

    public void setNewEmployee(Employee newEmployee) {
        this.newEmployee = newEmployee;
    }

}
