package ws.restful;

import ejb.session.stateless.CustomerSessionBeanLocal;
import ejb.session.stateless.FamilyGroupSessionBeanLocal;
import entity.Customer;
import entity.FamilyGroup;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import util.exception.CustomerDoesNotBelongToFamilyGroupException;
import util.exception.CustomersDoNotHaveSameAddressOrPostalCodeException;
import util.exception.FamilyGroupNotFoundException;
import util.exception.FamilyGroupReachedLimitOf5MembersException;
import util.exception.InvalidLoginCredentialException;
import ws.datamodel.AddFamilyGroupMemberReq;
import ws.datamodel.ErrorRsp;
import ws.datamodel.RemoveFamilyGroupMemberReq;
import ws.datamodel.RetrieveCustomerFamilyGroupRsp;

/**
 * REST Web Service
 *
 * @author tjle2
 */
@Path("FamilyGroup")
public class FamilyGroupResource {

    @Context
    private UriInfo context;

    private final SessionBeanLookup sessionBeanLookup;

    private final CustomerSessionBeanLocal customerSessionBeanLocal;
    private final FamilyGroupSessionBeanLocal familyGroupSessionBeanLocal;

    public FamilyGroupResource() {

        sessionBeanLookup = new SessionBeanLookup();

        customerSessionBeanLocal = sessionBeanLookup.lookupCustomerSessionBeanLocal();
        familyGroupSessionBeanLocal = sessionBeanLookup.lookupFamilyGroupSessionBeanLocal();
    }

    /**
     * Retrieves representation of an instance of ws.restful.FamilyGroupResource
     *
     * @return an instance of java.lang.String
     */
    @Path("retrieveCustomerFamilyGroup")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveCustomerFamilyGroup(@QueryParam("username") String username, @QueryParam("password") String password) {
        try {
            Customer customer = customerSessionBeanLocal.customerLogin(username, password);
            System.out.println("********** FamilyGroup.retrieveCustomerFamilyGroup(): Customer " + customer.getUsername() + " login remotely via web service");

            FamilyGroup familyGroup = familyGroupSessionBeanLocal.retrieveFamilyGroupByCustomer(customer);

            for (Customer familyGroupMember : familyGroup.getCustomers()) {
                familyGroupMember.setFamilyGroup(null);
                familyGroupMember.getTransactions().clear();
                familyGroupMember.getBills().clear();
                familyGroupMember.getSubscriptions().clear();
                familyGroupMember.getQuizAttempts().clear();
            }

            return Response.status(Response.Status.OK).entity(new RetrieveCustomerFamilyGroupRsp(familyGroup)).build();
        } catch (InvalidLoginCredentialException ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED).entity(errorRsp).build();
        } catch (FamilyGroupNotFoundException ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();
        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }
    }

    @Path("addFamilyGroupMember")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveCustomerFamilyGroup(AddFamilyGroupMemberReq addFamilyGroupMemberReq) {

        try {
            Customer customer = customerSessionBeanLocal.customerLogin(addFamilyGroupMemberReq.getUsername(), addFamilyGroupMemberReq.getPassword());
            System.out.println("********** FamilyGroup.addFamilyGroupMember(): Customer " + addFamilyGroupMemberReq.getUsername() + " login remotely via web service");

            familyGroupSessionBeanLocal.addFamilyMember(addFamilyGroupMemberReq.getCustomer(), addFamilyGroupMemberReq.getFamilyGroup());

            return Response.status(Response.Status.OK).build();
        } catch (InvalidLoginCredentialException ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED).entity(errorRsp).build();
        } catch (CustomersDoNotHaveSameAddressOrPostalCodeException | FamilyGroupReachedLimitOf5MembersException ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();
        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }
    }

    @Path("removeFamilyGroupMember")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeFamilyGroupMember(RemoveFamilyGroupMemberReq removeFamilyGroupMemberReq) {

        try {
            Customer customer = customerSessionBeanLocal.customerLogin(removeFamilyGroupMemberReq.getUsername(), removeFamilyGroupMemberReq.getPassword());
            System.out.println("********** FamilyGroup.removeFamilyGroupMember(): Customer " + removeFamilyGroupMemberReq.getUsername() + " login remotely via web service");

            familyGroupSessionBeanLocal.removeFamilyMember(removeFamilyGroupMemberReq.getCustomer(), removeFamilyGroupMemberReq.getFamilyGroup());

            return Response.status(Response.Status.OK).build();
        } catch (InvalidLoginCredentialException ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED).entity(errorRsp).build();
        } catch (CustomerDoesNotBelongToFamilyGroupException | FamilyGroupNotFoundException ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();
        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }
    }
}
