package ws.restful;

import ejb.session.stateless.CustomerSessionBeanLocal;
import ejb.session.stateless.UsageDetailSessionBeanLocal;
import entity.Customer;
import entity.UsageDetail;
import java.util.List;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import util.exception.InvalidLoginCredentialException;
import util.exception.UsageDetailNotFoundException;
import ws.datamodel.ErrorRsp;
import ws.datamodel.RetrieveSubscriptionCurrentUsageDetailsReq;
import ws.datamodel.RetrieveSubscriptionCurrentUsageDetailsRsp;
import ws.datamodel.RetrieveSubscriptionUsageDetailsReq;
import ws.datamodel.RetrieveSubscriptionUsageDetailsRsp;
import ws.datamodel.RetrieveUsageDetailRsp;

/**
 * REST Web Service
 *
 * @author tjle2
 */
@Path("UsageDetail")
public class UsageDetailResource {

    @Context
    private UriInfo context;

    private final SessionBeanLookup sessionBeanLookup;

    private final UsageDetailSessionBeanLocal usageDetailSessionBeanLocal;
    private final CustomerSessionBeanLocal customerSessionBeanLocal;

    /**
     * Creates a new instance of UsageDetailResource
     */
    public UsageDetailResource() {

        sessionBeanLookup = new SessionBeanLookup();

        usageDetailSessionBeanLocal = sessionBeanLookup.lookupUsageDetailSessionBeanLocal();
        customerSessionBeanLocal = sessionBeanLookup.lookupCustomerSessionBeanLocal();
    }

    @Path("retrieveUsageDetail/{usageDetailId}")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveUsageDetail(@QueryParam("username") String username, @QueryParam("password") String password, @PathParam("usageDetailId") Long usageDetailId) {
        try {
            Customer customer = customerSessionBeanLocal.customerLogin(username, password);
            System.out.println("********** UsageDetailResource.retrieveUsageDetail(): Customer " + customer.getUsername() + " login remotely via web service");

            UsageDetail usageDetail = usageDetailSessionBeanLocal.retrieveUsageDetailByUsageDetailId(usageDetailId);

            usageDetail.setBill(null);
            usageDetail.setSubscription(null);

            return Response.status(Status.OK).entity(new RetrieveUsageDetailRsp(usageDetail)).build();
        } catch (InvalidLoginCredentialException ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED).entity(errorRsp).build();
        } catch (UsageDetailNotFoundException ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();
        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }
    }

    @Path("retrieveSubscriptionUsageDetails")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveSubscriptionUsageDetails(RetrieveSubscriptionUsageDetailsReq retrieveSubscriptionUsageDetailsReq) {
        if (retrieveSubscriptionUsageDetailsReq != null) {
            try {
                Customer customer = customerSessionBeanLocal.customerLogin(retrieveSubscriptionUsageDetailsReq.getUsername(), retrieveSubscriptionUsageDetailsReq.getPassword());
                System.out.println("********** UsageDetailResource.retrieveSubscriptionUsageDetails(): Customer " + customer.getUsername() + " login remotely via web service");

                List<UsageDetail> usageDetails = usageDetailSessionBeanLocal.retrieveSubscriptionUsageDetails(retrieveSubscriptionUsageDetailsReq.getSubscription());

                for (UsageDetail usageDetail : usageDetails) {
                    usageDetail.setBill(null);
                    usageDetail.setSubscription(null);
                }

                return Response.status(Status.OK).entity(new RetrieveSubscriptionUsageDetailsRsp(usageDetails)).build();
            } catch (InvalidLoginCredentialException ex) {
                ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
                return Response.status(Response.Status.UNAUTHORIZED).entity(errorRsp).build();
            } catch (Exception ex) {
                ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
            }
        } else {
            ErrorRsp errorRsp = new ErrorRsp("Invalid retrieve subscription usage details request");
            return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();
        }
    }

    @Path("retrieveSubscriptionCurrentUsageDetails")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveSubscriptionCurrentUsageDetails(RetrieveSubscriptionCurrentUsageDetailsReq retrieveSubscriptionCurrentUsageDetailsReq) {
        if (retrieveSubscriptionCurrentUsageDetailsReq != null) {
            try {
                Customer customer = customerSessionBeanLocal.customerLogin(retrieveSubscriptionCurrentUsageDetailsReq.getUsername(), retrieveSubscriptionCurrentUsageDetailsReq.getPassword());
                System.out.println("********** UsageDetailResource.retrieveSubscriptionCurrentUsageDetails(): Customer " + customer.getUsername() + " login remotely via web service");

                UsageDetail usageDetail = usageDetailSessionBeanLocal.retrieveSubscriptionCurrentUsageDetails(retrieveSubscriptionCurrentUsageDetailsReq.getSubscription());

                usageDetail.setBill(null);
                usageDetail.setSubscription(null);

                return Response.status(Status.OK).entity(new RetrieveSubscriptionCurrentUsageDetailsRsp(usageDetail)).build();
            } catch (InvalidLoginCredentialException ex) {
                ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
                return Response.status(Response.Status.UNAUTHORIZED).entity(errorRsp).build();
            } catch (Exception ex) {
                ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
            }
        } else {
            ErrorRsp errorRsp = new ErrorRsp("Invalid retrieve subscription usage details request");
            return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();
        }
    }
}
