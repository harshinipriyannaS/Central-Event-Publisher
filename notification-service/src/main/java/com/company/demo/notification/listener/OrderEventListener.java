package com.company.demo.notification.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventListener {

    private static final Logger log = LoggerFactory.getLogger(OrderEventListener.class);

    @JmsListener(destination = "order-to-notification-queue")
    public void handleOrderCreated(String message) {
        log.info("Final destination of data - Received order: {}", message);
    }
}
