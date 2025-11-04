package com.example.health_log;

// import 문은 모두 파일 상단에 위치해야 합니다.
import java.util.Date;
import java.util.List;

// public 클래스는 파일 당 하나만 존재해야 합니다.
public class Video {

    // --- 필드 (데이터 변수) ---
    private String title;
    private List<String> tags;
    private String description;
    private String uploader; // 업로드한 사용자 정보
    private Date uploadDate;   // 업로드 날짜 (String -> Date 타입으로 변경 권장)
    private String thumbnailUrl; // 동영상 썸네일 이미지 URL
    private int likes;          // 좋아요 수
    private int comments;       // 댓글 수

    // --- 생성자 ---
    // Firebase DB를 사용할 경우, 비어있는 기본 생성자가 반드시 필요합니다.
    public Video() {
    }

    // 데이터를 넣어서 객체를 생성할 때 사용하는 생성자
    public Video(String title, List<String> tags, String description, String uploader) {
        this.title = title;
        this.tags = tags;
        this.description = description;
        this.uploader = uploader;
        this.uploadDate = new Date(); // 객체 생성 시점의 날짜를 자동으로 저장
        this.likes = 0; // 초기 좋아요는 0
        this.comments = 0; // 초기 댓글 수는 0
    }

    // --- Getters (데이터를 가져오는 메서드) ---
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

    // --- Setters (데이터를 설정하는 메서드) ---
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
