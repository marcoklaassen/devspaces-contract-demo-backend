package org.acme;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
@Cacheable
public class Contract {

    @Id
    @GeneratedValue
    public Long id;

    @Column(length = 40)
    public String type;

    @Column(length = 40)
    public String customer;

    @Column
    public Boolean firstContractOfCustomer;

    public Contract() {
    }

    public Contract(String type, String customer) {
        this.type = type;
        this.customer = customer;
    }

    public Contract(String type, String customer, Boolean firstContractOfCustomer) {
        this.type = type;
        this.customer = customer;
        this.firstContractOfCustomer = firstContractOfCustomer;
    }
}