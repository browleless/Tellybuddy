package ws.datamodel;

/**
 *
 * @author tjle2
 */
public class ErrorRsp {

    private String message;

    public ErrorRsp() {
    }

    public ErrorRsp(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
