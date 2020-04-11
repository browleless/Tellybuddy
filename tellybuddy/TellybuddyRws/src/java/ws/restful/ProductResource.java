package ws.restful;

import ejb.session.stateless.CustomerSessionBeanLocal;
import ejb.session.stateless.ProductSessionBeanLocal;
import entity.LuxuryProduct;
import entity.Product;
import entity.Tag;
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
import util.exception.ProductNotFoundException;
import ws.datamodel.ErrorRsp;
import ws.datamodel.RetrieveAllProductsRsp;
import ws.datamodel.RetrieveProductRsp;

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
    @Consumes(MediaType.TEXT_PLAIN)
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

                if (product instanceof LuxuryProduct) {
                    ((LuxuryProduct) product).getProductItems().clear();
                }
            }

            return Response.status(Response.Status.OK).entity(new RetrieveAllProductsRsp(products)).build();
        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }
    }
    
    @Path("retrieveAllNormalProducts")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAllNormalProducts() {

        try {
            List<Product> products = productSessionBeanLocal.retrieveAllNormalProducts();

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
        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }
    }
    
    @Path("retrieveAllLuxuryProducts")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAllLuxuryProducts() {

        try {
            List<Product> luxuryProducts = productSessionBeanLocal.retrieveAllLuxuryProducts();

            for (Product lp : luxuryProducts) {
                if (lp.getCategory().getParentCategory() != null) {
                    lp.getCategory().getParentCategory().getSubCategories().clear();
                }

                lp.getCategory().getProducts().clear();

                for (Tag tag : lp.getTags()) {
                    tag.getProducts().clear();
                }

                if (lp instanceof LuxuryProduct) {
                    ((LuxuryProduct) lp).getProductItems().clear();
                }
            }

            return Response.status(Response.Status.OK).entity(new RetrieveAllProductsRsp(luxuryProducts)).build();
        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }
    }
    
    @Path("retrieveAllDiscountedProducts")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAllDiscountedProducts() {

        try {
            List<Product> discounted = productSessionBeanLocal.retrieveAllDiscountedProducts();

            for (Product p : discounted) {
                if (p.getCategory().getParentCategory() != null) {
                    p.getCategory().getParentCategory().getSubCategories().clear();
                }

                p.getCategory().getProducts().clear();

                for (Tag tag : p.getTags()) {
                    tag.getProducts().clear();
                }

                if (p instanceof LuxuryProduct) {
                    ((LuxuryProduct) p).getProductItems().clear();
                }
            }

            return Response.status(Response.Status.OK).entity(new RetrieveAllProductsRsp(discounted)).build();
        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }
    }

    @Path("retrieveProduct/{productId}")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveProduct(@PathParam("productId") Long productId) {
        try {
            Product product = productSessionBeanLocal.retrieveProductByProductId(productId);

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

            return Response.status(Response.Status.OK).entity(new RetrieveProductRsp(product)).build();
        } catch (ProductNotFoundException ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();
        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }
    }
}
