package com.omnia.redis.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.omnia.core.constant.OmniaConstants;
import com.omnia.core.util.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.security.InvalidAlgorithmParameterException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(
        prefix = OmniaConstants.OMNIA_BASE_PACKAGE + ".redis",
        name = "enabled",
        havingValue = "true"
)
public class SecureRedisService {

    @Value("${com.omnia.redis.shuffling-salt:34813}")
    private int shufflingSalt;

    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String SECURE_FORMAT = "ENC(%s)";
    private static final Pattern SECURE_PATTERN = Pattern.compile("ENC\\((.+)\\)");

    public <T> T get(String key, Class<T> clazz) throws JsonProcessingException, InvalidAlgorithmParameterException {

        if (ObjectUtils.isEmpty(key))
            return null;

        String data = redisTemplate.opsForValue().get(key);
        if (data == null)
            return null;

        Matcher matcher = SECURE_PATTERN.matcher(data);
        if (!matcher.matches())
            throw new InvalidAlgorithmParameterException();

        data = StringUtil.arrange(matcher.group(1), shufflingSalt);

        return objectMapper.readValue(data, clazz);
    }

    public <T> T get(String key, TypeReference<T> clazz) throws JsonProcessingException, InvalidAlgorithmParameterException {

        if (ObjectUtils.isEmpty(key))
            return null;

        String data = redisTemplate.opsForValue().get(key);
        if (data == null)
            return null;

        Matcher matcher = SECURE_PATTERN.matcher(data);
        if (!matcher.matches())
            throw new InvalidAlgorithmParameterException();

        data = StringUtil.arrange(matcher.group(1), shufflingSalt);

        return objectMapper.readValue(data, clazz);
    }

    public <T> T getAndDelete(String key, Class<T> clazz) throws JsonProcessingException, InvalidAlgorithmParameterException {
        if (ObjectUtils.isEmpty(key))
            return null;

        String data = redisTemplate.opsForValue().get(key);
        if (data == null)
            return null;

        Matcher matcher = SECURE_PATTERN.matcher(data);
        if (!matcher.matches())
            throw new InvalidAlgorithmParameterException();

        data = StringUtil.arrange(matcher.group(1), shufflingSalt);
        redisTemplate.delete(key);
        return objectMapper.readValue(data, clazz);
    }

    public <T> T getAndDelete(String key, TypeReference<T> clazz) throws JsonProcessingException, InvalidAlgorithmParameterException {
        if (ObjectUtils.isEmpty(key))
            return null;

        String data = redisTemplate.opsForValue().get(key);
        if (data == null)
            return null;

        Matcher matcher = SECURE_PATTERN.matcher(data);
        if (!matcher.matches())
            throw new InvalidAlgorithmParameterException();

        data = StringUtil.arrange(matcher.group(1), shufflingSalt);
        redisTemplate.delete(key);
        return objectMapper.readValue(data, clazz);
    }

    public <T> void set(String key, T value) throws JsonProcessingException {

        if (ObjectUtils.isEmpty(key) || value == null)
            return;

        redisTemplate.opsForValue().set(
                key,
                String.format(SECURE_FORMAT, StringUtil.shuffle(objectMapper.writeValueAsString(value), shufflingSalt)));
    }

    public void delete(String key) {
        if (ObjectUtils.isEmpty(key))
            return;

        redisTemplate.delete(key);
    }

    public <T> void set(String key, T value, long ttl) throws JsonProcessingException {

        if (ObjectUtils.isEmpty(key) || value == null || ttl <= 0)
            return;

        redisTemplate.opsForValue().set(
                key,
                String.format(SECURE_FORMAT, StringUtil.shuffle(objectMapper.writeValueAsString(value), shufflingSalt)),
                ttl, TimeUnit.SECONDS);
    }
}