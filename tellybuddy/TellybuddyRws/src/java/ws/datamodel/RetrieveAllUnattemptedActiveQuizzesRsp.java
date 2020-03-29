package ws.datamodel;

import entity.Quiz;
import java.util.List;

/**
 *
 * @author tjle2
 */
public class RetrieveAllUnattemptedActiveQuizzesRsp {

    private List<Quiz> quizzes;

    public RetrieveAllUnattemptedActiveQuizzesRsp() {
    }

    public RetrieveAllUnattemptedActiveQuizzesRsp(List<Quiz> quizzes) {
        this.quizzes = quizzes;
    }

    public List<Quiz> getQuizzes() {
        return quizzes;
    }

    public void setQuizzes(List<Quiz> quizzes) {
        this.quizzes = quizzes;
    }
}
