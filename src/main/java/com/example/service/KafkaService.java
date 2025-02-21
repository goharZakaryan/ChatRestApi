package com.example.service;

import com.example.model.Message;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@ApplicationScoped
public class KafkaService {
    @Channel("chat-messages")
    Emitter<Message> messageEmitter;

    public Uni<Void> sendMessage(Message message) {
        return Uni.createFrom().item(message)
                .onItem().transform(msg -> {
                    messageEmitter.send(msg);
                    return null;
                });
    }
}