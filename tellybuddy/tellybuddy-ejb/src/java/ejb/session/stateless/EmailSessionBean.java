package ejb.session.stateless;

import entity.Bill;
import java.util.concurrent.Future;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import util.email.EmailManager;

/**
 *
 * @author tjle2
 */
@Stateless
public class EmailSessionBean implements EmailSessionBeanLocal {

    private final String GMAIL_USERNAME = "tellybuddy3106@gmail.com";
    private final String GMAIL_PASSWORD = "Tellybuddy@3106";

    @Asynchronous
    @Override
    public Future<Boolean> emailBillNotificationAsync(Bill bill, Integer subscriptionTotalAllowedData, Integer subscriptionTotalAllowedSms, Integer subscriptionTotalAllowedTalktime, String fromEmailAddress, String toEmailAddress) throws InterruptedException {

        EmailManager emailManager = new EmailManager(GMAIL_USERNAME, GMAIL_PASSWORD);
        Boolean result = emailManager.emailBillNotification(bill, subscriptionTotalAllowedData, subscriptionTotalAllowedSms, subscriptionTotalAllowedTalktime, fromEmailAddress, toEmailAddress);

        return new AsyncResult<>(result);
    }
}
