package com.omnia.amqp.config;

import com.omnia.core.constant.BajetConstants;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@Accessors(chain = true)
@ConfigurationProperties(prefix = BajetConstants.BAJET_BASE_PACKAGE + ".amqp.rabbitmq.bindings")
public class BindingProperties implements IBindingProperties {

    private String queue;
    private String exchange;
    private String routingKey;
}