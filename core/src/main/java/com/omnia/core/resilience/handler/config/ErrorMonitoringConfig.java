package com.omnia.core.resilience.handler.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
        ErrorMonitoringProperties.class,
})
public class ErrorMonitoringConfig {
}