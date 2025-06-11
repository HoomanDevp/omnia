package com.omnia.core.resilience.handler.config;

import com.omnia.core.constant.BajetConstants;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@Accessors(chain = true)
@ConfigurationProperties(prefix = BajetConstants.BAJET_BASE_PACKAGE + ".core.resilience.monitoring")
public class ErrorMonitoringProperties {

    private boolean enabled = false;
}