package ws.restful;

import ejb.session.stateless.CustomerSessionBeanLocal;
import ejb.session.stateless.TagSessionBeanLocal;
import entity.Tag;
import java.util.List;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import ws.datamodel.ErrorRsp;
import ws.datamodel.RetrieveAllTagsRsp;

/**
 * REST Web Service
 *
 * @author tjle2
 */
@Path("Tag")
public class TagResource {

    @Context
    private UriInfo context;

    private final SessionBeanLookup sessionBeanLookup;

    private final TagSessionBeanLocal tagSessionBeanLocal;

    /**
     * Creates a new instance of TagResource
     */
    public TagResource() {

        sessionBeanLookup = new SessionBeanLookup();

        tagSessionBeanLocal = sessionBeanLookup.lookupTagSessionBeanLocal();
    }

    /**
     * Retrieves representation of an instance of ws.restful.TagResource
     *
     * @return an instance of java.lang.String
     */
    @Path("retrieveAllTags")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAllTags() {
        try {
            List<Tag> tags = tagSessionBeanLocal.retrieveAllTags();

            for (Tag tag : tags) {
                tag.getProducts().clear();
            }

            return Response.status(Status.OK).entity(new RetrieveAllTagsRsp(tags)).build();
        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }
    }
}
