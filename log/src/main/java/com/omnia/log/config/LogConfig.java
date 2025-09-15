package com.omnia.log.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;
import java.util.Set;

@Configuration
@EnableConfigurationProperties({
        LogProperties.class,
        LogFileProperties.class,
        LogSplunkProperties.class,
        LogElasticProperties.class,
        LogConsoleProperties.class
})
public class LogConfig {
    public static final String MASK = "*****";
    public static final int REGULAR_DEPTH = 4;
    public static final int REQUEST_DEPTH = 8;
    public static final Set<String> SENSITIVE_FIELDS = new HashSet<>();
    public static final Set<String> SENSITIVE_PATHS = new HashSet<>();
}