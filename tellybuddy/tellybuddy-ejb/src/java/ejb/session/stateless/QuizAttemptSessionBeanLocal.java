/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Customer;
import entity.Quiz;
import entity.QuizAttempt;
import entity.QuizResponse;
import java.util.List;
import javax.ejb.Local;
import util.exception.CreateNewQuizAttemptException;
import util.exception.QuizAttemptNotFoundException;

/**
 *
 * @author tjle2
 */
@Local
public interface QuizAttemptSessionBeanLocal {

    public List<QuizAttempt> retrieveCustomerQuizAttempts(Customer customer);

    public Integer createNewQuizAttempt(Customer customer, Quiz quiz, List<QuizResponse> quizResponses) throws CreateNewQuizAttemptException;

    public QuizAttempt retrieveQuizAttemptByQuizAttemptId(Long quizAttemptId) throws QuizAttemptNotFoundException;
    
}
