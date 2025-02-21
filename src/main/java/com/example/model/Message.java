package com.example.model;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;

@Entity
public class Message extends PanacheEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    public User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    public User recipient;

    @ManyToOne(fetch = FetchType.LAZY)
    public ChatGroup group;

    @Column(nullable = false, length = 1000)
    @Size(max = 1000)
    @NotNull
    public String content;

    @Column(nullable = false)
    @NotNull
    public Instant timestamp = Instant.now(); // Initialize to current time

    public boolean isGroupMessage;
    public boolean isBroadcast;

    // Constructors
    public Message() {}

    public Message(User sender, String content, boolean isGroupMessage, boolean isBroadcast) {
        this.sender = sender;
        this.content = content;
        this.isGroupMessage = isGroupMessage;
        this.isBroadcast = isBroadcast;
    }

    // Getters and setters (if not using Lombok)
    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    public ChatGroup getGroup() {
        return group;
    }

    public void setGroup(ChatGroup group) {
        this.group = group;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isGroupMessage() {
        return isGroupMessage;
    }

    public void setGroupMessage(boolean groupMessage) {
        isGroupMessage = groupMessage;
    }

    public boolean isBroadcast() {
        return isBroadcast;
    }

    public void setBroadcast(boolean broadcast) {
        isBroadcast = broadcast;
    }
}