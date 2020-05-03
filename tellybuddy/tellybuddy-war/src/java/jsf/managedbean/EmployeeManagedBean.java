/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.managedbean;

import ejb.session.stateless.EmployeeSessionBeanLocal;
import entity.Employee;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.primefaces.shaded.commons.io.FilenameUtils;
import util.exception.EmployeeNotFoundException;
import util.exception.EmployeeUsernameExistException;
import util.exception.InputDataValidationException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author kaikai
 */
@Named(value = "employeeManagedBean")
@ViewScoped
public class EmployeeManagedBean implements Serializable {

    /**
     * Creates a new instance of EmployeeManagedBean
     */
    @EJB(name = "EmployeeSessionBeanLocal")
    private EmployeeSessionBeanLocal employeeSessionBeanLocal;
    private List<Employee> employees;
    private List<Employee> filteredEmployees;
    private Employee newEmployee;
    private Employee employeeToUpdate;
    private UploadedFile employeeProfileImageFile;

    public EmployeeManagedBean() {
        newEmployee = new Employee();
    }

    @PostConstruct
    public void postConstruct() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        Map<String, Object> sessionMap = externalContext.getSessionMap();
        Employee currentEmployee = (Employee) sessionMap.get("currentEmployee");
        List<Employee> temp = employeeSessionBeanLocal.retrieveAllEmployees();
        temp.remove(currentEmployee);
        setEmployees(temp);
    }


    public void createNewEmployee(ActionEvent event) {

        try {
            String filePath = this.saveUploadedImage();
            this.newEmployee.setPhotoPath(filePath);
            Long newEmployeeId = employeeSessionBeanLocal.createNewEmployee(getNewEmployee());
            employees.add(newEmployee);
            setNewEmployee(new Employee());
            this.employeeProfileImageFile = null;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "New employee created successfully (Employee ID: " + newEmployeeId + ")", null));
        } catch (EmployeeUsernameExistException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred creating new employee: " + ex.getMessage(), null));
        } catch (UnknownPersistenceException | InputDataValidationException ex) {
            Logger.getLogger(EmployeeManagedBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void updateEmployee(ActionEvent event) {

        try {
            employeeSessionBeanLocal.updateEmployee(getEmployeeToUpdate());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Employee updated successfully", null));
        } catch (EmployeeNotFoundException ex) {
            Logger.getLogger(updateEmployeeManagedBean.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void upload(FileUploadEvent event) {
        this.employeeProfileImageFile = event.getFile();
        if (employeeProfileImageFile != null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Successfully uploaded file: " + employeeProfileImageFile.getFileName(), null));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "File upload unsuccessful. Please try again!", null));
        }
    }

    public String saveUploadedImage() {

        String absolutePathToImages = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/").substring(0, FacesContext.getCurrentInstance().getExternalContext().getRealPath("/").indexOf("\\dist")) + "\\tellybuddy-war\\web\\management\\account\\employeeProfilePicture";
        Path folder = Paths.get(absolutePathToImages);
        System.out.println(absolutePathToImages);

        try {
            String filename = FilenameUtils.getBaseName(employeeProfileImageFile.getFileName());
            String extension = FilenameUtils.getExtension(employeeProfileImageFile.getFileName());
            InputStream input = employeeProfileImageFile.getInputstream();
//            Path file = Files.createTempFile(folder, filename + "-", "." + extension);
//            Files.copy(input, file, StandardCopyOption.REPLACE_EXISTING);

            File newFile = new File(absolutePathToImages, filename + '.' + extension);
            Files.copy(input, newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.out.println(newFile.toString());
            
            return filename + "." + extension;
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return null;

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

    public Employee getEmployeeToUpdate() {
        return employeeToUpdate;
    }

    public void setEmployeeToUpdate(Employee employeeToUpdate) {
//        if (this.saveUploadedImage() != null) {
//            String filePath = this.saveUploadedImage();
//            this.employeeToUpdate.setPhotoPath(filePath);
//        }
        this.employeeToUpdate = employeeToUpdate;
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

    public EmployeeSessionBeanLocal getEmployeeSessionBeanLocal() {
        return employeeSessionBeanLocal;
    }

    public void setEmployeeSessionBeanLocal(EmployeeSessionBeanLocal employeeSessionBeanLocal) {
        this.employeeSessionBeanLocal = employeeSessionBeanLocal;
    }

    public UploadedFile getEmployeeProfileImageFile() {
        return employeeProfileImageFile;
    }

    public void setEmployeeProfileImageFile(UploadedFile employeeProfileImageFile) {
        this.employeeProfileImageFile = employeeProfileImageFile;
    }

}
