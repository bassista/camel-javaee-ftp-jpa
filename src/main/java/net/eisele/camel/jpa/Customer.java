package net.eisele.camel.jpa;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Customer Status Entity
 *
 * @author Markus Eisele <markus@jboss.org>
 */
@XmlRootElement(name = "customer")
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@NamedQueries({
    @NamedQuery(name = "Customer.findAll",
            query = "SELECT c FROM Customer c"),
    @NamedQuery(name = "Customer.findByID",
            query = "SELECT c FROM Customer c WHERE c.customerID = :customerID"),})
public class Customer implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column(name = "CID")
    private Long customerID;
    @Column(name = "CNAME")
    private String name;
    @Column(name = "CSURENAME")
    private String surename;
    @Column(name = "CSTREET")
    private String street;
    @Column(name = "CNUMBER")
    private String number;
    @Column(name = "CPCODE")
    private String postalCode;
    @Column(name = "CSTATE")
    private String state;
    @Column(name = "CCOUNTRY")
    private String country;
    @Column(name = "CDEALERID")
    private String dealerId;

    public Long getCustomerID() {
        return customerID;
    }

    public void setCustomerID(Long customerID) {
        this.customerID = customerID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurename() {
        return surename;
    }

    public void setSurename(String surename) {
        this.surename = surename;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Customer() {
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 13 * hash + Objects.hashCode(this.customerID);
        return hash;
    }

    public String getDealerId() {
        return dealerId;
    }

    public void setDealerId(String dealerId) {
        this.dealerId = dealerId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Customer other = (Customer) obj;
        if (!Objects.equals(this.customerID, other.customerID)) {
            return false;
        }
        return Objects.equals(this.dealerId, other.dealerId);
    }

    @Override
    public String toString() {
        return "Customer{" + "customerID=" + customerID + ", name=" + name + ", surename=" + surename + ", street=" + street + ", number=" + number + ", postalCode=" + postalCode + ", state=" + state + ", country=" + country + ", dealerId=" + dealerId + '}';
    }

}
