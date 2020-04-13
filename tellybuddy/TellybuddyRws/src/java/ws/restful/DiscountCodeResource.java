package ws.restful;

import ejb.session.stateless.CustomerSessionBeanLocal;
import ejb.session.stateless.DiscountCodeSessionBeanLocal;
import entity.Customer;
import entity.DiscountCode;
import java.util.List;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import util.exception.DiscountCodeNotFoundException;
import util.exception.InvalidLoginCredentialException;
import ws.datamodel.ErrorRsp;
import ws.datamodel.RetrieveAllAvailableDiscountCodesRsp;
import ws.datamodel.RetrieveDiscountCodeRsp;

/**
 * REST Web Service
 *
 * @author tjle2
 */
@Path("DiscountCode")
public class DiscountCodeResource {

    @Context
    private UriInfo context;

    private final SessionBeanLookup sessionBeanLookup;

    private final DiscountCodeSessionBeanLocal discountCodeSessionBeanLocal;
    private final CustomerSessionBeanLocal customerSessionBeanLocal;

    public DiscountCodeResource() {

        sessionBeanLookup = new SessionBeanLookup();

        discountCodeSessionBeanLocal = sessionBeanLookup.lookupDiscountCodeSessionBeanLocal();
        customerSessionBeanLocal = sessionBeanLookup.lookupCustomerSessionBeanLocal();
    }

    /**
     * Retrieves representation of an instance of
     * ws.restful.DiscountCodeResource
     *
     * @return an instance of java.lang.String
     */
    @Path("retrieveAllUsableActiveDiscountCodes")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAllUsableActiveDiscountCodes() {

        try {
            List<DiscountCode> discountCodes = discountCodeSessionBeanLocal.retrieveAllUsableActiveDiscountCodes();

            for (DiscountCode discountCode : discountCodes) {
                discountCode.setTransaction(null);
            }

            return Response.status(Response.Status.OK).entity(new RetrieveAllAvailableDiscountCodesRsp(discountCodes)).build();
        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }
    }

    @Path("retrieveDiscountCode/{discountCodeId}")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveDiscountCode(@QueryParam("username") String username, @QueryParam("password") String password, @PathParam("discountCodeId") Long discountCodeId) {
        try {
            Customer customer = customerSessionBeanLocal.customerLogin(username, password);
            System.out.println("********** DiscountCodeResource.retrieveDiscountCode(): Customer " + customer.getUsername() + " login remotely via web service");

            DiscountCode discountCode = discountCodeSessionBeanLocal.retrieveDiscountCodeByDiscountCodeId(discountCodeId);
            discountCode.setTransaction(null);

            return Response.status(Response.Status.OK).entity(new RetrieveDiscountCodeRsp(discountCode)).build();
        } catch (InvalidLoginCredentialException ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED).entity(errorRsp).build();
        } catch (DiscountCodeNotFoundException ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();
        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }
    }
}
