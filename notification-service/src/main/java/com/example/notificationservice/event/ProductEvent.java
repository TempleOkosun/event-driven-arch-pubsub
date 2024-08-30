package com.example.notificationservice.event;

import com.example.notificationservice.model.Product;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;

@Getter
@Slf4j
public class ProductEvent extends ApplicationEvent {
    private final Product product;

    public ProductEvent(Object source, Product product) {
        super(source);
        this.product = product;
    }

}
