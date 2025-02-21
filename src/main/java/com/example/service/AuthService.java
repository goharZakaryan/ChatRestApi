package com.example.service;


import com.example.model.User;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ApplicationScoped
public class AuthService {
    private final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

    public Uni<User> register(String username, String password, String email) {
        return Uni.createFrom().item(() -> {
            var user = new User();
            user.username = username;
            user.password = hashPassword(password); // Implement proper hashing
            user.email = email;
            return user;
        }).flatMap(user -> user.persist());
    }

    public Uni<String> login(String username, String password) {
        return User.<User>find("username", username)
                .firstResult()
                .onItem().transform(user -> generateJWT(user)); // Implement JWT generation
    }

    private String hashPassword(String password) {
        // Implement password hashing
        return password;
    }

    private String generateJWT(User user) {
        // Implement JWT generation
        return "jwt-token";
    }
}