package ws.restful;

import ejb.session.stateless.CustomerSessionBeanLocal;
import ejb.session.stateless.EmailSessionBeanLocal;
import entity.Customer;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import util.exception.CustomerExistException;
import util.exception.CustomerNotFoundException;
import util.exception.InvalidLoginCredentialException;
import ws.datamodel.ChangePasswordReq;
import ws.datamodel.ChangePasswordRsp;
import ws.datamodel.CreateCustomerReq;
import ws.datamodel.CreateCustomerRsp;
import ws.datamodel.CustomerLoginRsp;
import ws.datamodel.ErrorRsp;
import ws.datamodel.RetrieveCurrentCustomerRsp;
import ws.datamodel.RetrieveCustomerFromFamilyGroupIdRsp;
import ws.datamodel.RetrieveCustomerRsp;
import ws.datamodel.UpdateCustomerDetailsForCustomerReq;

/**
 * REST Web Service
 *
 * @author tjle2
 */
@Path("Customer")
public class CustomerResource {

    @Context
    private UriInfo context;

    private final SessionBeanLookup sessionBeanLookup;

    private final CustomerSessionBeanLocal customerSessionBeanLocal;
    private final EmailSessionBeanLocal emailSessionBeanLocal;

    public CustomerResource() {

        sessionBeanLookup = new SessionBeanLookup();

        customerSessionBeanLocal = sessionBeanLookup.lookupCustomerSessionBeanLocal();
        emailSessionBeanLocal = sessionBeanLookup.lookupEmailSessionBeanLocal();
    }

