package com.example.parcellocker.service;

import java.security.SecureRandom;

@FunctionalInterface
public interface PickupCodeGenerator {

    String generate();

    static PickupCodeGenerator randomSixDigitGenerator() {
        SecureRandom random = new SecureRandom();

        return () -> String.format("%06d", random.nextInt(1_000_000));
    }
}