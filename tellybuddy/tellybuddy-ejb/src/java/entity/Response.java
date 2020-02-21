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
public class Response implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long responseId;
    
    @Column(nullable = false)
    @NotNull
    private Boolean isCorrect;
    
    @OneToOne(optional = false)
    @JoinColumn(nullable = false)
    private Question question;
    
    @OneToOne(optional = false)
    @JoinColumn(nullable = false)
    private Answer answer;

    public Response() {
    }

    public Response(Boolean isCorrect) {
        this();
        this.isCorrect = isCorrect;
    }

    public Long getResponseId() {
        return responseId;
    }

    public void setResponseId(Long responseId) {
        this.responseId = responseId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (responseId != null ? responseId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the responseId fields are not set
        if (!(object instanceof Response)) {
            return false;
        }
        Response other = (Response) object;
        if ((this.responseId == null && other.responseId != null) || (this.responseId != null && !this.responseId.equals(other.responseId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Response[ id=" + responseId + " ]";
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
