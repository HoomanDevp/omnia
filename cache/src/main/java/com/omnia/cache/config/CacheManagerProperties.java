package com.omnia.cache.config;

import com.omnia.core.constant.OmniaConstants;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@Accessors(chain = true)
@ConfigurationProperties(prefix = OmniaConstants.OMNIA_BASE_PACKAGE + ".cache.cache-manager")
public class CacheManagerProperties {

    private boolean enabled = false;
    private long redisTtlInSeconds = 3600;
    private long caffeineTtlInSeconds = 60;
    private long caffeineMaxSize = 5000;
}