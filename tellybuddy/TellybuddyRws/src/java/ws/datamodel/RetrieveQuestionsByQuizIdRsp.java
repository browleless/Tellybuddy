package ws.datamodel;

import entity.Question;
import java.util.List;

/**
 *
 * @author tjle2
 */
public class RetrieveQuestionsByQuizIdRsp {

    private List<Question> questions;

    public RetrieveQuestionsByQuizIdRsp() {
    }

    public RetrieveQuestionsByQuizIdRsp(List<Question> questions) {
        this.questions = questions;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }
}
