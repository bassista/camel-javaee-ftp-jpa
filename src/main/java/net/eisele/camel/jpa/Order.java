/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.eisele.camel.jpa;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author myfear
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "Order.findAll",
            query = "SELECT o FROM Order o"),
    @NamedQuery(name = "Order.findByID",
            query = "SELECT o FROM Order o WHERE o.orderId = :orderId"),})
@Table(name = "ORDERENTRIES")
public class Order implements Serializable {

    @Id
    @Column(name="ORDERID")
    private Long orderId;
    @Column(name="OCID")
    private Long customerId;
    @Column(name="OISBN")
    private String isbn;
    @Column(name="ODID")
    private Long dealerId;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Long getDealerId() {
        return dealerId;
    }

    public void setDealerId(Long dealerId) {
        this.dealerId = dealerId;
    }

    public Order() {
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Objects.hashCode(this.orderId);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Order other = (Order) obj;
        if (!Objects.equals(this.orderId, other.orderId)) {
            return false;
        }
        return Objects.equals(this.dealerId, other.dealerId);
    }

    @Override
    public String toString() {
        return "Order{" + "orderId=" + orderId + ", customerId=" + customerId + ", isbn=" + isbn + ", dealerId=" + dealerId + '}';
    }

}
