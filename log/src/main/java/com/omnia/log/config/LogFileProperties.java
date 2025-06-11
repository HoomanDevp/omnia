package com.omnia.log.config;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@Accessors(chain = true)
@ConfigurationProperties(prefix = "com.omnia.log.file")
public class LogFileProperties {

    private boolean enabled = false;
    private String name;
    private String path;
    private int maxHistory;
    private String maxFileSize;
    private String totalSize;
    private String pattern;
}