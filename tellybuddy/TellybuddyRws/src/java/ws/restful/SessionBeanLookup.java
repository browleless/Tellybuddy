package ws.restful;

import ejb.session.stateless.AnnouncementSessionBeanLocal;
import ejb.session.stateless.AnswerSessionBeanLocal;
import ejb.session.stateless.BillSessionBeanLocal;
import ejb.session.stateless.CategorySessionBeanLocal;
import ejb.session.stateless.CustomerSessionBeanLocal;
import ejb.session.stateless.DiscountCodeSessionBeanLocal;
import ejb.session.stateless.EmailSessionBeanLocal;
import ejb.session.stateless.FamilyGroupSessionBeanLocal;
import ejb.session.stateless.PaymentSessionBeanLocal;
import ejb.session.stateless.PhoneNumberSessionBeanLocal;
import ejb.session.stateless.PlanSessionBeanLocal;
import ejb.session.stateless.ProductItemSessionBeanLocal;
import ejb.session.stateless.ProductSessionBeanLocal;
import ejb.session.stateless.QuestionSessionBeanLocal;
import ejb.session.stateless.QuizAttemptSessionBeanLocal;
import ejb.session.stateless.QuizSessionBeanLocal;
import ejb.session.stateless.SubscriptionSessonBeanLocal;
import ejb.session.stateless.TagSessionBeanLocal;
import ejb.session.stateless.TransactionSessionBeanLocal;
import ejb.session.stateless.UsageDetailSessionBeanLocal;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import ejb.session.stateless.QuizResponseSessionBeanLocal;

/**
 *
 * @author tjle2
 */
public class SessionBeanLookup {

    private final String ejbModuleJndiPath;

    public SessionBeanLookup() {
        this.ejbModuleJndiPath = "java:global/tellybuddy/tellybuddy-ejb/";
    }

    public ProductSessionBeanLocal lookupProductSessionBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (ProductSessionBeanLocal) c.lookup(ejbModuleJndiPath + "ProductSessionBean!ejb.session.stateless.ProductSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    public AnnouncementSessionBeanLocal lookupAnnouncementSessionBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (AnnouncementSessionBeanLocal) c.lookup(ejbModuleJndiPath + "AnnouncementSessionBean!ejb.session.stateless.AnnouncementSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    public AnswerSessionBeanLocal lookupAnswerSessionBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (AnswerSessionBeanLocal) c.lookup(ejbModuleJndiPath + "AnswerSessionBean!ejb.session.stateless.AnswerSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    public BillSessionBeanLocal lookupBillSessionBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (BillSessionBeanLocal) c.lookup(ejbModuleJndiPath + "BillSessionBean!ejb.session.stateless.BillSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    public CategorySessionBeanLocal lookupCategorySessionBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (CategorySessionBeanLocal) c.lookup(ejbModuleJndiPath + "CategorySessionBean!ejb.session.stateless.CategorySessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    public CustomerSessionBeanLocal lookupCustomerSessionBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (CustomerSessionBeanLocal) c.lookup(ejbModuleJndiPath + "CustomerSessionBean!ejb.session.stateless.CustomerSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    public DiscountCodeSessionBeanLocal lookupDiscountCodeSessionBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (DiscountCodeSessionBeanLocal) c.lookup(ejbModuleJndiPath + "DiscountCodeSessionBean!ejb.session.stateless.DiscountCodeSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    public FamilyGroupSessionBeanLocal lookupFamilyGroupSessionBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (FamilyGroupSessionBeanLocal) c.lookup(ejbModuleJndiPath + "FamilyGroupSessionBean!ejb.session.stateless.FamilyGroupSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    public PaymentSessionBeanLocal lookupPaymentSessionBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (PaymentSessionBeanLocal) c.lookup(ejbModuleJndiPath + "PaymentSessionBean!ejb.session.stateless.PaymentSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    public PhoneNumberSessionBeanLocal lookupPhoneNumberSessionBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (PhoneNumberSessionBeanLocal) c.lookup(ejbModuleJndiPath + "PhoneNumberSessionBean!ejb.session.stateless.PhoneNumberSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    public PlanSessionBeanLocal lookupPlanSessionBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (PlanSessionBeanLocal) c.lookup(ejbModuleJndiPath + "PlanSessionBean!ejb.session.stateless.PlanSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    public ProductItemSessionBeanLocal lookupProductItemSessionBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (ProductItemSessionBeanLocal) c.lookup(ejbModuleJndiPath + "ProductItemSessionBean!ejb.session.stateless.ProductItemSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    public QuestionSessionBeanLocal lookupQuestionSessionBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (QuestionSessionBeanLocal) c.lookup(ejbModuleJndiPath + "QuestionSessionBean!ejb.session.stateless.QuestionSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    public QuizAttemptSessionBeanLocal lookupQuizAttemptSessionBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (QuizAttemptSessionBeanLocal) c.lookup(ejbModuleJndiPath + "QuizAttemptSessionBean!ejb.session.stateless.QuizAttemptSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    public QuizSessionBeanLocal lookupQuizSessionBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (QuizSessionBeanLocal) c.lookup(ejbModuleJndiPath + "QuizSessionBean!ejb.session.stateless.QuizSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    public QuizResponseSessionBeanLocal lookupQuizResponseSessionBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (QuizResponseSessionBeanLocal) c.lookup(ejbModuleJndiPath + "QuizResponseSessionBean!ejb.session.stateless.QuizResponseSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    public SubscriptionSessonBeanLocal lookupSubscriptionSessonBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (SubscriptionSessonBeanLocal) c.lookup(ejbModuleJndiPath + "SubscriptionSessonBean!ejb.session.stateless.SubscriptionSessonBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    public TagSessionBeanLocal lookupTagSessionBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (TagSessionBeanLocal) c.lookup(ejbModuleJndiPath + "TagSessionBean!ejb.session.stateless.TagSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    public TransactionSessionBeanLocal lookupTransactionSessionBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (TransactionSessionBeanLocal) c.lookup(ejbModuleJndiPath + "TransactionSessionBean!ejb.session.stateless.TransactionSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    public UsageDetailSessionBeanLocal lookupUsageDetailSessionBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (UsageDetailSessionBeanLocal) c.lookup(ejbModuleJndiPath + "UsageDetailSessionBean!ejb.session.stateless.UsageDetailSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
    
    public EmailSessionBeanLocal lookupEmailSessionBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (EmailSessionBeanLocal) c.lookup(ejbModuleJndiPath + "EmailSessionBean!ejb.session.stateless.EmailSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
}
