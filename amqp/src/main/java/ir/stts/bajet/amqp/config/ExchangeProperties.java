package ir.stts.bajet.amqp.config;

import ir.stts.bajet.core.constant.BajetConstants;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@Getter
@Setter
@Accessors(chain = true)
@ConfigurationProperties(prefix = BajetConstants.BAJET_BASE_PACKAGE + ".amqp.rabbitmq.exchanges")
public class ExchangeProperties implements IExchangeProperties {

    private String name;
    private String type; // topic, direct, fanout, headers
    private boolean durable;
    private boolean autoDelete;
    private Map<String, Object> arguments;
}