package com.omnia.amqp.config;

public interface IBindingProperties {

    String getQueue();

    IBindingProperties setQueue(String queue);

    String getExchange();

    IBindingProperties setExchange(String exchange);

    String getRoutingKey();

    IBindingProperties setRoutingKey(String routingKey);
}