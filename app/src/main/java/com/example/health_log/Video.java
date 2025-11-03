package com.example.health_log;

import java.util.List;

public class Video {
    private String title;
    private String uploader;
    private int likes;
    private int comments;
    private String uploadDate;
    private List<String> tags;
    private String thumbnailUrl;

    public Video(String title, String uploader, int likes, int comments, String uploadDate, List<String> tags, String thumbnailUrl) {
        this.title = title;
        this.uploader = uploader;
        this.likes = likes;
        this.comments = comments;
        this.uploadDate = uploadDate;
        this.tags = tags;
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getUploader() {
        return uploader;
    }

    public int getLikes() {
        return likes;
    }

    public int getComments() {
        return comments;
    }

    public String getUploadDate() {
        return uploadDate;
    }

    public List<String> getTags() {
        return tags;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }
}
