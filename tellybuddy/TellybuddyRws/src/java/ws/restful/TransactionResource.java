/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.restful;

import ejb.session.stateless.CustomerSessionBeanLocal;
import ejb.session.stateless.TransactionSessionBeanLocal;
import entity.Customer;
import entity.Transaction;
import entity.TransactionLineItem;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import util.exception.CreateNewSaleTransactionException;
import util.exception.CustomerNotFoundException;
import util.exception.DiscountCodeNotFoundException;
import util.exception.InvalidLoginCredentialException;
import util.exception.TransactionAlreadyVoidedRefundedException;
import util.exception.TransactionNotFoundException;
import util.exception.TransactionUnableToBeRefundedException;
import ws.datamodel.CreateNewTransactionReq;
import ws.datamodel.CreateNewTransactionRsp;
import ws.datamodel.ErrorRsp;
import ws.datamodel.RefundTransactionReq;
import ws.datamodel.RefundTransactionRsp;
import ws.datamodel.RetrieveCustomerTransactionsRsp;
import ws.datamodel.RetrieveTransactionRsp;

/**
 * REST Web Service
 *
 * @author markt
 */
@Path("Transaction")
public class TransactionResource {

    CustomerSessionBeanLocal customerSessionBeanLocal = lookupCustomerSessionBeanLocal();

    TransactionSessionBeanLocal transactionSessionBeanLocal = lookupTransactionSessionBeanLocal();

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of TransactionResource
     */
    public TransactionResource() {
    }

    @Path("retrieveCustomerTransactions")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveCustomerTransactions(@QueryParam("username") String username, @QueryParam("password") String password) {
        try {
            Customer customer = customerSessionBeanLocal.customerLogin(username, password);
            System.out.println("********** TransactionResource.retrieveCustomerTransactions: Customer " + customer.getUsername() + " login remotely via web service");

            List<Transaction> transactions = transactionSessionBeanLocal.retrieveTransactionsByCustomer(customer);

            for (Transaction transaction : transactions) {
                transaction.setCustomer(null);
                transaction.setDiscountCode(null);
                transaction.setTransactionLineItems(null);
            }
            return Response.status(Status.OK).entity(new RetrieveCustomerTransactionsRsp(transactions)).build();
        } catch (InvalidLoginCredentialException ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED).entity(errorRsp).build();
        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }
    }

    @Path("retrieveTransactionById/{transactionId}")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveTransaction(@QueryParam("username") String username, @QueryParam("password") String password, @PathParam("transactionId") Long transactionId) {
        try {
            Customer customer = customerSessionBeanLocal.customerLogin(username, password);
            System.out.println("********** TransactionResource.retrieveTransaction(): Customer " + customer.getUsername() + " login remotely via web service");

            Transaction transaction = transactionSessionBeanLocal.retrieveTransactionByTransactionId(transactionId);

            transaction.setCustomer(null);
            
            if (transaction.getDiscountCode() != null) {
                transaction.getDiscountCode().setTransaction(null);
            }

            for (TransactionLineItem tli : transaction.getTransactionLineItems()) {
                tli.setTransaction(null);

                if (tli.getProduct() != null) {
                    tli.getProduct().setCategory(null);
                    tli.getProduct().setTags(null);
                }

                if (tli.getProductItem() != null) {
                    tli.getProductItem().setLuxuryProduct(null);
                }

                if (tli.getSubscription() != null) {
                    tli.getSubscription().setCustomer(null);
                    tli.getSubscription().setUsageDetails(null);
                    tli.getSubscription().getPhoneNumber().setSubscription(null);
                }
            }

            return Response.status(Response.Status.OK).entity(new RetrieveTransactionRsp(transaction)).build();
        } catch (InvalidLoginCredentialException ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED).entity(errorRsp).build();
        } catch (TransactionNotFoundException ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();
        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }
    }

    @Path("createNewTransaction")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createNewTransaction(CreateNewTransactionReq createNewTransactionReq) {

        if (createNewTransactionReq != null) {
            try {
                Customer customer = customerSessionBeanLocal.customerLogin(createNewTransactionReq.getUsername(), createNewTransactionReq.getPassword());
                System.out.println("********** TransactionResource.createNewTransaction(): Customer " + customer.getUsername() + " login remotely via web service");

                Transaction transaction = transactionSessionBeanLocal.createNewTransaction(createNewTransactionReq.getCustomerId(), createNewTransactionReq.getNewTransaction(), createNewTransactionReq.getDiscountCodeName(), createNewTransactionReq.getCreditCardNo(), createNewTransactionReq.getCvv());
                CreateNewTransactionRsp createNewTransactionRsp = new CreateNewTransactionRsp(transaction.getTransactionId());

                return Response.status(Response.Status.OK).entity(createNewTransactionRsp).build();
            } catch (InvalidLoginCredentialException ex) {
                ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
                return Response.status(Status.UNAUTHORIZED).entity(errorRsp).build();
            } catch (CustomerNotFoundException | CreateNewSaleTransactionException | DiscountCodeNotFoundException ex) {
                ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
                return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();
            } catch (Exception ex) {
                ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
            }
        } else {
            ErrorRsp errorRsp = new ErrorRsp("Invalid create new transaction request");
            return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();
        }
    }

    @Path("refundTransactionRequest")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response refundTransactionRequest(RefundTransactionReq refundTransactionReq) {

        try {
            Customer customer = customerSessionBeanLocal.customerLogin(refundTransactionReq.getUsername(), refundTransactionReq.getPassword());
            System.out.println("********** TransactionResource.refundTransactionRequest(): Customer " + customer.getUsername() + " login remotely via web service");

            transactionSessionBeanLocal.requestTransactionRefund(refundTransactionReq.getTransactionId());

            return Response.status(Response.Status.OK).entity(new RefundTransactionRsp(refundTransactionReq.getTransactionId())).build();
        } catch (InvalidLoginCredentialException ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Status.UNAUTHORIZED).entity(errorRsp).build();
        } catch (TransactionNotFoundException | TransactionAlreadyVoidedRefundedException | TransactionUnableToBeRefundedException ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();
        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }
    }

    private TransactionSessionBeanLocal lookupTransactionSessionBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (TransactionSessionBeanLocal) c.lookup("java:global/tellybuddy/tellybuddy-ejb/TransactionSessionBean!ejb.session.stateless.TransactionSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    private CustomerSessionBeanLocal lookupCustomerSessionBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (CustomerSessionBeanLocal) c.lookup("java:global/tellybuddy/tellybuddy-ejb/CustomerSessionBean!ejb.session.stateless.CustomerSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
}
