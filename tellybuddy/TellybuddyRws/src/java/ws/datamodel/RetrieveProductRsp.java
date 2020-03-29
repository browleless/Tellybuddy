package ws.datamodel;

import entity.Product;

/**
 *
 * @author tjle2
 */
public class RetrieveProductRsp {

    private Product product;

    public RetrieveProductRsp() {
    }

    public RetrieveProductRsp(Product product) {
        this.product = product;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
