package ir.stts.bajet.redis.rate;

import ir.stts.bajet.core.constant.BajetConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
        prefix = BajetConstants.BAJET_BASE_PACKAGE + ".redis",
        name = "enabled",
        havingValue = "true"
)
public class RedisRateLimiter {

    private final static String RATE_LIMIT_KEY_FORMAT = "rate-limit:%s:%s:%s";
    private final static String RATE_LIMIT_IDENTIFIER_KEY_FORMAT = "rate-limit:%s:%s:%s:%s";

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${spring.application.name}")
    private String applicationName;

    public boolean isAllowed(String context, String method, int maxCalls, int timeWindowSec) {

        String redisKey = String.format(RATE_LIMIT_KEY_FORMAT, applicationName, context, method);

        Long count = redisTemplate.opsForValue().increment(redisKey);

        if (count != null && count == 1)
            redisTemplate.expire(redisKey, timeWindowSec, TimeUnit.SECONDS);

        return count != null && count <= maxCalls;
    }

    public boolean isAllowed(String context, String method, String identifier, int maxCalls, int timeWindowSec) {

        String redisKey = String.format(RATE_LIMIT_IDENTIFIER_KEY_FORMAT, applicationName, context, method, identifier);

        Long count = redisTemplate.opsForValue().increment(redisKey);

        if (count != null && count == 1)
            redisTemplate.expire(redisKey, timeWindowSec, TimeUnit.SECONDS);

        return count != null && count <= maxCalls;
    }
}