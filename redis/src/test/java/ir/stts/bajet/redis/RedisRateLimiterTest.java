package ir.stts.bajet.redis;

import ir.stts.bajet.redis.rate.RedisRateLimiter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class RedisRateLimiterTest {

    private RedisRateLimiter redisRateLimiter;

    @Mock
    private RedisTemplate<String, String> redisTemplate;
    @Mock
    private ValueOperations<String, String> valueOperations;

    @BeforeEach
    void setUp() throws Exception {

        AutoCloseable mocks = MockitoAnnotations.openMocks(this);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        redisRateLimiter = new RedisRateLimiter(redisTemplate);

        mocks.close();
    }

    @Test
    void isAllowedWhenFirstCallShouldReturnTrueAndSetExpire() {

        int maxCalls = 5;
        int timeWindowSec = 60;
        String method = "testMethod";
        String context = "testContext";
        String redisKey = "rate-limit:null:testContext:testMethod";

        when(valueOperations.increment(redisKey)).thenReturn(1L);
        boolean result = redisRateLimiter.isAllowed(context, method, maxCalls, timeWindowSec);

        assertTrue(result);
        verify(redisTemplate, times(1)).expire(redisKey, timeWindowSec, TimeUnit.SECONDS);
    }

    @Test
    void isAllowedWhenWithinLimitShouldReturnTrue() {

        int maxCalls = 5;
        int timeWindowSec = 60;
        String method = "testMethod";
        String context = "testContext";
        String redisKey = "rate-limit:null:testContext:testMethod";

        when(valueOperations.increment(redisKey)).thenReturn(3L);
        boolean result = redisRateLimiter.isAllowed(context, method, maxCalls, timeWindowSec);

        assertTrue(result);
        verify(redisTemplate, never()).expire(redisKey, timeWindowSec, TimeUnit.SECONDS);
    }

    @Test
    void isAllowedWhenExceedsLimitShouldReturnFalse() {

        int maxCalls = 5;
        int timeWindowSec = 60;
        String method = "testMethod";
        String context = "testContext";
        String redisKey = "rate-limit:null:testContext:testMethod";

        when(valueOperations.increment(redisKey)).thenReturn(6L);
        boolean result = redisRateLimiter.isAllowed(context, method, maxCalls, timeWindowSec);

        assertFalse(result);
        verify(redisTemplate, never()).expire(redisKey, timeWindowSec, TimeUnit.SECONDS);
    }

    @Test
    void isAllowedWithIdentifierWhenFirstCallShouldReturnTrueAndSetExpire() {

        int maxCalls = 5;
        int timeWindowSec = 60;
        String identifier = "user1";
        String method = "testMethod";
        String context = "testContext";
        String redisKey = "rate-limit:null:testContext:testMethod:user1";

        when(valueOperations.increment(redisKey)).thenReturn(1L);
        boolean result = redisRateLimiter.isAllowed(context, method, identifier, maxCalls, timeWindowSec);

        assertTrue(result);
        verify(redisTemplate, times(1)).expire(redisKey, timeWindowSec, TimeUnit.SECONDS);
    }

    @Test
    void isAllowedWithIdentifierWhenWithinLimitShouldReturnTrue() {

        int maxCalls = 5;
        int timeWindowSec = 60;
        String identifier = "user1";
        String method = "testMethod";
        String context = "testContext";
        String redisKey = "rate-limit:null:testContext:testMethod:user1";

        when(valueOperations.increment(redisKey)).thenReturn(3L);
        boolean result = redisRateLimiter.isAllowed(context, method, identifier, maxCalls, timeWindowSec);

        assertTrue(result);
        verify(redisTemplate, never()).expire(redisKey, timeWindowSec, TimeUnit.SECONDS);
    }

    @Test
    void isAllowedWithIdentifierWhenExceedsLimitShouldReturnFalse() {

        int maxCalls = 5;
        int timeWindowSec = 60;
        String identifier = "user1";
        String method = "testMethod";
        String context = "testContext";
        String redisKey = "rate-limit:null:testContext:testMethod:user1";

        when(valueOperations.increment(redisKey)).thenReturn(6L);
        boolean result = redisRateLimiter.isAllowed(context, method, identifier, maxCalls, timeWindowSec);

        assertFalse(result);
        verify(redisTemplate, never()).expire(redisKey, timeWindowSec, TimeUnit.SECONDS);
    }
}