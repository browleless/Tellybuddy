package ejb.session.stateful;

import ejb.session.stateless.ResponseSessionBeanLocal;
import entity.Customer;
import entity.Quiz;
import entity.QuizAttempt;
import entity.Response;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.exception.AnswerNotFoundException;
import util.exception.QuestionNotFoundException;
import util.exception.QuizNotFoundException;
import util.exception.ResponseNotFoundException;

/**
 *
 * @author tjle2
 */
@Stateful
public class QuizAttemptSessionBean implements QuizAttemptSessionBeanLocal {
    
    @EJB
    private QuizSessionBeanLocal quizSessionBeanLocal;
    
    @EJB
    private ResponseSessionBeanLocal responseSessionBeanLocal;
    
    @PersistenceContext(unitName = "tellybuddy-ejbPU")
    private EntityManager entityManager;
    
    private List<Response> responses;
    
    public QuizAttemptSessionBean() {
        initialiseState();
    }
    
    private void initialiseState() {
        responses = new ArrayList<>();
    }
    
    @Override
    public void addResponse(Response newResponse) {
        
        responses.add(newResponse);
    }
    
    @Override
    public void deleteResponse(Response response) {

        // might have problem because of equals method unsure if overridden correctly
        responses.remove(response);
    }
    
    @Override
    public QuizAttempt submitQuizAttempt(Customer customer, Quiz quiz) throws QuestionNotFoundException, AnswerNotFoundException, ResponseNotFoundException, QuizNotFoundException {
        
        Quiz quizToAssociateWith = quizSessionBeanLocal.retrieveQuizById(quiz.getQuizId());
        Customer customerToAssociateWith = entityManager.find(Customer.class, customer.getCustomerId());
        
        QuizAttempt newQuizAttempt = new QuizAttempt();
        int quizScore = 0;
        
        for (Response response : responses) {
            if (response.getIsCorrect()) {
                ++quizScore;
            }
            Long newResponseId = responseSessionBeanLocal.createNewResponse(response, response.getQuestion(), response.getAnswer());
            newQuizAttempt.getResponses().add(responseSessionBeanLocal.retrieveResponseById(newResponseId));
        }
        
        newQuizAttempt.setQuiz(quizToAssociateWith);
        newQuizAttempt.setCustomer(customerToAssociateWith);
        newQuizAttempt.setScore(quizScore);
        newQuizAttempt.setCompletedDate(new Date());
        
        entityManager.persist(newQuizAttempt);
        entityManager.flush();
        
        quizToAssociateWith.getQuizAttempts().add(newQuizAttempt);
        customerToAssociateWith.getQuizAttempts().add(newQuizAttempt);
        
        initialiseState();
        
        return newQuizAttempt;
    }
}
