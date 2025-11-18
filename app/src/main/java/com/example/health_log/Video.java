package com.example.health_log;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class Video {

    private int id;
    private String title;
    private String description;

    @SerializedName("video_file")
    private String videoFile;

    private User uploader;

    @SerializedName("created_at")
    private String createdAt;

    private List<Comment> comments;

    @SerializedName("likes_count")
    private int likesCount;

    // ✅ [추가됨] UserProfileActivity에서 태그 리스트를 넘기고 있어서 필드를 추가했습니다.
    private List<String> tags;

    // ==========================================
    // ✅ [중요 1] 기본 생성자
    // Retrofit(Gson)이 JSON을 변환할 때 꼭 필요합니다. 지우면 안 됩니다!
    // ==========================================
    public Video() {
    }

    // ==========================================
    // ✅ [중요 2] 사용자 정의 생성자 (오류 해결 핵심)
    // UserProfileActivity에서 new Video(제목, 태그, 설명)을 호출할 때 사용됩니다.
    // ==========================================
    public Video(String title, List<String> tags, String description) {
        this.title = title;
        this.tags = tags;
        this.description = description;

        // 더미 데이터 사용 시 NullPointerException 방지를 위한 기본 초기화
        this.likesCount = 0;
        this.comments = new ArrayList<>();
        this.createdAt = "2025-01-01"; // 임시 날짜
    }

    // --- Getters ---
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getVideoFile() {
        return videoFile;
    }

    public User getUploader() {
        return uploader;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public int getLikesCount() {
        return likesCount;
    }

    // ✅ 태그 Getter 추가
    public List<String> getTags() {
        return tags;
    }

    // --- Setters (필요한 경우 추가) ---
    public void setUploader(String uploaderName) {
        // User 객체를 간편하게 설정하기 위한 유틸리티성 Setter (선택사항)
        if (this.uploader == null) {
            this.uploader = new User();
        }
        // User 클래스에 setUsername이나 setName이 있다고 가정
        // this.uploader.setUsername(uploaderName);
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }
}