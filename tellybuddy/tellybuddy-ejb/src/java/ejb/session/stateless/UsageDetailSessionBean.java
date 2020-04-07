package ejb.session.stateless;

import entity.Subscription;
import entity.UsageDetail;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.SubscriptionNotFoundException;
import util.exception.UsageDetailNotFoundException;

/**
 *
 * @author tjle2
 */
@Stateless
public class UsageDetailSessionBean implements UsageDetailSessionBeanLocal {

    @EJB
    private SubscriptionSessonBeanLocal subscriptionSessonBeanLocal;

    @PersistenceContext(unitName = "tellybuddy-ejbPU")
    private EntityManager entityManager;

    @Override
    public Long createNewUsageDetail(Subscription subcription) throws SubscriptionNotFoundException {

        Date currentDate = new Date();
        Date futureDate = new Date(currentDate.getTime());
        futureDate.setMonth((new Date().getMonth() + 1) % 12);

        UsageDetail newUsageDetail = new UsageDetail(currentDate, futureDate);
        Subscription subscriptionToAssociateWith = subscriptionSessonBeanLocal.retrieveSubscriptionBySubscriptionId(subcription.getSubscriptionId());

        newUsageDetail.setSubscription(subscriptionToAssociateWith);
        entityManager.persist(newUsageDetail);
        entityManager.flush();

        subscriptionToAssociateWith.getUsageDetails().add(newUsageDetail);

        return newUsageDetail.getUsageDetailId();
    }

    @Override
    public UsageDetail retrieveUsageDetailByUsageDetailId(Long usageDetailId) throws UsageDetailNotFoundException {

        UsageDetail usageDetail = entityManager.find(UsageDetail.class, usageDetailId);

        if (usageDetail != null) {
            return usageDetail;
        } else {
            throw new UsageDetailNotFoundException("Usage Detail" + usageDetail + " does not exist!");
        }
    }

    @Override
    public List<UsageDetail> retrieveSubscriptionUsageDetails(Subscription subscription) {

        Query query = entityManager.createQuery("SELECT ud FROM UsageDetail ud WHERE ud.subscription = :inSubscription");
        query.setParameter("inSubscription", subscription);

        return query.getResultList();
    }

    @Override
    public UsageDetail retrieveSubscriptionCurrentUsageDetails(Subscription subscription) {

        Query query = entityManager.createQuery("SELECT ud FROM UsageDetail ud WHERE ud.subscription = :inSubscription AND (CURRENT_TIMESTAMP BETWEEN ud.startDate and ud.endDate)");
        query.setParameter("inSubscription", subscription);

        return (UsageDetail) query.getSingleResult();
    }

    @Override
    public void updateUsageDetail(UsageDetail usageDetail) throws UsageDetailNotFoundException {

        try {
            UsageDetail usageDetailToUpdate = retrieveUsageDetailByUsageDetailId(usageDetail.getUsageDetailId());

            // need to set to simulate incrementing usage detail, then call this with a timer
        } catch (UsageDetailNotFoundException ex) {
            throw new UsageDetailNotFoundException("Usage Detail Id not provided for update!");
        }
    }
}
