package ws.restful;

import ejb.session.stateless.CustomerSessionBeanLocal;
import ejb.session.stateless.QuestionSessionBeanLocal;
import entity.Answer;
import entity.Customer;
import entity.Question;
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
import ws.datamodel.RetrieveQuestionsByQuizIdRsp;

/**
 * REST Web Service
 *
 * @author tjle2
 */
@Path("Question")
public class QuestionResource {

    @Context
    private UriInfo context;

    private final SessionBeanLookup sessionBeanLookup;

    private final CustomerSessionBeanLocal customerSessionBeanLocal;
    private final QuestionSessionBeanLocal questionSessionBeanLocal;

    /**
     * Creates a new instance of QuestionResource
     */
    public QuestionResource() {

        sessionBeanLookup = new SessionBeanLookup();

        customerSessionBeanLocal = sessionBeanLookup.lookupCustomerSessionBeanLocal();
        questionSessionBeanLocal = sessionBeanLookup.lookupQuestionSessionBeanLocal();
    }

    /**
     * Retrieves representation of an instance of ws.restful.QuestionResource
     *
     * @return an instance of java.lang.String
     */
    @Path("retrieveQuestionsByQuizId/{quizId}")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveQuestionsByQuizId(@QueryParam("username") String username, @QueryParam("password") String password, @PathParam("quizId") Long quizId) {

        try {
            Customer customer = customerSessionBeanLocal.customerLogin(username, password);
            System.out.println("********** QuestionResource.retrieveQuestionsByQuizId(): Customer " + username + " login remotely via web service");

            List<Question> questions = questionSessionBeanLocal.retrieveQuestionsByQuizId(quizId);

            for (Question question : questions) {
                question.getQuiz().getQuestions().clear();
                question.getQuiz().getQuizAttempts().clear();
                for (Answer answer : question.getAnswers()) {
                    answer.setQuestion(null);
                }
            }

            return Response.status(Response.Status.OK).entity(new RetrieveQuestionsByQuizIdRsp(questions)).build();
        } catch (InvalidLoginCredentialException ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED).entity(errorRsp).build();
        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }
    }
}
