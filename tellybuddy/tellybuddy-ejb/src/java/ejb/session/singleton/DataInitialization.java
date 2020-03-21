/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.singleton;

import entity.Category;
import entity.Employee;
import entity.Plan;
import entity.Product;
import entity.Tag;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.enumeration.AccessRightEnum;

/**
 *
 * @author admin
 */
@Singleton
@LocalBean
@Startup
public class DataInitialization {

    @PersistenceContext(unitName = "tellybuddy-ejbPU")
    private EntityManager em;

    @PostConstruct
    public void postConstruct() {
        if (em.find(Employee.class, 1l) == null) {
            initialiseData();
        }
    }

    private void initialiseData() {

        Employee newEmployee = new Employee("manager", "password", "Default", "Manager", AccessRightEnum.MANAGER);
        em.persist(newEmployee);
        em.flush();

        newEmployee = new Employee("employee", "password", "Default", "Employee", AccessRightEnum.EMPLOYEE);
        em.persist(newEmployee);
        em.flush();

        Plan newPlan = new Plan("Saver 15", 15, BigDecimal.valueOf(25), BigDecimal.valueOf(2.5), Integer.valueOf(1500), Integer.valueOf(100), Integer.valueOf(100), null, null);
        em.persist(newPlan);
        em.flush();

        Category newCat1 = new Category("Cat A", "test");
        Tag newTag1 = new Tag("Testing");

        Product newProd = new Product("PROD001", "testing", "testing", BigDecimal.ONE, 20, 50);
        newProd.setCategory(newCat1);
        List<Tag> tags = new ArrayList<>();
        tags.add(newTag1);
        newProd.setTags(tags);
        em.persist(newProd);
        em.flush();
    }

}
