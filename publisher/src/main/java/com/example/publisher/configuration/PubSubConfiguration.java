package com.example.publisher.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageHandler;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.integration.outbound.PubSubMessageHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class PubSubConfiguration {

    @Value("${pubsub.topic-id}")
    private String topicId;

    //Publisher
    @Bean("pubSubOutputChannel")
    public DirectChannel pubSubOutputChannel() {
        return new DirectChannel();
    }

    // an outbound channel adapter to send messages from the input message channel to the topic
    @Bean("defaultMessageSender")
    @ServiceActivator(inputChannel = "pubSubOutputChannel")
    public MessageHandler messageSender(PubSubTemplate pubSubTemplate) {
        var adapter = new PubSubMessageHandler(pubSubTemplate, topicId);
//        adapter.setSync(true);

        adapter.setSuccessCallback(
                ((ackId, message) ->
                        log.info("Message was sent via the outbound channel adapter to: {}!", topicId)));
        adapter.setFailureCallback(
                (cause, message) -> log.info("Error sending {} due to ", message, cause));
        return adapter;
    }
}