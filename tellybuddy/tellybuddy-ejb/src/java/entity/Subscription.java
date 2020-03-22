package entity;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import util.enumeration.SubscriptionStatusEnum;

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
    private HashMap<String, Integer> dataUnits;

    @Column(nullable = false)
    @NotNull
    private HashMap<String, Integer> talkTimeUnits;

    @Column(nullable = false)
    @NotNull
    private HashMap<String, Integer> smsUnits;
    
    @Column(nullable = false)
    @NotNull
    private SubscriptionStatusEnum subscriptionStatusEnum;

    @Column(nullable = false)
    @NotNull
    private Boolean isActive;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date subscriptionStartDate;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    @Future
    private Date subscriptionEndDate;

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
        this.isActive = false;
        this.usageDetails = new ArrayList<>();
        this.dataUnits = new HashMap<>();
        this.smsUnits = new HashMap<>();
        this.talkTimeUnits = new HashMap<>();
        this.subscriptionStatusEnum = subscriptionStatusEnum.PENDINGAPPROVAL;
    }

    public Subscription(Integer allocatedDataUnits, Integer allocatedTalktimeUnits, Integer allocatedSmsUnits) {
        this();
        this.dataUnits.put("allocated", allocatedDataUnits);
        this.smsUnits.put("allocated", allocatedSmsUnits);
        this.talkTimeUnits.put("allocated", allocatedTalktimeUnits);
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

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Date getSubscriptionStartDate() {
        return subscriptionStartDate;
    }

    public void setSubscriptionStartDate(Date subscriptionStartDate) {
        this.subscriptionStartDate = subscriptionStartDate;
    }

    public Date getSubscriptionEndDate() {
        return subscriptionEndDate;
    }

    public void setSubscriptionEndDate(Date subscriptionEndDate) {
        this.subscriptionEndDate = subscriptionEndDate;
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

    public HashMap<String, Integer> getDataUnits() {
        return dataUnits;
    }

    public void setDataUnits(HashMap<String, Integer> dataUnits) {
        this.dataUnits = dataUnits;
    }

    public HashMap<String, Integer> getTalkTimeUnits() {
        return talkTimeUnits;
    }

    public void setTalkTimeUnits(HashMap<String, Integer> talkTimeUnits) {
        this.talkTimeUnits = talkTimeUnits;
    }

    public HashMap<String, Integer> getSmsUnits() {
        return smsUnits;
    }

    public void setSmsUnits(HashMap<String, Integer> smsUnits) {
        this.smsUnits = smsUnits;
    }

    public List<Integer> getTalkTimeAsList() {
        return new ArrayList<Integer>(this.talkTimeUnits.values());
    }

    public List<Integer> getSmsAsList() {
        return new ArrayList<Integer>(this.smsUnits.values());
    }

    public List<Integer> getDataAsList() {
        return new ArrayList<Integer>(this.dataUnits.values());
    }

    public String getFormattedStartDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        return dateFormat.format(this.subscriptionStartDate);
    }

    public SubscriptionStatusEnum getSubscriptionStatusEnum() {
        return subscriptionStatusEnum;
    }

    public void setSubscriptionStatusEnum(SubscriptionStatusEnum subscriptionStatusEnum) {
        this.subscriptionStatusEnum = subscriptionStatusEnum;
    }

}
