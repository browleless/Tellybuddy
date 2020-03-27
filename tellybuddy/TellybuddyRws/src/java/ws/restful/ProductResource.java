package ws.restful;

import ejb.session.stateless.ProductSessionBeanLocal;
import entity.Product;
import entity.Tag;
import java.util.List;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import ws.datamodel.ErrorRsp;
import ws.datamodel.RetrieveAllProductsRsp;

/**
 * REST Web Service
 *
 * @author tjle2
 */
@Path("Product")
public class ProductResource {

    @Context
    private UriInfo context;

    private final SessionBeanLookup sessionBeanLookup;

    private final ProductSessionBeanLocal productSessionBeanLocal;

    public ProductResource() {

        sessionBeanLookup = new SessionBeanLookup();

        productSessionBeanLocal = sessionBeanLookup.lookupProductSessionBeanLocal();
    }

    @Path("retrieveAllProducts")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAllProducts() {

        try {
            List<Product> products = productSessionBeanLocal.retrieveAllProducts();

            for (Product product : products) {
                if (product.getCategory().getParentCategory() != null) {
                    product.getCategory().getParentCategory().getSubCategories().clear();
                }

                product.getCategory().getProducts().clear();

                for (Tag tag : product.getTags()) {
                    tag.getProducts().clear();
                }
            }

            return Response.status(Response.Status.OK).entity(new RetrieveAllProductsRsp(products)).build();
        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }
    }
}
