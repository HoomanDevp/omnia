package ir.stts.bajet.redis.config;

import ir.stts.bajet.core.constant.BajetConstants;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(
        prefix = BajetConstants.BAJET_BASE_PACKAGE + ".redis",
        name = "enabled",
        havingValue = "false",
        matchIfMissing = true
)
@EnableAutoConfiguration(exclude = {
        RedisAutoConfiguration.class
})
public class RedisDisableConfig {
}