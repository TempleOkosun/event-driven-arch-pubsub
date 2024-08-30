package com.example.notificationservice.eventservice;

import com.example.notificationservice.event.ProductEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AnotherProductEventService {

    @EventListener
    public void anotherHandlerForProductEvent(ProductEvent productEvent) {
        log.info("Another ProductEvent Listener: {}", productEvent.getProduct());
    }
}
