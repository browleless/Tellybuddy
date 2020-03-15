/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Answer;
import entity.Question;
import javax.ejb.Local;
import util.exception.AnswerNotFoundException;
import util.exception.DeleteAnswerException;
import util.exception.QuestionNotFoundException;

/**
 *
 * @author tjle2
 */
@Local
public interface AnswerSessionBeanLocal {

    public Long createNewAnswer(Question question, Answer newAnswer) throws QuestionNotFoundException;

    public Answer retrieveAnswerByAnswerId(Long answerId) throws AnswerNotFoundException;

    public void updateAnswer(Answer answer) throws AnswerNotFoundException;

    public void deleteAnswer(Answer answer) throws DeleteAnswerException, AnswerNotFoundException;
    
}
