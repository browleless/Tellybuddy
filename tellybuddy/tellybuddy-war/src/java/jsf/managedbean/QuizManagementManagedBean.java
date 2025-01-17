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
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.view.ViewScoped;
import org.primefaces.component.tabview.Tab;
import org.primefaces.component.tabview.TabView;
import org.primefaces.event.TabChangeEvent;
import util.exception.CreateNewQuizException;
import util.exception.DeleteQuizException;
import util.exception.QuizNameExistException;
import util.exception.QuizNotFoundException;
import util.exception.UpdateQuizException;

/**
 *
 * @author tjle2
 */
@Named(value = "quizManagementManagedBean")
@ViewScoped

public class QuizManagementManagedBean implements Serializable {

    @EJB
    private QuizSessionBeanLocal quizSessionBeanLocal;

    private String quizName;
    private Date quizStartDateTime;
    private Date quizEndDateTime;

    private List<Question> questions;

    private Question newQuestion;

    private Question questionToEdit;

    private String selectedFilter;

    private Date dateTimeNow;
    private Date dateToday;

    private List<Quiz> quizzes;
    private List<Quiz> filteredQuizzes;

    private Quiz quizToUpdate;

    private int currentTabIndex;

    public QuizManagementManagedBean() {

        dateTimeNow = new Date();
        dateToday = new Date();
        dateToday.setHours(0);
        dateToday.setMinutes(0);
        dateToday.setSeconds(0);
        selectedFilter = "Active";
        currentTabIndex = 0;
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
    }

    public void addNewQuestion(ActionEvent event) {

        String quizType = (String) event.getComponent().getAttributes().get("quizType");

        if (quizType.equals("new")) {
            getQuestions().add(getNewQuestion());
        } else if (quizType.equals("existing")) {
            getQuizToUpdate().getQuestions().add(getNewQuestion());
        }

        setNewQuestion(new Question());
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
            Quiz newQuiz = new Quiz(quizName, quizStartDateTime, quizEndDateTime, questions.size());
            newQuiz.setQuestions(questions);
            Long quizId = quizSessionBeanLocal.createNewQuiz(newQuiz);
            if ((newQuiz.getOpenDate().before(dateTimeNow) && selectedFilter.equals("Active")) || (newQuiz.getOpenDate().after(dateTimeNow) && selectedFilter.equals("Upcoming"))) {
                quizzes.add(quizSessionBeanLocal.retrieveQuizByQuizId(quizId));
            }
            initialiseState();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "New Quiz created successfully!", null));
        } catch (CreateNewQuizException | QuizNameExistException | QuizNotFoundException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while publishing the quiz: " + ex.getMessage(), null));
        }
    }

    public void deleteQuiz(ActionEvent event) {

        try {
            Quiz quizToDelete = (Quiz) event.getComponent().getAttributes().get("quizToDelete");
            quizSessionBeanLocal.deleteQuiz(quizToDelete);
            quizzes.remove(quizToDelete);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Quiz deleted successfully!", null));
        } catch (DeleteQuizException | QuizNotFoundException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while deleting the quiz: " + ex.getMessage(), null));
        }
    }

    public void deleteExistingQuestion(ActionEvent event) {

        Question existingQuestionToDelete = (Question) event.getComponent().getAttributes().get("existingQuestionToDelete");
        quizToUpdate.getQuestions().remove(existingQuestionToDelete);

        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Question deleted successfully!", null));
    }

    public void updateQuiz(ActionEvent event) {

        try {
            quizToUpdate.setUnitsWorth(quizToUpdate.getQuestions().size());
            quizSessionBeanLocal.updateQuiz(quizToUpdate);
            if ((quizToUpdate.getOpenDate().before(dateTimeNow) && selectedFilter.equals("Upcoming")) || (quizToUpdate.getOpenDate().after(dateTimeNow) && selectedFilter.equals("Active"))) {
                quizzes.remove(quizToUpdate);
            }
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Quiz updated successfully!", null));
        } catch (QuizNotFoundException | UpdateQuizException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while updating the quiz: " + ex.getMessage(), null));
        }
    }

    public long calculateTimerTime(Quiz quiz) {

        if (quiz.getOpenDate().before(dateTimeNow) && quiz.getExpiryDate().after(dateTimeNow)) {
            return (quiz.getExpiryDate().getTime() - dateTimeNow.getTime()) / 1000;
        } else if (quiz.getOpenDate().after(dateTimeNow)) {
            return (quiz.getOpenDate().getTime() - dateTimeNow.getTime()) / 1000;
        } else {
            return (dateTimeNow.getTime() - quiz.getExpiryDate().getTime()) / 1000;
        }
    }

    public void doFilter() {

        if (selectedFilter.equals("Active")) {
            setQuizzes(quizSessionBeanLocal.retrieveActiveQuizzes());
        } else if (selectedFilter.equals("Upcoming")) {
            setQuizzes(quizSessionBeanLocal.retrieveUpcomingQuizzes());
        } else if (selectedFilter.equals("Past")) {
            setQuizzes(quizSessionBeanLocal.retirevePastQuizzes());
        }
    }

    public void setQuestionAnswer(AjaxBehaviorEvent event) {

        int answerIndex = (int) event.getComponent().getAttributes().get("answerIndex");
        String questionType = (String) event.getComponent().getAttributes().get("questionType");

        if (questionType.equals("new")) {
            for (int i = 0; i < newQuestion.getAnswers().size(); i++) {
                if (i != answerIndex || (i == answerIndex && newQuestion.getAnswers().get(i).getIsAnswer() == false)) {
                    newQuestion.getAnswers().get(i).setIsAnswer(Boolean.FALSE);
                }
            }
        } else if (questionType.equals("existing")) {
            for (int i = 0; i < questionToEdit.getAnswers().size(); i++) {
                if (i != answerIndex || (i == answerIndex && questionToEdit.getAnswers().get(i).getIsAnswer() == false)) {
                    questionToEdit.getAnswers().get(i).setIsAnswer(Boolean.FALSE);
                }
            }
        }
    }

    public void clearNewQuestion() {

        this.newQuestion = new Question();
    }

    public void onTabChange(TabChangeEvent event) {

        Tab activeTab = event.getTab();
        currentTabIndex = ((TabView) event.getSource()).getChildren().indexOf(activeTab);
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

    public int getCurrentTabIndex() {
        return currentTabIndex;
    }

    public void setCurrentTabIndex(int currentTabIndex) {
        this.currentTabIndex = currentTabIndex;
    }

}
