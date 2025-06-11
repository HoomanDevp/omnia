package ir.stts.bajet.redis.config;

import ir.stts.bajet.core.constant.BajetConstants;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@Accessors(chain = true)
@ConfigurationProperties(prefix = BajetConstants.BAJET_BASE_PACKAGE + ".redis")
public class RedisProperties {

    private boolean enabled = false;
    private int shufflingSalt = 34813;
}