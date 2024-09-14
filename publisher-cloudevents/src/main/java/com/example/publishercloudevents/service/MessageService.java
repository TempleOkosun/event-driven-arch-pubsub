package com.example.publishercloudevents.service;

import com.example.publishercloudevents.messaging.EventPublisher;
import com.example.publishercloudevents.model.Product;
import org.springframework.stereotype.Component;


import lombok.RequiredArgsConstructor;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class MessageService {

    private final EventPublisher eventPublisher;


    public void sendMessageToPubSub() {
        Product product = Product.builder().id(100).name("Cricket Bat").description("Suitable for kids").build();
        Product product1 = Product.builder().id(101).name("Running Shoes").description("Adult size.").build();
        eventPublisher.prepareMessage(product,"", Map.of("", "") );
        eventPublisher.prepareMessage(product1, "", Map.of("", ""));
    }
}