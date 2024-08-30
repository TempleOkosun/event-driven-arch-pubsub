package com.example.publisher.configuration;

import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.messaging.handler.annotation.Header;

@MessagingGateway(defaultRequestChannel = "pubSubOutputChannel")
public interface MessagePubsubGateway {
    void send(String message, @Header("trigger") String trigger);
}
