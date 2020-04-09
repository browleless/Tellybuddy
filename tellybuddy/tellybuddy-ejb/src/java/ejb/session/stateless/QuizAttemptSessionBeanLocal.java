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
import util.exception.AnswerNotFoundException;
import util.exception.CustomerNotFoundException;
import util.exception.QuestionNotFoundException;
import util.exception.QuizAttemptNotFoundException;
import util.exception.QuizNotFoundException;
import util.exception.QuizResponseNotFoundException;

/**
 *
 * @author tjle2
 */
@Local
public interface QuizAttemptSessionBeanLocal {

    public void addResponse(QuizResponse newQuizResponse);

    public void deleteResponse(QuizResponse quizResponse);

    public QuizAttempt submitQuizAttempt(Customer customer, Quiz quiz) throws QuestionNotFoundException, AnswerNotFoundException, QuizResponseNotFoundException, QuizNotFoundException;

    public List<QuizAttempt> retrieveCustomerQuizAttempts(Customer customer);

    public Long createNewQuizAttempt(Customer customer, QuizAttempt quizAttempt, Quiz quiz, List<QuizResponse> responses) throws CustomerNotFoundException, QuizNotFoundException, QuestionNotFoundException, AnswerNotFoundException, QuizResponseNotFoundException;

    public QuizAttempt retrieveQuizAttemptByQuizAttemptId(Long quizAttemptId) throws QuizAttemptNotFoundException;
    
}
