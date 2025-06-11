package ir.stts.bajet.log.config;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@Accessors(chain = true)
@ConfigurationProperties(prefix = "ir.stts.bajet.log.splunk")
public class LogSplunkProperties {

    private boolean enabled = false;
    private String url;
    private String token;
    private String index;
    private int batchSizeCount;
    private long flushIntervalMillis = 30 * 1000;
    private boolean disableCertValidation;
}