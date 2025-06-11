package com.omnia.amqp.config;

import org.springframework.amqp.core.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Initializer {

    public static List<Exchange> initExchanges(List<IExchangeProperties> exchangeProperties) {

        List<Exchange> exchanges = new ArrayList<>();
        for (IExchangeProperties config : exchangeProperties) {

            Exchange exchange = switch (config.getType().toLowerCase()) {

                case "topic" -> new TopicExchange(
                        config.getName(),
                        config.isDurable(),
                        config.isAutoDelete(),
                        config.getArguments());
                case "direct" -> new DirectExchange(
                        config.getName(),
                        config.isDurable(),
                        config.isAutoDelete(),
                        config.getArguments());
                case "fanout" -> new FanoutExchange(
                        config.getName(),
                        config.isDurable(),
                        config.isAutoDelete(),
                        config.getArguments());
                case "headers" -> new HeadersExchange(
                        config.getName(),
                        config.isDurable(),
                        config.isAutoDelete(),
                        config.getArguments());
                default -> throw new IllegalArgumentException("Unknown exchange type: " + config.getType());
            };

            exchanges.add(exchange);
        }

        return exchanges;
    }

    public static List<Binding> initBindings(List<Queue> queues, List<Exchange> exchanges, List<IBindingProperties> bindingProperties) {

        List<Binding> bindings = new ArrayList<>();
        for (IBindingProperties config : bindingProperties) {

            Queue queue = queues.stream()
                    .filter(q -> q.getName().equals(config.getQueue()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Queue not found: " + config.getQueue()));
            Exchange exchange = exchanges.stream()
                    .filter(e -> e.getName().equals(config.getExchange()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Exchange not found: " + config.getExchange()));

            switch (exchange) {

                case TopicExchange topicExchange -> bindings.add(BindingBuilder
                        .bind(queue)
                        .to(topicExchange)
                        .with(config.getRoutingKey()));
                case DirectExchange directExchange -> bindings.add(BindingBuilder
                        .bind(queue)
                        .to(directExchange)
                        .with(config.getRoutingKey()));
                case FanoutExchange fanoutExchange -> bindings.add(BindingBuilder
                        .bind(queue)
                        .to(fanoutExchange));
                case HeadersExchange headersExchange -> bindings.add(BindingBuilder
                        .bind(queue)
                        .to(headersExchange)
                        .where(config.getRoutingKey())
                        .exists());
                case null, default -> throw new IllegalArgumentException("Unsupported exchange type for binding: "
                        + Objects.requireNonNull(exchange).getClass().getSimpleName());
            }
        }

        return bindings;
    }

    public static List<Queue> initQueues(List<IQueueProperties> queueProperties) {

        List<Queue> queues = new ArrayList<>();
        for (IQueueProperties config : queueProperties)
            queues.add(new Queue(
                    config.getName(),
                    config.isDurable(),
                    config.isExclusive(),
                    config.isAutoDelete(),
                    config.getArguments()));

        return queues;
    }
}