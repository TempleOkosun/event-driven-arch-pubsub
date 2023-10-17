package com.example.notificationservice.configuration;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.integration.AckMode;
import com.google.cloud.spring.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.google.cloud.spring.pubsub.support.GcpPubSubHeaders;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.handler.annotation.Header;

@Slf4j
@Configuration
public class PubSubConfiguration {

    // subscription
    @Value("${pubsub.subscription-id}")
    private String subscriptionId;

    // a message channel for messages arriving from the subscription
    @Bean
    public MessageChannel pubSubInputChannel() {
        return new PublishSubscribeChannel();
    }

    // an inbound channel adapter to listen to the subscription &
    // send message to the input channel
    @Bean
    public PubSubInboundChannelAdapter messageChannelAdapter(@Qualifier("pubSubInputChannel") MessageChannel inputChannel, PubSubTemplate pubSubTemplate) {
        PubSubInboundChannelAdapter adapter = new PubSubInboundChannelAdapter(pubSubTemplate, subscriptionId);
        adapter.setOutputChannel(inputChannel);
        adapter.setAckMode(AckMode.MANUAL);
        adapter.setPayloadType(String.class);
        return adapter;
    }


    // service activator - defines what happens to the messages arriving in the message channel.
    @ServiceActivator(inputChannel = "pubSubInputChannel")
    public void messageReceiver(String payload, @Header(GcpPubSubHeaders.ORIGINAL_MESSAGE) BasicAcknowledgeablePubsubMessage message) {
        log.info("Message arrived via an inbound channel adapter, activator-1 from sub: {}! Payload: {}", subscriptionId, payload);
        message.ack();
    }
}