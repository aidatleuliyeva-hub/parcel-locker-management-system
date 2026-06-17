package com.example.parcellocker.application.dto;

import com.example.parcellocker.domain.Size;

public record LockerCellView(
        Long id,
        String cellNumber,
        Size size,
        boolean occupied
) {
}