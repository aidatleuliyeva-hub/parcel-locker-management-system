package com.example.parcellocker.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue
    private Long id;

    private String fullName;

    private String phoneNumber;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Parcel> parcels = new ArrayList<>();

    protected Customer() {
        // Required by JPA
    }

    public Customer(String fullName, String phoneNumber) {
        setFullName(fullName);
        setPhoneNumber(phoneNumber);
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public List<Parcel> getParcels() {
        return Collections.unmodifiableList(parcels);
    }

    public void addParcel(Parcel parcel) {
        Objects.requireNonNull(parcel, "Parcel cannot be null");

        if (!parcels.contains(parcel)) {
            parcels.add(parcel);
            parcel.setCustomerInternal(this);
        }
    }

    public void removeParcel(Parcel parcel) {
        Objects.requireNonNull(parcel, "Parcel cannot be null");

        if (parcels.remove(parcel)) {
            parcel.setCustomerInternal(null);
        }
    }

    private void setFullName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("Customer full name cannot be blank");
        }
        this.fullName = fullName.trim();
    }

    private void setPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new IllegalArgumentException("Customer phone number cannot be blank");
        }
        this.phoneNumber = phoneNumber.trim();
    }
}