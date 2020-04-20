/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.singleton;

import ejb.session.stateless.CustomerSessionBeanLocal;
import ejb.session.stateless.FamilyGroupSessionBeanLocal;
import ejb.session.stateless.ProductSessionBeanLocal;
import ejb.session.stateless.SubscriptionSessonBeanLocal;
import ejb.session.stateless.TransactionSessionBeanLocal;
import entity.Customer;
import entity.Announcement;

import entity.Category;

import entity.Employee;
import entity.FamilyGroup;
import entity.LuxuryProduct;
import entity.Payment;
import entity.PhoneNumber;
import entity.Plan;
import entity.Subscription;
import java.util.Date;
import entity.Product;
import entity.ProductItem;
import entity.Tag;
import entity.Transaction;
import entity.TransactionLineItem;
import entity.UsageDetail;
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
import util.exception.CreateNewSubscriptionException;
import util.exception.CustomerNotFoundException;
import util.exception.CustomerNotYetApproved;
import util.exception.InputDataValidationException;
import util.exception.PhoneNumberInUseException;
import util.exception.PlanAlreadyDisabledException;
import util.exception.ProductNotFoundException;
import util.exception.SubscriptionExistException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author admin
 */
@Singleton
@LocalBean
@Startup
public class DataInitialization {

    @EJB(name = "ProductSessionBeanLocal")
    private ProductSessionBeanLocal productSessionBeanLocal;

    @EJB(name = "TransactionSessionBeanLocal")
    private TransactionSessionBeanLocal transactionSessionBeanLocal;

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
                subscription.setIsActive(true);
                subscription.setSubscriptionStatusEnum(SubscriptionStatusEnum.TERMINATING);
                
                
                
                UsageDetail u1 = new UsageDetail(new SimpleDateFormat("dd/MM/yyyy").parse("20/02/2020"), new SimpleDateFormat("dd/MM/yyyy").parse("19/03/2020"));
                subscription.getUsageDetails().add(u1);
                u1.setSubscription(subscription);
                u1.setBill(null);
                em.persist(u1);
                em.flush();
                UsageDetail u2 = new UsageDetail(new SimpleDateFormat("dd/MM/yyyy").parse("20/03/2020"), new SimpleDateFormat("dd/MM/yyyy").parse("19/04/2020"));
                subscription.getUsageDetails().add(u2);
                u2.setSubscription(subscription);
                u2.setBill(null);
                em.persist(u2);
                em.flush();
                

                subscription = new Subscription(20, 5, 5);
                cal = Calendar.getInstance();
                cal.add(Calendar.MONTH, -2);
                subscription.setSubscriptionStartDate(cal.getTime());
                subscriptionSessonBean.createNewSubscription(subscription, 1l, 2l, 2l);
                subscription.setIsActive(true);
                
                
                u1 = new UsageDetail(new SimpleDateFormat("dd/MM/yyyy").parse("20/02/2020"), new SimpleDateFormat("dd/MM/yyyy").parse("19/03/2020"));
                subscription.getUsageDetails().add(u1);
                u1.setSubscription(subscription);
                u1.setBill(null);
                em.persist(u1);
                em.flush();
                u2 = new UsageDetail(new SimpleDateFormat("dd/MM/yyyy").parse("20/03/2020"), new SimpleDateFormat("dd/MM/yyyy").parse("19/04/2020"));
                subscription.getUsageDetails().add(u2);
                u2.setSubscription(subscription);
                u2.setBill(null);
                em.persist(u2);
                em.flush();

