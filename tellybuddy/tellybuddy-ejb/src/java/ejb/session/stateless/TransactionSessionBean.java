/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Customer;
import entity.DiscountCode;
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
import util.exception.CreateNewSaleTransactionException;
import util.exception.CustomerNotFoundException;
import util.exception.DiscountCodeNotFoundException;
import util.exception.ProductInsufficientQuantityOnHandException;
import util.exception.ProductNotFoundException;
import util.exception.TransactionAlreadyVoidedRefundedException;
import util.exception.TransactionNotFoundException;

/**
 *
 * @author markt
 */
@Stateless
@Local(TransactionSessionBeanLocal.class)
public class TransactionSessionBean implements TransactionSessionBeanLocal {

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
    public Transaction createNewTransaction(Long customerId, Transaction newTransaction, String discountCodeName) throws CustomerNotFoundException, CreateNewSaleTransactionException, DiscountCodeNotFoundException {
        if (newTransaction != null) {
            try {
                Customer customer = customerSessionBeanLocal.retrieveCustomerByCustomerId(customerId);
                DiscountCode discountCode = discountCodeSessionBeanLocal.retrieveDiscountCodeByDiscountCodeName(discountCodeName);
                newTransaction.setCustomer(customer);
                newTransaction.setDiscountCode(discountCode);
                customer.getTransactions().add(newTransaction);

                em.persist(newTransaction);

                for (TransactionLineItem transactionLineItem : newTransaction.getTransactionLineItems()) {
                    productSessionBeanLocal.debitQuantityOnHand(transactionLineItem.getProduct().getProductId(), transactionLineItem.getQuantity());
                    em.persist(transactionLineItem);
                }

                em.flush();

                return newTransaction;
            } catch (ProductNotFoundException | ProductInsufficientQuantityOnHandException ex) {
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

        return query.getResultList();
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

    // Updated in v4.1
    @Override
    public void voidRefundTransaction(Long transactionId) throws TransactionNotFoundException, TransactionAlreadyVoidedRefundedException {
        Transaction transaction = retrieveTransactionByTransactionId(transactionId);

        if (!transaction.getVoidRefund()) {
            for (TransactionLineItem transactionLineItem : transaction.getTransactionLineItems()) {
                try {
                    productSessionBeanLocal.creditQuantityOnHand(transactionLineItem.getProduct().getProductId(), transactionLineItem.getQuantity());
                } catch (ProductNotFoundException ex) {
                    ex.printStackTrace(); // Ignore exception since this should not happen
                }
            }

            transaction.setVoidRefund(true);
        } else {
            throw new TransactionAlreadyVoidedRefundedException("The sale transaction has aready been voided/refunded");
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
