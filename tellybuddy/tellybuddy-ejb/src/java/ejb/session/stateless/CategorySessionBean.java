/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Category;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author kaikai
 */
@Stateless
public class CategorySessionBean implements CategorySessionBeanLocal {

    @PersistenceContext(unitName = "tellybuddy-ejbPU")
    private EntityManager em;

    public Long createNewCategory(Category newCategory) {
        em.persist(newCategory);
        em.flush();

        return newCategory.getCategoryId();
    }

    public List<Category> retrieveAllCategory() {
        Query q = em.createQuery("SELECT c FROM Category c");

        return q.getResultList();
    }
    
    

}
