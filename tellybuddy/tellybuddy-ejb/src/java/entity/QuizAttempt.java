package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 *
 * @author tjle2
 */
@Entity
public class QuizAttempt implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long quizAttemptId;
    
    @Column(nullable = false)
    @NotNull
    @Positive
    @Min(0)
    private Integer score;
    
    @Column(nullable = false)
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date completedDate;
    
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private Customer customer;
    
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private Quiz quiz;
    
    @OneToMany
    private List<QuizResponse> quizResponses;

    public QuizAttempt() {
        this.quizResponses = new ArrayList<>();
    }

    public QuizAttempt(Integer score, Date completedDate) {
        this();
        this.score = score;
        this.completedDate = completedDate;
    }
    
    public Long getQuizAttemptId() {
        return quizAttemptId;
    }

    public void setQuizAttemptId(Long quizAttemptId) {
        this.quizAttemptId = quizAttemptId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (quizAttemptId != null ? quizAttemptId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the quizAttemptId fields are not set
        if (!(object instanceof QuizAttempt)) {
            return false;
        }
        QuizAttempt other = (QuizAttempt) object;
        if ((this.quizAttemptId == null && other.quizAttemptId != null) || (this.quizAttemptId != null && !this.quizAttemptId.equals(other.quizAttemptId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.QuizAttempt[ id=" + quizAttemptId + " ]";
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Date getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(Date completedDate) {
        this.completedDate = completedDate;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public List<QuizResponse> getQuizResponses() {
        return quizResponses;
    }

    public void setQuizResponses(List<QuizResponse> quizResponses) {
        this.quizResponses = quizResponses;
    }
    
}
