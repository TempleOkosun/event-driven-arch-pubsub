package com.example.notificationservice.eventservice;

import com.example.notificationservice.event.ProductEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProductEventService {

    @EventListener
    public void handleProductEvent(ProductEvent productEvent) {
        log.info("ProductEvent Listener: {}", productEvent);
    }
}
