package com.example.resource;

import com.example.service.AuthService;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

record RegisterRequest(String username, String password, String email) {}
record LoginRequest(String username, String password) {}

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {
    @Inject
    AuthService authService;

    @POST
    @Path("/register")
    public Uni<Response> register(RegisterRequest request) {
        return authService.register(request.username(), request.password(), request.email())
                .onItem().transform(user -> Response.status(Response.Status.CREATED).build());
    }

    @POST
    @Path("/login")
    public Uni<Response> login(LoginRequest request) {
        return authService.login(request.username(), request.password())
                .onItem().transform(token -> Response.ok().entity(token).build());
    }
}
