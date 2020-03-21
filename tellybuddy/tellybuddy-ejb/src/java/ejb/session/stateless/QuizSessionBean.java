package ejb.session.stateless;

import entity.Answer;
import entity.Question;
import entity.Quiz;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.exception.DeleteAnswerException;
import util.exception.DeleteQuestionException;
import util.exception.DeleteQuizException;
import util.exception.QuestionNotFoundException;
import util.exception.QuizNameExistException;
import util.exception.QuizNotFoundException;

/**
 *
 * @author tjle2
 */
@Stateless
public class QuizSessionBean implements QuizSessionBeanLocal {

    @EJB
    private AnswerSessionBeanLocal answerSessionBeanLocal;

    @EJB
    private QuestionSessionBeanLocal questionSessionBeanLocal;

    @PersistenceContext(unitName = "tellybuddy-ejbPU")
    private EntityManager entityManager;

    public QuizSessionBean() {
    }

    public Long createNewQuiz(Quiz newQuiz) throws QuizNameExistException, QuizNotFoundException, QuestionNotFoundException {

        try {

            List<Question> questions = new ArrayList<>();

            for (Question question : newQuiz.getQuestions()) {

                questions.add(question);
            }

            newQuiz.getQuestions().clear();

            entityManager.persist(newQuiz);
            entityManager.flush();

            for (Question question : questions) {

                List<Answer> answers = new ArrayList<>();

                for (Answer answer : question.getAnswers()) {
                    answers.add(answer);
                }

                question.getAnswers().clear();

                questionSessionBeanLocal.createNewQuestion(newQuiz, question);

                for (Answer answer : answers) {
                    answerSessionBeanLocal.createNewAnswer(question, answer);
                }
            }

            return newQuiz.getQuizId();
        } catch (PersistenceException ex) {
            if (ex.getCause() != null && ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getSimpleName().equals("SQLIntegrityConstraintViolationException")) {
                throw new QuizNameExistException("Quiz with same name already exist");
            } else {
                throw new QuizNameExistException("An unexpected error has occurred: " + ex.getMessage());
            }
        }
    }

    @Override
    public Quiz retrieveQuizByQuizId(Long quizId) throws QuizNotFoundException {

        Quiz quiz = entityManager.find(Quiz.class, quizId);

        if (quiz != null) {
            return quiz;
        } else {
            throw new QuizNotFoundException("Quiz ID " + quizId + " does not exist!");
        }
    }

    @Override
    public List<Quiz> retrieveAllQuizzes() {

        Query query = entityManager.createQuery("SELECT q FROM Quiz q");

        return query.getResultList();
    }

    @Override
    public List<Quiz> retrieveActiveQuizzes() {

        Query query = entityManager.createQuery("SELECT q FROM Quiz q WHERE CURRENT_TIMESTAMP BETWEEN q.openDate AND q.expiryDate ORDER BY q.expiryDate");

        return query.getResultList();
    }

    @Override
    public List<Quiz> retrieveUpcomingQuizzes() {

        Query query = entityManager.createQuery("SELECT q FROM Quiz q WHERE CURRENT_TIMESTAMP < q.openDate ORDER BY q.openDate");

        return query.getResultList();
    }

    @Override
    public void updateQuiz(Quiz quiz) throws QuizNotFoundException {

        if (quiz != null && quiz.getQuizId() != null) {

            Quiz quizToUpdate = retrieveQuizByQuizId(quiz.getQuizId());

            quizToUpdate.setOpenDate(quiz.getOpenDate());
            quizToUpdate.setExpiryDate(quiz.getExpiryDate());
            quizToUpdate.setUnitsWorth(quiz.getUnitsWorth());

        } else {
            throw new QuizNotFoundException("Quiz ID not provided for quiz to be updated");
        }
    }

    @Override
    public void deleteQuiz(Quiz quiz) throws QuizNotFoundException, DeleteQuizException, DeleteQuestionException, DeleteAnswerException {

        if (quiz != null && quiz.getQuizId() != null) {

            Quiz quizToDelete = retrieveQuizByQuizId(quiz.getQuizId());

            if (!quizToDelete.getQuizAttempts().isEmpty()) {
                throw new DeleteQuizException("Quiz has attempts already, manage attempts before deleting quiz!");
            }

            for (Question question : quiz.getQuestions()) {
                try {
                    questionSessionBeanLocal.deleteQuestion(question);
                } catch (QuestionNotFoundException ex) {
                    // won't happen
                    ex.printStackTrace();
                }
            }
            entityManager.remove(quizToDelete);
        } else {
            throw new QuizNotFoundException("Quiz ID not provided for quiz to be deleted");
        }
    }
}
