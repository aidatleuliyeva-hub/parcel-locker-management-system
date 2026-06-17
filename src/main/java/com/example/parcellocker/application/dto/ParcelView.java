package com.example.parcellocker.application.dto;

import com.example.parcellocker.domain.ParcelStatus;
import com.example.parcellocker.domain.Size;

public record ParcelView(
        Long id,
        String trackingNumber,
        String description,
        Size size,
        ParcelStatus status,
        String customerName,
        String lockerCellNumber,
        String pickupCode
) {
}