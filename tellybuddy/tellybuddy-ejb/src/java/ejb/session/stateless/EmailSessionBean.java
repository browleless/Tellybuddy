package ejb.session.stateless;

import entity.Bill;
import entity.Customer;
import java.util.concurrent.Future;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import util.email.EmailManager;
import util.exception.CustomerNotFoundException;

/**
 *
 * @author tjle2
 */
@Stateless
public class EmailSessionBean implements EmailSessionBeanLocal {

    @EJB
    private CustomerSessionBeanLocal customerSessionBeanLocal;

    private final String GMAIL_USERNAME = "tellybuddy3106@gmail.com";
    private final String GMAIL_PASSWORD = "Tellybuddy@3106";

    private final EmailManager emailManager;

    public EmailSessionBean() {
        emailManager = new EmailManager(GMAIL_USERNAME, GMAIL_PASSWORD);
    }

    @Asynchronous
    @Override
    public Future<Boolean> emailBillNotificationAsync(Bill bill, Integer subscriptionTotalAllowedData, Integer subscriptionTotalAllowedSms, Integer subscriptionTotalAllowedTalktime, String fromEmailAddress, String toEmailAddress) throws InterruptedException {

        Boolean result = emailManager.emailBillNotification(bill, subscriptionTotalAllowedData, subscriptionTotalAllowedSms, subscriptionTotalAllowedTalktime, fromEmailAddress, toEmailAddress);

        return new AsyncResult<>(result);
    }

    @Asynchronous
    @Override
    public Future<Boolean> emailCustomerAccountCreationNotification(Customer customer, String fromEmailAddress, String toEmailAddress) throws InterruptedException {

        Boolean result = emailManager.emailCustomerAccountCreationNotification(customer, fromEmailAddress, toEmailAddress);

        return new AsyncResult<>(result);
    }

    @Asynchronous
    @Override
    public Future<Boolean> emailCustomerResetPasswordLink(String fromEmailAddress, String toEmailAddress) throws InterruptedException, CustomerNotFoundException {

        Customer customer = customerSessionBeanLocal.retrieveCustomerByEmail(toEmailAddress);

        Boolean result = emailManager.emailCustomerResetPasswordLink(customer, fromEmailAddress, toEmailAddress);

        return new AsyncResult<>(result);
    }
}
