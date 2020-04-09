package jsf.managedbean;

import ejb.session.stateless.DiscountCodeSessionBeanLocal;
import entity.DiscountCode;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import util.exception.DiscountCodeAlreadyExpiredException;
import util.exception.DiscountCodeExistException;
import util.exception.DiscountCodeNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author tjle2
 */
@Named(value = "flashCouponManagementManagedBean")
@ViewScoped

public class FlashCouponManagementManagedBean implements Serializable {

    @EJB(name = "DiscountCodeSessionBeanLocal")
    private DiscountCodeSessionBeanLocal discountCodeSessionBeanLocal;

    private List<DiscountCode> discountCodes;

    private Date dateTimeNow;
    private Date dateToday;
    private List<String> colours;

    private DiscountCode newDiscountCode;

    private DiscountCode discountCodeToView;
    private DiscountCode discountCodeToUpdate;

    private String selectedFilter;

    public FlashCouponManagementManagedBean() {

        newDiscountCode = new DiscountCode();
        dateTimeNow = new Date();
        dateToday = new Date();
        dateToday.setHours(0);
        dateToday.setMinutes(0);
        dateToday.setSeconds(0);

        colours = new ArrayList<>();
        colours.add("#F50057");
        colours.add("#26A69A");
        colours.add("#FFC400");
        colours.add("#536DFE");
        colours.add("#7C4DFF");
        colours.add("#00C853");
        colours.add("#FF7043");
        colours.add("#E53935");

        selectedFilter = "Active";
    }

    @PostConstruct
    public void postConstruct() {

        setDiscountCodes(discountCodeSessionBeanLocal.retrieveAllActiveDiscountCodes());
    }

    public void createNewFlashCoupon(ActionEvent event) {

        try {
            Long newDiscountCodeId = discountCodeSessionBeanLocal.createNewDiscountCode(getNewDiscountCode());
            getDiscountCodes().add(getNewDiscountCode());

            setNewDiscountCode(new DiscountCode());

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "New flash coupon created successfully (Discount Code ID: " + newDiscountCodeId + ")", null));

        } catch (DiscountCodeExistException | InputDataValidationException | UnknownPersistenceException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred creating new flash coupon: " + ex.getMessage(), null));
        }
    }

    public void updateFlashCoupon(ActionEvent event) {

        try {
            discountCodeSessionBeanLocal.updateDiscountCode(getDiscountCodeToUpdate());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Flash discount updated successfully", null));
        } catch (DiscountCodeAlreadyExpiredException | DiscountCodeNotFoundException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred updating the flash coupon: " + ex.getMessage(), null));
        }
    }

    public void deleteFlashCoupon(ActionEvent event) {

        try {
            DiscountCode discountCodeToDelete = (DiscountCode) event.getComponent().getAttributes().get("discountCodeToDelete");
            discountCodeSessionBeanLocal.deleteDiscountCode(discountCodeToDelete.getDiscountCodeId());
            getDiscountCodes().remove(discountCodeToDelete);

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Flash discount deleted successfully", null));
        } catch (DiscountCodeNotFoundException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred deleting the flash coupon: " + ex.getMessage(), null));
        }
    }

    public long calculateTimerTime(DiscountCode discountCode) {

        return (discountCode.getExpiryDate().getTime() - dateTimeNow.getTime()) / 1000;
    }

    public String getColour() {

        return colours.get((int) (Math.random() * 8));
    }

    private String generateRandomCode() {

        while (true) {

            String generatedString = "";

            try {
                int leftLimit = 48; // numeral '0'
                int rightLimit = 122; // letter 'z'
                int targetStringLength = 6;
                Random random = new Random();

                generatedString = random.ints(leftLimit, rightLimit + 1)
                        .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                        .limit(targetStringLength)
                        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                        .toString()
                        .toUpperCase();

                discountCodeSessionBeanLocal.retrieveDiscountCodeByDiscountCodeName(generatedString);
            } catch (DiscountCodeNotFoundException ex) {
                return generatedString;
            }
        }
    }

    public void generateRandomCodeForNewCoupon() {

        newDiscountCode.setDiscountCode(generateRandomCode());
    }

    public void generateRandomCodeForExistingCoupon() {

        discountCodeToUpdate.setDiscountCode(generateRandomCode());
    }

    public void doFilter() {

        if (selectedFilter.equals("Active")) {
            setDiscountCodes(discountCodeSessionBeanLocal.retrieveAllActiveDiscountCodes());
        } else if (selectedFilter.equals("Past")) {
            setDiscountCodes(discountCodeSessionBeanLocal.retrieveAllPastDiscountCodes());
        }
    }

    public List<DiscountCode> getDiscountCodes() {
        return discountCodes;
    }

    public void setDiscountCodes(List<DiscountCode> discountCodes) {
        this.discountCodes = discountCodes;
    }

    public Date getDateTimeNow() {
        return dateTimeNow;
    }

    public void setDateTimeNow(Date dateTimeNow) {
        this.dateTimeNow = dateTimeNow;
    }

    public Date getDateToday() {
        return dateToday;
    }

    public void setDateToday(Date dateToday) {
        this.dateToday = dateToday;
    }

    public DiscountCode getNewDiscountCode() {
        return newDiscountCode;
    }

    public void setNewDiscountCode(DiscountCode newDiscountCode) {
        this.newDiscountCode = newDiscountCode;
    }

    public DiscountCode getDiscountCodeToView() {
        return discountCodeToView;
    }

    public void setDiscountCodeToView(DiscountCode discountCodeToView) {
        this.discountCodeToView = discountCodeToView;
    }

    public DiscountCode getDiscountCodeToUpdate() {
        return discountCodeToUpdate;
    }

    public void setDiscountCodeToUpdate(DiscountCode discountCodeToUpdate) {
        this.discountCodeToUpdate = discountCodeToUpdate;
    }

    public List<String> getColours() {
        return colours;
    }

    public void setColours(List<String> colours) {
        this.colours = colours;
    }

    public String getSelectedFilter() {
        return selectedFilter;
    }

    public void setSelectedFilter(String selectedFilter) {
        this.selectedFilter = selectedFilter;
    }

}
