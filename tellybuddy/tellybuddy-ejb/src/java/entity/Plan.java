/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Future;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

/**
 *
 * @author tjle2
 */
@Entity
public class Plan implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long planId;

    @Column(nullable = false)
    @NotNull
    @Positive
    @Min(10)
    private Integer totalBasicUnits;

    @Column(nullable = false, precision = 5, scale = 2)
    @NotNull
    @Digits(integer = 3, fraction = 2)
    @DecimalMin("0.00")
    private BigDecimal price;

    @Column(nullable = false, precision = 5, scale = 2)
    @NotNull
    @Digits(integer = 3, fraction = 2)
    @DecimalMin("0.00")
    private BigDecimal penalty;
        
    @Column(nullable = false, length = 20, unique = true)
    @NotNull
    @Size(min = 6, max = 20)
    private String name;

    @Column(nullable = false, precision = 4, scale = 2)
    @NotNull
    @Digits(integer = 2, fraction = 2)
    @DecimalMin("0.00")
    private BigDecimal addOnPrice;

    @Column(nullable = false)
    @NotNull
    @Positive
    @Min(100)
    private Integer dataConversionRate;

    @Column(nullable = false)
    @NotNull
    @Positive
    @Min(25)
    private Integer smsConversionRate;

    @Column(nullable = false)
    @NotNull
    @Positive
    @Min(30)
    private Integer talktimeConversionRate;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date startTime;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    @Future
    private Date endTime;

    @NotNull
    @Column(nullable = false)
    private Boolean isDisabled;

    @NotNull
    @Column(nullable = false)
    private Boolean isInUse;

    public Plan() {
        this.isDisabled = false;
        this.isInUse = false;
    }

    public Plan(String name, Integer totalBasicUnits, BigDecimal price, BigDecimal penalty, BigDecimal addOnPrice, Integer dataConversionRate, Integer smsConversionRate, Integer talktimeConversionRate, Date startTime, Date endTime) {
        this();
        this.name = name;
        this.totalBasicUnits = totalBasicUnits;
        this.price = price;
        this.addOnPrice = addOnPrice;
        this.dataConversionRate = dataConversionRate;
        this.smsConversionRate = smsConversionRate;
        this.talktimeConversionRate = talktimeConversionRate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.penalty = penalty;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (planId != null ? planId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the planId fields are not set
        if (!(object instanceof Plan)) {
            return false;
        }
        Plan other = (Plan) object;
        if ((this.planId == null && other.planId != null) || (this.planId != null && !this.planId.equals(other.planId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Plan[ id=" + planId + " ]";
    }

    public Integer getTotalBasicUnits() {
        return totalBasicUnits;
    }

    public void setTotalBasicUnits(Integer totalBasicUnits) {
        this.totalBasicUnits = totalBasicUnits;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getAddOnPrice() {
        return addOnPrice;
    }

    public void setAddOnPrice(BigDecimal addOnPrice) {
        this.addOnPrice = addOnPrice;
    }

    public Integer getDataConversionRate() {
        return dataConversionRate;
    }

    public void setDataConversionRate(Integer dataConversionRate) {
        this.dataConversionRate = dataConversionRate;
    }

    public Integer getSmsConversionRate() {
        return smsConversionRate;
    }

    public void setSmsConversionRate(Integer smsConversionRate) {
        this.smsConversionRate = smsConversionRate;
    }

    public Integer getTalktimeConversionRate() {
        return talktimeConversionRate;
    }

    public void setTalktimeConversionRate(Integer talktimeConversionRate) {
        this.talktimeConversionRate = talktimeConversionRate;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Boolean getIsDisabled() {
        return isDisabled;
    }

    public void setIsDisabled(Boolean isDisabled) {
        this.isDisabled = isDisabled;
    }

    public Boolean getIsInUse() {
        return isInUse;
    }

    public void setIsInUse(Boolean isInUse) {
        this.isInUse = isInUse;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPenalty() {
        return penalty;
    }

    public void setPenalty(BigDecimal penalty) {
        this.penalty = penalty;
    }
}