    @Path("customerLogin")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response customerLogin(@QueryParam("username") String username, @QueryParam("password") String password) {

        try {

            Customer customer = customerSessionBeanLocal.customerLogin(username, password);
            System.out.println("********** CustomerResource.updateCustomerDetailsForCustomer(): Customer " + customer.getUsername() + " login remotely via web service");
            customer.setPassword(null);
            customer.setSalt(null);

            customer.getBills().clear();
            customer.getQuizAttempts().clear();
            customer.getSubscriptions().clear();
            customer.getTransactions().clear();
            customer.getAnnouncements().clear();
            customer.setFamilyGroup(null);

            return Response.status(Response.Status.OK).entity(new CustomerLoginRsp(customer)).build();

        } catch (InvalidLoginCredentialException ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED).entity(errorRsp).build();
        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }
    }

    @Path("retrieveCustomerBySalt/{salt}")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveCustomerBySalt(@PathParam("salt") String salt) {

        try {
            Customer customer = customerSessionBeanLocal.retrieveCustomerBySalt(salt);

            customer.setPassword(null);
            customer.setSalt(null);

            customer.getBills().clear();
            customer.getQuizAttempts().clear();
            customer.getSubscriptions().clear();
            customer.getTransactions().clear();
            // customer.getAnnouncements().clear();
            customer.setFamilyGroup(null);

            return Response.status(Response.Status.OK).entity(new RetrieveCustomerRsp(customer)).build();

        } catch (CustomerNotFoundException ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED).entity(errorRsp).build();
        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }
    }

    @Path("retrieveCustomerByEmail")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveCustomerByEmail(@QueryParam("email") String email) {

        try {
            Customer customer = customerSessionBeanLocal.retrieveCustomerByEmail(email);

            customer.setPassword(null);
            customer.setSalt(null);

            customer.getBills().clear();
            customer.getQuizAttempts().clear();
            customer.getSubscriptions().clear();
            customer.getTransactions().clear();
            // customer.getAnnouncements().clear();
            customer.setFamilyGroup(null);

            return Response.status(Response.Status.OK).entity(new RetrieveCustomerRsp(customer)).build();

        } catch (CustomerNotFoundException ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED).entity(errorRsp).build();
        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }
    }

    @Path("resetPasswordRequest")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response emailResetPasswordLink(@QueryParam("email") String email) {

        try {
            emailSessionBeanLocal.emailCustomerResetPasswordLink("tellybuddy3106@gmail.com", email);

            return Response.status(Response.Status.OK).build();
        } catch (CustomerNotFoundException | InterruptedException ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();
        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createNewCustomer(CreateCustomerReq createCustomerReq) {

        if (createCustomerReq != null) {
            try {
                Long customerId = customerSessionBeanLocal.createCustomer(createCustomerReq.getCustomer());
                CreateCustomerRsp createCustomerRsp = new CreateCustomerRsp(customerId);

                return Response.status(Response.Status.OK).entity(createCustomerRsp).build();
            } catch (CustomerExistException ex) {
                ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
                return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();
            } catch (Exception ex) {
                ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
            }
        } else {
            ErrorRsp errorRsp = new ErrorRsp("Invalid create new customer request");
            return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();
        }
    }

    @Path("changePassword")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response changePassword(ChangePasswordReq changePasswordReq) {

        if (changePasswordReq != null) {
            try {
                customerSessionBeanLocal.updateCustomerPassword(changePasswordReq.getCustomer());

                return Response.status(Response.Status.OK).entity(new ChangePasswordRsp("Password has been successfully reset! Login now!")).build();
            } catch (CustomerNotFoundException ex) {
                ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
                return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();
            } catch (Exception ex) {
                ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
            }
        } else {
            ErrorRsp errorRsp = new ErrorRsp("Invalid create new customer request");
            return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();
        }
    }

    @Path("retrieveCustomerFromFamilyGroupId/{familyGroupId}")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveCustomerFromFamilyGroupId(@QueryParam("username") String username, @QueryParam("password") String password, @QueryParam("familyGroupId") Long familyGroupId) {
        try {
            Customer customer = customerSessionBeanLocal.customerLogin(username, password);
            System.out.println("********** CustomerResource.retrieveCustomerFromFamilyGroupId(): Customer " + customer.getUsername() + " login remotely via web service");
            List<Customer> customers = customerSessionBeanLocal.retrieveCustomerFromFamilyGroupId(familyGroupId);
            for (Customer c : customers) {
                c.setPassword(null);
                c.setSalt(null);
                c.getSubscriptions().clear();
                c.getBills().clear();
                c.getTransactions().clear();
                c.getQuizAttempts().clear();
                c.setFamilyGroup(null);
            }
            return Response.status(Response.Status.OK).entity(new RetrieveCustomerFromFamilyGroupIdRsp(customers)).build();
        } catch (InvalidLoginCredentialException ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED).entity(errorRsp).build();
        }catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }
    }
    
    @Path("retrieveCurrentCustomer")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveCurrentCustomer(@QueryParam("username")String username, @QueryParam("password") String password){
        try{
            Customer customer = customerSessionBeanLocal.customerLogin(username, password);
            System.out.println("********** CustomerResource.retrieveCustomerByUsername(): Customer " + customer.getUsername() + " login remotely via web service");
            customer.setPassword(null);
            customer.setSalt(null);
            customer.getBills().clear();
            customer.getQuizAttempts().clear();
            customer.getSubscriptions().clear();
            customer.getTransactions().clear();
            customer.setFamilyGroup(null);
            return Response.status(Response.Status.OK).entity(new RetrieveCurrentCustomerRsp(customer)).build();
        }catch(InvalidLoginCredentialException ex){
           ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED).entity(errorRsp).build(); 
        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }
    }

    @Path("updateCustomerDetailsForCustomer")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response UpdateCustomerDetailsForCustomer(UpdateCustomerDetailsForCustomerReq updateCustomerDetailsForCustomerReq) {
        if (updateCustomerDetailsForCustomerReq != null) {
            try {
                Customer customer = customerSessionBeanLocal.customerLogin(updateCustomerDetailsForCustomerReq.getUsername(), updateCustomerDetailsForCustomerReq.getPassword());
                System.out.println("********** CustomerResource.updateCustomerDetailsForCustomer(): Customer " + customer.getUsername() + " login remotely via web service");
                customerSessionBeanLocal.updateCustomerDetailsForCustomer(updateCustomerDetailsForCustomerReq.getCustomer());

                return Response.status(Response.Status.OK).build();
            } catch (InvalidLoginCredentialException ex) {
                ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
                return Response.status(Response.Status.UNAUTHORIZED).entity(errorRsp).build();
            } catch (CustomerNotFoundException ex) {
                ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
            }
        } else {
            ErrorRsp errorRsp = new ErrorRsp("Invalid update customer request");
            return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();
        }
    }

}
