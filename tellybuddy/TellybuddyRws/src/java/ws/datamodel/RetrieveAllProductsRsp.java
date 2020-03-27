package ws.datamodel;

import entity.Product;
import java.util.List;

/**
 *
 * @author tjle2
 */
public class RetrieveAllProductsRsp {
    
    private List<Product> products;

    public RetrieveAllProductsRsp() {
    }

    public RetrieveAllProductsRsp(List<Product> products) {
        this.products = products;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
