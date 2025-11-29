package com.example.health_log;

import com.google.gson.annotations.SerializedName;

public class User {
    private int id;
    private String username;
    @SerializedName("first_name")
    private String firstName;
    private String role;

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
