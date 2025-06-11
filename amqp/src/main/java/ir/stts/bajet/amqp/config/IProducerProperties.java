package ir.stts.bajet.amqp.config;

import ir.stts.bajet.amqp.service.constant.MessageType;

public interface IProducerProperties {

    String getName();

    IProducerProperties setName(String name);

    String getExchange();

    IProducerProperties setExchange(String exchange);

    String getRoutingKey();

    IProducerProperties setRoutingKey(String routingKey);

    MessageType getMessageType();

    IProducerProperties setMessageType(MessageType messageType);
}