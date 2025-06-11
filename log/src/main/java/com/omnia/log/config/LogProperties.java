package com.omnia.log.config;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@Accessors(chain = true)
@ConfigurationProperties(prefix = "com.omnia.log")
public class LogProperties {

    private LogFileProperties logFile;
    private LogSplunkProperties logSplunk;
    private LogElasticProperties logElastic;
    private LogConsoleProperties logConsole;
}