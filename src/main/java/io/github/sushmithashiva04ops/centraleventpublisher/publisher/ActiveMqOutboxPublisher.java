package io.github.sushmithashiva04ops.centraleventpublisher.publisher;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import io.github.sushmithashiva04ops.centraleventpublisher.core.OutboxMessagePublisher;

@Component
public class ActiveMqOutboxPublisher implements OutboxMessagePublisher {

    private final JmsTemplate jmsTemplate;

    public ActiveMqOutboxPublisher(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    @Override
    public void publish(String payload, String queueName) {

        // -------------------------------------------
        // NULL / EMPTY queue safe guard (IMPORTANT)
        // -------------------------------------------
        if (queueName == null || queueName.isBlank()) {
            System.out.println("Skipping publish → queueName is NULL/EMPTY");
            return;
        }

        jmsTemplate.convertAndSend(queueName, payload);
    }

    @Override
    public void publish(String payload) {
        throw new UnsupportedOperationException("Use publish(payload, queueName) instead");
    }
}
