package ejb.session.stateless;

import ejb.session.stateless.QuestionSessionBeanLocal;
import entity.Answer;
import entity.Question;
import entity.Quiz;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.DeleteAnswerException;
import util.exception.DeleteQuestionException;
import util.exception.DeleteQuizException;
import util.exception.QuestionNotFoundException;
import util.exception.QuizNotFoundException;

/**
 *
 * @author tjle2
 */
@Stateless
public class QuizSessionBean implements QuizSessionBeanLocal {

    @EJB
    private QuestionSessionBeanLocal questionSessionBeanLocal;

    @PersistenceContext(unitName = "tellybuddy-ejbPU")
    private EntityManager entityManager;

    private List<Question> questions;
    private HashMap<Question, List<Answer>> questionAnswerMap;

    public QuizSessionBean() {
        initialiseState();
    }

    private void initialiseState() {
        questions = new ArrayList<>();
        questionAnswerMap = new HashMap<>();
    }

    @Override
    public Quiz retrieveQuizById(Long quizId) throws QuizNotFoundException {

        Quiz quiz = entityManager.find(Quiz.class, quizId);

        if (quiz != null) {
            return quiz;
        } else {
            throw new QuizNotFoundException("Quiz ID " + quizId + " does not exist!");
        }
    }

    @Override
    public void addQuestion(Question newQuestion, List<Answer> answers) {

        questions.add(newQuestion);
        questionAnswerMap.put(newQuestion, answers);
    }

    @Override
    public void deleteQuestion(Question question) {

        // might have problem cause overriden quiz equals method might not be written correctly since ids of questions are not created yet
        questions.remove(question);
        questionAnswerMap.remove(question);
    }

    @Override
    public Quiz publishQuiz(Date openDate, Date expiryDate, Integer unitsWorth) throws QuizNotFoundException, QuestionNotFoundException {

        Quiz newQuiz = new Quiz(openDate, expiryDate, unitsWorth);

        entityManager.persist(newQuiz);
        entityManager.flush();

        for (Question question : questions) {
            
            Long newQuestionId = questionSessionBeanLocal.createNewQuestion(newQuiz, question);
            
            // was previouly null, only updated when persisted and flushed
            question.setQuestionId(newQuestionId);
            
            for (Answer answer : questionAnswerMap.get(question)) {
                questionSessionBeanLocal.addNewAnswer(question, answer);
            }
        }

        initialiseState();

        return newQuiz;
    }
    
    @Override
    public List<Quiz> retrieveAllQuizzes() {
        
        Query query = entityManager.createQuery("SELECT q FROM Quiz q");
        
        return query.getResultList();
    }
    
    @Override
    public List<Quiz> retrieveActiveQuizzes() {
        
        Query query = entityManager.createQuery("SELECT q FROM Quiz q WHERE q.expiryDate < CURRENT_TIMESTAMP");
        
        return query.getResultList();
    }

    @Override
    public void updateQuiz(Quiz quiz) throws QuizNotFoundException {

        if (quiz != null && quiz.getQuizId() != null) {

            Quiz quizToUpdate = retrieveQuizById(quiz.getQuizId());

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
            
            Quiz quizToDelete = retrieveQuizById(quiz.getQuizId());
            
            if (!quiz.getQuestions().isEmpty()) {
                throw new DeleteQuizException("Quiz has attempts already, manage attempts before deleting quiz!");
            }
            
            for (Question question : quizToDelete.getQuestions()) {
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
