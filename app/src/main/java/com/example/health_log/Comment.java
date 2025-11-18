package com.example.health_log;

import com.google.gson.annotations.SerializedName;

public class Comment {
    private int id;
    private User user;
    private String text;
    @SerializedName("is_adopted")
    private boolean isAdopted;
    @SerializedName("created_at")
    private String createdAt;

    public int getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getText() {
        return text;
    }

    public boolean isAdopted() {
        return isAdopted;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setText(String text) {
        this.text = text;
    }
}
