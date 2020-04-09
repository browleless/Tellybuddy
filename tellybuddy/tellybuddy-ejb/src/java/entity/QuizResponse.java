package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

/**
 *
 * @author tjle2
 */
@Entity
public class QuizResponse implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long quizResponseId;

    @Column(nullable = false)
    @NotNull
    private Boolean isCorrect;

    @OneToOne(optional = false)
    @JoinColumn(nullable = false)
    private Question question;

    @OneToOne(optional = false)
    @JoinColumn(nullable = false)
    private Answer answer;

    public QuizResponse() {
        this.isCorrect = Boolean.FALSE;
    }

    public Long getQuizResponseId() {
        return quizResponseId;
    }

    public void setQuizResponseId(Long quizResponseId) {
        this.quizResponseId = quizResponseId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (quizResponseId != null ? quizResponseId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the quizResponseId fields are not set
        if (!(object instanceof QuizResponse)) {
            return false;
        }
        QuizResponse other = (QuizResponse) object;
        if ((this.quizResponseId == null && other.quizResponseId != null) || (this.quizResponseId != null && !this.quizResponseId.equals(other.quizResponseId)) || (this.quizResponseId == null && other.quizResponseId == null && !this.question.equals(other.question))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Response[ id=" + quizResponseId + " ]";
    }

    public Boolean getIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(Boolean isCorrect) {
        this.isCorrect = isCorrect;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Answer getAnswer() {
        return answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

}
