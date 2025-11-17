package com.example.health_log;

import java.util.Date;
import java.util.List;

public class Video {
    private String title;
    private List<String> tags;
    private String description; // 추천 추가 정보

    // 시스템 자동 생성 정보
    private String uploader; // 현재 로그인된 사용자 정보
    private Date uploadDate;
    private String thumbnailUrl;
    private int likes;
    private int comments;

    // 생성자: 필수 정보만 받음 (나머지는 자동 설정 또는 추후 설정)
    public Video(String title, List<String> tags, String description) {
        this.title = title;
        this.tags = tags;
        this.description = description;
        // 시스템 자동 생성 정보는 초기화 (업로드 시점에 설정)
        this.likes = 0;
        this.comments = 0;
        this.uploadDate = new Date(); // 생성 시점의 시간으로 임시 설정
    }

    // --- Getters ---
    public String getTitle() {
        return title;
    }

    public List<String> getTags() {
        return tags;
    }

    public String getDescription() {
        return description;
    }

    public String getUploader() {
        return uploader;
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public int getLikes() {
        return likes;
    }

    public int getComments() {
        return comments;
    }

    // --- Setters ---
    public void setTitle(String title) {
        this.title = title;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUploader(String uploader) {
        this.uploader = uploader;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }
}