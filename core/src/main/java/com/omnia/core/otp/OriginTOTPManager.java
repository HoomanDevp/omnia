package com.omnia.core.otp;

import com.omnia.core.constant.OmniaConstants;
import com.omnia.core.converter.ByteConverter;
import com.omnia.core.header.constant.ClientType;
import com.omnia.core.header.constant.GatewayType;
import com.omnia.core.resilience.exception.ForbiddenException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;

@ConditionalOnProperty(
        prefix = OmniaConstants.OMNIA_BASE_PACKAGE + ".totp",
        name = "mock-enabled",
        havingValue = "false"
)
@Component
public class OriginTOTPManager implements TOTPManager {

    private static final long TIME_STEP_SECONDS = 10L;
    private static final String HMAC_ALGORITHM = "HmacSHA1";

    @Override
    public String generate(String salt, GatewayType gatewayType, ClientType clientType, long digits) {
        String seed = generateSeed(salt, gatewayType, clientType);
        return generate(seed, digits, Instant.now().getEpochSecond());
    }

    @Override
    public boolean verify(String salt, GatewayType gatewayType, ClientType clientType, String code, long digits, long validDurationSeconds) {

        int window = (int) Math.ceil((double) validDurationSeconds / TIME_STEP_SECONDS);
        String seed = generateSeed(salt, gatewayType, clientType);
        long currentTimeSeconds = Instant.now().getEpochSecond();

        for (int i = -window; i <= window; i++) {
            String candidate = generate(seed, digits, currentTimeSeconds + (i * TIME_STEP_SECONDS));
            if (candidate.equals(code)) {
                return true;
            }
        }
        return false;
    }

    private String generate(String seed, long digits, long timestampSeconds) {

        long timeWindow = timestampSeconds / TIME_STEP_SECONDS;
        try {
            byte[] secretBytes = Base64.getDecoder().decode(seed);

            byte[] timeBytes = new byte[8];
            for (int i = 7; i >= 0; i--) {
                timeBytes[i] = (byte) (timeWindow & 0xFF);
                timeWindow >>= 8;
            }

            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(secretBytes, HMAC_ALGORITHM);
            mac.init(keySpec);

            byte[] hash = mac.doFinal(timeBytes);

            int offset = hash[hash.length - 1] & 0x0F;
            int binaryCode = ((hash[offset] & 0x7F) << 24) |
                    ((hash[offset + 1] & 0xFF) << 16) |
                    ((hash[offset + 2] & 0xFF) << 8) |
                    (hash[offset + 3] & 0xFF);

            int otp = binaryCode % (int) Math.pow(10, digits);
            return String.format("%0" + digits + "d", otp);

        } catch (Exception e) {
            throw new IllegalStateException("Failed to generate TOTP", e);
        }
    }

    private String generateSeed(String salt, GatewayType gatewayType, ClientType clientType) {

        String pureSeed = String.format("%s/%s/%s", salt, gatewayType.name(), clientType.name());

        try {
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
            byte[] bytes = sha1.digest((pureSeed).getBytes());
            return ByteConverter.from(bytes).toHex();
        } catch (NoSuchAlgorithmException e) {
            throw new ForbiddenException(null);
        }
    }
}
