/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Email;
import javax.validation.constraints.Future;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import util.enumeration.CustomerStatusEnum;
import util.security.CryptographicHelper;

/**
 *
 * @author admin
 */
@Entity
public class Customer implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerId;

    @Column(nullable = false, unique = true, length = 32)
    @NotNull
    @Size(min = 4, max = 32)
    private String username;

    @Column(columnDefinition = "CHAR(32) NOT NULL")
    @NotNull
    private String password;

    @Column(nullable = false, length = 32)
    @NotNull
    @Size(min = 2, max = 24)
    private String firstName;

    @Column(nullable = false, length = 32)
    @NotNull
    @Size(min = 2, max = 24)
    private String lastName;

    @Column(nullable = false)
    @NotNull
    private Date joinDate;
    
    @Column(nullable = false)
    @NotNull
    @Positive
    @Min(16)
    @Max(99)
    private Integer age;

    @Column(nullable = true, length = 255)
    @Size(min = 10, max = 64)
    private String address;

    @Column(nullable = true, length = 255)
    @Size(min = 10, max = 64)
    private String newAddress;

    @Column(nullable = true, length = 6)
    @Size(min = 6, max = 6)
    @Pattern(regexp = "^[0-9]{6}$")
    private String postalCode;

    @Column(nullable = true, length = 6)
    @Size(min = 6, max = 6)
    @Pattern(regexp = "^[0-9]{6}$")
    private String newPostalCode;

    @Column(nullable = true, unique = true, length = 9)
    @Size(min = 9, max = 9)
    @Pattern(regexp = "^[STFG]\\d{7}[A-JZ]$")
    private String nric;

    @Column(nullable = true, unique = true, length = 9)
    @Size(min = 9, max = 9)
    @Pattern(regexp = "^[STFG]\\d{7}[A-JZ]$")
    private String newNric;

    @Column(nullable = true, unique = false)
//    @Column(nullable = true)
    private String nricFrontImagePath;

    @Column(nullable = true, unique = false)
//    @Column(nullable = true)
    private String nricBackImagePath;

    @Column(nullable = true, unique = false)
//    @Column(nullable = true)
    private String newNricFrontImagePath;
    @Column(nullable = true, unique = false)
//    @Column(nullable = true)
    private String newNricBackImagePath;

    @Column(nullable = true, unique = false)
    private String profilePhoto;

    @Column(nullable = false, unique = true, length = 64)
    @NotNull
    @Size(max = 64)
    @Email
    private String email;

    @Column(nullable = false)
    @NotNull
    @Min(0)
    @Max(1000)
    private Integer loyaltyPoints;

    @Column(nullable = false)
    @NotNull
    private CustomerStatusEnum customerStatusEnum;

    @Column(nullable = false)
    @NotNull
    @Min(0)
    @Max(1000)
    private Integer consecutiveMonths;

    @Column(nullable = false)
    @NotNull
    @Min(0)
    @Max(1000)
    private Integer counter;

    @Column(length = 16)
    @Size(min = 16, max = 16)
    @Pattern(regexp = "^[0-9]{16}$")
    private String creditCardNumber;

    @Column(length = 3)
    @Size(min = 3, max = 3)
    @Pattern(regexp = "^[0-9]{3}$")
    private String cvv;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    @Future
    private Date creditCardExpiryDate;

    @Column(nullable = false)
    @NotNull
    private Boolean isApproved;

    @Column(columnDefinition = "CHAR(32) NOT NULL")
    private String salt;

    @OneToMany(mappedBy = "customer")
    private List<Bill> bills;

    @OneToMany(mappedBy = "customer")
    private List<Subscription> subscriptions;

    @OneToMany(mappedBy = "customer")
    private List<QuizAttempt> quizAttempts;

    @OneToMany(mappedBy = "customer")
    private List<Transaction> transactions;

