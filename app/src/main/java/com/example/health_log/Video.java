package com.example.health_log;

// 1. import 문은 모두 파일의 최상단에 위치해야 합니다.
import java.util.Date;
import java.util.List;

// 2. public 클래스는 파일 당 하나만 존재해야 합니다.
public class Video {

    // --- 필드 (클래스의 속성) ---
    private String title;
    private String description;
    private List<String> tags;
    private String uploader;
    private Date uploadDate;
    private String thumbnailUrl;
    private int likes;
    private int comments;

    // --- 생성자 ---
    // 객체를 생성할 때 호출되는 메서드
    public Video(String title, List<String> tags, String description) {
        this.title = title;
        this.tags = tags;
        this.description = description;

        // 아래 값들은 보통 시스템에서 자동으로 설정하므로 생성자에서 초기화합니다.
        this.uploader = "Default Uploader"; // 또는 현재 로그인 사용자 정보
        this.uploadDate = new Date(); // 객체 생성 시점의 현재 날짜/시간
        this.thumbnailUrl = ""; // 썸네일 URL은 나중에 설정
        this.likes = 0;
        this.comments = 0;
    }

    // --- Getters (필드 값을 외부로 반환하는 메서드) ---
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getTags() {
        return tags;
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

    // --- Setters (필드 값을 외부에서 설정하는 메서드) ---
    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
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
