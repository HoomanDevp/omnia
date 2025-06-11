package com.omnia.redis.otp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.omnia.core.constant.BajetConstants;
import com.omnia.core.resilience.constant.IErrorCode;
import com.omnia.core.resilience.exception.InvalidDataException;
import com.omnia.redis.service.SecureRedisService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.function.Function;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(
        prefix = BajetConstants.BAJET_BASE_PACKAGE + ".redis",
        name = "enabled",
        havingValue = "true"
)
public class OtpRedisService {

    @Value("${spring.application.name}")
    private String applicationName;
    private final SecureRedisService secureRedisService;

    private final static String OTP_KEY_FORMAT = "%s:otp:%s:%s";

    @Getter
    @Setter
    @Accessors(chain = true)
    public static class OtpVerificationData {
        private String otp;
        private String otpHash;
    }

    @FunctionalInterface
    public interface OtpGenerateFunction extends Supplier<String> {
    }

    @FunctionalInterface
    public interface OtpVerifyFunction extends Function<OtpVerificationData, Boolean> {
    }

    public String store(String key, String action, long ttlInSecond, OtpGenerateFunction otpGenerateFunction) throws InvalidAlgorithmParameterException, JsonProcessingException {

        validateInputs(key, action, otpGenerateFunction);
        String redisOtpKey = String.format(OTP_KEY_FORMAT, applicationName, key, action);
        String storedOtpHash = secureRedisService.get(redisOtpKey, String.class);
        if (storedOtpHash != null)
            return storedOtpHash;

        String otpHash = otpGenerateFunction.get();
        secureRedisService.set(redisOtpKey, otpHash, ttlInSecond);

        return otpHash;
    }

    public String store(String key, String action, long ttlInSecond, boolean force, OtpGenerateFunction otpGenerateFunction) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, JsonProcessingException {

        validateInputs(key, action, otpGenerateFunction);
        String redisOtpKey = String.format(OTP_KEY_FORMAT, applicationName, key, action);
        String storedOtpHash = secureRedisService.get(redisOtpKey, String.class);
        if (storedOtpHash != null) {

            if (force) {

                String otpHash = otpGenerateFunction.get();
                secureRedisService.set(redisOtpKey, otpHash, ttlInSecond);

                return otpHash;
            }

            return storedOtpHash;
        }

        String otpHash = otpGenerateFunction.get();
        secureRedisService.set(redisOtpKey, otpHash, ttlInSecond);

        return otpHash;
    }

    public boolean verify(String key, String action, String otp, OtpVerifyFunction verifyFunction) throws InvalidAlgorithmParameterException, JsonProcessingException {

        validateInputs(key, action, verifyFunction);
        String redisOtpKey = String.format(OTP_KEY_FORMAT, applicationName, key, action);
        String storedOtpHash = secureRedisService.getAndDelete(redisOtpKey, String.class);
        if (!StringUtils.hasText(storedOtpHash))
            return false;

        return verifyFunction.apply(new OtpVerificationData()
                .setOtp(otp)
                .setOtpHash(storedOtpHash));
    }

    private void validateInputs(Object... objects) {

        for (Object object : objects)
            if (ObjectUtils.isEmpty(object))
                throw new InvalidDataException(IErrorCode._INFR_10022);
    }
}