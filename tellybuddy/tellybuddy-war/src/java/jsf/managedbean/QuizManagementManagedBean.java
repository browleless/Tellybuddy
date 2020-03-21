package jsf.managedbean;

import ejb.session.stateless.QuizSessionBeanLocal;
import entity.Answer;
import entity.Question;
import entity.Quiz;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import util.exception.DeleteAnswerException;
import util.exception.DeleteQuestionException;
import util.exception.DeleteQuizException;
import util.exception.QuestionNotFoundException;
import util.exception.QuizNameExistException;
import util.exception.QuizNotFoundException;

/**
 *
 * @author tjle2
 */
@Named(value = "quizManagementManagedBean")
@ViewScoped

public class QuizManagementManagedBean implements Serializable {

    public int getQuestionToEditIndex() {
        return questionToEditIndex;
    }

    public void setQuestionToEditIndex(int questionToEditIndex) {
        this.questionToEditIndex = questionToEditIndex;
    }

    @EJB
    private QuizSessionBeanLocal quizSessionBeanLocal;

    private String quizName;
    private Date quizStartDateTime;
    private Date quizEndDateTime;
    private Integer quizUnitsWorth;

    private List<Question> questions;
    private int answerIndex;

    private Question newQuestion;

    private Question questionToEdit;
    private int questionToEditIndex;

    private String selectedFilter;

    private Date dateTimeNow;
    private Date dateToday;

    private List<Quiz> quizzes;
    private List<Quiz> filteredQuizzes;

    private Quiz quizToUpdate;

    public QuizManagementManagedBean() {

        dateTimeNow = new Date();
        dateToday = new Date();
        dateToday.setHours(0);
        dateToday.setMinutes(0);
        dateToday.setSeconds(0);
        selectedFilter = "Active";
        initialiseState();
    }

    @PostConstruct
    public void postConstruct() {

        setQuizzes(quizSessionBeanLocal.retrieveActiveQuizzes());
    }

    private void initialiseState() {
        setQuestions(new ArrayList<>());
        setNewQuestion(new Question());
        quizName = null;
        quizStartDateTime = null;
        quizEndDateTime = null;
        quizUnitsWorth = null;
    }

    public void addNewQuestion(ActionEvent event) {

        newQuestion.getAnswers().get(answerIndex).setIsAnswer(Boolean.TRUE);

        getQuestions().add(getNewQuestion());

        setNewQuestion(new Question());
        setAnswerIndex(-1);
    }

    public void addNewAnswer(ActionEvent event) {

        String questionType = (String) event.getComponent().getAttributes().get("questionType");
        Answer answer = new Answer("");

        if (questionType.equals("new")) {
            getNewQuestion().getAnswers().add(answer);
        } else if (questionType.equals("existing")) {
            getQuestionToEdit().getAnswers().add(answer);
        }
    }

    public void deleteQuestion(ActionEvent event) {

        Question questionToDelete = (Question) event.getComponent().getAttributes().get("questionToDelete");

        getQuestions().remove(questionToDelete);

        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Question removed successfully!", null));
    }

    public void deleteAnswer(ActionEvent event) {

        Answer answerToDelete = (Answer) event.getComponent().getAttributes().get("answerToDelete");
        String questionType = (String) event.getComponent().getAttributes().get("questionType");

        if (questionType.equals("new")) {
            getNewQuestion().getAnswers().remove(answerToDelete);
        } else if (questionType.equals("existing")) {
            getQuestionToEdit().getAnswers().remove(answerToDelete);
        }

        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Quiz answer removed successfully!", null));
    }

