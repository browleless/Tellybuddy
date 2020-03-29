/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Question;
import entity.Quiz;
import java.util.List;
import javax.ejb.Local;
import util.exception.DeleteAnswerException;
import util.exception.DeleteQuestionException;
import util.exception.QuestionNotFoundException;
import util.exception.QuizNotFoundException;

/**
 *
 * @author tjle2
 */
@Local
public interface QuestionSessionBeanLocal {

    public Long createNewQuestion(Quiz quiz, Question newQuestion) throws QuizNotFoundException;

    public Question retrieveQuestionByQuestionId(Long questionId) throws QuestionNotFoundException;

    public void updateQuestion(Question question) throws QuestionNotFoundException;

    public void deleteQuestion(Question question) throws QuestionNotFoundException, DeleteQuestionException, DeleteAnswerException;

    public List<Question> retrieveQuestionsByQuizId(Long quizId);
    
}
