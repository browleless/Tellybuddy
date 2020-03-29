package ws.restful;

import ejb.session.stateless.AnswerSessionBeanLocal;
import ejb.session.stateless.CustomerSessionBeanLocal;
import entity.Answer;
import entity.Customer;
import java.util.List;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import util.exception.InvalidLoginCredentialException;
import ws.datamodel.ErrorRsp;
import ws.datamodel.RetrieveAnswersByQuestionIdRsp;

/**
 * REST Web Service
 *
 * @author tjle2
 */
@Path("Answer")
public class AnswerResource {

    @Context
    private UriInfo context;

    private final SessionBeanLookup sessionBeanLookup;

    private final CustomerSessionBeanLocal customerSessionBeanLocal;
    private final AnswerSessionBeanLocal answerSessionBeanLocal;

    /**
     * Creates a new instance of AnswerResource
     */
    public AnswerResource() {

        sessionBeanLookup = new SessionBeanLookup();

        customerSessionBeanLocal = sessionBeanLookup.lookupCustomerSessionBeanLocal();
        answerSessionBeanLocal = sessionBeanLookup.lookupAnswerSessionBeanLocal();
    }

    /**
     * Retrieves representation of an instance of ws.restful.AnswerResource
     *
     * @return an instance of java.lang.String
     */
    @Path("retrieveAnswersByQuestionId/{questionId}")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAnswersByQuestionId(@QueryParam("username") String username, @QueryParam("password") String password, @PathParam("questionId") Long questionId) {

        try {
            Customer customer = customerSessionBeanLocal.customerLogin(username, password);
            System.out.println("********** AnswerResource.retrieveAnswersByQuestionId(): Customer " + username + " login remotely via web service");

            List<Answer> answers = answerSessionBeanLocal.retrieveAnswersByQuestionId(questionId);

            for (Answer answer : answers) {
                answer.setQuestion(null);
            }

            return Response.status(Response.Status.OK).entity(new RetrieveAnswersByQuestionIdRsp(answers)).build();
        } catch (InvalidLoginCredentialException ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED).entity(errorRsp).build();
        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }
    }
}
