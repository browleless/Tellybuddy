/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.DiscountCode;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exception.DiscountCodeAlreadyExpiredException;
import util.exception.DiscountCodeExistException;
import util.exception.DiscountCodeNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author ngjin
 */
@Stateless
public class DiscountCodeSessionBean implements DiscountCodeSessionBeanLocal {

    @PersistenceContext(unitName = "tellybuddy-ejbPU")
    private EntityManager em;

    private final ValidatorFactory validatorFactory;

    private final Validator validator;

    public DiscountCodeSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public Long createNewDiscountCode(DiscountCode discountCode) throws UnknownPersistenceException, DiscountCodeExistException,
            InputDataValidationException {

        //check uniqueness of the planName using bean validator before persisting
        Set<ConstraintViolation<DiscountCode>> constraintViolations = validator.validate(discountCode);

        if (constraintViolations.isEmpty()) {
            try {
                em.persist(discountCode);
                em.flush();

                return discountCode.getDiscountCodeId();
            } catch (PersistenceException ex) {
                if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                        throw new DiscountCodeExistException("Discount Code cannot be created as there is already a discount code created with the same name!");
                    } else {
                        throw new UnknownPersistenceException(ex.getMessage());
                    }
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            }
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }

    @Override
    public DiscountCode retrieveDiscountCodeByDiscountCodeId(Long discountCodeId) throws DiscountCodeNotFoundException {
        DiscountCode dc = em.find(DiscountCode.class, discountCodeId);

        if (dc != null) {
            return dc;
        } else {
            throw new DiscountCodeNotFoundException("Discount Code " + discountCodeId + " does not exist!");
        }
    }

    @Override
    public DiscountCode retrieveDiscountCodeByDiscountCodeName(String discountCodeName) throws DiscountCodeNotFoundException {

        try {
            Query q = em.createQuery("SELECT dc FROM DiscountCode dc WHERE dc.discountCode = :inDiscountCodeName");
            q.setParameter("inDiscountCodeName", discountCodeName);

            DiscountCode discountCode = (DiscountCode) q.getSingleResult();

            return discountCode;
        } catch (NoResultException ex) {
            throw new DiscountCodeNotFoundException("Discount Code '" + discountCodeName + "' does not exist!");
        }
    }

    @Override
    public List<DiscountCode> retrieveAllDiscountCodes() {
        Query q = em.createQuery("SELECT dc FROM DiscountCode dc");

        return q.getResultList();
    }

    @Override
    public List<DiscountCode> retrieveAllActiveDiscountCodes() {

        Query q = em.createQuery("SELECT dc FROM DiscountCode dc WHERE dc.expiryDate > CURRENT_TIMESTAMP ORDER BY dc.expiryDate");

        return q.getResultList();
    }

    @Override
    public List<DiscountCode> retrieveAllPastDiscountCodes() {

        Query q = em.createQuery("SELECT dc FROM DiscountCode dc WHERE dc.expiryDate < CURRENT_TIMESTAMP ORDER BY dc.expiryDate");

        return q.getResultList();
    }
    
    @Override
    public List<DiscountCode> retrieveAllUsableActiveDiscountCodes() {

        Query q = em.createQuery("SELECT dc FROM DiscountCode dc WHERE dc.expiryDate > CURRENT_TIMESTAMP AND NOT EXISTS (SELECT t FROM Transaction t WHERE dc = t.discountCode) ORDER BY dc.expiryDate");

        return q.getResultList();
    }

    @Override
    public void updateDiscountCode(DiscountCode dc) throws DiscountCodeAlreadyExpiredException, DiscountCodeNotFoundException {
        if (dc.getDiscountCodeId() != null) {
            DiscountCode discountCodeToUpdate = retrieveDiscountCodeByDiscountCodeId(dc.getDiscountCodeId());

            //check if announcement has already expired
            Date current = new Date();

            if (discountCodeToUpdate.getExpiryDate().before(current)) {
                throw new DiscountCodeAlreadyExpiredException("Discount code cannot be updated as it expired!");
            } else {
                discountCodeToUpdate.setDiscountCode(dc.getDiscountCode());
                discountCodeToUpdate.setDiscountRate(dc.getDiscountRate());
                discountCodeToUpdate.setExpiryDate(dc.getExpiryDate());
            }
        } else {
            throw new DiscountCodeNotFoundException("Discount Code " + dc.getDiscountCodeId() + " does not exist!");
        }
    }

    @Override
    public void deleteDiscountCode(Long discountCodeId) throws DiscountCodeNotFoundException {

        DiscountCode dcToDelete = retrieveDiscountCodeByDiscountCodeId(discountCodeId);

        em.remove(dcToDelete);
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<DiscountCode>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }
}
