package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Future;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 *
 * @author tjle2
 */
@Entity
public class UsageDetail implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long usageDetailId;

    @Column(nullable = false)
    @NotNull
    @Min(0)
    private Integer talktimeUsage;

    @Column(nullable = false)
    @NotNull
    @Min(0)
    private Integer smsUsage;

    @Column(nullable = false, precision = 4, scale = 1)
    @NotNull
    @Digits(integer = 3, fraction = 1)
    @DecimalMin("0.0")
    private BigDecimal dataUsage;

    @Column(nullable = false)
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;

    @Column(nullable = false)
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Future
    private Date endDate;
    
    @OneToOne(mappedBy = "usageDetail")
    private Bill bill;
    
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private Subscription subscription;

    public UsageDetail() {
        this.dataUsage = BigDecimal.valueOf(0);
        this.smsUsage = 0;
        this.talktimeUsage = 0;
    }

    public UsageDetail(Date startDate, Date endDate) {
        this();
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Long getUsageDetailId() {
        return usageDetailId;
    }

    public void setUsageDetailId(Long usageDetailId) {
        this.usageDetailId = usageDetailId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (usageDetailId != null ? usageDetailId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the usageDetailId fields are not set
        if (!(object instanceof UsageDetail)) {
            return false;
        }
        UsageDetail other = (UsageDetail) object;
        if ((this.usageDetailId == null && other.usageDetailId != null) || (this.usageDetailId != null && !this.usageDetailId.equals(other.usageDetailId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.UsageDetails[ id=" + usageDetailId + " ]";
    }

    public Integer getTalktimeUsage() {
        return talktimeUsage;
    }

    public void setTalktimeUsage(Integer talktimeUsage) {
        this.talktimeUsage = talktimeUsage;
    }

    public Integer getSmsUsage() {
        return smsUsage;
    }

    public void setSmsUsage(Integer smsUsage) {
        this.smsUsage = smsUsage;
    }

    public BigDecimal getDataUsage() {
        return dataUsage;
    }

    public void setDataUsage(BigDecimal dataUsage) {
        this.dataUsage = dataUsage;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Bill getBill() {
        return bill;
    }

    public void setBill(Bill bill) {
        this.bill = bill;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

}
