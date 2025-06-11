package com.omnia.redis.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.omnia.core.constant.BajetConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(
        prefix = BajetConstants.BAJET_BASE_PACKAGE + ".redis",
        name = "enabled",
        havingValue = "true"
)
public class RedisService {

    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, String> redisTemplate;

    public <T> T get(String key, Class<T> clazz) throws JsonProcessingException {

        if (ObjectUtils.isEmpty(key))
            return null;

        String data = redisTemplate.opsForValue().get(key);
        if (data == null)
            return null;

        return objectMapper.readValue(data, clazz);
    }

    public <T> T getAndDelete(String key, Class<T> clazz) throws JsonProcessingException {
        if (ObjectUtils.isEmpty(key))
            return null;

        String data = redisTemplate.opsForValue().get(key);
        if (data == null)
            return null;

        redisTemplate.delete(key);
        return objectMapper.readValue(data, clazz);
    }

    public <T> void set(String key, T value) throws JsonProcessingException {

        if (ObjectUtils.isEmpty(key) || value == null)
            return;

        redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(value));
    }

    public <T> void set(String key, T value, long ttl) throws JsonProcessingException {

        if (ObjectUtils.isEmpty(key) || value == null || ttl <= 0)
            return;

        redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(value), ttl, TimeUnit.SECONDS);
    }
}