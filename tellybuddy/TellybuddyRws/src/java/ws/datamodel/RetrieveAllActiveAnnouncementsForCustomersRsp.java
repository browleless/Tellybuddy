package ws.datamodel;

import entity.Announcement;
import java.util.List;

/**
 *
 * @author tjle2
 */
public class RetrieveAllActiveAnnouncementsForCustomersRsp {
    
    private List<Announcement> announcements;

    public RetrieveAllActiveAnnouncementsForCustomersRsp() {
    }

    public RetrieveAllActiveAnnouncementsForCustomersRsp(List<Announcement> announcements) {
        this.announcements = announcements;
    }

    public List<Announcement> getAnnouncements() {
        return announcements;
    }

    public void setAnnouncements(List<Announcement> announcements) {
        this.announcements = announcements;
    }
}
