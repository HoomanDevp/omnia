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
    public final static String MASK = "*****";
    public final static int REGULAR_DEPTH = 2;
    public final static int REQUEST_DEPTH = 4;
    public final static Set<String> SENSITIVE_FIELDS = new HashSet<>();
}