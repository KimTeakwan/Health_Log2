package com.example.health_log;

public class SignUpRequest {
    private String username;
    private String password;
    private String email;
    private String role;

    public SignUpRequest(String username, String password, String email, String role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
    }

    // Getters and setters are not strictly required for Gson serialization,
    // but they are good practice.
}
