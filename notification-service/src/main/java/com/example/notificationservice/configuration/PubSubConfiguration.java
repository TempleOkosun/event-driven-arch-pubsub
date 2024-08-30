package com.example.notificationservice.configuration;

import com.example.notificationservice.event.ProductEvent;
import com.example.notificationservice.model.Product;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.google.cloud.spring.pubsub.support.GcpPubSubHeaders;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Header;

import java.util.Map;

@Slf4j
@Configuration
public class PubSubConfiguration {

    // subscription
    @Value("${pubsub.subscription-id}")
    private String subscriptionId;

    // subscription
    @Value("${pubsub.subscription-id2}")
    private String subscriptionId2;

    private final ApplicationEventPublisher applicationEventPublisher;

    public PubSubConfiguration(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }


    // a message channel for messages arriving from the subscription
//    @Bean
//    public MessageChannel pubSubInputChannel() {
//        return new PublishSubscribeChannel();
//    }

    @Bean("pubSubInputChannel")
    public DirectChannel pubSubInputChannel() {
        return new DirectChannel();
    }

    // an inbound channel adapter to listen to the subscription &
    // send message to the input channel
    @Bean
    public PubSubInboundChannelAdapter messageChannelAdapter(@Qualifier("pubSubInputChannel") MessageChannel inputChannel, PubSubTemplate pubSubTemplate) {
        PubSubInboundChannelAdapter adapter = new PubSubInboundChannelAdapter(pubSubTemplate, subscriptionId);
        adapter.setOutputChannel(inputChannel);
//        adapter.setAckMode(AckMode.MANUAL);
        adapter.setPayloadType(String.class);
        return adapter;
    }

//    @Bean
//    public PubSubInboundChannelAdapter messageChannelAdapter2(@Qualifier("pubSubInputChannel") MessageChannel inputChannel, PubSubTemplate pubSubTemplate) {
//        PubSubInboundChannelAdapter adapter = new PubSubInboundChannelAdapter(pubSubTemplate, subscriptionId2);
//        adapter.setOutputChannel(inputChannel);
//        adapter.setPayloadType(String.class);
//        return adapter;
//    }


    // service activator - defines what happens to the messages arriving in the message channel.
    @ServiceActivator(inputChannel = "pubSubInputChannel")
    public void messageReceiver(Message<String> message)
    {
        String payload = message.getPayload();
        MessageHeaders headers = message.getHeaders();
        // Different ways of getting headers:
        // @Header(value = GcpPubSubHeaders.TOPIC) String topic
        // @Header(GcpPubSubHeaders.ORIGINAL_MESSAGE) BasicAcknowledgeablePubsubMessage message
        log.info("Message arrived via an inbound channel adapter, Payload: {}, Headers:{}, class:{}", payload, headers, headers.getClass());
//        log.info("Message arrived via an inbound channel adapter, from Sub:{}, Payload: {}, Headers:{}", message.getProjectSubscriptionName(), payload, message.getPubsubMessage().getAttributesMap());
        try{
            processPayload(payload);
        } catch (Exception e) {
            log.error("Error processing payload", e);
        }

    }

    private void processPayload(String payload) throws JsonProcessingException {
        log.info("Enter processPayload");
        ObjectMapper objectMapper = new ObjectMapper();
        Product product = objectMapper.readValue(payload, Product.class);
        log.info("product: {}", product);
        applicationEventPublisher.publishEvent(new ProductEvent(this, product));
        log.info("processed successfully");
    }
}