package ws.datamodel;

import entity.QuizAttempt;

/**
 *
 * @author tjle2
 */
public class RetrieveQuizAttemptRsp {
    
    private QuizAttempt quizAttempt;

    public RetrieveQuizAttemptRsp() {
    }

    public RetrieveQuizAttemptRsp(QuizAttempt quizAttempt) {
        this.quizAttempt = quizAttempt;
    }

    public QuizAttempt getQuizAttempt() {
        return quizAttempt;
    }

    public void setQuizAttempt(QuizAttempt quizAttempt) {
        this.quizAttempt = quizAttempt;
    }
}
