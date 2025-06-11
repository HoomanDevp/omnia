package com.omnia.redis.lock;

import com.omnia.core.constant.BajetConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(
        prefix = BajetConstants.BAJET_BASE_PACKAGE + ".redis",
        name = "enabled",
        havingValue = "true"
)
public class DistributedRedisLockService {

    private static final String LOCK_VALUE = "LOCKED";
    private final RedisTemplate<String, String> redisTemplate;

    public boolean lock(String lockName) {

        Boolean success = redisTemplate.opsForValue().setIfAbsent(lockName, LOCK_VALUE);
        return Boolean.TRUE.equals(success);
    }

    public boolean unLock(String lockName) {

        Boolean exists = redisTemplate.hasKey(lockName);
        if (Boolean.TRUE.equals(exists)) {

            redisTemplate.delete(lockName);
            return true;
        }

        return false;
    }

    public boolean safeLock(String lockName, long ttlInSeconds) {

        Boolean success = redisTemplate.opsForValue().setIfAbsent(lockName, LOCK_VALUE, ttlInSeconds, TimeUnit.SECONDS);
        return Boolean.TRUE.equals(success);
    }
}