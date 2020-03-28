/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.singleton;

import ejb.session.stateless.CustomerSessionBeanLocal;
import ejb.session.stateless.FamilyGroupSessionBeanLocal;
import ejb.session.stateless.SubscriptionSessonBeanLocal;
import entity.Customer;
import entity.Announcement;

import entity.Category;

import entity.Employee;
import entity.FamilyGroup;
import entity.PhoneNumber;
import entity.Plan;
import entity.Subscription;
import java.util.Date;
import entity.Product;
import entity.Tag;
import java.math.BigDecimal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.enumeration.AccessRightEnum;
import util.enumeration.AnnouncementRecipientEnum;
import util.enumeration.SubscriptionStatusEnum;
import util.exception.CustomerAlreadyInFamilyGroupException;
import util.exception.CustomersDoNotHaveSameAddressOrPostalCodeException;
import util.exception.FamilyGroupReachedLimitOf5MembersException;

/**
 *
 * @author admin
 */
@Singleton
@LocalBean
@Startup
public class DataInitialization {

    @EJB
    private SubscriptionSessonBeanLocal subscriptionSessonBean;

    @EJB
    private CustomerSessionBeanLocal customerSessionBeanLocal;

    @EJB
    private FamilyGroupSessionBeanLocal familyGroupSessionBeanLocal;

    @PersistenceContext(unitName = "tellybuddy-ejbPU")
    private EntityManager em;
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");

    @PostConstruct
    public void postConstruct() {
        if (em.find(Employee.class, 1l) == null && em.find(Customer.class, 1l) == null) {
            initialiseData();
        }

    }

