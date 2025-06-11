package com.omnia.core;

import com.omnia.core.converter.HexConverter;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

class HexConverterTest {

    @Test
    void testFromValidHexString() {

        String hexString = "1a2b3c4d";
        HexConverter hexConverter = HexConverter.from(hexString);

        assertNotNull(hexConverter);
        assertEquals(hexString, hexConverter.getHexString());
    }

    @Test
    void testFromNullHexString() {
        assertThrows(IllegalArgumentException.class, () -> HexConverter.from((String) null));
    }

    @Test
    void testFromEmptyHexString() {
        assertThrows(IllegalArgumentException.class, () -> HexConverter.from(""));
    }

    @Test
    void testFromOddLengthHexString() {

        String hexString = "1a2b3";
        assertThrows(IllegalArgumentException.class, () -> HexConverter.from(hexString));
    }

    @Test
    void testToUpperCase() {

        String hexString = "1a2b3c4d";
        HexConverter hexConverter = HexConverter.from(hexString);

        assertEquals("1A2B3C4D", hexConverter.toUpperCase());
    }

    @Test
    void testToLowerCase() {

        String hexString = "1A2B3C4D";
        HexConverter hexConverter = HexConverter.from(hexString);

        assertEquals("1a2b3c4d", hexConverter.toLowerCase());
    }

    @Test
    void testToBytes() {

        String hexString = "1a2b3c4d";
        HexConverter hexConverter = HexConverter.from(hexString);

        byte[] expectedBytes = {0x1a, 0x2b, 0x3c, 0x4d};
        assertArrayEquals(expectedBytes, hexConverter.toBytes());
    }

    @Test
    void testToInt() {

        String hexString = "1A";
        HexConverter hexConverter = HexConverter.from(hexString);

        assertEquals(26, hexConverter.toInt());
    }

    @Test
    void testToLong() {

        String hexString = "1A2B3C4D";
        HexConverter hexConverter = HexConverter.from(hexString);

        assertEquals(439041101, hexConverter.toLong());
    }

    @Test
    void testToBigInteger() {

        String hexString = "1A2B3C4D";
        HexConverter hexConverter = HexConverter.from(hexString);

        BigInteger expectedBigInteger = new BigInteger("1A2B3C4D", 16);
        assertEquals(expectedBigInteger, hexConverter.toBigInteger());
    }
}