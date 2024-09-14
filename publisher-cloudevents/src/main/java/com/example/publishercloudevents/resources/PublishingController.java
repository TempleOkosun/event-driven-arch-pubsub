package com.example.publishercloudevents.resources;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import com.example.publishercloudevents.service.MessageService;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PublishingController {
    private final MessageService messageService;


    @RequestMapping("/publish")
    public ResponseEntity<String> testPublish(){
        messageService.sendMessageToPubSub();
        return new ResponseEntity<>("SUCCESS", HttpStatus.CREATED);
    }
}