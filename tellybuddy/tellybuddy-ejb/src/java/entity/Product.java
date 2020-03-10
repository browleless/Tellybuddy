package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

/**
 *
 * @author tjle2
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)

public class Product implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long productId;

    @Column(nullable = false, unique = true, length = 8)
    @NotNull
    @Size(min = 8, max = 8)
    protected String skuCode;

    @Column(nullable = false, length = 64)
    @NotNull
    @Size(min = 4, max = 64)
    protected String name;

    @Column(nullable = false, length = 128)
    @NotNull
    @Size(max = 128)
    protected String description;

    @Column(nullable = false, precision = 6, scale = 2)
    @NotNull
    @Digits(integer = 4, fraction = 2)
    @DecimalMin("0.00")
    protected BigDecimal price;

    @Column(nullable = false)
    @NotNull
    @Positive
    @Min(1)
    protected Integer quantityOnHand;
    
    @Column(nullable = false)
    @NotNull
    @Min(0)
    private Integer reorderQuantity;

    @ManyToMany(mappedBy = "products")
    private List<Tag> tags;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private Category category;

    public Product() {
        this.tags = new ArrayList<>();
        reorderQuantity = 0;
        quantityOnHand = 0;
        price = new BigDecimal("0.00");
        
    }

    public Product(String skuCode, String name, String description, BigDecimal price, Integer quantityOnHand, Integer reorderQuantity) {
        this();
        this.skuCode = skuCode;
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantityOnHand = quantityOnHand;
        this.reorderQuantity = reorderQuantity;
    }

    public Integer getReorderQuantity() {
        return reorderQuantity;
    }

    public void setReorderQuantity(Integer reorderQuantity) {
        this.reorderQuantity = reorderQuantity;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (productId != null ? productId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the productId fields are not set
        if (!(object instanceof Product)) {
            return false;
        }
        Product other = (Product) object;
        if ((this.productId == null && other.productId != null) || (this.productId != null && !this.productId.equals(other.productId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Product[ id=" + productId + " ]";
    }

    public void addTag(Tag tag) {
        if (tag != null) {
            if (!this.tags.contains(tag)) {
                this.tags.add(tag);

                if (!tag.getProducts().contains(this)) {
                    tag.getProducts().add(this);
                }
            }
        }
    }

    public void removeTag(Tag tag) {
        if (tag != null) {
            if (this.tags.contains(tag)) {
                this.tags.remove(tag);

                if (tag.getProducts().contains(this)) {
                    tag.getProducts().remove(this);
                }
            }
        }
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getQuantityOnHand() {
        return quantityOnHand;
    }

    public void setQuantityOnHand(Integer quantityOnHand) {
        this.quantityOnHand = quantityOnHand;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        if(this.category != null)
        {
            if(this.category.getProducts().contains(this))
            {
                this.category.getProducts().remove(this);
            }
        }
        
        this.category = category;
        
        if(this.category != null)
        {
            if(!this.category.getProducts().contains(this))
            {
                this.category.getProducts().add(this);
            }
        }
    }

}
