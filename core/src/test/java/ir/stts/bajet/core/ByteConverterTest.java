package ir.stts.bajet.core;

import ir.stts.bajet.core.converter.ByteConverter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ByteConverterTest {

    @Test
    void testFromValidByteArray() {

        byte[] data = {1, 2, 3, 4};
        ByteConverter byteConverter = ByteConverter.from(data);

        assertNotNull(byteConverter);
        assertArrayEquals(data, byteConverter.getBytes());
    }

    @Test
    void testFromNullByteArray() {
        assertThrows(IllegalArgumentException.class, () -> ByteConverter.from(null));
    }

    @Test
    void testFromEmptyByteArray() {

        byte[] emptyData = {};
        assertThrows(IllegalArgumentException.class, () -> ByteConverter.from(emptyData));
    }

    @Test
    void testToHex() {

        byte[] data = {1, 2, 3, 4};
        ByteConverter byteConverter = ByteConverter.from(data);

        String expectedHex = "01020304";
        assertEquals(expectedHex, byteConverter.toHex());
    }

    @Test
    void testToBase64() {

        byte[] data = {1, 2, 3, 4};
        ByteConverter byteConverter = ByteConverter.from(data);

        String expectedBase64 = "AQIDBA==";
        assertEquals(expectedBase64, byteConverter.toBase64());
    }

    @Test
    void testToUtf8() {

        byte[] data = {72, 101, 108, 108, 111};
        ByteConverter byteConverter = ByteConverter.from(data);

        String expectedUtf8 = "Hello";
        assertEquals(expectedUtf8, byteConverter.toUtf8());
    }

    @Test
    void testToInt() {

        byte[] data = {0, 0, 0, 10};
        ByteConverter byteConverter = ByteConverter.from(data);

        assertEquals(10, byteConverter.toInt());
    }

    @Test
    void testToIntThrowsExceptionForShortArray() {

        byte[] data = {0, 0, 0};
        ByteConverter byteConverter = ByteConverter.from(data);

        assertThrows(IllegalArgumentException.class, byteConverter::toInt);
    }

    @Test
    void testToLong() {

        byte[] data = {0, 0, 0, 0, 0, 0, 0, 10};
        ByteConverter byteConverter = ByteConverter.from(data);

        assertEquals(10L, byteConverter.toLong());
    }

    @Test
    void testToLongThrowsExceptionForShortArray() {

        byte[] data = {0, 0, 0, 0, 0, 0, 0};
        ByteConverter byteConverter = ByteConverter.from(data);

        assertThrows(IllegalArgumentException.class, byteConverter::toLong);
    }
}