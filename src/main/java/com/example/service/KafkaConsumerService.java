package com.example.service;

import com.example.model.Message;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class KafkaConsumerService {

    @Incoming("chat-in")
    public void consume(Message message) {
        System.out.println("Received message: " + message);
        // Process the message here
    }
}