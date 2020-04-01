    /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

/**
 *
 * @author tjle2
 */
@Entity
public class FamilyGroup implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long familyGroupId;

    @Column(nullable = false, length = 64)
    @NotNull
    @Size(max = 64)
    private String description;

    @Column(nullable = false)
    @NotNull
    @Positive
    @Min(1)
    @Max(5)
    private Integer numberOfMembers;

    @Column(nullable = false)
    @NotNull
    @Min(0)
    @Max(1000)
    private Integer donatedUnits;

    @Column(nullable = false)
    @NotNull
    @Min(0)
    @Max(25)
    private Integer discountRate;

    @OneToMany(mappedBy = "familyGroup")
    private List<Customer> customers;

    public FamilyGroup() {
        this.numberOfMembers = 1;
        this.donatedUnits = 0;
        this.discountRate = 0;
        this.customers = new ArrayList<>();
    }

    public FamilyGroup(String description) {
        this();
        this.description = description;
    }

    public Long getFamilyGroupId() {
        return familyGroupId;
    }

    public void setFamilyGroupId(Long familyGroupId) {
        this.familyGroupId = familyGroupId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (familyGroupId != null ? familyGroupId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the familyGroupId fields are not set
        if (!(object instanceof FamilyGroup)) {
            return false;
        }
        FamilyGroup other = (FamilyGroup) object;
        if ((this.familyGroupId == null && other.familyGroupId != null) || (this.familyGroupId != null && !this.familyGroupId.equals(other.familyGroupId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.FamilyGroup[ id=" + familyGroupId + " ]";
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getNumberOfMembers() {
        return numberOfMembers;
    }

    public void setNumberOfMembers(Integer numberOfMembers) {
        this.numberOfMembers = numberOfMembers;
    }

    public Integer getDonatedUnits() {
        return donatedUnits;
    }

    public void setDonatedUnits(Integer donatedUnits) {
        this.donatedUnits = donatedUnits;
    }

    public Integer getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(Integer discountRate) {
        this.discountRate = discountRate;
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
    }

}
