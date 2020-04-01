/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.singleton;

import entity.Customer;
import entity.Announcement;

import entity.Category;

import entity.Employee;
import entity.FamilyGroup;
import entity.LuxuryProduct;
import entity.PhoneNumber;
import entity.Plan;
import entity.Subscription;
import java.util.Date;
import entity.Product;
import entity.ProductItem;
import entity.Tag;
import java.math.BigDecimal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.enumeration.AccessRightEnum;
import util.enumeration.AnnouncementRecipientEnum;

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
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");

    @PostConstruct
    public void postConstruct() {
        if (em.find(Employee.class, 1l) == null && em.find(Customer.class, 1l) == null) {
            initialiseData();
        }

    }

    private void initialiseData() {
        try {

            FamilyGroup familyGroup1 = new FamilyGroup("IS3106 Warriors");
            em.persist(familyGroup1);
            em.flush();

            Employee newEmployee = new Employee("manager", "password", "Default", "Manager", AccessRightEnum.MANAGER, "path");
            em.persist(newEmployee);
            em.flush();

            newEmployee = new Employee("employee", "password", "Default", "Employee", AccessRightEnum.EMPLOYEE, "path");
            em.persist(newEmployee);
            em.flush();

            Plan newPlan = new Plan("Saver 15", 15, BigDecimal.valueOf(25), BigDecimal.valueOf(2.5), Integer.valueOf(1500), Integer.valueOf(100), Integer.valueOf(100), null, null);
            em.persist(newPlan);
            em.flush();

            PhoneNumber phoneno1 = new PhoneNumber("96820119");
            em.persist(phoneno1);
            em.flush();
            PhoneNumber phoneno2 = new PhoneNumber("54322345");
            em.persist(phoneno2);
            em.flush();
            PhoneNumber phoneno3 = new PhoneNumber("07978291");
            em.persist(phoneno3);
            em.flush();

            Customer customer = new Customer("customer1", "password1", "Mark", "Tan", Integer.valueOf(20), "This is my address", "428198", "marktan@gmail.com", "S9709388A", "./nricPhoto.jpg");
            em.persist(customer);
            em.flush();

            familyGroup1.getCustomers().add(customer);

            Subscription subscription = new Subscription(10, 10, 10);
            subscription.setCustomer(customer);
            subscription.setPlan(newPlan);
            subscription.setPhoneNumber(phoneno1);
            subscription.setSubscriptionStartDate(new Date(2020, 4, 20));
            em.persist(subscription);
            em.flush();
            customer.getSubscriptions().add(subscription);

            subscription = new Subscription(20, 5, 5);
            subscription.setCustomer(customer);
            subscription.setPlan(newPlan);
            subscription.setPhoneNumber(phoneno2);
            subscription.setSubscriptionStartDate(new Date(2020, 3, 15));
            em.persist(subscription);
            em.flush();
            customer.getSubscriptions().add(subscription);

            customer = new Customer("customer2", "password2", "Jun Le", "Tay", Integer.valueOf(20), "This is my address", "117417", "tayjl@gmail.com", "S2341179A", "./nricPhoto2.jpg");
            em.persist(customer);
            em.flush();

            familyGroup1.getCustomers().add(customer);

            subscription = new Subscription(20, 5, 5);
            subscription.setCustomer(customer);
            subscription.setPlan(newPlan);
            subscription.setPhoneNumber(phoneno3);
            subscription.setSubscriptionStartDate(new Date(2020, 4, 1));
            em.persist(subscription);
            em.flush();
            customer.getSubscriptions().add(subscription);

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

            initialiseProducts();
        } catch (ParseException ex) {
            Logger.getLogger(DataInitialization.class.getName()).log(Level.SEVERE, null, ex);
        }

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
        LuxuryProduct iphoneXS = new LuxuryProduct("0000000001", "SKU001", "iPhone XS", "iphone xs", BigDecimal.valueOf(1000.0), 10, 50, "./productImages/iphoneXS.JPG");
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
            unique++;
            productItems.add(pi);
        }
        
        //product 2
        LuxuryProduct googlePixel4 = new LuxuryProduct("0000000002", "SKU002", "Google Pixel 4", "Google Pixel 4", BigDecimal.valueOf(799.0), 20, 20, "./productImages/googlePixel4.jpg");
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
        
        productItems = iphoneXS.getProductItems();

        for (int i = 0; i < googlePixel4.getQuantityOnHand(); i++) {
            String s = uniqueSerialNum(unique);

            ProductItem pi = new ProductItem(s, googlePixel4.getPrice());
            pi.setLuxuryProduct(googlePixel4);
            em.persist(pi);
            unique++;
            productItems.add(pi);
        }
        
        //product 3
        LuxuryProduct samsungFlipZ = new LuxuryProduct("0000000003", "SKU003", "Samsung Flip Z", "Samsung Flip Z", BigDecimal.valueOf(899.0), 20, 20, "./productImages/samsungFlipZ.jpg");
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
            unique++;
            productItems.add(pi);
        }
        

        //product 4
        Product iphoneXScover = new Product("SKU004", "iPhone XS WeBareBear Case", "iphone xs hp case", BigDecimal.valueOf(15.0), 10, 50, "./productImages/iphoneXSwbbcase.jpg");
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
        Product appleWire = new Product("SKU005", "Apple Charger", "Apple Lighting to USB Cable (1 meter)", BigDecimal.valueOf(10.0), 40, 20, "./productImages/appleWire.JPG");
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
    }

    private String uniqueSerialNum(int unique) {
        String s = Integer.toString(unique);

        while (s.length() < 10) {
            s = "0" + s;
        }

        return s;
    }

}
