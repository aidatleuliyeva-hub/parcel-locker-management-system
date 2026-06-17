package com.example.parcellocker.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "parcels")
public class Parcel {

    @Id
    @GeneratedValue
    private Long id;

    private String trackingNumber;

    private String description;

    @Enumerated(EnumType.STRING)
    private Size size;

    @Enumerated(EnumType.STRING)
    private ParcelStatus status;

    private String pickupCode;

    @ManyToOne(fetch = FetchType.LAZY)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    private LockerCell lockerCell;

    protected Parcel() {
        // Required by JPA
    }

    public Parcel(String trackingNumber, String description, Size size, Customer customer) {
        setTrackingNumber(trackingNumber);
        setDescription(description);
        setSize(size);

        this.status = ParcelStatus.CREATED;

        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }

        customer.addParcel(this);
    }

    public Long getId() {
        return id;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public String getDescription() {
        return description;
    }

    public Size getSize() {
        return size;
    }

    public ParcelStatus getStatus() {
        return status;
    }

    public String getPickupCode() {
        return pickupCode;
    }

    public Customer getCustomer() {
        return customer;
    }

    public LockerCell getLockerCell() {
        return lockerCell;
    }

    public boolean isCreated() {
        return status == ParcelStatus.CREATED;
    }

    public boolean isAssigned() {
        return status == ParcelStatus.ASSIGNED;
    }

    public boolean isCollected() {
        return status == ParcelStatus.COLLECTED;
    }

    public void assignTo(LockerCell lockerCell, String pickupCode) {
        if (lockerCell == null) {
            throw new IllegalArgumentException("Locker cell cannot be null");
        }

        if (pickupCode == null || pickupCode.isBlank()) {
            throw new IllegalArgumentException("Pickup code cannot be blank");
        }

        this.lockerCell = lockerCell;
        this.pickupCode = pickupCode.trim();
        this.status = ParcelStatus.ASSIGNED;
    }

    public void collect() {
        this.status = ParcelStatus.COLLECTED;
        this.pickupCode = null;
        this.lockerCell = null;
    }

    void setCustomerInternal(Customer customer) {
        this.customer = customer;
    }

    private void setTrackingNumber(String trackingNumber) {
        if (trackingNumber == null || trackingNumber.isBlank()) {
            throw new IllegalArgumentException("Tracking number cannot be blank");
        }
        this.trackingNumber = trackingNumber.trim();
    }

    private void setDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Parcel description cannot be blank");
        }
        this.description = description.trim();
    }

    private void setSize(Size size) {
        if (size == null) {
            throw new IllegalArgumentException("Parcel size cannot be null");
        }
        this.size = size;
    }
}