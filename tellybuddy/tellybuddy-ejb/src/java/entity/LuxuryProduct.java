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
import javax.persistence.OneToMany;
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

    public LuxuryProduct() {
        super();
        this.productItems = new ArrayList<>();
    }

    public LuxuryProduct(String serialNumber, String skuCode, String name, String description, BigDecimal price, Integer quantityOnHand, Integer reorderQuantity) {
        super(skuCode, name, description, price, quantityOnHand, reorderQuantity);
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


}
