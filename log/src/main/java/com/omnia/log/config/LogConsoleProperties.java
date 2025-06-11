package com.omnia.log.config;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@Accessors(chain = true)
@ConfigurationProperties(prefix = "com.omnia.log.console")
public class LogConsoleProperties {

    private boolean enabled = false;
    private String pattern;
}