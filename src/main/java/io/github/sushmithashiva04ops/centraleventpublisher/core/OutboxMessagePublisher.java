package io.github.sushmithashiva04ops.centraleventpublisher.core;

public interface OutboxMessagePublisher {

    void publish(String payload) throws Exception;

    void publish(String payload, String queueName) throws Exception;
}
