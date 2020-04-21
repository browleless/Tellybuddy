package ws.restful;

import ejb.session.stateless.BillSessionBeanLocal;
import ejb.session.stateless.CustomerSessionBeanLocal;
import entity.Bill;
import entity.Customer;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import util.exception.BillNotFoundException;
import util.exception.InvalidLoginCredentialException;
import ws.datamodel.ErrorRsp;
import ws.datamodel.RetrieveBillRsp;
import ws.datamodel.RetrieveCustomerBillsRsp;
import ws.datamodel.RetrieveCustomerOutstandingBillsReq;
import ws.datamodel.RetrieveCustomerOutstandingBillsRsp;
import ws.datamodel.RetrieveSubscriptionBillsReq;
import ws.datamodel.RetrieveSubscriptionBillsRsp;
import ws.datamodel.RetrieveSubscriptionOutstandingBillsReq;
import ws.datamodel.RetrieveSubscriptionOutstandingBillsRsp;

/**
 * REST Web Service
 *
 * @author tjle2
 */
@Path("Bill")
public class BillResource {

    @Context
    private UriInfo context;

    private final SessionBeanLookup sessionBeanLookup;

    private final BillSessionBeanLocal billSessionBeanLocal;
    private final CustomerSessionBeanLocal customerSessionBeanLocal;

    /**
     * Creates a new instance of BillResource
     */
    public BillResource() {

        sessionBeanLookup = new SessionBeanLookup();

        billSessionBeanLocal = sessionBeanLookup.lookupBillSessionBeanLocal();
        customerSessionBeanLocal = sessionBeanLookup.lookupCustomerSessionBeanLocal();
    }

    /**
     * Retrieves representation of an instance of ws.restful.BillResource
     *
     * @return an instance of java.lang.String
     */
    @Path("retrieveCustomerBills")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveCustomerBills(@QueryParam("username") String username, @QueryParam("password") String password) {
        try {
            Customer customer = customerSessionBeanLocal.customerLogin(username, password);
            System.out.println("********** BillResource.retrieveCustomerBills(): Customer " + customer.getUsername() + " login remotely via web service");

            List<Bill> bills = billSessionBeanLocal.retrieveBillByCustomer(customer);

            for (Bill bill : bills) {
                bill.setCustomer(null);
                bill.setUsageDetail(null);
            }

            return Response.status(Status.OK).entity(new RetrieveCustomerBillsRsp(bills)).build();
        } catch (InvalidLoginCredentialException ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED).entity(errorRsp).build();
        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }
    }

    @Path("retrieveBillsBySubscription")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveBillsBySubscription(RetrieveSubscriptionBillsReq retrieveSubscriptionBillsReq) {
        try {
            Customer customer = customerSessionBeanLocal.customerLogin(retrieveSubscriptionBillsReq.getUsername(), retrieveSubscriptionBillsReq.getPassword());
            System.out.println("********** BillResource.retrieveBillsBySubscription(): Customer " + customer.getUsername() + " login remotely via web service");

            List<Bill> bills = billSessionBeanLocal.retrieveBillsBySubscription(retrieveSubscriptionBillsReq.getSubscription());

            for (Bill bill : bills) {
                bill.setCustomer(null);
                bill.getUsageDetail().setBill(null);
                bill.getUsageDetail().setSubscription(null);
            }

            return Response.status(Status.OK).entity(new RetrieveSubscriptionBillsRsp(bills)).build();
        } catch (InvalidLoginCredentialException ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED).entity(errorRsp).build();
        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }
    }

    @Path("retrieveCustomerOutstandingBills")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveCustomerOutstandingBills(RetrieveCustomerOutstandingBillsReq retrieveCustomerOutstandingBillsReq) {
        try {
            Customer customer = customerSessionBeanLocal.customerLogin(retrieveCustomerOutstandingBillsReq.getUsername(), retrieveCustomerOutstandingBillsReq.getPassword());
            System.out.println("********** BillResource.retrieveCustomerOutstandingBills(): Customer " + customer.getUsername() + " login remotely via web service");

            List<Bill> bills = billSessionBeanLocal.retrieveCustomerOutstandingBills(retrieveCustomerOutstandingBillsReq.getCustomer());

            for (Bill bill : bills) {
                bill.setCustomer(null);

                bill.getUsageDetail().setBill(null);
                bill.getUsageDetail().getSubscription().getUsageDetails().clear();
                bill.getUsageDetail().getSubscription().setCustomer(null);
                bill.getUsageDetail().getSubscription().getPhoneNumber().setSubscription(null);
            }

            return Response.status(Status.OK).entity(new RetrieveCustomerOutstandingBillsRsp(bills)).build();
        } catch (InvalidLoginCredentialException ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED).entity(errorRsp).build();
        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }
    }
    
    @Path("retrieveSubscriptionOutstandingBills")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveSubscriptionOutstandingBills(RetrieveSubscriptionOutstandingBillsReq retrieveSubscriptionOutstandingBillsReq) {
        try {
            Customer customer = customerSessionBeanLocal.customerLogin(retrieveSubscriptionOutstandingBillsReq.getUsername(), retrieveSubscriptionOutstandingBillsReq.getPassword());
            System.out.println("********** BillResource.retrieveSubscriptionOutstandingBills(): Customer " + customer.getUsername() + " login remotely via web service");

            List<Bill> bills = billSessionBeanLocal.retrieveSubscriptionOutstandingBills(retrieveSubscriptionOutstandingBillsReq.getSubscription());

            for (Bill bill : bills) {
                bill.setCustomer(null);
                bill.setUsageDetail(null);
            }

            return Response.status(Status.OK).entity(new RetrieveSubscriptionOutstandingBillsRsp(bills)).build();
        } catch (InvalidLoginCredentialException ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED).entity(errorRsp).build();
        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }
    }

    @Path("retrieveBill/{billId}")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveBill(@QueryParam("username") String username, @QueryParam("password") String password, @PathParam("billId") Long billId) {
        try {
            Customer customer = customerSessionBeanLocal.customerLogin(username, password);
            System.out.println("********** BillResource.retrieveBill(): Customer " + customer.getUsername() + " login remotely via web service");

            Bill bill = billSessionBeanLocal.retrieveBillByBillId(billId);

            bill.getCustomer().getQuizAttempts().clear();
            bill.getCustomer().getTransactions().clear();
            bill.getCustomer().getSubscriptions().clear();
            bill.getCustomer().getBills().clear();
            bill.getCustomer().setFamilyGroup(null);

            bill.getCustomer().setPassword(null);
            bill.getCustomer().setSalt(null);

            bill.getUsageDetail().setBill(null);
            bill.getUsageDetail().setSubscription(null);

            return Response.status(Status.OK).entity(new RetrieveBillRsp(bill)).build();
        } catch (InvalidLoginCredentialException ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED).entity(errorRsp).build();
        } catch (BillNotFoundException ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();
        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }
    }
}
