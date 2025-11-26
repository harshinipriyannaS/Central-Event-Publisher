package com.company.eventpublisher.sender;

public interface JmsSender {
    
    boolean send(String queueName, String payload);
}
