package com.omnia.amqp.config;

import java.util.Map;

public interface IExchangeProperties {

    String getName();

    IExchangeProperties setName(String name);

    String getType(); // topic, direct, fanout, headers

    IExchangeProperties setType(String type); // topic, direct, fanout, headers

    boolean isDurable();

    IExchangeProperties setDurable(boolean durable);

    boolean isAutoDelete();

    IExchangeProperties setAutoDelete(boolean autoDelete);

    Map<String, Object> getArguments();

    IExchangeProperties setArguments(Map<String, Object> arguments);
}