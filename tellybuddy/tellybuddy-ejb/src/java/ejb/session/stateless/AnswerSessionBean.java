package ejb.session.stateless;

import entity.Answer;
import entity.Question;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.AnswerNotFoundException;
import util.exception.DeleteAnswerException;
import util.exception.QuestionNotFoundException;

/**
 *
 * @author tjle2
 */
@Stateless
public class AnswerSessionBean implements AnswerSessionBeanLocal {
    
    @EJB
    private QuestionSessionBeanLocal questionSessionBeanLocal;
    
    @PersistenceContext(unitName = "tellybuddy-ejbPU")
    private EntityManager entityManager;
    
    @Override
    public Long createNewAnswer(Question question, Answer newAnswer) throws QuestionNotFoundException {
        
        Question questionToAssociateWith = questionSessionBeanLocal.retrieveQuestionById(question.getQuestionId());
        
        newAnswer.setQuestion(questionToAssociateWith);
        
        entityManager.persist(newAnswer);
        entityManager.flush();
        
        questionToAssociateWith.getAnswers().add(newAnswer);
        
        return newAnswer.getAnswerId();
    }
    
    @Override
    public Answer retrieveAnswerById(Long answerId) throws AnswerNotFoundException {
        
        Answer answer = entityManager.find(Answer.class, answerId);
        
        if (answer != null) {
            return answer;
        } else {
            throw new AnswerNotFoundException("Question ID " + answerId + " does not exist!");
        }
    }
    
    @Override
    public void updateAnswer(Answer answer) throws AnswerNotFoundException {
        
        if (answer != null && answer.getAnswerId() != null) {
            
            Answer answerToUpdate = retrieveAnswerById(answer.getAnswerId());
            
            answerToUpdate.setAnswer(answer.getAnswer());
            answerToUpdate.setIsAnswer(answer.getIsAnswer());
            
        } else {
            throw new AnswerNotFoundException("Answer ID not provided for answer to be deleted");
            
        }
    }
    
    @Override
    public void deleteAnswer(Answer answer) throws DeleteAnswerException, AnswerNotFoundException {
        
        if (answer != null && answer.getAnswerId() != null) {
            
            Answer answerToDelete = retrieveAnswerById(answer.getAnswerId());
            
            Query query = entityManager.createQuery("SELECT r FROM Response r WHERE r.answer = :inAnswer");
            query.setParameter("inAnswer", answerToDelete);
            
            if (!query.getResultList().isEmpty()) {
                throw new DeleteAnswerException("Answer has been recorded in a response, unable to delete");
            }
            
            answerToDelete.getQuestion().getAnswers().remove(answerToDelete);
            entityManager.remove(answerToDelete);
            
        } else {
            throw new AnswerNotFoundException("Answer ID not provided for answer to be deleted");
            
        }
    }
}