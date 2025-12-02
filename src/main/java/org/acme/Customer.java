package org.acme;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Customer {
    public Long id;
    public String name;

    public Customer() {
    }

    public Customer(String name) {
        this.name = name;
    }
}

