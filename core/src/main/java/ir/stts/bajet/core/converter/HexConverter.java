package ir.stts.bajet.core.converter;

import lombok.Getter;

import java.math.BigInteger;

@Getter
public class HexConverter {

    private final String hexString;

    private HexConverter(String hexString) {
        this.hexString = hexString;
    }

    public static HexConverter from(String hexString) {

        if (hexString == null || hexString.isEmpty())
            throw new IllegalArgumentException("Hex string cannot be null or empty");
        if (hexString.length() % 2 != 0)
            throw new IllegalArgumentException("Hex string must have an even length");

        return new HexConverter(hexString);
    }

    public String toUpperCase() {

        return this.hexString.toUpperCase();
    }

    public String toLowerCase() {

        return this.hexString.toLowerCase();
    }

    public byte[] toBytes() {

        int length = hexString.length();
        byte[] bytes = new byte[length / 2];
        for (int i = 0; i < length; i += 2)
            bytes[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));

        return bytes;
    }

    public int toInt() {

        return Integer.parseUnsignedInt(hexString, 16);
    }

    public long toLong() {

        return Long.parseUnsignedLong(hexString, 16);
    }

    public BigInteger toBigInteger() {

        return new BigInteger(hexString, 16);
    }
}