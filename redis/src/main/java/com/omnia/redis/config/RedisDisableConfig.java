package com.omnia.redis.config;

import com.omnia.core.constant.OmniaConstants;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(
        prefix = OmniaConstants.OMNIA_BASE_PACKAGE + ".redis",
        name = "enabled",
        havingValue = "false",
        matchIfMissing = true
)
@EnableAutoConfiguration(exclude = {
        RedisAutoConfiguration.class
})
public class RedisDisableConfig {
}