package com.omnia.amqp.config;

import com.omnia.amqp.service.constant.MessageType;

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