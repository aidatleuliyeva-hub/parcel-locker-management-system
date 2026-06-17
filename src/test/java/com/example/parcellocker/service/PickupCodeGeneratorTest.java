package com.example.parcellocker.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PickupCodeGeneratorTest {

    @Test
    void randomSixDigitGeneratorShouldGenerateSixDigitCode() {
        PickupCodeGenerator generator = PickupCodeGenerator.randomSixDigitGenerator();

        String code = generator.generate();

        assertThat(code).hasSize(6);
        assertThat(code).containsOnlyDigits();
    }
}