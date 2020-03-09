package ejb.session.stateless;

import ejb.session.stateful.QuizSessionBeanLocal;
import entity.Answer;
import entity.Question;
import entity.Quiz;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.AnswerNotFoundException;
import util.exception.DeleteAnswerException;
import util.exception.DeleteQuestionException;
import util.exception.QuestionNotFoundException;
import util.exception.QuizNotFoundException;

/**
 *
 * @author tjle2
 */
@Stateless
public class QuestionSessionBean implements QuestionSessionBeanLocal {

    @EJB
    private QuizSessionBeanLocal quizSessionBeanLocal;

    @EJB
    private AnswerSessionBeanLocal answerSessionBeanLocal;

    @PersistenceContext(unitName = "tellybuddy-ejbPU")
    private EntityManager entityManager;

    @Override
    public Long createNewQuestion(Quiz quiz, Question newQuestion) throws QuizNotFoundException {

        Quiz quizToAssociateWith = quizSessionBeanLocal.retrieveQuizById(quiz.getQuizId());

        newQuestion.setQuiz(quizToAssociateWith);

        entityManager.persist(newQuestion);
        entityManager.flush();

        quizToAssociateWith.getQuestions().add(newQuestion);

        return newQuestion.getQuestionId();
    }

    @Override
    public Question retrieveQuestionById(Long questionId) throws QuestionNotFoundException {

        Question question = entityManager.find(Question.class, questionId);

        if (question != null) {
            return question;
        } else {
            throw new QuestionNotFoundException("Question ID " + questionId + " does not exist!");
        }
    }

    @Override
    public void addNewAnswer(Question currentQuestion, Answer newAnswer) throws QuestionNotFoundException {

        answerSessionBeanLocal.createNewAnswer(currentQuestion, newAnswer);
    }

    @Override
    public void updateQuestion(Question question) throws QuestionNotFoundException {

        if (question != null && question.getQuestionId() != null) {

            Question questionToUpdate = retrieveQuestionById(question.getQuestionId());
            questionToUpdate.setQuestion(question.getQuestion());

        } else {
            throw new QuestionNotFoundException("Quiz ID not provided for quiz to be updated");
        }
    }

    @Override
    public void deleteQuestion(Question question) throws QuestionNotFoundException, DeleteQuestionException, DeleteAnswerException {

        if (question != null && question.getQuestionId() != null) {

            Question questionToDelete = retrieveQuestionById(question.getQuestionId());

            Query query = entityManager.createQuery("SELECT r FROM Response r WHERE r.question = :inQuestion");
            query.setParameter("inQuestion", questionToDelete);

            if (!query.getResultList().isEmpty()) {
                throw new DeleteQuestionException("Answer has been recorded in a response, unable to delete");
            }

            for (Answer answer : questionToDelete.getAnswers()) {
                try {
                    answerSessionBeanLocal.deleteAnswer(answer);
                } catch (AnswerNotFoundException ex) {
                    // won't happen
                    ex.printStackTrace();
                }
            }

            questionToDelete.getQuiz().getQuestions().remove(questionToDelete);
            entityManager.remove(questionToDelete);

        } else {
            throw new QuestionNotFoundException("Quiz ID not provided for quiz to be deleted");
        }
    }
}
