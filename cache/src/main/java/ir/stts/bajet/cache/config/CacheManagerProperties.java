package ir.stts.bajet.cache.config;

import ir.stts.bajet.core.constant.BajetConstants;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@Accessors(chain = true)
@ConfigurationProperties(prefix = BajetConstants.BAJET_BASE_PACKAGE + ".cache.cache-manager")
public class CacheManagerProperties {

    private boolean enabled = false;
    private long ttlInSeconds = 3600;
}