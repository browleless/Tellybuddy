/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Answer;
import entity.Question;
import entity.QuizResponse;
import javax.ejb.Local;
import util.exception.AnswerNotFoundException;
import util.exception.QuestionNotFoundException;
import util.exception.QuizResponseNotFoundException;

/**
 *
 * @author tjle2
 */
@Local
public interface QuizResponseSessionBeanLocal {

    public QuizResponse createNewQuizResponse(QuizResponse newResponse, Question question, Answer answer) throws QuestionNotFoundException, AnswerNotFoundException;

    public QuizResponse retrieveQuizResponseByQuizResponseId(Long quizResponseId) throws QuizResponseNotFoundException;
    
}
