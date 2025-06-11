package ir.stts.bajet.core.resilience.handler.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
        ErrorMonitoringProperties.class,
})
public class ErrorMonitoringConfig {
}