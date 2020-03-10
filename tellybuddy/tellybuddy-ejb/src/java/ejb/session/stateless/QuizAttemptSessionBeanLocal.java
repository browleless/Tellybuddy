/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Customer;
import entity.Quiz;
import entity.QuizAttempt;
import entity.Response;
import javax.ejb.Local;
import util.exception.AnswerNotFoundException;
import util.exception.QuestionNotFoundException;
import util.exception.QuizNotFoundException;
import util.exception.ResponseNotFoundException;

/**
 *
 * @author tjle2
 */
@Local
public interface QuizAttemptSessionBeanLocal {

    public void addResponse(Response newResponse);

    public void deleteResponse(Response response);

    public QuizAttempt submitQuizAttempt(Customer customer, Quiz quiz) throws QuestionNotFoundException, AnswerNotFoundException, ResponseNotFoundException, QuizNotFoundException;
    
}
