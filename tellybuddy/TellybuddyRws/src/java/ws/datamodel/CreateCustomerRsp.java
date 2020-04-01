package ws.datamodel;

/**
 *
 * @author tjle2
 */
public class CreateCustomerRsp {

    private Long customerId;

    public CreateCustomerRsp() {
    }

    public CreateCustomerRsp(Long customerId) {
        this.customerId = customerId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
}
