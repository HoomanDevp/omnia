package ir.stts.bajet.amqp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ir.stts.bajet.amqp.config.IProducerProperties;
import ir.stts.bajet.amqp.config.IRabbitMQProperties;
import ir.stts.bajet.amqp.service.constant.MessageType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@RequiredArgsConstructor
public class RabbitMQManager {

    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;
    private final IRabbitMQProperties properties;

    public void sendMessage(String producerName, Object message) throws JsonProcessingException {

        IProducerProperties producerProperties = properties.getProducers().stream()
                .filter(p -> p.getName().equals(producerName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Producer not found: " + producerName));

        Object formattedMessage = this.formatMessage(producerProperties.getMessageType(), message);

        rabbitTemplate.convertAndSend(
                producerProperties.getExchange(),
                producerProperties.getRoutingKey(),
                formattedMessage
        );
    }

    private Object formatMessage(MessageType messageType, Object message) throws JsonProcessingException {

        return switch (messageType) {
            case TEXT_PLAIN -> message.toString();
            case APPLICATION_JSON -> objectMapper.writeValueAsString(message);
        };
    }
}