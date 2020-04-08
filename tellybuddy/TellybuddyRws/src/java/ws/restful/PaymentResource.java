package ws.restful;

import ejb.session.stateless.CustomerSessionBeanLocal;
import ejb.session.stateless.PaymentSessionBeanLocal;
import entity.Customer;
import entity.Payment;
import java.util.List;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import util.exception.BillAlreadyPaidException;
import util.exception.BillNotFoundException;
import util.exception.InvalidLoginCredentialException;
import ws.datamodel.ErrorRsp;
import ws.datamodel.MakeBillPaymentReq;
import ws.datamodel.MakeBillPaymentRsp;
import ws.datamodel.RetrieveCustomerPaymentsRsp;

/**
 * REST Web Service
 *
 * @author tjle2
 */
@Path("Payment")
public class PaymentResource {

    @Context
    private UriInfo context;

    private final SessionBeanLookup sessionBeanLookup;

    private final CustomerSessionBeanLocal customerSessionBeanLocal;
    private final PaymentSessionBeanLocal paymentSessionBeanLocal;

    /**
     * Creates a new instance of PaymentResource
     */
    public PaymentResource() {

        sessionBeanLookup = new SessionBeanLookup();

        customerSessionBeanLocal = sessionBeanLookup.lookupCustomerSessionBeanLocal();
        paymentSessionBeanLocal = sessionBeanLookup.lookupPaymentSessionBeanLocal();
    }

    /**
     * Retrieves representation of an instance of ws.restful.PaymentResource
     *
     * @return an instance of java.lang.String
     */
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveCustomerPayments(@QueryParam("username") String username, @QueryParam("password") String password) {
        try {
            Customer customer = customerSessionBeanLocal.customerLogin(username, password);
            System.out.println("********** PaymentResource.retrieveCustomerPayments(): Customer " + customer.getUsername() + " login remotely via web service");

            List<Payment> payments = paymentSessionBeanLocal.retrieveCustomerPayments(customer);

            return Response.status(Response.Status.OK).entity(new RetrieveCustomerPaymentsRsp(payments)).build();
        } catch (InvalidLoginCredentialException ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED).entity(errorRsp).build();
        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response makeBillPayment(MakeBillPaymentReq makeBillPaymentReq) {

        if (makeBillPaymentReq != null) {
            try {
                Customer customer = customerSessionBeanLocal.customerLogin(makeBillPaymentReq.getUsername(), makeBillPaymentReq.getPassword());
                System.out.println("********** PaymentResource.makeBillPayment(): Customer " + customer.getUsername() + " login remotely via web service");

                Long paymentId = paymentSessionBeanLocal.createNewBillPayment(makeBillPaymentReq.getPayment(), makeBillPaymentReq.getBill());

                return Response.status(Response.Status.OK).entity(new MakeBillPaymentRsp(paymentId)).build();
            } catch (InvalidLoginCredentialException ex) {
                ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
                return Response.status(Response.Status.UNAUTHORIZED).entity(errorRsp).build();
            } catch (BillAlreadyPaidException | BillNotFoundException ex) {
                ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
                return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();
            } catch (Exception ex) {
                ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
            }
        } else {
            ErrorRsp errorRsp = new ErrorRsp("Invalid create bill payment request");
            return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();
        }
    }
}
