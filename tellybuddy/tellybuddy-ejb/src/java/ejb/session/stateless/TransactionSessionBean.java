/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Customer;
import entity.DiscountCode;
import entity.Payment;
import entity.Subscription;
import entity.Transaction;
import entity.TransactionLineItem;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBContext;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.enumeration.TransactionStatusEnum;
import util.exception.CreateNewSaleTransactionException;
import util.exception.CreateNewSubscriptionException;
import util.exception.CustomerNotFoundException;
import util.exception.CustomerNotYetApproved;
import util.exception.DiscountCodeNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.PhoneNumberInUseException;
import util.exception.PlanAlreadyDisabledException;
import util.exception.ProductInsufficientQuantityOnHandException;
import util.exception.ProductNotFoundException;
import util.exception.SubscriptionExistException;
import util.exception.TransactionAlreadyVoidedRefundedException;
import util.exception.TransactionNotFoundException;
import util.exception.TransactionUnableToBeRefundedException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author markt
 */
@Stateless
@Local(TransactionSessionBeanLocal.class)
public class TransactionSessionBean implements TransactionSessionBeanLocal {

    @EJB
    private SubscriptionSessonBeanLocal subscriptionSessonBeanLocal;

    @EJB(name = "PaymentSessionBeanLocal")
    private PaymentSessionBeanLocal paymentSessionBeanLocal;

    @PersistenceContext(unitName = "tellybuddy-ejbPU")
    private EntityManager em;
    @Resource
    private EJBContext eJBContext;

    @EJB
    private CustomerSessionBeanLocal customerSessionBeanLocal;
    @EJB
    private ProductSessionBeanLocal productSessionBeanLocal;
    @EJB
    private DiscountCodeSessionBeanLocal discountCodeSessionBeanLocal;

    public TransactionSessionBean() {
    }

    @Override
    public Transaction createNewTransaction(Long customerId, Transaction newTransaction, String discountCodeName, String creditCardNo, String cvv) throws CustomerNotFoundException, CreateNewSaleTransactionException, DiscountCodeNotFoundException {
        if (newTransaction != null) {
            try {

                Customer customer = customerSessionBeanLocal.retrieveCustomerByCustomerId(customerId);

                List<TransactionLineItem> transactionLineItemsToAdd = new ArrayList<>();

                for (TransactionLineItem transactionLineItem : newTransaction.getTransactionLineItems()) {
                    transactionLineItem.setTransaction(null);
                    transactionLineItemsToAdd.add(transactionLineItem);
                }

                newTransaction.setTransactionStatus(TransactionStatusEnum.PROCESSING);
                newTransaction.setTransactionDateTime(new Date());
                newTransaction.setCustomer(customer);
                newTransaction.getTransactionLineItems().clear();

                em.persist(newTransaction);
                em.flush();

                if (discountCodeName != null) {
                    DiscountCode discountCode = discountCodeSessionBeanLocal.retrieveDiscountCodeByDiscountCodeName(discountCodeName);
                    newTransaction.setDiscountCode(discountCode);
                    discountCode.setTransaction(newTransaction);
                }

                for (TransactionLineItem transactionLineItemToAdd : transactionLineItemsToAdd) {
                    if (transactionLineItemToAdd.getProductItem() != null || transactionLineItemToAdd.getProduct() != null) {
                        if (transactionLineItemToAdd.getProduct() != null) {
                            productSessionBeanLocal.debitQuantityOnHand(transactionLineItemToAdd.getProduct().getProductId(), transactionLineItemToAdd.getQuantity());

                            transactionLineItemToAdd.setTransaction(newTransaction);

                            em.persist(transactionLineItemToAdd);
                            em.flush();

                            transactionLineItemToAdd.setProduct(transactionLineItemToAdd.getProduct());
                        } else {
                            productSessionBeanLocal.debitQuantityOnHand(transactionLineItemToAdd.getProductItem().getLuxuryProduct().getProductId(), transactionLineItemToAdd.getQuantity());

                            transactionLineItemToAdd.setTransaction(newTransaction);

                            em.persist(transactionLineItemToAdd);
                            em.flush();

                            transactionLineItemToAdd.setProductItem(transactionLineItemToAdd.getProductItem());
                        }
                    } else {
                        Subscription subscription = transactionLineItemToAdd.getSubscription();
                        Subscription newSubscription = new Subscription(subscription.getAllocatedData(), subscription.getAllocatedTalkTime(), subscription.getAllocatedSms());

                        newSubscription = subscriptionSessonBeanLocal.createNewSubscription(newSubscription, subscription.getPlan().getPlanId(), subscription.getCustomer().getCustomerId(), subscription.getPhoneNumber().getPhoneNumberId());

                        transactionLineItemToAdd.setTransaction(newTransaction);
                        transactionLineItemToAdd.setSubscription(null);

                        em.persist(transactionLineItemToAdd);
                        em.flush();

                        transactionLineItemToAdd.setSubscription(newSubscription);
                    }

                    transactionLineItemToAdd.setTransaction(newTransaction);
                    newTransaction.getTransactionLineItems().add(transactionLineItemToAdd);
                }

                Payment newPayment = paymentSessionBeanLocal.createNewPayment(creditCardNo, cvv, newTransaction.getTotalPrice());

                newTransaction.setPayment(newPayment);
                customer.getTransactions().add(newTransaction);

                return newTransaction;
            } catch (ProductNotFoundException | ProductInsufficientQuantityOnHandException | CreateNewSubscriptionException | CustomerNotFoundException | CustomerNotYetApproved | DiscountCodeNotFoundException | PhoneNumberInUseException | PlanAlreadyDisabledException | SubscriptionExistException | InputDataValidationException | UnknownPersistenceException ex) {
                // The line below rolls back all changes made to the database.
                eJBContext.setRollbackOnly();
                throw new CreateNewSaleTransactionException(ex.getMessage());
            }
        } else {
            throw new CreateNewSaleTransactionException("Sale transaction information not provided");
        }
    }

