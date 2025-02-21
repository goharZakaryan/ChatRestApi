package com.example.resource;


import com.example.model.Message;
import com.example.service.ChatService;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

record MessageRequest(String recipientUsername, String content) {}

@Path("/chat")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ChatResource {
    @Inject
    ChatService chatService;
    @Inject
    SecurityIdentity securityIdentity;
    @POST
    @Path("/message")
    public Uni<Response> sendMessage(MessageRequest request) {

        return chatService.sendDirectMessage( request.recipientUsername(), request.content())
                .onItem().transform(message -> Response.status(Response.Status.CREATED).build());
    }

    @GET
    @Path("/messages")
    public Multi<Message> getMessages() {
        // Get authenticated user from security context
        return chatService.getMessages();
    }
}