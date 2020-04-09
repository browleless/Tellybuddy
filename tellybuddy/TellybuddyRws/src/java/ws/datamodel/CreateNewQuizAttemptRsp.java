package ws.datamodel;

/**
 *
 * @author tjle2
 */
public class CreateNewQuizAttemptRsp {

    private Long quizAttemptId;

    public CreateNewQuizAttemptRsp() {
    }

    public CreateNewQuizAttemptRsp(Long quizAttemptId) {
        this.quizAttemptId = quizAttemptId;
    }

    public Long getQuizAttemptId() {
        return quizAttemptId;
    }

    public void setQuizAttemptId(Long quizAttemptId) {
        this.quizAttemptId = quizAttemptId;
    }
}
