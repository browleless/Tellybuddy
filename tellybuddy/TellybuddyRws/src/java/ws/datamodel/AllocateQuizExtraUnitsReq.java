package ws.datamodel;

import entity.Subscription;

/**
 *
 * @author tjle2
 */
public class AllocateQuizExtraUnitsReq {
    
    private String username;
    private String password;
    private Subscription subscription;
    private Integer dataUnits;
    private Integer smsUnits;
    private Integer talktimeUnits;

    public AllocateQuizExtraUnitsReq() {
    }

    public AllocateQuizExtraUnitsReq(String username, String password, Subscription subscription, Integer dataUnits, Integer smsUnits, Integer talktimeUnits) {
        this.username = username;
        this.password = password;
        this.subscription = subscription;
        this.dataUnits = dataUnits;
        this.smsUnits = smsUnits;
        this.talktimeUnits = talktimeUnits;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    public Integer getDataUnits() {
        return dataUnits;
    }

    public void setDataUnits(Integer dataUnits) {
        this.dataUnits = dataUnits;
    }

    public Integer getSmsUnits() {
        return smsUnits;
    }

    public void setSmsUnits(Integer smsUnits) {
        this.smsUnits = smsUnits;
    }

    public Integer getTalktimeUnits() {
        return talktimeUnits;
    }

    public void setTalktimeUnits(Integer talktimeUnits) {
        this.talktimeUnits = talktimeUnits;
    }
}
