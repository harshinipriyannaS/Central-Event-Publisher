package io.github.sushmithashiva04ops.centraleventpublisher.listener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import io.github.sushmithashiva04ops.centraleventpublisher.config.OutboxProperties;

@Component
public class DynamicOutboxListener {

    // Map of queueName -> messages
    private final Map<String, List<String>> queueMessages = new HashMap<>();

    public DynamicOutboxListener(OutboxProperties props) {
        // Initialize lists for each listening queue
        props.getItems().stream()
                .map(OutboxProperties.OutboxConfigItem::getQueueNameListen)
                .filter(q -> q != null && !q.isBlank())
                .forEach(q -> queueMessages.put(q, new CopyOnWriteArrayList<>()));
    }

    /**
     * Add a message to a specific queue
     */
    public void addMessage(String queueName, String message) {
        List<String> messages = queueMessages.get(queueName);
        if (messages != null) {
            messages.add(message);
        }
    }

    /**
     * Get all messages for a queue
     */
    public List<String> getMessages(String queueName) {
        return queueMessages.get(queueName);
    }

    /**
     * Get the size of messages for a queue
     */
    public int getQueueSize(String queueName) {
        List<String> messages = queueMessages.get(queueName);
        return messages != null ? messages.size() : 0;
    }

    /**
     * Get all queue names
     */
    public List<String> getQueues() {
        return queueMessages.keySet().stream().toList();
    }

    /**
     * Get the full map of queues -> messages
     */
    public Map<String, List<String>> getQueueMessagesMap() {
        return queueMessages;
    }
}
