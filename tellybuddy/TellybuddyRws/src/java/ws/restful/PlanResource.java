package ws.restful;

import ejb.session.stateless.CustomerSessionBeanLocal;
import ejb.session.stateless.PlanSessionBeanLocal;
import entity.Plan;
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
import util.exception.PlanNotFoundException;
import ws.datamodel.ErrorRsp;
import ws.datamodel.RetrieveAllActiveFlashPlansRsp;
import ws.datamodel.RetrieveAllPlansRsp;
import ws.datamodel.RetrievePlanRsp;

/**
 * REST Web Service
 *
 * @author tjle2
 */
@Path("Plan")
public class PlanResource {

    @Context
    private UriInfo context;

    private final SessionBeanLookup sessionBeanLookup;

    private final PlanSessionBeanLocal planSessionBeanLocal;

    /**
     * Creates a new instance of PlanResource
     */
    public PlanResource() {

        sessionBeanLookup = new SessionBeanLookup();

        planSessionBeanLocal = sessionBeanLookup.lookupPlanSessionBeanLocal();
    }

    /**
     * Retrieves representation of an instance of ws.restful.PlanResource
     *
     * @return an instance of java.lang.String
     */
    @Path("retrieveAllPlans")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAllPlans() {
        try {
            List<Plan> plans = planSessionBeanLocal.retrieveAllPlans();

            return Response.status(Response.Status.OK).entity(new RetrieveAllPlansRsp(plans)).build();
        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }
    }

    @Path("retrieveAllActiveFlashPlans")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAllActiveFlashPlans() {
        try {
            List<Plan> plans = planSessionBeanLocal.retrieveAllActiveFlashPlans();

            return Response.status(Response.Status.OK).entity(new RetrieveAllActiveFlashPlansRsp(plans)).build();
        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }
    }

    @Path("retrieveAllNormalPlans")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAllNormalPlans() {
        try {
            List<Plan> plans = planSessionBeanLocal.retrieveAllNormalPlans();

            return Response.status(Response.Status.OK).entity(new RetrieveAllActiveFlashPlansRsp(plans)).build();
        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }
    }

    @Path("retrievePlan/{planId}")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrievePlan(@PathParam("planId") Long planId) {
        try {
            Plan plan = planSessionBeanLocal.retrievePlanByPlanId(planId);

            return Response.status(Response.Status.OK).entity(new RetrievePlanRsp(plan)).build();
        } catch (PlanNotFoundException ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();
        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }
    }
}
