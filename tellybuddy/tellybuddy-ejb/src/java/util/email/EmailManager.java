package util.email;

import entity.Bill;
import entity.Customer;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author tjle2
 */
public class EmailManager {

    private final String emailServerName = "smtp.gmail.com";
    private final String mailer = "JavaMailer";
    private String smtpAuthUser;
    private String smtpAuthPassword;

    public EmailManager() {
    }

    public EmailManager(String smtpAuthUser, String smtpAuthPassword) {
        this.smtpAuthUser = smtpAuthUser;
        this.smtpAuthPassword = smtpAuthPassword;
    }

    public Boolean emailBillNotification(Bill bill, Integer subscriptionTotalAllowedData, Integer subscriptionTotalAllowedSms, Integer subscriptionTotalAllowedTalktime, String fromEmailAddress, String toEmailAddress) {

        String emailBody = "";

        emailBody += "You have a new bill to attend to for Subscription: " + bill.getUsageDetail().getSubscription().getPlan().getName() + " (" + bill.getUsageDetail().getSubscription().getPhoneNumber().getPhoneNumber() + ")\n\n\n";

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MMM/yyyy");
        emailBody += "Billing Cycle: " + formatter.format(bill.getUsageDetail().getStartDate()) + " — " + formatter.format(bill.getUsageDetail().getEndDate()) + "\n\n";

        emailBody += "SUMMARY\n";
        emailBody += "Standard Plan Price: " + NumberFormat.getCurrencyInstance().format(bill.getPrice()) + "\n";
        emailBody += "Add On Additional Price: " + NumberFormat.getCurrencyInstance().format(bill.getAddOnPrice()) + "\n";
        emailBody += "Exceeded Additional Price: " + NumberFormat.getCurrencyInstance().format(bill.getExceedPenaltyPrice()) + "\n\n";
        emailBody += "Total Payable Price: " + NumberFormat.getCurrencyInstance().format(bill.getPrice().add(bill.getAddOnPrice()).add(bill.getExceedPenaltyPrice())) + "\n\n\n";

        emailBody += "BREAKDOWN OF USAGE DETAIL:\n";
        emailBody += "Data Usage: " + bill.getUsageDetail().getDataUsage().setScale(1) + " / " + ((double) subscriptionTotalAllowedData / 1000) + " (GB)\n";
        emailBody += "SMS Usage: " + bill.getUsageDetail().getSmsUsage() + " / " + subscriptionTotalAllowedSms + "\n";
        emailBody += "Talktime Usage: " + bill.getUsageDetail().getTalktimeUsage() + " / " + subscriptionTotalAllowedTalktime + " (min)\n\n\n";

        emailBody += "Your new allocation (if any made) has also been adjusted for the new cycle.\n\n";
        emailBody += "Add On units, Family Group Additional units have also been reset for the new billing cycle.\n\n";

        emailBody += "Thank You for choosing Tellybuddy as your trusted telco! Enjoy!";

        try {
            Properties props = new Properties();
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.host", emailServerName);
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.debug", "true");
            javax.mail.Authenticator auth = new SMTPAuthenticator(smtpAuthUser, smtpAuthPassword);
            Session session = Session.getInstance(props, auth);
            session.setDebug(true);
            Message msg = new MimeMessage(session);

            if (msg != null) {
                msg.setFrom(InternetAddress.parse(fromEmailAddress, false)[0]);
                msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmailAddress, false));
                msg.setSubject("Your Tellybuddy Bill for number (" + bill.getUsageDetail().getSubscription().getPhoneNumber().getPhoneNumber() + ") is now ready!");
                msg.setText(emailBody);
                msg.setHeader("X-Mailer", mailer);

                Date timeStamp = new Date();
                msg.setSentDate(timeStamp);

                Transport.send(msg);

                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }
    }

    public Boolean emailCustomerAccountCreationNotification(Customer customer, String fromEmailAddress, String toEmailAddress) {

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa");

        String emailBody = "";

        emailBody += "Dear " + customer.getFirstName() + " " + customer.getLastName() + ",\n\n";
        emailBody += "Your account for Tellybuddy (Username: " + customer.getUsername() + ") has been successfully created at " + formatter.format(new Date()) + ".\n\n";

        emailBody += "Log in to your account today and start browsing for our products and mobile plans! Enjoy!";

        try {
            Properties props = new Properties();
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.host", emailServerName);
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.debug", "true");
            javax.mail.Authenticator auth = new SMTPAuthenticator(smtpAuthUser, smtpAuthPassword);
            Session session = Session.getInstance(props, auth);
            session.setDebug(true);
            Message msg = new MimeMessage(session);

            if (msg != null) {
                msg.setFrom(InternetAddress.parse(fromEmailAddress, false)[0]);
                msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmailAddress, false));
                msg.setSubject("Welcome to Tellybuddy, " + customer.getFirstName() + " " + customer.getLastName() + "!");
                msg.setText(emailBody);
                msg.setHeader("X-Mailer", mailer);

                Date timeStamp = new Date();
                msg.setSentDate(timeStamp);

                Transport.send(msg);

                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }
    }
}
