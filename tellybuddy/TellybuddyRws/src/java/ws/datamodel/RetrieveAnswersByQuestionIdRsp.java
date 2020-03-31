package ws.datamodel;

import entity.Answer;
import java.util.List;

/**
 *
 * @author tjle2
 */
public class RetrieveAnswersByQuestionIdRsp {

    private List<Answer> answers;

    public RetrieveAnswersByQuestionIdRsp() {
    }

    public RetrieveAnswersByQuestionIdRsp(List<Answer> answers) {
        this.answers = answers;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }
}
