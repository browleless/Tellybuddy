package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import util.enumeration.AnnouncementRecipientEnum;

/**
 *
 * @author tjle2
 */
@Entity
public class Announcement implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long announcementId;

    @Column(nullable = false, length = 32)
    @NotNull
    @Size(min = 4, max = 32)
    private String title;

    @Column(nullable = false, length = 255)
    @NotNull
    @Size(max = 255)
    private String content;

    @Column(nullable = false)
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date postedDate;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
   // @Future
    private Date expiryDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private AnnouncementRecipientEnum announcementRecipientEnum;

    public Announcement() {
    }

    public Announcement(String title, String content, Date postedDate, Date expiryDate, AnnouncementRecipientEnum announcementRecipientEnum) {
        this();
        this.title = title;
        this.content = content;
        this.postedDate = postedDate;
        this.expiryDate = expiryDate;
        this.announcementRecipientEnum = announcementRecipientEnum;
    }

    public Long getAnnouncementId() {
        return announcementId;
    }

    public void setAnnouncementId(Long announcementId) {
        this.announcementId = announcementId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (announcementId != null ? announcementId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the announcementId fields are not set
        if (!(object instanceof Announcement)) {
            return false;
        }
        Announcement other = (Announcement) object;
        if ((this.announcementId == null && other.announcementId != null) || (this.announcementId != null && !this.announcementId.equals(other.announcementId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Announcement[ id=" + announcementId + " ]";
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getPostedDate() {
        return postedDate;
    }

    public void setPostedDate(Date postedDate) {
        this.postedDate = postedDate;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public AnnouncementRecipientEnum getAnnouncementRecipientEnum() {
        return announcementRecipientEnum;
    }

    public void setAnnouncementRecipientEnum(AnnouncementRecipientEnum announcementRecipientEnum) {
        this.announcementRecipientEnum = announcementRecipientEnum;
    }

}
