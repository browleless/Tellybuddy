/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Answer;
import entity.Question;
import entity.Response;
import javax.ejb.Local;
import util.exception.AnswerNotFoundException;
import util.exception.QuestionNotFoundException;
import util.exception.ResponseNotFoundException;

/**
 *
 * @author tjle2
 */
@Local
public interface ResponseSessionBeanLocal {

    public Long createNewResponse(Response newResponse, Question question, Answer answer) throws QuestionNotFoundException, AnswerNotFoundException;

    public Response retrieveResponseById(Long responseId) throws ResponseNotFoundException;
    
}
