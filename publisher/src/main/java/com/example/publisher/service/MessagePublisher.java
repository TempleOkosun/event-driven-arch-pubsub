package com.example.publisher.service;

import com.example.publisher.model.Product;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.ByteString;


import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MessagePublisher {

    private final MessageHandler defaultMessageSender;
    private final ObjectMapper objectMapper;

    public void sendMessageToPubSub() throws JsonProcessingException {
        Product product = Product.builder().id(100).name("Cricket Bat").description("Suitable for kids").build();
        String jsonString = objectMapper.writeValueAsString(product);
        Message<ByteString> message = MessageBuilder.withPayload(ByteString.copyFromUtf8(jsonString)).build();
        defaultMessageSender.handleMessage(message);

        // filter has been set on subscription to receive just the messages with the notification header.
        Product product1 = Product.builder().id(101).name("Running Shoe").description("Adult size.").build();
        String jsonString1 = objectMapper.writeValueAsString(product1);
        Message<ByteString> message1 = MessageBuilder.withPayload(ByteString.copyFromUtf8(jsonString1))
                .setHeader("FROM_SERVICE", "PUBLISHER")
                .setHeader("TO_SERVICE", "NOTIFICATION_SERVICE")
                .build();
        defaultMessageSender.handleMessage(message1);
    }
}