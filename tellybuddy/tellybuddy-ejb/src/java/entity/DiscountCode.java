package entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Future;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author tjle2
 */
@Entity
public class DiscountCode implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long discountCodeId;
    
    @Column(nullable = false, length = 16)
    @NotNull
    @Size(min = 6, max = 16)
    private String discountCode;
    
    @Column(nullable = false)
    @NotNull
    @Min(3)
    @Max(40)
    private Integer discountRate;
    
    @Column(nullable = false)
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Future
    private Date expiryDate;
    
    @OneToOne(mappedBy = "discountCode")
    private Transaction transaction;

    public DiscountCode() {
    }

    public DiscountCode(String discountCode, Integer discountRate, Date expiryDate) {
        this();
        this.discountCode = discountCode;
        this.discountRate = discountRate;
        this.expiryDate = expiryDate;
    }
    
    public Long getDiscountCodeId() {
        return discountCodeId;
    }

    public void setDiscountCodeId(Long discountCodeId) {
        this.discountCodeId = discountCodeId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (discountCodeId != null ? discountCodeId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the discountCodeId fields are not set
        if (!(object instanceof DiscountCode)) {
            return false;
        }
        DiscountCode other = (DiscountCode) object;
        if ((this.discountCodeId == null && other.discountCodeId != null) || (this.discountCodeId != null && !this.discountCodeId.equals(other.discountCodeId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.DiscountCode[ id=" + discountCodeId + " ]";
    }

    public String getDiscountCode() {
        return discountCode;
    }

    public void setDiscountCode(String discountCode) {
        this.discountCode = discountCode;
    }

    public Integer getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(Integer discountRate) {
        this.discountRate = discountRate;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }
    
}
