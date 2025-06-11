package com.omnia.amqp.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.omnia.amqp.service.RabbitMQManager;
import com.omnia.core.constant.BajetConstants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@EnableConfigurationProperties({
        QueueProperties.class,
        BindingProperties.class,
        ExchangeProperties.class,
        ProducerProperties.class,
        RabbitMQProperties.class
})
@ConditionalOnProperty(
        prefix = BajetConstants.BAJET_BASE_PACKAGE + ".amqp.rabbitmq",
        name = "enabled",
        havingValue = "true"
)
public class RabbitMQConfig {

    @Bean
    public List<Queue> queues(RabbitMQProperties rabbitMQProperties) {
        return Initializer.initQueues(rabbitMQProperties.getQueues());
    }

    @Bean
    public List<Exchange> exchanges(RabbitMQProperties rabbitMQProperties) {
        return Initializer.initExchanges(rabbitMQProperties.getExchanges());
    }

    @Bean
    public List<Binding> bindings(List<Queue> queues, List<Exchange> exchanges, RabbitMQProperties rabbitMQProperties) {
        return Initializer.initBindings(queues, exchanges, rabbitMQProperties.getBindings());
    }

    @Bean
    public RabbitMQManager rabbitMQManager(ObjectMapper objectMapper, RabbitTemplate rabbitTemplate, RabbitMQProperties rabbitMQProperties) {
        return new RabbitMQManager(objectMapper, rabbitTemplate, rabbitMQProperties);
    }
}