package ws.datamodel;

/**
 *
 * @author tjle2
 */
public class ChangePasswordRsp {
    
    private String message;

    public ChangePasswordRsp() {
    }

    public ChangePasswordRsp(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
