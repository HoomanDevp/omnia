package ir.stts.bajet.amqp.config;

import ir.stts.bajet.amqp.service.constant.MessageType;
import ir.stts.bajet.core.constant.BajetConstants;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@Accessors(chain = true)
@ConfigurationProperties(prefix = BajetConstants.BAJET_BASE_PACKAGE + ".amqp.rabbitmq.producers")
public class ProducerProperties implements IProducerProperties {

    private String name;
    private String exchange;
    private String routingKey;
    private MessageType messageType;
}