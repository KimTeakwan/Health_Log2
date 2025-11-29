package com.example.health_log.model;

import com.google.gson.annotations.SerializedName;

public class SimpleUser {
    @SerializedName("id")
    private int id;

    @SerializedName("username")
    private String username;

    @SerializedName("profile_image_url")
    private String profileImageUrl;

    // Getters
    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }
}
