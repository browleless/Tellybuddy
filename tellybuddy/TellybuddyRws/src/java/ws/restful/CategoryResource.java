package ws.restful;

import ejb.session.stateless.CategorySessionBeanLocal;
import ejb.session.stateless.CustomerSessionBeanLocal;
import entity.Category;
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
import ws.datamodel.RetrieveAllCategoriesRsp;

/**
 * REST Web Service
 *
 * @author tjle2
 */
@Path("Category")
public class CategoryResource {

    @Context
    private UriInfo context;

    private final SessionBeanLookup sessionBeanLookup;

    private final CategorySessionBeanLocal categorySessionBeanLocal;
    private final CustomerSessionBeanLocal customerSessionBeanLocal;

    /**
     * Creates a new instance of CategoryResource
     */
    public CategoryResource() {

        sessionBeanLookup = new SessionBeanLookup();

        categorySessionBeanLocal = sessionBeanLookup.lookupCategorySessionBeanLocal();
        customerSessionBeanLocal = sessionBeanLookup.lookupCustomerSessionBeanLocal();
    }

    /**
     * Retrieves representation of an instance of ws.restful.CategoryResource
     *
     * @return an instance of java.lang.String
     */
    @Path("retrieveAllCategories")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAllCategories() {
        try {
            List<Category> categories = categorySessionBeanLocal.retrieveAllCategories();

            for (Category category : categories) {
                category.getProducts().clear();
            }

            return Response.status(Status.OK).entity(new RetrieveAllCategoriesRsp(categories)).build();
        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }
    }
}
