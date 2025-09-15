package com.omnia.amqp.config;

import com.omnia.core.constant.OmniaConstants;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@Getter
@Setter
@Accessors(chain = true)
@ConfigurationProperties(prefix = OmniaConstants.OMNIA_BASE_PACKAGE + ".amqp.rabbitmq.queses")
public class QueueProperties implements IQueueProperties {

    private String name;
    private boolean durable;
    private boolean exclusive;
    private boolean autoDelete;
    private Map<String, Object> arguments;
}