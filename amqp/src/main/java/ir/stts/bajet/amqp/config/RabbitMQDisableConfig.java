package ir.stts.bajet.amqp.config;

import ir.stts.bajet.core.constant.BajetConstants;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(
        prefix = BajetConstants.BAJET_BASE_PACKAGE + ".amqp.rabbitmq",
        name = "enabled",
        havingValue = "false",
        matchIfMissing = true
)
@EnableAutoConfiguration(exclude = {
        RabbitAutoConfiguration.class
})
public class RabbitMQDisableConfig {
}