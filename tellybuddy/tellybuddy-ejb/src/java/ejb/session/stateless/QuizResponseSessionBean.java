package ejb.session.stateless;

import entity.Answer;
import entity.Question;
import entity.QuizResponse;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.exception.AnswerNotFoundException;
import util.exception.QuestionNotFoundException;
import util.exception.QuizResponseNotFoundException;

/**
 *
 * @author tjle2
 */
@Stateless
public class QuizResponseSessionBean implements QuizResponseSessionBeanLocal {

    @EJB
    private AnswerSessionBeanLocal answerSessionBeanLocal;

    @EJB
    private QuestionSessionBeanLocal questionSessionBeanLocal;

    @PersistenceContext(unitName = "tellybuddy-ejbPU")
    private EntityManager entityManager;

    @Override
    public QuizResponse createNewQuizResponse(QuizResponse newQuizResponse, Question question, Answer answer) throws QuestionNotFoundException, AnswerNotFoundException {

        Question questionToAssociateWith = questionSessionBeanLocal.retrieveQuestionByQuestionId(question.getQuestionId());
        Answer answerToAssociateWith = answerSessionBeanLocal.retrieveAnswerByAnswerId(answer.getAnswerId());

        newQuizResponse.setQuestion(questionToAssociateWith);
        newQuizResponse.setAnswer(answerToAssociateWith);

        if (answerToAssociateWith.getIsAnswer()) {
            newQuizResponse.setIsCorrect(Boolean.TRUE);
        }

        entityManager.persist(newQuizResponse);
        entityManager.flush();

        return newQuizResponse;
    }

    @Override
    public QuizResponse retrieveQuizResponseByQuizResponseId(Long quizResponseId) throws QuizResponseNotFoundException {

        QuizResponse response = entityManager.find(QuizResponse.class, quizResponseId);

        if (response != null) {
            return response;
        } else {
            throw new QuizResponseNotFoundException("Response ID " + quizResponseId + " does not exist!");
        }
    }
}
