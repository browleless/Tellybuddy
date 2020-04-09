package ws.restful;

import ejb.session.stateless.CustomerSessionBeanLocal;
import ejb.session.stateless.QuizSessionBeanLocal;
import entity.Customer;
import entity.Quiz;
import java.util.List;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import util.exception.InvalidLoginCredentialException;
import ws.datamodel.ErrorRsp;
import ws.datamodel.RetrieveAllUnattemptedActiveQuizzesRsp;

/**
 * REST Web Service
 *
 * @author tjle2
 */
@Path("Quiz")
public class QuizResource {

    @Context
    private UriInfo context;

    private final SessionBeanLookup sessionBeanLookup;

    private final CustomerSessionBeanLocal customerSessionBeanLocal;
    private final QuizSessionBeanLocal quizSessionBeanLocal;

    public QuizResource() {

        sessionBeanLookup = new SessionBeanLookup();

        customerSessionBeanLocal = sessionBeanLookup.lookupCustomerSessionBeanLocal();
        quizSessionBeanLocal = sessionBeanLookup.lookupQuizSessionBeanLocal();
    }

    /**
     * Retrieves representation of an instance of ws.restful.QuizResource
     *
     * @return an instance of java.lang.String
     */
    @Path("retrieveAllUnattemptedActiveQuizzes")
    @GET
    @Consumes(MediaType.TEXT_PLAIN) 
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAllUnattemptedActiveQuizzes(@QueryParam("username") String username, @QueryParam("password") String password) {
        try {
            Customer customer = customerSessionBeanLocal.customerLogin(username, password);
            System.out.println("********** QuizResource.retrieveAllUnattemptedQuizzes(): Customer " + customer.getUsername() + " login remotely via web service");

            List<Quiz> quizzes = quizSessionBeanLocal.retrieveAllUnattemptedActiveQuizzes(customer);

            for (Quiz quiz : quizzes) {
                quiz.getQuestions().clear();
                quiz.getQuizAttempts().clear();
            }

            return Response.status(Response.Status.OK).entity(new RetrieveAllUnattemptedActiveQuizzesRsp(quizzes)).build();
        } catch (InvalidLoginCredentialException ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED).entity(errorRsp).build();
        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }
    }
}
