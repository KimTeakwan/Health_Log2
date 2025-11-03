package com.example.health_log;

import java.util.List;
import java.util.Date;

public class Video {
    private String title;
    private List<String> tags;
    private String description; // 추천 추가 정보

    // 시스템 자동 생성 정보
    private String uploader; // 현재 로그인된 사용자 정보 (간단히 String으로 표현)
    private Date uploadDate;
    private String thumbnailUrl;
    private int likes;
    private int comments;

    public Video(String title, List<String> tags, String description) {
        this.title = title;
        this.tags = tags;
        this.description = description;
        // 시스템 자동 생성 정보는 생성자에서 초기화하지 않음 (업로드 시점에 설정)
        this.likes = 0;
        this.comments = 0;
    }

    // Getters
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

    // Setters (시스템 자동 생성 정보는 내부적으로 설정되므로, 필요한 경우에만 setter 제공)
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