package ejb.session.stateless;

import entity.Customer;
import entity.Quiz;
import entity.QuizAttempt;
import entity.QuizResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.AnswerNotFoundException;
import util.exception.CustomerNotFoundException;
import util.exception.QuestionNotFoundException;
import util.exception.QuizAttemptNotFoundException;
import util.exception.QuizNotFoundException;
import util.exception.QuizResponseNotFoundException;

/**
 *
 * @author tjle2
 */
@Stateless
public class QuizAttemptSessionBean implements QuizAttemptSessionBeanLocal {

    @EJB
    private CustomerSessionBeanLocal customerSessionBeanLocal;

    @EJB
    private QuizSessionBeanLocal quizSessionBeanLocal;

    @EJB
    private QuizResponseSessionBeanLocal quizResponseSessionBeanLocal;

    @PersistenceContext(unitName = "tellybuddy-ejbPU")
    private EntityManager entityManager;

    private List<QuizResponse> quizResponses;

    public QuizAttemptSessionBean() {
        initialiseState();
    }

    private void initialiseState() {
        quizResponses = new ArrayList<>();
    }

    @Override
    public void addResponse(QuizResponse newQuizResponse) {

        quizResponses.add(newQuizResponse);
    }

    @Override
    public void deleteResponse(QuizResponse quizResponse) {

        // might have problem because of equals method unsure if overridden correctly
        quizResponses.remove(quizResponse);
    }

    @Override
    public QuizAttempt submitQuizAttempt(Customer customer, Quiz quiz) throws QuestionNotFoundException, AnswerNotFoundException, QuizResponseNotFoundException, QuizNotFoundException {

        Quiz quizToAssociateWith = quizSessionBeanLocal.retrieveQuizByQuizId(quiz.getQuizId());
        Customer customerToAssociateWith = entityManager.find(Customer.class, customer.getCustomerId());

        QuizAttempt newQuizAttempt = new QuizAttempt();
        int quizScore = 0;

        for (QuizResponse quizResponse : quizResponses) {
            if (quizResponse.getIsCorrect()) {
                ++quizScore;
            }
            QuizResponse qr = quizResponseSessionBeanLocal.createNewQuizResponse(quizResponse, quizResponse.getQuestion(), quizResponse.getAnswer());
            newQuizAttempt.getQuizResponses().add(qr);
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

    @Override
    public Long createNewQuizAttempt(Customer customer, QuizAttempt quizAttempt, Quiz quiz, List<QuizResponse> quizResponses) throws CustomerNotFoundException, QuizNotFoundException, QuestionNotFoundException, AnswerNotFoundException, QuizResponseNotFoundException {

        int score = 0;

        QuizAttempt newQuizAttempt = new QuizAttempt();

        Customer customerToAssociateWith = customerSessionBeanLocal.retrieveCustomerByCustomerId(customer.getCustomerId());
        Quiz quizToAssociateWith = quizSessionBeanLocal.retrieveQuizByQuizId(quiz.getQuizId());

        for (QuizResponse quizResponse : quizResponses) {
            QuizResponse qr = quizResponseSessionBeanLocal.createNewQuizResponse(quizResponse, quizResponse.getQuestion(), quizResponse.getAnswer());
            newQuizAttempt.getQuizResponses().add(qr);
            if (qr.getIsCorrect()) {
                ++score;
            }
        }

        newQuizAttempt.setScore(score);
        newQuizAttempt.setCustomer(customerToAssociateWith);
        newQuizAttempt.setQuiz(quizToAssociateWith);
        newQuizAttempt.setCompletedDate(new Date());

        entityManager.persist(newQuizAttempt);
        entityManager.flush();

        customerToAssociateWith.getQuizAttempts().add(newQuizAttempt);
        quizToAssociateWith.getQuizAttempts().add(newQuizAttempt);

        return newQuizAttempt.getQuizAttemptId();
    }

    @Override
    public QuizAttempt retrieveQuizAttemptByQuizAttemptId(Long quizAttemptId) throws QuizAttemptNotFoundException {
        QuizAttempt quizAttempt = entityManager.find(QuizAttempt.class, quizAttemptId);

        if (quizAttempt != null) {
            return quizAttempt;
        } else {
            throw new QuizAttemptNotFoundException("Quiz Attempt Id " + quizAttemptId + " does not exist!");
        }
    }

    @Override
    public List<QuizAttempt> retrieveCustomerQuizAttempts(Customer customer) {

        Query query = entityManager.createQuery("SELECT qa FROM QuizAttempt qa WHERE qa.customer = :inCustomer");
        query.setParameter("inCustomer", customer);

        return query.getResultList();
    }
}
