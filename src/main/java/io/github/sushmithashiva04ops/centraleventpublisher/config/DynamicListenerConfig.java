package io.github.sushmithashiva04ops.centraleventpublisher.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import io.github.sushmithashiva04ops.centraleventpublisher.listener.DynamicOutboxListener;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.MessageListener;
import jakarta.jms.TextMessage;

@Configuration
public class DynamicListenerConfig {

    private final ConnectionFactory connectionFactory;

    public DynamicListenerConfig(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Bean
    public List<DefaultMessageListenerContainer> dynamicListeners(
            DynamicOutboxListener listenerHolder
    ) {
        List<DefaultMessageListenerContainer> containers = new ArrayList<>();

        for (String queue : listenerHolder.getQueues()) {

            // Safety check
            if (queue == null || queue.isBlank()) {
                System.out.println("Skipping listener creation → queueNameListen is NULL/EMPTY");
                continue;
            }

            DefaultMessageListenerContainer container = new DefaultMessageListenerContainer();
            container.setConnectionFactory(connectionFactory);
            container.setDestinationName(queue);

            // Set MessageListener
            container.setMessageListener((MessageListener) message -> {
                try {
                    String body = (message instanceof TextMessage)
                            ? ((TextMessage) message).getText()
                            : message.toString();

                    // Add to the corresponding queue list
                    listenerHolder.addMessage(queue, body);

                    System.out.println("Received on " + queue + ": " + body
                            + " | Total messages in queue: " + listenerHolder.getQueueSize(queue));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            container.initialize();
            container.start();

            containers.add(container);
        }

        return containers;
    }
}
