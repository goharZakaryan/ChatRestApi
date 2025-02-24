package com.example.service;

import com.example.model.User;
import io.smallrye.jwt.build.Jwt;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class AuthService {
    private final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

    public Uni<User> register(String username, String password, String email) {
        return Uni.createFrom().item(() -> {
            var user = new User();
            user.username = username;
            user.password = hashPassword(password);
            user.email = email;
            return user;
        }).flatMap(user -> user.persist());
    }

    public Uni<String> login(String username, String password) {
        return User.<User>find("username", username)
                .firstResult()
                .onItem().transform(user -> {
                    if (user != null && verifyPassword(password, user.password)) {
                        return generateJWT(user);
                    } else {
                        throw new RuntimeException("Invalid username or password");
                    }
                });
    }

    private String hashPassword(String password) {
        try {
            byte[] salt = new byte[16];
            new SecureRandom().nextBytes(salt);
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hash = factory.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(salt) + "$" + Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Error while hashing password", e);
        }
    }

    private boolean verifyPassword(String password, String storedHash) {
        try {
            String[] parts = storedHash.split("\\$");
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] hash = Base64.getDecoder().decode(parts[1]);

            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] testHash = factory.generateSecret(spec).getEncoded();

            return java.util.Arrays.equals(hash, testHash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Error while verifying password", e);
        }
    }

    private String generateJWT(User user) {


        return Jwt.issuer("https://chat-api.com")
                .upn(user.username)
                .groups("user")
                .claim( user.username, user.email)
                .sign();
    }
}
