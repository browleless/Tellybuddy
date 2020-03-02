package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Future;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 *
 * @author tjle2
 */
@Entity
public class Subscription implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long subcscriptionId;
    
    @Column(nullable = false)
    @NotNull
    @Positive
    @Min(0)
    @Max(100)
    private Integer allocatedDataUnits;
    
    @Column(nullable = false)
    @NotNull
    @Positive
    @Min(0)
    @Max(100)
    private Integer allocatedTalktimeUnits;
    
    @Column(nullable = false)
    @NotNull
    @Positive
    @Min(0)
    @Max(100)
    private Integer allocatedSmsUnits;

    @Column(nullable = false)
    @NotNull
    @Positive
    @Min(0)
    @Max(100)
    private Integer nextMonthDataUnits;
    
    @Column(nullable = false)
    @NotNull
    @Positive
    @Min(0)
    @Max(100)
    private Integer nextMonthTalktimeUnits;
    
    @Column(nullable = false)
    @NotNull
    @Positive
    @Min(0)
    @Max(100)
    private Integer nextMonthSmsUnits;
    
    @Column(nullable = false)
    @NotNull
    @Positive
    @Min(0)
    @Max(100)
    private Integer addOnDataUnits;
    
    @Column(nullable = false)
    @NotNull
    @Positive
    @Min(0)
    @Max(100)
    private Integer addOnTalktimeUnits;
    
    @Column(nullable = false)
    @NotNull
    @Positive
    @Min(0)
    @Max(100)
    private Integer addOnSmsUnits;
    
    @Column(nullable = false)
    @NotNull
    private Boolean isActive;
    
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date contractStartDate;
    
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    @Future
    private Date contractEndDate;
    
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private Customer customer;
    
    @OneToMany(mappedBy = "subscription")
    private List<UsageDetails> usageDetails;
    
    @OneToOne(optional = false)
    @JoinColumn(nullable = false)
    private Plan plan;
    
    @OneToOne(optional = false)
    @JoinColumn(nullable = false)
    private PhoneNumber phoneNumber;
    
    public Subscription() {
        this.addOnDataUnits = 0;
        this.addOnSmsUnits = 0;
        this.addOnTalktimeUnits = 0;
        this.nextMonthDataUnits = 0;
        this.nextMonthSmsUnits = 0;
        this.nextMonthTalktimeUnits = 0;
        this.isActive = false;
        this.usageDetails = new ArrayList<>();
    }

    public Subscription(Integer allocatedDataUnits, Integer allocatedTalktimeUnits, Integer allocatedSmsUnits) {
        this();
        this.allocatedDataUnits = allocatedDataUnits;
        this.allocatedTalktimeUnits = allocatedTalktimeUnits;
        this.allocatedSmsUnits = allocatedSmsUnits;
    }

    public Long getSubcscriptionId() {
        return subcscriptionId;
    }

    public void setSubcscriptionId(Long subcscriptionId) {
        this.subcscriptionId = subcscriptionId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (subcscriptionId != null ? subcscriptionId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the subcscriptionId fields are not set
        if (!(object instanceof Subscription)) {
            return false;
        }
        Subscription other = (Subscription) object;
        if ((this.subcscriptionId == null && other.subcscriptionId != null) || (this.subcscriptionId != null && !this.subcscriptionId.equals(other.subcscriptionId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Subscription[ id=" + subcscriptionId + " ]";
    }

    public Integer getAllocatedDataUnits() {
        return allocatedDataUnits;
    }

    public void setAllocatedDataUnits(Integer allocatedDataUnits) {
        this.allocatedDataUnits = allocatedDataUnits;
    }

    public Integer getAllocatedTalktimeUnits() {
        return allocatedTalktimeUnits;
    }

    public void setAllocatedTalktimeUnits(Integer allocatedTalktimeUnits) {
        this.allocatedTalktimeUnits = allocatedTalktimeUnits;
    }

    public Integer getAllocatedSmsUnits() {
        return allocatedSmsUnits;
    }

    public void setAllocatedSmsUnits(Integer allocatedSmsUnits) {
        this.allocatedSmsUnits = allocatedSmsUnits;
    }

    public Integer getAddOnDataUnits() {
        return addOnDataUnits;
    }

    public void setAddOnDataUnits(Integer addOnDataUnits) {
        this.addOnDataUnits = addOnDataUnits;
    }

    public Integer getAddOnTalktimeUnits() {
        return addOnTalktimeUnits;
    }

    public void setAddOnTalktimeUnits(Integer addOnTalktimeUnits) {
        this.addOnTalktimeUnits = addOnTalktimeUnits;
    }

    public Integer getAddOnSmsUnits() {
        return addOnSmsUnits;
    }

    public void setAddOnSmsUnits(Integer addOnSmsUnits) {
        this.addOnSmsUnits = addOnSmsUnits;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Date getContractStartDate() {
        return contractStartDate;
    }

    public void setContractStartDate(Date contractStartDate) {
        this.contractStartDate = contractStartDate;
    }

    public Date getContractEndDate() {
        return contractEndDate;
    }

    public void setContractEndDate(Date contractEndDate) {
        this.contractEndDate = contractEndDate;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<UsageDetails> getUsageDetails() {
        return usageDetails;
    }

    public void setUsageDetails(List<UsageDetails> usageDetails) {
        this.usageDetails = usageDetails;
    }

    public Plan getPlan() {
        return plan;
    }

    public void setPlan(Plan plan) {
        this.plan = plan;
    }

    public PhoneNumber getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(PhoneNumber phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Integer getNextMonthDataUnits() {
        return nextMonthDataUnits;
    }

    public void setNextMonthDataUnits(Integer nextMonthDataUnits) {
        this.nextMonthDataUnits = nextMonthDataUnits;
    }

    public Integer getNextMonthTalktimeUnits() {
        return nextMonthTalktimeUnits;
    }

    public void setNextMonthTalktimeUnits(Integer nextMonthTalktimeUnits) {
        this.nextMonthTalktimeUnits = nextMonthTalktimeUnits;
    }

    public Integer getNextMonthSmsUnits() {
        return nextMonthSmsUnits;
    }

    public void setNextMonthSmsUnits(Integer nextMonthSmsUnits) {
        this.nextMonthSmsUnits = nextMonthSmsUnits;
    }
    
}
