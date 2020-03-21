/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Answer;
import entity.Question;
import entity.Quiz;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;
import util.exception.DeleteAnswerException;
import util.exception.DeleteQuestionException;
import util.exception.DeleteQuizException;
import util.exception.QuestionNotFoundException;
import util.exception.QuizNameExistException;
import util.exception.QuizNotFoundException;

/**
 *
 * @author tjle2
 */
@Local
public interface QuizSessionBeanLocal {

    public Quiz retrieveQuizByQuizId(Long quizId) throws QuizNotFoundException;

    public void updateQuiz(Quiz quiz) throws QuizNotFoundException;

    public void deleteQuiz(Quiz quiz) throws QuizNotFoundException, DeleteQuizException, DeleteQuestionException, DeleteAnswerException;

    public List<Quiz> retrieveAllQuizzes();

    public List<Quiz> retrieveActiveQuizzes();

    public List<Quiz> retrieveUpcomingQuizzes();

    public Long createNewQuiz(Quiz newQuiz) throws QuizNameExistException, QuizNotFoundException, QuestionNotFoundException;

}
