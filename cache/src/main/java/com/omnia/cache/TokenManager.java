package com.omnia.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.omnia.cache.config.InMemoryTokenProperties;
import com.omnia.log.LogSpec;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

@Slf4j
@Component
@RequiredArgsConstructor
@EnableConfigurationProperties({
        InMemoryTokenProperties.class
})
public class TokenManager {

    private Duration DEFAULT_EXPIRATION_SECONDS;

    private final ObjectMapper objectMapper;
    private final InMemoryTokenProperties properties;

    private final Map<String, TokenInfo> store = new ConcurrentHashMap<>();
    private final Map<String, ReentrantLock> locks = new ConcurrentHashMap<>();

    @PostConstruct
    void init() {
        DEFAULT_EXPIRATION_SECONDS = Duration.ofSeconds(properties.getExpirationSeconds());
    }

    public String get(String key) {

        ReentrantLock lock = locks.computeIfAbsent(key, k -> new ReentrantLock());

        lock.lock();
        try {
            TokenInfo tokenInfo = store.get(key);

            if (tokenInfo == null)
                return null;

            if (isTokenExpired(tokenInfo.expiry)) {
                remove(key);
                return null;
            }

            return tokenInfo.token();
        } finally {
            lock.unlock();
            locks.remove(key);
        }
    }

    public <T> T get(String key, Duration expiry, TypeReference<T> typeReference, Supplier<T> readData) {

        String cacheData = null;
        try {
            cacheData = get(key);
        } catch (Exception e) {

            log.error("{}", LogSpec
                    .ofException("Cannot read data from in-memory cache with key: " + key, e));
        }

        T data;
        if (cacheData == null) {

            data = readData.get();
            try {
                put(key, objectMapper.writeValueAsString(data), expiry);
            } catch (JsonProcessingException e) {

                log.error(LogSpec
                        .ofException("Cannot add data to in-memory cache with key: " + key, e)
                        .toString());
            }
        } else {

            try {
                data = objectMapper.readValue(cacheData, typeReference);
                if (ObjectUtils.isEmpty(data))
                    data = readData.get();
            } catch (JsonProcessingException e) {

                data = readData.get();
                log.error("{}", LogSpec
                        .ofException("Cannot convert data to target list", e));
            }
        }

        return data;
    }

    public void put(String key, String token) {

        put(key, token, DEFAULT_EXPIRATION_SECONDS);
    }

    public void put(String key, String token, Duration expiry) {

        ReentrantLock lock = locks.computeIfAbsent(key, k -> new ReentrantLock());

        lock.lock();
        try {
            store.put(key, new TokenInfo(token, LocalDateTime.now().plusSeconds(expiry.toSeconds())));
        } finally {
            lock.unlock();
            locks.remove(key);
        }
    }

    public void remove(String key) {

        ReentrantLock lock = locks.computeIfAbsent(key, k -> new ReentrantLock());

        lock.lock();
        try {
            store.remove(key);
        } finally {
            lock.unlock();
            locks.remove(key);
        }
    }

    public void removeByValue(String token) {

        ReentrantLock lock = locks.computeIfAbsent(token, k -> new ReentrantLock());

        lock.lock();
        try {
            store.entrySet().removeIf(entry -> entry.getValue().token().equals(token));
        } finally {
            lock.unlock();
            locks.remove(token);
        }
    }

    private boolean isTokenExpired(LocalDateTime expiry) {
        return expiry.isBefore(LocalDateTime.now());
    }

    private record TokenInfo(String token, LocalDateTime expiry) {
    }
}