    public void publishQuiz(ActionEvent event) {

        try {
            Quiz newQuiz = new Quiz(quizName, quizStartDateTime, quizEndDateTime, quizUnitsWorth);
            newQuiz.setQuestions(questions);
            Long quizId = quizSessionBeanLocal.createNewQuiz(newQuiz);
            if ((newQuiz.getOpenDate().before(dateTimeNow) && selectedFilter.equals("Active")) || (newQuiz.getOpenDate().after(dateTimeNow) && selectedFilter.equals("Upcoming"))) {
                quizzes.add(quizSessionBeanLocal.retrieveQuizByQuizId(quizId));
            }
            initialiseState();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "New Quiz created successfully!", null));
        } catch (QuizNotFoundException | QuestionNotFoundException | QuizNameExistException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while publishing the quiz: " + ex.getMessage(), null));
        }
    }

    public void deleteQuiz(ActionEvent event) {

        try {
            Quiz quizToDelete = (Quiz) event.getComponent().getAttributes().get("quizToDelete");
            quizSessionBeanLocal.deleteQuiz(quizToDelete);
            quizzes.remove(quizToDelete);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Quiz deleted successfully!", null));
        } catch (DeleteAnswerException | DeleteQuestionException | DeleteQuizException | QuizNotFoundException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while deleting the quiz: " + ex.getMessage(), null));
        }
    }

    public void amendQuestion(ActionEvent event) {

        for (Answer answer : questionToEdit.getAnswers()) {
            answer.setIsAnswer(Boolean.FALSE);
        }

        questionToEdit.getAnswers().get(answerIndex).setIsAnswer(Boolean.TRUE);

        getQuestions().set(questionToEditIndex, questionToEdit);

        setAnswerIndex(-1);
    }

    public void deleteExistingQuestion(ActionEvent event) {
        
        System.out.println(quizToUpdate.getQuestions());

        Question existingQuestionToDelete = (Question) event.getComponent().getAttributes().get("existingQuestionToDelete");
        
        System.out.println(existingQuestionToDelete);
        //quizToUpdate.getQuestions().remove(existingQuestionToDelete);

        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Question deleted successfully!", null));
    }

    public long calculateTimerTime(Quiz quiz) {

        if (quiz.getOpenDate().before(new Date())) {
            return (quiz.getExpiryDate().getTime() - dateTimeNow.getTime()) / 1000;
        } else {
            return (quiz.getOpenDate().getTime() - dateTimeNow.getTime()) / 1000;
        }
    }

    public void doFilter() {

        if (selectedFilter.equals("Active")) {
            setQuizzes(quizSessionBeanLocal.retrieveActiveQuizzes());
        } else if (selectedFilter.equals("Upcoming")) {
            setQuizzes(quizSessionBeanLocal.retrieveUpcomingQuizzes());
        }
    }

    public String getQuizName() {
        return quizName;
    }

    public void setQuizName(String quizName) {
        this.quizName = quizName;
    }

    public Date getQuizStartDateTime() {
        return quizStartDateTime;
    }

    public void setQuizStartDateTime(Date quizStartDateTime) {
        this.quizStartDateTime = quizStartDateTime;
    }

    public Date getQuizEndDateTime() {
        return quizEndDateTime;
    }

    public void setQuizEndDateTime(Date quizEndDateTime) {
        this.quizEndDateTime = quizEndDateTime;
    }

    public Integer getQuizUnitsWorth() {
        return quizUnitsWorth;
    }

    public void setQuizUnitsWorth(Integer quizUnitsWorth) {
        this.quizUnitsWorth = quizUnitsWorth;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public Question getNewQuestion() {
        return newQuestion;
    }

    public void setNewQuestion(Question newQuestion) {
        this.newQuestion = newQuestion;
    }

    public Date getDateTimeNow() {
        return dateTimeNow;
    }

    public void setDateTimeNow(Date dateTimeNow) {
        this.dateTimeNow = dateTimeNow;
    }

    public int getAnswerIndex() {
        return answerIndex;
    }

    public void setAnswerIndex(int answerIndex) {
        this.answerIndex = answerIndex;
    }

    public Question getQuestionToEdit() {
        return questionToEdit;
    }

    public void setQuestionToEdit(Question questionToEdit) {
        this.questionToEdit = questionToEdit;
    }

    public String getSelectedFilter() {
        return selectedFilter;
    }

    public void setSelectedFilter(String selectedFilter) {
        this.selectedFilter = selectedFilter;
    }

    public Date getDateToday() {
        return dateToday;
    }

    public void setDateToday(Date dateToday) {
        this.dateToday = dateToday;
    }

    public List<Quiz> getQuizzes() {
        return quizzes;
    }

    public void setQuizzes(List<Quiz> quizzes) {
        this.quizzes = quizzes;
    }

    public List<Quiz> getFilteredQuizzes() {
        return filteredQuizzes;
    }

    public void setFilteredQuizzes(List<Quiz> filteredQuizzes) {
        this.filteredQuizzes = filteredQuizzes;
    }

    public Quiz getQuizToUpdate() {
        return quizToUpdate;
    }

    public void setQuizToUpdate(Quiz quizToUpdate) {
        this.quizToUpdate = quizToUpdate;
    }

}
