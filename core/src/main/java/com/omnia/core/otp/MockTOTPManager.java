package com.omnia.core.otp;

import com.omnia.core.constant.OmniaConstants;
import com.omnia.core.header.constant.ClientType;
import com.omnia.core.header.constant.GatewayType;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@ConditionalOnProperty(
        prefix = OmniaConstants.OMNIA_BASE_PACKAGE + ".totp",
        name = "mock-enabled",
        havingValue = "true"
)
@Component
@Profile({OmniaConstants.DEV_ENV, OmniaConstants.STAGE_ENV, OmniaConstants.TEST_ENV})
public class MockTOTPManager implements TOTPManager {


    private final Map<String, OTPEntry> otpStore = new ConcurrentHashMap<>();
    private final ScheduledExecutorService cleaner = Executors.newSingleThreadScheduledExecutor();

    public MockTOTPManager() {
        cleaner.scheduleAtFixedRate(this::cleanupExpiredOtps, 30, 30, TimeUnit.SECONDS);
    }

    @Override
    public String generate(String salt, GatewayType gatewayType, ClientType clientType, long digits) {
        String otp = "1".repeat((int) digits);
        String key = getKey(salt, gatewayType, clientType, digits);
        otpStore.put(key, new OTPEntry(otp, Instant.now().plusSeconds(120), gatewayType, clientType, digits));
        return otp;
    }

    @Override
    public boolean verify(String salt, GatewayType gatewayType, ClientType clientType, String code, long digits, long validDurationSeconds) {
        String key = getKey(salt, gatewayType, clientType, digits);
        OTPEntry entry = otpStore.get(key);
        if (entry != null && !entry.isExpired() && entry.code().equals(code)) {
            otpStore.remove(key);
            return true;
        }
        return false;
    }

    private String getKey(String salt, GatewayType gatewayType, ClientType clientType, long digits) {
        return salt + ":" + gatewayType + ":" + clientType + ":" + digits;
    }

    private void cleanupExpiredOtps() {
        otpStore.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }

    @PreDestroy
    public void shutdown() {
        cleaner.shutdown();
        try {
            if (!cleaner.awaitTermination(5, TimeUnit.SECONDS)) {
                cleaner.shutdownNow();
            }
        } catch (InterruptedException e) {
            cleaner.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private record OTPEntry(
            String code,
            Instant expiresAt,
            GatewayType gatewayType,
            ClientType clientType,
            long digits
    ) {
        boolean isExpired() {
            return Instant.now().isAfter(expiresAt);
        }
    }
}
