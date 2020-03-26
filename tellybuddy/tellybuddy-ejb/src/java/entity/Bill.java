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
import javax.validation.constraints.NotNull;

/**
 *
 * @author tjle2
 */
@Entity
public class Bill implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long billId;
    
    @Column(nullable = false, precision = 7, scale = 2)
    @NotNull
    @Digits(integer = 5, fraction = 2)
    @DecimalMin("0.00")
    private BigDecimal price;
    
    @Column(nullable = false, precision = 5, scale = 2)
    @NotNull
    @Digits(integer = 3, fraction = 2)
    @DecimalMin("0.00")
    private BigDecimal addOnPrice;
    
    @Column(nullable = false, precision = 5, scale = 2)
    @NotNull
    @Digits(integer = 3, fraction = 2)
    @DecimalMin("0.00")
    private BigDecimal exceedPenaltyPrice;
    
    @Column(nullable = false)
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;
    
    @Column(nullable = false)
    @NotNull
    private Boolean paid;
    
    @OneToOne
    private Payment payment;
    
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private Customer customer;
    
    @OneToOne(optional = false)
    @JoinColumn(nullable = false)
    private UsageDetail usageDetail;

    public Bill() {
        this.paid = false;
    }

    public Bill(BigDecimal price, Date date, BigDecimal addOnPrice, BigDecimal exceedPenaltyPrice) {
        this();
        this.price = price;
        this.date = date;
        this.addOnPrice = addOnPrice;
        this.exceedPenaltyPrice = exceedPenaltyPrice;
    }

    public Long getBillId() {
        return billId;
    }

    public void setBillId(Long billId) {
        this.billId = billId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (billId != null ? billId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the billId fields are not set
        if (!(object instanceof Bill)) {
            return false;
        }
        Bill other = (Bill) object;
        if ((this.billId == null && other.billId != null) || (this.billId != null && !this.billId.equals(other.billId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Bill[ id=" + billId + " ]";
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Boolean getPaid() {
        return paid;
    }

    public void setPaid(Boolean paid) {
        this.paid = paid;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public UsageDetail getUsageDetail() {
        return usageDetail;
    }

    public void setUsageDetail(UsageDetail usageDetail) {
        this.usageDetail = usageDetail;
    }

    public BigDecimal getAddOnPrice() {
        return addOnPrice;
    }

    public void setAddOnPrice(BigDecimal addOnPrice) {
        this.addOnPrice = addOnPrice;
    }

    public BigDecimal getExceedPenaltyPrice() {
        return exceedPenaltyPrice;
    }

    public void setExceedPenaltyPrice(BigDecimal exceedPenaltyPrice) {
        this.exceedPenaltyPrice = exceedPenaltyPrice;
    }
    
}
