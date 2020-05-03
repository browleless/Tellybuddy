package ws.restful;

import ejb.session.stateless.CustomerSessionBeanLocal;
import ejb.session.stateless.PhoneNumberSessionBeanLocal;
import entity.PhoneNumber;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import util.exception.PhoneNumberNotFoundException;
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

    private final PhoneNumberSessionBeanLocal phoneNumberSessionBeanLocal;

    public PhoneNumberResource() {

        sessionBeanLookup = new SessionBeanLookup();

        phoneNumberSessionBeanLocal = sessionBeanLookup.lookupPhoneNumberSessionBeanLocal();
    }

    /**
     * Retrieves representation of an instance of ws.restful.PhoneNumberResource
     *
     * @return an instance of java.lang.String
     */
    @Path("retrieveAllAvaialblePhoneNumbers")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAllAvaialblePhoneNumbers() {
        try {
            List<PhoneNumber> phoneNumbers = phoneNumberSessionBeanLocal.retrieveListOfAvailablePhoneNumbers();

            for (PhoneNumber phoneNumber : phoneNumbers) {
                phoneNumber.setSubscription(null);
            }

            return Response.status(Response.Status.OK).entity(new RetrieveAllAvailablePhoneNumbersRsp(phoneNumbers)).build();
        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }
    }

    @Path("retrievePhoneNumber/{phoneNumberId}")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrievePhoneNumber(@PathParam("phoneNumberId") Long phoneNumberId) {
        try {
            PhoneNumber phoneNumber = phoneNumberSessionBeanLocal.retrievePhoneNumberByPhoneNumberId(phoneNumberId);
            phoneNumber.setSubscription(null);

            return Response.status(Response.Status.OK).entity(new RetrievePhoneNumberRsp(phoneNumber)).build();
        } catch (PhoneNumberNotFoundException ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();
        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }
    }
}
