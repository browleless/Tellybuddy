/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Announcement;
import java.util.List;
import javax.ejb.Local;
import util.exception.AnnouncementAlreadyExpiredException;
import util.exception.AnnouncementNotFoundException;

/**
 *
 * @author ngjin
 */
@Local
public interface AnnouncementSessionBeanLocal {

    public Long createNewAnnouncement(Announcement a);

    public Announcement retrieveAnnouncementByAnnouncementId(Long announcementId) throws AnnouncementNotFoundException;

    public List<Announcement> retrieveAllAnnouncements();

    public List<Announcement> retrieveAllActiveAnnoucements();

    public void updateAnnouncement(Announcement a) throws AnnouncementNotFoundException, AnnouncementAlreadyExpiredException;

    public void deleteAnnouncement(Long announcementId) throws AnnouncementNotFoundException;

    public List<Announcement> retrieveAllExpiredAnnouncements();

    public List<Announcement> retrieveAllActiveAnnoucementsForEmployees();

}