    private void initialiseData() {
        try {

            this.createPhoneNumbers();
            this.createCustomers();

            Employee newEmployee = new Employee("manager", "password", "Default", "Manager", AccessRightEnum.MANAGER, "path");
            em.persist(newEmployee);
            em.flush();

            newEmployee = new Employee("employee", "password", "Default", "Employee", AccessRightEnum.EMPLOYEE, "path");
            em.persist(newEmployee);
            em.flush();

            Plan newPlan = new Plan("Saver 15", 15, BigDecimal.valueOf(25), BigDecimal.valueOf(2.5), Integer.valueOf(1500), Integer.valueOf(100), Integer.valueOf(100), null, null);
            em.persist(newPlan);
            em.flush();
            try {
                Subscription subscription = new Subscription(10, 10, 10);
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.MONTH, -2);
                subscription.setSubscriptionStartDate(cal.getTime());
                subscriptionSessonBean.createNewSubscription(subscription, 1l, 1l, 1l);
                subscription.setSubscriptionStatusEnum(SubscriptionStatusEnum.TERMINATING);

                subscription = new Subscription(20, 5, 5);
                cal = Calendar.getInstance();
                cal.add(Calendar.MONTH, -2);
                subscription.setSubscriptionStartDate(cal.getTime());
                subscriptionSessonBean.createNewSubscription(subscription, 1l, 2l, 2l);

                subscription = new Subscription(30, 0, 0);
                cal = Calendar.getInstance();
                cal.add(Calendar.MONTH, -1);
                subscription.setSubscriptionStartDate(cal.getTime());
                subscriptionSessonBean.createNewSubscription(subscription, 1l, 2l, 3l);

                subscription = new Subscription(15, 5, 10);
                cal = Calendar.getInstance();
                cal.add(Calendar.MONTH, -1);
                subscription.setSubscriptionStartDate(cal.getTime());
                subscriptionSessonBean.createNewSubscription(subscription, 1l, 3l, 4l);

            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }

            Announcement newAnnouncement = new Announcement("Flash deal", "content", formatter.parse("16-Mar-2020 23:37:50"), formatter.parse("23-Mar-2020 23:37:50"), AnnouncementRecipientEnum.CUSTOMER);
            em.persist(newAnnouncement);
            em.flush();
            newAnnouncement = new Announcement("New year deal", "content", formatter.parse("16-Jan-2020 23:37:50"), formatter.parse("23-Jan-2020 23:37:50"), AnnouncementRecipientEnum.CUSTOMER);
            em.persist(newAnnouncement);
            em.flush();
            newAnnouncement = new Announcement("Update password", "content", formatter.parse("16-Mar-2020 23:37:50"), formatter.parse("23-Dec-2020 23:37:50"), AnnouncementRecipientEnum.EMPLOYEES);
            em.persist(newAnnouncement);
            em.flush();
            newAnnouncement = new Announcement("Internal discount", "content", formatter.parse("16-Mar-2019 23:37:50"), formatter.parse("23-Dec-2019 23:37:50"), AnnouncementRecipientEnum.EMPLOYEES);
            em.persist(newAnnouncement);
            em.flush();

            Category newCat1 = new Category("Cat A", "test");
            Tag newTag1 = new Tag("Testing");
            em.persist(newCat1);
            em.flush();
            em.persist(newTag1);
            em.flush();

            Product newProd = new Product("SKU001", "testing", "testing", BigDecimal.ONE, 20, 50, "path");
            newProd.setCategory(newCat1);
            List<Tag> tags = new ArrayList<>();
            tags.add(newTag1);
            newProd.setTags(tags);
            em.persist(newProd);
            em.flush();

            Customer customer1 = em.find(Customer.class, 1l);
            Customer customer2 = em.find(Customer.class, 2l);
            Customer customer3 = em.find(Customer.class, 3l);
            Customer customer4 = em.find(Customer.class, 4l);
            Customer customer5 = em.find(Customer.class, 5l);
            FamilyGroup fg1 = new FamilyGroup("IS3106 Warriors");
            fg1.getCustomers().add(customer1);
            fg1.getCustomers().add(customer2);
            customer1.setFamilyGroup(fg1);
            customer2.setFamilyGroup(fg1);
            em.persist(fg1);
            em.flush();
            FamilyGroup fg2 = new FamilyGroup("I lOVE NUS");
            fg2.getCustomers().add(customer3);
            fg2.getCustomers().add(customer4);
            fg2.getCustomers().add(customer5);
            customer3.setFamilyGroup(fg2);
            customer4.setFamilyGroup(fg2);
            customer5.setFamilyGroup(fg2);
            em.persist(fg2);
            em.flush();
        } catch (ParseException ex) {
            Logger.getLogger(DataInitialization.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void createCustomers() {
        Calendar timeNow = Calendar.getInstance();
        timeNow.add(Calendar.MONTH, -2);
        Customer customer = new Customer("customer1", "password1", "Mark", "Tan", Integer.valueOf(20), "This is my address", "428198", "marktan@gmail.com", "S9709388A", "./nricPhoto.jpg", timeNow.getTime(),"mt.jpg");
        em.persist(customer);
        em.flush();

        customer = new Customer("customer2", "password2", "Jun Le", "Tay", Integer.valueOf(20), "This is my address", "428198", "tayjl@gmail.com", "S9941179A", null, timeNow.getTime(), "tayjl.jpg");
        em.persist(customer);
        em.flush();

        timeNow.add(Calendar.MONTH, 1);
        customer = new Customer("customer3", "password2", "Jing Wen", "Ng", Integer.valueOf(20), "This is my address", "117417", "ngJW@gmail.com", "S9841379A", null, timeNow.getTime(), "jw.jpg");
        em.persist(customer);
        em.flush();

        customer = new Customer("customer4", "password4", "Kai Xin", "Zhu", Integer.valueOf(20), "This is my address", "117417", "kathareverusa@gmail.com", "S9641179A", null, timeNow.getTime(), "kx.jpg");
        em.persist(customer);
        em.flush();
        timeNow.add(Calendar.MONTH, 1);
        customer = new Customer("customer5", "password5", "Wee kek", "Tan", Integer.valueOf(20), "This is my address", "117417", "tanwk@gmail.com", "S4041179A", null, timeNow.getTime(), "tanwk.jpg");
        em.persist(customer);
        em.flush();

    }

    private void createPhoneNumbers() {
        PhoneNumber phoneno = new PhoneNumber("96820119");
        em.persist(phoneno);
        em.flush();
        phoneno = new PhoneNumber("54322345");
        em.persist(phoneno);
        em.flush();
        phoneno = new PhoneNumber("07978291");
        em.persist(phoneno);
        em.flush();
        phoneno = new PhoneNumber("12345678");
        em.persist(phoneno);
        em.flush();
        phoneno = new PhoneNumber("43121234");
        em.persist(phoneno);
        em.flush();
        phoneno = new PhoneNumber("21314234");
        em.persist(phoneno);
        em.flush();
        phoneno = new PhoneNumber("65433456");
        em.persist(phoneno);
        em.flush();
        phoneno = new PhoneNumber("34567543");
        em.persist(phoneno);
        em.flush();
        phoneno = new PhoneNumber("85467654");
        em.persist(phoneno);
        em.flush();
        phoneno = new PhoneNumber("74568656");
        em.persist(phoneno);
        em.flush();
        phoneno = new PhoneNumber("98766543");
        em.persist(phoneno);
        em.flush();
    }

    public void persist(Object object) {
        em.persist(object);
    }
}
