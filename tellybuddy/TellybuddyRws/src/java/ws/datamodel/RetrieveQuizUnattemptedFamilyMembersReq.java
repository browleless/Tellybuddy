package ws.datamodel;

import entity.Customer;
import entity.Quiz;

/**
 *
 * @author tjle2
 */
public class RetrieveQuizUnattemptedFamilyMembersReq {
    
    private String username;
    private String password;
    private Quiz quiz;
    private Customer customer;

    public RetrieveQuizUnattemptedFamilyMembersReq() {
    }

    public RetrieveQuizUnattemptedFamilyMembersReq(String username, String password, Quiz quiz, Customer customer) {
        this.username = username;
        this.password = password;
        this.quiz = quiz;
        this.customer = customer;
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

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
