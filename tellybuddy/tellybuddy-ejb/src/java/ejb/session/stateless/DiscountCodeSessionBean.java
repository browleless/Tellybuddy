/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.DiscountCode;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.DiscountCodeAlreadyExpiredException;
import util.exception.DiscountCodeNotFoundException;

/**
 *
 * @author ngjin
 */
@Stateless
public class DiscountCodeSessionBean implements DiscountCodeSessionBeanLocal {
    
    @PersistenceContext(unitName = "tellybuddy-ejbPU")
    private EntityManager em;
    
    public DiscountCodeSessionBean() {
        
    }
    
    @Override
    public Long createNewDiscountCode(DiscountCode discountCode) {
        //add in bean validation?

        em.persist(discountCode);
        em.flush();
        
        return discountCode.getDiscountCodeId();
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
    public List<DiscountCode> retrieveAllDiscountCodes() {
        Query q = em.createQuery("SELECT dc FROM DiscountCode dc");
        
        return q.getResultList();
    }
    
    @Override
    public List<DiscountCode> retrieveAllActiveDiscountCodes() {
        Date current = new Date();
        
        Query q = em.createQuery("SELECT dc FROM DiscountCode dc WHERE dc.expiryDate <= :inCurrent");
        q.setParameter("inCurrent", current);
        
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
                //discountCodeToUpdate.setDiscountCode(dc.getDiscountCode());
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
}
