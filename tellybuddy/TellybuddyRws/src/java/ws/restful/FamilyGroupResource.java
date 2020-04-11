package ws.restful;

import ejb.session.stateless.CustomerSessionBeanLocal;
import ejb.session.stateless.FamilyGroupSessionBeanLocal;
import entity.Customer;
import entity.FamilyGroup;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import util.exception.CustomerAlreadyInFamilyGroupException;
import util.exception.CustomerDoesNotBelongToFamilyGroupException;
import util.exception.CustomerNotFoundException;
import util.exception.CustomerNotVerifiedException;
import util.exception.CustomersDoNotHaveSameAddressOrPostalCodeException;
import util.exception.FamilyGroupDonatedUnitsExceededLimitException;
import util.exception.FamilyGroupNotFoundException;
import util.exception.FamilyGroupReachedLimitOf5MembersException;
import util.exception.InsufficientDataUnitsToDonateToFamilyGroupException;
import util.exception.InsufficientDonatedUnitsInFamilyGroupException;
import util.exception.InsufficientSmsUnitsToDonateToFamilyGroupException;
import util.exception.InsufficientTalktimeUnitsToDonateToFamilyGroupException;
import util.exception.InvalidLoginCredentialException;
import ws.datamodel.AddFamilyGroupMemberReq;
import ws.datamodel.ConsumeUnitsFromFamilyGroupReq;
import ws.datamodel.CreateFamilyGroupReq;
import ws.datamodel.CreateFamilyGroupRsp;
import ws.datamodel.DonateUnitsToFamilyGroupReq;
import ws.datamodel.ErrorRsp;
import ws.datamodel.RemoveFamilyGroupMemberReq;
import ws.datamodel.RetrieveCustomerFamilyGroupRsp;
import ws.datamodel.UpdateFamilyGroupReq;

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

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createFamilyGroup(CreateFamilyGroupReq createFamilyGroupReq) {

        if (createFamilyGroupReq != null) {
            try {
                Customer customer = customerSessionBeanLocal.customerLogin(createFamilyGroupReq.getUsername(), createFamilyGroupReq.getPassword());
                System.out.println("********** FamilyGroup.createFamilyGroup(): Customer " + createFamilyGroupReq.getUsername() + " login remotely via web service");

                Long familyGroupId = familyGroupSessionBeanLocal.createFamilyGroup(createFamilyGroupReq.getFamilyGroup(), createFamilyGroupReq.getCustomer());

                return Response.status(Response.Status.OK).entity(new CreateFamilyGroupRsp(familyGroupId)).build();
            } catch (InvalidLoginCredentialException ex) {
                ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
                return Response.status(Response.Status.UNAUTHORIZED).entity(errorRsp).build();
            } catch (CustomerNotFoundException | CustomerNotVerifiedException | CustomerAlreadyInFamilyGroupException ex) {
                ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
                return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();
            } catch (Exception ex) {
                ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
            }
        } else {
            ErrorRsp errorRsp = new ErrorRsp("Invalid create new family group request");
            return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();
        }
    }

    @Path("addFamilyGroupMember")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addFamilyGroupMember(AddFamilyGroupMemberReq addFamilyGroupMemberReq) {

        if (addFamilyGroupMemberReq != null) {
            try {
                Customer customer = customerSessionBeanLocal.customerLogin(addFamilyGroupMemberReq.getUsername(), addFamilyGroupMemberReq.getPassword());
                System.out.println("********** FamilyGroup.addFamilyGroupMember(): Customer " + addFamilyGroupMemberReq.getUsername() + " login remotely via web service");

                familyGroupSessionBeanLocal.addFamilyMember(addFamilyGroupMemberReq.getCustomer(), addFamilyGroupMemberReq.getFamilyGroup());

                return Response.status(Response.Status.OK).build();
            } catch (InvalidLoginCredentialException ex) {
                ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
                return Response.status(Response.Status.UNAUTHORIZED).entity(errorRsp).build();
            } catch (CustomersDoNotHaveSameAddressOrPostalCodeException | FamilyGroupReachedLimitOf5MembersException | CustomerAlreadyInFamilyGroupException | CustomerNotVerifiedException ex) {
                ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
                return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();
            } catch (Exception ex) {
                ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
            }
        } else {
            ErrorRsp errorRsp = new ErrorRsp("Invalid add family group member request");
            return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();
        }
    }

    @Path("removeFamilyGroupMember")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeFamilyGroupMember(RemoveFamilyGroupMemberReq removeFamilyGroupMemberReq) {

        if (removeFamilyGroupMemberReq != null) {
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
        } else {
            ErrorRsp errorRsp = new ErrorRsp("Invalid remove family group member request");
            return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();
        }
    }

    @Path("donateUnitsToFamilyGroup")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response donateUnitsToFamilyGroup(DonateUnitsToFamilyGroupReq donateUnitsToFamilyGroupReq) {

        if (donateUnitsToFamilyGroupReq != null) {
            try {
                Customer customer = customerSessionBeanLocal.customerLogin(donateUnitsToFamilyGroupReq.getUsername(), donateUnitsToFamilyGroupReq.getPassword());
                System.out.println("********** FamilyGroup.donateUnitsToFamilyGroup(): Customer " + donateUnitsToFamilyGroupReq.getUsername() + " login remotely via web service");

                familyGroupSessionBeanLocal.donateUnits(donateUnitsToFamilyGroupReq.getCustomer(), donateUnitsToFamilyGroupReq.getSubscription(), donateUnitsToFamilyGroupReq.getFamilyGroup(), donateUnitsToFamilyGroupReq.getSmsUnits(), donateUnitsToFamilyGroupReq.getDataUnits(), donateUnitsToFamilyGroupReq.getTalktimeUnits());

                return Response.status(Response.Status.OK).build();
            } catch (InvalidLoginCredentialException ex) {
                ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
                return Response.status(Response.Status.UNAUTHORIZED).entity(errorRsp).build();
            } catch (CustomerDoesNotBelongToFamilyGroupException | FamilyGroupDonatedUnitsExceededLimitException | InsufficientDataUnitsToDonateToFamilyGroupException | InsufficientSmsUnitsToDonateToFamilyGroupException | InsufficientTalktimeUnitsToDonateToFamilyGroupException ex) {
                ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
                return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();
            } catch (Exception ex) {
                ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
            }
        } else {
            ErrorRsp errorRsp = new ErrorRsp("Invalid donate units to family group request");
            return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();
        }
    }

    @Path("consumeUnitsFromFamilyGroup")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response consumeUnitsFromFamilyGroup(ConsumeUnitsFromFamilyGroupReq consumeUnitsFromFamilyGroupReq) {

        if (consumeUnitsFromFamilyGroupReq != null) {
            try {
                Customer customer = customerSessionBeanLocal.customerLogin(consumeUnitsFromFamilyGroupReq.getUsername(), consumeUnitsFromFamilyGroupReq.getPassword());
                System.out.println("********** FamilyGroup.consumeUnitsFromFamilyGroup(): Customer " + consumeUnitsFromFamilyGroupReq.getUsername() + " login remotely via web service");

                familyGroupSessionBeanLocal.useUnits(consumeUnitsFromFamilyGroupReq.getCustomer(), consumeUnitsFromFamilyGroupReq.getSubscription(), consumeUnitsFromFamilyGroupReq.getFamilyGroup(), consumeUnitsFromFamilyGroupReq.getSmsUnits(), consumeUnitsFromFamilyGroupReq.getDataUnits(), consumeUnitsFromFamilyGroupReq.getTalktimeUnits());

                return Response.status(Response.Status.OK).build();
            } catch (InvalidLoginCredentialException ex) {
                ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
                return Response.status(Response.Status.UNAUTHORIZED).entity(errorRsp).build();
            } catch (CustomerDoesNotBelongToFamilyGroupException | InsufficientDonatedUnitsInFamilyGroupException ex) {
                ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
                return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();
            } catch (Exception ex) {
                ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
            }
        } else {
            ErrorRsp errorRsp = new ErrorRsp("Invalid consume units from family group request");
            return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();
        }
    }
    
    @Path("updateFamilyGroup")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateFamilyGroup(UpdateFamilyGroupReq updateFamilyGroupReq) {

        if (updateFamilyGroupReq != null) {
            try {
                Customer customer = customerSessionBeanLocal.customerLogin(updateFamilyGroupReq.getUsername(), updateFamilyGroupReq.getPassword());
                System.out.println("********** FamilyGroup.updateFamilyGroup(): Customer " + updateFamilyGroupReq.getUsername() + " login remotely via web service");

                familyGroupSessionBeanLocal.updateFamilyPlan(updateFamilyGroupReq.getFamilyGroup());

                return Response.status(Response.Status.OK).build();
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
        } else {
            ErrorRsp errorRsp = new ErrorRsp("Invalid request for update of family group");
            return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();
        }
    }

    @Path("{familyGroupId}")
    @DELETE
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteFamilyGroup(@QueryParam("username") String username, @QueryParam("password") String password, @PathParam("familyGroupId") Long familyGroupId) {
        try {
            Customer customer = customerSessionBeanLocal.customerLogin(username, password);
            System.out.println("********** FamilyGroup.deleteFamilyGroup(): Customer " + customer.getUsername() + " login remotely via web service");

            familyGroupSessionBeanLocal.deleteFamilyGroup(familyGroupId);

            return Response.status(Status.OK).build();
        } catch (InvalidLoginCredentialException ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Status.UNAUTHORIZED).entity(errorRsp).build();
        } catch (FamilyGroupNotFoundException ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Status.BAD_REQUEST).entity(errorRsp).build();
        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }
    }
}
