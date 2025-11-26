package com.company.eventpublisher.sender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;

public class JmsSenderImpl implements JmsSender {

    private static final Logger log = LoggerFactory.getLogger(JmsSenderImpl.class);

    private final JmsTemplate jmsTemplate;

    public JmsSenderImpl(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    @Override
    public boolean send(String queueName, String payload) {
        try {
            jmsTemplate.convertAndSend(queueName, payload);
            log.debug("Successfully sent message to queue: {}", queueName);
            return true;
        } catch (JmsException e) {
            log.error("Failed to send message to queue: {}", queueName, e);
            return false;
        }
    }
}