                subscription = new Subscription(30, 0, 0);
                cal = Calendar.getInstance();
                cal.add(Calendar.MONTH, -1);
                subscription.setSubscriptionStartDate(cal.getTime());
                subscriptionSessonBean.createNewSubscription(subscription, 1l, 2l, 3l);
                subscription.setIsActive(true);
                u1 = new UsageDetail(new SimpleDateFormat("dd/MM/yyyy").parse("20/02/2020"), new SimpleDateFormat("dd/MM/yyyy").parse("19/03/2020"));
                subscription.getUsageDetails().add(u1);
                u1.setSubscription(subscription);
                u1.setBill(null);
                em.persist(u1);
                em.flush();
                u2 = new UsageDetail(new SimpleDateFormat("dd/MM/yyyy").parse("20/03/2020"), new SimpleDateFormat("dd/MM/yyyy").parse("19/04/2020"));
                subscription.getUsageDetails().add(u2);
                u2.setSubscription(subscription);
                u2.setBill(null);
                em.persist(u2);
                em.flush();
                

                subscription = new Subscription(15, 5, 10);
                cal = Calendar.getInstance();
                cal.add(Calendar.MONTH, -1);
                subscription.setSubscriptionStartDate(cal.getTime());
                subscriptionSessonBean.createNewSubscription(subscription, 1l, 3l, 4l);
            } catch (InputDataValidationException ex) {
                Logger.getLogger(DataInitialization.class.getName()).log(Level.SEVERE, null, ex);
            } catch (UnknownPersistenceException ex) {
                Logger.getLogger(DataInitialization.class.getName()).log(Level.SEVERE, null, ex);
            } catch (CustomerNotYetApproved ex) {
                Logger.getLogger(DataInitialization.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SubscriptionExistException ex) {
                Logger.getLogger(DataInitialization.class.getName()).log(Level.SEVERE, null, ex);
            } catch (PhoneNumberInUseException ex) {
                Logger.getLogger(DataInitialization.class.getName()).log(Level.SEVERE, null, ex);
            } catch (PlanAlreadyDisabledException ex) {
                Logger.getLogger(DataInitialization.class.getName()).log(Level.SEVERE, null, ex);
            } catch (CreateNewSubscriptionException ex) {
                Logger.getLogger(DataInitialization.class.getName()).log(Level.SEVERE, null, ex);
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

            Customer customer1 = em.find(Customer.class, 1l);
            Customer customer2 = em.find(Customer.class, 2l);
            Customer customer3 = em.find(Customer.class, 3l);
            Customer customer4 = em.find(Customer.class, 4l);
            Customer customer5 = em.find(Customer.class, 5l);
            Customer customer6 = em.find(Customer.class, 6l);

            FamilyGroup fg1 = new FamilyGroup("IS3106 Warriors");
            fg1.getCustomers().add(customer1);
            customer1.setOwnerOfFamilyGroup(true);
            fg1.getCustomers().add(customer2);
            customer1.setFamilyGroup(fg1);
            customer2.setFamilyGroup(fg1);
            em.persist(fg1);
            em.flush();
            FamilyGroup fg2 = new FamilyGroup("I lOVE NUS");
            fg2.getCustomers().add(customer3);
            fg2.getCustomers().add(customer4);
            //fg2.getCustomers().add(customer5);
            //  fg2.getCustomers().add(customer6);
            customer3.setFamilyGroup(fg2);
            customer4.setFamilyGroup(fg2);
            //   customer5.setFamilyGroup(fg2);
            //    customer6.setFamilyGroup(fg2);
            em.persist(fg2);
            em.flush();
            initialiseProducts();

            Payment payment = new Payment("1234123412341234", "123", new Date(), new BigDecimal("12.12"));
            em.persist(payment);
            em.flush();

            Transaction testTransaction = new Transaction(new BigDecimal("12.12"), new Date());
            testTransaction.setCustomer(customer1);
            testTransaction.setPayment(payment);
            em.persist(testTransaction);
            em.flush();

            TransactionLineItem item = new TransactionLineItem(BigDecimal.ONE, 1, BigDecimal.ZERO);
            item.setProduct(productSessionBeanLocal.retrieveProductByProductId(4l));
            item.setTransaction(testTransaction);
            em.persist(item);
            em.flush();
            testTransaction.getTransactionLineItems().add(item);

        } catch (ParseException ex) {
            Logger.getLogger(DataInitialization.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ProductNotFoundException ex) {
            Logger.getLogger(DataInitialization.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void createCustomers() {
        Calendar timeNow = Calendar.getInstance();
        timeNow.add(Calendar.MONTH, -2);
        Customer customer = new Customer("customer1", "password1", "Mark", "Tan", Integer.valueOf(20), "This is my address", "428198", "marktan@gmail.com", "S9709388A", "./nricPhoto.jpg", timeNow.getTime(), "mt.jpg");
        //customer.setIsApproved(true);
        em.persist(customer);
        em.flush();

        customer = new Customer("customer2", "password2", "Jun Le", "Tay", Integer.valueOf(20), "This is my address", "428198", "tayjl@gmail.com", "S9941179A", null, timeNow.getTime(), "tayjl.jpg");
        em.persist(customer);
        //customer.setIsApproved(true);
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
       // customer.setIsApproved(true);
        em.persist(customer);
        em.flush();

        customer = new Customer("customer6", "password6", "Ethan", "Project Manager", Integer.valueOf(20), "This is my address", "117417", "ethank@gmail.com", "S4041889A", null, timeNow.getTime(), "ethan.jpg");
       // customer.setIsApproved(true);
        em.persist(customer);
        em.flush();
        
        timeNow.add(Calendar.MONTH, 1);
        customer = new Customer("customer7", "password7", "WK wife", "Tan", Integer.valueOf(20), "This is my address", "117417", "tanwk2@gmail.com", "S4041178A", null, timeNow.getTime(), null);
        em.persist(customer);
        em.flush();
        customer = new Customer("customer8", "password8", "WK son", "Tan", Integer.valueOf(20), "This is my address", "117417", "tanwk3@gmail.com", "S4041177A", null, timeNow.getTime(), null);
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
    }

    public void persist(Object object) {
        em.persist(object);
    }

    private void initialiseProducts() {
        Category phoneAccessories = new Category("Phone Accessories", "Phone Accessories");
        em.persist(phoneAccessories);
        em.flush();

        Category handset = new Category("Handset", "Handsets");
        em.persist(handset);
        em.flush();

        Category apple = new Category("Apple", "Brand");
        em.persist(apple);
        em.flush();

        Category android = new Category("Android", "Brand");
        em.persist(android);
        em.flush();

        //new tags
        Tag popular = new Tag("Popular");
        em.persist(popular);
        em.flush();

        Tag appleTag = new Tag("Apple");
        em.persist(appleTag);
        em.flush();

        Tag samsung = new Tag("Samsung");
        em.persist(samsung);
        em.flush();

        Tag google = new Tag("Google");
        em.persist(google);
        em.flush();

        Tag discount = new Tag("Discount");
        em.persist(discount);
        em.flush();

        Tag tagNew = new Tag("New");
        em.persist(tagNew);
        em.flush();

        Tag mobile = new Tag("Mobile");
        em.persist(mobile);
        em.flush();

        Tag cartoon = new Tag("Cartoon");
        em.persist(cartoon);
        em.flush();

        //product 1
        LuxuryProduct iphoneXS = new LuxuryProduct("0000000001", "SKU001", "iPhone XS", "iPhone XS (Black)", BigDecimal.valueOf(1000.0), 10, 50, "iphoneXS.JPG");
        iphoneXS.setCategory(apple);
        List<Tag> tags = new ArrayList<>();
        List<Product> products = apple.getProducts();
        products.add(iphoneXS);
        apple.setProducts(products);
        tags.add(popular);
        tags.add(appleTag);
        tags.add(tagNew);
        tags.add(mobile);
        iphoneXS.setTags(tags);
        em.persist(iphoneXS);
        em.flush();
        List<Product> tagT = popular.getProducts();
        tagT.add(iphoneXS);
        tagT = appleTag.getProducts();
        tagT.add(iphoneXS);
        tagT = tagNew.getProducts();
        tagT.add(iphoneXS);
        tagT = mobile.getProducts();
        tagT.add(iphoneXS);

        List<ProductItem> productItems = iphoneXS.getProductItems();

        Integer unique = 1;

        for (int i = 0; i < 10; i++) {
            String s = uniqueSerialNum(unique);

            ProductItem pi = new ProductItem(s, iphoneXS.getPrice());
            pi.setLuxuryProduct(iphoneXS);
            em.persist(pi);
            em.flush();
            unique++;
            productItems.add(pi);
        }

        //product 2
        LuxuryProduct googlePixel4 = new LuxuryProduct("0000000002", "SKU002", "Google Pixel 4", "Google Pixel 4 (White)", BigDecimal.valueOf(799.0), 20, 20, "googlePixel4.jpg");
        googlePixel4.setCategory(android);
        tags = new ArrayList<>();
        products = android.getProducts();
        products.add(googlePixel4);
        android.setProducts(products);
        tags.add(popular);
        tags.add(mobile);
        tags.add(google);
        googlePixel4.setTags(tags);
        em.persist(googlePixel4);
        em.flush();
        tagT = popular.getProducts();
        tagT.add(googlePixel4);
        tagT = mobile.getProducts();
        tagT.add(googlePixel4);
        tagT = google.getProducts();
        tagT.add(googlePixel4);

        productItems = googlePixel4.getProductItems();

        for (int i = 0; i < googlePixel4.getQuantityOnHand(); i++) {
            String s = uniqueSerialNum(unique);

            ProductItem pi = new ProductItem(s, googlePixel4.getPrice());
            pi.setLuxuryProduct(googlePixel4);
            em.persist(pi);
            em.flush();
            unique++;
            productItems.add(pi);
        }

        //product 3
        LuxuryProduct samsungFlipZ = new LuxuryProduct("0000000003", "SKU003", "Samsung Flip Z", "Samsung Flip Z (Black)", BigDecimal.valueOf(899.0), 20, 20, "samsungFlipZ.jpg");
        samsungFlipZ.setCategory(android);
        tags = new ArrayList<>();
        products = android.getProducts();
        products.add(samsungFlipZ);
        android.setProducts(products);
        tags.add(popular);
        tags.add(mobile);
        tags.add(samsung);
        samsungFlipZ.setTags(tags);
        em.persist(samsungFlipZ);
        em.flush();
        tagT = popular.getProducts();
        tagT.add(samsungFlipZ);
        tagT = mobile.getProducts();
        tagT.add(samsungFlipZ);
        tagT = samsung.getProducts();
        tagT.add(samsungFlipZ);

        productItems = iphoneXS.getProductItems();

        for (int i = 0; i < googlePixel4.getQuantityOnHand(); i++) {
            String s = uniqueSerialNum(unique);

            ProductItem pi = new ProductItem(s, googlePixel4.getPrice());
            pi.setLuxuryProduct(googlePixel4);
            em.persist(pi);
            em.flush();
            unique++;
            productItems.add(pi);
        }

        //product 4
        Product iphoneXScover = new Product("SKU004", "iPhone XS WeBareBear Case", "iPhone XS case", BigDecimal.valueOf(15.0), 10, 50, "iphoneXSwbbcase.jpg");
        iphoneXScover.setCategory(phoneAccessories);
        products = phoneAccessories.getProducts();
        products.add(iphoneXScover);
        phoneAccessories.setProducts(products);
        tags = new ArrayList<>();
        tags.add(popular);
        tags.add(appleTag);
        tags.add(mobile);
        tags.add(cartoon);
        iphoneXScover.setTags(tags);
        em.persist(iphoneXScover);
        em.flush();
        tagT = popular.getProducts();
        tagT.add(iphoneXScover);
        tagT = appleTag.getProducts();
        tagT.add(iphoneXScover);
        tagT = mobile.getProducts();
        tagT.add(iphoneXScover);
        tagT = cartoon.getProducts();
        tagT.add(iphoneXScover);

        //product 5
        Product appleWire = new Product("SKU005", "Apple Charger", "Apple Lighting to USB Cable (1 meter, White)", BigDecimal.valueOf(10.0), 40, 20, "appleWire.JPG");
        appleWire.setCategory(phoneAccessories);
        products = phoneAccessories.getProducts();
        products.add(appleWire);
        phoneAccessories.setProducts(products);
        tags = new ArrayList<>();
        tags.add(appleTag);
        tags.add(popular);
        appleWire.setTags(tags);
        em.persist(appleWire);
        em.flush();
        tagT = appleTag.getProducts();
        tagT.add(appleWire);
        tagT = popular.getProducts();
        tagT.add(appleWire);

        //product 6
        Product androidCharger = new Product("SKU006", "Android Charger", "Android Charger (1 meter, White)", BigDecimal.valueOf(10.0), 40, 20, "androidCharger.jpg");
        androidCharger.setCategory(phoneAccessories);
        products = phoneAccessories.getProducts();
        products.add(androidCharger);
        phoneAccessories.setProducts(products);
        tags = new ArrayList<>();
        tags.add(samsung);
        tags.add(popular);
        androidCharger.setTags(tags);
        em.persist(androidCharger);
        em.flush();
        tagT = samsung.getProducts();
        tagT.add(androidCharger);
        tagT = popular.getProducts();
        tagT.add(androidCharger);

        //product 7
        LuxuryProduct sonyXperia1 = new LuxuryProduct("0000000007", "SKU007", "Sony Xperia1-ii", "Sony Xperia1-ii (White)", BigDecimal.valueOf(999.0), 20, 20, "sonyXperia1.JPG");
        sonyXperia1.setCategory(android);
        tags = new ArrayList<>();
        products = android.getProducts();
        products.add(sonyXperia1);
        android.setProducts(products);
        tags.add(popular);
        tags.add(mobile);
        tags.add(tagNew);
        sonyXperia1.setTags(tags);
        em.persist(sonyXperia1);
        em.flush();
        tagT = popular.getProducts();
        tagT.add(sonyXperia1);
        tagT = mobile.getProducts();
        tagT.add(sonyXperia1);
        tagT = tagNew.getProducts();
        tagT.add(sonyXperia1);

        productItems = sonyXperia1.getProductItems();

        for (int i = 0; i < sonyXperia1.getQuantityOnHand(); i++) {
            String s = uniqueSerialNum(unique);

            ProductItem pi = new ProductItem(s, sonyXperia1.getPrice());
            pi.setLuxuryProduct(sonyXperia1);
            em.persist(pi);
            em.flush();
            unique++;
            productItems.add(pi);
        }

        //product 8
        LuxuryProduct iPhone11Pro = new LuxuryProduct("0000000008", "SKU008", "iPhone 11 Pro", "iPhone 11 Pro (Black)", BigDecimal.valueOf(1010.0), 20, 20, "iphone11Pro.jpg");
        iPhone11Pro.setCategory(apple);
        tags = new ArrayList<>();
        products = apple.getProducts();
        products.add(iPhone11Pro);
        apple.setProducts(products);
        tags.add(popular);
        tags.add(mobile);
        tags.add(tagNew);
        tags.add(appleTag);
        iPhone11Pro.setTags(tags);
        em.persist(iPhone11Pro);
        em.flush();
        tagT = popular.getProducts();
        tagT.add(iPhone11Pro);
        tagT = mobile.getProducts();
        tagT.add(iPhone11Pro);
        tagT = tagNew.getProducts();
        tagT.add(iPhone11Pro);
        tagT = appleTag.getProducts();
        tagT.add(iPhone11Pro);

        productItems = iPhone11Pro.getProductItems();

        for (int i = 0; i < iPhone11Pro.getQuantityOnHand(); i++) {
            String s = uniqueSerialNum(unique);

            ProductItem pi = new ProductItem(s, iPhone11Pro.getPrice());
            pi.setLuxuryProduct(iPhone11Pro);
            em.persist(pi);
            em.flush();
            unique++;
            productItems.add(pi);
        }

        //product 9
        LuxuryProduct oppoReno2 = new LuxuryProduct("0000000009", "SKU009", "OPPO Reno 2", "OPPO Reno 2 (Black)", BigDecimal.valueOf(798.0), 20, 20, "oppoReno2.jpg");
        oppoReno2.setCategory(android);
        tags = new ArrayList<>();
        products = android.getProducts();
        products.add(oppoReno2);
        android.setProducts(products);
        tags.add(popular);
        tags.add(mobile);
        tags.add(tagNew);
        oppoReno2.setTags(tags);
        em.persist(oppoReno2);
        em.flush();
        tagT = popular.getProducts();
        tagT.add(oppoReno2);
        tagT = mobile.getProducts();
        tagT.add(oppoReno2);
        tagT = tagNew.getProducts();
        tagT.add(oppoReno2);

        productItems = oppoReno2.getProductItems();

        for (int i = 0; i < oppoReno2.getQuantityOnHand(); i++) {
            String s = uniqueSerialNum(unique);

            ProductItem pi = new ProductItem(s, oppoReno2.getPrice());
            pi.setLuxuryProduct(oppoReno2);
            em.persist(pi);
            em.flush();
            unique++;
            productItems.add(pi);
        }

        //product 10
        LuxuryProduct galaxyNote10 = new LuxuryProduct("0000000010", "SKU010", "Galaxy Note 10+", "Galaxy Note 10+ (Black)", BigDecimal.valueOf(888.0), 20, 20, "galaxyNote10+.jpg");
        galaxyNote10.setCategory(android);
        tags = new ArrayList<>();
        products = android.getProducts();
        products.add(galaxyNote10);
        android.setProducts(products);
        tags.add(popular);
        tags.add(mobile);
        tags.add(tagNew);
        tags.add(samsung);
        galaxyNote10.setTags(tags);
        em.persist(galaxyNote10);
        em.flush();
        tagT = popular.getProducts();
        tagT.add(galaxyNote10);
        tagT = mobile.getProducts();
        tagT.add(galaxyNote10);
        tagT = tagNew.getProducts();
        tagT.add(galaxyNote10);
        tagT = samsung.getProducts();
        tagT.add(galaxyNote10);

        productItems = galaxyNote10.getProductItems();

        for (int i = 0; i < galaxyNote10.getQuantityOnHand(); i++) {
            String s = uniqueSerialNum(unique);

            ProductItem pi = new ProductItem(s, galaxyNote10.getPrice());
            pi.setLuxuryProduct(galaxyNote10);
            em.persist(pi);
            em.flush();
            unique++;
            productItems.add(pi);
        }

        //product 11
        Product carMount = new Product("SKU011", "Universal Car Mount", "Universal Car Mount (Black)", BigDecimal.valueOf(15.0), 40, 20, "carMount.jpg");
        carMount.setCategory(phoneAccessories);
        products = phoneAccessories.getProducts();
        products.add(carMount);
        phoneAccessories.setProducts(products);
        tags = new ArrayList<>();
        tags.add(popular);
        carMount.setTags(tags);
        em.persist(carMount);
        em.flush();
        tagT = popular.getProducts();
        tagT.add(carMount);

        //product 12
        Product leatherCover = new Product("SKU012", "Samsung Flip Z Leather Phone Cover", "Samsung Flip Z Leather Phone Cover (Gret)", BigDecimal.valueOf(49.0), 40, 20, "leatherCover.jpg");
        leatherCover.setCategory(phoneAccessories);
        products = phoneAccessories.getProducts();
        products.add(leatherCover);
        phoneAccessories.setProducts(products);
        tags = new ArrayList<>();
        tags.add(popular);
        tags.add(samsung);
        leatherCover.setTags(tags);
        em.persist(leatherCover);
        em.flush();
        tagT = popular.getProducts();
        tagT.add(leatherCover);
        tagT = samsung.getProducts();
        tagT.add(leatherCover);

    }

    private String uniqueSerialNum(int unique) {
        String s = Integer.toString(unique);

        while (s.length() < 10) {
            s = "0" + s;
        }

        return s;
    }

}
