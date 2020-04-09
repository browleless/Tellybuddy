package ws.datamodel;

import entity.Customer;
import entity.Quiz;
import entity.QuizAttempt;
import entity.QuizResponse;
import java.util.List;

/**
 *
 * @author tjle2
 */
public class CreateNewQuizAttemptReq {

    private String username;
    private String password;
    private QuizAttempt quizAttempt;
    private Customer customer;
    private Quiz quiz;
    private List<QuizResponse> quizResponses;

    public CreateNewQuizAttemptReq() {
    }

    public CreateNewQuizAttemptReq(String username, String password, QuizAttempt quizAttempt, Customer customer, Quiz quiz, List<QuizResponse> quizResponses) {
        this.username = username;
        this.password = password;
        this.quizAttempt = quizAttempt;
        this.customer = customer;
        this.quiz = quiz;
        this.quizResponses = quizResponses;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public QuizAttempt getQuizAttempt() {
        return quizAttempt;
    }

    public void setQuizAttempt(QuizAttempt quizAttempt) {
        this.quizAttempt = quizAttempt;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public List<QuizResponse> getQuizResponses() {
        return quizResponses;
    }

    public void setQuizResponses(List<QuizResponse> quizResponses) {
        this.quizResponses = quizResponses;
    }
}
