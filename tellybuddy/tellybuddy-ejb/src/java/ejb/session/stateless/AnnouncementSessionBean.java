/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Announcement;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.enumeration.AnnouncementRecipientEnum;
import util.exception.AnnouncementAlreadyExpiredException;
import util.exception.AnnouncementNotFoundException;

/**
 *
 * @author ngjin
 */
@Stateless
public class AnnouncementSessionBean implements AnnouncementSessionBeanLocal {

    @PersistenceContext(unitName = "tellybuddy-ejbPU")
    private EntityManager em;

    public AnnouncementSessionBean() {

    }

    @Override
    public Long createNewAnnouncement(Announcement a) {
        em.persist(a);
        em.flush();

        return a.getAnnouncementId();
    }

    @Override
    public Announcement retrieveAnnouncementByAnnouncementId(Long announcementId) throws AnnouncementNotFoundException {
        Announcement a = em.find(Announcement.class, announcementId);

        if (a != null) {
            return a;
        } else {
            throw new AnnouncementNotFoundException("Announcement Id " + announcementId + " does not exist!");
        }
    }

    @Override
    public List<Announcement> retrieveAllAnnouncements() {
        Query q = em.createQuery("SELECT a FROM Announcement a");

        return q.getResultList();
    }

    @Override
    public List<Announcement> retrieveAllActiveAnnoucements() {
        Date current = new Date();

        Query q = em.createQuery("SELECT a FROM Announcement a WHERE a.expiryDate >= :inCurrent");
        q.setParameter("inCurrent", current);

        return q.getResultList();
    }

    @Override
    public List<Announcement> retrieveAllActiveAnnoucementsForEmployees() {
        Date current = new Date();

        Query q = em.createQuery("SELECT a FROM Announcement a WHERE a.expiryDate >= :inCurrent AND a.announcementRecipientEnum = :inRecipient");
        q.setParameter("inCurrent", current);
        q.setParameter("inRecipient", AnnouncementRecipientEnum.EMPLOYEES);
        List<Announcement> activeAnnouncements = q.getResultList();
        Collections.sort(activeAnnouncements, new Comparator<Announcement>() {
            //Sort to return latest first
            public int compare(Announcement o1, Announcement o2) {
                return o2.getPostedDate().compareTo(o1.getPostedDate());
            }
        });
        return activeAnnouncements;
    }

    public List<Announcement> retrieveAllActiveAnnouncementsForCustomers() {

        Query query = em.createQuery("SELECT a FROM Announcement a WHERE a.expiryDate > CURRENT_TIMESTAMP AND a.announcementRecipientEnum = :inAnnouncementRecipientEnum");
        query.setParameter("inAnnouncementRecipientEnum", AnnouncementRecipientEnum.CUSTOMER);

        return query.getResultList();
    }

    @Override
    public List<Announcement> retrieveAllExpiredAnnouncements() {
        Date current = new Date();

        Query q = em.createQuery("SELECT a FROM Announcement a WHERE a.expiryDate <= :inCurrent");
        q.setParameter("inCurrent", current);
        return q.getResultList();
    }

    @Override
    public void updateAnnouncement(Announcement a) throws AnnouncementNotFoundException, AnnouncementAlreadyExpiredException {
        if (a.getAnnouncementId() != null) {
            Announcement announcementToUpdate = retrieveAnnouncementByAnnouncementId(a.getAnnouncementId());

            //check if announcement has already expired
            Date current = new Date();

            if (announcementToUpdate.getExpiryDate().before(current)) {
                throw new AnnouncementAlreadyExpiredException("Announcement cannot be updated as it expired!");
            } else {
                announcementToUpdate.setTitle(a.getTitle());
                announcementToUpdate.setContent(a.getContent());
                announcementToUpdate.setPostedDate(a.getPostedDate());
                announcementToUpdate.setExpiryDate(a.getExpiryDate());
                announcementToUpdate.setAnnouncementRecipientEnum(a.getAnnouncementRecipientEnum());
            }
        } else {
            throw new AnnouncementNotFoundException("Announcement Id " + a.getAnnouncementId() + " does not exist!");
        }
    }

    /**
     *
     * @param announcementId
     * @throws AnnouncementNotFoundException
     */
    public void deleteAnnouncement(Long announcementId) throws AnnouncementNotFoundException {
        Announcement announcementToDelete = retrieveAnnouncementByAnnouncementId(announcementId);

        em.remove(announcementToDelete);
    }
}
