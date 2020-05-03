package ws.restful;

import ejb.session.stateless.CustomerSessionBeanLocal;
import ejb.session.stateless.ProductSessionBeanLocal;
import entity.LuxuryProduct;
import entity.Product;
import entity.Tag;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import util.exception.InvalidLoginCredentialException;
import util.exception.ProductNotFoundException;
import ws.datamodel.ErrorRsp;
import ws.datamodel.RetrieveAllProductsRsp;
import ws.datamodel.RetrieveProductRsp;
import ws.datamodel.RetrieveProductsByMultipleCategoriesReq;
import ws.datamodel.RetrieveProductsByTagsReq;

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
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAllProducts() {

        try {
            List<Product> products = productSessionBeanLocal.retrieveAllProducts();

            for (Product product : products) {

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

    @Path("searchProductsByName")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchProductsByName(@QueryParam("searchString") String searchString) {
        try {
            List<Product> products = productSessionBeanLocal.searchProductsByName(searchString);

            for (Product p : products) {

                p.getCategory().getProducts().clear();

                for (Tag tag : p.getTags()) {
                    tag.getProducts().clear();
                }

                if (p instanceof LuxuryProduct) {
                    ((LuxuryProduct) p).getProductItems().clear();
                }
            }
            return Response.status(Response.Status.OK).entity(new RetrieveAllProductsRsp(products)).build();
        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }
    }

    @Path("filterProductsByCategory/{categoryId}")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response filterProductsByCategory(@PathParam("categoryId") Long categoryId) {
        try {
            List<Product> products = productSessionBeanLocal.filterProductsByCategory(categoryId);

            for (Product p : products) {

                p.getCategory().getProducts().clear();

                for (Tag tag : p.getTags()) {
                    tag.getProducts().clear();
                }

                if (p instanceof LuxuryProduct) {
                    ((LuxuryProduct) p).getProductItems().clear();
                }
            }
            System.out.println("restful: " + products.size());
            return Response.status(Response.Status.OK).entity(new RetrieveAllProductsRsp(products)).build();
        } catch (Exception ex) {
            ex.printStackTrace();
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }
    }

    @Path("filterProductsByMultipleCategories")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response filterProductsByMultipleCategories(RetrieveProductsByMultipleCategoriesReq retrieveProductsByMultipleCategoriesReq) {
        if (retrieveProductsByMultipleCategoriesReq != null) {
            System.out.println("entered here: filterProductsByMultipleCategories RESTFUL METHOD");
            try {
                List<Product> products = productSessionBeanLocal.filterProductsByMultipleCategories(retrieveProductsByMultipleCategoriesReq.getCategoryIds());
                System.out.println("{Category} num of products retrieved: " + products.size());

                for (Product p : products) {

                    p.getCategory().getProducts().clear();

                    for (Tag tag : p.getTags()) {
                        tag.getProducts().clear();
                    }

                    if (p instanceof LuxuryProduct) {
                        ((LuxuryProduct) p).getProductItems().clear();
                    }
                }
                return Response.status(Response.Status.OK).entity(new RetrieveAllProductsRsp(products)).build();
            } catch (Exception ex) {
                ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
            }
        } else {
            ErrorRsp errorRsp = new ErrorRsp("Invalid retrieve products by tags request");
            return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();
        }

    }

    @Path("filterProductsByTags")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response filterProductsByTags(RetrieveProductsByTagsReq retrieveProductsByTagsReq) {

        if (retrieveProductsByTagsReq != null) {
            try {
                List<Product> products = productSessionBeanLocal.filterProductsByTags(retrieveProductsByTagsReq.getTagIds(), retrieveProductsByTagsReq.getCondition());
                System.out.println("{TAG} num of products retrieved: " + products.size());

                for (Product p : products) {

                    p.getCategory().getProducts().clear();

                    for (Tag tag : p.getTags()) {
                        tag.getProducts().clear();
                    }

                    if (p instanceof LuxuryProduct) {
                        ((LuxuryProduct) p).getProductItems().clear();
                    }
                }
                return Response.status(Response.Status.OK).entity(new RetrieveAllProductsRsp(products)).build();
            } catch (Exception ex) {
                ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
            }
        } else {
            ErrorRsp errorRsp = new ErrorRsp("Invalid retrieve products by tags request");
            return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();
        }

    }

    @Path("retrieveAllDiscountedNormalProducts")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAllDiscountedNormalProducts() {

        try {
            List<Product> discounted = productSessionBeanLocal.retrieveAllDiscountedProducts();
            List<Product> discountedNormal = new ArrayList<>();

            for (Product p : discounted) {

                p.getCategory().getProducts().clear();

                for (Tag tag : p.getTags()) {
                    tag.getProducts().clear();
                }

                if (p instanceof LuxuryProduct) {
                    ((LuxuryProduct) p).getProductItems().clear();
                } else {
                    discountedNormal.add(p);
                }
            }

            return Response.status(Response.Status.OK).entity(new RetrieveAllProductsRsp(discountedNormal)).build();
        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }
    }

    @Path("retrieveAllDiscountedLuxuryProducts")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAllDiscountedLuxuryProducts() {

        try {
            List<Product> discounted = productSessionBeanLocal.retrieveAllDiscountedProducts();
            List<Product> discountedLuxury = new ArrayList<>();

            for (Product p : discounted) {

                p.getCategory().getProducts().clear();

                for (Tag tag : p.getTags()) {
                    tag.getProducts().clear();
                }

                if (p instanceof LuxuryProduct) {
                    ((LuxuryProduct) p).getProductItems().clear();
                    discountedLuxury.add(p);
                }
            }
            System.out.println("size " + discountedLuxury.size());
            System.out.println("name " + discountedLuxury.get(0).getName());

            return Response.status(Response.Status.OK).entity(new RetrieveAllProductsRsp(discountedLuxury)).build();
        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }
    }

    @GET
    @Path("retrieveProductImage/{productId}")
    @Produces("image/jpg")
    public Response retrieveProductImage(@PathParam("productId") Long productId) throws SQLException {
        try {
            Product product = productSessionBeanLocal.retrieveProductByProductId(productId);
            File file = new File(ProductResource.class.getProtectionDomain().getCodeSource().getLocation().getFile().substring(1, ProductResource.class.getProtectionDomain().getCodeSource().getLocation().getFile().indexOf("/dist")).replace("/", "\\") + "\\tellybuddy-war\\web\\management\\products\\productImages\\" + product.getProductImagePath());
            System.out.println("********** ProductResource.retrieveProductImage() for: " + product.getName());
            System.out.println(file.getAbsolutePath());
            return Response.ok(file, "image/jpg").build();
        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }
    }
}
