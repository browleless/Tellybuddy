package ws.datamodel;

import entity.QuizAttempt;
import java.util.List;

/**
 *
 * @author tjle2
 */
public class RetrieveCustomerQuizAttemptsRsp {
    
    private List<QuizAttempt> quizAttempts;

    public RetrieveCustomerQuizAttemptsRsp() {
    }

    public RetrieveCustomerQuizAttemptsRsp(List<QuizAttempt> quizAttempts) {
        this.quizAttempts = quizAttempts;
    }

    public List<QuizAttempt> getQuizAttempts() {
        return quizAttempts;
    }

    public void setQuizAttempts(List<QuizAttempt> quizAttempts) {
        this.quizAttempts = quizAttempts;
    }
}
