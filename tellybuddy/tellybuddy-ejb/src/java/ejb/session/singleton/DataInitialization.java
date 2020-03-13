/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.singleton;

import entity.Employee;
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
    }

}
