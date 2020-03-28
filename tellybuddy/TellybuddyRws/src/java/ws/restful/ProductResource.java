package ws.restful;

import ejb.session.stateless.CustomerSessionBeanLocal;
import ejb.session.stateless.ProductSessionBeanLocal;
import entity.Customer;
import entity.LuxuryProduct;
import entity.Product;
import entity.Tag;
import java.util.List;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import util.exception.InvalidLoginCredentialException;
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
    private final CustomerSessionBeanLocal customerSessionBeanLocal;

    public ProductResource() {

        sessionBeanLookup = new SessionBeanLookup();

        productSessionBeanLocal = sessionBeanLookup.lookupProductSessionBeanLocal();
        customerSessionBeanLocal = sessionBeanLookup.lookupCustomerSessionBeanLocal();
    }

    @Path("retrieveAllProducts")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAllProducts(@QueryParam("username") String username, @QueryParam("password") String password) {

        try {
            Customer customer = customerSessionBeanLocal.customerLogin(username, password);
            System.out.println("********** ProductResource.retrieveAllProducts(): Customer " + customer.getUsername() + " login remotely via web service");

            List<Product> products = productSessionBeanLocal.retrieveAllProducts();

            for (Product product : products) {
                if (product.getCategory().getParentCategory() != null) {
                    product.getCategory().getParentCategory().getSubCategories().clear();
                }

                product.getCategory().getProducts().clear();

                for (Tag tag : product.getTags()) {
                    tag.getProducts().clear();
                }

                if (product instanceof LuxuryProduct) {
                    ((LuxuryProduct) product).getProductItems().clear();
                }
            }

            return Response.status(Response.Status.OK).entity(new RetrieveAllProductsRsp(products)).build();
        } catch (InvalidLoginCredentialException ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED).entity(errorRsp).build();
        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }
    }
}