//    @ManyToMany
//    @JoinColumn(nullable = false)
//    private List<Announcement> announcements;
    @ManyToOne
    private FamilyGroup familyGroup;

    private Boolean ownerOfFamilyGroup;

    public Customer() {
        this.salt = CryptographicHelper.getInstance().generateRandomString(32);
        this.loyaltyPoints = 0;
        this.consecutiveMonths = 0;
        this.counter = 0;
        this.bills = new ArrayList<>();
        this.subscriptions = new ArrayList<>();
        this.quizAttempts = new ArrayList<>();
        this.transactions = new ArrayList<>();
        this.isApproved = false;
        this.ownerOfFamilyGroup = false;

//        this.announcements = new ArrayList<>();
    }

    public Customer(String username, String password, String firstName, String lastName, Integer age, String newAddress, String newPostalCode, String email, String newNric, String newNricFrontImagePath, String newNricBackImagePath, Date joinDate, String profilePhoto) {
        this();
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.newAddress = newAddress;
        this.newPostalCode = newPostalCode;
        this.email = email;
        this.newNric = newNric;
        this.newNricFrontImagePath = newNricFrontImagePath;
        this.newNricBackImagePath = newNricBackImagePath;
        this.customerStatusEnum = CustomerStatusEnum.PENDING;
        this.joinDate = joinDate;
        this.profilePhoto = profilePhoto;
        hashPassword(password);
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (customerId != null ? customerId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the customerId fields are not set
        if (!(object instanceof Customer)) {
            return false;
        }
        Customer other = (Customer) object;
        if ((this.customerId == null && other.customerId != null) || (this.customerId != null && !this.customerId.equals(other.customerId))) {
            return false;
        }
        return true;
    }

    public Boolean getOwnerOfFamilyGroup() {
        return ownerOfFamilyGroup;
    }

    public void setOwnerOfFamilyGroup(Boolean ownerOfFamilyGroup) {
        this.ownerOfFamilyGroup = ownerOfFamilyGroup;
    }

    @Override
    public String toString() {
        return "entity.Customer[ id=" + customerId + " ]";
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setNewNricBackImagePath(String newNricBackImagePath) {
        this.newNricBackImagePath = newNricBackImagePath;
    }

    public String getNewNricBackImagePath() {
        return newNricBackImagePath;
    }

    public void hashPassword(String password) {
        if (password != null) {
            this.password = CryptographicHelper.getInstance().byteArrayToHexString(CryptographicHelper.getInstance().doMD5Hashing(password + this.salt));
        } else {
            this.password = null;
        }
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getNric() {
        return nric;
    }

    public void setNric(String nric) {
        this.nric = nric;
    }

    public Integer getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public Integer getCounter() {
        return counter;
    }

    public void setCounter(Integer counter) {
        this.counter = counter;
    }

    public void setLoyaltyPoints(Integer loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }

    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    public void setCreditCardNumber(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public Date getCreditCardExpiryDate() {
        return creditCardExpiryDate;
    }

    public void setCreditCardExpiryDate(Date creditCardExpiryDate) {
        this.creditCardExpiryDate = creditCardExpiryDate;
    }

    public List<Bill> getBills() {
        return bills;
    }

    public void setBills(List<Bill> bills) {
        this.bills = bills;
    }

    public List<Subscription> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(List<Subscription> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public List<QuizAttempt> getQuizAttempts() {
        return quizAttempts;
    }

    public void setQuizAttempts(List<QuizAttempt> quizAttempts) {
        this.quizAttempts = quizAttempts;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public FamilyGroup getFamilyGroup() {
        return familyGroup;
    }

    public void setFamilyGroup(FamilyGroup familyGroup) {
        this.familyGroup = familyGroup;
    }

    public String getNricFrontImagePath() {
        return nricFrontImagePath;
    }

    public void setNricFrontImagePath(String nricFrontImagePath) {
        this.nricFrontImagePath = nricFrontImagePath;
    }

//    public List<Announcement> getAnnouncements() {
//        return announcements;
//    }
//
//    public void setAnnouncements(List<Announcement> announcements) {
//        this.announcements = announcements;
//    }

    public String getNewAddress() {
        return newAddress;
    }

    public void setNewAddress(String newAddress) {
        this.newAddress = newAddress;
    }

    public String getNricBackImagePath() {
        return nricBackImagePath;
    }

    public void setNricBackImagePath(String nricBackImagePath) {
        this.nricBackImagePath = nricBackImagePath;
    }

    public String getNewNricFrontImagePath() {
        return newNricFrontImagePath;
    }

    public void setNewNricFrontImagePath(String newNricFrontImagePath) {
        this.newNricFrontImagePath = newNricFrontImagePath;
    }

    public String getNewPostalCode() {
        return newPostalCode;
    }

    public void setNewPostalCode(String newPostalCode) {
        this.newPostalCode = newPostalCode;
    }

    public String getNewNric() {
        return newNric;
    }

    public void setNewNric(String newNric) {
        this.newNric = newNric;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public Integer getConsecutiveMonths() {
        return consecutiveMonths;
    }

    public void setConsecutiveMonths(Integer consecutiveMonths) {
        this.consecutiveMonths = consecutiveMonths;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getIsApproved() {
        return isApproved;
    }

    public void setIsApproved(Boolean isApproved) {
        this.isApproved = isApproved;
    }

    public CustomerStatusEnum getCustomerStatusEnum() {
        return customerStatusEnum;
    }

    public void setCustomerStatusEnum(CustomerStatusEnum customerStatusEnum) {
        this.customerStatusEnum = customerStatusEnum;
    }

    public Date getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(Date joinDate) {
        this.joinDate = joinDate;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }
}
