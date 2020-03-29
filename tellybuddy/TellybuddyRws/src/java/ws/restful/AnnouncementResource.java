package ws.restful;

import ejb.session.stateless.AnnouncementSessionBeanLocal;
import ejb.session.stateless.CustomerSessionBeanLocal;
import entity.Announcement;
import entity.Customer;
import java.util.List;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import util.exception.InvalidLoginCredentialException;
import ws.datamodel.ErrorRsp;
import ws.datamodel.RetrieveAllActiveAnnouncementsForCustomersRsp;

/**
 * REST Web Service
 *
 * @author tjle2
 */
@Path("Announcement")
public class AnnouncementResource {

    @Context
    private UriInfo context;

    private final SessionBeanLookup sessionBeanLookup;

    private final AnnouncementSessionBeanLocal announcementSessionBeanLocal;
    private final CustomerSessionBeanLocal customerSessionBeanLocal;

    /**
     * Creates a new instance of AnnouncementResource
     */
    public AnnouncementResource() {

        sessionBeanLookup = new SessionBeanLookup();

        announcementSessionBeanLocal = sessionBeanLookup.lookupAnnouncementSessionBeanLocal();
        customerSessionBeanLocal = sessionBeanLookup.lookupCustomerSessionBeanLocal();
    }

    /**
     * Retrieves representation of an instance of
     * ws.restful.AnnouncementResource
     *
     * @return an instance of java.lang.String
     */
    @Path("retrieveAllActiveAnnouncementsForCustomers")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAllActiveAnnouncementsForCustomers(@QueryParam("username") String username, @QueryParam("password") String password) {
        try {
            Customer customer = customerSessionBeanLocal.customerLogin(username, password);
            System.out.println("********** AnnouncementResource.retrieveAllActiveAnnouncementsForCustomers(): Customer " + customer.getUsername() + " login remotely via web service");

            List<Announcement> announcements = announcementSessionBeanLocal.retrieveAllActiveAnnouncementsForCustomers();

            return Response.status(Response.Status.OK).entity(new RetrieveAllActiveAnnouncementsForCustomersRsp(announcements)).build();
        } catch (InvalidLoginCredentialException ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED).entity(errorRsp).build();
        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }
    }
}
