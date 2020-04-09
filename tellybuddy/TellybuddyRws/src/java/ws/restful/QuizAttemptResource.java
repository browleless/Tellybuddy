package ws.restful;

import ejb.session.stateless.CustomerSessionBeanLocal;
import ejb.session.stateless.QuizAttemptSessionBeanLocal;
import entity.Customer;
import entity.QuizAttempt;
import entity.QuizResponse;
import java.util.List;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import util.exception.AnswerNotFoundException;
import util.exception.CustomerNotFoundException;
import util.exception.InvalidLoginCredentialException;
import util.exception.QuestionNotFoundException;
import util.exception.QuizAttemptNotFoundException;
import util.exception.QuizNotFoundException;
import util.exception.QuizResponseNotFoundException;
import ws.datamodel.CreateNewQuizAttemptReq;
import ws.datamodel.CreateNewQuizAttemptRsp;
import ws.datamodel.ErrorRsp;
import ws.datamodel.RetrieveCustomerQuizAttemptsRsp;
import ws.datamodel.RetrieveQuizAttemptRsp;

/**
 * REST Web Service
 *
 * @author tjle2
 */
@Path("QuizAttempt")
public class QuizAttemptResource {

    @Context
    private UriInfo context;

    private final SessionBeanLookup sessionBeanLookup;

    private final CustomerSessionBeanLocal customerSessionBeanLocal;
    private final QuizAttemptSessionBeanLocal quizAttemptSessionBeanLocal;

    /**
     * Creates a new instance of QuizAttemptResource
     */
    public QuizAttemptResource() {

        sessionBeanLookup = new SessionBeanLookup();

        customerSessionBeanLocal = sessionBeanLookup.lookupCustomerSessionBeanLocal();
        quizAttemptSessionBeanLocal = sessionBeanLookup.lookupQuizAttemptSessionBeanLocal();
    }

    /**
     * Retrieves representation of an instance of ws.restful.QuizAttemptResource
     *
     * @return an instance of java.lang.String
     */
    @Path("retrieveCustomerQuizAttempts")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveCustomerQuizAttempts(@QueryParam("username") String username, @QueryParam("password") String password) {
        try {
            Customer customer = customerSessionBeanLocal.customerLogin(username, password);
            System.out.println("********** QuizAttemptResource.retrieveCustomerQuizAttempts(): Customer " + customer.getUsername() + " login remotely via web service");

            List<QuizAttempt> quizAttempts = quizAttemptSessionBeanLocal.retrieveCustomerQuizAttempts(customer);

            for (QuizAttempt quizAttempt : quizAttempts) {
                quizAttempt.setCustomer(null);
                quizAttempt.setQuiz(null);
                quizAttempt.getQuizResponses().clear();
            }

            return Response.status(Response.Status.OK).entity(new RetrieveCustomerQuizAttemptsRsp(quizAttempts)).build();
        } catch (InvalidLoginCredentialException ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED).entity(errorRsp).build();
        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }
    }

    @Path("retrieveQuizAttempt/{quizAttemptId}")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveQuizAttempt(@QueryParam("username") String username, @QueryParam("password") String password, @PathParam("quizAttemptId") Long quizAttemptId) {
        try {
            Customer customer = customerSessionBeanLocal.customerLogin(username, password);
            System.out.println("********** QuizAttemptResource.retrieveCustomerQuizAttempts(): Customer " + customer.getUsername() + " login remotely via web service");

            QuizAttempt quizAttempt = quizAttemptSessionBeanLocal.retrieveQuizAttemptByQuizAttemptId(quizAttemptId);

            quizAttempt.getCustomer().getQuizAttempts().clear();
            quizAttempt.getCustomer().getBills().clear();
            quizAttempt.getCustomer().getSubscriptions().clear();
            quizAttempt.getCustomer().getTransactions().clear();
            quizAttempt.getCustomer().setFamilyGroup(null);
            
            quizAttempt.getCustomer().setPassword(null);
            quizAttempt.getCustomer().setSalt(null);

            for (QuizResponse quizResponse : quizAttempt.getQuizResponses()) {
                quizResponse.setQuestion(null);
                quizResponse.setAnswer(null);
            }

            quizAttempt.getQuiz().getQuizAttempts().clear();
            quizAttempt.getQuiz().getQuestions().clear();

            return Response.status(Response.Status.OK).entity(new RetrieveQuizAttemptRsp(quizAttempt)).build();
        } catch (InvalidLoginCredentialException ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED).entity(errorRsp).build();
        } catch (QuizAttemptNotFoundException ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();
        } catch (Exception ex) {
            ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
        }
    }

    /**
     * PUT method for updating or creating an instance of QuizAttemptResource
     *
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createNewQuizAttempt(CreateNewQuizAttemptReq createNewQuizAttemptReq) {
        if (createNewQuizAttemptReq != null) {
            try {
                Customer customer = customerSessionBeanLocal.customerLogin(createNewQuizAttemptReq.getUsername(), createNewQuizAttemptReq.getPassword());
                System.out.println("********** QuizAttemptResource.createNewQuizAttempt(): Customer " + customer.getUsername() + " login remotely via web service");

                Long quizAttemptId = quizAttemptSessionBeanLocal.createNewQuizAttempt(customer, createNewQuizAttemptReq.getQuizAttempt(), createNewQuizAttemptReq.getQuiz(), createNewQuizAttemptReq.getQuizResponses());

                return Response.status(Response.Status.OK).entity(new CreateNewQuizAttemptRsp(quizAttemptId)).build();
            } catch (InvalidLoginCredentialException ex) {
                ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
                return Response.status(Response.Status.UNAUTHORIZED).entity(errorRsp).build();
            } catch (AnswerNotFoundException | CustomerNotFoundException | QuestionNotFoundException | QuizNotFoundException | QuizResponseNotFoundException ex) {
                ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
                return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();
            } catch (Exception ex) {
                ErrorRsp errorRsp = new ErrorRsp(ex.getMessage());
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorRsp).build();
            }
        } else {
            ErrorRsp errorRsp = new ErrorRsp("Invalid create quiz attempt request");
            return Response.status(Response.Status.BAD_REQUEST).entity(errorRsp).build();
        }
    }
}
