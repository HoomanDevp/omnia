package ir.stts.bajet.amqp.config;

import java.util.Map;

public interface IQueueProperties {

    String getName();

    IQueueProperties setName(String name);

    boolean isDurable();

    IQueueProperties setDurable(boolean durable);

    boolean isExclusive();

    IQueueProperties setExclusive(boolean exclusive);

    boolean isAutoDelete();

    IQueueProperties setAutoDelete(boolean autoDelete);

    Map<String, Object> getArguments();

    IQueueProperties setArguments(Map<String, Object> arguments);
}