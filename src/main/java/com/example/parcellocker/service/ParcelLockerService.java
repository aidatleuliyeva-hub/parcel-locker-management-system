package com.example.parcellocker.service;

import com.example.parcellocker.domain.LockerCell;
import com.example.parcellocker.domain.Parcel;
import com.example.parcellocker.domain.ParcelStatus;

import java.util.Objects;

public class ParcelLockerService {

    private final PickupCodeGenerator pickupCodeGenerator;

    public ParcelLockerService(PickupCodeGenerator pickupCodeGenerator) {
        this.pickupCodeGenerator = Objects.requireNonNull(
                pickupCodeGenerator,
                "Pickup code generator cannot be null"
        );
    }

    public void assignParcelToCell(Parcel parcel, LockerCell lockerCell) {
        validateParcelAndCell(parcel, lockerCell);

        if (parcel.getStatus() == ParcelStatus.ASSIGNED) {
            throw new BusinessException("Parcel is already assigned to a locker cell");
        }

        if (parcel.getStatus() == ParcelStatus.COLLECTED) {
            throw new BusinessException("Collected parcel cannot be assigned again");
        }

        if (lockerCell.isOccupied()) {
            throw new BusinessException("Locker cell is already occupied");
        }

        if (parcel.getSize() != lockerCell.getSize()) {
            throw new BusinessException("Parcel size must match locker cell size");
        }

        String pickupCode = pickupCodeGenerator.generate();

        if (pickupCode == null || pickupCode.isBlank()) {
            throw new BusinessException("Generated pickup code cannot be blank");
        }

        parcel.assignTo(lockerCell, pickupCode);
        lockerCell.markOccupied();
    }

    public void collectParcel(Parcel parcel, String pickupCode) {
        if (parcel == null) {
            throw new BusinessException("Parcel cannot be null");
        }

        if (parcel.getStatus() == ParcelStatus.CREATED) {
            throw new BusinessException("Parcel has not been assigned to a locker cell yet");
        }

        if (parcel.getStatus() == ParcelStatus.COLLECTED) {
            throw new BusinessException("Parcel has already been collected");
        }

        if (pickupCode == null || pickupCode.isBlank()) {
            throw new BusinessException("Pickup code cannot be blank");
        }

        if (!pickupCode.equals(parcel.getPickupCode())) {
            throw new BusinessException("Invalid pickup code");
        }

        LockerCell lockerCell = parcel.getLockerCell();

        if (lockerCell == null) {
            throw new BusinessException("Assigned parcel must have a locker cell");
        }

        lockerCell.release();
        parcel.collect();
    }

    private void validateParcelAndCell(Parcel parcel, LockerCell lockerCell) {
        if (parcel == null) {
            throw new BusinessException("Parcel cannot be null");
        }

        if (lockerCell == null) {
            throw new BusinessException("Locker cell cannot be null");
        }
    }
}