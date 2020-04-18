package ejb.session.stateless;

import entity.Answer;
import entity.Customer;
import entity.FamilyGroup;
import entity.Question;
import entity.Quiz;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.exception.AnswerNotFoundException;
import util.exception.CreateNewQuizException;
import util.exception.DeleteAnswerException;
import util.exception.DeleteQuestionException;
import util.exception.DeleteQuizException;
import util.exception.FamilyGroupNotFoundException;
import util.exception.QuestionNotFoundException;
import util.exception.QuizNameExistException;
import util.exception.QuizNotFoundException;
import util.exception.UpdateQuizException;

/**
 *
 * @author tjle2
 */
@Stateless
public class QuizSessionBean implements QuizSessionBeanLocal {

    @EJB
    private FamilyGroupSessionBeanLocal familyGroupSessionBeanLocal;

    @EJB
    private AnswerSessionBeanLocal answerSessionBeanLocal;

    @EJB
    private QuestionSessionBeanLocal questionSessionBeanLocal;

    @PersistenceContext(unitName = "tellybuddy-ejbPU")
    private EntityManager entityManager;

    @Resource
    private EJBContext ejbContext;

    public QuizSessionBean() {
    }

    @Override
    public Long createNewQuiz(Quiz newQuiz) throws QuizNameExistException, CreateNewQuizException {

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
        } catch (QuestionNotFoundException | QuizNotFoundException ex) {
            ejbContext.setRollbackOnly();
            throw new CreateNewQuizException(ex.getMessage());
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
    public List<Quiz> retirevePastQuizzes() {

        Query query = entityManager.createQuery("SELECT q FROM Quiz q WHERE q.expiryDate < CURRENT_TIMESTAMP ORDER BY q.expiryDate");

        return query.getResultList();
    }

    @Override
    public List<Quiz> retrieveUpcomingQuizzes() {

        Query query = entityManager.createQuery("SELECT q FROM Quiz q WHERE CURRENT_TIMESTAMP < q.openDate ORDER BY q.openDate");

        return query.getResultList();
    }

    @Override
    public List<Quiz> retrieveAllUnattemptedActiveQuizzes(Customer customer) {

        Query query = entityManager.createQuery("SELECT q FROM Quiz q WHERE q.quizAttempts IS EMPTY AND (CURRENT_TIMESTAMP BETWEEN q.openDate AND q.expiryDate) UNION SELECT q FROM Quiz q WHERE q.quizAttempts IS NOT EMPTY AND (CURRENT_TIMESTAMP BETWEEN q.openDate AND q.expiryDate) AND NOT EXISTS (SELECT qa from QuizAttempt qa WHERE qa.quiz = q AND qa.customer = :inCustomer)");
        query.setParameter("inCustomer", customer);

        return query.getResultList();
    }

    @Override
    public List<Customer> retrieveQuizUnattemptedFamilyMembers(Quiz quiz, Customer customer) throws FamilyGroupNotFoundException {
        
        FamilyGroup familyGroup = familyGroupSessionBeanLocal.retrieveFamilyGroupByCustomer(customer);
        
        Query query = entityManager.createQuery("SELECT c FROM Customer c WHERE c <> :inCustomer AND c.familyGroup IS NOT NULL AND c.familyGroup = :inFamilyGroup AND (EXISTS (SELECT q FROM Quiz q WHERE q = :inQuiz AND q.quizAttempts IS EMPTY) OR EXISTS (SELECT q FROM Quiz q, IN (q.quizAttempts) qa WHERE q = :inQuiz AND q.quizAttempts IS NOT EMPTY AND c <> qa.customer))");
        query.setParameter("inQuiz", quiz);
        query.setParameter("inFamilyGroup", familyGroup);
        query.setParameter("inCustomer", customer);

        return query.getResultList();
    }

    @Override
    public void updateQuiz(Quiz quiz) throws QuizNotFoundException, UpdateQuizException {

        if (quiz != null && quiz.getQuizId() != null) {

            try {
                Quiz quizToUpdate = retrieveQuizByQuizId(quiz.getQuizId());

                quizToUpdate.setName(quiz.getName());
                quizToUpdate.setOpenDate(quiz.getOpenDate());
                quizToUpdate.setExpiryDate(quiz.getExpiryDate());
                quizToUpdate.setUnitsWorth(quiz.getUnitsWorth());

                List<Question> updatedQuizQuestions = quiz.getQuestions();
                List<Question> questionsToDelete = new ArrayList<>();
                List<Question> questionsToAdd = new ArrayList<>();

                List<Answer> answersToAdd = new ArrayList<>();
                List<Answer> answersToUpdate = new ArrayList<>();
                List<Answer> answersToDelete = new ArrayList<>();

                for (Question questionToCheck : quizToUpdate.getQuestions()) {
                    if (!updatedQuizQuestions.contains(questionToCheck)) {
                        questionsToDelete.add(questionToCheck);
                    } else {
                        for (Question questionToUpdate : updatedQuizQuestions) {
                            if (questionToCheck.equals(questionToUpdate)) {

                                questionSessionBeanLocal.updateQuestion(questionToUpdate);

                                for (Answer answer : questionToUpdate.getAnswers()) {
                                    if (answer.getAnswerId() == null) {
                                        answersToAdd.add(answer);
                                    } else if (questionToCheck.getAnswers().contains(answer)) {
                                        answersToUpdate.add(answer);
                                    }
                                }

                                for (Answer answerToDelete : questionToCheck.getAnswers()) {
                                    if (!questionToUpdate.getAnswers().contains(answerToDelete)) {
                                        answersToDelete.add(answerToDelete);
                                    }
                                }

                                for (Answer answerToAdd : answersToAdd) {
                                    answerSessionBeanLocal.createNewAnswer(questionToCheck, answerToAdd);
                                }

                                answersToAdd.clear();

                                for (Answer answerToUpdate : answersToUpdate) {
                                    answerSessionBeanLocal.updateAnswer(answerToUpdate);
                                }

                                answersToUpdate.clear();

                                for (Answer answerToDelete : answersToDelete) {
                                    answerSessionBeanLocal.deleteAnswer(answerToDelete);
                                }

                                answersToDelete.clear();

                                break;
                            }
                        }
                    }
                }

                for (Question questionToCheck : updatedQuizQuestions) {
                    if (!quizToUpdate.getQuestions().contains(questionToCheck)) {
                        questionsToAdd.add(questionToCheck);
                    }
                }

                for (Question questionToAdd : questionsToAdd) {

                    answersToAdd = new ArrayList<>();

                    for (Answer answerToAdd : questionToAdd.getAnswers()) {
                        answersToAdd.add(answerToAdd);
                    }

                    questionToAdd.getAnswers().clear();

                    questionSessionBeanLocal.createNewQuestion(quizToUpdate, questionToAdd);

                    for (Answer answerToAdd : answersToAdd) {
                        answerSessionBeanLocal.createNewAnswer(questionToAdd, answerToAdd);

                    }
                }

                for (Question questionToDelete : questionsToDelete) {

                    answersToDelete = new ArrayList<>();

                    for (Answer answerToDelete : questionToDelete.getAnswers()) {
                        answersToDelete.add(answerToDelete);
                    }

                    for (Answer answerToDelete : answersToDelete) {
                        answerSessionBeanLocal.deleteAnswer(answerToDelete);
                    }

                    questionSessionBeanLocal.deleteQuestion(questionToDelete);

                }
            } catch (AnswerNotFoundException | DeleteAnswerException | DeleteQuestionException | QuestionNotFoundException | QuizNotFoundException ex) {
                ejbContext.setRollbackOnly();
                throw new UpdateQuizException(ex.getMessage());
            }
        } else {
            throw new QuizNotFoundException("Quiz ID not provided for quiz to be updated");
        }
    }

    @Override
    public void deleteQuiz(Quiz quiz) throws QuizNotFoundException, DeleteQuizException {

        if (quiz != null && quiz.getQuizId() != null) {

            try {
                Quiz quizToDelete = retrieveQuizByQuizId(quiz.getQuizId());

                if (!quizToDelete.getQuizAttempts().isEmpty()) {
                    throw new DeleteQuizException("Quiz has attempts already, manage attempts before deleting quiz!");
                }

                for (Question question : quiz.getQuestions()) {
                    questionSessionBeanLocal.deleteQuestion(question);
                }

                entityManager.remove(quizToDelete);
            } catch (DeleteAnswerException | DeleteQuestionException | QuestionNotFoundException | QuizNotFoundException ex) {
                ejbContext.setRollbackOnly();
                throw new DeleteQuizException(ex.getMessage());
            }
        } else {
            throw new QuizNotFoundException("Quiz ID not provided for quiz to be deleted");
        }
    }
}
