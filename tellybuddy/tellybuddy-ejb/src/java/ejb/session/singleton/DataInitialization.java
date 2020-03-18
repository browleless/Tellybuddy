/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.singleton;

import entity.Customer;
import entity.Employee;
import entity.Plan;
import java.math.BigDecimal;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
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
        if (em.find(Employee.class, 1l) == null && em.find(Customer.class, 1l) == null) {
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

        Customer customer = new Customer("customer1", "password1", "mafgfrk", "tan", Integer.valueOf(20), "asfdthcghjkaf", "428198", "mk323333tsk@gmail.com", null, null);
        em.persist(customer);
        em.flush();
         customer = new Customer("customer2", "password2", "mark", "tan", Integer.valueOf(20), "asfdsdfsafdsfsaf", "428198", "mktsk@gmail.com", null, null);
        em.persist(customer);
        em.flush();
        Plan newPlan = new Plan("Saver 15", 15, BigDecimal.valueOf(25), BigDecimal.valueOf(2.5), Integer.valueOf(1500), Integer.valueOf(100), Integer.valueOf(100), null, null);

        em.persist(newPlan);

        em.flush();
    }

}
