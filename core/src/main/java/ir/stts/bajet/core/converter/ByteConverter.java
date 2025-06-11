package ir.stts.bajet.core.converter;

import lombok.Getter;

import java.util.Base64;

@Getter
public final class ByteConverter {

    private final byte[] bytes;

    private ByteConverter(byte[] bytes) {
        this.bytes = bytes;
    }

    public static ByteConverter from(byte[] bytes) {

        if (bytes == null || bytes.length == 0)
            throw new IllegalArgumentException("Byte array cannot be null or empty");

        return new ByteConverter(bytes);
    }

    public String toHex() {

        StringBuilder hexString = new StringBuilder();
        for (byte aByte : bytes) {

            String hex = Integer.toHexString(0xFF & aByte);
            if (hex.length() == 1)
                hexString.append('0');

            hexString.append(hex);
        }

        return hexString.toString();
    }

    public String toBase64() {

        return Base64.getEncoder().encodeToString(bytes);
    }

    public String toUtf8() {

        return new String(bytes);
    }

    public int toInt() {

        if (bytes != null && bytes.length >= 4)
            return ((bytes[0] & 0xFF) << 24) | ((bytes[1] & 0xFF) << 16) | ((bytes[2] & 0xFF) << 8) | (bytes[3] & 0xFF);
        else
            throw new IllegalArgumentException("Byte array must be at least 4 bytes long to convert to int");
    }

    public long toLong() {

        if (bytes != null && bytes.length >= 8)
            return ((long) (bytes[0] & 0xFF) << 56)
                    | ((long) (bytes[1] & 0xFF) << 48)
                    | ((long) (bytes[2] & 0xFF) << 40)
                    | ((long) (bytes[3] & 0xFF) << 32)
                    | ((long) (bytes[4] & 0xFF) << 24)
                    | ((long) (bytes[5] & 0xFF) << 16)
                    | ((long) (bytes[6] & 0xFF) << 8)
                    | ((long) (bytes[7] & 0xFF));
        else
            throw new IllegalArgumentException("Byte array must be at least 8 bytes long to convert to long");
    }
}