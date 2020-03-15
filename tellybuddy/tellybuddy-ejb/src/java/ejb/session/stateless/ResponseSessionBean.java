package ejb.session.stateless;

import entity.Answer;
import entity.Question;
import entity.Response;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.exception.AnswerNotFoundException;
import util.exception.QuestionNotFoundException;
import util.exception.ResponseNotFoundException;

/**
 *
 * @author tjle2
 */
@Stateless
public class ResponseSessionBean implements ResponseSessionBeanLocal {

    @EJB
    private AnswerSessionBeanLocal answerSessionBeanLocal;

    @EJB
    private QuestionSessionBeanLocal questionSessionBeanLocal;

    @PersistenceContext(unitName = "tellybuddy-ejbPU")
    private EntityManager entityManager;
    
    @Override
    public Long createNewResponse(Response newResponse, Question question, Answer answer) throws QuestionNotFoundException, AnswerNotFoundException {
        
        Question questionToAssociateWith = questionSessionBeanLocal.retrieveQuestionByQuestionId(question.getQuestionId());
        Answer answerToAssociateWith = answerSessionBeanLocal.retrieveAnswerByAnswerId(answer.getAnswerId());
        
        newResponse.setQuestion(questionToAssociateWith);
        newResponse.setAnswer(answerToAssociateWith);
        
        entityManager.persist(newResponse);
        entityManager.flush();
        
        return newResponse.getResponseId();
    }
    
    @Override
    public Response retrieveResponseByResponseId(Long responseId) throws ResponseNotFoundException {
        
        Response response = entityManager.find(Response.class, responseId);

        if (response != null) {
            return response;
        } else {
            throw new ResponseNotFoundException("Response ID " + responseId + " does not exist!");
        }
    }
}
