package com.example.parcellocker.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "locker_cells")
public class LockerCell {

    @Id
    @GeneratedValue
    private Long id;

    private String cellNumber;

    @Enumerated(EnumType.STRING)
    private Size size;

    private boolean occupied;

    protected LockerCell() {
        // Required by JPA
    }

    public LockerCell(String cellNumber, Size size) {
        setCellNumber(cellNumber);
        setSize(size);
        this.occupied = false;
    }

    public Long getId() {
        return id;
    }

    public String getCellNumber() {
        return cellNumber;
    }

    public Size getSize() {
        return size;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public boolean isAvailable() {
        return !occupied;
    }

    public void markOccupied() {
        if (occupied) {
            throw new IllegalStateException("Locker cell is already occupied");
        }
        this.occupied = true;
    }

    public void release() {
        this.occupied = false;
    }

    private void setCellNumber(String cellNumber) {
        if (cellNumber == null || cellNumber.isBlank()) {
            throw new IllegalArgumentException("Locker cell number cannot be blank");
        }
        this.cellNumber = cellNumber.trim();
    }

    private void setSize(Size size) {
        if (size == null) {
            throw new IllegalArgumentException("Locker cell size cannot be null");
        }
        this.size = size;
    }
}