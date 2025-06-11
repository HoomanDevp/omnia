package ir.stts.bajet.amqp.config;

import java.util.List;

public interface IRabbitMQProperties {

    List<IQueueProperties> getQueues();

    IRabbitMQProperties setQueues(List<IQueueProperties> queuesProperties);

    List<IBindingProperties> getBindings();

    IRabbitMQProperties setBindings(List<IBindingProperties> bindingProperties);

    List<IExchangeProperties> getExchanges();

    IRabbitMQProperties setExchanges(List<IExchangeProperties> exchangeProperties);

    List<IProducerProperties> getProducers();

    IRabbitMQProperties setProducers(List<IProducerProperties> producerProperties);
}