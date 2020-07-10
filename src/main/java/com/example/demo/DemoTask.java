package com.example.demo;

import com.example.demo.publisher.FieldPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DemoTask {

    @Autowired
    FieldPublisher fieldPublisher;

    @Scheduled(fixedRate = 1000)
    public void task() {
    }
}