    @Override
    public List<Transaction> retrieveAllTransactions() {
        Query query = em.createQuery("SELECT st FROM Transaction st");

        List<Transaction> transactions = query.getResultList();

        for (Transaction transaction : transactions) {
            transaction.getTransactionLineItems().size();
        }

        return transactions;
    }

    @Override
    public List<Transaction> retrieveAllMonthlyTransactions() {

        Query query = em.createQuery("SELECT st FROM Transaction st WHERE st.transactionDateTime BETWEEN :inputMonthStart AND :inputMonthEnd");

        LocalDateTime monthBegin = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime monthEnd = LocalDateTime.now().withDayOfMonth(1).plusMonths(1).minusDays(-1).withHour(23).withMinute(59).withSecond(59);
        Date inputMonthStart = Date.from(monthBegin.atZone(ZoneId.systemDefault()).toInstant());
        Date inputMonthEnd = Date.from(monthEnd.atZone(ZoneId.systemDefault()).toInstant());
        query.setParameter("inputMonthStart", inputMonthStart);
        query.setParameter("inputMonthEnd", inputMonthEnd);
        return query.getResultList();
    }

    @Override
    public List<Transaction> retrieveTransactionsByCustomer(Customer customer) {
        Query query = em.createQuery("SELECT t FROM Transaction t WHERE t.customer = :customer");
        query.setParameter("customer", customer);
        return query.getResultList();

    }

    @Override
    public List<TransactionLineItem> retrieveTransactionLineItemsByProductId(Long productId) {
        Query query = em.createQuery("SELECT tl FROM TransactionLineItem tl WHERE tl.product.productId = :inProductId");
        query.setParameter("inProductId", productId);

        if (query.getResultList() == null) {
            return new ArrayList<TransactionLineItem>();
        } else {
            return query.getResultList();
        }
    }

    @Override
    public Transaction retrieveTransactionByTransactionId(Long transactionId) throws TransactionNotFoundException {
        Transaction transaction = em.find(Transaction.class, transactionId);

        if (transaction != null) {
            transaction.getTransactionLineItems().size();

            return transaction;
        } else {
            throw new TransactionNotFoundException("Sale Transaction ID " + transactionId + " does not exist!");
        }
    }

    @Override
    public void updateTransaction(Transaction Transaction) {
        em.merge(Transaction);
    }

    @Override
    public void requestTransactionRefund(Long transactionId) throws TransactionNotFoundException, TransactionAlreadyVoidedRefundedException, TransactionUnableToBeRefundedException {
        Transaction transactionToRefund = retrieveTransactionByTransactionId(transactionId);

        if (transactionToRefund.getTransactionStatus() == TransactionStatusEnum.RECEIVED) {
            transactionToRefund.setTransactionStatus(TransactionStatusEnum.REFUND_REQUESTED);
        } else if (transactionToRefund.getTransactionStatus() == TransactionStatusEnum.REFUNDED) {
            throw new TransactionAlreadyVoidedRefundedException("The sale transaction has already been refunded!");
        } else {
            throw new TransactionUnableToBeRefundedException("Please wait till you have received the product before requesting a refund!");
        }
    }

    @Override
    public void refundTransaction(Long transactionId) throws TransactionNotFoundException, TransactionAlreadyVoidedRefundedException, TransactionUnableToBeRefundedException {
        Transaction transaction = retrieveTransactionByTransactionId(transactionId);

        if (transaction.getTransactionStatus() == TransactionStatusEnum.REFUND_REQUESTED) {
            for (TransactionLineItem transactionLineItem : transaction.getTransactionLineItems()) {
                try {
                    productSessionBeanLocal.creditQuantityOnHand(transactionLineItem.getProduct().getProductId(), transactionLineItem.getQuantity());
                } catch (ProductNotFoundException ex) {
                    ex.printStackTrace(); // Ignore exception since this should not happen
                }
            }

            transaction.setTransactionStatus(TransactionStatusEnum.REFUNDED);
        } else if (transaction.getTransactionStatus() == TransactionStatusEnum.REFUNDED) {
            throw new TransactionAlreadyVoidedRefundedException("The sale transaction has already been refunded!");
        } else {
            throw new TransactionUnableToBeRefundedException("An error has occurred! Plesae check that the correct sale transaction has selected");
        }
    }

    @Override
    public void deleteTransaction(Transaction transaction) {
        throw new UnsupportedOperationException();
    }

    public void persist(Object object) {
        em.persist(object);
    }

}
