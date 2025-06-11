package com.omnia.log.config;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@Accessors(chain = true)
@ConfigurationProperties(prefix = "com.omnia.log.elastic")
public class LogElasticProperties {

    private boolean enabled = false;

    private boolean secure = false;
    private String username;
    private String password;
    private int port = 9200;
    private String host = "http://localhost";

    private int bulkSize = 100;
    private String bulkPath = "/_bulk";
    private int flushIntervalInMillis = 500;

    private int maxRetries = 3;
    private long retryDelayInMillis = 30000;

    private int queueSize = 256;
    private int maxFlushTime = 1000;
    private int discardingThreshold = -1;
}