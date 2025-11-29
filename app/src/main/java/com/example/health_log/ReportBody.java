package com.example.health_log;

import com.google.gson.annotations.SerializedName;

public class ReportBody {

    @SerializedName("content_type")
    private String contentType;

    @SerializedName("object_id")
    private int objectId;

    @SerializedName("reason")
    private String reason;

    @SerializedName("description")
    private String description;

    public ReportBody(String contentType, int objectId, String reason, String description) {
        this.contentType = contentType;
        this.objectId = objectId;
        this.reason = reason;
        this.description = description;
    }
}
