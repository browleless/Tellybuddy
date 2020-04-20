/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Customer;
import entity.FamilyGroup;
import entity.Quiz;
import java.util.List;
import javax.ejb.Local;
import util.exception.CreateNewQuizException;
import util.exception.DeleteQuizException;
import util.exception.FamilyGroupNotFoundException;
import util.exception.QuizNameExistException;
import util.exception.QuizNotFoundException;
import util.exception.UpdateQuizException;

/**
 *
 * @author tjle2
 */
@Local
public interface QuizSessionBeanLocal {

    public Quiz retrieveQuizByQuizId(Long quizId) throws QuizNotFoundException;

    public void updateQuiz(Quiz quiz) throws QuizNotFoundException, UpdateQuizException;

    public void deleteQuiz(Quiz quiz) throws QuizNotFoundException, DeleteQuizException;

    public List<Quiz> retrieveAllQuizzes();

    public List<Quiz> retrieveActiveQuizzes();

    public List<Quiz> retrieveUpcomingQuizzes();

    public Long createNewQuiz(Quiz newQuiz) throws QuizNameExistException, CreateNewQuizException;

    public List<Quiz> retirevePastQuizzes();

    public List<Quiz> retrieveAllUnattemptedActiveQuizzes(Customer customer);

    public List<Customer> retrieveQuizUnattemptedFamilyMembers(Quiz quiz, Customer customer) throws FamilyGroupNotFoundException;

}
