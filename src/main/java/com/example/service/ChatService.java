package com.example.service;

import com.example.model.Message;
import com.example.model.User;
import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.Instant;

@ApplicationScoped
public class ChatService {

    @Inject
    KafkaService kafkaService;

    @Inject
    SecurityIdentity securityIdentity;

    public Uni<PanacheEntityBase> sendDirectMessage(String recipientUsername, String content) {
        // Get authenticated user from security context
        String authenticatedUsername = securityIdentity.getPrincipal().getName(); // Get username
        return User.<User>find("username", recipientUsername)
                .firstResult()
                .onItem().transformToUni(recipient -> {
                    if (recipient == null) {
                        // If recipient is not found, return a failed Uni
                        return Uni.createFrom().failure(new IllegalArgumentException("Recipient not found"));
                    }
                    return User.<User>find("username", authenticatedUsername)
                            .firstResult()
                            .onItem().transformToUni(sender -> {
                                if (sender == null) {
                                    // If sender (authenticated user) is not found, return a failed Uni
                                    return Uni.createFrom().failure(new IllegalArgumentException("Sender not found"));
                                }

                                // Create message
                                Message message = new Message();
                                message.sender = sender;
                                message.recipient = recipient;
                                message.content = content;
                                message.timestamp = Instant.now();
                                message.isGroupMessage = false;
                                message.isBroadcast = false;

                                return message.persist()
                                        .onItem().transformToUni(persistedMessage -> {
                                            // Send message via Kafka
                                            return kafkaService.sendMessage((Message) persistedMessage)
                                                    .onItem().transform(ignored -> persistedMessage);
                                        });
                            });
                });
    }

    public Multi<Message> getMessages() {
        // Get authenticated user from security context
        String authenticatedUsername = securityIdentity.getPrincipal().getName();

        // Find the authenticated user
        return User.<User>find("username", authenticatedUsername)
                .firstResult()
                .onItem().transformToMulti(user -> {
                    if (user == null) {
                        // If user is not found, return an empty Multi
                        return Multi.createFrom().empty();
                    }

                    // Fetch messages where the user is either the sender or recipient
                    return Message.<Message>find("sender = ?1 or recipient = ?1", user.username)
                            .list()
                            .onItem().transformToMulti(messages -> Multi.createFrom().iterable(messages));
                });
    }


}
