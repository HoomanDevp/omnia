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
@ConfigurationProperties(prefix = OmniaConstants.OMNIA_BASE_PACKAGE + ".amqp.rabbitmq.exchanges")
public class ExchangeProperties implements IExchangeProperties {

    private String name;
    private String type; // topic, direct, fanout, headers
    private boolean durable;
    private boolean autoDelete;
    private Map<String, Object> arguments;
}