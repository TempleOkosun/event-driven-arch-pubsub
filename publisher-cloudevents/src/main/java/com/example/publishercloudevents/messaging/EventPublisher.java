package com.example.publishercloudevents.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.TopicName;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.provider.EventFormatProvider;
import io.cloudevents.jackson.JsonFormat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class EventPublisher {
    @Value("")
    private String topic;

    @Value("")
    private String project;

    public void prepareMessage(Object msg, String topic, Map<String, String> attributes) {
        if (msg != null) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                String message = "";
                message = objectMapper.writeValueAsString(msg);
                publishEvent(message, topic, attributes);
            } catch (Exception e) {
                log.error("error in publishing message", e);
            }
        }
    }

    private void publishEvent(String message, String topic, Map<String, String> headers) throws Exception {
        TopicName topicName = TopicName.of(project, topic);
        Publisher publisher = Publisher.newBuilder(topicName).build();
        try {
            ByteString data = ByteString.copyFromUtf8(message);
            PubsubMessage.Builder pubsubMessage = PubsubMessage.newBuilder().setData(data);
            PubsubMessage pubsubmessage = pubsubMessage.build();
            pubsubMessage.putAllAttributes(headers);
            publisher.publish(pubsubmessage);
            log.info("published message-{}, with headers-{}, successfully to topic-{} ", message, topic, headers);
        } finally {
            publisher.shutdown();
            publisher.awaitTermination(1, TimeUnit.MINUTES);
        }
    }

    public boolean cloudEventMessage(Object msg, String topic, String type, Map<String, String> attributes) {
        if (msg != null) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                String message = objectMapper.writeValueAsString(msg);
                CloudEvent event = CloudEventBuilder.v1()
                        .withId(UUID.randomUUID().toString())
                        .withData("application/json; charset=utf-8", message.getBytes())
                        .withSource(URI.create("//pubsub.googleapis.com/${topic}"))
                        .withType(type)
                        .withTime(OffsetDateTime.now(ZoneId.of("America/Toronto")))
                        .build();
                byte[] serialized = Objects.requireNonNull(EventFormatProvider.getInstance().resolveFormat(JsonFormat.CONTENT_TYPE)).serialize(event);
                String cloudEventMessage = objectMapper.writeValueAsString(serialized);
                publishEvent(cloudEventMessage, topic, attributes);
                return true;
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                log.error("Interrupted Exception occurred", ie);
                return false;
            } catch (Exception e) {
                log.error("error in publishing message", e);
                return false;
            }
        }
        return false;
    }


}