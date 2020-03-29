package ws.restful;

import ejb.session.stateless.CustomerSessionBeanLocal;
import ejb.session.stateless.PhoneNumberSessionBeanLocal;
import entity.Customer;
import entity.PhoneNumber;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import util.exception.InvalidLoginCredentialException;
import ws.datamodel.ErrorRsp;
import ws.datamodel.RetrieveAllAvailablePhoneNumbersRsp;
import ws.datamodel.RetrievePhoneNumberRsp;

/**
 * REST Web Service
 *
 * @author tjle2
 */
@Path("PhoneNumber")
public class PhoneNumberResource {

    @Context
    private UriInfo context;

    private final SessionBeanLookup sessionBeanLookup;

    private final CustomerSessionBeanLocal customerSessionBeanLocal;
    private final PhoneNumberSessionBeanLocal phoneNumberSessionBeanLocal;

    public PhoneNumberResource() {

        sessionBeanLookup = new SessionBeanLookup();

        customerSessionBeanLocal = sessionBeanLookup.lookupCustomerSessionBeanLocal();
        phoneNumberSessionBeanLocal = sessionBeanLookup.lookupPhoneNumberSessionBeanLocal();
    }

    /**
     * Retrieves representation of an instance of ws.restful.PhoneNumberResource
     *
     * @return an instance of java.lang.String
     */
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAllAvaialblePhoneNumbers(@QueryParam("username") String username, @QueryParam("password") String password) {
        try {
            Customer customer = customerSessionBeanLocal.customerLogin(username, password);
            System.out.println("********** PhoneNumberResource.retrieveAllAvaialblePhoneNumbers(): Customer " + customer.getUsername() + " login remotely via web service");

            List<PhoneNumber> phoneNumbers = phoneNumberSessionBeanLocal.retrieveListOfAvailablePhoneNumbers();
            
            for (PhoneNumber phoneNumber : phoneNumbers) {
                phoneNumber.setSubscription(null);
            }

            return Response.status(Response.Status.OK).entity(new RetrieveAllAvailablePhoneNumbersRsp(phoneNumbers)).build();
        } catch (InvalidLoginCredentialException ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED).entity(errorRsp).build();
        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }
    }
    
    @Path("retrievePhoneNumber/{phoneNumberId}")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrievePhoneNumber(@QueryParam("username") String username, @QueryParam("password") String password, @PathParam("phoneNumberId") Long phoneNumberId) {
        try {
            Customer customer = customerSessionBeanLocal.customerLogin(username, password);
            System.out.println("********** PhoneNumberResource.retrievePhoneNumber(): Customer " + customer.getUsername() + " login remotely via web service");

            PhoneNumber phoneNumber = phoneNumberSessionBeanLocal.retrievePhoneNumberByPhoneNumberId(phoneNumberId);
            phoneNumber.setSubscription(null);

            return Response.status(Response.Status.OK).entity(new RetrievePhoneNumberRsp(phoneNumber)).build();
        } catch (InvalidLoginCredentialException ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED).entity(errorRsp).build();
        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }
    }
}
