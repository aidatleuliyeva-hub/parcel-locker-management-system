package com.example.parcellocker.application.dto;

public record CustomerView(
        Long id,
        String fullName,
        String phoneNumber,
        int parcelCount
) {
}