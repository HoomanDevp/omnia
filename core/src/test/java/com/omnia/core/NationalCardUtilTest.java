package com.omnia.core;

import com.omnia.core.util.NationalCardUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NationalCardUtilTest {

    @Test
    void testInvalidIdentificationCode() {
        String invalidCode = "0022214721"; // Example invalid code
        assertFalse(NationalCardUtil.nationalCodeValidation(invalidCode));
    }

    @Test
    void testValidIdentificationCode() {
        String validCode = "0022214720"; // Example invalid code
        assertTrue(NationalCardUtil.nationalCodeValidation(validCode));
    }

    @Test
    void testIdenticalDigits() {
        String identicalCode = "0000000000"; // Repeated digit
        assertFalse(NationalCardUtil.nationalCodeValidation(identicalCode));
    }

    @Test
    void testEmptyOrNull() {
        assertFalse(NationalCardUtil.nationalCodeValidation(null));
        assertFalse(NationalCardUtil.nationalCodeValidation(""));
    }

    @Test
    void testLessThan10Digits() {
        String shortCode = "12345";
        assertFalse(NationalCardUtil.nationalCodeValidation(shortCode));
    }

    @Test
    void testMoreThan10Digits() {
        String longCode = "1234567890123";
        assertFalse(NationalCardUtil.nationalCodeValidation(longCode));
    }

}
