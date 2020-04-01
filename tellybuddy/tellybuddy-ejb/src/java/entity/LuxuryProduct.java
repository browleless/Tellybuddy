package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author tjle2
 */
@Entity
public class LuxuryProduct extends Product implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(nullable = false, length = 10)
    @NotNull
    @Size(min = 10, max = 10)
    private String serialNumber;

    @OneToMany(mappedBy = "luxuryProduct")
    private List<ProductItem> productItems;
    
//    @Column
//    @Temporal(TemporalType.TIMESTAMP)
//    private Date dealStartTime;
//
//    @Column
//    @Temporal(TemporalType.TIMESTAMP)
//    @Future
//    private Date dealEndTime;
//
//    @Column(nullable = true, precision = 6, scale = 2)
//    @Digits(integer = 4, fraction = 2)
//    @DecimalMin("0.00")
//    protected BigDecimal discountPrice;
    

    public LuxuryProduct() {
        super();
       this.productItems = new ArrayList<>();
    }

    public LuxuryProduct(String serialNumber, String skuCode, String name, String description, BigDecimal price, Integer quantityOnHand, Integer reorderQuantity, String productImagePath) {
        super(skuCode, name, description, price, quantityOnHand, reorderQuantity, productImagePath);
        this.productItems = new ArrayList<>();
        this.serialNumber = serialNumber;
    }

    @Override
    public String toString() {
        return "entity.LuxuryProduct[ id=" + productId + " ]";
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public List<ProductItem> getProductItems() {
        return productItems;
    }

    public void setProductItems(List<ProductItem> productItems) {
        this.productItems = productItems;
    }

//    public Date getDealStartTime() {
//        return dealStartTime;
//    }
//
//    public void setDealStartTime(Date dealStartTime) {
//        this.dealStartTime = dealStartTime;
//    }
//
//    public Date getDealEndTime() {
//        return dealEndTime;
//    }
//
//    public void setDealEndTime(Date dealEndTime) {
//        this.dealEndTime = dealEndTime;
//    }
//
//    public BigDecimal getDiscountPrice() {
//        return discountPrice;
//    }
//
//    public void setDiscountPrice(BigDecimal discountPrice) {
//        this.discountPrice = discountPrice;
//    }


}
