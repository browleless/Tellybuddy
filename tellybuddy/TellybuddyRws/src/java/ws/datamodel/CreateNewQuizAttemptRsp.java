package ws.datamodel;

/**
 *
 * @author tjle2
 */
public class CreateNewQuizAttemptRsp {

    private Integer quizScore;

    public CreateNewQuizAttemptRsp() {
    }

    public CreateNewQuizAttemptRsp(Integer quizScore) {
        this.quizScore = quizScore;
    }

    public Integer getQuizScore() {
        return quizScore;
    }

    public void setQuizScore(Integer quizScore) {
        this.quizScore = quizScore;
    }
}
