package com.omnia.amqp.config;

import com.omnia.core.constant.OmniaConstants;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(
        prefix = OmniaConstants.OMNIA_BASE_PACKAGE + ".amqp.rabbitmq",
        name = "enabled",
        havingValue = "false",
        matchIfMissing = true
)
@EnableAutoConfiguration(exclude = {
        RabbitAutoConfiguration.class
})
public class RabbitMQDisableConfig {
}