package com.omnia.amqp.config;

import com.omnia.core.constant.BajetConstants;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
@ConfigurationProperties(prefix = BajetConstants.BAJET_BASE_PACKAGE + ".amqp.rabbitmq")
public class RabbitMQProperties implements IRabbitMQProperties {

    private boolean enabled = false;
    private List<IQueueProperties> queues;
    private List<IBindingProperties> bindings;
    private List<IExchangeProperties> exchanges;
    private List<IProducerProperties> producers;
}