package com.omnia.cache.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.omnia.core.constant.OmniaConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties({
        CacheManagerProperties.class
})
@ConditionalOnProperty(
        prefix = OmniaConstants.OMNIA_BASE_PACKAGE + ".cache.cache-manager",
        name = "enabled",
        havingValue = "true"
)
@EnableCaching
public class CacheConfig {

    private final CacheManagerProperties properties;

    @Bean
    @ConditionalOnBean(RedisConnectionFactory.class)
    @ConditionalOnClass(RedisConnectionFactory.class)
    public CacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {

        RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(properties.getRedisTtlInSeconds()))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.string()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.json()));

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(cacheConfig)
                .build();
    }


    @Bean
    @Primary
    @ConditionalOnClass(Caffeine.class)
    public CacheManager caffeineCacheManager() {
        Caffeine<Object, Object> caffeine = Caffeine.newBuilder()
                .initialCapacity(100)                                     // sensible default to reduce resizing
                .maximumSize(properties.getCaffeineMaxSize())             // limit to prevent OOM
                .expireAfterWrite(properties.getCaffeineTtlInSeconds(), TimeUnit.SECONDS);

        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        caffeineCacheManager.setCaffeine(caffeine);
        return caffeineCacheManager;
    }

    @Bean
    public CacheManager concurrentMapCacheManager() {
        return new ConcurrentMapCacheManager();
    }

}