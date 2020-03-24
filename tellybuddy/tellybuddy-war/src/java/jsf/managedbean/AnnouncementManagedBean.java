/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.managedbean;

import ejb.session.stateless.AnnouncementSessionBeanLocal;
import entity.Announcement;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import util.enumeration.AnnouncementRecipientEnum;
import util.exception.AnnouncementAlreadyExpiredException;
import util.exception.AnnouncementNotFoundException;

/**
 *
 * @author kaikai
 */
@Named(value = "announcementManagedBean")
@ViewScoped
public class AnnouncementManagedBean implements Serializable{


    @EJB(name = "AnnouncementSessionBeanLocal")
    private AnnouncementSessionBeanLocal announcementSessionBeanLocal;

    private List<Announcement> ongoingAnnouncements;
    private List<Announcement> expiredAnnouncements;
    private List<Announcement> filteredAnnouncements;
    private List<Announcement> announcements;

    private Announcement announcementToView;
    private Announcement ongoingToUpdate;

    private Announcement newAnnouncement;
    private String selectedFilter;
    

    public AnnouncementManagedBean() {
        newAnnouncement = new Announcement();
        selectedFilter = "Ongoing";
    }

    @PostConstruct
    public void postConstruct() {
        setOngoingAnnouncements(announcementSessionBeanLocal.retrieveAllActiveAnnoucements());
        setExpiredAnnouncements(announcementSessionBeanLocal.retrieveAllExpiredAnnouncements());
        setAnnouncements(announcementSessionBeanLocal.retrieveAllAnnouncements());
    }

    public void createNewAnnouncement(ActionEvent event) {
            Long newAnnouncementId = announcementSessionBeanLocal.createNewAnnouncement(getNewAnnouncement());
            announcements.add(newAnnouncement);
            ongoingAnnouncements.add(newAnnouncement);
            setNewAnnouncement(new Announcement());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "New announcement created successfully (Announcement ID: " + newAnnouncementId + ")", null));
        
    }
    public void updateAnnouncement(ActionEvent event) {

        try {
            announcementSessionBeanLocal.updateAnnouncement(getOngoingToUpdate());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Announcement updated successfully", null));
        } catch (AnnouncementNotFoundException ex) {
            Logger.getLogger(AnnouncementManagedBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AnnouncementAlreadyExpiredException ex) {
            Logger.getLogger(AnnouncementManagedBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void deleteAnnouncement(ActionEvent event) {

        try {
            Announcement announcementToDelete = (Announcement) event.getComponent().getAttributes().get("announcementToDelete");
            announcementSessionBeanLocal.deleteAnnouncement(announcementToDelete.getAnnouncementId());
            if(ongoingAnnouncements.contains(announcementToDelete)){
                ongoingAnnouncements.remove(announcementToDelete);
            }else{
                expiredAnnouncements.remove(announcementToDelete);
            }
            announcements.remove(announcementToDelete);
            
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Announcement deleted successfully", null));
        } catch (AnnouncementNotFoundException ex) {
            Logger.getLogger(AnnouncementManagedBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<Announcement> getOngoingAnnouncements() {
        return ongoingAnnouncements;
    }

    public List<Announcement> getFilteredAnnouncements() {
        return filteredAnnouncements;
    }

    public void setFilteredAnnouncements(List<Announcement> filteredAnnouncements) {
        this.filteredAnnouncements = filteredAnnouncements;
    }

    public Announcement getNewAnnouncement() {
        return newAnnouncement;
    }

    public void setNewAnnouncement(Announcement newAnnouncement) {
        this.newAnnouncement = newAnnouncement;
    }

    public void setOngoingAnnouncements(List<Announcement> ongoingAnnouncements) {
        this.ongoingAnnouncements = ongoingAnnouncements;
    }

    public List<Announcement> getAnnouncements() {
        return announcements;
    }

    public void setAnnouncements(List<Announcement> announcements) {
        this.announcements = announcements;
    }

    public List<Announcement> getExpiredAnnouncements() {
        return expiredAnnouncements;
    }

    public void setExpiredAnnouncements(List<Announcement> expiredAnnouncements) {
        this.expiredAnnouncements = expiredAnnouncements;
    }
     public Announcement getAnnouncementToView() {
        return announcementToView;
    }

    public void setAnnouncementToView(Announcement announcementToView) {
        this.announcementToView = announcementToView;
    }

    public String getSelectedFilter() {
        return selectedFilter;
    }

    public void setSelectedFilter(String selectedFilter) {
        this.selectedFilter = selectedFilter;
    }

    public Announcement getOngoingToUpdate() {
        return ongoingToUpdate;
    }
    
    public void doFilter() {

        if (selectedFilter.equals("Ongoing")) {
            announcements = ongoingAnnouncements;
        } else if (selectedFilter.equals("Expired")) {
            announcements = expiredAnnouncements;
        }
    }

    public void setOngoingToUpdate(Announcement ongoingToUpdate) {
        this.ongoingToUpdate = ongoingToUpdate;
        System.out.println(ongoingToUpdate.getAnnouncementRecipientEnum());
    }
  

}


    
