package com.company.demo.order.listener;

import com.company.demo.order.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class InventoryEventListener {

    private static final Logger log = LoggerFactory.getLogger(InventoryEventListener.class);
    private final OrderService orderService;

    public InventoryEventListener(OrderService orderService) {
        this.orderService = orderService;
    }

    @JmsListener(destination = "inventory-to-order-queue")
    public void handleInventoryUpdate(String message) {
        log.info("Received inventory update: {}", message);
        orderService.processInventoryUpdate(message);
    }
}
