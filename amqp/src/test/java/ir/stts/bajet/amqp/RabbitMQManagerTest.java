package ir.stts.bajet.amqp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ir.stts.bajet.amqp.config.IProducerProperties;
import ir.stts.bajet.amqp.config.IRabbitMQProperties;
import ir.stts.bajet.amqp.service.RabbitMQManager;
import ir.stts.bajet.amqp.service.constant.MessageType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class RabbitMQManagerTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private IRabbitMQProperties properties;

    @Mock
    private IProducerProperties producerProperties;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private RabbitMQManager rabbitMQManager;

    @BeforeEach
    public void setup() throws Exception {

        AutoCloseable mocks = MockitoAnnotations.openMocks(this);

        mocks.close();
    }

    @Test
    public void sendMessage_ShouldSendMessageFormatted_WhenProducerConfigIsValid() throws JsonProcessingException {

        String producerName = "testProducer";
        Object message = new Object();
        String routingKey = "testRoutingKey";
        String exchange = "testExchange";
        String formattedMessage = "formattedMessage";
        when(properties.getProducers()).thenReturn(List.of(producerProperties));
        when(producerProperties.getName()).thenReturn(producerName);
        when(producerProperties.getExchange()).thenReturn(exchange);
        when(producerProperties.getRoutingKey()).thenReturn(routingKey);
        when(producerProperties.getMessageType()).thenReturn(MessageType.APPLICATION_JSON);
        when(objectMapper.writeValueAsString(any(Object.class))).thenReturn(formattedMessage);

        rabbitMQManager.sendMessage(producerName, message);

        verify(rabbitTemplate, times(1)).convertAndSend(eq(exchange), eq(routingKey), eq(formattedMessage));
    }

    @Test
    public void send_Message_ShouldThrowException_WhenProducerNotFound() {

        String producerName = "nonExistentProducer";
        Object message = new Object();

        when(properties.getProducers()).thenReturn(List.of());

        try {
            rabbitMQManager.sendMessage(producerName, message);
        } catch (IllegalArgumentException ex) {
            assert (ex.getMessage().equals("Producer not found: " + producerName));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
