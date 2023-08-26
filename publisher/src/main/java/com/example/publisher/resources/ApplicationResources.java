package com.example.publisher.resources;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.RequiredArgsConstructor;
import com.example.publisher.service.MessagePublisher;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApplicationResources {

    private final MessagePublisher messagePublisher;

    @RequestMapping("/publish")
    public ResponseEntity<String> testPublish() throws JsonProcessingException {
        messagePublisher.sendMessageToPubSub();
        return new ResponseEntity<>("SUCCESS", HttpStatus.CREATED);
    }
}