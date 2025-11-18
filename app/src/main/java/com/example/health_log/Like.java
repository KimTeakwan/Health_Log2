package com.example.health_log;

import com.google.gson.annotations.SerializedName;

public class Like {
    private int id;
    private User user;
    private Video video;
    @SerializedName("created_at")
    private String createdAt;

    public int getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Video getVideo() {
        return video;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
