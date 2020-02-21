package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 *
 * @author tjle2
 */
@Entity
public class PhoneNumber implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long phoneNumberId;

    @Column(nullable = false, unique = true, length = 8)
    @NotNull
    @Size(min = 8, max = 8)
    @Pattern(regexp = "^[0-9]{8}$")
    private String phoneNumber;

    @Column(nullable = false)
    @NotNull
    private Boolean inUse;
    
    @OneToOne(mappedBy = "phoneNumber")
    private Subscription subscription;

    public PhoneNumber() {
        this.inUse = false;
    }

    public PhoneNumber(String phoneNumber) {
        this();
        this.phoneNumber = phoneNumber;
    }

    public Long getPhoneNumberId() {
        return phoneNumberId;
    }

    public void setPhoneNumberId(Long phoneNumberId) {
        this.phoneNumberId = phoneNumberId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (phoneNumberId != null ? phoneNumberId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the phoneNumberId fields are not set
        if (!(object instanceof PhoneNumber)) {
            return false;
        }
        PhoneNumber other = (PhoneNumber) object;
        if ((this.phoneNumberId == null && other.phoneNumberId != null) || (this.phoneNumberId != null && !this.phoneNumberId.equals(other.phoneNumberId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.PhoneNumber[ id=" + phoneNumberId + " ]";
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Boolean getInUse() {
        return inUse;
    }

    public void setInUse(Boolean inUse) {
        this.inUse = inUse;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

}
