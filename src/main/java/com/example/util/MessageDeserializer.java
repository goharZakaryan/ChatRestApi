package com.example.util;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;
import com.example.model.Message;

public class MessageDeserializer extends ObjectMapperDeserializer<Message> {
    public MessageDeserializer() {
        super(Message.class);
    }
}