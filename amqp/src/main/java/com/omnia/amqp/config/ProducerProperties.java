package com.omnia.amqp.config;

import com.omnia.amqp.service.constant.MessageType;
import com.omnia.core.constant.OmniaConstants;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@Accessors(chain = true)
@ConfigurationProperties(prefix = OmniaConstants.OMNIA_BASE_PACKAGE + ".amqp.rabbitmq.producers")
public class ProducerProperties implements IProducerProperties {

    private String name;
    private String exchange;
    private String routingKey;
    private MessageType messageType;
}