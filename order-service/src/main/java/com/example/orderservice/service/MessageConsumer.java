package com.example.orderservice.service;

import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MessageConsumer {

    public void consume(BasicAcknowledgeablePubsubMessage message) {
//        log.info("Raw message received from PubSub in Order Service:{}", message.getPubsubMessage());
        log.info("Formatted message received from PubSub in Order Service:{}", message.getPubsubMessage().getData().toStringUtf8());
//        log.info(message.getPubsubMessage().getAttributesMap().toString());
        message.ack();
    }
}