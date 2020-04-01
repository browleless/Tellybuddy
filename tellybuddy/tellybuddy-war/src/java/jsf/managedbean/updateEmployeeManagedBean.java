/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.managedbean;

import ejb.session.stateless.EmployeeSessionBeanLocal;
import entity.Employee;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.primefaces.shaded.commons.io.FilenameUtils;
import util.exception.EmployeeNotFoundException;

/**
 *
 * @author kaikai
 */
@Named(value = "updateEmployeeManagedBean")
@ViewScoped
public class updateEmployeeManagedBean implements Serializable {

    @EJB(name = "EmployeeSessionBeanLocal")
    private EmployeeSessionBeanLocal employeeSessionBeanLocal;

    private Employee currentEmployee;
    private Employee employeeToUpdate;
    private String updatedPassword;
    private UploadedFile employeeProfileImageFile;

    public String getUpdatedPassword() {
        return updatedPassword;
    }

    public void setUpdatedPassword(String updatedPassword) {
        this.updatedPassword = updatedPassword;
    }

    public updateEmployeeManagedBean() {
        employeeToUpdate = new Employee();
    }

    @PostConstruct
    public void postConstruct() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        Map<String, Object> sessionMap = externalContext.getSessionMap();
        currentEmployee = (Employee) sessionMap.get("currentEmployee");
    }

    public void updateEmployee(ActionEvent event) {

        try {
            employeeToUpdate.setUpdatedPassword(updatedPassword);
            if (employeeProfileImageFile != null) {
                String filePath = this.saveUploadedImage();
                employeeToUpdate.setPhotoPath(filePath);
                System.out.println("HI THEREEEEEE_------------------------------");
            }
            
            setCurrentEmployee(employeeToUpdate);
            employeeSessionBeanLocal.updateEmployee(getEmployeeToUpdate());
            setEmployeeToUpdate(new Employee());
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

        String absolutePathToProductImages = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/").substring(0, FacesContext.getCurrentInstance().getExternalContext().getRealPath("/").indexOf("\\dist")) + "\\tellybuddy-war\\web\\management\\account\\employeeProfilePicture";
        Path folder = Paths.get(absolutePathToProductImages);
        System.out.println(absolutePathToProductImages);

        try {
            String filename = FilenameUtils.getBaseName(employeeProfileImageFile.getFileName());
            String extension = FilenameUtils.getExtension(employeeProfileImageFile.getFileName());
            Path file = Files.createTempFile(folder, filename + "-", "." + extension);
            InputStream input = employeeProfileImageFile.getInputstream();

            Files.copy(input, file, StandardCopyOption.REPLACE_EXISTING);

            System.out.println(file.toString());
            return file.getFileName().toString();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return null;

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
